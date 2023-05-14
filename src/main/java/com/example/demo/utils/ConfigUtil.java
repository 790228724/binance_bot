package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * description: ConfigUtil <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/9 17:50 <br>
 */
@Slf4j
public class ConfigUtil {
    public static final String CONFIG_PAUSE_TIME_MINUTES = "thread.pauseTimeInMinutes";
    public static final String CONFIG_BINANCE_TICK_INTERVAL = "tick.timeFrame";
    public static final String CONFIG_BINANCE_API_KEY = "binance.apiKey";
    public static final String CONFIG_BINANCE_API_SECRET = "binance.apiSecret";
    public static final String CONFIG_TRADING_DO_TRADES = "trading.doTrades";
    public static final String CONFIG_TRADING_STOPLOSS_PERCENTAGE = "trading.stopLossPercentage";
    public static final String CONFIG_TRADING_TRADE_SIZE_BTC = "trading.tradeSizeBTC";
    public static final String CONFIG_TRADING_MAX_SIMULTANEOUS_TRADES = "trading.maxSimultaneousTrades";
    public static final String CONFIG_TRADING_DO_TRAILING_STOP = "trading.doTrailingStop";
    public static final String CONFIG_TRADING_STRATEGY = "trading.strategy";

    private static String systemConfigFilePath = "";

    public static void setSystemConfigFilePath(String path) {
        log.info("Setting config file path to: " + path);
        systemConfigFilePath = path;
    }

    public static String readPropertyValue(String property) {
        if (StringUtils.isNotEmpty(systemConfigFilePath)) {
            return readPropertyValue(property, true);
        }
        return readPropertyValue(property, false);
    }

    private static String readPropertyValue(String property, boolean useExternalFile) {
        FileInputStream file = null;
        String propertyValue = "";
        Properties configProperties = new Properties();
        String configFilePath = "./config.properties";
        if (!useExternalFile) {
            configFilePath = ConfigUtil.class.getResource("/config.properties").getFile();
        }
        if (StringUtils.isNotEmpty(systemConfigFilePath)) {
            configFilePath = systemConfigFilePath;
        }
        try {
            file = new FileInputStream(configFilePath);
            configProperties.load(file);
            propertyValue = configProperties.getProperty(property);
        } catch (FileNotFoundException e) {
            log.error("Unable to locate config properties file", e);
        } catch (IOException e) {
            log.error("Unable to open config properties file", e);
        } finally {
            IOUtils.closeQuietly(file);
        }

        return propertyValue;
    }
}
