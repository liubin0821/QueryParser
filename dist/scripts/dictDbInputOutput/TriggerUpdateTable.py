#!/usr/bin/env python
# -*- coding:utf-8 -*-

__status__ = 'Development'
__author__ = 'xuxiang <xuxiang@myhexin.com>'

import MysqlHelper
from MysqlHelper import AbstractDao
from InheritType import InheritType
import StringUtil
import sys
import os

sys.path.append(os.getcwd()+'/../dictSynchronization')
import setting
           
class UpdateDataLines(AbstractDao):
    __tableName = 'update_data_lines'
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_value:prop=%s;%s$\n'
    typesWithParentIds = None
            
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_up=False):
        update_id = 0
        textList =[]
        
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textList.count(text)==0:
                textList.append(text)
            update_id = row.get('id')
        
        result = {'update':[],'delete':[]}
        result['update_num'] = len(textList)
        result['update_id'] = update_id
        for text in textList:
            textResult = self.getTextInfo(text, ms_update=ms_up)
            if textResult is None or len(textResult)==0:
                result['delete'].append(text)
            else:
                result['update'].append(textResult[0])
        return result
    
    def getTextInfo(self, text ,args=None, ms_update=False):
        sql = 'SELECT * FROM data_lines_v WHERE TEXT = %s'
        args = (text)
        result = []
        maxQuerySize=100000
        maxWriteSize=maxQuerySize/10
        
        #少年们, 我尽力说的简单一点了,  用一个map 和一个 list 处理type_id中的priority以及出现的顺序问题  就是pids                
        textToPids = {} # {text, [pids,pids, infos, used]}
        preText=''
        tempList=[]
        
        inheritType = InheritType(None, db = self.db)
        if self.typesWithParentIds is None:
            self.typesWithParentIds = inheritType.selectAllWithParentIds()
        
        page=0
        cursql=sql
        sql=cursql + ' limit ' + str(page*maxQuerySize) + ',' + str(maxQuerySize) 
        page=page+1
        rows=self.db.select( sql = sql, args = args, is_dict = True )
        while len(rows) > 0:
            for row in rows:
                # ms_update模式下，不在主搜索目录中的词典不导入
                if ms_update and row['type_lable'] in setting.MS_PROPS_IGNORE:
                    continue
                
                if not ms_update and row['type_lable'] in setting.PROPS_IGNORE:
                    continue
                
                row['text']=row['text'].strip()#徐祥的这个没有去除回车
                if (preText!=row['text'] and len(tempList)>=maxWriteSize):
                    self.combineType(tempList,textToPids,result)
                    del textToPids
                    del tempList
                    textToPids={}
                    tempList=[]
                self.dealWithRow(textToPids,row,tempList)
                preText=row['text']
            del rows
            sql=cursql + ' limit ' + str(page*maxQuerySize) + ',' + str(maxQuerySize) 
            page=page+1
            rows=self.db.select( sql = sql, args = args, is_dict = True )    
        self.db.cursor.close()    
        self.combineType(tempList,textToPids,result)
        del textToPids
        del tempList
        return result

    def combineType(self,tempList,textToPids,result):
        for row in tempList:
            if not textToPids[row['text']][3]:
                textToPids[row['text']][3] = True
                typeIds=[]
                typeIdSet=set()
                item=textToPids[row['text']][0].keys()
                item.sort(reverse=True);
                for onePriority in item :
                    for oneType in textToPids[row['text']][0][onePriority] :
                        if not typeIdSet.__contains__(oneType):
                            typeIdSet.add(oneType)
                            typeIds.append(oneType)
                types = []
                for typeId in typeIds:
                   types.append(self.typesWithParentIds[typeId]['label'])
                   
                typeStr = '|'.join(types)
        
                # 增加infos=XXXXX;
                infos = textToPids[row['text']][2]
                if (infos is not None) and (0 != len(infos)):
                    infos = 'infos=%s;' % '|'.join(infos)
    
                line = self.__line_format % (row.get('text').strip() or '', row.get('cate') or '', row.get('seg') or '', typeStr or '', infos or '')
                result.append(line)     
                
    def dealWithRow(self,textToPids,row,tempList):
        if textToPids.has_key(row['text']) :
            textToPids.get(row['text'])[1].add(row['type_id']);
            if textToPids.get(row['text'])[0].has_key(row['priority']):
                textToPids.get(row['text'])[0].get(row['priority']).append(row['type_id'])
            else:
                textToPids.get(row['text'])[0][row['priority']]=[row['type_id']]
        else:        
            textToPids.setdefault(row['text'], [{},set(), set(), False])[1].add(row['type_id'])
            textToPids.setdefault(row['text'], [{},set(), set(), False])[0][row['priority']]=[row['type_id']]
          
        #infos 中id等信息         
        if row['infos'] is not None and row['infos'] != '':
            textToPids.setdefault(row['text'], [{},set(), set(), False])[2].add('['+row['type_lable']+']:'+row['infos'].strip())
        
        if row['type_id'] in self.typesWithParentIds:
            for typeId in self.typesWithParentIds[row['type_id']]['pids']:
                if not textToPids[row['text']][1].__contains__(typeId):
                    textToPids[row['text']][0].get(row['priority']).append(typeId)
        tempList.append(row)

