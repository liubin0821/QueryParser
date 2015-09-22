#!/usr/bin/python
#-*- coding: utf-8 -*-

import os,sys
import urllib,urllib2
import json
import sys
import shlex
import subprocess
import logging

reload(sys)  
sys.setdefaultencoding('utf8')  

sys.path.append(os.getcwd()+'/../dictDbInputOutput')
import InheritDictUpdate

sys.path.append(os.getcwd()+'/../dictSynchronization')
import setting

logging.basicConfig(level=logging.DEBUG,
                format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                datefmt='%a, %d %b %Y %H:%M:%S',
                filename='incre_ltp.log',
                filemode='a')

def makeJson(ddict):
    json_str = json.dumps(ddict)
    return json_str

def post(url, json_str):
    data = {}
    data["data"] = json_str
    data = urllib.urlencode(data)
    res = urllib.urlopen(url, data).read()
    return res
    
def makeMap():
    param = {}
    param["PORT"] = setting.PORT
    param["DICT_SOURCE"] = setting.DICT_SOURCE
    param["MODEL"] = "1" # 0全量更新 1增量更新
    param["HOSTINFO"] = []
    param["delete"] = []
    param["add"] = []
    param["update"] = []
    param["HTTP_RELOAD"] = []
    param["MAIL"]=setting.MAIL # 邮件名，缺省
    return param

def removeErrorDict(data):
    result = []
    for line in data:
        lines = line.split("/*Cate=")
        if len(lines) == 2 and lines[0].find("|")<0:
            result.append(line) 
        elif line.find("|")<0 :
            result.append(line) 
        else:    
            print "词典格式错误,或者词典文本中不能包含'|' --> " + line;
    return result     

def writeStrToLog(str):
    if str is not None :
            logging.debug('\n\n\nstart new incremental update \n'+str)
            print(str)
            
def writeListToLog(type,data):
    if data is not None :
        for line in data:
            logging.debug(type+'\t'+line)
            print(type+'\t'+line)




param = makeMap()
updater = InheritDictUpdate.InheritDictUpdate(setting.ONTOLOGYDB_CONFIG)
needUpdate = True
updateDict = {} #从数据库中读出的更新及删除信息
#判断更新那些环境

#主搜索用
ms_param = makeMap()
ms_update = False

#根据参数,设置HOSTINFO,HTTP_RELOAD
for i in range(1, len(sys.argv)):
    if setting.MAKE_LTP_BIN_SERVICE.has_key(sys.argv[i]):
        if not sys.argv[i] in setting.MS_LTP_MACHINE_IDS:
            param["HOSTINFO"].append(setting.MAKE_LTP_BIN_SERVICE[sys.argv[i]])
            #param["HTTP_RELOAD"].append(setting.MAKE_LTP_BIN_RELOAD[sys.argv[i]])
            #线上通过定时任务重新加载词典,这个8888接口是假的
            param["HTTP_RELOAD"].append("http://192.168.201.164:8888/ltp_reload") 
        else:
            ms_update = True
            ms_param["HOSTINFO"].append(setting.MAKE_LTP_BIN_SERVICE[sys.argv[i]])
            ms_param["HTTP_RELOAD"].append(setting.MAKE_LTP_BIN_RELOAD[sys.argv[i]])
            ms_param["PORT"] = setting.MS_PORT
    elif sys.argv[i] == "noUpdate":
        needUpdate = False
    elif sys.argv[i] == "fullUpdate":
        param["MODEL"] = "0"  
        ms_param["MODEL"] = "0"   

#默认更新164        
if len(param["HOSTINFO"]) == 0 and not ms_update:
    param["HOSTINFO"].append(setting.MAKE_LTP_BIN_SERVICE["164"])
    #这个是真的,测试环境正常reload
    param["HTTP_RELOAD"].append(setting.MAKE_LTP_BIN_RELOAD["164"])                 

#日志  发现bug时使用
writeStrToLog(makeJson(param))


#默认是增量式更新,从ontologydb.dicts_update_recode取得修改的dicts
if len(param["HOSTINFO"]) != 0:
    if param["MODEL"] == "1" and needUpdate:
        writeStrToLog("是增量式更新...")
        writeStrToLog("从ontologydb.dict_update_record读取增量修改内容...")
    #     updateList = updater.getAllUpdateInfo() #old
        updateDict = updater.getAllUpdateInfoNew() #new
        updateList = updater.getAllUpdateInfoFromDict(updateDict) #new
        writeStrToLog("从ontologydb.dict_update_record读取完毕...")
        param["delete"] = removeErrorDict(updateList["delete"])   
        param["update"] = removeErrorDict(updateList["update"])
    elif len(param["HOSTINFO"]) != 0:
        writeStrToLog("是全量更新...")

    json_str = makeJson(param)
    result = ''
    
    #print json_str
    writeStrToLog("JSON: " + json_str)
    
    writeStrToLog("Ltp URL = " + setting.MAKE_BIN_URL)
    writeStrToLog("Start posting json to Ltp Service.")
    result = post(setting.MAKE_BIN_URL, json_str)
    writeStrToLog("Post Ltp Completed.")
    print "\n\n"
    print "HTTP POST RETURN : " + result
    print "\n\n"
    writeStrToLog("HTTP POST RETURN : " +result);

#主搜索增量全量
if ms_update:
    if ms_param["MODEL"] == "1" and needUpdate:
        writeStrToLog("是增量式更新(主搜索)...")
        ms_updateDict = updater.getAllUpdateInfoNew(ms_update) #new
        ms_updateList = updater.getAllUpdateInfoFromDict(ms_updateDict) #new
        ms_param["delete"] = removeErrorDict(ms_updateList["delete"])   
        ms_param["update"] = removeErrorDict(ms_updateList["update"])
    else:
        writeStrToLog("是全量更新(主搜索)...")
        ms_param["DICT_SOURCE"] = setting.MS_DICT_SOURCE
        
    json_str = makeJson(ms_param)
    result = ''
    
    #print json_str
    writeStrToLog("JSON: " + json_str)
    
    writeStrToLog("Ltp URL = " + setting.MAKE_BIN_URL)
    writeStrToLog("Start posting json to Ltp Service.")
    result = post(setting.MAKE_BIN_URL, json_str)
    writeStrToLog("Post Ltp Completed.")
    print "\n\n"
    print "HTTP POST RETURN : " + result
    print "\n\n"
    writeStrToLog("HTTP POST RETURN : " +result);

if result.startswith('增量更新:') and (len(param["delete"])>0 or len(param["update"])>0) and needUpdate:
#     updater.deleteByLines(param["update"]) #old
#     updater.deleteByLines(param["delete"]) #old
    updater.deleteAllUpdateInfo(updateDict) #new
    #日志  发现bug时使用
    writeListToLog('delete',param["delete"])
    writeListToLog('update',param["update"])
    
    
    #164有变更调用新系统的回归测试
    if param["HTTP_RELOAD"][0].find("164")>=0 :
        fdout = open("test.out", 'w')
        fderr = open("test.err", 'w')
        sp = subprocess.Popen("bash ~/queryParser/scripts/dictDbToLtpBin/doSuccess.sh",shell=True,stdout=fdout, stderr=fderr)
        #os.system("cd ~/queryParser/scripts/dictDbToLtpBin;bash doSuccess.sh")

print("结束")