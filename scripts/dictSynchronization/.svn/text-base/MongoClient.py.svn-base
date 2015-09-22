#! /usr/bin/env python 
# -*- coding:utf-8 -*-
import pymongo
import re
import json
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

class MongoClient:
    
    def __init__(self, host, port, user, password, db):
        self.conn = pymongo.Connection(host,port,slave_okay=True)
        self.db=self.conn[db]
        #self.db.authenticate(user,password)
        #self.collection = db_auth['Fundfullname']
    
    @classmethod
    def from_setting(cls, setting):
        host = setting.get("host")
        port = int(setting.get("port"))
        user = setting.get("user")
        password = setting.get("password")
        db = setting.get("db")
        return cls(host, port, user, password, db)
    
        
    def read(self,table,sql):
        collection = self.db[table]
        data = []
        sql_map=json.loads(sql)
        rows = collection.find({},sql_map)
        key_id = re.compile('{"(.*?)":1').match(sql).group(1)
        
        if rows is None :
            return data
        
        for row in rows:
            line='\t'
            if row.has_key(key_id):
                line=line+re.sub('\t', ' ',str(row[key_id]).strip())
                
            for key in row.keys():#遍历字典
                if key==key_id :
                    continue
                item=re.sub('\t', ' ',str(row[key]).strip())
                line = line + '\t' + str(item)
                
            line = line[1:]
            data.append(line)
        return data


def main():
    client=MongoClient.from_setting({'host':"172.20.23.51", 'port':27018, 'db':'OntologyData', 'user':'admin', 'password':'admin'})
    data=client.read('Fundfullname','')  

if __name__ == "__main__":
    main()
    pass  

    
    