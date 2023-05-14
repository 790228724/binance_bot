package com.example.demo.thirdpart.feixiaohao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.thirdpart.coingecko.domain.Coins.CoinFullData;
import com.example.demo.thirdpart.coingecko.domain.Coins.MarketData;
import com.example.demo.trade.TradeUtil;
import com.example.demo.thirdpart.coingecko.CoinGeckoApiClient;
import com.example.demo.thirdpart.coingecko.impl.CoinGeckoApiClientImpl;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * description: MyTokenApiClient <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/13 15:21 <br>
 */
public class FeiXiaoHaoApiClient {
    public static String coin(String coin,String newCoin){
        DecimalFormat df = new DecimalFormat("######0.00");
        NumberFormat nf = new DecimalFormat("$,###.####");
        DecimalFormat df2 = new DecimalFormat(",###");
        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();
        CoinFullData coinFullData = client.getCoinById(newCoin);
        MarketData marketData = coinFullData.getMarketData();
        StringBuilder sb = new StringBuilder();
        try {
                sb.append("************").append(coin).append("************\n");
                Double price = marketData.getCurrentPrice().get("usd");
                Double high  = marketData.getAth().get("usd");
                BigDecimal drop = BigDecimal.valueOf(price).divide(BigDecimal.valueOf(high),BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100));
                sb.append("当前价格: ").append(nf.format(price));
                sb.append(" 历史最高价: ").append(nf.format(high)).append("\n");
                sb.append("相对历史最高跌幅: ").append(df.format(drop)).append("%\n");
                try {
                    sb.append("市值: ").append(nf.format(marketData.getMarketCap().get("usd")));
                }catch (Exception e){
                    e.printStackTrace();
                }
                sb.append(" 排名: ").append(coinFullData.getMarketCapRank()).append("\n");
                sb.append("总量: ").append(df2.format(marketData.getMaxSupply()));
                try {
                    sb.append(" 流通量: ").append(df2.format(marketData.getCirculatingSupply())).append("\n");
                } catch (Exception e){
                    e.printStackTrace();
                }

                sb.append("24h: ").append(df.format(marketData.getPriceChangePercentage24h())).append("%\n");
                sb.append("7d: ").append(df.format(marketData.getPriceChangePercentage7d())).append("%\n");
                try {
                    sb.append(TradeUtil.emaStragy(coin, price));
                }catch (Exception e){
                    e.printStackTrace();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    public static String hot(){
        String uri="http://dncapi.bqrank.net/api/coin/hotcoin_search?webp=1&pagesize=100&page=1";
        List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
        try {
            StringBuilder sb = new StringBuilder();
            String result = makeGetAPICall(uri, paratmers);
            JSONArray array = JSONObject.parseObject(result).getJSONArray("data");
            for(int i= 0 ; i<array.size(); i++){
                if(i == 15){
                    break;
                }
                JSONObject jsonObject = array.getJSONObject(i);
                sb.append(jsonObject.get("name"))
                        .append(" $")
                        .append(jsonObject.get("current_price_usd"))
                        .append(" ")
                        .append(jsonObject.get("changerate_utc8"))
                        .append("% ");
                int start_level= (int) jsonObject.get("star_level");
                for(int j =0 ;j<start_level;j++){
                    sb.append("⭐");
                }
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
        return null;
    }
    public static String concept(int id){
        String uri = "http://dncapi.bqrank.net/api/concept/web-conceptdetail?webp=1&id="+id+"&page=1&pagesize=100";
        List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
        DecimalFormat df = new DecimalFormat("######0.00");
        try {
            StringBuilder sb = new StringBuilder();
            String result = makeGetAPICall(uri, paratmers);
            System.out.println(result);
            JSONObject object = JSONObject.parseObject(result);
            JSONObject detail = object.getJSONObject("detail");
            sb.append("牛币: ").append(detail.get("best")).append(" ").append(df.format(detail.get("best_percent"))).append("% ");
            sb.append("傻币: ").append(detail.get("worst")).append(" ").append(df.format(detail.get("worst_percent"))).append("%\n");
            JSONArray array = object.getJSONArray("data");
            for(int i= 0 ; i<array.size(); i++){
                if(i == 15){
                    break;
                }
                JSONObject jsonObject = array.getJSONObject(i);
                sb.append(jsonObject.get("symbol")).append("($").
                        append(df.format(jsonObject.get("current_price"))).
                        append(" ").
                        append(df.format(jsonObject.get("change_percent"))).append("%) ");
                if((i+1)%3 == 0){
                    sb.append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
        return null;
    }
    public static String makeGetAPICall(String uri, List<NameValuePair> parameters)
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
    public static String makePostAPICall(String uri, List<NameValuePair> parameters,String json)
            throws Exception {
        String response_content = "";
        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(query.build());
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("Content-type","application/json; charset=utf-8");
        request.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
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
    public static void main(String[] args){
        System.out.println(FeiXiaoHaoApiClient.coin("UNI","uniswap"));
    }
}
