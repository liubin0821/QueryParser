#!/usr/bin/python2.6  
# -*- coding: utf-8 -*-

import os

import setting
import FindTheChangedPart

ltp_path = setting.LTP_PATH
dicts_path = ltp_path + "/dicts/"
new_system_dict = [#"inherit_dicts_trigger.dict"
                   #, "inherit_dicts.dict"
                   #, "inherit_dicts_trans.dict"
                     "stock_str_new.dict"
                    , "stock_str_trans.dict"
                    , "stock_name_new.dict"
                    , "fund_prod_trans_new.dict"
                    , "fund_seg_new.dict"
                    , "stock_prod_new.dict"
                    , "stock_person_new.dict"
                    , "stock_prod_trans_new.dict"
                    , "fund_prod_trans_new.dict"
                    , "stock_trans.dict"
                    , "stock_misc_new.dict"
                    , "stock_seg_new.dict"
                    , "all_date_new.dict"
                    , "all_geo_trans.dict"
                    , "all_inst_trans.dict"
                    , "all_trans.dict"
                    #, "stock_index.dict"
                    #, "stock_index_trans.dict"
                    , "stock_keyword.dict"]

for fileName in new_system_dict:
    filePath = dicts_path+fileName
    if os.path.exists(filePath):
        file2 = open(filePath, 'r')
        data=file2.readlines()
        FindTheChangedPart.run(data,fileName)

print ""        
print "wrote new system dict change into db .."         
        
