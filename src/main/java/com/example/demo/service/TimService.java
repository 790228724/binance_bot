package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import com.example.demo.cache.CoinCache;
import com.example.demo.thirdpart.feixiaohao.FeiXiaoHaoApiClient;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * description: TimSerivce <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/12 17:34 <br>
 */
@Service
@Slf4j
public class TimService {
    private static Map<String,String> mapping = new HashMap<>();


    public static String topic(String topic){
        boolean flag = false;
        StringBuilder sb  = new StringBuilder();
        if(topic.toUpperCase().equals("HOT")){
            flag =true;
            sb.append("*************HOT COIN************\n");
            String hotCoins = FeiXiaoHaoApiClient.hot();
            sb.append(hotCoins).append("\n");
            sb.append("***********************************\n");
        }
        if(topic.toUpperCase().equals("SBF")){
            flag =true;
            sb.append("*************SBF COIN************\n");
            String hotCoins = FeiXiaoHaoApiClient.concept(55);
            sb.append(hotCoins).append("\n");
            sb.append("***********************************\n");
        }
        if(topic.toUpperCase().equals("BSC")){
            flag =true;
            sb.append("*************BSC COIN************\n");
            String hotCoins = FeiXiaoHaoApiClient.concept(53);
            sb.append(hotCoins).append("\n");
            sb.append("***********************************\n");
        }
        if(topic.toUpperCase().equals("NFT")){
            flag =true;
            sb.append("*************NFT COIN************\n");
            String hotCoins = FeiXiaoHaoApiClient.concept(41);
            sb.append(hotCoins).append("\n");
            sb.append("***********************************\n");
        }
        if(flag){
            return sb.toString();
        }else{
            return null;
        }
    }
    public static String coin(String coin) throws Exception {
        StringBuilder sb = new StringBuilder();
        if(CoinCache.coinsMap.containsKey(coin)){
            String newCoin = CoinCache.coinsMap.get(coin);
            String result = FeiXiaoHaoApiClient.coin(coin,newCoin);
            sb.append(result);
        }
        return sb.toString();
    }


    public static void main(String[] args) throws Exception {
        CoinCache.initCoins();
        System.out.println(coin("MIR"));
    }
}
