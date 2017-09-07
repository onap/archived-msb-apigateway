/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.util;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static int connectionTimeOut = 2 * 1000;


    public static String httpGet(String url) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Content-type", "application/json; charset=utf-8");
        httpGet.setHeader("Accept", "application/json");
        try {
            CloseableHttpResponse res = httpClient.execute(httpGet);
            result = EntityUtils.toString(res.getEntity());
            if (res.getStatusLine().getStatusCode() != CommonUtil.SC_OK) {
                logger.error(result);
            }
            res.close();
        } catch (ClientProtocolException e) {
            logger.error(url + ":httpGetWithJSON connect faild");
        } catch (IOException e) {
            logger.error(url + ":httpGetWithJSON connect faild");
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(url + ":close  httpClient faild");
            }
        }

        return result;

    }

    public static HttpGetResult httpGetStatusAndBody(String url) {
        HttpGetResult result = new HttpGetResult();
        String body = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Content-type", "application/json; charset=utf-8");
        httpGet.setHeader("Accept", "application/json");

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeOut).build();
        httpGet.setConfig(requestConfig);

        try {
            CloseableHttpResponse res = httpClient.execute(httpGet);
            body = EntityUtils.toString(res.getEntity());
            if (res.getStatusLine().getStatusCode() != CommonUtil.SC_OK) {
                logger.error(body);
            }
            result.setBody(body);
            result.setStatusCode(res.getStatusLine().getStatusCode());
            res.close();
        } catch (ClientProtocolException e) {
            logger.error(url + ":httpGetWithJSON connect faild", e);
        } catch (IOException e) {
            logger.error(url + ":httpGetWithJSON connect faild", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(url + ":close  httpClient faild");
            }
        }

        return result;

    }

    public static int httpGetStatus(String url) throws Exception {
        int iStatus = 500;
        CloseableHttpClient httpClient = HttpClients.createDefault();


        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();// 设置请求和传输超时时间
        httpGet.setConfig(requestConfig);
        httpGet.addHeader("Content-type", "application/json; charset=utf-8");
        httpGet.setHeader("Accept", "application/json");
        try {
            CloseableHttpResponse res = httpClient.execute(httpGet);

            iStatus = res.getStatusLine().getStatusCode();
            res.close();
        } catch (ClientProtocolException e) {
            logger.error(url + " httpGet connect faild:" + e.getMessage());
        } catch (IOException e) {
            logger.error(url + " httpGet connect faild:" + e.getMessage());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(url + " httpGet close faild:" + e.getMessage());
            }
        }

        return iStatus;

    }
}
