#!/usr/bin/env python
# -*- coding:utf-8 -*-

__status__ = 'Development'
__author__ = 'xuxiang <xuxiang@myhexin.com>'

import re
import MysqlHelper
from MysqlHelper import AbstractDao



class UpdateRecode(AbstractDao):
    __tableName = 'dict_update_recode'
    
    def __init__(self, dbconfig, db = None):
        AbstractDao.__init__(self, dbconfig, db)
        self.tableConfig = {
            'Value=onto_value:prop'     : 'data_lines',
            'Value=trans'               : 'dict_trans',
            'Value=onto_trigger:prop'   : 'dict_onto_trigger',
            'Value=onto_class'          : 'dict_onto_class',
            'Value=onto_keyword'        : 'dict_onto_keyword',
            'Value=;'                   : 'dict_no_value',
            'Value=onto_logic'          : 'dict_onto_logic',
            'Value=onto_sort'           : 'dict_onto_sort',
            'Value=onto_date'           : 'dict_onto_date',
            'Value=onto_techOp'         : 'dict_onto_techOp',
            'Value=onto_change'         : 'dict_onto_change',
            'Value=onto_special'        : 'dict_onto_special',
            'Value=onto_qword'          : 'dict_onto_qword',
            'Value=onto_vagueNum'       : 'dict_onto_vagueNum',
            'Value=onto_techPeriod'     : 'dict_onto_techPeriod'}
    
    def deleteByLines(self, lines):
        for line in lines:
            self.deleteByLine(line)
    
    def deleteByLine(self, line):
        text = None
        textP = re.search('#(.*)/\*', line)
        if textP is None:
            text = line
        else:
            text = textP.group(1)
        text=text.strip()
#         tableName = None
#         for k in self.tableConfig:
#             if line.find(k) != -1:
#                 tableName = self.tableConfig[k]
                
        try:
#             if tableName is not None:
                self.db.delete(self.__tableName, {'text':text})
                self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()
    
if __name__ == "__main__":
#     ONTOLOGYDB_CONFIG = {
#         'host' : "172.20.201.147",
#         'port' : 3306,
#         'db' : 'ontologydb',
#         'user' : 'qnateam',
#         'passwd' : 'qnateam'
#     }    
#     testObj = UpdateRecode(ONTOLOGYDB_CONFIG)
#     print testObj.deleteByLines(['李科', 'sadf'])
    pass