#!/usr/bin/env python
# -*- coding:utf-8 -*-

__status__ = 'Development'
__author__ = 'xuxiang <xuxiang@myhexin.com>'

import MysqlHelper
from MysqlHelper import AbstractDao



class InheritType(AbstractDao):
    
    def insert(self, label):
        try:
            self.db.insert_ignore( 'data_type', {'label' : label} )
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()
    
    def selectByLabel(self, label):
        select_sql = """
        SELECT id, label FROM data_type WHERE label = %s
        """
        rows = self.db.select(select_sql, args = label, is_dict = True )
        
        if rows is None or len(rows) == 0:
            return None
        return rows[0]
    
    def selectAll(self):
        select_sql = '''
        SELECT id, label FROM data_type
        '''
        
        return self.db.select( sql = select_sql, is_dict = True )
    
    # 返回一个集合(set)  
    def selectParentIdsById(self, id, hisIds = {}):
  
        if id in hisIds:
            return hisIds.get(id)[1]
        
        select_sql = '''
        SELECT super_id, type_id
        FROM super_type
        WHERE super_id IS NOT NULL AND type_id IS NOT NULL
        AND type_id = %s
        '''
        
        parentRows = self.db.select( sql = select_sql, args = (id), is_dict = True)
        
        if parentRows is None or len(parentRows) == 0:
            return list()
        
        hisIds[id] = [set(),list()]    
        for parentRow in parentRows:
            parentId = parentRow.get('super_id')
            if(not hisIds[id][0].__contains__(parentId)):
                hisIds[id][0].add(parentId)
                hisIds[id][1].append(parentId)
            ppids = self.selectParentIdsById(parentId, hisIds)
        
        #item [set(),list()]        
        for typeId in ppids:
            if(not hisIds[id][0].__contains__(typeId)):
                hisIds[id][0].add(typeId)
                hisIds[id][1].append(typeId)
                
        return hisIds.get(id)[1]
    
    def selectAllWithParentIds(self):
        '''
                        返回 {id, {label, pids}}
        '''
        hisIds = {}
        types = self.selectAll()
        result = {}
        for type in types:
            result[type['id']] = {'label':type['label']}
            result[type['id']]['pids'] = self.selectParentIdsById(id = type['id'], hisIds = hisIds)
        
        return result
        
if __name__ == "__main__":
#     ONTOLOGYDB_CONFIG = {
#         'host' : "172.20.201.147",
#         'port' : 3306,
#         'db' : 'ontologydb',
#         'user' : 'qnateam',
#         'passwd' : 'qnateam'
#     }    
#     testObj = InheritType(ONTOLOGYDB_CONFIG)
#     print testObj.selectAllWithParentIds()
    pass