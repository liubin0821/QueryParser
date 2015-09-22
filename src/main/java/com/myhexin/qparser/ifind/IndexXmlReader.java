package com.myhexin.qparser.ifind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.conf.FakeNumDefInfo;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.FakeNumType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.WebAction;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.PropNodeFacade;

public class IndexXmlReader {
    private static String confFileName = null;
    private static Query.Type qtype_ = null;
    /**
     * parse config file to memory
     * @param doc XMLDom 文档
     * @param qtype 配置文件类型
     * @return indexInfos 指标信息map
     */
    public static HashMap<String, IndexInfo> loadIndex(Document doc, Query.Type qtype)
    throws DataConfException{
        if (qtype == Query.Type.FUND) {
            confFileName = Param.FUND_INDEX_FILE;
            qtype_ = Query.Type.FUND;
        } else {
            confFileName = Param.STOCK_INDEX_FILE;
            qtype_ = Query.Type.STOCK;
        }
        
        HashMap<String, IndexInfo> infos = new HashMap<String, IndexInfo>();
        Element root = doc.getDocumentElement();
        ArrayList<Node> infonodes = IndexXmlReader.getChildElementsByTagName(root, "index");
        for(int i=0; i<infonodes.size(); i++)
        {
            Node indexI = infonodes.get(i);
            String title = IndexXmlReader.getAttributeByName(indexI, "title");
            if (infos.containsKey(title)) {
                throw new DataConfException(confFileName, -1,
                        "XML文件中节点重复：%s", title);
            }
            IndexInfo info = new IndexInfo(title);
            try {
                getSingleInfoFromNode(title, info, indexI, qtype);
            } catch (UnexpectedException e) {
                throw new DataConfException(confFileName, -1, e.getLogMsg());
            }catch (NotSupportedException e) {
                throw new DataConfException(confFileName, -1, e.getLogMsg());
            }
            infos.put(title, info);
        }
        return infos;
    }
    
    private static void getSingleInfoFromNode(String titleStr, IndexInfo info,
            Node indexI, Query.Type qtype)
    throws DataConfException,
            UnexpectedException, NotSupportedException {

        info.id = IndexXmlReader.getAttributeByName(indexI, "id");
        if (info.id == null || (info.id.trim()).length() == 0) {
            throw new DataConfException(confFileName, -1, "XML文件中指标“%s”缺少id",
                    titleStr);
        }

        info.unitList = IndexXmlReader.getAttributeByName(indexI, "unit_list");
        info.pubUnit = IndexXmlReader.getAttributeByName(indexI, "pub-unit");
        info.pubTitle = IndexXmlReader.getAttributeByName(indexI, "pub-title");
        
        ArrayList<Node> childInfos = IndexXmlReader.getChildElementsByTagName(indexI, "params", "param");
        ArrayList<IFindParam> paramsInfo = new ArrayList<IFindParam>();
        for(int i=0; i<childInfos.size(); i++)
        {
            Node child = childInfos.get(i);
            getParamInfo(titleStr, child, paramsInfo);
        }
        paramsInfo.trimToSize();
        info.params = paramsInfo;

        childInfos = IndexXmlReader.getChildElementsByTagName(indexI, "aliases", "alias");
        for(int i=0; i<childInfos.size(); i++)
        {
            Node child = childInfos.get(i);
            String aliasName = getAttributeByName(child, "title");
            if(aliasName != null)
            {
                info.putAliasNode(aliasName);
            }
        }
        

        Node childInfo;

        childInfo = IndexXmlReader.getChildElementByTagName(indexI, "value");
        if(childInfo != null)
            getValsInfo(titleStr, childInfo, info);

        childInfo = IndexXmlReader.getChildElementByTagName(indexI, "vague_num");
        if(childInfo != null)
            getVagueInfo(titleStr, childInfo, info, PropType.NUM);

        childInfo = IndexXmlReader.getChildElementByTagName(indexI, "vague_date");
        if(childInfo != null)
            getVagueInfo(titleStr, childInfo, info, PropType.DATE);

    }
    
