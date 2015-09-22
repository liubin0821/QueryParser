#-*-coding:utf-8-*-
#!/usr/bin/env python2.6

import sys
import traceback
import time
import os
import re

from mysql import MySqlClient

ENV = "PROD"
#主搜索admin地址
MS_MYSQL_SERVERS = {
        "PROD":{'host':"192.168.201.147", 'port':3306, 'db':'other_admin', 'user':'qnateam', 'password':'qnateam'},
        "TEST":{'host':"192.168.23.52", 'port':3306, 'db':'other_admin', 'user':'qnateam', 'password':'qnateam'},
}
MS_MYSQL = MS_MYSQL_SERVERS[ENV]

def readFromMysql():
    mysqlClient = MySqlClient.from_setting(MS_MYSQL)

    sql = """
    SELECT type_label, ms_only, ms_ignore FROM `ms_props` 
    """
    data = mysqlClient.readlines(mysqlClient.conn, sql)
    return data

def load_list():
    data = readFromMysql()
    ignore_list = set([])
    only_list = set([])
    if data <> None:
        for row in data:
            (type_label, ms_only, ms_ignore) = row.strip().split('\t')
            if ms_only == '1':
                only_list.add(type_label)
            if ms_ignore == '1':
                ignore_list.add(type_label)
    return list(ignore_list), list(only_list)