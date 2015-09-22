package com.myhexin.qparser.ifind;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.StrStrPair;

public class IFindParamList {
    private static HashMap<String, IFindParamList> info_ = null;
    public static void loadParamListInfo(String paramFile) throws DataConfException {
        try {
            info_ = new HashMap<String, IFindParamList>();
            
            FileInputStream fis;
                fis = new FileInputStream(paramFile);
            InputSource inputSource = new InputSource(fis);
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile("//param");
            NodeList nodes = (NodeList)expr.evaluate(inputSource, XPathConstants.NODESET);
            expr = xpath.compile("child::item");
            
            for(int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if(node.getNodeType() != Node.ELEMENT_NODE) { continue; }
                IFindParamList pli = new IFindParamList();
                pli.listName = node.getAttributes().getNamedItem("name").getNodeValue();
                pli.dataType = node.getAttributes().getNamedItem("datatype").getNodeValue();
                NodeList items = (NodeList)expr.evaluate(node, XPathConstants.NODESET);
                for(int j = 0; j < items.getLength(); j++) {
                    Node item = items.item(j);
                    String name = item.getAttributes().getNamedItem("name").getNodeValue();
                    String value = item.getAttributes().getNamedItem("value").getNodeValue();
                    pli.title2value_.add(new StrStrPair(name, value));
                }
                info_.put(pli.listName, pli);
            }
            fis.close();
        } catch (Exception e) {
            throw new DataConfException(paramFile, -1, e.getMessage());
        }
    }
    
    public static IFindParamList getList(String listName) {
        return info_.get(listName);
    }
    
    private IFindParamList() {}

    /**
     * 根据参数值的标题获取其在参数列表中的索引
     * @param title 参数值的标题
     * @return 对应的参数列表索引，若不存在此值标题，返回<code>null</code>
     * @see #getTitle(String)
     */
    public String getValue(String title) {
        for(StrStrPair pair : title2value_) {
            if(pair.first.equals(title)) {
                return pair.second;
            }
        }
        return null;
    }
    
    /**
     * 根据参数值在参数列表中的索引获取其标题
     * @param title 参数值的索引
     * @return 对应的参数值的索引，若不存在此索引，返回<code>null</code>
     */
    public String getTitle(String value) {
        for(StrStrPair pair : title2value_) {
            if(pair.second.equals(value)) {
                return pair.first;
            }
        }
        return null;
    }
    
    public ArrayList<StrStrPair> getTitle2Vals(){
        return title2value_;
    }
    
    public int size(){
        return title2value_.size();
    }
    
    public String listName = null;
    public String dataType = null;
    private ArrayList<StrStrPair> title2value_ = new ArrayList<StrStrPair>();
}
