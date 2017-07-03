package org.onap.msb.apiroute.wrapper.util;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.onap.msb.apiroute.ApiRouteAppConfig;
import org.onap.msb.apiroute.api.DiscoverInfo;
import org.onap.msb.apiroute.wrapper.InitRouteServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unchecked")
public class ConfigUtil {
  private final static ConfigUtil instance = new ConfigUtil();


  private ConfigUtil() {}

  public static ConfigUtil getInstance() {
    return instance;
  }
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtil.class);
  
  private String serverPort="80";
  
  private String IUI_ROOT_PATH="iui";  
  
  private  String API_ROOT_PATH="api"; 
  
  private String namespaceMatches="all";
  
  private String visualRangeMatches="0";
  
  private String nodeMetaQueryParam="";
  
  private String network_plane_typeMatches="";
  
  private String[] routeWay={"ip"};
  
  private Map<String,String> labelMapMatches;
  
  private DiscoverInfo discoverInfo=new DiscoverInfo();  
  
  private String consul_ip="";
  
  private  String metricsUrl = "http://127.0.0.1:8066/admin/metrics";
  
  public void initRootPath() {
    String apiRootPathConfSource="Default";
    String iuiRootPathConfSource="Default";
    
    try {
      
      URL urlRootPath =
          ConfigUtil.class.getResource("/ext/initUrlRootPath/initUrlRootPath.json");
      if (urlRootPath != null) {
        String path = urlRootPath.getPath();

        LOGGER.warn("read initUrlRootPath:" + path);

        String fileContent = FileUtil.readFile(path);
        ObjectMapper mapper = new ObjectMapper();
      
        Map<String, String> map = mapper.readValue(fileContent, HashMap.class);
        if (map.get("iuiRootPath") != null) {
          IUI_ROOT_PATH = map.get("iuiRootPath");
          iuiRootPathConfSource="initUrlRootPath.json";
        }
        if (map.get("apiRootPath") != null) {
          API_ROOT_PATH = map.get("apiRootPath");
          apiRootPathConfSource="initUrlRootPath.json";
        }
       
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      LOGGER.error("init UrlRootPath throw exception", e);
    }
    
    LOGGER.warn("init IUI_ROOT_PATH from ["+iuiRootPathConfSource+"]:"+IUI_ROOT_PATH);
    LOGGER.warn("init API_ROOT_PATH from ["+apiRootPathConfSource+"]:"+API_ROOT_PATH);

  }
  
  public void initApiGatewayPort() {
    
    String env_APIGATEWAY_EXPOSE_PORT=System.getenv("APIGATEWAY_EXPOSE_PORT");
    String httpExposePortConfSource="Default";
    try {
      // read initApiGatewayConfig
      if (StringUtils.isBlank(env_APIGATEWAY_EXPOSE_PORT)) {
        URL apiGatewayConfigPath =
            ConfigUtil.class
                .getResource("/ext/initApiGatewayConfig/initApiGatewayConfig.json");
        if (apiGatewayConfigPath != null) {
          String path = apiGatewayConfigPath.getPath();

          LOGGER.warn("read initApiGatewayConfig:" + path);

          String fileContent = FileUtil.readFile(path);
          ObjectMapper mapper = new ObjectMapper();

          Map<String, Object> labelMap = mapper.readValue(fileContent, Map.class);
          if (labelMap.get("port") != null) {
            serverPort = (String) labelMap.get("port");
            httpExposePortConfSource="initApiGatewayConfig.json";
          }
        }
      } else {
           serverPort = env_APIGATEWAY_EXPOSE_PORT;
           httpExposePortConfSource="env:APIGATEWAY_EXPOSE_PORT";
      }
      LOGGER.warn("init APIGATEWAY http publish Port from ["+httpExposePortConfSource+"]:"+serverPort);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      LOGGER.error(
          "read  initApiGatewayConfig Files or env(APIGATEWAY_EXPOSE_PORT) throw exception", e);
    }
    
   
  }
  
  public void initConsulIp() {
    String sys_consulIp=System.getenv("CONSUL_IP");
    if (StringUtils.isNotBlank(sys_consulIp)) {
      consul_ip=sys_consulIp;
      LOGGER.warn("init consul_Ip  from [env:CONSUL_IP]:" + sys_consulIp);
    }
    else{
      LOGGER.warn("init consul_Ip  from [env:CONSUL_IP] is blank");
    }
    

  }
  
  public void initRouteNameSpaceMatches() {
    String env_NAMESPACE=System.getenv("NAMESPACE");
    String namespaceConfSource="Default";
    try {
      // read NAMESPACE
      if (StringUtils.isBlank(env_NAMESPACE)) {
        URL routeLabelsPath =
            InitRouteServiceWrapper.class
                .getResource("/ext/initRouteLabels/initRouteLabelsMatches.json");
        if (routeLabelsPath != null) {
          String path = routeLabelsPath.getPath();

          String fileContent = FileUtil.readFile(path);
          ObjectMapper mapper = new ObjectMapper();

          Map<String, Object> labelMap = mapper.readValue(fileContent, Map.class);
          if (labelMap.get("namespace") != null) {
            namespaceMatches = (String) labelMap.get("namespace");
            namespaceConfSource="initRouteLabelsMatches.json";
          }
        }
      } else {
        namespaceMatches =env_NAMESPACE;
        namespaceConfSource="env:NAMESPACE";
      }
      LOGGER.warn("init namespace Filter from ["+namespaceConfSource+"]:" + namespaceMatches);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      LOGGER.error("read  initRouteNameSpaceMatches Files or env(NAMESPACE) throw exception",
          e);
    }
    
  

 }
  /**
   * @Title: initRouteLabelsMatches
   * @Description: TODO(According to the environment variable or a JSON file configuration
   *               initialization Route filter conditions)
   * @return: void
   */
  public void initRouteLabelsMatches() {
    String env_ROUTE_LABELS=System.getenv("ROUTE_LABELS");
    String visualRangeConfSource="Default";
    String networkPlaneConfSource="Default";
    String labelConfSource="Default";
    try {
     
      // read ROUTE_LABELS
      if (StringUtils.isBlank(env_ROUTE_LABELS)) {
        URL routeLabelsPath =
            InitRouteServiceWrapper.class
                .getResource("/ext/initRouteLabels/initRouteLabelsMatches.json");
        if (routeLabelsPath != null) {
          String path = routeLabelsPath.getPath();

          String fileContent = FileUtil.readFile(path);
          ObjectMapper mapper = new ObjectMapper();

          Map<?, ?> labelMap = mapper.readValue(fileContent, Map.class);
          if (labelMap.get("predefineLabels") != null) {
            Map<String, String> predefineLabelMapMatches =
                (Map<String, String>) labelMap.get("predefineLabels");
            if (predefineLabelMapMatches.get("visualRange") != null) {
              visualRangeMatches = predefineLabelMapMatches.get("visualRange");
              visualRangeConfSource="initRouteLabelsMatches.json";
            }
            if (predefineLabelMapMatches.get("network_plane_type") != null) {
              network_plane_typeMatches =
                  predefineLabelMapMatches.get("network_plane_type");
              networkPlaneConfSource="initRouteLabelsMatches.json";
            }
          }

          if (labelMap.get("customLabels") != null) {
            labelMapMatches = (Map<String, String>) labelMap.get("customLabels");
            labelConfSource="initRouteLabelsMatches.json";
          }

        }
      } else {
        String[] env_routeLabels = StringUtils.split(env_ROUTE_LABELS, ",");
        Map<String, String> labelMap = new HashMap<String, String>();

        for (int i = 0; i < env_routeLabels.length; i++) {
          String[] labels = StringUtils.split(env_routeLabels[i], ":");

          if ("visualRange".equals(labels[0])) {
            visualRangeMatches = labels[1];
            visualRangeConfSource="env:ROUTE_LABELS";
          } else if ("network_plane_type".equals(labels[0])) {
            network_plane_typeMatches = labels[1];
            networkPlaneConfSource="env:ROUTE_LABELS";
          } else {
            labelMap.put(labels[0], labels[1]);
          }

        }

        labelConfSource="env:ROUTE_LABELS";
        labelMapMatches = labelMap;

      }
      LOGGER.warn("init visualRange Filter from [ "+visualRangeConfSource+" ]:" + visualRangeMatches);
      LOGGER.warn("init network_plane_type Filter from [ "+networkPlaneConfSource+" ]:" + network_plane_typeMatches);
      LOGGER.warn("init customLabels Filter from [ "+labelConfSource+" ]:" + labelMapMatches);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      LOGGER.error(
          "read  initRouteLabelsPathMatches Files or env(ROUTE_LABELS) throw exception",
          e);
    }
  }
  
  public void initRouteWay() {
    String env_ROUTE_WAY=System.getenv("ROUTE_WAY");
    try {
      // read NAMESPACE
      if (StringUtils.isBlank(env_ROUTE_WAY)) {
        URL routeLabelsPath =
            InitRouteServiceWrapper.class.getResource("/ext/initRouteWay/initRouteWay.json");
        if (routeLabelsPath != null) {
          String path = routeLabelsPath.getPath();

          String fileContent = FileUtil.readFile(path);
          ObjectMapper mapper = new ObjectMapper();

          Map<String, Object> routeWayMap = mapper.readValue(fileContent, Map.class);
          String routeWayFromConfig=(String)routeWayMap.get("routeWay");
          if (StringUtils.isNotBlank(routeWayFromConfig)) {
            routeWay =
                StringUtils.split(routeWayFromConfig, RouteUtil.SPLIT_LINE);
            LOGGER.warn("init RouteWay from [initRouteWay.json]:" + routeWayFromConfig);
          }
        }
      } else {
        routeWay = StringUtils.split(env_ROUTE_WAY, RouteUtil.SPLIT_LINE);
        LOGGER.warn("read initRouteWay from [env:ROUTE_WAY]:" + env_ROUTE_WAY);
      }
      

  


    } catch (Exception e) {
      // TODO Auto-generated catch block
      LOGGER.error("read  initRouteWay Files or env(ROUTE_WAY) throw exception", e);
    }
  }

  
  public void initDiscoverInfo(ApiRouteAppConfig configuration){
    DiscoverInfo config_discoverInfo = configuration.getDiscoverInfo();


    discoverInfo.setEnabled(config_discoverInfo.isEnabled());

    String discoverInfoConfSource="yaml config";

    if (config_discoverInfo.isEnabled()) {
      
     String discoverIP;
     String env_SDCLIENT_IP=System.getenv("SDCLIENT_IP");
    
        if (StringUtils.isBlank(env_SDCLIENT_IP)) {
          // yml
          discoverIP = config_discoverInfo.getIp();
        } else {
          discoverIP = env_SDCLIENT_IP;
          discoverInfoConfSource="env:SDCLIENT_IP";
        }

       discoverInfo.setIp(discoverIP.trim());
       discoverInfo.setPort(config_discoverInfo.getPort());
    }
    
    LOGGER.warn("init DiscoverInfo from ["+discoverInfoConfSource+"]--" + discoverInfo.toString()+" Enabled:"+discoverInfo.isEnabled());
  }
  
	public void initNodeMetaQueryParam() {
		// judge consul register node:caltalog
		String env_CONSUL_REGISTER_MODE = System.getenv("CONSUL_REGISTER_MODE");

		if (env_CONSUL_REGISTER_MODE == null
				|| !env_CONSUL_REGISTER_MODE.trim().equals("catalog")) {
			nodeMetaQueryParam = "";
			return;
		}

		// visual range
		String nodemeta_visualrange = nodemeta_visualrange(visualRangeMatches);

		LOGGER.warn("calc nodemeta_visualrange from [" + visualRangeMatches
				+ "]:" + nodemeta_visualrange);

		nodeMetaQueryParam = nodemeta_visualrange;

		// name space
		String nodemeta_namespace = nodemeta_namespace(namespaceMatches);
		LOGGER.warn("calc nodemeta_namespace from [" + namespaceMatches + "]:"
				+ nodemeta_namespace);

		if (!nodeMetaQueryParam.isEmpty() && !nodemeta_namespace.isEmpty()) {
			nodeMetaQueryParam += "&";
		}
		nodeMetaQueryParam += nodemeta_namespace;

		/*
		 * // nodemeta = (!nodemeta_visualrange.isEmpty() && !nodemeta_namespace
		 * .isEmpty()) ? nodemeta_visualrange + "&" + nodemeta_namespace :
		 * nodemeta_visualrange + nodemeta_namespace;
		 */

	}

	private String nodemeta_visualrange(final String visualRangeMatches) {

		if (visualRangeMatches == null || visualRangeMatches.isEmpty()) {
			return "";
		}

		// external:0
		if (visualRangeMatches.trim().equals("0")) {
			return "node-meta=external:true";
		}

		// internal:1
		if (visualRangeMatches.trim().equals("1")) {
			return "node-meta=internal:true";
		}

		return "";
	}
	

	private String nodemeta_namespace(final String namespaceMatches) {

		// exclude null,"",all,&,|,!
		if (namespaceMatches == null || namespaceMatches.isEmpty()
				|| namespaceMatches.contains("all")
				|| namespaceMatches.contains("&")
				|| namespaceMatches.contains("|")
				|| namespaceMatches.contains("!")) {
			return "";
		}

		return "node-meta=ns:" + namespaceMatches;
	}
  
  public String getServerPort() {
    return serverPort;
  }

  public String getIUI_ROOT_PATH() {
    return IUI_ROOT_PATH;
  }

  public String getAPI_ROOT_PATH() {
    return API_ROOT_PATH;
  }

  public String getNamespaceMatches() {
    return namespaceMatches;
  }

  public String getVisualRangeMatches() {
    return visualRangeMatches;
  }

  public String getNetwork_plane_typeMatches() {
    return network_plane_typeMatches;
  }
  
  public String[] getRouteWay() {
    return routeWay.clone();
  }

  public Map<String, String> getLabelMapMatches() {
    return labelMapMatches;
  }

  public DiscoverInfo getDiscoverInfo() {
    return discoverInfo;
  }

  public String getMetricsUrl() {
    return metricsUrl;
  }

  public void setMetricsUrl(String metricsUrl) {
    this.metricsUrl = metricsUrl;
  }

	public String getNodeMetaQueryParam() {
		return nodeMetaQueryParam;
	}

  public String getConsul_ip() {
    return consul_ip;
  }



}
