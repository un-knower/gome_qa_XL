package com.gome.util;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {
	
	private static  JedisPool jedisPool = null;
	
	//获取链接
	public static synchronized Jedis getJedis(){
		if(jedisPool==null){
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			//指定连接池中最大空闲连接数
			jedisPoolConfig.setMaxIdle(1000);
			//链接池中创建的最大连接数
			jedisPoolConfig.setMaxTotal(1000);
			//设置创建链接的超时时间
			jedisPoolConfig.setMaxWaitMillis(20000);
			//表示连接池在创建链接的时候会先测试一下链接是否可用，这样可以保证连接池中的链接都可用的
			jedisPoolConfig.setTestOnBorrow(true);
			
			jedisPool = new JedisPool(jedisPoolConfig, "10.112.75.26", 6379);
		}
		return jedisPool.getResource();
	}
	
	//关闭链接
	public static void closeResource(Jedis jedis){
		if(null !=jedis){
			jedis.close();
		}
	}
	
	
	public static void main(String[] args) {
		Jedis jedis=RedisUtils.getJedis();
		jedis.set("At_产品尺寸mm", "attr_1522");
		//System.out.println(jedis.get("test"));
		
		// String[] names = new String[]{"张三", "李四", "王五", "找六", "王麻子"};
		 //创建并设置一个set的值
		// jedis.sadd("testnames", names);
		 //获取一个set中所有的元素
//	     Set<String> namesSet = jedis.smembers("testnames");
//          for (String name : namesSet) {
//            System.out.print(name + " ");  //set集合的特点：无序、无重复元素
//         }
//		Map<String,String> maps=new HashMap<String, String>();
//		maps.put("1001", "test1");
//		maps.put("1002", "test2");
//		maps.put("1003", "test3");
//		for (Map.Entry<String, String> entry : maps.entrySet()) {
//			 System.out.println("key:"+entry.getKey());
//			 System.out.println("value:"+entry.getValue());
//			 String id=entry.getKey();
//			 String name=entry.getValue();
//			 String key = "sku_" + id;
//			 jedis.set(key, name);
//		}
	}

}
