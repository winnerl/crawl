package com.fun.crawl.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class BigDecimalUtil {

    /**
     * d1 + d2
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double add(String d1, String d2) { // 进行加法运算
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.add(b2).doubleValue();
    }

    /**
     * d1-d2
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double sub(String d1, String d2) { // 进行减法运算
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * d1 * d2
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double mul(String d1, String d2) { // 进行乘法运算
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * d1 / d2
     *
     * @param d1
     * @param d2
     * @param len 小数点后几位
     * @return
     */
    public static double div(String d1, String d2, int len) {// 进行除法运算
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 判断是否是正数(正整数+正浮点数+非0)
     *
     * @param num
     * @return true是  false不是
     */
    public static boolean isRightNumber(String num) {
        //正整数
        String reg = "^[0-9]*[1-9][0-9]*$";
        //正浮点数
        String reg2 = "^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$";
        System.out.println("正整数判断：" + Pattern.compile(reg).matcher(num).find());
        System.out.println("正浮点数判断：" + Pattern.compile(reg2).matcher(num).find());
        if (Pattern.compile(reg).matcher(num).find() || Pattern.compile(reg2).matcher(num).find()) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
//		System.out.println(sub("1.0","0.1"));
//		System.out.println(div("0.1","30",2));
//		double a = BigDecimalUtil.mul("9.9", "9.98888");
//		System.out.println(a);
////		System.out.println(BigDecimalUtil.add("9.9", "98.01"));
//		System.out.println(isRightNumber("0.001"));
        DecimalFormat df = new DecimalFormat("#0.00");

        double d1 = 21212351233.24224;
        double d2 = 1.01342;
        double d3 = 2.0;
        System.out.println(df.format(d2));
        System.out.println(df.format(d1));
        System.out.println(df.format(d3));


    }

}
