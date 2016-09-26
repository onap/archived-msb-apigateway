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
package org.openo.msb.wrapper.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openo.msb.api.MicroServiceFullInfo;
import org.openo.msb.api.MicroServiceInfo;
import org.openo.msb.api.Node;
import org.openo.msb.api.NodeInfo;
import org.openo.msb.api.Service;
import org.openo.msb.wrapper.serviceListener.IMicroServiceChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class MicroServiceDB {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicroServiceDB.class);

    private static MicroServiceDB instance = new MicroServiceDB();

    private List<IMicroServiceChangeListener> serviceListenerlist =
            new ArrayList<IMicroServiceChangeListener>();

    private MicroServiceDB() {}

    public static MicroServiceDB getInstance() {
        return instance;
    }

   
    public void addServiceChangeListener(IMicroServiceChangeListener listener) {
        synchronized (serviceListenerlist) {
            serviceListenerlist.add(listener);
        }
    }

   
    public void removeServiceChangeListener(IMicroServiceChangeListener listener) {
        synchronized (serviceListenerlist) {
            serviceListenerlist.remove(listener);
        }
    }
    
    
    public MicroServiceFullInfo[] getAllMicroServiceInstances() throws Exception {
        Jedis jedis = null;
        MicroServiceFullInfo[] microServiceList;
        try {
            jedis = JedisUtil.getJedis();

            String routekey =
                    MicroServiceUtil.getPrefixedKey("","*", MicroServiceUtil.SUFFIX_PATH_INFO);
            Set<String> serviceSet = jedis.keys(routekey);
            microServiceList = new MicroServiceFullInfo[serviceSet.size()];

            Pattern redisKeyPattern = MicroServiceUtil.getRedisKeyPattern();
            int i = 0;
            for (String servicePath : serviceSet) {
                Matcher matcher = redisKeyPattern.matcher(servicePath);
                if (matcher.matches()) {
                    microServiceList[i] = getMicroServiceByJedis(jedis, matcher.group("servicename"),matcher.group("version"), "");
                    i++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new Exception("call redis throw exception:"+e.getMessage());       
       } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        
        return microServiceList;
    }

    public void saveMicroServiceInfo2Redis(MicroServiceInfo microServiceInfo,String serverPort) throws Exception {
        // 1.1 set info
        String serviceInfokey =
                MicroServiceUtil.getServiceInfoKey(serverPort,microServiceInfo.getServiceName(),
                        microServiceInfo.getVersion());
        Map<String, String> serviceInfoMap = new HashMap<String, String>();
        serviceInfoMap.put("url", microServiceInfo.getUrl());
        serviceInfoMap.put("protocol", microServiceInfo.getProtocol());
        serviceInfoMap.put("visualRange",microServiceInfo.getVisualRange());
        serviceInfoMap.put("lb_policy",microServiceInfo.getLb_policy());
        serviceInfoMap.put("status", "0");
        
        

        // 1.2 set lb info
        String serviceLBkey =
                MicroServiceUtil.getPrefixedKey(serverPort,microServiceInfo.getServiceName(),
                        microServiceInfo.getVersion(), MicroServiceUtil.ROUTE_PATH_LOADBALANCE);


        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis();
            // 2.1 save info
            jedis.hmset(serviceInfokey, serviceInfoMap);


            for(Node node:microServiceInfo.getNodes()){
               
                String key=serviceLBkey+":"+node.getIp()+"-"+node.getPort();
                
                Map<String,String> nodeMap = new HashMap<String,String>();
               
                nodeMap.put("ip", node.getIp());
                nodeMap.put("port", node.getPort());
                nodeMap.put("ttl", Integer.toString(node.getTtl()));
                long expiration_time=System.currentTimeMillis()+node.getTtl()*1000;
                nodeMap.put("expiration", Long.toString(expiration_time));
                
                if(jedis.keys(key).isEmpty()){
                    nodeMap.put("created_at",  Long.toString(System.currentTimeMillis()));
                }
//                else{
//                    Map<String,String> nodeLBmap = jedis.hgetAll(key); 
//                    nodeMap.put("created_at", nodeLBmap.get("created_at"));
//                }
                nodeMap.put("updated_at", Long.toString(System.currentTimeMillis()));
                
                jedis.hmset(key, nodeMap);
            }
            
//            jedis.sadd(serviceLBkey, nodeArray);

        } catch (Exception e) {
            LOGGER.error("save to redis throw exception", e);
            throw new Exception("save to  redis throw exception:"+e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }



    }
    
    public void updateMicroServiceStatus(String serviceName, String version,String status) throws Exception{
        
        
        String serviceInfokey = MicroServiceUtil.getServiceInfoKey("",serviceName, version);
        Map<String, String> serviceInfoMap = new HashMap<String, String>();
        serviceInfoMap.put("status", status);


        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new Exception("fetch from jedis pool failed,null object!");
            }
            jedis.hmset(serviceInfokey, serviceInfoMap);
        }
        catch (Exception e) {
            LOGGER.error("update MicroService status throw exception", e);
            throw new Exception("update MicroService status throw exception:"+e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        
    }
    
    
    public void  updateMicroServiceNode2Redis(String serviceName, String version,String ip,String port,int ttl) throws Exception {
        String serviceLBkey =
                MicroServiceUtil.getPrefixedKey("",serviceName,version, MicroServiceUtil.ROUTE_PATH_LOADBALANCE);


        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis();
          
               
                String nodeKey=serviceLBkey+":"+ip+"-"+port;                
                Map<String,String> nodeLBmap = jedis.hgetAll(nodeKey);
                
                if(nodeLBmap.isEmpty()){
                    throw new NullPointerException(" MicroService Node not fond ");  
                }
                
               
                nodeLBmap.put("ttl", Integer.toString(ttl));
                long expiration_time=System.currentTimeMillis()+ttl*1000;
                nodeLBmap.put("expiration", Long.toString(expiration_time));
                nodeLBmap.put("updated_at", Long.toString(System.currentTimeMillis()));
                
                jedis.hmset(nodeKey, nodeLBmap);


        } 
        catch (NullPointerException e){
            throw e;  
        }
        catch (Exception e) {
            LOGGER.error("update MicroService Node throw exception", e);
            throw new Exception("update MicroService Node throw exception:"+e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
    }
    
    
    public void noticeUpdateApiListener(String serviceName,String version,Service microServiceInfo,String serverPort) {
        if (isNeedNotify(microServiceInfo)) {
            for (IMicroServiceChangeListener serviceListener : serviceListenerlist) {
                serviceListener.onChange(serviceName,version, microServiceInfo,serverPort);
            }
        }
        
    }
    
    public void noticeUpdateStatusListener(Service microServiceInfo,String status) {
       
        for (IMicroServiceChangeListener serviceListener : serviceListenerlist) {
            serviceListener.onStatusChange(microServiceInfo.getServiceName(),microServiceInfo.getUrl(),
                    microServiceInfo.getVersion(),microServiceInfo.getProtocol(),status);
        }
    }
    
 

    public void noticeApiListener(Service microServiceInfo, String type,String serverPort) {
        if (isNeedNotify(microServiceInfo)) {

            if ("ADD".equals(type)) {
                for (IMicroServiceChangeListener serviceListener : serviceListenerlist) {
                    serviceListener.onSave(microServiceInfo,serverPort);
                }
            } else if ("DELETE".equals(type)) {
                for (IMicroServiceChangeListener serviceListener : serviceListenerlist) {
                    serviceListener.onDelete(microServiceInfo.getServiceName(),microServiceInfo.getUrl(),
                            microServiceInfo.getVersion(),microServiceInfo.getProtocol(),serverPort);
                }
            } 

        }
    }


    public MicroServiceFullInfo getMicroServiceInstance(String serviceName, String version,String serverPort)
            throws Exception {
        if (null == version || "null".equals(version)) {
            version = "";
        }

        Jedis jedis = null;
        MicroServiceFullInfo microServiceInfo = null;
       
        try {
            jedis = JedisUtil.getJedis();
            
            microServiceInfo= getMicroServiceByJedis(jedis,serviceName,version, serverPort);

           
        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new Exception("call redis throw exception:"+e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        
        return microServiceInfo;
       
    }

    private MicroServiceFullInfo getMicroServiceByJedis(Jedis jedis,String serviceName, String version,String serverPort){
        MicroServiceFullInfo microServiceInfo = null;
        String serviceInfoKey = MicroServiceUtil.getServiceInfoKey(serverPort,serviceName, version);
        Map<String, String> infomap = jedis.hgetAll(serviceInfoKey);
        if (!infomap.isEmpty()) {
            microServiceInfo = new MicroServiceFullInfo();
            microServiceInfo.setServiceName(serviceName);
            microServiceInfo.setVersion(version);
            microServiceInfo.setUrl(infomap.get("url"));
            microServiceInfo.setProtocol(infomap.get("protocol"));
            microServiceInfo.setVisualRange(infomap.get("visualRange"));
            microServiceInfo.setStatus(infomap.get("status"));
            microServiceInfo.setLb_policy(infomap.get("lb_policy"));

            String nodeLBkey =
                    MicroServiceUtil.getPrefixedKey(serverPort,microServiceInfo.getServiceName(),
                            microServiceInfo.getVersion(),
                            MicroServiceUtil.ROUTE_PATH_LOADBALANCE);
            
            Set<String>  nodeKeys=jedis.keys(nodeLBkey+":*");
            
            Set<NodeInfo> nodes=new HashSet<NodeInfo>();
            for(String nodeKey:nodeKeys){
                Map<String,String> nodeLBmap = jedis.hgetAll(nodeKey);
                NodeInfo nodeInfo=new NodeInfo();
                nodeInfo.setNodeId(serviceName+"_"+nodeLBmap.get("ip")+"_"+nodeLBmap.get("port"));
                nodeInfo.setIp(nodeLBmap.get("ip"));
                nodeInfo.setPort(nodeLBmap.get("port"));
                nodeInfo.setTtl(Integer.parseInt(nodeLBmap.get("ttl")));
                nodeInfo.setCreated_at(new Date(Long.parseLong(nodeLBmap.get("created_at"))));
                nodeInfo.setUpdated_at(new Date(Long.parseLong(nodeLBmap.get("updated_at"))));
                nodeInfo.setExpiration(new Date(Long.parseLong(nodeLBmap.get("expiration"))));
              
                nodes.add(nodeInfo);
            }
            
            microServiceInfo.setNodes(nodes);
        }
            
           
           
            
            return microServiceInfo;
    }
        

    public void deleteMicroService(String serviceName, String version,String serverPort) throws Exception {
        if (null == version || "null".equals(version)) {
            version = "";
        }
        
        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis();
            String routekey = MicroServiceUtil.getPrefixedKey(serverPort,serviceName, version, "*");
            Set<String> infoSet = jedis.keys(routekey);

            String[] paths = new String[infoSet.size()];

            infoSet.toArray(paths);

            jedis.del(paths);
        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new Exception("call redis throw exception:"+e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
    }

    public void deleteNode(String serviceName, String version, String ip,String port) throws Exception {
        if (null == version || "null".equals(version)) {
            version = "";
        }
        
        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis();
            String serviceLBkey =
                    MicroServiceUtil.getPrefixedKey("",serviceName, version,
                            MicroServiceUtil.ROUTE_PATH_LOADBALANCE,ip+"-"+port);
            jedis.del(serviceLBkey);
        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new Exception("call redis throw exception:"+e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

    }


    /**
     * Determine whether the service needs to send a notification 
     * TODO: filter according to the agreement,
     * the only notice of agreement for REST \ UI interface MSB - REST     
     * @param protocol
     * @return
     */
    private boolean isNeedNotifyByProtocol(String protocol) {
        return "UI".equalsIgnoreCase(protocol) ||("REST".equalsIgnoreCase(protocol));
    }
    
    /**
     * Determine whether the service needs to send a notification 
     * TODO: according to the visual range filter conditions   
     * @param visualRange
     * @return
     */
    private boolean isNeedNotifyByVisualRange(String visualRange) {
        String[] rangeArray=StringUtils.split(visualRange, "|");
        return RouteUtil.contain(RouteUtil.visualRangeMatches, rangeArray);
    }

    /**
     * According to the MicroServiceInfo entity information to judge whether need to send a notification
     * @param microServiceInfo
     * @return
     */
    private boolean isNeedNotify(Service microServiceInfo) {
        if (null != microServiceInfo) {
            return isNeedNotifyByProtocol(microServiceInfo.getProtocol()) &&
                   isNeedNotifyByVisualRange(microServiceInfo.getVisualRange());
        } else {
            return false;
        }
    }
    
    
    
}
