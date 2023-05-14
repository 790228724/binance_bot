package com.example.demo.thirdpart.coingecko.example;

import com.example.demo.thirdpart.coingecko.CoinGeckoApiClient;
import com.example.demo.thirdpart.coingecko.impl.CoinGeckoApiClientImpl;

public class PingExample {
    public static void main(String[] args) {

        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();
        System.out.println(client.ping());

    }
}
