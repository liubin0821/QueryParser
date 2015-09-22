#!/usr/bin/env python
# -*- coding:utf-8 -*-


#toSimplified(toLowerAndHalf(value.lower()))
# 大写转小写 全角转半角   繁体转简体
def getOneDictLineWithId(value,type,id,from_where=''):
    return '#'+value.lower().strip()+'/*Cate=pretreat;Seg=;Value=onto_value:prop='+type+';infos=id:'+id+';from_where='+from_where+';$\n'  

def getOneDictLine(value,type,from_where=''):
    return '#'+value.lower().strip()+'/*Cate=pretreat;Seg=;Value=onto_value:prop='+type+';from_where='+from_where+';$\n'  

# 大写转小写 全角转半角   繁体转简体
def getOnePostDictLineWithId(value,type,id,from_where=''):
    return '#'+value.lower().strip()+'/*Cate=posttreat;Seg=;Value=onto_value:prop='+type+';infos=id:'+id+';from_where='+from_where+';$\n'  

def getOnePostDictLine(value,type,from_where=''):
    return '#'+value.lower().strip()+'/*Cate=posttreat;Seg=;Value=onto_value:prop='+type+';from_where='+from_where+';$\n'  


