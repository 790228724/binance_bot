package com.example.demo.thirdpart.coingecko.example;

import com.example.demo.thirdpart.coingecko.CoinGeckoApiClient;
import com.example.demo.thirdpart.coingecko.constant.Currency;
import com.example.demo.thirdpart.coingecko.impl.CoinGeckoApiClientImpl;

import java.util.Map;

public class SimpleExample {
    public static void main(String[] args) {

        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

        Map<String, Map<String, Double>> bitcoin = client.getPrice("bitcoin",Currency.USD);

        System.out.println(bitcoin);

        double bitcoinPrice = bitcoin.get("bitcoin").get(Currency.USD);

        System.out.println(bitcoinPrice);
    }
}