    private static void getVagueInfo(String titleStr, Node infoI,
            IndexInfo info, PropType proptype) throws DataConfException,
            UnexpectedException {
        assert (proptype == PropType.NUM || proptype == PropType.DATE);
        boolean forNum = proptype == PropType.NUM;
        HashMap<FakeNumType, SemanticNode> vagueInfoNum = new HashMap<FakeNumType, SemanticNode>();
        HashMap<String, SemanticNode> vagueInfoDate = new HashMap<String, SemanticNode>();
        NodeList infonodes = infoI.getChildNodes();
        if (infonodes.getLength() == 0) {
            // 没有就不需继续解析
            return;
        }
        for (int i = 0; i < infonodes.getLength(); i++) {
            Node vagueI = infonodes.item(i);
            if (vagueI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            } else if (!vagueI.getNodeName().equals("vague")) {
                throw new DataConfException(confFileName, -1, "XML文件中未知节点：%s",
                        vagueI.getNodeName());
            }
            NamedNodeMap valsNodes = vagueI.getAttributes();
            Node textNode = valsNodes.getNamedItem("text");
            Node valNode = valsNodes.getNamedItem("value");
            if (textNode == null || valNode == null) {
                throw new DataConfException(confFileName, -1,
                        "XML文件中指标%s的默认值信息残缺：text：%s;value:%s", titleStr,
                        textNode, valNode);
            }
            String text = textNode.getNodeValue();
            String valStr = valNode.getNodeValue();
            SemanticNode val = null;

            try {
                val = forNum ? NumUtil.makeNumNodeFromStr(valStr) : DateUtil
                        .makeDateNodeFromStr(valStr,null);
            } catch (NotSupportedException e) {
                throw new DataConfException(confFileName, -1,
                        "XML文件中指标%s的模糊数值暂无法解析：%s", titleStr, valStr);
            }
            if (forNum) {
                FakeNumType fakeNumType = FakeNumDefInfo.parseFakeNumType(text);
                if (fakeNumType == null) {
                    throw new DataConfException(confFileName, -1,
                            "XML文件中指标%s的模糊类型暂无法解析：%s", titleStr, text);
                }
                vagueInfoNum.put(fakeNumType, val);
            } else {
                vagueInfoDate.put(text, val);
            }
        }
        if (forNum) {
            if (!info.vagueNumInfo.isEmpty()) {
                throw new DataConfException(confFileName, -1,
                        "XML文件中指标%s的数值不确定信息重复", titleStr);
            }
            info.vagueNumInfo = vagueInfoNum;
        } else {
            if (!info.vagueDateInfo.isEmpty()) {
                throw new DataConfException(confFileName, -1,
                        "XML文件中指标%s的时间不确定信息重复", titleStr);
            }
            info.vagueDateInfo = vagueInfoDate;
        }
    }


    private static void getValsInfo(String titleStr, Node infoI, IndexInfo info)
            throws DataConfException, UnexpectedException,
            NotSupportedException {
        NamedNodeMap valsNodes = infoI.getAttributes();
        Node maxNode = valsNodes.getNamedItem("max");
        Node minNode = valsNodes.getNamedItem("min");
        Node defNode = valsNodes.getNamedItem("default");
        Node movableRangeNode = valsNodes.getNamedItem("movable_range");
        Node webActionNode = valsNodes.getNamedItem("web_style");
        
        
        info.valType = getAttributeByName(infoI, "type");
        info.valUnit = getAttributeByName(infoI, "unit");
        info.valDefault = getAttributeByName(infoI, "default");
        info.valMatchType = getAttributeByName(infoI, "defaultMatchType");

        info.maxVal = getValsForIndex(titleStr, maxNode);
        info.minVal = getValsForIndex(titleStr, minNode);
        info.defVal = getValsForIndex(titleStr, defNode);

        parseMovable(movableRangeNode,info);
        
        checkIsEQ(info.maxVal);
        checkIsEQ(info.minVal);
        checkIsEQ(info.movableRange);
        
        parseWebActions(info,webActionNode);
    }

