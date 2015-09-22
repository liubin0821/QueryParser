package com.myhexin.qparser.onto.user;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.RefType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.onto.UserClassNodeFacade;
import com.myhexin.qparser.onto.UserPropNodeFacade;

public class UserIndexXmlLoader {
    public static void load(Document doc, Type type) throws DataConfException {
        HashMap<String, SemanticNode> fakeOnto = new HashMap<String, SemanticNode>();
        HashMap<String, ArrayList<UserIndexInfo>> replaceTrees = new HashMap<String, ArrayList<UserIndexInfo>>();
        Element root = doc.getDocumentElement();
        NodeList infonodes = root.getChildNodes();
        String fileName = type == Type.STOCK ? Param.STOCK_USER_INDEX_FILE
                : Param.FUND_USER_INDEX_FILE;
        if (infonodes.getLength() == 0) {
            //现在没有信息也可以
            return;
           // throw new DataConfException(fileName, -1, "未定义任何内容");
        }
        for (int i = 0; i < infonodes.getLength(); i++) {
            Node classNodeI = infonodes.item(i);
            if (classNodeI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            } else if (!classNodeI.getNodeName().equals("fake_index")) {
                throw new DataConfException(fileName, -1,
                        "expect class node while got %s",
                        classNodeI.getNodeName());
            }

            NamedNodeMap namedNodeMap = classNodeI.getAttributes();
            String title = namedNodeMap.getNamedItem("title").getNodeValue();
            Node reportTypeNode = namedNodeMap.getNamedItem("report_type");

            if (title.isEmpty()) {
                throw new DataConfException(fileName, -1, "有Class的label为空");
            } else if (fakeOnto.get(title) != null) {
                throw new DataConfException(fileName, -1,
                        "Duplicated Class “%s”", title);
            }

            UserClassNodeFacade fakeClassNode = new UserClassNodeFacade(title);
            ReportType reportType = OntoXmlReader.parseReportType(reportTypeNode == null ? null: reportTypeNode.getNodeValue());
            fakeClassNode.setReportType(reportType);
            
            fakeOnto.put(title, fakeClassNode);
            replaceTrees.put(title, new ArrayList<UserIndexInfo>());
            
            parseFakeIndexInfo(fakeOnto, replaceTrees, fakeClassNode,
                    classNodeI.getChildNodes(),fileName,type);
        }
        // 此代码不再使用
        /*
        if (type == Type.STOCK) {
            MemOnto.setStkUserOnto_(fakeOnto);
            UserIndexInfo.setFakeStkIndexInfo(replaceTrees);
        } else if (type == Type.FUND) {
            MemOnto.setFundUserOnto_(fakeOnto);
            UserIndexInfo.setFakeFundIndexInfo(replaceTrees);
        } else {
          assert(false);
        }
        */
    }

