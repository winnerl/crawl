package com.fun.crawl.utils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 产生UUID的唯一性ID的工具类
 */
public class UUIDGen {
	public static String genUUID() {
		// TODO 随机生成UUID
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		String temp = str.substring(0,8)+str.substring(9, 13)+str.substring(14, 18)+str.substring(19, 23)+str.substring(24);
		return temp;
	}
	/**
	 * 获取uuid，去除-
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	//随机生成四位数字
	public static String getCode(){
		int m = (int)(Math.random()*9000+1000);
		return String.valueOf(m);
	}
	//随机生成六位数字
	public static String getRandom(){
		int i=(int)(Math.random()*900000+100000); 
		return String.valueOf(i);
	}
	
	public static String getEightRandom(){
		int i=(int)(Math.random()*90000000+10000000); 
		return String.valueOf(i);
	}
	
	/**
	 * 随机生成3位数字
	 * @return
	 */
	public static String getThreeRandom(){
		int m = (int)(Math.random()*900+100);
		return String.valueOf(m);
	}

	/**
	 * 随机生成3位数字
	 * @return
	 */
	public static String getFourRandom(){
		int m = (int)(Math.random()*900+100);
		return String.valueOf(m)+"V";
	}

	/**
	 * 生成唯一编号
	 * @return
	 */
	public static String getOnlyCode(){
		String res=	System.currentTimeMillis()+"";
		res="ZK"+res.substring(9, res.length())+getThreeRandom();
		return res;
	}
	/**
	 * id放入list
	 * 
	 * @param id
	 *            id(多个已逗号分隔)
	 * @return List集合
	 */
	public static List<String> getList(String id) {
	    List<String> list = new ArrayList<String>();
	    if(StringUtils.isNotBlank(id)){
	    	String[] str = id.split(",");
	    	for (int i = 0; i < str.length; i++) {
	    		list.add(str[i]);
	    	}
	    }
	    return list;
	}
	
	/**
	 * double值比较
	 * @param done	值1
	 * @param dtwo	值2
	 * @return done=dtwo:0相等，done>dtwo:1大于，done<dtwo:-1小于于，
	 */
	public static int doubleCompare(String done,String dtwo) {
		java.math.BigDecimal d1=new java.math.BigDecimal(done);
		java.math.BigDecimal d2=new java.math.BigDecimal(dtwo);
		return d1.compareTo(d2);
	}
	
	/**
	 * 微信过滤表情
	 * @author hsw
	 *
	 */
	 public static String filterEmoji(String source) {
	        if (source == null) {
	            return source;
	        }
	        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
	                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
	        Matcher emojiMatcher = emoji.matcher(source);
	        if (emojiMatcher.find()) {
	            source = emojiMatcher.replaceAll("*");
	            return source;
	        }
	        return source;
	    }
	
	
	public static void main(String[] args) {
//		System.out.println(getOnlyCode());
		System.out.println(genUUID());
		System.out.println(doubleCompare("11.24", "11.25"));
		System.out.println(doubleCompare("11.24", "11.24"));
		System.out.println(doubleCompare("11.25", "11.24"));
	}	
}

