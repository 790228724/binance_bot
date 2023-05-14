package com.example.demo.thirdpart.coingecko.example;

import com.example.demo.thirdpart.coingecko.domain.Status.StatusUpdates;
import com.example.demo.thirdpart.coingecko.CoinGeckoApiClient;
import com.example.demo.thirdpart.coingecko.impl.CoinGeckoApiClientImpl;

public class StatusUpdatesExample {
    public static void main(String[] args) {

        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

        StatusUpdates statusUpdates = client.getStatusUpdates();
        System.out.println(statusUpdates);

        long totalStatusUpdates = statusUpdates.getUpdates().size();
        System.out.println(totalStatusUpdates);
    }
}
