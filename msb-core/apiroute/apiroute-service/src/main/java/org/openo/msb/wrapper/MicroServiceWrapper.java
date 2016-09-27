/**
 * Copyright 2016 ZTE Corporation.
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
package org.openo.msb.wrapper;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openo.msb.api.MicroServiceFullInfo;
import org.openo.msb.api.MicroServiceInfo;
import org.openo.msb.api.Node;
import org.openo.msb.api.NodeInfo;
import org.openo.msb.api.exception.ExtendedInternalServerErrorException;
import org.openo.msb.api.exception.ExtendedNotFoundException;
import org.openo.msb.api.exception.ExtendedNotSupportedException;
import org.openo.msb.wrapper.util.MicroServiceDB;
import org.openo.msb.wrapper.util.RegExpTestUtil;
import org.openo.msb.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicroServiceWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicroServiceWrapper.class);

    private static MicroServiceWrapper instance = new MicroServiceWrapper();


    private MicroServiceWrapper() {}

    public static MicroServiceWrapper getInstance() {
        return instance;
    }


    /**
     * @Title: getAllMicroServiceInstances
     * @Description: getAllMicroServiceInstances
     * @param: @return
     * @return: Response
     * @throws Exception
     */
    public MicroServiceFullInfo[] getAllMicroServiceInstances(){

        try {
            return MicroServiceDB.getInstance().getAllMicroServiceInstances();

        } catch (Exception e) {
            throw new ExtendedInternalServerErrorException(e.getMessage());
        }

    }

    /**
     * @Title: getMicroServiceInstance
     * @Description: (getMicroServiceInstance)
     * @param: @param serviceName
     * @param: @param version
     * @param: @return
     * @return: ApiRouteInfo
     */
    public MicroServiceFullInfo getMicroServiceInstance(String serviceName, String version,String serverPort) {
        if("null".equals(version)) {
            version="";
        }
        serviceName=serviceName.replace("*", "/");
        
        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException("version (" + version
                        + ") is not a valid  format");
            }
        }

        MicroServiceFullInfo microServiceInfo;
        try {
            microServiceInfo =
                    MicroServiceDB.getInstance().getMicroServiceInstance(serviceName, version,serverPort);

        } catch (Exception e) {
            throw new ExtendedInternalServerErrorException(e.getMessage());
        }

        if (null == microServiceInfo) {
            String errInfo =
                    "microservice not found: serviceName-" + serviceName + ",version-" + version;
            LOGGER.warn(errInfo);
            throw new ExtendedNotFoundException(errInfo);

        }

        return microServiceInfo;
    }



    /**
     * @Title: updateMicroServiceInstance
     * @Description: updateMicroServiceInstance
     * @param: serviceName
     * @param: version
     * @param: microServiceInfo
     * @return: RouteResult
     */
    public synchronized MicroServiceFullInfo updateMicroServiceInstance(String serviceName,
            String version, MicroServiceInfo microServiceInfo) {
        if("null".equals(version)) {
            version="";
        }
        serviceName=serviceName.replace("*", "/");

        try {
       
            
            MicroServiceFullInfo oldService= getMicroServiceInstance(serviceName,version,"");

            // Delete the original record
            MicroServiceDB.getInstance().deleteMicroService(serviceName, version,"");
            // Notify the listeners
            MicroServiceDB.getInstance().noticeApiListener(oldService, "DELETE","");
            // Save the new record
            MicroServiceDB.getInstance().saveMicroServiceInfo2Redis(microServiceInfo,"");
          
            MicroServiceDB.getInstance().noticeApiListener(microServiceInfo, "ADD","");
            MicroServiceFullInfo newMicroServiceInfo =
                    MicroServiceDB.getInstance().getMicroServiceInstance(
                            microServiceInfo.getServiceName(), microServiceInfo.getVersion(),"");
            return newMicroServiceInfo;
        } catch (Exception e) {
            LOGGER.error("update MicroService throw exception", e);
            throw new ExtendedInternalServerErrorException(e.getMessage());
        }


    }

    public synchronized MicroServiceFullInfo updateMicroServiceNode(String serviceName,
            String version, String ip,String port, int ttl) {
        if("null".equals(version)) {
            version="";
        }
        serviceName=serviceName.replace("*", "/");
        
        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException(
                    "update MicroService Node FAIL:serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException(
                        "update MicroService Node FAIL:version is not a valid  format");
            }
        }

        if (!RegExpTestUtil.ipRegExpTest(ip)) {
            throw new ExtendedNotSupportedException("update MicroService Node FAIL:ip(" + ip
                    + ")is not a valid IP address");
        }
        
        if (!RegExpTestUtil.portRegExpTest(port)) {
            throw new ExtendedNotSupportedException("update MicroService Node FAIL:port(" + port
                    + ")is not a valid Port address");
        }
        
        try {

            MicroServiceDB.getInstance().updateMicroServiceNode2Redis(serviceName, version, ip,port,ttl);

            MicroServiceFullInfo newMicroServiceInfo =
                    MicroServiceDB.getInstance().getMicroServiceInstance(serviceName, version,"");

            return newMicroServiceInfo;
        } catch (NullPointerException e) {
            throw new ExtendedNotFoundException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("update MicroServiceNode throw exception", e);
            throw new ExtendedInternalServerErrorException(e.getMessage());
        }
    }

    /**
     * @Title updateMicroServiceStatus
     * @Description updateMicroServiceStatus
     * @param serviceName
     * @param version
     * @param status
     * @return
     * @return RouteResult
     */
 
    public synchronized MicroServiceFullInfo updateMicroServiceStatus(String serviceName, String version,
            String status) {

        if ("null".equals(version)) {
            version = "";
        }
        serviceName=serviceName.replace("*", "/");

        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException(
                    "update MicroService status FAIL:serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException(
                        "update MicroService status FAIL:version is not a valid  format");
            }
        }
        
        if(!"0".equals(status) && !"2".equals(status) && !"1".equals(status)){

            throw new ExtendedNotSupportedException("update MicroService status FAIL:status is wrong");
        }
        
        
        try {

            MicroServiceDB.getInstance().updateMicroServiceStatus(serviceName, version, status);

            MicroServiceFullInfo newMicroServiceInfo =
                    MicroServiceDB.getInstance().getMicroServiceInstance(serviceName, version,"");
            
            // Notify the listeners
            MicroServiceDB.getInstance().noticeUpdateStatusListener(newMicroServiceInfo, status);


            return newMicroServiceInfo;
        } catch (NullPointerException e) {
            throw new ExtendedNotFoundException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("update MicroServiceNode throw exception", e);
            throw new ExtendedInternalServerErrorException(e.getMessage());
        }
 

    }


    public synchronized MicroServiceFullInfo saveMicroServiceInstance(
            MicroServiceInfo microServiceInfo, boolean createOrUpdate,String requestIP,String serverPort) {

        if (StringUtils.isBlank(microServiceInfo.getServiceName())
                || StringUtils.isBlank(microServiceInfo.getProtocol())
                || microServiceInfo.getNodes().size() == 0) {
            throw new ExtendedNotSupportedException(
                    "register MicroServiceInfo FAIL: Some required fields are empty");
        }

        for (Node node : microServiceInfo.getNodes()) {
         
            if(node.getIp()==null || node.getIp().isEmpty()){
                node.setIp(requestIP);
            }
            else if (!RegExpTestUtil.ipRegExpTest(node.getIp())) {
                throw new ExtendedNotSupportedException("register MicroServiceInfo FAIL:IP("
                        + node.getIp() + ")is not a valid ip address");
            }
            
            if (!RegExpTestUtil.portRegExpTest(node.getPort())) {
                throw new ExtendedNotSupportedException("register MicroServiceInfo FAIL:Port("
                        + node.getPort() + ")is not a valid Port address");
            }
        }

        if (StringUtils.isNotBlank(microServiceInfo.getVersion())) {
            if (!RegExpTestUtil.versionRegExpTest(microServiceInfo.getVersion())) {
                throw new ExtendedNotSupportedException(
                        "register MicroServiceInfo FAIL:version is not a valid  format");

            }
        }

        if (StringUtils.isNotBlank(microServiceInfo.getUrl().trim())) {
            if (!RegExpTestUtil.urlRegExpTest(microServiceInfo.getUrl())) {
                throw new ExtendedNotSupportedException(
                        "register MicroServiceInfo FAIL:url is not a valid format(url must be begin with /)");

            }
        }


        if (RouteUtil.PROTOCOL_LIST.indexOf(microServiceInfo.getProtocol().trim()) == -1) {
            throw new ExtendedNotSupportedException(
                    "register MicroServiceInfo FAIL:Protocol is wrong,value range:("
                            + RouteUtil.PROTOCOL_LIST + ")");
        }

        MicroServiceFullInfo existingMicroServiceInfo;
        try {
            //To determine whether a service already exists
            existingMicroServiceInfo =
                    MicroServiceDB.getInstance().getMicroServiceInstance(
                            microServiceInfo.getServiceName().trim(), microServiceInfo.getVersion().trim(),serverPort);

            MicroServiceFullInfo newMicroServiceInfo ;
            if (existingMicroServiceInfo != null) {
                //a service already exists

                if (!existingMicroServiceInfo.getProtocol().equals(microServiceInfo.getProtocol())) {
                    throw new ExtendedNotSupportedException(
                            "MicroServiceInfo with different protocols and same serviceName is already existing");
                }

                if (createOrUpdate == false) {
                    //After the first remove added
                    MicroServiceDB.getInstance().deleteMicroService(
                            microServiceInfo.getServiceName(), microServiceInfo.getVersion(),serverPort);

                    MicroServiceDB.getInstance().saveMicroServiceInfo2Redis(microServiceInfo,serverPort);
         
                } else {
                    //Add the original record and save directly
                    MicroServiceDB.getInstance().saveMicroServiceInfo2Redis(microServiceInfo,serverPort);
                }
                
                newMicroServiceInfo =
                        MicroServiceDB.getInstance().getMicroServiceInstance(
                                microServiceInfo.getServiceName(), microServiceInfo.getVersion(),serverPort);

                //Notify the listeners
                MicroServiceDB.getInstance().noticeUpdateApiListener(microServiceInfo.getServiceName(),microServiceInfo.getVersion(),newMicroServiceInfo,serverPort);

            } else {
                //Save the new record
                MicroServiceDB.getInstance().saveMicroServiceInfo2Redis(microServiceInfo,serverPort);
                //Notify the listeners
                MicroServiceDB.getInstance().noticeApiListener(microServiceInfo, "ADD",serverPort);
                newMicroServiceInfo =
                        MicroServiceDB.getInstance().getMicroServiceInstance(
                                microServiceInfo.getServiceName(), microServiceInfo.getVersion(),serverPort);
            }

  

            return newMicroServiceInfo;

        } catch (ExtendedNotSupportedException e) {
            throw e;
        } catch (Exception e) {
            throw new ExtendedInternalServerErrorException(e.getMessage());
        }

    }
    
    
    public synchronized void deleteMicroService(String serviceName, String version) {
        if("null".equals(version)) {
            version="";
        }
        serviceName=serviceName.replace("*", "/");
        
        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException(
                    "delete MicroServiceInfo FAIL：serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException(
                        "delete MicroServiceInfo FAIL:version is not a valid  format");

            }
        }

        try {

            MicroServiceFullInfo microServiceInfo =
                    MicroServiceDB.getInstance().getMicroServiceInstance(serviceName, version,"");

            if (microServiceInfo == null) {
                LOGGER.warn("serviceName-"+ serviceName + ",version-" + version + " not fond ");
                return;
            }

            MicroServiceDB.getInstance().deleteMicroService(serviceName, version,"");
            //Notify the listeners
            MicroServiceDB.getInstance().noticeApiListener(microServiceInfo, "DELETE","");
        
        } catch (Exception e) {
            LOGGER.error("delete MicroServiceInfo throw exception", e);
            throw new ExtendedInternalServerErrorException(e.getMessage());

        }
        
        LOGGER.info("delete MicroServiceInfo success:serviceName-"
                + serviceName + ",version-" + version );

    }


    public synchronized void deleteMicroService(String serviceName, String version,String serverPort) {
        if("null".equals(version)) {
            version="";
        }
        serviceName=serviceName.replace("*", "/");
        
        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException(
                    "delete MicroServiceInfo FAIL：serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException(
                        "delete MicroServiceInfo FAIL:version is not a valid  format");

            }
        }

        try {

            MicroServiceFullInfo microServiceInfo =
                    MicroServiceDB.getInstance().getMicroServiceInstance(serviceName, version,serverPort);

            if (microServiceInfo == null) {
                throw new ExtendedNotFoundException("delete MicroServiceInfo FAIL:serviceName-"
                        + serviceName + ",version-" + version + " not fond ");
            }

            MicroServiceDB.getInstance().deleteMicroService(serviceName, version,serverPort);
            //Notify the listeners
            MicroServiceDB.getInstance().noticeApiListener(microServiceInfo, "DELETE",serverPort);
        } catch (ExtendedNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("delete MicroServiceInfo throw exception", e);
            throw new ExtendedInternalServerErrorException(e.getMessage());

        }
        
        LOGGER.info("delete MicroServiceInfo success:serviceName-"
                + serviceName + ",version-" + version );

    }

    public synchronized void deleteMicroServiceInstance(String serviceName, String version,
            String ip,String port) {
        if("null".equals(version)) {
            version="";
        }
        serviceName=serviceName.replace("*", "/");
        
        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException(
                    "delete MicroServiceInfo FAIL:serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException(
                        "delete MicroServiceInfo FAIL:version is not a valid  format");
            }
        }

        if (!RegExpTestUtil.ipRegExpTest(ip)) {
            throw new ExtendedNotSupportedException("delete MicroServiceInfo FAIL:IP(" + ip
                    + ")is not a valid IP address");
        }
        
        if (!RegExpTestUtil.portRegExpTest(port)) {
            throw new ExtendedNotSupportedException("delete MicroServiceInfo FAIL:Port(" + port
                    + ")is not a valid Port address");
        }


        try {
            MicroServiceFullInfo microServiceInfo =
                    MicroServiceDB.getInstance().getMicroServiceInstance(serviceName, version,"");

            if (microServiceInfo == null) {
                throw new ExtendedNotFoundException("delete MicroServiceInfo FAIL:serviceName-"
                        + serviceName + ",version-" + version + " not fond ");
            }

            Set<NodeInfo> nodes = microServiceInfo.getNodes();

            boolean ifFindBNode = false;

            for (Node node : nodes) {
                if (node.getIp().equals(ip) && node.getPort().equals(port)) {
                    ifFindBNode = true;
                    nodes.remove(node);

                    if (nodes.isEmpty()) {
                        //delete MicroService
                        MicroServiceDB.getInstance().deleteMicroService(serviceName, version,"");
                        //Notify the listeners
                        MicroServiceDB.getInstance().noticeApiListener(microServiceInfo, "DELETE","");
                    } else {
                        //delete Node
                        MicroServiceDB.getInstance().deleteNode(serviceName, version, ip,port);
                        MicroServiceDB.getInstance().noticeUpdateApiListener(serviceName, version,microServiceInfo,"");
                    }

                    break;
                }
            }

            if (!ifFindBNode) {
                throw new ExtendedNotFoundException("delete MicroServiceInfo FAIL: node-" + ip+":"+port
                        + " not fond ");
            }


        } catch (ExtendedNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("deleteApiRoute throw exception", e);
            throw new ExtendedInternalServerErrorException(e.getMessage());

        }

    }


}
