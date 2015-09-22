#!/usr/bin/env python
# -*- coding:utf-8 -*-

__status__ = 'Development'
__author__ = 'xuxiang <xuxiang@myhexin.com>'

import MysqlHelper
from MysqlHelper import AbstractDao
from UpdateRecode import UpdateRecode
import InheritDict
import TriggerUpdateTable
import re


########################################################################
#    OntoValue
########################################################################
class InheritDictUpdate(AbstractDao):
    
    DATA_LINES = 'update_data_lines'
    TRANS = 'update_dict_trans'
    ONTO_CLASS = 'update_dict_onto_class'
    ONTO_KEYWORD = 'update_dict_onto_keyword'
    ONTO_DATE = 'update_dict_onto_date'
    ONTO_TECHOP = 'update_dict_onto_techop'
    ONTO_TECHPERIOD = 'update_dict_onto_techperiod'
    ONTO_CHANGE = 'update_dict_onto_change'
    NO_VALUE = 'update_dict_no_value'
    ONTO_QWORD = 'update_dict_onto_qword'
    ONTO_TRIGGER = 'update_dict_onto_trigger'
    ONTO_LOGIC = 'update_dict_onto_logic'
    ONTO_SORT = 'update_dict_onto_sort'
    ONTO_SPECIAL = 'update_dict_onto_special'
    ONTO_VAGUENUM = 'update_dict_onto_vaguenum'
    UPDATE_TOTAL_NUM = 3000
    
    def __init__(self, dbconfig, db = None):
        AbstractDao.__init__(self, dbconfig, db)
        self.tableConfig = {
            'data_lines'   : InheritDict.OntoValue(None, db = self.db),
            'dict_trans'   : InheritDict.Trans(None, db = self.db),
            'dict_onto_class'   : InheritDict.OntoClass(None, db = self.db),
            'dict_onto_trigger' : InheritDict.OntoTrigger(None, db = self.db),
            'dict_no_value' : InheritDict.NullValue(None, db = self.db),
            'dict_onto_keyword' : InheritDict.OntoKeyword(None, db = self.db),
            'dict_onto_date' : InheritDict.OntoDate(None, db = self.db),
            'dict_onto_logic' : InheritDict.OntoLogic(None, db = self.db),
            'dict_onto_qword' : InheritDict.OntoQword(None, db = self.db),
            'dict_onto_sort' : InheritDict.OntoSort(None, db = self.db),
            'dict_onto_special' : InheritDict.OntoSpecial(None, db = self.db),
            'dict_onto_techOp' : InheritDict.OntoTechOp(None, db = self.db),
            'dict_onto_techPeriod' : InheritDict.OntoTechPeriod(None, db = self.db),
            'dict_onto_vagueNum' : InheritDict.OntoVagueNum(None, db = self.db),
            'dict_onto_change' : InheritDict.OntoChange(None, db = self.db),
            self.DATA_LINES   : TriggerUpdateTable.UpdateDataLines(None, db = self.db),
            self.TRANS : TriggerUpdateTable.UpdateDictTrans(None, db = self.db),
            self.ONTO_CLASS : TriggerUpdateTable.UpdateDictOntoClass(None, db = self.db),
            self.ONTO_KEYWORD : TriggerUpdateTable.UpdateDictOntoKeyword(None, db = self.db),
            self.ONTO_DATE : TriggerUpdateTable.UpdateDictOntoDate(None, db = self.db),
            self.ONTO_TECHOP : TriggerUpdateTable.UpdateDictOntoTechOp(None, db = self.db),
            self.ONTO_TECHPERIOD : TriggerUpdateTable.UpdateDictOntoTechPeriod(None, db = self.db),
            self.ONTO_CHANGE : TriggerUpdateTable.UpdateDictOntoChange(None, db = self.db),
            self.NO_VALUE : TriggerUpdateTable.UpdateDictNoValue(None, db = self.db),
            self.ONTO_QWORD : TriggerUpdateTable.UpdateDictOntoQword(None, db = self.db),
            self.ONTO_TRIGGER : TriggerUpdateTable.UpdateDictOntoTrigger(None, db = self.db),
            self.ONTO_LOGIC : TriggerUpdateTable.UpdateDictOntoLogic(None, db = self.db),
            self.ONTO_SORT : TriggerUpdateTable.UpdateDictOntoSort(None, db = self.db),
            self.ONTO_SPECIAL : TriggerUpdateTable.UpdateDictOntoSpecial(None, db = self.db),
            self.ONTO_VAGUENUM : TriggerUpdateTable.UpdateDictOntoVaguenum(None, db = self.db)}
    
    def getAllUpdateInfo(self):
        result = {}
        result.setdefault('delete', [])
        result.setdefault('update', [])
        
        select_sql = '''
        SELECT text, tableName FROM dict_update_recode
        '''
        rows = self.db.select( sql = select_sql, is_dict = True )
        
        for row in rows:
            row['text']=row['text'].strip()#徐祥的这个没有去除回车
            dict_db = self.tableConfig.get(row['tableName'])
            if dict_db is not None:
                values = dict_db.selectByText(row['text'])
                if values is None or len(values) == 0:
                    result.setdefault('delete', []).append(row['text'])
                else:
                    value = values[0]
                    result.setdefault('update', []).append(value)
        
        print "读到update: " + str(len(result['update'])) + "行"
        print "读到delete: " + str(len(result['delete'])) + "行"            
        return result
    
    '''
        函数返回结果的结构为key为表名,value中包含update和delete字典的字典,
        update_id是更新到的id(删除时使用),update_num是更新的总数(用于限制数量),如:
    {'update_dict_onto_techperiod': {'update': [], 'delete': [],'update_num':XX,'update_id':XX}, 
    'update_dict_onto_date': {'update': [], ...}
    '''
    def getAllUpdateInfoNew(self, ms_update=False):
        result = {}
        num = self.UPDATE_TOTAL_NUM
        tableList =[self.DATA_LINES, self.TRANS, self.ONTO_CLASS,
                    self.ONTO_KEYWORD, self.ONTO_DATE, self.ONTO_TECHOP,
                    self.ONTO_TECHPERIOD, self.ONTO_CHANGE, self.NO_VALUE,
                    self.ONTO_QWORD, self.ONTO_TRIGGER, self.ONTO_LOGIC,
                    self.ONTO_SORT, self.ONTO_SPECIAL, self.ONTO_VAGUENUM]
        
        if ms_update:
            tableList =[self.DATA_LINES, self.TRANS, self.ONTO_KEYWORD, 
                        self.ONTO_DATE, self.ONTO_TECHOP,self.ONTO_TECHPERIOD,
                        self.ONTO_CHANGE, self.NO_VALUE, self.ONTO_QWORD,
                        self.ONTO_TRIGGER, self.ONTO_LOGIC, self.ONTO_SORT, 
                        self.ONTO_SPECIAL, self.ONTO_VAGUENUM]
        
        for table in tableList:
            resultDict = self.tableConfig.get(table).getUpdateInfo(num, ms_update)
            update_num = resultDict['update_num']
            if update_num>0:
                result[table] = resultDict
                num = num - update_num
                if num==0:
                    return result
        return result
    
    def getAllUpdateInfoFromDict(self,tableResult):
        result = {}
        result.setdefault('delete', [])
        result.setdefault('update', [])
        for table,value in tableResult.items():
            update = value['update']
            for up in update:
                result.setdefault('update', []).append(up)
            delete = value['delete']
            for de in delete:
                result.setdefault('delete', []).append(de)
        return result
    
    def deleteByLines(self, lines):
        updateRecode = UpdateRecode(None, db = self.db)
        updateRecode.deleteByLines(lines)
    
    def deleteByLine(self, line):
        updateRecode = UpdateRecode(None, db = self.db)
        updateRecode.deleteByLine(line)
        
    def deleteAllUpdateInfo(self,tableResult):
        for table,value in tableResult.items():
            update_id = value['update_id']
            if update_id>0:
                update_dict_db = self.tableConfig.get(table)
                update_dict_db.deleteById(update_id)
    
    def compareLists(self,list1,list2):
        list1.sort()
        list2.sort()
        if cmp(list1,list2)==0:
            return True
        print 'list1:%s'%list1
        print 'list2:%s'%list2
        return False
    
    def compareResults(self,result1,result2):
        if self.compareLists(result1['update'],result2['update'])==False:
            return False
        if self.compareLists(result1['delete'],result2['delete'])==False:
            return False;
        
        return True

