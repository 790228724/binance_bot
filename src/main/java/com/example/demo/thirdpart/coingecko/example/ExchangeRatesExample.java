package com.example.demo.thirdpart.coingecko.example;

import com.example.demo.thirdpart.coingecko.domain.ExchangeRates.ExchangeRates;
import com.example.demo.thirdpart.coingecko.CoinGeckoApiClient;
import com.example.demo.thirdpart.coingecko.impl.CoinGeckoApiClientImpl;

public class ExchangeRatesExample {
    public static void main(String[] args) {

        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

        ExchangeRates exchangeRates = client.getExchangeRates();
        System.out.println(exchangeRates);

    }
}
