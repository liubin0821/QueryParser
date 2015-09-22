# -*- coding:utf-8 -*-
#!/usr/bin/env python

import sys
import traceback
import time
import os
import filecmp
import difflib
import re
import GetOneDictLine
import FindTheChangedPart


from mysql import MySqlClient
from redis_client import RedisSqlClient
import setting

data=['#黄守岩/*Cate=pretreat;Seg=;Value=onto_value:prop=_人名;$']

FindTheChangedPart.run(data,'stock_concept.dict')