def test():
    ONTOLOGYDB_CONFIG = {
        'host' : "127.0.0.1",
        'port' : 3306,
        'db' : 'ontologydb',
        'user' : 'qnateam',
        'passwd' : 'qnateam'
    }    
    inheritDictUpdate = InheritDictUpdate(ONTOLOGYDB_CONFIG)
        
#     print '从update_record中获取更新信息:'
#     result = inheritDictUpdate.getAllUpdateInfo()
#     print '更新列表:%s'%result['update']
#     print '删除列表:%s'%result['delete'],'\n'
    
    print '从各个update表中获取更新信息:'
    resultNew = inheritDictUpdate.getAllUpdateInfoNew()
    print '所属表及更新删除列表:%s'%resultNew,'\n'
    
#     print '比较两种方式获取到的更新信息是否相同:'
#     resultNew2 = inheritDictUpdate.getAllUpdateInfoFromDict(resultNew)
#     compareResult = inheritDictUpdate.compareResults(result, resultNew2)
#     print '比较结果:%s'%compareResult,'\n'
    
#     print '使用两种方式删除更新信息'
#     inheritDictUpdate.deleteByLines(result['update'])
#     inheritDictUpdate.deleteByLines(result['delete'])
#      
#     inheritDictUpdate.deleteAllUpdateInfo(resultNew)
    
    pass

if __name__ == "__main__":
#     test()
    pass