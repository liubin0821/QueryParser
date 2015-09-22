package com.myhexin.qparser.onto.user;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.DataConfException;

public class UserIndexInfo {

    public static void loadStockFakeIndex(Document doc)
            throws DataConfException {
        UserIndexXmlLoader.load(doc, Type.STOCK);
    }

    public static void loadFundFakeIndex(Document doc) throws DataConfException {
        UserIndexXmlLoader.load(doc, Type.FUND);
    }

    public static void setFakeStkIndexInfo(HashMap<String, ArrayList<UserIndexInfo>> replaceTrees) {
        UserIndexInfo.fakeStkIndexInfo = replaceTrees;
    }

    public static void setFakeFundIndexInfo(HashMap<String, ArrayList<UserIndexInfo>> fakeFundIndexInfo) {
        UserIndexInfo.fakeFundIndexInfo = fakeFundIndexInfo;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
       return this.description;
    }


    public static ArrayList<UserIndexInfo> getReplaceTreeByUnit(
            ArrayList<UserIndexInfo> allReplaceTree, Unit unit) {
        if(unit==null){
            return allReplaceTree;
        }
        ArrayList<UserIndexInfo> replaceTreeTmp = new ArrayList<UserIndexInfo>();
        for (int i = 0; i < allReplaceTree.size(); i++) {
            if (allReplaceTree.get(i).unit.equals(unit)) {
                replaceTreeTmp.add(allReplaceTree.get(i));
            }
        }
        return replaceTreeTmp;
    }

    public static UserIndexInfo getReplaceTreeByDescription(
            ArrayList<UserIndexInfo> allReplaceTree, String description) {
        assert(allReplaceTree!=null&&!allReplaceTree.isEmpty());
        if(description==null){
            return allReplaceTree.get(0);
        }
        for (int i = 0; i < allReplaceTree.size(); i++) {
            if (allReplaceTree.get(i).description.equals(description)) {
                return allReplaceTree.get(i);
            }
        }
        return null;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }


    private String description = null;
    private Unit unit = null;
    
    private static HashMap<String, ArrayList<UserIndexInfo>> fakeStkIndexInfo = new HashMap<String, ArrayList<UserIndexInfo>>();
    private static HashMap<String, ArrayList<UserIndexInfo>> fakeFundIndexInfo = new HashMap<String, ArrayList<UserIndexInfo>>();

}
