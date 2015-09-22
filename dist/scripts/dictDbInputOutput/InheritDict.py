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
from ms_props import *

def drawFieldFromLine( line, fieldConfig ):
    for (k, v) in fieldConfig.items():
        s = line.find(v[0])
        if s != -1:
            s += len(v[0])
            e = line.find(v[1], s)
            v[2] = StringUtil.subString(line, s, e)      
                

########################################################################
#    OntoValue
########################################################################
class OntoValue(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_value:prop=%s;%s$\n'
    __tableName = 'data_lines_v'
    
    typesWithParentIds = None

    def selectAll(self, ms_up=False):
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql, ms_update=ms_up)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
   
    def selectBySql(self, sql, args = None, ms_update = False):
        self.MS_PROPS_IGNORE, self.PROPS_IGNORE = load_list()
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
        
        #self.db.execute( sql = sql, args = args)
        #rows=self.db.get_rows(size=maxQuerySize, is_dict = True)
        page=0
        cursql=sql
        sql=cursql + ' limit ' + str(page*maxQuerySize) + ',' + str(maxQuerySize) 
        page=page+1
        rows=self.db.select( sql = sql, args = args, is_dict = True )
        while len(rows) > 0:
            for row in rows:
                # ms_update模式下，不在主搜索目录中的词典不导入
                if ms_update and row['type_lable'] in self.MS_PROPS_IGNORE:
                    continue
                
                if not ms_update and row['type_lable'] in self.PROPS_IGNORE:
                    continue
                
                row['text']=row['text'].strip()#徐祥的这个没有去除回车
                if (preText!=row['text'] and len(tempList)>=maxWriteSize):
                    self.combineType(tempList,textToPids,result)
                    #del result
                    del textToPids
                    del tempList
                    #result=[]
                    textToPids={}
                    tempList=[]
                self.dealWithRow(textToPids,row,tempList)
                preText=row['text']
            del rows    
            #rows=self.db.get_rows(size=maxQuerySize, is_dict = True)
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
        tempList.append(row);

    def insertByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', ''],
            'cate'  : ['Cate=', ';', ''],
            'seg'   : ['Seg=', ';', ''],
            'infos' : ['infos=', ';', ''],
            'types' : ['Value=onto_value:prop=', ';', ''],
            'from_where' : ['from_where=', ';', '']
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        if fieldConfig['types'][2] is not None:
            inheritType = InheritType(None, db = self.db)
            for type in fieldConfig['types'][2].split('|'):
                typeRow = inheritType.selectByLabel(type)
                if typeRow is None:
                    inheritType.insert(type)
                    typeRow = inheritType.selectByLabel(type)
                type_id = typeRow['id']
                try:
                    self.db.insert_ignore(self.__tableName, 
                                          {'type_id':type_id, 'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2],
                                           'infos':fieldConfig['infos'][2], 'from_where':fieldConfig['from_where'][2],'create_date':None})
                    self.db.commit()
                except:
                    # Rollback in case there is any error
                    self.db.rollback()
                    
    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'types' : ['Value=onto_value:prop=', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        if fieldConfig['types'][2] is not None:
            inheritType = InheritType(None, db = self.db)
            for type in fieldConfig['types'][2].split('|'):
                typeRow = inheritType.selectByLabel(type)
                if typeRow is None:
                    continue
                type_id = typeRow['id']
                try:
                    self.db.delete(self.__tableName, {'type_id':type_id, 'text':fieldConfig['text'][2]})
                    self.db.commit()
                except:
                    # Rollback in case there is any error
                    self.db.rollback()

########################################################################
#    Trans
########################################################################
class Trans(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=trans:%s;$\n'
    __tableName = 'dict_trans'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'value' : ['Value=trans:', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'value':fieldConfig['value'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()
            
    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoClass
########################################################################
class OntoClass(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_class:%s;$\n'
    __tableName = 'dict_onto_class'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'value' : ['Value=onto_class:', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'value':fieldConfig['value'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()
    
    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()
    
########################################################################
#    Trigger
########################################################################
class OntoTrigger(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_trigger:prop=%s;direction=%s;isindex=%s;skiplist=%s;$\n'
    __tableName = 'dict_onto_trigger'
    
    def selectAll(self, ms_up=False):
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )    

    def selectBySql(self, sql, args = None):
        result = []
        
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % \
            (row.get('text') or '', 
             row.get('cate') or '', 
             row.get('seg') or '', 
             row.get('value') or '', 
             row.get('direction') or '', 
             row.get('isindex') or '', 
             row.get('skiplist') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'value' : ['Value=onto_trigger:prop=', ';', None],
            'direction' : ['direction=', ';', None],
            'isindex'   : ['isindex=', ';', None],
            'skiplist'  : ['skiplist=', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'value':fieldConfig['value'][2], 'direction':fieldConfig['direction'][2], 'isindex':fieldConfig['isindex'][2], 'skiplist':fieldConfig['skiplist'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()
    
    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()
            
########################################################################
#    OntoKeyword
########################################################################
class OntoKeyword(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_keyword:%s;$\n'
    __tableName = 'dict_onto_keyword'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'value' : ['Value=onto_keyword:', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'value':fieldConfig['value'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    NullValue
########################################################################
class NullValue(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=;$\n'
    __tableName = 'dict_no_value'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoLogic
########################################################################
class OntoLogic(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_logic:logicType=%s;$\n'
    __tableName = 'dict_onto_logic'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('logic_type') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'logicType'   : ['Value=onto_logic:logicType=', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'logic_type':fieldConfig['logicType'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoSort
########################################################################
class OntoSort(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_sort:valueType=%s;descending=%s;k=%s;isTopK=%s;$\n'
    __tableName = 'dict_onto_sort'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value_type') or '', row.get('descending') or '', row.get('k') or '', row.get('is_top_k') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'valueType'  : ['Value=onto_sort:valueType=', ';', None],
            'descending' : ['descending=', ';', None],
            'k'          : [';k=', ';', None],
            'isTopK'     : ['isTopK=', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'valueType':fieldConfig['value_type'][2], 'descending':fieldConfig['descending'][2], 'k':fieldConfig['k'][2], 'is_top_k':fieldConfig['isTopK'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoDate
########################################################################
class OntoDate(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_date:%s;$\n'
    __tableName = 'dict_onto_date'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'value' : ['Value=onto_date:', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'value':fieldConfig['value'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoTechOp
########################################################################
class OntoTechOp(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_techOp:%s;$\n'
    __tableName = 'dict_onto_techOp'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'value' : ['Value=onto_techOp:', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'value':fieldConfig['value'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoChange
########################################################################
class OntoChange(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_change:%s;$\n'
    __tableName = 'dict_onto_change'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'value' : ['Value=onto_change:', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'value':fieldConfig['value'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoSpecial
########################################################################
class OntoSpecial(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_special:msg=%s;$\n'
    __tableName = 'dict_onto_special'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('msg') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'msg'   : ['Value=onto_special:msg=', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'msg':fieldConfig['msg'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoQword
########################################################################
class OntoQword(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_qword:type=%s;$\n'
    __tableName = 'dict_onto_qword'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('type') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'type'   : ['Value=onto_qword:type=', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'type':fieldConfig['type'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoVagueNum
########################################################################
class OntoVagueNum(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_vagueNum:vague_type=%s;$\n'
    __tableName = 'dict_onto_vagueNum'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('vague_type') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'vague_type'   : ['Value=onto_vagueNum:vague_type=', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'vague_type':fieldConfig['vague_type'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

########################################################################
#    OntoTechPeriod
########################################################################
class OntoTechPeriod(AbstractDao):
    
    __line_format = '#%s/*Cate=%s;Seg=%s;Value=onto_techPeriod:%s;$\n'
    __tableName = 'dict_onto_techPeriod'
    
    def selectAll(self, ms_up=False):
        result = []
        
        select_sql = '''
        SELECT * FROM %s order by text
        ''' % self.__tableName
        return self.selectBySql(select_sql)
    
    def selectByText(self, text):
        
        select_sql = '''
        SELECT * FROM %s where text = %%s
        ''' % self.__tableName
        return self.selectBySql( sql = select_sql, args = text )
    
    def selectBySql(self, sql, args = None):
        result = []
        rows = self.db.select( sql = sql, args = args, is_dict = True )
        
        for row in rows:
            line = self.__line_format % (row.get('text') or '', row.get('cate') or '', row.get('seg') or '', row.get('value') or '')
            result.append(line)
        return result
    
    def insertByLine(self, line):
        
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
            'cate'  : ['Cate=', ';', None],
            'seg'   : ['Seg=', ';', None],
            'value' : ['Value=onto_techPeriod:', ';', None]
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.insert_ignore(self.__tableName, {'text':fieldConfig['text'][2], 'cate':fieldConfig['cate'][2], 'seg':fieldConfig['seg'][2], 'value':fieldConfig['value'][2], 'create_date':None})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

    def deleteByLine(self, line):
        fieldConfig = {
            # key       S    E    V
            'text'  : ['#', '/*', None],
        }
        
        drawFieldFromLine(line, fieldConfig)
        
        try:
            self.db.delete(self.__tableName, {'text':fieldConfig['text'][2]})
            self.db.commit()
        except:
            # Rollback in case there is any error
            self.db.rollback()

if __name__ == "__main__":
    pass
