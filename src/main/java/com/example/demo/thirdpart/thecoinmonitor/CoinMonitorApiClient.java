package com.example.demo.thirdpart.thecoinmonitor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: CoinMonitorApiClient <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/19 13:35 <br>
 */
public class CoinMonitorApiClient {
    private static Map<String,String> cache = new HashMap<>();
    public static String monitor() throws Exception {
        boolean flag = false;
        String url = "https://thecoinmonitor.com/api/alerts?page=1";
        String result = makeAPICall(url,new ArrayList<NameValuePair>());
        System.out.println(result);
        StringBuilder sb = new StringBuilder();
        JSONObject object = JSONObject.parseObject(result);
        JSONArray docs = object.getJSONArray("docs");
        sb.append("*********异动Monitor*******\\n");
        for(int i = 0 ; i<docs.size();i++){
            JSONObject doc =  docs.getJSONObject(i);
            String id = doc.getString("_id");
            String suffix = doc.getString("suffix");
            String exchange = doc.getString("exchange");
            if("BINANCE".equals(exchange)){
                if("USDT".equals(suffix) || "BUSD".equals(suffix)){
                    if(!cache.containsKey(id)){
                        flag = true;
                        String symbol = doc.getString("symbol");
                        sb.append(symbol).append("\\n");
                        cache.put(id,symbol);
                    }
                }
            }
        }
        if(flag){
            return sb.toString();
        }else{
            return null;
        }
    }
    public static String makeAPICall(String uri, List<NameValuePair> parameters)
            throws Exception {
        String response_content = "";
        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        CloseableHttpResponse response = client.execute(request);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return response_content;
    }
    public static void main(String[] args) throws Exception {
        System.out.println(monitor());
    }
}
