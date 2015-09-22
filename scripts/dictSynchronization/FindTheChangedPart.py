#!/usr/bin/env python
# -*- coding:utf-8 -*-
import sys
import os
sys.path.append(os.getcwd()+'/../dictDbInputOutput')
import InheritDictImport
import Normalize
import setting

reload(sys)
sys.setdefaultencoding("utf-8")

synchronizationPath = setting.LTP_PATH + '/dictSynchronization/'
lastDataFold = synchronizationPath + '.lastData/'
backUpFold = lastDataFold + 'backup/'

if not os.path.exists(lastDataFold):
    os.makedirs(lastDataFold)
    
if not os.path.exists(backUpFold):    
    os.makedirs(backUpFold)
    


def write_to_file(list,file_name):
    out = open(file_name, "wb");
    for value in list:
        out.write(value);
    out.close;

def normalizeList(lists):
    result = []
    for line in lists:
        lines = line.split("/*Cate=")
        if len(lines) == 2 and lines[0].find("|")<0:
            lines[0] = Normalize.normalize(lines[0])
            result.append(lines[0]+"/*Cate="+lines[1])
        else:
            result.append(line)    
    return result        
    
def writeChangeToDictDb(deletedList,addingList):
    deletedList = normalizeList(deletedList)
    addingList = normalizeList(addingList)
    
    inheritDictImport = InheritDictImport.InheritDictImport(setting.ONTOLOGYDB_CONFIG)
    inheritDictImport.deleteByLines(deletedList)
    inheritDictImport.importByLines(addingList)
    print

def getNotInListB(listA,ListB):
    if len(ListB)==0 or len(listA)==0:
        return listA
    result = []
    sets=set(ListB)
    for line in listA:
        if line not in sets:
            result.append(line)
    return result

def getChanged(data,fileList,addingList,deletedList):
    lenData=len(data)
    lenfileList=len(fileList)
    dataPos=0
    fileListPos=0
    while(dataPos<lenData and fileListPos<lenfileList):
        if data[dataPos] < fileList[fileListPos]:
            addingList.append(data[dataPos])
            dataPos=dataPos+1
        elif fileList[fileListPos] < data[dataPos]:
            deletedList.append(fileList[fileListPos])
            fileListPos=fileListPos+1
        else:
            dataPos=dataPos+1
            fileListPos=fileListPos+1
    if dataPos<lenData-1:
        addingList.extend(data[dataPos:])
    elif fileListPos<lenfileList-1:
        deletedList.extend(fileList[fileListPos:])
    
def run(data,fileName):
    is_update = False
    filePath = lastDataFold + fileName
    fileName=fileName.decode()
    deletedList = []
    addingList = []
    
    if len(data)>0 and not data[len(data)-1].endswith('\n') :#如果最后没有回车,添加回车
        data[len(data)-1]=data[len(data)-1]+'\n'
                
    data.sort()
       
    if len(data) > 0 and os.path.exists(filePath):
            file2 = open(filePath, 'r')
            fileList=file2.readlines()
            fileList.sort()
            getChanged(data,fileList,addingList,deletedList)
            #addingList=getNotInListB(data,fileList)
            #deletedList=getNotInListB(fileList,data)                                          
            if len(addingList)>0 or len(deletedList)>0:
                is_update = True
                writeChangeToDictDb(deletedList,addingList)
                os.system("mv " + filePath + " " + backUpFold)                
                write_to_file(data,filePath)
                print "write to " + filePath + ": success!"
                print ""
    elif len(data) > 0:
        is_update = True
        addingList = data
        writeChangeToDictDb(deletedList,addingList)
        write_to_file(data,filePath)
        print "write to " + filePath + ": success!"
        print ""
        
    return is_update     
    