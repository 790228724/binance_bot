package com.example.demo.utils;


import com.example.demo.http.bean.BaseResponseObject;
import com.example.demo.http.exception.HttpUtilClosableException;
import com.google.gson.JsonSyntaxException;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.*;

/**
 * description: HttpUtil <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/9 13:04 <br>
 */

public class HttpUtil {

    /**
     * http请求方式
     */
    public enum Https {
        /**
         * post
         */
        POST,

        /**
         * get
         */
        GET;
    }

    /**
     * 编码格式 发送编码格式统一用 utf-8
     */
    private static final String CHARSET_UTF8 = "UTF-8";

    /**
     * 设置连接超时时间 单位 毫秒(ms)
     */
    private static final int CONNECT_TIMEOUT = 3000;

    /**
     * 设置读取超时时间 单位 毫秒(ms)
     */
    private static final int SOCKET_TIMEOUT = 10000;

    /**
     * 设置请求时间 单位 毫秒(ms)
     */
    private static final int CONNECT_REQUEST_TIMEOUT = 3000;

    /**
     * 最大连接数
     */
    private static final int MAX_TOTAL = 128;

    /**
     * 单路由最大连接数
     */
    private static final int MAX_PER_ROUTE = 128;

    /**
     * 单列模式
     */
    private volatile static HttpUtil instance = null;

    /**
     * 单列模式
     */
    private static HttpUtil getInstance() {
        if (instance == null) {
            synchronized (HttpUtil.class) {
                if (instance == null) {
                    instance = new HttpUtil();
                }
            }
        }
        return instance;
    }

    public HttpUtil() {
        this.httpClient = HttpClients.custom()
                .setConnectionManager(getConnectionManager())
                .setDefaultRequestConfig(getRequestConfig())
                .setRetryHandler(getHandle())
                .evictExpiredConnections()
                .build();
    }

    /**
     * httpclient
     */
    private volatile CloseableHttpClient httpClient;

    /**
     * httpclient
     */
    private volatile CloseableHttpClient httpClientWithSSL;

    /**
     * 当前使用的证书
     */
    private volatile SSLContext currentSSLContext;

