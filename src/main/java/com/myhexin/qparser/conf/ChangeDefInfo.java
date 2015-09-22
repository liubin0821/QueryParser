package com.myhexin.qparser.conf;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.ChangeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;

public class ChangeDefInfo {
    
    public static void loadStockChangeInfo(Document doc)
            throws DataConfException {
        load(doc, Type.STOCK);
    }

    public static void loadFundChangeInfo(Document doc)
            throws DataConfException {
        load(doc, Type.FUND);
    }
    
    private static void load(Document doc, Type type) throws DataConfException {
        HashMap<String, ChangeDefInfo> changeInfos = new HashMap<String, ChangeDefInfo>();
        Element root = doc.getDocumentElement();
        NodeList infoNodes = root.getChildNodes();
        String fileName = type == Type.STOCK ? /*Param.STOCK_CHANGE_INFO_FILE*/""
                : Param.FUND_CHANGE_INFO_FILE;
        if (infoNodes.getLength() == 0) {
            //现在没有信息也可以
            return;
           // throw new DataConfException(fileName, -1, "未定义任何内容");
        }
        for (int i = 0; i < infoNodes.getLength(); i++) {
            Node changeNodeI = infoNodes.item(i);
            if (changeNodeI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            } else if (!changeNodeI.getNodeName().equals("change")) {
                throw new DataConfException(fileName, -1,
                        "expect change node while got %s",
                        changeNodeI.getNodeName());
            }
            ChangeDefInfo changeInfoI = new ChangeDefInfo();
             
            NamedNodeMap namedNodeMap = changeNodeI.getAttributes();
            String label = namedNodeMap.getNamedItem("label").getNodeValue();
            Node isCrease = namedNodeMap.getNamedItem("is_crease");
            Node creaseType = namedNodeMap.getNamedItem("crease_type");
            Node unitNode = namedNodeMap.getNamedItem("unit");
            parseUnitInfo(label,fileName,unitNode==null?null:unitNode.getNodeValue(),changeInfoI);
            
            try {
                parseOtherInfo(label,changeInfoI,changeNodeI,fileName,type);
            } catch (UnexpectedException e) {
                throw new DataConfException(fileName, -1,e.getLogMsg());
            } 
            changeInfos.put(label, changeInfoI);
        }
        changeDefInfos = changeInfos;
    }
    
    
    private static void parseOtherInfo(String label, ChangeDefInfo changeInfoI,
            Node changeNodeI, String fileName, Type type) throws DataConfException, UnexpectedException, DOMException {
       NodeList infoNodes = changeNodeI.getChildNodes();
       for (int i = 0; i < infoNodes.getLength(); i++) {
           Node infoNodeI = infoNodes.item(i);
           if (changeNodeI.getNodeType() != Node.ELEMENT_NODE) {
               continue;
           } 
           String nodeName = infoNodeI.getNodeName();
           if(nodeName.equals("of_index")){
               parseOfIndexInfo(label,fileName,infoNodeI.getNodeValue(),changeInfoI,type);
           }else if(nodeName.equals("def_class_info")){
               parseDefClassInfo(label,fileName,infoNodeI,changeInfoI,type);
           }else {
               throw new DataConfException(fileName, -1,
                       "change “%s” unexpect node :%s ",
                       nodeName);
           }
       }
    }

    private static void parseUnitInfo(String label, String fileName,
            String unitStr, ChangeDefInfo changeInfoI) {
        if (unitStr == null || unitStr.isEmpty()) {
            return;
        }
        ArrayList<Unit> unitsTmp = new ArrayList<Unit>();
        String[] unitStrs = unitStr.split("\\|");
        for (int i = 0; i < unitStrs.length; i++) {
            unitsTmp.add(EnumConvert.getUnitFromStr(unitStrs[i]));
        }
        changeInfoI.setUnits(unitsTmp);
    }
    
    private static void parseOfIndexInfo(String label, String fileName,
            String indexStr, ChangeDefInfo changeInfoI, Type type) throws UnexpectedException {
        if (indexStr == null || indexStr.isEmpty()) {
            return;
        }
        ArrayList<ClassNodeFacade> indexsTmp = new ArrayList<ClassNodeFacade>();
        String[] indexStrs = indexStr.split("\\|");
        for (int i = 0; i < indexStrs.length; i++) {
            ClassNodeFacade indexI = MemOnto.getOnto(indexStrs[i], ClassNodeFacade.class, type);
            indexsTmp.add(indexI);
        }
        changeInfoI.setOfIndex(indexsTmp);
    }

    private static void parseDefClassInfo(String label, String fileName,
            Node infoNodeI, ChangeDefInfo changeInfoI, Type type) {
        
        
        
    }

    private static HashMap<String, ChangeDefInfo> changeDefInfos = new HashMap<String, ChangeDefInfo>();
    
    public ChangeDefInfo getChangeInfoByLabel(String Label){
        return changeDefInfos.get(Label);
    }
    
    
    public void setCrease(boolean isCrease) {
        this.isCrease = isCrease;
    }

    public boolean isCrease() {
        return isCrease;
    }

    public void setOfIndex(ArrayList<ClassNodeFacade> ofIndex) {
        this.ofIndex = ofIndex;
    }

    public ArrayList<ClassNodeFacade> getOfIndex() {
        return ofIndex;
    }

    public void setDefIndex(HashMap<Unit,ClassNodeFacade> defIndex) {
        this.defIndex = defIndex;
    }

    public HashMap<Unit,ClassNodeFacade> getDefIndex() {
        return defIndex;
    }


    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public ChangeType getChangeType() {
        return changeType;
    }


    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    private boolean isCrease = true;
    private ChangeType changeType = ChangeType.INCREASE;
    private ArrayList<Unit> units = null;
    private ArrayList<ClassNodeFacade> ofIndex = null;
    private HashMap<Unit,ClassNodeFacade> defIndex = new HashMap<Unit,ClassNodeFacade>();
    
}
