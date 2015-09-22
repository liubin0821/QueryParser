# -*- coding:utf-8 -*-
#!/usr/bin/env python

import sys
import traceback
import time
import os
import filecmp
import difflib
import re
import GetOneDictLine
import FindTheChangedPart


from mysql import MySqlClient
from redis_client import RedisSqlClient
import setting

'''
install driver : 
    apt-get install python-psycopg2
    apt-get install python-dev
    pip install ujson

同步ifind的a,h股，美股的股票全称，简称，代码以及官网url
1.导入mysql
2.导入entry page的库
如果已经导过就跳过

'''

def selectUrl(url):
    """ 有些类似巨潮的聚合网站过滤"""
    if url.find(';') > 0:
        urls = url.split(';')
        urls = filter(lambda x : x.find("irasia.com") < 0, urls)
        url = urls[0]
    if not url.startswith("http"):
        url = "http://" + url
    if not url.endswith("/"):
        url += "/"
    return url

def readFromMysql():
    mysqlClient = MySqlClient.from_setting(setting.MYSQL)

    sql = """
    SELECT type, sitename, alias, url, op_user FROM `site_name` 
    """
    data = mysqlClient.readlines(mysqlClient.conn, sql)
    return data

def write_to_file(list,file_name):
    out = open(file_name, "wb");
    for value in list:
        out.write(value);
    out.close;
    
def writeToDict(data):
    #must after load data  转变为词典行数据
    new_data=[]
    #001005  6888   www.axiata.com  Axiata Group Berhad Axiata Group Berhad Axiata  None
    for item in data:
        tup = item.split('\t')
        type=tup[0]
        if type == "ASTOCK":
            type = "_a股"
        elif type == "HKSTOCK":
            type = "_港股"
        elif type == "USSTOCK":
            type = "_美股"
        sitename=tup[1]
        aliass=tup[2].split('#')
        url=tup[3]
        # #同花顺/*Cate=pretreat;Seg=;Value=onto_value:prop=_股票简称;infos=id:300033;$
        if re.match(r"\d+$", sitename.lower()) and True or False:
            new_data.append(GetOneDictLine.getOnePostDictLineWithId(sitename,type,sitename.lower()))
        else:
            new_data.append(GetOneDictLine.getOneDictLineWithId(sitename,type,sitename.lower()))
        for alias in aliass:
            if alias != "":
                if re.match(r"\d+$", alias.lower()) and True or False:
                    new_data.append(GetOneDictLine.getOnePostDictLineWithId(alias,type,sitename.lower()))
                else:
                    new_data.append(GetOneDictLine.getOneDictLineWithId(alias,type,sitename.lower()))
    
    
    is_update = False
    is_update =  FindTheChangedPart.run(new_data,setting.SITE_NAME_FILE)
    return is_update

def writeToRedis(data):
    redisClient = RedisSqlClient.from_setting(setting)
    
    for item in data:
        tup = item.split('\t')
        tup = item.split('\t')
        type=tup[0]
        sitename=tup[1]
        alias=tup[2]
        url=tup[3]
        redisClient.writeToRedis("sitename:"+sitename, url)

def readFromRedis(data):
    redisClient = RedisSqlClient.from_setting(setting)
    
    for item in data:
        tup = item.split('\t')
        type=tup[0]
        sitename=tup[1]
        alias=tup[2]
        url=tup[3]
        uuid = redisClient.readFromRedis("sitename:"+sitename)
        #print uuid
def dealWithConcept():
    mysqlClient = MySqlClient.from_setting(setting.MYSQL_SERVERS["52"])
    type = "_所属概念"
    sql = """
    SELECT t.id, t.`name` FROM `concept_list` t 
    """
    data = mysqlClient.readlines(mysqlClient.conn, sql)
    if data is None:
        return False
    
    new_data = []
    for item in data:
        tup = item.split('\t')
        if len(tup) == 2 :
            new_data.append(GetOneDictLine.getOneDictLineWithId(tup[1],type,tup[0].lower()))
            
    return FindTheChangedPart.run(new_data,setting.STOCK_CONCEPT_FILE)        
    

def process(write_to_dict, write_to_redis):
    data = readFromMysql();
    if data is None:
        print "access postgresql error!"
    else:
        is_update = False
        if write_to_dict==True:
            print "write_to_dict: write to dict"
            is_update = writeToDict(data)
            if is_update == True:
                if write_to_redis == True:
                    print "write_to_redis: write to redis"
                    writeToRedis(data)
                    #readFromRedis(data)
        elif write_to_redis==True:
            print "write_to_redis: write to redis"
            writeToRedis(data)
            
        #is_update = dealWithConcept()  or is_update   
        if is_update :    
            os.system("bash " + setting.LTP_SERVER + " restart")

def main():
    start = time.time()
    try:  
        if len(sys.argv) == 1:
            process(True, True);
        else:
            temp = sys.argv[1];
            print "argument: "+temp
            if temp == "write_to_dict":
                process(True, False);
            elif temp == "write_to_redis":
                process(False, True);
            else:
                print "Bad argument: "+temp;
        print "ok"
    except Exception as e:
        print "exception,need manual check:" + str(e)
        print traceback.format_exc()
        print str(e)
    end = time.time()
    print "cost of one round clusting(seconds):" + str(int(end-start))

if __name__ == '__main__':
    main()
