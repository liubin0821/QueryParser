#!/usr/bin/env python
# -*- coding:utf-8 -*-

import psycopg2
import PostGreHelper
import sys
import os
import filecmp
import difflib
import setting
import GetOneDictLine
import FindTheChangedPart
import re

'''
install driver : 
    apt-get install python-psycopg2
    apt-get install python-dev
    pip install ujson
    pip install requests 
导出ifind的

'''
def getData(sql):
    """ 
        "172.20.205.108", 5432 , "guest" ,"guest_110", "postgres"
        Ifind database access.
        Returns:
            lines or None if error
    """
    postgre  = PostGreHelper.PostGreHelper()
    handle = postgre.open("172.20.205.108", 5432 , "guest" ,"guest_110", "postgres")
    return postgre.readlines(handle, sql)

def getBusinessNameFromSql():
    words = []
    type = "_业务名称"
    sql = """
    select distinct F002 from stk039
    where isvalid = '1'
    and f001 = '2'
    """
    data = getData(sql)
    if data is None:
        failExit("access postgresql error!")
    for item in data:
        if item.find('(') < 0 and item.find('（') < 0:
            for subProduct in re.split(",|，|;|；|、", item):
                words.append(subProduct.strip())
                #print subProduct.strip()
        else:
            words.append(item.strip())
            #print item.strip()


    sql = """
    select distinct products from
    (
      select f001V_stk219 as products, 
                   t1.declaredate_stk219 as valid_date,
                                max(t1.declaredate_stk219) over (partition by t1.orgid_stk219) as latest
                                    from STK219 t1 join stk001 t2 on t2.comcode = t1.ORGID_STK219 
                                    where t1.isvalid = 1 and t2.isvalid = 1 and f001V_stk219 not like '%B、C、D%'
    ) t where valid_date = latest """

    data = getData(sql)
    if data is None:
        failExit("access postgresql error!")
    for item in data:
        item = item.replace('  ', '')
        item = item.replace('　　', '')
        if item.find('(') < 0 and item.find('（') < 0:
            for product in re.split(",|，|;|；|、", item):
                words.append(product.strip())
                #print product.strip()
        else:
            for product in item.strip().split('、'):
                if product.find('(') < 0 and product.find('（') < 0:
                    for subProduct in re.split(",|，|;|；|、", product):
                        words.append(subProduct.strip())
                        #print subProduct.strip()
                else:
                    words.append(product.strip())
                    #print product.strip()
                 
    new_data = []                    
    for item in words:
        if item != "" and not isDigit(item.lower()):
             new_data.append(GetOneDictLine.getOneDictLine(item,type)) 
    return new_data

def getTrustProduct():
    
    new_data = []
    typeMap = {"银行理财产品":"_理财产品", "信托":"_信托产品"}
    sql = """
    select SEQ id,f003v_pub205,F016V_PUB205 full_name,SECNAME_PUB205 short_name
    from PUB205 
    where f003v_pub205 in ('银行理财产品','信托')
    """
    data = getData(sql)
    if data is None:
        failExit("access postgresql error!")        
    for item in data:
        cols=item.split('\t');
        id = ''
        type = ''
        for col in cols:
            if(col == cols[0]):
                id = col
                continue
            elif(col == cols[1]):
                type = typeMap[col]
                continue
            elif(id != '' and col != ''):
                new_data.append(GetOneDictLine.getOneDictLineWithId(col,type,id));  
    return new_data   
 
def getAnalystName():
    new_data = []
    type="_分析师姓名"
    sql = """
    select distinct t.f003v_yb002 from yb002 t where t.f003v_yb002 != ''
    """
    data = getData(sql)
    if data is None:
        failExit("access postgresql error!")        
    for line in data:
        new_data.append(GetOneDictLine.getOneDictLine(line,type));  
    return new_data
    
def getSecuritiesBusiness():
    new_data = []
    type="_研究机构"
    sql = """
    SELECT Y.ORGID_YB001 "id",P.ORGNAME_PUB203 "name",p.f022v_pub203 "short_name" FROM YB001 Y,PUB203 P 
    WHERE Y.ORGID_YB001 = P.ORGID_PUB203 
    AND Y.ISVALID = 1 
    AND P.ISVALID = 1
    """
    data = getData(sql)
    if data is None:
        failExit("access postgresql error!")        
    for line in data:
        cols=line.split('\t');
        if len(cols)==3:
            if cols[1] != '':
                new_data.append(GetOneDictLine.getOneDictLineWithId(cols[1],type,cols[0])); 
            if cols[2] != '':    
                new_data.append(GetOneDictLine.getOneDictLineWithId(cols[2],type,cols[0])); 
    return new_data
    
def isDigit(my_str):
    pattern = re.compile(r'^([\\d零一两二三四五六七八九十百千万亿萬仟佰拾玖捌柒陆伍叁贰壹]|肆)+$')
    match = pattern.match(my_str)
    if match:
        return True
    try:
        float(my_str)
    except ValueError:
        return False
    return True

    

def process():
    is_update = False
    data = getBusinessNameFromSql()
    is_update = (FindTheChangedPart.run(data,setting.BUSINESS_NAME_FILE) or is_update)
    data=[];
    
#     data = getAnalystName() 
#     is_update = (FindTheChangedPart.run(data,setting.ANALYST_NAME) or is_update)
#     data=[];
    
#     data = getSecuritiesBusiness() 
#     is_update = (FindTheChangedPart.run(data,setting.RESEARCH_INSTITUTE) or is_update)
#     data=[];
      
#     data = getTrustProduct() 
#     is_update = (FindTheChangedPart.run(data,setting.TRUST_PRODUCT_FILE) or is_update)
#     data=[]; 
    
    
    if is_update == True:
        os.system("bash " + setting.LTP_SERVER + " restart")
        print "over.."

if __name__ == '__main__':
    process()
