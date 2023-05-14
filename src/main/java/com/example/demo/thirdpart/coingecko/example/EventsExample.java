package com.example.demo.thirdpart.coingecko.example;

import com.example.demo.thirdpart.coingecko.domain.Events.EventCountries;
import com.example.demo.thirdpart.coingecko.domain.Events.EventTypes;
import com.example.demo.thirdpart.coingecko.domain.Events.Events;
import com.example.demo.thirdpart.coingecko.CoinGeckoApiClient;
import com.example.demo.thirdpart.coingecko.impl.CoinGeckoApiClientImpl;

public class EventsExample {
    public static void main(String[] args) {

        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();
        Events events = client.getEvents();
        System.out.println(events);

        long eventCount = events.getCount();
        System.out.println(eventCount);

        EventCountries eventCountries = client.getEventsCountries();
        System.out.println(eventCountries);

        EventTypes eventsTypes = client.getEventsTypes();
        System.out.println(eventsTypes);
    }
}
