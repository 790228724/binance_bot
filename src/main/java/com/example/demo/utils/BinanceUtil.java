package com.example.demo.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerPrice;
import com.example.demo.bean.Ticker;
import com.example.demo.common.okhttp.OkHttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.ta4j.core.*;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.LinkedList;
import java.util.List;

/**
 * description: BinanceUtil <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/9 17:32 <br>
 */

public class BinanceUtil {
    private static Num LAST_BAR_CLOSE_PRICE;

    private static String API_KEY;
    private static String API_SECRET;

    private static BinanceApiRestClient client = null;
    private static BinanceApiWebSocketClient liveClient = null;

    public static List<String> getBitcoinSymbols() {
        List<String> symbols = new LinkedList<String>();
        BinanceApiRestClient client = getRestClient();
        List<TickerPrice> prices = client.getAllPrices();
        for (TickerPrice tickerPrice : prices) {
            if (StringUtils.endsWith(tickerPrice.getSymbol(), "USDT")) {
                symbols.add(tickerPrice.getSymbol());
            }
        }
        return symbols;
    }

    public static List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval) {
        return getRestClient().getCandlestickBars(symbol, interval);
    }

    public static TickerPrice getPrice(String symbol) {
        return getRestClient().getPrice(symbol);
    }


    public static List<Candlestick> getLatestCandlestickBars(String symbol, CandlestickInterval interval) {
        return getRestClient().getCandlestickBars(symbol, interval, 1, null, null);
    }


    public static Ticker getTicker(String symbol){
        String async = OkHttpUtils.builder()
                .url("https://api.binance.com/api/v3/ticker?symbol="+ symbol +"&windowSize=1m")
                .get()
                .sync();
        JSONObject parse = (JSONObject) JSONObject.parse(async);
        if(parse.containsKey("code")){
            return null;
        }else{
            Ticker ticker = parse.toJavaObject(Ticker.class);
            return ticker;
        }
    }

    public static BinanceApiRestClient getRestClient() {
        if (client == null) {
            BinanceApiClientFactory factory = BinanceApiClientFactory
                    .newInstance(API_KEY, API_SECRET);
            client = factory.newRestClient();
        }
        return client;
    }

    public static BinanceApiWebSocketClient getWebSocketClient() {
        if (liveClient == null) {
            BinanceApiClientFactory factory = BinanceApiClientFactory
                    .newInstance(API_KEY, API_SECRET);
            liveClient = factory.newWebSocketClient();

        }
        return liveClient;
    }

    public static void init(String binanceApiKey, String binanceApiSecret) throws Exception {
        if (StringUtils.isEmpty(binanceApiKey) || StringUtils.isEmpty(binanceApiSecret)) {
            throw new Exception("Binance API params cannot be empty; please check the config properties file");
        }
        API_KEY = binanceApiKey;
        API_SECRET = binanceApiSecret;
    }

    public static void main(String[] args) throws Exception {
        init("LXyty1nDerKp0x9QRMXcW9YCsCbgv0h9HGxNb8C5Ysj7ov6rrSoBSGmjNrOs67Xo", "gZeHmJiRlsbZ8dMgkRIHxkgSGfQLpQOf0vQFRwmsLJ4YOlrqlK6Zrky7SnakvCvk");
        //List<String> symbols = getBitcoinSymbols();
        List<Candlestick> candlesticks = getCandlestickBars("BTCUSDT", CandlestickInterval.DAILY);
        BarSeries barSeries = BinanceTa4jUtil.convertToBarSeries(candlesticks.subList(0, 100), "BTCUSDT", CandlestickInterval.DAILY.getIntervalId());
        Strategy strategy = BinanceTa4jUtil.buildStrategy(barSeries, "EMA");
        // Initializing the trading history
        TradingRecord tradingRecord = new BaseTradingRecord();
        System.out.println("************************************************************");
        for (int i = 100; i < candlesticks.size(); i++) {
            Bar newBar = BinanceTa4jUtil.convertToBaseBar(candlesticks.get(i));
            barSeries.addBar(newBar);
            int endIndex = barSeries.getEndIndex();
            if (strategy.shouldEnter(endIndex)) {
                // Our strategy should enter
                //System.out.println("Strategy should ENTER on " + endIndex);
                boolean entered = tradingRecord.enter(endIndex, newBar.getClosePrice(), DecimalNum.valueOf(10));
                if (entered) {
                    Trade entry = tradingRecord.getLastEntry();
                    System.out.println("buy signal (price: " + entry.getNetPrice().doubleValue()
                            + " buy time: " + barSeries.getBar(entry.getIndex()).getSimpleDateName() + ")");
                }
            } else if (strategy.shouldExit(endIndex)) {
                // Our strategy should exit
                //System.out.println("Strategy should EXIT on " + endIndex);
                boolean exited = tradingRecord.exit(endIndex, newBar.getClosePrice(), DecimalNum.valueOf(10));
                if (exited) {
                    Trade exit = tradingRecord.getLastExit();
                    System.out.println("sell signal (price=" + exit.getNetPrice().doubleValue()
                            + " sell time: " + barSeries.getBar(exit.getIndex()).getSimpleDateName() + ")");
                }
            }
        }
    }


}