    private static void parseWebActions(IndexInfo info, Node webActionNode)
            throws UnexpectedException {
        if (webActionNode == null) {
            return;
        }
        String[] webActionsStrs = webActionNode.getNodeValue().split(";");
        if (webActionsStrs.length == 1 && webActionsStrs[0].trim().isEmpty()) {
            return;
        }
        for (int i = 0; i < webActionsStrs.length; i++) {
            WebAction addWebAction = getWebActionFromStr(webActionsStrs[i]);
            info.webActions.add(addWebAction);
        }

    }

    private static WebAction getWebActionFromStr(String webActionStr)
            throws UnexpectedException {
        //添加web处理信号，暂时只有一种
        WebAction webAction = null;
        if (webActionStr.equals(MiscDef.WEBSTYLE_HIGH_LIGHT)) {
            webAction = WebAction.HIGH_LIGHT;
        } else {
            throw new UnexpectedException("Unexpected web action :%s",
                    webActionStr);
        }
        return webAction;
    }

    private static void parseMovable(Node movableNode, IndexInfo info)
            throws NotSupportedException, UnexpectedException {
        if (movableNode == null || movableNode.getNodeValue() == null
                || movableNode.getNodeValue().trim().length() == 0) {
            return;
        }
        String movableStr = movableNode.getNodeValue();
        if (movableStr.equals("0")) {
            //当浮动范围设为0时，即为指标强制不浮动
            info.valIsMovable = false;
            return;
        }
        info.movableRange = NumUtil.makeNumNodeFromStr(movableStr);
    }

    private static void checkIsEQ(SemanticNode val) throws UnexpectedException {
        if (val == null || val.type != NodeType.NUM
                && val.type != NodeType.DATE) {
            return;
        } else if (val.type == NodeType.NUM
                && !((NumNode) val).getRangeType().equals(OperDef.QP_EQ)
                || val.type == NodeType.DATE
                && !((DateNode) val).getRangeType().equals(OperDef.QP_EQ)) {
            throw new UnexpectedException("Range is Not EQ");
        }
    }

    private static SemanticNode getValsForIndex(String titleStr, Node valNode)
            throws DataConfException, UnexpectedException,
            NotSupportedException {
        if (valNode == null) {
            return null;
        }
        
        
        String valStr = valNode.getNodeValue();
        boolean isNum = false;
        boolean isDate = false;
        ClassNodeFacade ontoClass = MemOnto.getOntoFirstOne(titleStr, ClassNodeFacade.class, qtype_);
        if(ontoClass!=null) {
            ontoClass.isDateIndex();
            ontoClass.isNumIndex();
        }

        if (isNum) {
            // 若为数字，按数字解析
            NumNode num = NumUtil.makeNumNodeFromStr(valStr);
            return num;
        } else if (isDate) {
            // 若为时间，按时间解析
            DateNode date = DateUtil.makeDateNodeFromStr(valStr,null);
            return date;
        } else {
            // 若都不是，就按字符串解析，ofWhat是指标的值属性
          return makeStrNodeFromStr(ontoClass, valStr);
        }
    }

    private static SemanticNode makeStrNodeFromStr(ClassNodeFacade ontoClass,
            String valStr) throws UnexpectedException {
        PropNodeFacade valProp = getValPropOfClass(ontoClass);
        if (valProp==null || !valProp.isStrProp()) {
            //throw new UnexpectedException("非字符串型指标");
        	return null;
        }
        StrNode valNode = new StrNode(valStr);
        //valNode.ofWhat = new ArrayList<SemanticNode>();
        //valNode.ofWhat.add(valProp);
        return valNode;
    }
    
    /**
     * 取得指标的值参数。此处所有的值参数均以“_”开头
     * TODO ??这么做对不对?
     * 
     * @param ontoClass
     *            指标
     * @return 取得的值参数，如“_数值”
     */
    public static PropNodeFacade getValPropOfClass(ClassNodeFacade ontoClass) {
    	if(ontoClass!=null && ontoClass.getAllProps()!=null) {
    		List<PropNodeFacade> props = ontoClass.getAllProps();
            for (PropNodeFacade pn : props) {
                if (pn.getText().startsWith("_")) {
                    return pn;
                }
            }
    	}
        
        return null;
    }

