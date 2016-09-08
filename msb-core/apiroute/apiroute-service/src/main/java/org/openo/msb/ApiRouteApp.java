/**
 * Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.msb;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.openo.msb.api.ApiRouteInfo;
import org.openo.msb.api.ConsulInfo;
import org.openo.msb.api.CustomRouteInfo;
import org.openo.msb.api.DiscoverInfo;
import org.openo.msb.api.IuiRouteInfo;
import org.openo.msb.api.RouteServer;
import org.openo.msb.api.exception.ExtendedNotFoundException;
import org.openo.msb.health.ApiRouteHealthCheck;
import org.openo.msb.resources.ApiRouteResource;
import org.openo.msb.resources.CustomRouteResource;
import org.openo.msb.resources.IuiRouteResource;
import org.openo.msb.resources.MetricsResource;
import org.openo.msb.resources.MicroServiceResource;
import org.openo.msb.resources.ServiceAccessResource;
import org.openo.msb.wrapper.ApiRouteServiceWrapper;
import org.openo.msb.wrapper.CustomRouteServiceWrapper;
import org.openo.msb.wrapper.IuiRouteServiceWrapper;
import org.openo.msb.wrapper.serviceListener.MicroServiceChangeListener;
import org.openo.msb.wrapper.util.FileUtil;
import org.openo.msb.wrapper.util.JacksonJsonUtil;
import org.openo.msb.wrapper.util.JedisUtil;
import org.openo.msb.wrapper.util.MetricsUtil;
import org.openo.msb.wrapper.util.MicroServiceDB;
import org.openo.msb.wrapper.util.RegExpTestUtil;
import org.openo.msb.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ApiRouteApp extends Application<ApiRouteAppConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRouteApp.class);

    public static void main(String[] args) throws Exception {
        new ApiRouteApp().run(args);

    }

    private ApiRouteAppConfig config;

    @Override
    public String getName() {
        return " MicroService Bus ";
    }

    @Override
    public void initialize(Bootstrap<ApiRouteAppConfig> bootstrap) {
        
        
    }

    @Override
    public void run(ApiRouteAppConfig configuration, Environment environment) {
        
        initRootPath();
        
      
        new AssetsBundle("/iui-metrics", "/"+RouteUtil.IUI_ROOT_PATH+"/microservices/metrics",
            "index.html", "iui-metrics").run(environment); 
        
        new AssetsBundle("/iui-route",  "/"+RouteUtil.IUI_ROOT_PATH+"/microservices", "index.html",
                "iui-microservices").run(environment); 
        
        new AssetsBundle("/api-doc",  "/"+RouteUtil.IUI_ROOT_PATH+"/microservices/api-doc",
            "index.html", "api-doc").run(environment);
        
        new AssetsBundle("/ext",  "/"+RouteUtil.IUI_ROOT_PATH+"/microservices/ext",
            "index.html", "ext").run(environment);
        
        
        

        final ApiRouteHealthCheck healthCheck =
                new ApiRouteHealthCheck(configuration.getDefaultWorkspace());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(new ApiRouteResource());
        environment.jersey().register(new IuiRouteResource());
        environment.jersey().register(new MetricsResource());
        environment.jersey().register(new CustomRouteResource());
        environment.jersey().register(new ServiceAccessResource());
        environment.jersey().register(new MicroServiceResource());

        config = configuration;

        initSwaggerConfig(environment, configuration);
        initRedisConfig(configuration);
        checkRedisConnect();
        initMetricsConfig(configuration);
        initVisualRangeMatches();

        registerServiceChangeListener();


    }

    private void initMetricsConfig(ApiRouteAppConfig configuration) {

        SimpleServerFactory simpleServerFactory =
                (SimpleServerFactory) configuration.getServerFactory();
        HttpConnectorFactory httpConnectorFactory =
                (HttpConnectorFactory) simpleServerFactory.getConnector();
        MetricsUtil.adminContextPath =
                "http://127.0.0.1:" + httpConnectorFactory.getPort()
                        + simpleServerFactory.getAdminContextPath() + "/metrics";
    }

    private void initSwaggerConfig(Environment environment, ApiRouteAppConfig configuration) {

        environment.jersey().register(new ApiListingResource());
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        BeanConfig config = new BeanConfig();
        config.setTitle("MicroService Bus rest API");
        config.setVersion("1.0.0");
        config.setResourcePackage("org.openo.msb.resources");
        SimpleServerFactory simpleServerFactory =
                (SimpleServerFactory) configuration.getServerFactory();
        String basePath = simpleServerFactory.getApplicationContextPath();
        String rootPath = simpleServerFactory.getJerseyRootPath();

        rootPath = rootPath.substring(0, rootPath.indexOf("/*"));

        basePath =
                basePath.equals("/") ? rootPath : (new StringBuilder()).append(basePath)
                        .append(rootPath).toString();

        LOGGER.info("getApplicationContextPath： " + basePath);
        config.setBasePath(basePath);
        config.setScan(true);
    }
    
    
    private void initRootPath(){
        try {
            
            URL urlRootPath = ApiRouteApp.class.getResource("/ext/initUrlRootPath/initUrlRootPath.json");
            if (urlRootPath != null) {
                String path = urlRootPath.getPath();
    
                LOGGER.info("read initUrlRootPath:" + path);
              
                String fileContent = FileUtil.readFile(path);
                JSONObject jsonObj = JSONObject.fromObject(fileContent);
                RouteUtil.IUI_ROOT_PATH=jsonObj.get("iuiRootPath").toString();
                RouteUtil.API_ROOT_PATH=jsonObj.get("apiRootPath").toString();
                }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.error("read  initUrlRootPath Files throw exception", e);
        }
        
    }
    private void initRedisConfig(ApiRouteAppConfig configuration) {

        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String jarPath = path.substring(0, path.lastIndexOf("/"));

        LOGGER.info("jarpath: " + jarPath);
        LOGGER.info("getDefaultWorkspace " + configuration.getDefaultWorkspace());

        String confDir =
                jarPath + "/" + configuration.getDefaultWorkspace() + "/"
                        + configuration.getPropertiesDir();
        String propertiesPath = confDir + "/" + configuration.getPropertiesName();

        JedisUtil.propertiesPath = propertiesPath;

        LOGGER.info("propertiesPath: " + propertiesPath);
        LOGGER.info("confDir: " + confDir);

        try {
            File dirFile = new File(confDir);

            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.info("create RedisConfig confDir error: " + confDir + e.getMessage());
        }


        try {
            File propertiesFile = new File(propertiesPath);
            if (!propertiesFile.exists()) {


                propertiesFile.createNewFile();

                BufferedWriter output = new BufferedWriter(new FileWriter(propertiesFile));
                StringBuilder contentBuilder = new StringBuilder();
                contentBuilder.append("redis.host=127.0.0.1\n").append("redis.port=6379\n")
                        .append("#connectionTimeout\n").append("redis.connectionTimeout=2000\n")
                        .append("#redis dbIndex，defaule:0\n")
                        .append("redis.db_index=0\n\n")
                        .append("#--------------redis pool config--------------\n")
                        .append("#maxTotal\n").append("redis.pool.maxTotal=100\n")
                        .append("#maxIdle\n").append("redis.pool.maxIdle=20\n")
                        .append("#maxWaitMillis:ms\n")
                        .append("redis.pool.maxWaitMillis=1000\n")
                        .append("#testOnBorrow\n")
                        .append("redis.pool.testOnBorrow=false\n")
                        .append("#testOnReturn\n")
                        .append("redis.pool.testOnReturn=true\n")
                        .append("#nginx Port\n").append("server.port=10080\n");

                output.write(contentBuilder.toString());
                output.close();

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.info("create RedisConfig File error: " + propertiesPath + e.getMessage());
        }
    }


    private void checkRedisConnect() {

        new Thread(new Runnable() {
            public void run() {
                int n = 0;
                while (true) {
                    if (ApiRouteServiceWrapper.checkRedisConnect() == false) {
                        n++;
                        System.out.println(n
                                + "/10 : Initial Route Configuration——redis connection fail...");

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            LOGGER.error("Thread.sleep throw except:"+e.getMessage());
                        }


                        if (n >= 10) {
                            System.out.println("Initial Route Configuration fail,timeout exit");
                            LOGGER.error("Initial Route Configuration——redis connection fail,timeout exit...");
                            break;
                        }
                    } else {
                        System.out.println("starting to initial Route Configuration");
                        // initRouteInfoFromConfig();
                        initRouteInfoFromJson();
                        System.out.println("starting to initial consul Configuration");
                        runConsulClientApp();

                        break;
                    }
                }

            }
        }).start();
    }


    

    /**
     * @Title: initVisualRangeMatches
     * @Description: TODO(According to the environment variable or a JSON file configuration initialization VisualRange filter conditions)
     * @return: void
     */
    private void initVisualRangeMatches(){
        try {
            if(System.getenv("APIGATEWAY_VISUAL_RANGE")==null)
            {
            
            URL visualRangePath = ApiRouteApp.class.getResource("/ext/initVisualRange/initVisualRangeMatches.json");
            if (visualRangePath != null) {
                String path = visualRangePath.getPath();
    
                LOGGER.info("read initVisualRangeMatches:" + path);
              
                String fileContent = FileUtil.readFile(path);
                JSONObject jsonObj = JSONObject.fromObject(fileContent);
                String visualRangeArray=jsonObj.get("visualRange").toString();
                
            
                RouteUtil.visualRangeMatches=StringUtils.split(visualRangeArray, ",");  
             
                
               
                }
            }
            else{
                RouteUtil.visualRangeMatches=StringUtils.split(System.getenv("APIGATEWAY_VISUAL_RANGE"), ",");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.error("read  initVisualRangeMatches Files or env(APIGATEWAY_VISUAL_RANGE) throw exception", e);
        }
    }

    /**
     * @Title: initRouteInfoFromJson
     * @Description: TODO(按照JSON文件配置初始化route数据)
     * @return: void
     */
    private void initRouteInfoFromJson() {

        URL apiDocsPath = ApiRouteApp.class.getResource("/ext/initServices");
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
        try {
            List<ApiRouteInfo> routeList =
                    (List<ApiRouteInfo>) JacksonJsonUtil.jsonToListBean(fileContent);
            for (ApiRouteInfo route : routeList) {
                String url = route.getUrl();

                if (RegExpTestUtil.urlRegExpTest(route.getServiceName())) {
                    
                    try{
                    CustomRouteInfo dbCustomRoute =
                            CustomRouteServiceWrapper.getInstance().getCustomRouteInstance(
                                    route.getServiceName());
                    }
                    catch(ExtendedNotFoundException e){

                        LOGGER.info("initCustomRoute: ServiceName--" + route.getServiceName());

                        CustomRouteInfo customRouteInfo = new CustomRouteInfo();
                        customRouteInfo.setControl(route.getControl());
                        customRouteInfo.setServers(route.getServers());
                        customRouteInfo.setServiceName(route.getServiceName());
                        customRouteInfo.setStatus(route.getStatus());
                        customRouteInfo.setUrl(route.getUrl());


                        CustomRouteServiceWrapper.getInstance().saveCustomRouteInstance(
                                customRouteInfo, "");


                    }
                } else {

                    if (RegExpTestUtil.apiRouteUrlRegExpTest(url) || url.startsWith("/api/microservices/")) {


                        try{
                        ApiRouteInfo dbApiRoute =
                                ApiRouteServiceWrapper.getInstance().getApiRouteInstance(
                                        route.getServiceName(), route.getVersion());
                        }
                        catch(ExtendedNotFoundException e){
                            LOGGER.info("initapiRoute: ServiceName--" + route.getServiceName());
                            ApiRouteServiceWrapper.getInstance().saveApiRouteInstance(route, "");
                        }

                       
                    } else if (RegExpTestUtil.iuiRouteUrlRegExpTest(url)  || url.equals("/iui/microservices")) {
                        
                        try{
                        IuiRouteInfo dbIuiRoute =
                                IuiRouteServiceWrapper.getInstance().getIuiRouteInstance(
                                        route.getServiceName());
                        }
                        catch(ExtendedNotFoundException e){
                    
                            LOGGER.info(" initiuiRoute: ServiceName--" + route.getServiceName());
                            IuiRouteInfo iuiRouteInfo = new IuiRouteInfo();
                            iuiRouteInfo.setControl(route.getControl());
                            iuiRouteInfo.setServers(route.getServers());
                            iuiRouteInfo.setServiceName(route.getServiceName());
                            iuiRouteInfo.setStatus(route.getStatus());
                            
                            if(url.equals("/iui/microservices")){
                                iuiRouteInfo.setUrl("/"+RouteUtil.IUI_ROOT_PATH+"/microservices");
                            }
                            else{
                            iuiRouteInfo.setUrl(route.getUrl());
                            }

                            IuiRouteServiceWrapper.getInstance().saveIuiRouteInstance(iuiRouteInfo);

                        }

                    } else {
                        LOGGER.error("init Service throw exception——serviceName: " + route.getServiceName()+",url:"+url);
                    }
                }



            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.error("read  initServices Files throw exception", e);
        }

    }



    /**
     * The listener registration service changes
     */
    private void registerServiceChangeListener() {
        MicroServiceDB.getInstance().addServiceChangeListener(new MicroServiceChangeListener());
    }

    // Open the consul to monitor subscription service
    private void runConsulClientApp() {
        DiscoverInfo config_discoverInfo = config.getDiscoverInfo();
        
        ConsulInfo config_consulInfo=config.getConsulInfo();
        
        RouteUtil.discoverInfo.setEnabled(config_discoverInfo.isEnabled());        
        
        if (config_discoverInfo.isEnabled()) {
            try{
            if(System.getenv("SDCLIENT_SVC_PORT")==null)
            {
                //yml          
                RouteUtil.discoverInfo.setIp(config_discoverInfo.getIp()); 
                RouteUtil.discoverInfo.setPort(config_discoverInfo.getPort());
                
            }
            else{
               
                 String  discoverAddress=System.getenv("SDCLIENT_SVC_PORT").split("//")[1];
                 String sdIP=discoverAddress.split(":")[0];
                 int sdPort=Integer
                         .parseInt(discoverAddress.split(":")[1]);
                 
                 RouteUtil.discoverInfo.setIp(sdIP);
                 RouteUtil.discoverInfo.setPort(sdPort);
                 
                 config_consulInfo.setIp(sdIP);
                 config_consulInfo.setPort(sdPort);
             
                
            }
            
           
               
                //Registration service discovery routing
                //api
                ApiRouteInfo discoverApiService=new ApiRouteInfo();
                discoverApiService.setServiceName("msdiscover");
                discoverApiService.setUrl("/api/microservices/v1");
                discoverApiService.setVersion("v1");
                discoverApiService.setMetricsUrl("/admin/metrics");
                discoverApiService.setApiJson("/api/microservices/v1/swagger.json");
                
                RouteServer[] servers=new RouteServer[1];
                servers[0]=new RouteServer(RouteUtil.discoverInfo.getIp(),String.valueOf(RouteUtil.discoverInfo.getPort()));
                discoverApiService.setServers(servers);
             
                ApiRouteServiceWrapper.getInstance().saveApiRouteInstance(discoverApiService, "");
               
                //iui
                IuiRouteInfo discoverIUIService=new IuiRouteInfo();
                discoverIUIService.setServiceName("msdiscover");
                discoverIUIService.setUrl("/iui/microservices");
                discoverIUIService.setServers(servers);
                IuiRouteServiceWrapper.getInstance().saveIuiRouteInstance(discoverIUIService);
                
               

            ConsulClientApp consulClientApp = new ConsulClientApp(config_consulInfo.getIp(), config_consulInfo.getPort());
            // Monitor service change
            consulClientApp.startServiceListen();
            LOGGER.info("start monitor consul service--" +config_consulInfo.getIp() + ":"
                    + config_consulInfo.getPort());
            }
            catch(Exception e){
                LOGGER.error("start monitor consul service fail:"+e.getMessage()); 
            }
        }
        
       
    }

}