class UpdateDictTrans(AbstractDao):
    __tableName = 'update_dict_trans'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=trans:%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result
    
class UpdateDictOntoClass(AbstractDao):
    __tableName = 'update_dict_onto_class'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_class:%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result
    
class UpdateDictOntoKeyword(AbstractDao):
    __tableName = 'update_dict_onto_keyword'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_keyword:%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '',row.get('value') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result
    
class UpdateDictOntoDate(AbstractDao):
    __tableName = 'update_dict_onto_date'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_date:%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '',row.get('value') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result

class UpdateDictOntoTechOp(AbstractDao):
    __tableName = 'update_dict_onto_techop'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_techOp:%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '',row.get('value') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result

class UpdateDictOntoTechPeriod(AbstractDao):
    __tableName = 'update_dict_onto_techperiod'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_techPeriod:%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '',row.get('value') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result

class UpdateDictOntoChange(AbstractDao):
    __tableName = 'update_dict_onto_change'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_change:%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '',row.get('value') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result
    
class UpdateDictNoValue(AbstractDao):
    __tableName = 'update_dict_no_value'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result
    
class UpdateDictOntoQword(AbstractDao):
    __tableName = 'update_dict_onto_qword'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_qword:type=%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '',row.get('cate') or '',row.get('seg') or '',row.get('type') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result
            
class UpdateDictOntoTrigger(AbstractDao):
    __tableName = 'update_dict_onto_trigger'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_trigger:prop=%s;direction=%s;isindex=%s;skiplist=%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % \
                (row.get('text') or '', 
                 row.get('cate') or '', 
                 row.get('seg') or '', 
                 row.get('value') or '', 
                 row.get('direction') or '', 
                 row.get('isindex') or '', 
                 row.get('skiplist') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result
            
class UpdateDictOntoLogic(AbstractDao):
    __tableName = 'update_dict_onto_logic'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_logic:logicType=%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('logic_type') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result
            
class UpdateDictOntoSort(AbstractDao):
    __tableName = 'update_dict_onto_sort'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_sort:valueType=%s;descending=%s;k=%s;isTopK=%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value_type') or '', row.get('descending') or '', row.get('k') or '', row.get('is_top_k') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result

class UpdateDictOntoSpecial(AbstractDao):
    __tableName = 'update_dict_onto_special'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_special:msg=%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('msg') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result

class UpdateDictOntoVaguenum(AbstractDao):
    __tableName = 'update_dict_onto_vaguenum'
    
    def deleteById(self, id):
        delete_sql = 'delete from %s where id <=%s'%(self.__tableName,id)
        try:
            self.db.execute(delete_sql)
            self.db.commit()
        except:
            self.db.rollback()
            
    def getUpdateInfo(self,num,ms_update=False):
        update_id = 0
        textDict = {}
        resultDict={'update':{},'delete':{}}
        
        line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_vagueNum:vague_type=%s;$\n'
        select_sql = 'select * from %s order by id asc limit %s'%(self.__tableName,num)
        rows = self.db.select(sql = select_sql,is_dict = True )
        for row in rows:
            text =row['text']
            if textDict.has_key(text):
                    op = textDict[text]
                    del resultDict[op][text]
            if row.get('operation') == 'D':
                update_id = row.get('id')
                textDict[text] = 'delete'
                resultDict['delete'][text] = text
            else:
                line = line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('vague_type') or '')
                update_id = row.get('id')
                textDict[text] = 'update'
                resultDict['update'][text] = line
        
        result = {}
        result['update'] = resultDict['update'].values()
        result['delete'] = resultDict['delete'].values()
        result['update_num'] = len(textDict)
        result['update_id'] = update_id
        return result

if __name__ == "__main__":
    pass
