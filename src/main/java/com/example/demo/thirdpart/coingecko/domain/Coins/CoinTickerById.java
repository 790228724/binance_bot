package com.example.demo.thirdpart.coingecko.domain.Coins;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.demo.thirdpart.coingecko.domain.Shared.Ticker;
import lombok.*;

import java.util.List;

@Data
public class CoinTickerById {
    @JsonProperty("name")
    private String name;
    @JsonProperty("tickers")
    private List<Ticker> tickers;

}
