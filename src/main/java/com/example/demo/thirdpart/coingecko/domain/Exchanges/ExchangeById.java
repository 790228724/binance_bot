package com.example.demo.thirdpart.coingecko.domain.Exchanges;

import com.example.demo.thirdpart.coingecko.domain.Shared.Ticker;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExchangeById extends Exchanges{
    @JsonProperty("tickers")
    private List<Ticker> tickers;
    @JsonProperty("status_updates")
    private List<Object> statusUpdates;

}