    private static void parseFakeIndexInfo(
            HashMap<String, SemanticNode> fakeOnto,
            HashMap<String, ArrayList<UserIndexInfo>> replaceTrees,
            UserClassNodeFacade fakeClassNode, NodeList infoNodes, String fileName, Type type)
            throws DataConfException {
        for (int i = 0; i < infoNodes.getLength(); i++) {
            Node curInfo = infoNodes.item(i);
            if (curInfo.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            boolean hasOnto = false;
            try {
                if (curInfo.getNodeName().equals("fake_onto")) {
                    if (hasOnto) {
                        throw new DataConfException(fileName, -1,
                                "重复定义“%s”的语义信息", fakeClassNode.getText());
                    }
                    parseFakeIndexOnto(fakeOnto, fakeClassNode, curInfo.getChildNodes(),
                            fileName);
                    hasOnto = true;
                } else if (curInfo.getNodeName().equals("replace_tree")) {
                    parseReplaceTree(replaceTrees, fakeClassNode, curInfo,
                            fileName, type);
                } else {
                    throw new DataConfException(fileName, -1,
                            "Unexpect class node while got %s",
                            curInfo.getNodeName());
                }
            } catch (UnexpectedException e) {
                throw new DataConfException(fileName, -1, e.getLogMsg());
            }
        }
    }

    private static void parseReplaceTree(
            HashMap<String, ArrayList<UserIndexInfo>> replaceTrees,
            UserClassNodeFacade fakeClassNode, Node replaceTreeInfo, String fileName,
            Type type) throws DataConfException, UnexpectedException {
        ArrayList<UserIndexInfo> oldReplaceTrees = replaceTrees
                .get(fakeClassNode.getText());
        NamedNodeMap namedNodeMap = replaceTreeInfo.getAttributes();
        String description = namedNodeMap.getNamedItem("description")
                .getNodeValue();
        String unitStr = namedNodeMap.getNamedItem("unit") == null ? null
                : namedNodeMap.getNamedItem("unit").getNodeValue();
        Unit unit = EnumConvert.getUnitFromStr(unitStr)==Unit.UNKNOWN?null:EnumConvert.getUnitFromStr(unitStr);
        if (hasSameDescription(oldReplaceTrees,unit,description)) {
            throw new DataConfException(fileName, -1,
                    "Class %s 的description %s 在单位%s下有重复定义", fakeClassNode.getText(),
                    description,unitStr);
        }
        UserIndexInfo addFakeIndexInfo = new UserIndexInfo();
        addFakeIndexInfo.setDescription(description);
        addFakeIndexInfo.setUnit(unit);
        oldReplaceTrees.add(addFakeIndexInfo);
    }

    
    /**
     * 根据逻辑类型，构造逻辑节点
     * 
     * @param lt 逻辑类型
     * @return 构造出的逻辑节点
     */
    private static LogicNode makeLogicNodeByLogicType(LogicType lt) {
        assert (lt == LogicType.AND || lt == LogicType.OR);
        LogicNode ln = lt == LogicType.AND ? new LogicNode("AND")
                : new LogicNode("OR");
        ln.logicType = lt;
        return ln;
    }
    
    
    private static SemanticNode parseSMValueNode(String dataType,
            String defVal, String fileName) throws DataConfException, NotSupportedException {
        SemanticNode smNode = null;
        if (dataType.endsWith("DATE")) {
            smNode = new DateNode(defVal);
        } else if (dataType.endsWith("DOUBLE") || dataType.endsWith("LONG")) {
            smNode = new NumNode(defVal);
        } else if (dataType.endsWith("STR")) {
            smNode = new StrNode(defVal);
        } else {
            throw new DataConfException(fileName, -1,
                    "Unexpect SemanticNode dataType:%s", dataType);
        }
        if (defVal != null && !defVal.isEmpty()
                && smNode.type != NodeType.STR_VAL) {
            //尝试解析下，如果不能解析直接报错
            Object info = smNode.type == NodeType.DATE ? DateCompute
                    .getDateInfoFromStr(defVal, null) : NumParser
                    .getNumRangeFromStr(defVal);
        }
        return smNode;
    }


    private static void checkRefID(String refPropStr,
            UserClassNodeFacade fakeClassNode) throws UnexpectedException {
        if (fakeClassNode.getFakePropByID(refPropStr) == null) {
            throw new UnexpectedException("“%s”在伪指标“%s”无对应参数", refPropStr,
                    fakeClassNode.getText());
        }

    }

    private static RefType parseRefType(String refTypeStr)
            throws UnexpectedException {
        RefType refType = null;
        if (refTypeStr == null || refTypeStr.isEmpty()) {
            ;// No Op
        } else if (refTypeStr.equals("NULL")) {
            refType = RefType.NULL;
        } else if (refTypeStr.equals("COPY")) {
            refType = RefType.COPY;
        } else if (refTypeStr.equals("RELATIVE")) {
            refType = RefType.RELATIVE;
        } else if (refTypeStr.equals("REVERSAL")) {
            refType = RefType.REVERSAL;
        } else {
            throw new UnexpectedException("Unexpected refType:%s", refTypeStr);
        }
        return refType;
    }

    private static void checkValueNodeInfo(Node curInfo, UserClassNodeFacade fakeClassNode, String fileName, Type type)
            throws DataConfException {
        NamedNodeMap namedNodeMap = curInfo.getAttributes();
        if (namedNodeMap.getNamedItem("data_type") == null
                || namedNodeMap.getNamedItem("data_type").getNodeValue()
                        .isEmpty()) {
            throw new DataConfException(fileName, -1, "“%s”有data_type为空",
                    fakeClassNode.getText());
        }
        boolean refTypeIsEmpty = namedNodeMap.getNamedItem("ref_type") == null
                || namedNodeMap.getNamedItem("ref_type").getNodeValue()
                        .isEmpty();
        boolean refPropIsEmpty = namedNodeMap.getNamedItem("ref_prop") == null
                || namedNodeMap.getNamedItem("ref_prop").getNodeValue()
                        .isEmpty();
        if (!refTypeIsEmpty && refPropIsEmpty || refTypeIsEmpty
                && !refPropIsEmpty) {
            throw new DataConfException(fileName, -1,
                    "ref_type and ref_prop must  appeare at the same time",
                    namedNodeMap.getNamedItem("default").getNodeValue());
        }
        boolean defaultIsEmpty = namedNodeMap.getNamedItem("default") == null
                || namedNodeMap.getNamedItem("default").getNodeValue()
                        .isEmpty();
        boolean refTypeICopy = !refTypeIsEmpty
                && namedNodeMap.getNamedItem("ref_type").getNodeValue()
                        .equals("COPY");
        if (defaultIsEmpty && !refTypeICopy) {
            throw new DataConfException(fileName, -1, "“%s”有default为空,且refType不是copy",
                    fakeClassNode.getText());
        }
    }

    private static boolean hasSameDescription(
            ArrayList<UserIndexInfo> oldReplaceTrees, Unit unit, String description) {
        oldReplaceTrees = UserIndexInfo.getReplaceTreeByUnit(oldReplaceTrees, unit);
        for (UserIndexInfo fakeIndexInfo : oldReplaceTrees) {
            if (fakeIndexInfo.getDescription().equals(description)) {
                return true;
            }
        }
        return false;
    }

    private static void parseFakeIndexOnto(
            HashMap<String, SemanticNode> fakeOnto, UserClassNodeFacade fakeClassNode,
            NodeList propNodes, String fileName) throws DataConfException, UnexpectedException {
        for (int i = 0; i < propNodes.getLength(); i++) {
            Node curProp = propNodes.item(i);
            if (curProp.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            } else if (!curProp.getNodeName().equals("prop")) {
                throw new DataConfException(fileName, -1,
                        "expect class node while got %s", curProp.getNodeName());
            }
            NamedNodeMap namedNodeMap = curProp.getAttributes();
            checkFakePropInfo(fakeClassNode.getText(),fileName,namedNodeMap);
            
            String id = namedNodeMap.getNamedItem("id").getNodeValue();
            String label = namedNodeMap.getNamedItem("label").getNodeValue();
            String dataTypeStr =namedNodeMap.getNamedItem("data_type").getNodeValue();
            String unitStr = namedNodeMap.getNamedItem("unit")==null?null:namedNodeMap.getNamedItem("unit").getNodeValue(); 
            
            if(fakeClassNode.getPropByName(label)!=null){
                throw new DataConfException(fileName, -1,
                        "Class %s 的prop %s 有重复", fakeClassNode.getText(), label);
            }
            
            UserPropNodeFacade pn = new UserPropNodeFacade(label);
            pn.setValueType(OntoXmlReader.parseValueType_(dataTypeStr));
            
            if(!fakeOnto.containsKey(label)) {
                fakeOnto.put(label, pn);
            } else {
                if(!false)
                	continue;
                /** zd modified on 2013-05-30 目的：每个index使用相互独立的prop */
//                pn = (UserPropNodeFacade)fakeOnto.get(label);
            }
            pn.addID(fakeClassNode, id);
            OntoXmlReader.parseValueUnit_(unitStr, pn, fakeClassNode);
            pn.addOfWhat(fakeClassNode);
            fakeClassNode.addProp(pn);
        }
    }

    private static void checkFakePropInfo(String className, String fileName,NamedNodeMap namedNodeMap) throws DataConfException {
        Node idNode = namedNodeMap.getNamedItem("id");
        Node labelNode = namedNodeMap.getNamedItem("label");
        Node dataTypeNode =namedNodeMap.getNamedItem("data_type");
        
        if (idNode==null||idNode.getNodeValue().isEmpty()) {
            throw new DataConfException(fileName, -1,
                    "FakeClass %s 有FakeProp的id为空", className);
        }else if(labelNode==null||labelNode.getNodeValue().isEmpty()){
            throw new DataConfException(fileName, -1,
                    "FakeClass %s 有FakeProp的label为空", className);
        }else if(dataTypeNode==null||dataTypeNode.getNodeValue().isEmpty()){
            throw new DataConfException(fileName, -1,
                    "FakeClass %s 有FakeProp的dataType为空", className);
        }
    }
    
}
