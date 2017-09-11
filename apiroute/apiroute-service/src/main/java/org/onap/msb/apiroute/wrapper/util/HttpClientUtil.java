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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpMessage;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static int connectionTimeOut = 2 * 1000;

    public static HttpPost createHttpPost(String url, Object bean) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        setCommonHeader(httpPost);
        setStringEntity(httpPost, bean);
        return httpPost;
    }

    public static HttpGet createHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        setCommonHeader(httpGet);
        return httpGet;
    }

    public static HttpPut createHttpPut(String url, Object bean) throws Exception {
        HttpPut httpPut = new HttpPut(url);
        setCommonHeader(httpPut);
        setStringEntity(httpPut, bean);
        return httpPut;
    }

    public static HttpPut createHttpPut(String url) throws Exception {
        HttpPut httpPut = new HttpPut(url);
        setCommonHeader(httpPut);
        return httpPut;
    }

    private static void setCommonHeader(HttpMessage httpMessage) {
        httpMessage.addHeader("Content-type", "application/json; charset=utf-8");
        httpMessage.setHeader("Accept", "application/json");
    }

    private static void setStringEntity(HttpEntityEnclosingRequestBase httpMessage, Object bean) throws Exception {
        String entity = JacksonJsonUtil.beanToJson(bean);
        httpMessage.setEntity(new StringEntity(entity, Charset.forName("UTF-8")));
    }

    public static void closeHttpClient(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        closeHttpClient(httpClient);
        closeHttpResponse(response);
    }

    private static void closeHttpClient(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(httpClient + ":close  httpClient faild");
            }
        }
    }

    private static void closeHttpResponse(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                logger.error(response + ":close  response faild");
            }
        }
    }

    public static CloseableHttpResponse httpGetWithResponse(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Content-type", "application/json; charset=utf-8");
        httpGet.setHeader("Accept", "application/json");
        try {
            return httpClient.execute(httpGet);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(url + ":close  httpClient faild");
            }
        }
    }

    public static void delete(String url, String parameter) throws Exception {
        String result = null;
        String baseUrl;
        if (parameter != null) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("serviceName", parameter));
            baseUrl = url + "?" + URLEncodedUtils.format(params, "UTF-8");
        } else {
            baseUrl = url;
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();;
        try {

            HttpDelete httpDelete = new HttpDelete(baseUrl);
            CloseableHttpResponse res = httpClient.execute(httpDelete);

            if (res.getStatusLine().getStatusCode() != 200) {
                throw new Exception("delete fail");
            }

            res.close();
        } catch (IOException e) {
            String errorMsg = baseUrl + ":delete connect faild";
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                String errorMsg = baseUrl + ":close  httpClient faild";
            }
        }

    }


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
