#! /usr/bin/env python 
#coding=utf-8 
import redis
import types
import hashlib

class RedisSqlClient:
    
    def __init__(self, host, port, user, password, db):
        self.host = host
        self.port = port
        self.user = user
        self.password = password
        self.db = db
        self.r = self.getRedisClient()
    
    @classmethod
    def from_setting(cls, setting):
        redis_setting = setting.REDIS
        host = redis_setting.get("host")
        port = int(redis_setting.get("port"))
        user = redis_setting.get("user")
        password = redis_setting.get("password")
        db = redis_setting.get("db")
        return cls(host, port, user, password, db)
    
    def getRedisClient(self): 
        return redis.Redis(self.host, int(self.port), int(self.db))
    
    def writeToRedis(self, key, value):
        uid = self.get_uid(value)
        self.r.set(key, uid)
        
    def readFromRedis(self, key):
        return self.r.get(key)
    
    def get_uid(self, url):
        """
            get the uid of the url
            algorithm:
            1) get 16 bytes (128 bits) md5, encoded by hex
            2) split the first 8 bytes and the last 8 bytes
            3) convert the two 8 bytes into int
            4) XOR the two 8 bytes
            5) encode the result by hex
        """
        # convert unicode to str (with encode utf-8)
        # this function is str safe, without double encode error
        
        if isinstance(url, types.StringType):
            # md5 is a string represents a 32bytes hex number
            md5 = hashlib.new("md5", url).hexdigest()
            first_half_bytes = md5[:16]
            last_half_bytes = md5[16:]
            
            # get the two long int
            first_half_int = int(first_half_bytes, 16)
            last_half_int = int(last_half_bytes, 16)
            
            # XOR the two long int, get a long int
            xor_int = first_half_int ^ last_half_int
            
            # convert to a hex string
            uid = "%x" % xor_int
            
            return uid
    