    /**
     * 设置httpclient （带证书）
     *
     * @param sslContext SSL证书
     * @return
     */
    private CloseableHttpClient getHttpClient(SSLContext sslContext) {
        if (currentSSLContext != sslContext) {
            synchronized (this) {
                if (currentSSLContext != sslContext) {
                    try {
                        if (this.httpClientWithSSL != null) {
                            this.httpClientWithSSL.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        this.httpClientWithSSL = HttpClients.custom()
                                .setSSLContext(sslContext)
                                .setConnectionManager(getConnectionManager())
                                .setDefaultRequestConfig(getRequestConfig())
                                .evictExpiredConnections()
                                .build();
                    }
                    this.currentSSLContext = sslContext;
                }
            }
        }
        return this.httpClientWithSSL;
    }

    /**
     * 请求器的配置
     *
     * @return
     */
    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT)
                .build();
    }

    /**
     * httpclient连接池的配置
     *
     * @return
     */
    private static PoolingHttpClientConnectionManager getConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        //最大连接数
        connectionManager.setMaxTotal(MAX_TOTAL);
        //路由链接数
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        return connectionManager;
    }

    /**
     * 请求失败时,进行请求重试
     *
     * @return
     */
    private static HttpRequestRetryHandler getHandle() {
        return (e, i, httpContext) -> {
            if (i > 3) {
                //重试超过3次,放弃请求
                System.out.println("retry has more than 3 time, give up request");
                return false;
            }
            if (e instanceof ConnectTimeoutException) {
                // 连接超时
                return true;
            }
            if (e instanceof NoHttpResponseException) {
                //服务器没有响应,可能是服务器断开了连接,应该重试
                return true;
            }
            if (e instanceof UnknownHostException) {
                // 服务器不可达
                return true;
            }
            if (e instanceof SSLException) {
                return true;
            }

            HttpClientContext context = HttpClientContext.adapt(httpContext);
            HttpRequest request = context.getRequest();
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                //如果请求不是关闭连接的请求
                return true;
            }
            return false;
        };
    }

    /**
     * 创建访问的地址 拼接url
     *
     * @param url    基础url
     * @param params 请求参数
     * @return URI
     */
    private static URI getUrl(String url, Map<String, Object> params, Charset charset) throws HttpUtilClosableException {
        // 创建访问的地址
        try {
            URIBuilder uriBuilder = new URIBuilder(url).setCharset(charset);
            if (params != null) {
                Set<Map.Entry<String, Object>> entrySet = params.entrySet();
                for (Map.Entry<String, Object> entry : entrySet) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null) {
                        uriBuilder.setParameter(key, String.valueOf(value));
                    }
                }
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new HttpUtilClosableException(e.getMessage(), e);
        }
    }

    /**
     * 设置请求头
     *
     * @param httpRequest http请求
     * @param headers     请求头参数
     */
    private static HttpRequestBase setHeader(HttpRequestBase httpRequest, Map<String, String> headers) {
        // 设置请求头
        if (headers != null) {
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置请求头到 HttpRequestBase
                httpRequest.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpRequest;
    }

    /**
     * get请求方法 带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param params  请求参数
     * @return HttpGet
     */
    private static HttpGet doGet(String url, Map<String, String> headers, Map<String, Object> params, Charset charset) throws HttpUtilClosableException {
        // 创建访问的地址
        URI uri = getUrl(url, params, charset);
        // 创建http对象
        HttpGet httpGet = new HttpGet(uri);
        // 设置请求头
        return (HttpGet) setHeader(httpGet, headers);
    }

    /**
     * post请求 (key-value格式)
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param params  请求参数
     * @return HttpPost
     */
    private static HttpPost doPost(String url, Map<String, String> headers, Map<String, Object> params, Object jsonObject, String contentType, Charset charset) throws HttpUtilClosableException {
        // 创建httpPost
        HttpPost httpPost;
        if (jsonObject != null && params != null) {
            // 创建访问的地址
            URI uri = getUrl(url, params, charset);
            httpPost = new HttpPost(uri);
        } else {
            httpPost = new HttpPost(url);
        }
        // 设置请求参数
        if (jsonObject != null) {
            String json;
            if (jsonObject instanceof String) {
                json = (String) jsonObject;
            } else {
                json = GsonUtil.gsonString(jsonObject);
            }
            // 请求头
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
            // 设置参数
            StringEntity entity = new StringEntity(json, charset);
            if (contentType != null) {
                entity.setContentType(contentType);
            }
            httpPost.setEntity(entity);
        } else if (params != null) {
            // 设置参数
            List<NameValuePair> nvp = new ArrayList<>();
            Set<Map.Entry<String, Object>> entrySet = params.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    nvp.add(new BasicNameValuePair(key, String.valueOf(value)));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvp, charset));
        }
        return (HttpPost) setHeader(httpPost, headers);
    }

    /**
     * 获取SSL上下文对象,用来构建SSL Socket连接
     *
     * @param certPath SSL文件
     * @param certPass SSL密码
     * @return SSL上下文对象
     */
    public static SSLContext getSSLContext(String certPath, String certPass) throws HttpUtilClosableException {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // 证书文件流
            File file = new File(certPath);
            if (!file.exists()) {
                throw new HttpUtilClosableException("证书不存在");
            }
            InputStream input = new FileInputStream(file);
            keyStore.load(input, certPass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            return SSLContexts.custom()
                    //忽略掉对服务器端证书的校验
                    //.loadTrustMaterial((TrustStrategy) (chain, authType) -> true)
                    //加载服务端提供的truststore(如果服务器提供truststore的话就不用忽略对服务器端证书的校验了)
                    .loadKeyMaterial(keyStore, certPass.toCharArray()).build();
        } catch (Exception e) {
            throw new HttpUtilClosableException(e.getMessage(), e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 资源关闭
     */
    public static void close() {
        try {
            if (HttpUtil.getInstance().httpClient != null) {
                HttpUtil.getInstance().httpClient.close();
            }
            if (HttpUtil.getInstance().httpClientWithSSL != null) {
                HttpUtil.getInstance().httpClientWithSSL.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造http请求函数
     */
    public static class Builder {

        /**
         * 请求参数(key-value格式)
         */
        private Map<String, Object> params;

        /**
         * 参数编码格式
         */
        private Charset charset = Charset.forName(CHARSET_UTF8);

        /**
         * 请求参数(json格式)
         */
        private Object jsonObject;

        /**
         * 请求头
         */
        private Map<String, String> headers;

        /**
         * 参数返回格式
         */
        private String contentType;

        /**
         * http请求
         */
        private HttpUriRequest httpRequest;

        /**
         * SSL证书
         */
        private SSLContext sslContext;

        /**
         * http响应
         */
        private CloseableHttpResponse httpResponse;

        /**
         * httpUtil工具类实例化
         */
        private HttpUtil httpUtil;

        /**
         * 初始化
         */
        private Builder() {
            this.httpUtil = HttpUtil.getInstance();
        }


        /**
         * 添加参数
         *
         * @param key
         * @param value
         * @return
         */
        public Builder addParams(String key, Object value) {
            if (this.params == null) {
                this.params = new HashMap<>(5);
            }
            this.params.put(key, value);
            return this;
        }

        /**
         * 添加参数
         *
         * @param params
         * @return
         */
        public Builder addParams(Map<String, Object> params) {
            if (this.params == null) {
                this.params = new HashMap<>(5);
            }
            this.params.putAll(params);
            return this;
        }

        /**
         * 设置参数编码格式
         *
         * @param charset
         * @return
         */
        public Builder setCharset(String charset) {
            this.charset = Charset.forName(charset);
            return this;
        }

        /**
         * 添加参数
         */
        public Builder ajaxJson(Object object) {
            this.jsonObject = object;
            return this;
        }

        /**
         * 添加请求头
         */
        public Builder addHeaders(String key, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>(5);
            }
            this.headers.put(key, value);
            return this;
        }

        /**
         * 设置返回格式
         */
        public Builder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * 设置证书
         *
         * @param sslContext 证书
         * @return
         */
        public Builder setSSLContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        /**
         * MD5签名 (目前不支持json格式)
         *
         * @param secretKey 签名秘钥
         * @param keys      需要签名的参数
         * @return
         */
        public Builder signWithMD5(String secretKey, String... keys) {
            if (keys == null || keys.length == 0) {
                String sign = SignUtil.createSign(this.params, secretKey);
                this.params.put("sign", sign);
            } else {
                Map<String, Object> signMap = new HashMap<>(5);
                for (String key : keys) {
                    Object value = this.params.get(key);
                    if (value != null) {
                        signMap.put(key, value);
                    }
                }
                String sign = SignUtil.createSign(signMap, secretKey);
                this.params.put("sign", sign);
            }
            return this;
        }

        /**
         * http请求
         *
         * @param url   请求地址
         * @param https 请求方法
         * @return
         */
        public Builder doHttp(String url, Https https) {
            switch (https) {
                case POST:
                    return doPost(url);
                case GET:
                    return doGet(url);
                default:
                    return this;
            }
        }

        /**
         * get请求方法 带请求头和请求参数
         *
         * @param url 请求地址
         * @return HttpGet
         */
        public Builder doGet(String url) {
            try {
                this.httpRequest = HttpUtil.doGet(url, this.headers, this.params, this.charset);
            } catch (HttpUtilClosableException e) {
                e.printStackTrace();
            }
            return execute();
        }

        /**
         * post请求 (key-value格式)
         *
         * @param url 请求地址
         * @return HttpPost
         */
        public Builder doPost(String url) {
            try {
                this.httpRequest = HttpUtil.doPost(url, this.headers, this.params, this.jsonObject, this.contentType, this.charset);
            } catch (HttpUtilClosableException e) {
                e.printStackTrace();
            }
            return execute();
        }

        /**
         * http请求
         *
         * @return
         */
        private Builder execute() {
            if (this.httpRequest != null) {
                try {
                    //请求client
                    CloseableHttpClient httpClient;
                    if (this.sslContext != null) {
                        httpClient = this.httpUtil.getHttpClient(this.sslContext);
                    } else {
                        httpClient = this.httpUtil.httpClient;
                    }
                    this.httpResponse = httpClient.execute(this.httpRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return callback();
        }

        /**
         * 响应参数校验
         *
         * @return
         */
        private Builder callback() {
            if (this.httpResponse != null) {
                //响应状态
                StatusLine status = this.httpResponse.getStatusLine();
                if (status.getStatusCode() != HttpStatus.SC_OK) {
                    try {
                        throw new HttpUtilClosableException(status.getReasonPhrase());
                    } catch (HttpUtilClosableException e) {
                        e.printStackTrace();
                    } finally {
                        this.httpResponse = null;
                    }
                }
            }
            return this;
        }

        /**
         * 获取请求连接
         *
         * @return
         */
        public String getUrl() {
            if (this.httpResponse != null) {
                return this.httpRequest.getURI().toString();
            }
            return null;
        }

        /**
         * 获取响应头
         *
         * @param map
         * @param keys
         * @return
         */
        public Builder getHeaders(Map<String, String> map, String... keys) {
            if (this.httpResponse != null) {
                for (String key : keys) {
                    Header[] headers = this.httpResponse.getHeaders(key);
                    for (Header header : headers) {
                        map.put(header.getName(), header.getValue());
                    }
                }
            }
            return this;
        }

        /**
         * 获取响应头
         *
         * @param map
         * @return
         */
        public Builder getHeaders(Map<String, String> map) {
            if (this.httpResponse != null) {
                Header[] headers = this.httpResponse.getAllHeaders();
                for (Header header : headers) {
                    map.put(header.getName(), header.getValue());
                }
            }
            return this;
        }

        /**
         * 返回数据 byte
         *
         * @return
         */
        public byte[] toByte() {
            byte[] bytes = null;
            if (this.httpResponse != null) {
                try {
                    bytes = EntityUtils.toByteArray(this.httpResponse.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        this.httpResponse.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return bytes;
        }

        /**
         * 返回数据 InputStream
         *
         * @return
         */
        public InputStream toInput() {
            InputStream input = null;
            if (this.httpResponse != null) {
                try {
                    byte[] bytes = EntityUtils.toByteArray(this.httpResponse.getEntity());
                    //使用InputStream对象时，再从bytes转化回来
                    input = new ByteArrayInputStream(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        this.httpResponse.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return input;
        }

        /**
         * 返回数据 String
         *
         * @return
         */
        public String toJson() {
            return toJson(CHARSET_UTF8);
        }

        /**
         * 返回数据 String
         *
         * @param defaultCharset
         * @return
         */
        public String toJson(String defaultCharset) {
            String json = null;
            if (this.httpResponse != null) {
                try {
                    json = EntityUtils.toString(this.httpResponse.getEntity(), defaultCharset);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        this.httpResponse.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return json;
        }

        /**
         * 返回参数 map格式
         *
         * @return
         */
        public <T> Map<String, T> toMap() {
            Map<String, T> map = null;
            if (this.httpResponse != null) {
                try {
                    map = GsonUtil.gsonToMaps(this.toJson());
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
            return map;
        }

        /**
         * 返回参数 clazz
         *
         * @return
         */
        public <T> T toObject(Class<T> clazz) {
            T t = null;
            if (this.httpResponse != null) {
                try {
                    t = GsonUtil.gsonToBean(this.toJson(), clazz);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
            return t;
        }

        /**
         * 返回参数 list
         *
         * @return
         */
        public <T> List<T> toArray(Class<T> clazz) {
            List<T> array = null;
            if (this.httpResponse != null) {
                try {
                    array = GsonUtil.gsonToArray(toJson(), clazz);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
            return array;
        }

        /**
         * 返回参数 response
         *
         * @return
         */
        public <S, T extends BaseResponseObject> T toResponseObject(Class<? extends BaseResponseObject> clazz, Class<S> cls) {
            T t = null;
            if (this.httpResponse != null) {
                try {
                    t = GsonUtil.gsonToResponseObject(this.toJson(), clazz, cls);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
            return t;
        }

        /**
         * 返回参数 response array
         *
         * @return
         */
        public <S, T extends BaseResponseObject<List<S>>> T toResponseArray(Class<? extends BaseResponseObject> clazz, Class<S> cls) {
            T t = null;
            if (this.httpResponse != null) {
                try {
                    t = GsonUtil.gsonToResponseArray(this.toJson(), clazz, cls);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
            return t;
        }

        /**
         * 释放资源
         */
        public void close() {
            if (this.httpResponse != null) {
                try {
                    this.httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}