package com.example.demo.thirdpart.coingecko.example;

import com.example.demo.thirdpart.coingecko.domain.Coins.CoinList;
import com.example.demo.thirdpart.coingecko.CoinGeckoApiClient;
import com.example.demo.thirdpart.coingecko.impl.CoinGeckoApiClientImpl;

import java.util.List;

/**
 * description: CoinList <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/13 14:33 <br>
 */
public class CoinListExample {
    public static void main(String[] args){
        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();
        List<CoinList> coins = client.getCoinList();
        for(CoinList coinList: coins){
            System.out.println(coinList);
        }
    }
}
