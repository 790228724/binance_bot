package com.example.demo.bean;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class Ticker {
    private String symbol;                // 交易对符号
    private String priceChange;           // 价格变动
    private String priceChangePercent;    // 价格变动百分比
    private String weightedAvgPrice;      // 加权平均价格
    private String openPrice;             // 开盘价
    private String highPrice;             // 最高价
    private String lowPrice;              // 最低价
    private String lastPrice;             // 最新价格
    private String volume;                // 交易量
    private String quoteVolume;           // 报价资产交易量
    private long openTime;                // 开盘时间
    private long closeTime;               // 收盘时间
    private long firstId;                 // 首笔成交ID
    private long lastId;                  // 末笔成交ID
    private int count;                    // 成交笔数

}
