package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: StringUtil <br>
 *
 * @author xie hui <br>
 * @version 1.0 <br>
 * @date 2021/7/15 11:11 <br>
 */
public class StringUtil {
    public static boolean isContainChinese(String str){
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
    public static void main(String[] args) throws Exception {
        System.out.println(StringUtil.isContainChinese("人"));
    }
}
