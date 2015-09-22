#!/usr/bin/env python
# -*- coding:utf-8 -*-

import MySQLdb
import types
import traceback
import urlparse
import sys
import re
#from common.logger import log

class MySqlClient:

    def __init__(self, host, port, user, password, db):
        self.host = host
        self.port = port
        self.user = user
        self.password = password
        self.db = db
        self.conn = self.open()
    
    def open(self):
        '''
        打开一个数据库连接
        '''
        try:
            #log.info("MYSQL DB attempt to connect ....")
            conn = MySQLdb.connect(host=self.host, port=self.port, user=self.user, passwd=self.password, db=self.db)#reconnect=1)
            self.conn = conn
            self.write("set names utf8")

            #log.info("MYSQL DB CONNECTION OK ....")
            return conn
        except:
            #log.error("MySQL connection wrong !!!") 
            #log.error(str(self.host)+ " " + str(self.port) + "\t" + str(self.user) + "\t" + str(self.password) + "\t" + str(self.db))
            #log.error(traceback.format_exc())
            return None
    
    def reopen(self):
        '''
        重新打开一个数据库连接
        '''
        try:
            #log.info("MYSQL DB attempt to reconnect ....")
            conn = MySQLdb.connect(host=self.host, port=self.port, user=self.user, passwd=self.password, db=self.db)#reconnect=1)
            self.conn = conn
            self.write("set names utf8")
            #log.info("MYSQL DB RECONNECTION OK ....")
            return conn
        except:
            #log.error("MySQL reconnection wrong !!!") 
            #log.error(str(self.host)+ " " + str(self.port) + "\t" + str(self.user) + "\t" + str(self.password) + "\t" + str(self.db))
            #log.error(traceback.format_exc())
            return None
        
    @classmethod
    def from_setting(cls, mysql_setting):
        host = mysql_setting.get("host")
        port = int(mysql_setting.get("port"))
        user = mysql_setting.get("user")
        password = mysql_setting.get("password")
        db = mysql_setting.get("db")
        return cls(host, port, user, password, db)
    
    
     
    def readlines(self, conn, command, need_count =-1):
        '''
        给定一个command 和一个connection, 执行select 相关的command 将结果放到一个数组lines里面.
        每一个元素是一条记录，每个field用\t分隔开
        如果指定了need_count而且need_count大于0，最多放回need_count个结果
        如果执行出错，返回None
        '''
        #conn.ping()
        lines = []
        try:
            cursor = conn.cursor()
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
                    item = re.sub("\t", " ", str(item))
                    line = line + "\t" + str(item)
                    
                if line != "" :
                    line = line[1:]
                lines.append(line)
                count = count + 1 
            cursor.close()
            conn.commit()
            #print lines
        except:
            #log.error("MySQL read wrong! [%s]" %(command))
            #log.error(traceback.format_exc())
            return None
        return lines

    def setUTF8(self, conn):
        return self.write(conn,"set names utf8")

    def writeRetry(self, command, times = 1):
        while times > 0:
            times -= 1
            result = self.write(command)
            if result >= 0:
                return result
            else:
                #log.info("write failed, reopen mysql.")
                self.reopen()
        return -1


    def write(self, command):
        try:
            #conn.ping()
            cursor = self.conn.cursor ()
            cursor.execute("set names utf8")
            cursor.execute(command)
            cursor.close()
            self.conn.commit()
            return cursor.rowcount

        except:
            #log.error("Writting error [%s] !" %(command) )
            #log.error(traceback.format_exc())
            return -1
    

    def close(self, conn):
        conn.close()
    #def close(self):
        #self.conn.close()

if __name__ == "__main__":
    pass
 
