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

import org.apache.commons.lang3.StringUtils;
import org.openo.msb.api.DiscoverInfo;


public class RouteUtil {

    public static String IUI_ROOT_PATH="iui";  
    
    public static  String API_ROOT_PATH="api"; 
    
	public static final String ROUTE_PATH="msb:routing";   
	
	public static final String APIROUTE="api";  
	
	public static final String IUIROUTE="iui";  
	
	public static final String CUSTOMROUTE="custom";  
	
	public static final String P2PROUTE="p2p";  
	
	
	public static final String ROUTE_PATH_INFO="info";  
	
	public static final String ROUTE_PATH_LOADBALANCE="lb"; 
	
	public static final String APIROUTE_PATH_LIFE="life";  
	

	public static final String REQUEST_SUCCESS = "SUCCESS";
	
	public static final String REQUEST_FAIL = "FAIL";
	
    public static String PROTOCOL_LIST="REST,UI,MQ,FTP,SNMP,TCP,UDP"; 
    
    public static DiscoverInfo discoverInfo=new DiscoverInfo();
    
    
    public static String[] visualRangeRange={"0","1"};
    
    public static String[] controlRangeMatches={"0","1","2"};
    
    public static String[] statusRangeMatches={"0","1"};
    
    public static String[] useOwnUpstreamRangeMatches={"0","1"};
    
    public static String[] visualRangeMatches={"1"};
	
	/** 
	* @Title: getPrefixedKey  
	* @Description: TODO(Add base path prefix radis assembly path) 
	* @param: @param serviceName
	* @param: @param version
	* @param: @param type
	* @param: @return      
	* @return: String    
	*/
	
	public static String getPrefixedKey(String...paths){
		StringBuffer sb= new StringBuffer();
		
		    if(paths[0].trim().equals("") || paths[0].equals(String.valueOf(JedisUtil.serverPort))){
	            sb.append(ROUTE_PATH);
	        }
	        else{
	           sb.append(paths[0]); 
	        }
	
		for (int i = 1; i < paths.length; i++) {
			sb.append(":");
			sb.append(paths[i]);
		}
		return sb.toString();
	}
	
	
	public static Object[] concat(Object[] a, Object[] b) {
	    Object[] c= new Object[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
     }
	
	public static boolean contain(String[] array,String str){
	    for(int i=0;i<array.length;i++){
	          if(array[i].equals(str)){
	             return true;  
	          }
	    }
	    return false;

	  }
	
	public static boolean contain(String[] array,String value[]){
        for(int i=0;i<array.length;i++){
            for(int n=0;n<value.length;n++){
              if(array[i].equals(value[n])){
                 return true;  
              }
            }
        }
        return false;

      }
	
	public static String show(String[] array){
	    
        return StringUtils.join(array, "|");

      }
	
	public static void main(String[] args) {
	    String array[]={"1","2"};
	    System.out.println(StringUtils.join(array, "|"));
    }
	

}