    private static void getParamInfo(String titleStr,
            Node infoNodeI, ArrayList<IFindParam> paramsInfo)
            throws DataConfException {

        String title = infoNodeI.getAttributes().getNamedItem("title").getNodeValue();
        String ifind_type = infoNodeI.getAttributes().getNamedItem("ifind_type").getNodeValue();
        String name = infoNodeI.getAttributes().getNamedItem("name").getNodeValue();
        
        String list_name = null;
        if(infoNodeI.getAttributes().getNamedItem("list_name")!=null)
        	list_name = infoNodeI.getAttributes().getNamedItem("list_name").getNodeValue();
        
        list_name= list_name==null||(list_name.trim()).length()==0?null:list_name;
        String default_val = infoNodeI.getAttributes().getNamedItem("default_val").getNodeValue();
        
        boolean hasTitle = title != null && (title.trim()).length() != 0;
        boolean hasIfindType = ifind_type != null
                && (ifind_type.trim()).length() != 0;
        boolean hasName = name != null && (name.trim()).length() != 0;
        boolean hasDefVal = default_val != null
                && (default_val.trim()).length() != 0;
        IFindParam newIfindParam;
        try {
            newIfindParam  = new IFindParam(name, default_val, title,
                    ifind_type, list_name);
        } catch (UnexpectedException e) {
            //throw new DataConfException(confFileName, -1, "构造指标“%s”的IFindParam时出错", titleStr);
            return ;
        }
        if (!hasTitle || !hasIfindType || !hasName || !hasDefVal) {
            //throw new DataConfException(confFileName, -1,"XML文件中指标“%s”的一条Param信息残缺", titleStr);
            return ;
        } else if (containParam(paramsInfo, newIfindParam)) {
            //throw new DataConfException(confFileName, -1,"XML文件中指标“%s”的“%s”Param信息重复", titleStr, title);
            return ;
        }
        paramsInfo.add(newIfindParam);
    }

    private static boolean containParam(ArrayList<IFindParam> paramList,
            IFindParam newIfindParam) {
        assert (newIfindParam != null);
        for (IFindParam param : paramList) {
            if (param.title.equals(newIfindParam.title)) {
                return true;
            }
        }
        return false;
    }
     
    private static ArrayList<Node> getChildElementsByTagName(Node parent, String tagName, String secondTagName)
    {
        ArrayList<Node> results = new ArrayList<Node>();
        
        NodeList childs = parent.getChildNodes();
        if (childs.getLength() == 0) {
            return results;
        }

        for (int i = 0; i < childs.getLength(); i++) {
            Node indexI = childs.item(i);
            if (indexI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }else if (!indexI.getNodeName().equals(tagName)) {
                continue;
            }

            return IndexXmlReader.getChildElementsByTagName(indexI, secondTagName);
        }
        return results;
    }

    private static ArrayList<Node> getChildElementsByTagName(Node parent, String tagName)
    {
        ArrayList<Node> results = new ArrayList<Node>();
        
        NodeList childs = parent.getChildNodes();
        if (childs.getLength() == 0) {
            return results;
        }

        for (int i = 0; i < childs.getLength(); i++) {
            Node indexI = childs.item(i);
            if (indexI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }else if (!indexI.getNodeName().equals(tagName)) {
                continue;
            }

            results.add(indexI);
        }
        return results;
    }

    private static Node getChildElementByTagName(Node parent, String tagName)
    {
        NodeList childs = parent.getChildNodes();
        if (childs.getLength() == 0) {
            return null;
        }

        for (int i = 0; i < childs.getLength(); i++) {
            Node indexI = childs.item(i);
            if (indexI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }else if (!indexI.getNodeName().equals(tagName)) {
                continue;
            }

            return indexI;
        }
        return null;
    }

    private static String getAttributeByName(Node node, String attributeName)
    {
        Node attr = node.getAttributes().getNamedItem(attributeName);
        return attr != null? attr.getNodeValue() : null;
    }
}
