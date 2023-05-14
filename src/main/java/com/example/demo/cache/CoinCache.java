package com.example.demo.cache;

import com.example.demo.thirdpart.coingecko.domain.Coins.CoinList;
import com.example.demo.utils.TrustAllHttpsCertificates;
import com.example.demo.thirdpart.coingecko.CoinGeckoApiClient;
import com.example.demo.thirdpart.coingecko.impl.CoinGeckoApiClientImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: CoinCache <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/13 14:37 <br>
 */
public class CoinCache {
    public static Map<String,String> coinsMap = new HashMap<>();
    public static void initCoins(){
        try {
            TrustAllHttpsCertificates.setTrue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();
        List<CoinList> coins = client.getCoinList();
        for(CoinList coinList: coins){
            System.out.println(coinList);
            coinsMap.put(coinList.getSymbol().toUpperCase(),coinList.getId());
        }
        coinsMap.put("GTC","gitcoin");
        coinsMap.put("MASK","mask-network");
        coinsMap.put("UNI","uniswap");
        coinsMap.put("EPS","ellipsis");
    }

    public static void main(String[] args){
        CoinCache.initCoins();
        System.out.println(coinsMap.get("GTC"));

    }
}
