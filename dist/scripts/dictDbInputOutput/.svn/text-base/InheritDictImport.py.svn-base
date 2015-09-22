#!/usr/bin/env python
# -*- coding:utf-8 -*-

__status__ = 'Development'
__author__ = 'xuxiang <xuxiang@myhexin.com>'

from MysqlHelper import AbstractDao
import InheritDict



class InheritDictImport(AbstractDao):
    def __init__(self, dbconfig, db = None):
        AbstractDao.__init__(self, dbconfig, db)
        self.tableConfig = {
            'Value=onto_value:prop'   : InheritDict.OntoValue(None, db = self.db),
            'Value=trans'   : InheritDict.Trans(None, db = self.db),
            'Value=onto_trigger:prop'   : InheritDict.OntoTrigger(None, db = self.db),
            'Value=onto_class' : InheritDict.OntoClass(None, db = self.db),
            'Value=onto_keyword' : InheritDict.OntoKeyword(None, db = self.db),
            'Value=;' : InheritDict.NullValue(None, db = self.db),
            'Value=onto_logic' : InheritDict.OntoLogic(None, db = self.db),
            'Value=onto_sort' : InheritDict.OntoSort(None, db = self.db),
            'Value=onto_date' : InheritDict.OntoDate(None, db = self.db),
            'Value=onto_techOp' : InheritDict.OntoTechOp(None, db = self.db),
            'Value=onto_change' : InheritDict.OntoChange(None, db = self.db),
            'Value=onto_special' : InheritDict.OntoSpecial(None, db = self.db),
            'Value=onto_qword' : InheritDict.OntoQword(None, db = self.db),
            'Value=onto_vagueNum' : InheritDict.OntoVagueNum(None, db = self.db),
            'Value=onto_techPeriod' : InheritDict.OntoTechPeriod(None, db = self.db)}
        
    def importByLines(self, lines):
        failed_file = open('import_failed.log', "w+")
        failedCount = 0
        successCount = 0
        for line in lines:
            flag = False
            for k in self.tableConfig.keys():
                if line.find(k) != -1:
                    successCount += 1
                    flag = True
                    self.tableConfig.get(k).insertByLine(line)
                    break
            if not flag:
                failedCount += 1
                failed_file.write(line)
        failed_file.flush()
        failed_file.close()
        print "success : %d, failed : %d [%s]" % (successCount, failedCount, 'import_failed.log')

    def deleteByLines(self, lines):
        failed_file = open('delete_failed.log', "w+")
        failedCount = 0
        successCount = 0
        for line in lines:
            flag = False
            for k in self.tableConfig.keys():
                if line.find(k) != -1:
                    successCount += 1
                    flag = True
                    self.tableConfig.get(k).deleteByLine(line)
                    break
            if not flag:
                failedCount += 1
                failed_file.write(line)
        failed_file.flush()
        failed_file.close()
        print "success : %d, failed : %d [%s]" % (successCount, failedCount, 'delete_failed.log')

    def importByFiles(self, filePaths):
        for filePath in filePaths:
            file = open(filePath, "r")
            lines = file.readlines()
            self.importByLines(lines)
            file.flush()
            file.close()

if __name__ == "__main__":
    print 'start'
    ONTOLOGYDB_CONFIG = {
    'host' : "172.20.201.147",
    'port' : 3306,
    'db' : 'ontologydb',
    'user' : 'qnateam',
    'passwd' : 'qnateam'
    }
    inheritDictImport = InheritDictImport(ONTOLOGYDB_CONFIG)
    #inheritDictImport.importByFiles(['GetLtpDicts.txt'])
    inheritDictImport.importByLines(['#吴永行/*Cate=pretreat;Seg=;Value=onto_value:prop=_QFII名称;from_where=;$']);
    print "end"
    pass

