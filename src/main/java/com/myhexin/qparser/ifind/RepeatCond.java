package com.myhexin.qparser.ifind;

import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.define.OperDef;


public class RepeatCond {
    public String repeatLogicType = OperDef.AND;
    public String operator = null;
    public String cmpValue = null;
    public HashMap<IFindParam, ArrayList<String>> param2Vals =
        new HashMap<IFindParam, ArrayList<String>>();
}