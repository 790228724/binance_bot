package com.example.demo.thirdpart.bitfinex;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: Bitfinex <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/16 10:49 <br>
 */
public class BitfinexApiClient {

    public static String shortVolume() throws Exception {
        String uri = "https://api-pub.bitfinex.com/v2/stats1/pos.size:1m:tBTCUSD:short/last";
        List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
        try {
            String result = makeAPICall(uri, paratmers);
            return result;
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
        return null;
    }
    public static String longVolume() throws Exception {
        String uri = "https://api-pub.bitfinex.com/v2/stats1/pos.size:1m:tBTCUSD:long/last";
        List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
        try {
            String result = makeAPICall(uri, paratmers);
            return result;
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
        return null;
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
        System.out.println(shortVolume());
        System.out.println(longVolume());
    }
}
