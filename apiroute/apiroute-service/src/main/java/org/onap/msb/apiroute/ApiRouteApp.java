/**
 * Copyright 2016-2017 ZTE, Inc. and others.
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

package org.onap.msb.apiroute;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;

import org.onap.msb.apiroute.health.ApiRouteHealthCheck;
import org.onap.msb.apiroute.resources.ApiRouteResource;
import org.onap.msb.apiroute.resources.CustomRouteResource;
import org.onap.msb.apiroute.resources.IuiRouteResource;
import org.onap.msb.apiroute.resources.MicroServiceResource;
import org.onap.msb.apiroute.wrapper.InitRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ApiRouteApp extends Application<ApiRouteAppConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRouteApp.class);

    public static void main(String[] args) throws Exception {
        new ApiRouteApp().run(args);
        
        InitRouteServiceWrapper.getInstance().initFilterConfig();
        
        InitRouteServiceWrapper.getInstance().initDataSynchro();
        
        InitRouteServiceWrapper.getInstance().initHealthCheck();
    }


    @Override
    public String getName() {
        return " MicroService Bus ";
    }

    @Override
    public void initialize(Bootstrap<ApiRouteAppConfig> bootstrap) {
      super.initialize(bootstrap);
        
    }

    @Override
    public void run(ApiRouteAppConfig configuration, Environment environment) throws Exception {
     
   
      ConfigUtil.getInstance().initRootPath();
     
      
       String iuiRootPath=ConfigUtil.getInstance().getIUI_ROOT_PATH();
      
        // new AssetsBundle("/iui-metrics", "/"+iuiRootPath+"/microservices/metrics","index.html", "iui-metrics").run(environment); 
        
        new AssetsBundle("/iui-route",  "/"+iuiRootPath+"/microservices", "index.html","iui-microservices").run(environment); 
        
        new AssetsBundle("/api-doc",  "/"+iuiRootPath+"/microservices/api-doc","index.html", "api-doc").run(environment);
        
        new AssetsBundle("/ext",  "/"+iuiRootPath+"/microservices/ext","index.html", "ext").run(environment);
        
        
        

        final ApiRouteHealthCheck healthCheck =new ApiRouteHealthCheck();
        environment.healthChecks().register("consulCheck", healthCheck);
        
        environment.jersey().register(new ApiRouteResource());
        environment.jersey().register(new IuiRouteResource());   
        environment.jersey().register(new CustomRouteResource());
        environment.jersey().register(new MicroServiceResource());
        
        // initSwaggerConfig(environment, configuration);
        
        ConfigUtil.getInstance().initConsulIp();
        ConfigUtil.getInstance().initDiscoverInfo(configuration);
        // InitRouteServiceWrapper.getInstance().initMetricsConfig(configuration);
        
        
    }
    

    
    


    private void initSwaggerConfig(Environment environment, ApiRouteAppConfig configuration) {

        environment.jersey().register(new ApiListingResource());
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        BeanConfig config = new BeanConfig();
        config.setTitle("ApiRoute RESTful API");
        config.setVersion("1.0.0");
        config.setResourcePackage("org.onap.msb.apiroute.resources");
        SimpleServerFactory simpleServerFactory =(SimpleServerFactory) configuration.getServerFactory();
        String basePath = simpleServerFactory.getApplicationContextPath();
        String rootPath = simpleServerFactory.getJerseyRootPath();

        rootPath = rootPath.substring(0, rootPath.indexOf("/*"));

        basePath = basePath.equals("/") ? rootPath : (new StringBuilder()).append(basePath).append(rootPath).toString();

        LOGGER.warn("getApplicationContextPath： " + basePath);
        config.setBasePath(basePath);
        config.setScan(true);
    }
    

   

}
