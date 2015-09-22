#!/usr/bin/env python
# -*- coding: utf8 -*-

import os
import time
from fabric.api import local,run, env, settings,cd,hide
from fabric.contrib.files import exists
from fabric.operations import get 
from fabric.operations import put
import fabfile_setting

env.user = fabfile_setting.DEST_INFO["user"]
env.password = fabfile_setting.DEST_INFO["password"]
env.hosts = fabfile_setting.DEST_INFO["hosts"]

def _get_user_home():
    with settings(hide("running","stdout","stderr")):
        home = run('echo $HOME')
    return str(home)

def __backup__(dir_or_file):
    backup_dir = _get_user_home() + '/backup_dir'
    bn = os.path.basename(dir_or_file)
    update_time = time.strftime("%Y%m%d%H%M%S")
    if not exists(backup_dir):
        run('mkdir -p %s' % backup_dir)
    if exists("%s/%s.bk" % (backup_dir,bn)):
        run('rm -rf %s/%s.bk' % (backup_dir,bn))
    run('mv %s %s/%s.bk%s' % (dir_or_file,backup_dir,bn,update_time))

def __update__(src,dst):
    if exists(dst):
        __backup__(dst)
    dst = os.path.dirname(dst)
    put(src,dst)

def update_dicts():
    '''update dicts'''
    __update__(fabfile_setting.SRC_INFO["ltp"]+"/dicts",fabfile_setting.DEST_INFO["ltp"]+"/dicts")

def update_dict_conf():
    '''update dict conf'''
    __update__(fabfile_setting.SRC_INFO["ltp"]+"/conf",fabfile_setting.DEST_INFO["ltp"]+"/conf")

def update_data():
    '''update data'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/data",fabfile_setting.DEST_INFO["service"]+"/data")
    
def update_data_stock():
    '''update data stock'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock",fabfile_setting.DEST_INFO["service"]+"/data/stock")

def update_dicts_back_stage_conf():
    '''update dicts stock_index.dict stock_index_trans.dict stock_keyword.dict'''
    __update__(fabfile_setting.SRC_INFO["ltp"]+"/dicts/stock_index.dict",fabfile_setting.DEST_INFO["ltp"]+"/dicts/stock_index.dict")
    __update__(fabfile_setting.SRC_INFO["ltp"]+"/dicts/stock_index_trans.dict",fabfile_setting.DEST_INFO["ltp"]+"/dicts/stock_index_trans.dict")
    __update__(fabfile_setting.SRC_INFO["ltp"]+"/dicts/stock_keyword.dict",fabfile_setting.DEST_INFO["ltp"]+"/dicts/stock_keyword.dict")

def update_data_stock_back_stage_conf():
    '''update data stock stock_onto.xml stock_onto_condition.xml stock_phrase_indexgroup.xml stock_phrase_keywordgroup.xml stock_phrase_syntactic.xml stock_phrase_semantic.xml '''
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_onto.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_onto.xml")
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_onto_condition.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_onto_condition.xml")
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_phrase_indexgroup.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_phrase_indexgroup.xml")
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_phrase_keywordgroup.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_phrase_keywordgroup.xml")
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_phrase_syntactic.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_phrase_syntactic.xml")
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_phrase_semantic.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_phrase_semantic.xml")

def update_data_stock_index():
    '''update data stock stock_onto.xml'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_onto.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_onto.xml")

def update_dicts_index():
    '''update dicts stock_index.dict stock_index_trans.dict'''
    __update__(fabfile_setting.SRC_INFO["ltp"]+"/dicts/stock_index.dict",fabfile_setting.DEST_INFO["ltp"]+"/dicts/stock_index.dict")
    __update__(fabfile_setting.SRC_INFO["ltp"]+"/dicts/stock_index_trans.dict",fabfile_setting.DEST_INFO["ltp"]+"/dicts/stock_index_trans.dict")

def update_data_stock_indexgroup():
    '''update data stock_phrase_indexgroup.xml'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_phrase_indexgroup.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_phrase_indexgroup.xml")

def update_data_stock_keywordgroup():
    '''update data stock_phrase_keywordgroup.xml'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_phrase_keywordgroup.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_phrase_keywordgroup.xml")

def update_dicts_keyword():
    '''update dicts stock_index.dict stock_index_trans.dict'''
    __update__(fabfile_setting.SRC_INFO["ltp"]+"/dicts/stock_keyword.dict",fabfile_setting.DEST_INFO["ltp"]+"/dicts/stock_keyword.dict")

def update_data_stock_synt():
    '''update data stock_phrase_syntactic.xml'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_phrase_syntactic.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_phrase_syntactic.xml")

def update_data_stock_semantic():
    '''update data stock_phrase_semantic.xml'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/stock_phrase_semantic.xml",fabfile_setting.DEST_INFO["service"]+"/data/stock/stock_phrase_semantic.xml")
    
def update_lightparser_conf():
    '''update lightparser conf'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/conf",fabfile_setting.DEST_INFO["service"]+"/conf")

def update_lightparser_lib():
    '''update lightparser lib'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/lib",fabfile_setting.DEST_INFO["service"]+"/ThirdParty/lib")

def update_queryparser_jar():
    '''update queryParser.jar'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/QueryParser-1.0.0.jar",fabfile_setting.DEST_INFO["service"]+"/lib/QueryParser-1.0.0.jar")
	
def update_data_stock_file(file):
    '''update file'''
    __update__(fabfile_setting.SRC_INFO["service"]+"/data/stock/"+file,fabfile_setting.DEST_INFO["service"]+"/data/stock/"+file)

def update_dict_file(file):
    '''update dict'''
    __update__(fabfile_setting.SRC_INFO["ltp"]+"/dicts/"+file,fabfile_setting.DEST_INFO["ltp"]+"/dicts/"+file)

def restart_ltp():
    '''restart ltp service'''
    with cd(fabfile_setting.SRC_INFO["ltp"]+"/"):
        run("bash ltp-server.sh restart")

def restart_lightparser():
    '''restart lightparser'''
    with cd("~/lightparser/scripts/"):
        run("bash stop.sh;setsid bash start.sh")