package org.onap.msb.apiroute.wrapper;

import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.SimpleServerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.onap.msb.apiroute.ApiRouteApp;
import org.onap.msb.apiroute.ApiRouteAppConfig;
import org.onap.msb.apiroute.SyncDataManager;
import org.onap.msb.apiroute.api.ApiRouteInfo;
import org.onap.msb.apiroute.api.CustomRouteInfo;
import org.onap.msb.apiroute.api.DiscoverInfo;
import org.onap.msb.apiroute.api.IuiRouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.health.ConsulLinkHealthCheck;
import org.onap.msb.apiroute.health.RedisHealthCheck;
import org.onap.msb.apiroute.wrapper.serviceListener.MicroServiceChangeListener;
import org.onap.msb.apiroute.wrapper.serviceListener.RouteNotify;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.onap.msb.apiroute.wrapper.util.FileUtil;
import org.onap.msb.apiroute.wrapper.util.JacksonJsonUtil;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.onap.msb.apiroute.wrapper.util.RegExpTestUtil;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.core.type.TypeReference;

public class InitRouteServiceWrapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(InitRouteServiceWrapper.class);


  private static InitRouteServiceWrapper instance = new InitRouteServiceWrapper();

  private InitRouteServiceWrapper() {}

  public static InitRouteServiceWrapper getInstance() {
    return instance;
  }


  /**
   * The listener registration service changes
   */
  public void registerServiceChangeListener() {

    RouteNotify.getInstance().addServiceChangeListener(new MicroServiceChangeListener());

  }
  
  public void initFilterConfig(){
    //route init
    ConfigUtil.getInstance().initRouteWay();       
    ConfigUtil.getInstance().initApiGatewayPort();
    ConfigUtil.getInstance().initRouteNameSpaceMatches();
    ConfigUtil.getInstance().initRouteLabelsMatches();
    ConfigUtil.getInstance().initNodeMetaQueryParam();
    
  }
  
  public void initDataSynchro(){
    
    registerServiceChangeListener();

    boolean ifRedisConnect=startCheckRedisConnect();

    if(ifRedisConnect){
      initRouteInfoFromJson();      
      runConsulClientApp();
    }
  }
  
  public void initHealthCheck()
  {
      LOGGER.info("start check consul link thread");
      Thread tConsul= new Thread(new ConsulLinkHealthCheck(),"_healthcheck_consul_");
      tConsul.setDaemon(true);
      tConsul.start();
      
      LOGGER.info("start check redis thread");
      Thread tRedies= new Thread(new RedisHealthCheck(),"_healthcheck_redis_");
      tRedies.setDaemon(true);
      tRedies.start();
  }




  public boolean startCheckRedisConnect() {

        int n = 0;
        while (true) {
          if (!checkRedisConnect()) {
            n++;
            LOGGER.warn(n + "/10 : Initial Route Configuration——redis connection fail...");

            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              LOGGER.error("Thread.sleep throw except:" + e.getMessage());
            }


            if (n >= 10) {
              LOGGER.error("Initial Route Configuration——redis connection fail,timeout exit...");
              return false;
            }
          } else {
            LOGGER.warn(" Initial Route Configuration——redis connection success...");
           return true;
          }
        }

      
  }


  // Open the consul to monitor subscription service
  public void runConsulClientApp() {

    String consulIP;
    int consulPort;
    String consulConfSource="Default";

    
    DiscoverInfo discoverInfo = ConfigUtil.getInstance().getDiscoverInfo();

    if (discoverInfo.isEnabled()) {
      LOGGER.warn("starting to initial consul Configuration");
      String[] routeWay = ConfigUtil.getInstance().getRouteWay();
      try {
        String sys_consulIp=ConfigUtil.getInstance().getConsul_ip();
        if (StringUtils.isNotBlank(sys_consulIp)) {
          consulIP = sys_consulIp.trim();
          consulPort = RouteUtil.consulDeafultPort;
          consulConfSource="env:CONSUL_IP";
        } else {
          consulIP = discoverInfo.getIp();
          consulPort = discoverInfo.getPort();
          consulConfSource="init discoverInfo";
        }

        LOGGER.warn("init consul sync Address from [ "+consulConfSource+" ]:"  + consulIP + ":" + consulPort);

        // Registration service discovery routing
        // api
        ApiRouteInfo discoverApiService = new ApiRouteInfo();
        discoverApiService.setServiceName("msdiscover");
        discoverApiService.setUrl("/api/microservices/v1");
        discoverApiService.setVersion("v1");
        discoverApiService.setMetricsUrl("/admin/metrics");
        discoverApiService.setApiJson("/api/microservices/v1/swagger.json");
        discoverApiService.setHost("msb");

        RouteServer[] servers = new RouteServer[1];
        servers[0] = new RouteServer(discoverInfo.getIp(), String.valueOf(discoverInfo.getPort()));
        discoverApiService.setServers(servers);


        for (int i = 0; i < routeWay.length; i++) {
          ApiRouteServiceWrapper.getInstance().saveApiRouteInstance4Rest(discoverApiService,
              routeWay[i]);
        }



        // iui
        IuiRouteInfo discoverIUIService = new IuiRouteInfo();
        discoverIUIService.setServiceName("msdiscover");
        discoverIUIService.setUrl("/iui/microservices");
        discoverIUIService.setHost("msb");
        discoverIUIService.setServers(servers);

        for (int i = 0; i < routeWay.length; i++) {
          IuiRouteServiceWrapper.getInstance()
              .saveIuiRouteInstance(discoverIUIService, routeWay[i]);
        }


        /*
         * ConsulClientApp consulClientApp = new ConsulClientApp(consulIP, consulPort);
         * consulClientApp.startServiceListen();
         */

 //       SyncDataManager syncDataManager = new SyncDataManager();
        // Monitor serviceList change
        SyncDataManager.initSyncTask(consulIP, consulPort);


        LOGGER.warn("start monitor consul service--" + consulIP + ":" + consulPort);
      } catch (Exception e) {
        LOGGER.error("start monitor consul service fail:" + e.getMessage());
      }
    }
   


  }



  /**
   * @Title: initRouteInfoFromJson
   * @Description: TODO(According to the JSON file configuration initialization route data)
   * @return: void
   */
  public void initRouteInfoFromJson() {
    LOGGER.info("starting to initial Route Configuration");
    URL apiDocsPath = InitRouteServiceWrapper.class.getResource("/ext/initServices");
    if (apiDocsPath != null) {
      String path = apiDocsPath.getPath();

      LOGGER.info("read JsonFilefolder:" + path);

      try {
        File[] files = FileUtil.readFileFolder(path);
        for (int i = 0; i < files.length; i++) {
          File file = files[i];
          if (file.isFile() && file.getName().endsWith(".json")) {
            LOGGER.info("read JsonFile:" + file.getPath());
            String fileContent = FileUtil.readFile(file.getPath());
            saveInitService2redis(fileContent);
          } else {
            LOGGER.warn(file.getName() + " is not a right file");
          }
        }



      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        LOGGER.error("read  initServices Files throw FileNotFoundException", e);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        LOGGER.error("read  initServices Files throw IOexception", e);
      }

    }



  }



  private void saveInitService2redis(String fileContent) {

    String[] routeWay = ConfigUtil.getInstance().getRouteWay();
    String iuiRootPath = ConfigUtil.getInstance().getIUI_ROOT_PATH();

    try {
      List<ApiRouteInfo> routeList =
          JacksonJsonUtil.jsonToListBean(fileContent, new TypeReference<List<ApiRouteInfo>>() {});
      for (ApiRouteInfo route : routeList) {
        String url = route.getUrl();

        if (RegExpTestUtil.urlRegExpTest(route.getServiceName())) {

          for (int i = 0; i < routeWay.length; i++) {
            try {

              CustomRouteServiceWrapper.getInstance().getCustomRouteInstance(
                  route.getServiceName(), route.getHost(), "", routeWay[i]);

            } catch (ExtendedNotFoundException e) {

              LOGGER.info("initCustomRoute: ServiceName--" + route.getServiceName());

              CustomRouteInfo customRouteInfo = new CustomRouteInfo();
              customRouteInfo.setControl(route.getControl());
              customRouteInfo.setServers(route.getServers());
              customRouteInfo.setServiceName(route.getServiceName());
              customRouteInfo.setStatus(route.getStatus());
              customRouteInfo.setUrl(route.getUrl());
              customRouteInfo.setHost(route.getHost());



              CustomRouteServiceWrapper.getInstance().saveCustomRouteInstance(customRouteInfo,
                  routeWay[i]);

            }
          }
        } else {

          if (RegExpTestUtil.apiRouteUrlRegExpTest(url) || url.startsWith("/api/microservices/") || url.startsWith("/admin/microservices/")) {

            for (int i = 0; i < routeWay.length; i++) {
              try {

                ApiRouteServiceWrapper.getInstance().getApiRouteInstance(route.getServiceName(),
                    route.getVersion(), route.getHost(), route.getPublish_port(), routeWay[i]);

              } catch (ExtendedNotFoundException e) {
                LOGGER.info("initapiRoute: ServiceName--" + route.getServiceName());

                if (url.startsWith("/api/microservices")) {
                  if (StringUtils.isNotBlank(System.getenv("dwApp_server_connector_port"))) {
                    replaceApigatewayPort(route.getServers(),
                        System.getenv("dwApp_server_connector_port"));
                  }
                }
                
                if (url.startsWith("/admin/microservices")) {                 
                    replaceApigatewayPort(route.getServers(),ConfigUtil.getInstance().getServerPort());                  
                }

                ApiRouteServiceWrapper.getInstance().saveApiRouteInstance4Rest(route, routeWay[i]);

              }
            }


          } else if (RegExpTestUtil.iuiRouteUrlRegExpTest(url) || url.equals("/iui/microservices")) {

            for (int i = 0; i < routeWay.length; i++) {
              try {

                IuiRouteServiceWrapper.getInstance().getIuiRouteInstance(route.getServiceName(),
                    route.getHost(), "", routeWay[i]);

              } catch (ExtendedNotFoundException e) {

                LOGGER.info(" initiuiRoute: ServiceName--" + route.getServiceName());
                IuiRouteInfo iuiRouteInfo = new IuiRouteInfo();
                iuiRouteInfo.setControl(route.getControl());
                iuiRouteInfo.setServers(route.getServers());
                iuiRouteInfo.setServiceName(route.getServiceName());
                iuiRouteInfo.setStatus(route.getStatus());
                iuiRouteInfo.setHost(route.getHost());


                if (url.equals("/iui/microservices")) {
                  iuiRouteInfo.setUrl("/" + iuiRootPath + "/microservices");
                  if (StringUtils.isNotBlank(System.getenv("dwApp_server_connector_port"))) {
                    replaceApigatewayPort(iuiRouteInfo.getServers(),
                        System.getenv("dwApp_server_connector_port"));
                  }
                } else {
                  iuiRouteInfo.setUrl(route.getUrl());
                }

                IuiRouteServiceWrapper.getInstance()
                    .saveIuiRouteInstance(iuiRouteInfo, routeWay[i]);


              }
            }

          } else {
            LOGGER.error("init Service throw exception——serviceName: " + route.getServiceName()
                + ",url:" + url);
          }
        }



      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      LOGGER.error("read  initServices Files throw exception", e);
    }

  }

  private void replaceApigatewayPort(RouteServer[] servers, String apigatewayPort) {
    for (int i = 0; i < servers.length; i++) {
      servers[i].setPort(apigatewayPort);
    }
  }

  public void initMetricsConfig(ApiRouteAppConfig configuration) {

    SimpleServerFactory simpleServerFactory =
        (SimpleServerFactory) configuration.getServerFactory();
    HttpConnectorFactory httpConnectorFactory =
        (HttpConnectorFactory) simpleServerFactory.getConnector();
    String metricsUrl =
        "http://127.0.0.1:" + httpConnectorFactory.getPort()
            + simpleServerFactory.getAdminContextPath() + "/metrics";
    ConfigUtil.getInstance().setMetricsUrl(metricsUrl);
  }


  private boolean checkRedisConnect() {
    Jedis jedis = null;
    try {
       jedis = JedisUtil.borrowJedisInstance();
      return true;
    } catch (Exception e) {
      LOGGER.error("checkRedisConnect call redis throw exception", e);
    }finally {
      JedisUtil.returnJedisInstance(jedis);
    } 

    return false;
  }



}
