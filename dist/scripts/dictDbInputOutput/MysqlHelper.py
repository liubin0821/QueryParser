#!/usr/bin/env python
# -*- coding:utf-8 -*-

'''Implements a simple database interface

Example 0: Create connection:
dbconfig = {
    'host' : "172.20.201.147",
    'port' : 3306,
    'db' : 'ontologydb',
    'user' : 'qnateam',
    'passwd' : 'qnateam'
}
    # Set auto commit to false
    db = DB(dbconfig, False)

Example 1: Select SQL

a. Select the first two rows from ip table:

    # normal select
    db.select('select * from ip limit 2')
    # add a where condition:
    db.select('select * from ip where name != %s limit 2', ('0'))

b. Select all results but get only the first two:

    db.execute('select * from ip')
    # get dict rows
    db.get_rows(2, is_dict = True)

Example 2: Insert/Replace SQL

a. Insert a new record into ip table:

    db.insert('ip', {'address':'192.168.0.1', 'name': 'vm-xxx'})
    db.commit()

b. Insert multi-records into ip table:

    db.multi_insert('ip', ('address','name'), [('192.168.0.1', 'vm-xxx'),
        ('192.168.0.2', 'vm-yyy'), ('192.168.0.3', 'vm-zzz')])
    db.commit()

Example 3: Update SQL

a. Update the address of row whose name is vm-xxx:

    db.update('ip', {'address':'192.168.0.1'}, {'name': 'vm-xxx'})
    db.commit()

Example 4: Delete SQL

a. Delete the row whose name is 'vm-xxx':

    db.delete('ip', {'name': 'vm-xxx'})
    db.commit()
'''

# Can be 'Prototype', 'Development', 'Product'
__status__ = 'Development'
__author__ = 'xuxiang <xuxiang@myhexin.com> from http://www.sharejs.com/codes/python/5320'

import sys
import MySQLdb


class DB:
    '''A simple database query interface.'''
    def __init__(self, dbconfig, auto_commit = False):
        if 'charset' not in dbconfig:
            dbconfig['charset'] = 'utf8'
            
        if 'init_command' not in dbconfig:
            dbconfig['init_command'] = 'set names utf8'

        self.conn = MySQLdb.connect(
            host = dbconfig['host'],
            port = dbconfig['port'],
            user = dbconfig['user'],
            passwd = dbconfig['passwd'],
            db = dbconfig['db'],
            charset = dbconfig['charset'],
            init_command = dbconfig['init_command'])
        
        self.cursor = self.conn.cursor()
        self.autocommit(auto_commit)

    def execute(self, sql, args = None):
        self.cursor = self.conn.cursor()
        return self.cursor.execute(sql, args)

    def executemany(self, sql, args):
        '''Execute a multi-row query.'''
        return self.cursor.executemany(sql, args)

    def select(self, sql, args = None, size = None, is_dict = False):
        self.execute(sql, args)
        return self.get_rows(size, is_dict)

    def insert(self, table, column_dict):
        keys = '`,`'.join(column_dict.keys())
        values = column_dict.values()
        placeholder = ','.join([ '%s' for v in column_dict.values() ])
        ins_sql = 'INSERT INTO %(table)s (`%(keys)s`) VALUES (%(placeholder)s)'

        return self.execute(ins_sql % locals(), values)
    
    # 忽视重复的
    def insert_ignore(self, table, column_dict):
        keys = '`,`'.join(column_dict.keys())
        values = column_dict.values()
        placeholder = ','.join([ '%s' for v in column_dict.values() ])
        ins_sql = 'INSERT IGNORE INTO %(table)s (`%(keys)s`) VALUES (%(placeholder)s)'

        return self.execute(ins_sql % locals(), values)
    
    # 更新重复的
    def insert_duplicate(self, table, column_dict, update_list):
        keys = '`,`'.join(column_dict.keys())
        values = column_dict.values()
        placeholder = ','.join([ '%s' for v in column_dict.values() ])
        updateholder = ','.join([ '%s=VALUES(%s)' % (v, v) for v in update_list ])
        ins_sql = '''
        INSERT IGNORE INTO %(table)s (`%(keys)s`) VALUES (%(placeholder)s)
        ON DUPLICATE KEY UPDATE %(updateholder)s
        '''

        return self.execute(ins_sql % locals(), values)

    def multi_insert(self, sql, args):
        '''Execute a multi-row insert, the same as executemany'''
        return self.cursor.executemany(sql, args)

    def replace(self, table, column_dict):
        keys = '`,`'.join(column_dict.keys())
        values = column_dict.values()
        placeholder = ','.join([ '%s' for v in column_dict.values() ])
        repl_sql = 'REPLACE INTO %(table)s (`%(keys)s`) VALUES (%(placeholder)s)'

        return self.execute(repl_sql % locals(), values)

    def update(self, table, column_dict, cond_dict):
        set_stmt = ','.join([ '%s=%%s' % k for k in column_dict.keys() ])
        cond_stmt = ' AND '.join([ '%s=%%s' % k for k in cond_dict.keys() ])
        args = column_dict.values() + cond_dict.values()
        upd_sql = 'UPDATE %(table)s set %(set_stmt)s where %(cond_stmt)s'

        return self.execute(upd_sql % locals(), args)

    def delete(self, table, cond_dict):
        cond_stmt = ' AND '.join([ '%s=%%s' % k for k in cond_dict.keys() ])
        del_sql = 'DELETE FROM %(table)s where %(cond_stmt)s'
        return self.execute(del_sql % locals(), cond_dict.values())

    def get_rows(self, size = None, is_dict = False):
        if size is None:
            rows = self.cursor.fetchall()
        else:
            rows = self.cursor.fetchmany(size)

        if rows is None:
            rows = []

        if is_dict:
            dict_rows = []
            dict_keys = [ r[0] for r in self.cursor.description ]

            for row in rows:
                dict_rows.append(dict(zip(dict_keys, row)))

            rows = dict_rows

        return rows

    def get_rows_num(self):
        return self.cursor.rowcount

    def get_mysql_version(self):
        MySQLdb.get_client_info()

    def autocommit(self, flag):
        self.conn.autocommit(flag)

    def commit(self):
        '''Commits the current transaction.'''
        self.conn.commit()
        
    def rollback(self):
        '''Rollback the current transaction.'''
        self.conn.rollback()

    def __del__(self):
        #self.commit()
        self.close()

    def close(self):
        self.cursor.close()
        self.conn.close()
        
class AbstractDao: 
    def __init__(self, dbconfig, db = None):
        if db is not None:
            self.db = db
        else:
            self.db = DB( dbconfig )