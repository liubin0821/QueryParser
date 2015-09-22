package com.myhexin.qparser.onto;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.PredictNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumUtil;

public class InfoDef {
    public static HashMap<SemanticNode, HashMap<Object, PropNodeFacade>> getInfo(Document doc, HashMap<String, SemanticNode> dict) throws DataConfException {
        HashMap<SemanticNode, HashMap<Object, PropNodeFacade>> infos =
            new HashMap<SemanticNode, HashMap<Object, PropNodeFacade>>();
        Element root = doc.getDocumentElement();
        NodeList infonodes = root.getChildNodes();
        if (infonodes.getLength() == 0) {
            throw new DataConfException(Param.STOCK_DEFAULT_PROP_FILE, -1,
                    "XML文件为空");
        }
        int step = 0;
        for (int i = 0; i < infonodes.getLength(); i++) {
            Node info = infonodes.item(i);
            if (info.getNodeType() == Node.ELEMENT_NODE) {
                step++;
                String errInfo = String.format("第%d个点", step);
                String label = info.getAttributes().getNamedItem("label")
                        .getNodeValue();
                if (label.length() == 0 || label == null) {
                    throw new DataConfException(Param.STOCK_DEFAULT_PROP_FILE, -1,
                            errInfo + "的label为空");
                }
                if (info.getNodeName().equals("class")) {
                    SemanticNode cn = dict.get(label);
                    if (cn == null) {
                        throw new DataConfException(Param.STOCK_DEFAULT_PROP_FILE, -1, errInfo + "为Class，但在本体词典中没找到");
                    }
                    HashMap<Object, PropNodeFacade> info2Prop = new HashMap<Object, PropNodeFacade>();
                    info2Prop = getInfoMap(errInfo, info, dict);
                    infos.put(cn, info2Prop);
                } else if (info.getNodeName().equals("predict")) {
                    PredictNode pn = (PredictNode) dict.get(label);
                    if (pn == null) {
                        throw new DataConfException(Param.STOCK_DEFAULT_PROP_FILE, -1,
                                errInfo + "为Predict，但在本体词典中没找到");
                    }
                    HashMap<Object, PropNodeFacade> info2Prop = new HashMap<Object, PropNodeFacade>();
                    info2Prop = getInfoMap(errInfo, info, dict);
                    infos.put(pn, info2Prop);
                } else {
                    throw new DataConfException(Param.STOCK_DEFAULT_PROP_FILE, -1,
                            errInfo + "的类型错误");
                }
            }
        }
        return infos;

    }

    public static HashMap<Object, PropNodeFacade> getInfoMap(String errInfo,
            Node info, HashMap<String, SemanticNode> dict) throws DataConfException {
        HashMap<Object, PropNodeFacade> info2Prop = new HashMap<Object, PropNodeFacade>();
        for (Node node = info.getFirstChild(); node != null; node = node
                .getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("unit")) {
                    String unit = node.getAttributes().getNamedItem("label")
                            .getNodeValue();
                    String prop = node.getFirstChild().getNodeValue();
                    if (unit == null || prop == null) {
                        throw new DataConfException(Param.STOCK_DEFAULT_PROP_FILE, -1,
                                errInfo + "格式错误");
                    }
                    info2Prop.put(EnumConvert.getUnitFromStr(unit), (PropNodeFacade) dict.get(prop));
                } else if (node.getNodeName().equals("date")) {
                    String date = node.getAttributes().getNamedItem("label")
                            .getNodeValue();
                    String prop = node.getFirstChild().getNodeValue();
                    if (date == null
                            || prop == null
                            || (!date.equals("年") && !date.equals("月") && !date
                                    .equals("日"))) {
                        throw new DataConfException(Param.STOCK_DEFAULT_PROP_FILE, -1,
                                errInfo + "格式错误");
                    }
                    info2Prop.put(date, (PropNodeFacade) dict.get(prop));
                } else if (node.getNodeName().equals("funct")) {
                    String funct = node.getAttributes().getNamedItem("label")
                            .getNodeValue();
                    String prop = node.getFirstChild().getNodeValue();
                    if (funct == null || prop == null) {
                        throw new DataConfException(Param.STOCK_DEFAULT_PROP_FILE, -1,
                                errInfo + "格式错误");
                    }
                    info2Prop.put(funct, (PropNodeFacade) dict.get(prop));
                }
            }
        }
        return info2Prop;
    }
}
