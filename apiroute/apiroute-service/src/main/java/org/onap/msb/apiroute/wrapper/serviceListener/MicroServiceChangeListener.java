/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.serviceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.onap.msb.apiroute.api.ApiRouteInfo;
import org.onap.msb.apiroute.api.CustomRouteInfo;
import org.onap.msb.apiroute.api.DiscoverInfo;
import org.onap.msb.apiroute.api.IuiRouteInfo;
import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;
import org.onap.msb.apiroute.api.PublishFullAddress;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.wrapper.ApiRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.CustomRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.IuiRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.util.CommonUtil;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.onap.msb.apiroute.wrapper.util.HttpClientUtil;
import org.onap.msb.apiroute.wrapper.util.JacksonJsonUtil;
import org.onap.msb.apiroute.wrapper.util.RegExpTestUtil;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

public class MicroServiceChangeListener implements IMicroServiceChangeListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(MicroServiceChangeListener.class);

  @Override
  public void onDelete(MicroServiceFullInfo microServiceInfo) throws Exception {

    String path = microServiceInfo.getPath();

    String[] routeWay = ConfigUtil.getInstance().getRouteWay();

    for (int i = 0; i < routeWay.length; i++) {

      if (StringUtils.isNotBlank(path) && !"/".equals(path)) {
        // 1.按path优先判断类型
        String host = getHost(microServiceInfo);
        deleteServiceByUrl(path, host, microServiceInfo.getPublish_port(), routeWay[i]);
      } else {
        // 2.1 域名判断url
        if (RouteUtil.ROUTEWAY_DOMAIN.equals(routeWay[i]) && ifRootByDomain(microServiceInfo)) {
           deleteServiceByDomain4Root(microServiceInfo);
        } else {
          // 2.2 按协议优先判断类型
          deleteServiceByProtocol(microServiceInfo, routeWay[i]);
        }
        
      }


    }

  }


  @Override
  public void onSave(MicroServiceFullInfo microServiceInfo) throws Exception {

    String path = microServiceInfo.getPath();
    String[] routeWay = ConfigUtil.getInstance().getRouteWay();

    for (int i = 0; i < routeWay.length; i++) {
      // 1.按path优先判断类型
      if (StringUtils.isNotBlank(path) && !"/".equals(path)) {
        saveServiceByPath(microServiceInfo, routeWay[i]);
      } else {
     // 2.1 域名判断url
        if (RouteUtil.ROUTEWAY_DOMAIN.equals(routeWay[i]) && ifRootByDomain(microServiceInfo)) {
           saveServiceByDomain4Root(microServiceInfo);
        } else {
          // 2.2 按协议优先判断类型
          saveServiceByProtocol(microServiceInfo, routeWay[i]);
        }
      }
    }

  }

  
  //判断按协议发布地址是否和注册的URL一致，如果一致发布地址保存为/，否则保存为协议类型的发布地址
  private boolean ifRootByDomain(MicroServiceFullInfo microServiceInfo){
    
    
    if("/".equals(microServiceInfo.getUrl())) return true;
      
    String protocol = microServiceInfo.getProtocol();
    String routeName =
        RouteUtil.getRouteNameByns(microServiceInfo.getServiceName(),
            microServiceInfo.getNamespace());
    String publishUrl="";
    String version = "";
    if (StringUtils.isNotBlank(microServiceInfo.getVersion())) {
      version = "/" + microServiceInfo.getVersion();
    }

    switch (protocol) {
      case RouteUtil.PROTOCOL_UI:
        publishUrl = "/iui/" + routeName;
        break;
      case RouteUtil.PROTOCOL_REST:
        publishUrl = "/api/" + routeName + version;
        break;
      case RouteUtil.PROTOCOL_HTTP:
        publishUrl = "/" + routeName + version;
        break;
    }
    return  publishUrl.equals(microServiceInfo.getUrl());
   
  }


  private void saveServiceByDomain4Root(MicroServiceFullInfo microServiceInfo) throws Exception {

    CustomRouteInfo[] customRouteInfos =
        this.buildCustomRouteInfo(microServiceInfo, "/", RouteUtil.ROUTEWAY_DOMAIN);
    for (int i = 0; i < customRouteInfos.length; i++) {
      customRouteInfos[i].setUrl("/");
      CustomRouteServiceWrapper.getInstance().saveCustomRouteInstance(customRouteInfos[i],
          RouteUtil.ROUTEWAY_DOMAIN);
    }
  }
  
  private void deleteServiceByDomain4Root(MicroServiceFullInfo microServiceInfo) throws Exception {
   
    CustomRouteServiceWrapper.getInstance().deleteCustomRoute("/", getHost(microServiceInfo),
        microServiceInfo.getPublish_port(),  RouteUtil.ROUTEWAY_DOMAIN);
  }

  /**
   * @Title saveServiceByProtocol
   * @Description TODO(按用户注册协议保存服务)
   * @param microServiceInfo
   * @param routeWay
   * @return void
   * @throws Exception
   */
  private void saveServiceByProtocol(MicroServiceFullInfo microServiceInfo, String routeWay)
      throws Exception {
    String protocol = microServiceInfo.getProtocol();
    String routeName =
        RouteUtil.getRouteNameByns(microServiceInfo.getServiceName(),
            microServiceInfo.getNamespace());

    switch (protocol) {
      case RouteUtil.PROTOCOL_UI:
        IuiRouteInfo[] iuiRouteInfos =
            this.buildIuiRouteInfo(microServiceInfo, routeName, routeWay);
        for (int i = 0; i < iuiRouteInfos.length; i++) {
          IuiRouteServiceWrapper.getInstance().saveIuiRouteInstance(iuiRouteInfos[i], routeWay);
        }
        break;

      case RouteUtil.PROTOCOL_REST:

        ApiRouteInfo[] apiRouteInfos =
            this.buildApiRouteInfo(microServiceInfo, routeName, microServiceInfo.getVersion(),
                routeWay);
        for (int i = 0; i < apiRouteInfos.length; i++) {
          ApiRouteServiceWrapper.getInstance().saveApiRouteInstance(apiRouteInfos[i], routeWay);
        }
        break;
      case RouteUtil.PROTOCOL_HTTP:
        CustomRouteInfo[] customRouteInfos =
            this.buildCustomRouteInfo(microServiceInfo,
                getHttpName(routeName, microServiceInfo.getVersion()), routeWay);
        for (int i = 0; i < customRouteInfos.length; i++) {
          CustomRouteServiceWrapper.getInstance().saveCustomRouteInstance(customRouteInfos[i],
              routeWay);
        }
        break;
    }
  }

  /**
   * @Title deleteServiceByProtocol
   * @Description TODO(按用户注册协议删除服务)
   * @param microServiceInfo
   * @param routeWay
   * @return void
   */
  private void deleteServiceByProtocol(MicroServiceFullInfo microServiceInfo, String routeWay) {
    String protocol = microServiceInfo.getProtocol();
    String host = getHost(microServiceInfo);
    String routeName =
        RouteUtil.getRouteNameByns(microServiceInfo.getServiceName(),
            microServiceInfo.getNamespace());

    if (RouteUtil.PROTOCOL_UI.equals(protocol)) {

      if (RouteUtil.ROUTEWAY_IP.equals(routeWay)) {
        // two ports
        String[] publishPorts = StringUtils.split(microServiceInfo.getPublish_port(), "|");
        if (publishPorts.length == 2) {
          IuiRouteServiceWrapper.getInstance().deleteIuiRoute(routeName, host, publishPorts[0],
              routeWay);
          IuiRouteServiceWrapper.getInstance().deleteIuiRoute(routeName, host, publishPorts[1],
              routeWay);
          return;
        }
      }

      IuiRouteServiceWrapper.getInstance().deleteIuiRoute(routeName, host,
          microServiceInfo.getPublish_port(), routeWay);
    } else if (RouteUtil.PROTOCOL_REST.equals(protocol)) {

      if (RouteUtil.ROUTEWAY_IP.equals(routeWay)) {
        // two ports
        String[] publishPorts = StringUtils.split(microServiceInfo.getPublish_port(), "|");
        if (publishPorts.length == 2) {
          ApiRouteServiceWrapper.getInstance().deleteApiRoute(routeName,
              microServiceInfo.getVersion(), host, publishPorts[0], routeWay);
          ApiRouteServiceWrapper.getInstance().deleteApiRoute(routeName,
              microServiceInfo.getVersion(), host, publishPorts[1], routeWay);
          return;
        }
      }
      ApiRouteServiceWrapper.getInstance().deleteApiRoute(routeName, microServiceInfo.getVersion(),
          host, microServiceInfo.getPublish_port(), routeWay);
    } else if (RouteUtil.PROTOCOL_HTTP.equals(protocol)) {

      if (RouteUtil.ROUTEWAY_IP.equals(routeWay)) {
        // two ports
        String[] publishPorts = StringUtils.split(microServiceInfo.getPublish_port(), "|");
        if (publishPorts.length == 2) {
          CustomRouteServiceWrapper.getInstance().deleteCustomRoute(
              getHttpName(routeName, microServiceInfo.getVersion()), host, publishPorts[0],
              routeWay);
          CustomRouteServiceWrapper.getInstance().deleteCustomRoute(
              getHttpName(routeName, microServiceInfo.getVersion()), host, publishPorts[1],
              routeWay);
          return;
        }
      }
      CustomRouteServiceWrapper.getInstance().deleteCustomRoute(
          getHttpName(routeName, microServiceInfo.getVersion()), host,
          microServiceInfo.getPublish_port(), routeWay);
    }
  }

  /**
   * @Title saveServiceByUrl
   * @Description TODO(按URL地址判断服务协议并保存到路由表)
   * @param url
   * @param microServiceInfo
   * @param routeWay
   * @return void
   * @throws Exception
   */
  private void saveServiceByPath(MicroServiceFullInfo microServiceInfo, String routeWay)
      throws Exception {
    String redis_serviceName;
    String path=microServiceInfo.getPath();
    if (RegExpTestUtil.apiRouteUrlRegExpTest(path)) {
      // protocol:"REST"
      String[] serviceKey = RegExpTestUtil.apiServiceNameMatch4URL(path);
      if (serviceKey == null) {
        LOGGER.error("save api Service ByUrl is error:[url]" + path);
        return;
      }
      redis_serviceName = serviceKey[0];
      String redis_serviceVersion = serviceKey[1];

      ApiRouteInfo[] apiRouteInfos =
          this.buildApiRouteInfo(microServiceInfo, redis_serviceName, redis_serviceVersion,
              routeWay);
      for (int i = 0; i < apiRouteInfos.length; i++) {
        ApiRouteServiceWrapper.getInstance().saveApiRouteInstance(apiRouteInfos[i], routeWay);
      }
    } else if (RegExpTestUtil.iuiRouteUrlRegExpTest(path)) {
      // protocol:"UI"
      // 根据url获取服务名
      redis_serviceName = RegExpTestUtil.iuiServiceNameMatch4URL(path);
      if (redis_serviceName == null) {
        LOGGER.error("save iui Service ByUrl is error:[url]" + path);
        return;
      }
      IuiRouteInfo[] iuiRouteInfos =
          this.buildIuiRouteInfo(microServiceInfo, redis_serviceName, routeWay);
      for (int i = 0; i < iuiRouteInfos.length; i++) {
        IuiRouteServiceWrapper.getInstance().saveIuiRouteInstance(iuiRouteInfos[i], routeWay);
      }
    } else {
      // protocol:"HTTP";
      redis_serviceName = path;
      CustomRouteInfo[] customRouteInfos =
          this.buildCustomRouteInfo(microServiceInfo, redis_serviceName, routeWay);
      for (int i = 0; i < customRouteInfos.length; i++) {
        CustomRouteServiceWrapper.getInstance().saveCustomRouteInstance(customRouteInfos[i],
            routeWay);
      }
    }
  }

  /**
   * @Title deleteServiceByUrl
   * @Description TODO(按URL地址判断服务协议并从路由表删除)
   * @param url
   * @param host
   * @param publish_port
   * @param routeWay
   * @return void
   */
  private void deleteServiceByUrl(String url, String host, String publish_port, String routeWay) {
    // 根据Url格式判断服务类型
    String redis_serviceName;

    if (RegExpTestUtil.apiRouteUrlRegExpTest(url)) {
      // protocol:"REST"
      String[] serviceKey = RegExpTestUtil.apiServiceNameMatch4URL(url);
      if (serviceKey == null) {
        LOGGER.error("delete api Service ByUrl is error:[url]" + url);
        return;
      }

      redis_serviceName = serviceKey[0];
      String redis_serviceVersion = serviceKey[1];

      if (RouteUtil.ROUTEWAY_IP.equals(routeWay)) {
        // two ports
        String[] publishPorts = StringUtils.split(publish_port, "|");
        if (publishPorts.length == 2) {
          ApiRouteServiceWrapper.getInstance().deleteApiRoute(redis_serviceName,
              redis_serviceVersion, host, publishPorts[0], routeWay);
          ApiRouteServiceWrapper.getInstance().deleteApiRoute(redis_serviceName,
              redis_serviceVersion, host, publishPorts[1], routeWay);
          return;
        }
      }

      ApiRouteServiceWrapper.getInstance().deleteApiRoute(redis_serviceName, redis_serviceVersion,
          host, publish_port, routeWay);



    } else if (RegExpTestUtil.iuiRouteUrlRegExpTest(url)) {
      // protocol:"UI"
      // 根据url获取服务名
      redis_serviceName = RegExpTestUtil.iuiServiceNameMatch4URL(url);
      if (redis_serviceName == null) {
        LOGGER.error("delete iui Service ByUrl is error:[url]" + url);
        return;
      }

      if (RouteUtil.ROUTEWAY_IP.equals(routeWay)) {
        // two ports
        String[] publishPorts = StringUtils.split(publish_port, "|");
        if (publishPorts.length == 2) {
          IuiRouteServiceWrapper.getInstance().deleteIuiRoute(redis_serviceName, host,
              publishPorts[0], routeWay);
          IuiRouteServiceWrapper.getInstance().deleteIuiRoute(redis_serviceName, host,
              publishPorts[1], routeWay);
          return;
        }
      }

      IuiRouteServiceWrapper.getInstance().deleteIuiRoute(redis_serviceName, host, publish_port,
          routeWay);


    } else {
      // protocol:"HTTP";
      redis_serviceName = url;

      if (RouteUtil.ROUTEWAY_IP.equals(routeWay)) {
        // two ports
        String[] publishPorts = StringUtils.split(publish_port, "|");
        if (publishPorts.length == 2) {
          CustomRouteServiceWrapper.getInstance().deleteCustomRoute(redis_serviceName, host,
              publishPorts[0], routeWay);
          CustomRouteServiceWrapper.getInstance().deleteCustomRoute(redis_serviceName, host,
              publishPorts[1], routeWay);
          return;
        }
      }

      CustomRouteServiceWrapper.getInstance().deleteCustomRoute(redis_serviceName, host,
          publish_port, routeWay);
    }

  }



  /**
   * @Title getCustomName
   * @Description TODO(获取HTTP服务路由名)
   * @param routeName
   * @param version
   * @return
   * @return String
   */
  private String getHttpName(String routeName, String version) {
    if (!routeName.startsWith("/")) {
      routeName = "/" + routeName;
    }

    if (StringUtils.isNotBlank(version)) {
      routeName += "/" + version;
    }
    return routeName;
  }


  private String getHost(MicroServiceFullInfo microServiceInfo) {
    String host;
    if (StringUtils.isNotBlank(microServiceInfo.getHost())) {
      host = microServiceInfo.getHost().toLowerCase();
    } else {
      // host为空，取默认规则 服务名-ns
      host = microServiceInfo.getServiceName().toLowerCase();
    }

    return host;
  }



  @Override
  public void onChange(String serviceName, String version, MicroServiceFullInfo microServiceInfo)
      throws Exception {
    // TODO Auto-generated method stub

    if (RouteUtil.PROTOCOL_UI.equals(microServiceInfo.getProtocol())) {
      IuiRouteInfo[] iuiRouteInfos =
          this.buildIuiRouteInfo(microServiceInfo, serviceName, RouteUtil.ROUTEWAY_IP);
      for (int i = 0; i < iuiRouteInfos.length; i++) {
        IuiRouteServiceWrapper.getInstance().saveIuiRouteInstance(iuiRouteInfos[i],
            RouteUtil.ROUTEWAY_IP);
      }
    } else if (RouteUtil.PROTOCOL_REST.equals(microServiceInfo.getProtocol())) {
      ApiRouteInfo[] apiRouteInfos =
          this.buildApiRouteInfo(microServiceInfo, serviceName, version, RouteUtil.ROUTEWAY_IP);
      for (int i = 0; i < apiRouteInfos.length; i++) {
        ApiRouteServiceWrapper.getInstance().saveApiRouteInstance(apiRouteInfos[i],
            RouteUtil.ROUTEWAY_IP);
      }
    } else if (RouteUtil.PROTOCOL_HTTP.equals(microServiceInfo.getProtocol())) {
      if (!serviceName.startsWith("/")) {
        serviceName = "/" + serviceName;
      }
      CustomRouteInfo[] customRouteInfos =
          this.buildCustomRouteInfo(microServiceInfo, serviceName, RouteUtil.ROUTEWAY_IP);
      for (int i = 0; i < customRouteInfos.length; i++) {
        CustomRouteServiceWrapper.getInstance().saveCustomRouteInstance(customRouteInfos[i],
            RouteUtil.ROUTEWAY_IP);
      }
    }
  }


  @Override
  public void onStatusChange(String serviceName, String version, String host, String protocol,
      String publish_port, String status) {

    // 获取服务的host

    if (StringUtils.isBlank(host)) {
      host = serviceName.toLowerCase();
    }

    if (RouteUtil.PROTOCOL_UI.equals(protocol)) {

      IuiRouteServiceWrapper.getInstance().updateIuiRouteStatus(serviceName, host, publish_port,
          status, RouteUtil.ROUTEWAY_IP);

    } else if (RouteUtil.PROTOCOL_REST.equals(protocol)) {
      ApiRouteServiceWrapper.getInstance().updateApiRouteStatus(serviceName, version, host,
          publish_port, status, RouteUtil.ROUTEWAY_IP);

    } else if (RouteUtil.PROTOCOL_HTTP.equals(protocol)) {
      if (!serviceName.startsWith("/")) {
        serviceName = "/" + serviceName;
      }
      CustomRouteServiceWrapper.getInstance().updateCustomRouteStatus(serviceName, host,
          publish_port, status, RouteUtil.ROUTEWAY_IP);
    }


  }

  private boolean buildRouteHttpProtocol(MicroServiceFullInfo microServiceInfo, String routeWay) {

    // Portal协议处理
    if (RouteUtil.CUSTOM_PORTAL.equals(microServiceInfo.getCustom())) {
      if (RouteUtil.ROUTEWAY_DOMAIN.equals(routeWay)) {
        return true;
      } else {
        return false;
      }
    }

    // 自定义开启SSL处理
    return microServiceInfo.isEnable_ssl();

  }

  private RouteServer[] buildRouteNodes(MicroServiceFullInfo microServiceInfo, String routeWay) {

    // 针对custom=portal场景的域名路由使用apigateway发布地址作为node
    if (RouteUtil.CUSTOM_PORTAL.equals(microServiceInfo.getCustom())) {
      if (RouteUtil.ROUTEWAY_DOMAIN.equals(routeWay)) {

        String discoverServiceName =
            RouteUtil.getRouteNameByns(microServiceInfo.getServiceName(),
                microServiceInfo.getNamespace());
        List<Node> publishNodes =
            getPublishNodes(discoverServiceName, microServiceInfo.getVersion(),
                microServiceInfo.getNamespace());
        if (publishNodes != null && publishNodes.size() > 0) {
          RouteServer[] routeServers = new RouteServer[publishNodes.size()];
          int i = 0;
          for (Node node : publishNodes) {
            RouteServer routeServer = new RouteServer(node.getIp(), node.getPort());
            routeServers[i] = routeServer;
            i++;
          }
          return routeServers;
        }
      }
    }


    Set<Node> nodes = microServiceInfo.getNodes();
    RouteServer[] routeServers = new RouteServer[nodes.size()];
    int n = 0;
    for (Node node : nodes) {
      RouteServer routeServer = new RouteServer(node.getIp(), node.getPort());
      routeServers[n] = routeServer;
      n++;
    }

    return routeServers;

  }

  /**
   * From MicroServiceInfo to ApiRouteInfo
   * 
   * @param microServiceInfo
   * @return
   */
  private ApiRouteInfo[] buildApiRouteInfo(MicroServiceFullInfo microServiceInfo,
      String redis_serviceName, String redis_version, String routeWay) {

    ApiRouteInfo apiRouteInfo = new ApiRouteInfo();
    apiRouteInfo.setUrl(microServiceInfo.getUrl());

    apiRouteInfo.setServers(buildRouteNodes(microServiceInfo, routeWay));

    apiRouteInfo.setVisualRange(RouteUtil.getVisualRangeByRouter(microServiceInfo.getVisualRange()));


    if ("ip_hash".equals(microServiceInfo.getLb_policy())) {
      apiRouteInfo.setUseOwnUpstream("1");
    }

    apiRouteInfo.setConsulServiceName(microServiceInfo.getServiceName());
    apiRouteInfo.setServiceName(redis_serviceName);
    apiRouteInfo.setVersion(redis_version);
    apiRouteInfo.setApiJson(microServiceInfo.getUrl() + "/swagger.json");
    apiRouteInfo.setMetricsUrl("/admin/metrics");
    apiRouteInfo.setEnable_ssl(buildRouteHttpProtocol(microServiceInfo, routeWay));
    // 默认 HttpProtocol和PublishProtocol=http
    if (apiRouteInfo.isEnable_ssl()) {
      apiRouteInfo.setPublishProtocol("https");
    }

    // 获取服务的host
    String host = getHost(microServiceInfo);

    apiRouteInfo.setHost(host.toLowerCase());
    apiRouteInfo.setNamespace(microServiceInfo.getNamespace());

    if (RouteUtil.ROUTEWAY_IP.equals(routeWay)) {

      if (StringUtils.isNotBlank(microServiceInfo.getPublish_port())) {
        apiRouteInfo.setPublishProtocol("https");
      }

      // 获取服务的发布端口(支持多端口格式:https|http)
      String[] publishPorts = StringUtils.split(microServiceInfo.getPublish_port(), "|");
      if (publishPorts.length == 2) {
        apiRouteInfo.setPublishProtocol("https");
        apiRouteInfo.setPublish_port(publishPorts[0]);

        try {
          ApiRouteInfo apiRouteInfo_http = (ApiRouteInfo) apiRouteInfo.clone();
          apiRouteInfo.setPublishProtocol("http");
          apiRouteInfo.setPublish_port(publishPorts[1]);
          return new ApiRouteInfo[] {apiRouteInfo, apiRouteInfo_http};
        } catch (CloneNotSupportedException e) {
          LOGGER.error("CLONE is wrong:" + apiRouteInfo);
          return new ApiRouteInfo[] {apiRouteInfo};
        }

      }
    }



    apiRouteInfo.setPublish_port(microServiceInfo.getPublish_port());
    return new ApiRouteInfo[] {apiRouteInfo};


  }


  /**
   * From MicroServiceInfo to CustomRouteInfo
   * 
   * @param microServiceInfo
   * @return
   */
  private CustomRouteInfo[] buildCustomRouteInfo(MicroServiceFullInfo microServiceInfo,
      String redis_serviceName, String routeWay) {

    CustomRouteInfo customRouteInfo = new CustomRouteInfo();
    customRouteInfo.setUrl(microServiceInfo.getUrl());


    customRouteInfo.setServers(buildRouteNodes(microServiceInfo, routeWay));

    customRouteInfo.setVisualRange(RouteUtil.getVisualRangeByRouter(microServiceInfo.getVisualRange()));

    if ("ip_hash".equals(microServiceInfo.getLb_policy())) {
      customRouteInfo.setUseOwnUpstream("1");
    }

    customRouteInfo.setConsulServiceName(microServiceInfo.getServiceName());
    customRouteInfo.setServiceName(redis_serviceName);

    // 获取服务的host
    String host = getHost(microServiceInfo);

    customRouteInfo.setHost(host.toLowerCase());
    customRouteInfo.setNamespace(microServiceInfo.getNamespace());
    customRouteInfo.setEnable_ssl(buildRouteHttpProtocol(microServiceInfo, routeWay));

    if (customRouteInfo.isEnable_ssl()) {
      customRouteInfo.setPublishProtocol("https");
    }


    if (RouteUtil.ROUTEWAY_IP.equals(routeWay)) {
      if (StringUtils.isNotBlank(microServiceInfo.getPublish_port())) {
        customRouteInfo.setPublishProtocol("https");
      }

      String[] publishPorts = StringUtils.split(microServiceInfo.getPublish_port(), "|");
      if (publishPorts.length == 2) {
        // 获取服务的发布端口(支持多端口格式:https|http)
        customRouteInfo.setPublishProtocol("https");
        customRouteInfo.setPublish_port(publishPorts[0]);

        try {
          CustomRouteInfo customRouteInfo_http = (CustomRouteInfo) customRouteInfo.clone();
          customRouteInfo.setPublishProtocol("http");
          customRouteInfo.setPublish_port(publishPorts[1]);
          return new CustomRouteInfo[] {customRouteInfo, customRouteInfo_http};
        } catch (CloneNotSupportedException e) {
          LOGGER.error("CLONE is wrong:" + customRouteInfo);
          return new CustomRouteInfo[] {customRouteInfo};
        }

      }
    }


    customRouteInfo.setPublish_port(microServiceInfo.getPublish_port());
    return new CustomRouteInfo[] {customRouteInfo};
  }


  /**
   * From MicroServiceInfo to IuiRouteInfo
   * 
   * @param microServiceInfo
   * @return
   */
  private IuiRouteInfo[] buildIuiRouteInfo(MicroServiceFullInfo microServiceInfo,
      String redis_serviceName, String routeWay) {

    IuiRouteInfo iuiRouteInfo = new IuiRouteInfo();
    iuiRouteInfo.setUrl(microServiceInfo.getUrl());

    iuiRouteInfo.setServers(buildRouteNodes(microServiceInfo, routeWay));

    iuiRouteInfo.setVisualRange(RouteUtil.getVisualRangeByRouter(microServiceInfo.getVisualRange()));

    if ("ip_hash".equals(microServiceInfo.getLb_policy())) {
      iuiRouteInfo.setUseOwnUpstream("1");
    }


    iuiRouteInfo.setConsulServiceName(microServiceInfo.getServiceName());
    iuiRouteInfo.setServiceName(redis_serviceName);

    // 获取服务的host
    String host = getHost(microServiceInfo);

    iuiRouteInfo.setHost(host.toLowerCase());
    iuiRouteInfo.setNamespace(microServiceInfo.getNamespace());
    iuiRouteInfo.setEnable_ssl(buildRouteHttpProtocol(microServiceInfo, routeWay));
    if (iuiRouteInfo.isEnable_ssl()) {
      iuiRouteInfo.setPublishProtocol("https");
    }

    if (RouteUtil.ROUTEWAY_IP.equals(routeWay)) {

      if (StringUtils.isNotBlank(microServiceInfo.getPublish_port())) {
        iuiRouteInfo.setPublishProtocol("https");
      }

      String[] publishPorts = StringUtils.split(microServiceInfo.getPublish_port(), "|");
      if (publishPorts.length == 2) {
        // 获取服务的发布端口(支持多端口格式:https|http)
        iuiRouteInfo.setPublishProtocol("https");
        iuiRouteInfo.setPublish_port(publishPorts[0]);

        try {
          IuiRouteInfo iuiRouteInfo_http = (IuiRouteInfo) iuiRouteInfo.clone();
          iuiRouteInfo.setPublishProtocol("http");
          iuiRouteInfo.setPublish_port(publishPorts[1]);
          return new IuiRouteInfo[] {iuiRouteInfo, iuiRouteInfo_http};
        } catch (CloneNotSupportedException e) {
          LOGGER.error("CLONE is wrong:" + iuiRouteInfo);
          return new IuiRouteInfo[] {iuiRouteInfo};
        }

      }
    }
    iuiRouteInfo.setPublish_port(microServiceInfo.getPublish_port());
    return new IuiRouteInfo[] {iuiRouteInfo};
  }



  private List<Node> getPublishNodes(String discoverServiceName, String version, String namespace) {
    List<Node> nodes = new ArrayList<Node>();

    if (StringUtils.isBlank(version)) {
      version = "null";
    }

    DiscoverInfo discoverInfo = ConfigUtil.getInstance().getDiscoverInfo();

    String allpublishaddressUrl =
        (new StringBuilder().append("http://").append(discoverInfo.toString())
            .append(RouteUtil.MSB_ROUTE_URL).append("/").append(discoverServiceName)
            .append("/version/").append(version).append("/allpublishaddress?namespace=")
            .append(namespace).append("&visualRange=0")).toString();

    String resultJson = HttpClientUtil.httpGet(allpublishaddressUrl);
    List<PublishFullAddress> publishFullAddressList =
        JacksonJsonUtil
            .jsonToListBean(resultJson, new TypeReference<List<PublishFullAddress>>() {});
    if (publishFullAddressList != null && publishFullAddressList.size() > 0) {
      for (PublishFullAddress publishFullAddress : publishFullAddressList) {
        if (StringUtils.isNotBlank(publishFullAddress.getIp())
            && "https".equals(publishFullAddress.getPublish_protocol())) {
          nodes.add(new Node(publishFullAddress.getIp(), publishFullAddress.getPort()));
        }

      }
    }

    return nodes;
  }


}
