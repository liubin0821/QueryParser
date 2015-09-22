#!/usr/bin/env python
# -*- coding:utf-8 -*-
import psycopg2
import logging
import types
import traceback
import urlparse
import sys
import re
'''
install driver : apt-get install python-psycopg2

'''
class PostGreHelper:
    
    def open(self, host_str, port_str, user_str, password_str, db_str):
        '''
        打开一个数据库连接
        '''
        try:
            conn = psycopg2.connect(host=host_str, port=port_str, user=user_str, password=password_str, database = db_str)
            logging.info("PostGre DB CONNECTION OK ....")
            return conn
        except:
            logging.error("PostGre connection wrong !!!") 
            logging.error(str(host_str )+ " " + str(port_str) + "\t" + str(user_str) + "\t" + str(password_str) + "\t" + str(db_str))
            logging.error(traceback.format_exc())
            return None
    

    def readlines(self, conn, command, need_count =-1):
        '''
        给定一个command 和一个connection, 执行select 相关的command 将结果放到一个数组lines里面.
        每一个元素是一条记录，每个field用\t分隔开
        如果指定了need_count而且need_count大于0，最多放回need_count个结果
        如果执行出错，返回None
        '''
        lines = []
        try:
            cursor = conn.cursor ()
            cursor.execute(command)
            lines = []
            count = 0
            while ( need_count < 0 or (need_count > 0 and  count <  need_count) ):
                line = ""
                row = cursor.fetchone()
                if row == None:
                    break ;
                for item in row:
                    item = str(item).strip()
                    item = re.sub("[\r\n\t]", " ", str(item))
                    line = line + "\t" + str(item)
                if line != "" :
                    line = line[1:]
                lines.append(line)
                count = count + 1 
            cursor.close()
            conn.commit()
        except:
            logging.error("PostGre read wrong! [%s]" %(command))
            logging.error(traceback.format_exc())
            return None
        return lines

    def write(self, conn, command):
        try:
            cursor = conn.cursor ()
            cursor.execute("set names utf8")
            cursor.execute(command)
            cursor.close()
            conn.commit()
            return cursor.rowcount

        except:
            logging.error("Writting error [%s] !" %(command) )
            logging.error(traceback.format_exc())
            return -1
        
    @classmethod
    def from_setting(cls,conf):
        host = conf.get("host")
        port = int(conf.get("port"))
        user = conf.get("user")
        password = conf.get("password")
        db = conf.get("db")
        instance=cls()
        return instance.open(host, port, user, password, db)    
    
    def close(self, conn):
        conn.close()


if __name__ == "__main__":
    postgre  = PostGreHelper()
    handle = postgre.open("172.20.205.108", 5432 , "guest" ,"guest_110", "postgres")
    sql = """SELECT  a.HUMANID_FUND108,  a.HUMANNAME_FUND108, b.F001C_PUB202 , b.F002V_PUB202, b.F004V_PUB202, b.F008V_PUB202 , b.f010v_pub202  from FUND108 AS  a , PUB202   AS b  WHERE  b.HUMANID_PUB202 = a.HUMANID_FUND108"""
    sql = """
select distinct 
       ORGNAME_PUB203 机构名称,
       F022V_PUB203   机构简称,
       ORGID_PUB203   机构id
 
  from pub205 p5, pub203 p3
 where p5.isvalid = 1
   and p3.isvalid = 1
   and f002v_pub205 like '002%'
   and F014V_PUB205 = ORGID_PUB203
   and F017V_PUB205 <>'213008'
   and F001C_PUB203='0'

    """
    sql = """
    SELECT F002V_PUB205,B.SECCODE_PUB205,A.F012V_PUB203,A.ORGNAME_PUB203,A.F022V_PUB203,B.SECNAME_PUB205,B.F016V_PUB205 FROM PUB203 A,PUB205 B
    WHERE B.F014V_PUB205 = A.ORGID_PUB203
    AND B.F020N_PUB205 = '1'
    AND B.F021N_PUB205 = '0'
    AND A.ISVALID = '1'
    AND B.ISVALID = '1'
    AND (F002V_PUB205 ='001003'
    OR F002V_PUB205 ='001005'
    OR F002V_PUB205 ='001006'
    OR F002V_PUB205 = '001007'
    OR F002V_PUB205 = '001008'
    OR F002V_PUB205 ='001009')
    """
    #AND(F002V_PUB205 = '001001'
    data = postgre.readlines(handle, sql)
    #001005  6888   www.axiata.com  Axiata Group Berhad Axiata Group Berhad Axiata  None
    for item in data:
        tup = item.split('\t')
        if tup[0] == '001003':
            tup[0] = "HKSTOCK"
        else:
            tup[0] = "USSTOCK"
        if tup[1] == 'None' or tup[2] == 'None':
            print "filter", item
            continue

        names = set(filter(lambda x : x != 'None', tup[3:]))
        print '\t'.join([tup[0],tup[1], '#'.join(names), tup[2]])

