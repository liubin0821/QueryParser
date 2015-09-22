#!/bin/python
# -*- coding: utf-8 -*-

import sys, re, codecs

dataRoot = r'D:\project\qna\Ontology_Ask\trunk\QueryParser\data\\'
indexNodePtn = re.compile(r'^\s+<index.+?title="([^"]+)".+?>\s*$')
indexNodePtn2 = re.compile(r'^\s+<index.+?title="([^"]+)".+?/>\s*$')
indexEndPtn = re.compile(r'^\s*</index>\s*$')

halfYearIndex = [u'每股分红送转', u'每股股利(税前)', u'每股股利(随后)', u'每股红股', u'每股转增股本',
	u'b股最后交易日', u'分红方案进度', u'分红对象', u'是否分红', u'除权除息日', u'股权登记日', u'派息日',
	u'红股上市交易日', u'预案公告日', u'股东大会公告日', u'分红实施公告日', u'存货跌价准备合计']
predictIndex = [u'业绩预告类型', u'业绩预告日期', u'业绩预告摘要', u'预告净利润变动幅度(%)']

items = [] #orig line or Index instance

class Index:
	def __init__(self, title, rootLine):
		self.title = title;
		self.rootLine = rootLine
		self.childLine = []
		self.endLine = ''
		#print title
	def addChildLine(self, line):
		self.childLine.append(line)
	def setEndLine(self, endLine):
		self.endLine = endLine
	def changeP00007(self, newVal, reportError=True):
		defVal = 'default_val="'
		for i in range(len(self.childLine)):
			if self.childLine[i].find('list_name="P00007"') >= 0:
				valPos = self.childLine[i].find(defVal) + len(defVal)
				oldVal = self.childLine[i][valPos:valPos+8]
				self.childLine[i] = self.childLine[i][0:valPos] + newVal + self.childLine[i][valPos+8:]
				print '%s: Changed P00007 from %s to %s' % (self.title, oldVal, newVal)
				break
		else:
			if reportError: print 'ERROR: P00007 of %s not changed' % self.title
	def __str__(self):
		return '%s%s%s' % (self.rootLine, ''.join(self.childLine), self.endLine)

def loadXML(stock_index_xml):
	lines = codecs.open(stock_index_xml, u'r', u'utf-8').readlines()
	#print 'Read all content of %s. Got %d lines' % (stock_index_xml, len(lines))
	nIndex = 0
	i = 0
	while i < len(lines):
		mo = indexNodePtn2.match(lines[i])
		if mo:
			newInd = Index(mo.group(1), lines[i])
			items.append(newInd); nIndex += 1
			i += 1
			continue
		mo = indexNodePtn.match(lines[i])
		if not mo:
			items.append(lines[i]); i += 1;
			continue

		newInd = Index(mo.group(1), lines[i])
		i += 1
		while not indexEndPtn.match(lines[i]):
			newInd.addChildLine(lines[i])
			i += 1
		newInd.setEndLine(lines[i])
		items.append(newInd); nIndex += 1
		i += 1
	#print 'Done loading. Got %d index' % nIndex

def changeP00007():
	for item in items:
		if not isinstance(item, Index): continue
		if item.title in halfYearIndex:
			item.changeP00007('20120630')
		elif item.title in predictIndex:
			item.changeP00007('20121231')
		else:
			item.changeP00007('20120930', False)

def saveXML(confFile):
	ofh = codecs.open(confFile, 'w', 'utf-8')
	ofh.write(''.join([item.__str__() for item in items]))
	ofh.close()
	
if __name__ == '__main__':
	loadXML(dataRoot + r'stock\stock_index.xml')
	changeP00007()
	saveXML(dataRoot + r'stock\stock_index.xml2')
	
	
