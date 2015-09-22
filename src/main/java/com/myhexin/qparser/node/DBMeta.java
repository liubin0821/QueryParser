package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.define.EnumDef.Unit;


public class DBMeta {
    public DBMeta(){
        
    }
    public String tableName_ = null;
    public String latestTblName_ = null;
    public HashMap<Unit, String> unit2ColName_ = null;
    public String[] joinKey_ = null;
    public String dateColName_ = null;
}
