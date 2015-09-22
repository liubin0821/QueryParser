package com.myhexin.qparser.util.lightparser;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LightParserRedisState {

  private JedisPool jedisPool;
  private int db;
  
  public LightParserRedisState(Map map) {
    // init redis client
    String host = (String) map.get("host");
    int port =  Integer.parseInt((String)map.get("port"));
    int db = Integer.parseInt((String)map.get("db"));
    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxIdle(5);
    config.setMaxWait(100);
    
    this.jedisPool = new JedisPool(config, host, port);
    this.db = db;
  }
  
  public String getData(String additionalKey, String key ) {
    Jedis jedis = null;
    try {
      jedis = this.jedisPool.getResource();
      jedis.select(this.db);
      return jedis.hget(additionalKey, key);
    } catch (Exception e) {
      this.jedisPool.returnBrokenResource(jedis);
      throw new RuntimeException("failed to access redis", e);
    } finally {
      if (jedis != null) {
        this.jedisPool.returnResource(jedis);
      }
    }
  }
  
  public long hsetData(String additionalKey, String key, String value ) {
    Jedis jedis = null;
    try {
      jedis = this.jedisPool.getResource();
      jedis.select(this.db);
      return jedis.hset(additionalKey, key, value);
    } catch (Exception e) {
      this.jedisPool.returnBrokenResource(jedis);
      throw new RuntimeException("failed to access redis", e);
    } finally {
      if (jedis != null) {
        this.jedisPool.returnResource(jedis);
      }
    }
  }
  
  public long expireDate(String additionalKey, int seconds) {
    Jedis jedis = null;
    try {
      jedis = this.jedisPool.getResource();
      jedis.select(this.db);
      return jedis.expire(additionalKey, seconds);
    } catch (Exception e) {
      this.jedisPool.returnBrokenResource(jedis);
      throw new RuntimeException("failed to access redis", e);
    } finally {
      if (jedis != null) {
        this.jedisPool.returnResource(jedis);
      }
    }
  }

}
