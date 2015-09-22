#!/usr/bin/env python
# -*- coding:utf-8 -*-

import sys
import traceback
import time
import os
import filecmp
import difflib
import re
import GetOneDictLine
import FindTheChangedPart
import traceback

import PostGreHelper
from mysql import MySqlClient
from redis_client import RedisSqlClient
from MongoClient import MongoClient
import setting

reload(sys)
sys.setdefaultencoding("utf-8")

sys.path.append(os.getcwd()+'/../dictDbInputOutput')
import InheritDictUpdate

def readFromMysql(dbConf,sql):
    mysqlClient = MySqlClient.from_setting(dbConf)
    data = mysqlClient.readlines(mysqlClient.conn, sql)
    return data

def readFromifind(dbConf,sql):
    postgre  = PostGreHelper.PostGreHelper()
    handle = postgre.from_setting(dbConf);
    return postgre.readlines(handle, sql)

def readFromMongo(dbConf,sql):
    client=MongoClient.from_setting(dbConf)
    return client.read(dbConf.get("table"),sql)

def changeToDictLine(dictType,data,from_where):
    new_data=[]
    for line in data:
        cols = line.split('\t')
        id = ''
        for col in cols:
            if(col == cols[0]):
                id = col
                if dictType=="_上证a股代码" or dictType=="_深证a股代码":
                    new_data.append(GetOneDictLine.getOneDictLineWithId(col,dictType,cols[0],""));
                continue
            elif(col != ''):
                if(id != ''):
                    new_data.append(GetOneDictLine.getOneDictLineWithId(col,dictType,id,from_where));  
                else:
                    new_data.append(GetOneDictLine.getOneDictLine(col,dictType,from_where));     
    return new_data

def doSynchonize(dictType,data,from_where):
    new_data=changeToDictLine(dictType,data,from_where)
    return FindTheChangedPart.run(new_data,dictType+".dict")  

def getSynchonizeDbConf():
    #从数据库中读取配置
    basicDbConf={'host':"172.20.201.147", 'port':3306, 'db':'ontologydb', 'user':'qnateam', 'password':'qnateam'}
    sql = """ SELECT * FROM `automatic_synchronization_crl` WHERE is_using=TRUE """
    return readFromMysql(basicDbConf,sql)


def getMongoDbDicts():
    dbConf={'host':"172.20.201.147", 'port':3306, 'db':'ontologydb', 'user':'qnateam', 'password':'qnateam'}
    sql = """ SELECT * FROM data_lines_v WHERE  from_where='mongo' order by text ASC, id ASC"""
    
    updater = InheritDictUpdate.InheritDictUpdate(setting.ONTOLOGYDB_CONFIG)
    dict_db = updater.tableConfig.get('data_lines')
    if dict_db is  None:
        return
    
    #把dict中text根据type进行继承的合并, 相同text不同type的合并处理,生成dictLine
    data=dict_db.selectBySql(sql)

    return data
        
        

def main():
    confs=getSynchonizeDbConf()
    is_update = False
    if confs is not None:
        for line in confs:
            try:
                cols = line.split('\t')
                #if(len(cols) >= 9 and cols[2]=='mongo'):
                if(len(cols) >= 9 ):
                    dictType=cols[1]
                    print '\nstart dealwith ' + dictType +'\n'
                    conf={'host':cols[4], 'port':cols[5], 'db':cols[8], 'user':cols[6], 'password':cols[7],'table':cols[9]}
                    sql=cols[3]
                    data=[]
                    if cols[2] == "mysql" :
                        data=readFromMysql(conf,sql)
                    elif cols[2] == "ifind" :
                        data=readFromifind(conf,sql)
                    elif cols[2] == "mongo" :
                        data=readFromMongo(conf,sql)
                    is_update=(doSynchonize(dictType,data,cols[2]) or is_update)
            except Exception, e:
                exstr = traceback.format_exc()
                print exstr                
                pass 
        if is_update :    
            os.system("bash " + setting.LTP_SERVER + " restart")
                
if __name__ == "__main__":
    main()
    #getMongoDbDicts()
    pass              
            
            

    