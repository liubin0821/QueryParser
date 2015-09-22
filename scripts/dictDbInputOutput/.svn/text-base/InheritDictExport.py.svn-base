#!/usr/bin/env python
# -*- coding:utf-8 -*-

__status__ = 'Development'
__author__ = 'xuxiang <xuxiang@myhexin.com>'

import InheritDict
from MysqlHelper import AbstractDao



class InheritDictExport(AbstractDao):
    '''导出所有字典信息'''
    
    def __init__(self, dbconfig, db = None):
        AbstractDao.__init__(self, dbconfig, db)
        self.tableConfig = [
            InheritDict.OntoValue(None, db = self.db),
            InheritDict.Trans(None, db = self.db),
            InheritDict.OntoTrigger(None, db = self.db),
            InheritDict.OntoClass(None, db = self.db),
            InheritDict.OntoKeyword(None, db = self.db),
            InheritDict.NullValue(None, db = self.db),
            InheritDict.OntoLogic(None, db = self.db),
            InheritDict.OntoSort(None, db = self.db),
            InheritDict.OntoDate(None, db = self.db),
            InheritDict.OntoTechOp(None, db = self.db),
            InheritDict.OntoChange(None, db = self.db),
            InheritDict.OntoSpecial(None, db = self.db),
            InheritDict.OntoQword(None, db = self.db),
            InheritDict.OntoVagueNum(None, db = self.db),
            InheritDict.OntoTechPeriod(None, db = self.db)
        ]
        self.tableMSConfig = [
            InheritDict.OntoValue(None, db = self.db),
            InheritDict.Trans(None, db = self.db),
            InheritDict.OntoTrigger(None, db = self.db),
            InheritDict.OntoKeyword(None, db = self.db),
            InheritDict.NullValue(None, db = self.db),
            InheritDict.OntoLogic(None, db = self.db),
            InheritDict.OntoSort(None, db = self.db),
            InheritDict.OntoDate(None, db = self.db),
            InheritDict.OntoTechOp(None, db = self.db),
            InheritDict.OntoChange(None, db = self.db),
            InheritDict.OntoSpecial(None, db = self.db),
            InheritDict.OntoQword(None, db = self.db),
            InheritDict.OntoVagueNum(None, db = self.db),
            InheritDict.OntoTechPeriod(None, db = self.db)
        ]
    
    def queryAllDicts(self):
        result = []
        
        for dict_db in self.tableConfig:
            result.extend(dict_db.selectAll())
        self.db.close()    
        
        return result
    
    def queryMSDicts(self):
        result = []
        
        for dict_db in self.tableMSConfig:
            result.extend(dict_db.selectAll(ms_up=True))
        self.db.close()
        
        return result
    
    # 根据类型，查询出那些词属于该类，如果有重复，未去重
    # 使用用例 queryByTypes('A', 'B', ...)
    def queryByTypes(self, *types):
        select_sql = '''
        SELECT TEXT FROM data_lines WHERE type_id IN (SELECT id FROM data_type WHERE label IN (%s))
        ''' % (','.join('%s' for v in types))
        rows = self.db.select(select_sql, args = types, is_dict = False)
        result = []
        for row in rows:
            result.append(row[0])
        return result
    
    def exportToFile(self, outFilePath):
        result = self.queryAllDicts()
        out = open(outFilePath, 'w+')
        for line in result:
            out.write(line + '\n')
        out.flush()
        out.close()
if __name__ == "__main__":
#     ONTOLOGYDB_CONFIG = {
#         'host' : "172.20.201.147",
#         'port' : 3306,
#         'db' : 'ontologydb',
#         'user' : 'qnateam',
#         'passwd' : 'qnateam'
#     }    
#     inheritDictExport = InheritDictExport(ONTOLOGYDB_CONFIG)
#     print inheritDictExport.queryByTypes('_初级产品')
    pass
    