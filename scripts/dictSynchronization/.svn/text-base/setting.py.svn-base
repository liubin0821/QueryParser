#-*-coding:utf-8-*-
#!/usr/bin/env python2.6

import os
import logging

from ms_props import *

#common 
ENV = "PROD"
#ENV = "TEST"
APP_NAME = "stocksync"
PROXY_ADDRESS =  "172.20.201.254:3128"

#common 
#LTP_PATH = "."
LTP_PATH = "/home/ontology/ltp-server"
SITE_NAME_FILE="site_name.dict"
BUSINESS_NAME_FILE="business_name.dict"
TRUST_PRODUCT_FILE="trust_prod.dict"
STOCK_CONCEPT_FILE="stock_concept.dict"
ANALYST_NAME="analyst_name.dict" #分析师
RESEARCH_INSTITUTE="research_institute.dict" #_研究机构
LTP_SERVER=LTP_PATH+"/ltp-server.sh"

tempfile = LTP_PATH + '/dictSynchronization/.lastData/temp.dict'

MS_PROP_FILE = "ms_props.txt"

#app相关日志配置
LOG_DIR = LTP_PATH+'/ltp-logs/'
os.popen('mkdir -p ' + LOG_DIR)
LOG_INFO = {
  'LOG_FILE'  : LOG_DIR + APP_NAME + '.log',
  'LOG_LEVEL' : logging.INFO,
  'APP_NAME'  : APP_NAME
}

#Nagios 报警相关配置
NAGIOS_OK="0"
NAGIOS_CRITICAL="2"
NAGIOS_HOSTS={"PROD":"prod00034" , "TEST":"dfs00006.soniu.com"}
NAGIOS_HOST=NAGIOS_HOSTS[ENV]
NAGIOS_BIN="/usr/sbin/send_nsca"
NAGIOS_NAME=APP_NAME

STOCK_DATA_FILE="../data/code.txt"

MYSQL_SERVERS = { 
        "PROD":{'host':"192.168.201.147", 'port':3306, 'db':'other_admin', 'user':'qnateam', 'password':'qnateam'},
        "TEST":{'host':"172.20.201.147", 'port':3306, 'db':'other_admin', 'user':'qnateam', 'password':'qnateam'},
        "52":{'host':"172.20.0.52", 'port':3306, 'db':'web_stockpick', 'user':'parserteam', 'password':'parserteamer'}
    }
MYSQL = MYSQL_SERVERS[ENV]

REDIS_SERVERS = { 
        "PROD":{'host':"192.168.201.95", 'port':6679, 'db':2, 'user':'', 'password':''},
        "TEST":{'host':"172.20.23.213", 'port':6371, 'db':9, 'user':'', 'password':''}
    }
REDIS = REDIS_SERVERS[ENV]

#201 offline : 192.168.201.128 - 131
#23 offline : 192.168.23.11 - 18
#backup offline: 192.168.201.195-197

HBASE_INFOS = {
        "PROD":{
            'SrcHBase': {'host':'192.168.201.128','port':9090},
            'DestHBase': {'host':'192.168.201.195','port':9090},
            },
        "TEST":{
            'SrcHBase': {'host':'192.168.23.11','port':9090},
            'DestHBase': {'host':'192.168.23.11','port':9090},
            }
        }
HBASE_INFO = HBASE_INFOS[ENV]

# 林沛坤  词典服务
SERVER_PORT = 8888


#************************************
#一下为增量式更新配置
#************************************

PORT = "12548"
MS_PORT = "13548"
DICT_SOURCE = "http://192.168.201.164:8888/GetLtpDicts"
MS_DICT_SOURCE = "http://192.168.201.164:8888/GetMSLtpDicts"
MAIL = "wangjiajia xiawei chenhao aizhipeng liuxiaofeng"

#liuxiaofeng 2015/6/5 192.168.200.93:9191=>192.168.201.67:9191 on luyang's request
MAKE_BIN_URL = "http://192.168.201.67:9191/updateLtpDict?"

#词典存放的mysql数据库
ONTOLOGYDB_CONFIG = {
    'host' : "172.20.201.147",
    'port' : 3306,
    'db' : 'ontologydb',
    'user' : 'qnateam',
    'passwd' : 'qnateam'
}

ONTOLOGYDB_CONFIG_DEV = {
    'host' : "192.168.23.52",
    'port' : 3306,
    'db' : 'ontologydb',
    'user' : 'qnateam',
    'passwd' : 'qnateam'
}

#????
MAKE_LTP_BIN_SERVICE={
     "52":["backtest","192.168.23.52","backtest","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "86":["index","192.168.201.86","index","/home/index/ltp-server/data/segger.dict.bin.stock"],
     "164":["ontology","192.168.201.164","ontology2013","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "179":["ontology","192.168.201.179","123456","/home/ontology/ltp-server-new-system/data/segger.dict.bin.stock"],
     "167":["ontology","192.168.201.167","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "168":["ontology","192.168.201.168","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "180":["ontology","192.168.201.180","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "205":["ontology","192.168.201.205","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "206":["ontology","192.168.201.206","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "116":["ontology","192.168.201.116","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "117":["ontology","192.168.201.117","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "105":["ontology","192.168.201.105","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "21":["ontology","192.168.200.21","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"],
     "61":["ontology","192.168.200.61","654321","/home/ontology/ltp-server/data/segger.dict.bin.stock"]
    }

#这个http service是用来重新加载ltp bin文件的?????
MAKE_LTP_BIN_RELOAD={
     "52":"http://192.168.23.52:12548/ltp?reload=1",
     "86":"http://192.168.201.86:13548/ltp?reload=1",
     "164":"http://192.168.201.164:12548/ltp?reload=1",
     "179":"http://192.168.201.179:12548/ltp?reload=1",
     "167":"http://192.168.201.167:12548/ltp?reload=1",
     "168":"http://192.168.201.168:12548/ltp?reload=1",
     "180":"http://192.168.201.180:12548/ltp?reload=1",
     "205":"http://192.168.201.205:12548/ltp?reload=1",
     "206":"http://192.168.201.206:12548/ltp?reload=1",
     "116":"http://192.168.201.116:12548/ltp?reload=1",
     "117":"http://192.168.201.117:12548/ltp?reload=1",
     "105":"http://192.168.201.105:12548/ltp?reload=1",
     "21":"http://192.168.201.21:12548/ltp?reload=1",
     "61":"http://192.168.201.61:12548/ltp?reload=1"
    }


#主搜索的分词机器
MS_LTP_MACHINE_IDS=["86"]

#主搜索词典目录
MS_PROPS_IGNORE, PROPS_IGNORE = load_list()