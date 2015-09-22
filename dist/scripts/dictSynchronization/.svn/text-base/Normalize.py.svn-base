#!/usr/bin/env python
# -*- coding:utf-8 -*-

import MySQLdb

simplifiedMap = {}
isLoaded=False

#toSimplified(toLowerAndHalf(value.lower()))
# 大写转小写 全角转半角   繁体转简体

def normalize(text):
    return toSimplified(toLowerAndHalf(text.lower()))

def toLowerAndHalf(text):
    text=text.decode("utf-8")
    result = ''
    for i,ch in enumerate(text):
        if '！'.decode("utf-8") <= ch and ch <= '～'.decode("utf-8"):
            ch = chr( ord(ch) - (ord('！'.decode("utf-8"))-ord('!'.decode("utf-8"))))
        if 'A' <= ch and ch <= 'Z':
            ch = chr(ord(ch) + (ord('a') - ord('A')));
        elif '　'.decode("utf-8") == ch:
            ch = ' '; 
        result = result + ch
    return result.encode("utf-8")

        
def loadDictFromDb():
    if len(simplifiedMap)!=0 :
        return
    global isLoaded
    isLoaded = True
    db = MySQLdb.connect(host="172.20.201.147",port=3306,user="qnateam", passwd="qnateam", db="configFile",charset="utf8")
    cursor = db.cursor()
    cursor.execute("SELECT * FROM traditional_simplified_map")
    result = cursor.fetchall()
    for record in result:
        simplifiedMap[record[1]] = record[2]
    db.close()
    return simplifiedMap

def toSimplified(simplified):
    if not isLoaded :
        loadDictFromDb()
    simplified = simplified.decode("utf-8")
    if simplified == None:
        return None
    result = ""
    for i,ch in enumerate(simplified):
        temp = simplifiedMap.get(ch)
        if temp == None:
            result = result + ch
        else:
            result = result + temp
    return result.encode("utf-8")



