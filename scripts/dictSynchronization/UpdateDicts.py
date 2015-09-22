#!/usr/bin/python2.6  
# -*- coding: utf-8 -*-
import urllib2
import sys
import re
import os
import filecmp
import difflib
import time
import setting
import FindTheChangedPart

def load_stop_words(fileName):
    stop_words = []
    fr = open(fileName)
    for line in fr.readlines():
        stop_words.append(line.strip().strip('\n'))
    #print stop_words
    return stop_words

def makedir(dir):
    new_dir = dir
    if os.path.isdir(new_dir) == False:
        os.makedirs(new_dir)

def read_conf(domain, conf_path):
    updates = "update_dicts"
    dicts_map = {}
    with open(conf_path+'/ltp.conf.'+domain+'.update','r') as conf:
        for line in conf:
            temp_line = line.strip().strip('\n').split('=')
            if len(temp_line) == 2:
                key = temp_line[0].strip()
                value = temp_line[1].strip()
                if dicts_map.has_key(key):
                    dicts_map[key].append(value)
                else:
                    result = []
                    result.append(value)
                    dicts_map[key] = result
    return dicts_map

def get_new_sys_word(line, stop_tokens, stop_words):
    if line in stop_tokens:
        return ""
    p = re.compile('#(.+)/\*.+:(prop=)(.+?);.*\$');
    p_no_colon = re.compile('#(.+)/\*.+(prop=)(.+?);.*\$');
    p_top_n = re.compile('((前.+%)|(前百分之.+)|(前(五|十)))');
    match = p.search(line);
    if not match:
        match = p_no_colon.search(line)
    if match:
        key = match.group(1);
        value = match.group(0);
        prop = match.group(3);
        if p_top_n.search(key):
            return ""
        if key in stop_words:
            return ""
        if prop and prop.find("_所属概念")>=0 :#该类型不同步
            return ""         
        if prop and (prop.find("_高管生日")>=0 or prop.find("_董事长生日")>=0):
            value = value.replace(prop, '_生肖')
        if prop and (prop.find("_高管毕业学校")>=0 or prop.find("_董事长毕业学校")>=0):
            value = value.replace(prop, '_学校')
        if prop and (prop.find("_高管学历")>=0 or prop.find("_董事长学历")>=0):
            value = value.replace(prop, '_学历')
        if prop and (prop.find("_高管国籍")>=0 or prop.find("_董事长国籍")>=0):
            value = value.replace(prop, '_国家')
        if prop and (prop.find("_高管婚姻状况")>=0 or prop.find("_董事长婚姻状况")>=0):
            value = value.replace(prop, '_婚姻状况')    
        if prop and (prop.find("_高管姓名")>=0 or prop.find("_董事长姓名")>=0):
            value = value.replace(prop, '_姓名')
        return value;
    return line

def download_domain(domain, dicts_map, ltp_path):
    updates = "update_dicts"
    req = urllib2.Request('http://172.20.0.52/zfontology/interface/script/synwordOutputNew.php?dic=all&env=Prod&type='+domain);
    response = urllib2.urlopen(req);
    data = response.read();
    map={}

    stop_tokens = load_stop_words(ltp_path+"/"+"stop_tokens.txt");
    stop_words = load_stop_words(ltp_path+"/"+"stop_words.txt")
    p = re.compile('(.+\.dict).+(#.+;\$)');

    for line in data.split('\n'):    
        item=line.split('\t');
        if len(item) == 2:
            key = item[0];
            value = item[1];
            value = get_new_sys_word(value, stop_tokens, stop_words)
            if value == "":
                #line = f.readline();
                continue
            if map.has_key(key):
                map[key].append(value+"\r\n");
            else:
                result = []
                result.append(value+"\r\n")
                map[key] = result
    return map            


def write_to_file(list,file_name):
    out = open(file_name, "wb");
    for value in list:
        out.write(value);
    out.close;
    
def update_domain(domain, ltp_path, conf_path):
    is_update = False
    updates = "update_dicts"
    map = {} #全局词典
    dicts_map = read_conf(domain, conf_path);
    map=download_domain(domain, dicts_map, ltp_path)
    for value in dicts_map[updates]:
        if map.has_key(value):
            is_update = FindTheChangedPart.run(map[value],value) or is_update       
            
    print "update success: " + domain
    return is_update

ltp_path = setting.LTP_PATH
conf_path = ltp_path+"/conf"	

#makedir(temp_path)
#makedir(backup_path)

if len(sys.argv) == 1:
    is_update1 = update_domain("stock", ltp_path, conf_path);
    is_update2 = update_domain("fund", ltp_path, conf_path);
    is_update3 = update_domain("hkstock", ltp_path, conf_path);
    if is_update1 or is_update2 or is_update3:
        os.system("bash " + setting.LTP_SERVER + " restart")
        print 'over'
else:
    temp = sys.argv[1];
    if [ temp == "stock" or temp == "fund" or temp == "hkstock"]:
        is_update = update_domain(temp, ltp_path, conf_path);
        if is_update:
            os.system("bash " + setting.LTP_SERVER + " restart")
    elif [temp == "fina" or temp == "trust" or temp == "search" or temp == "all" or temp == "larq" or temp == "concept" or temp == "indus" or temp == "sector" or temp == "region" or temp == "bond" or temp == "person" or temp == "report" or temp == "fundmanager" ]:
        print "No support: "+temp;
    else:
        print "Bad argument: "+temp;