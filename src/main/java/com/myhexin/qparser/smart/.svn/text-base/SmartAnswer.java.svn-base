package com.myhexin.qparser.smart;

import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.except.DataConfException;

public class SmartAnswer {
    // 对于此类问题通用的答案集合
    public static ArrayList<SmartAnswerEntry> answerList = new ArrayList<SmartAnswerEntry>();

    /**
     * 我们还是用xml来进行智能回答问句的存储吧
     * 
     * @param doc
     * @throws DataConfException
     * @throws  
     */
    public static void loadSmartAnswer(Document doc) throws DataConfException {
        if (null == doc) {
            throw new DataConfException(Param.SMART_ANSWER_FILE, 0, "智能回答文件为空");
        }
        // 一个智能回答文集的一个family对应着一个SmartAnswerEntry
        ArrayList<SmartAnswerEntry> newAswList = new ArrayList<SmartAnswerEntry>();
        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpath = xfactory.newXPath();
            XPathExpression expr = xpath.compile("//family");
            NodeList familys=  (NodeList) expr.evaluate(doc,XPathConstants.NODESET);
            int familyCount = familys.getLength();
            Node family = null;
            NodeList familyMembers = null;
            for( int i = 0; i < familyCount; i++ ){
                family = familys.item(i);
                familyMembers = family.getChildNodes();
                int memberCount = familyMembers.getLength();
                Node member = null;
                SmartAnswerEntry aswEntry = new SmartAnswerEntry();
                for( int j = 0; j < memberCount; j++ ){
                    member = familyMembers.item(j);
                    String name = member.getNodeName();
                    NodeList memberChilds = null;
                    Node memberChild = null;
                    if( name.equals("query") ){
                        memberChilds = member.getChildNodes();
                        for( int k = 0; k < memberChilds.getLength(); k++ ){
                            memberChild = memberChilds.item(k);
                            if( memberChild.getNodeType() == Element.CDATA_SECTION_NODE ){
                                aswEntry.addRgx(memberChild.getTextContent());
                            }
                        }
                    }else if( name.equals("answer")){
                        memberChilds = member.getChildNodes();
                        for( int k = 0; k < memberChilds.getLength(); k++ ){
                            memberChild = memberChilds.item(k);
                            if( memberChild.getNodeType() == Element.CDATA_SECTION_NODE ){
                                aswEntry.addAsw(memberChild.getTextContent());
                            }
                        }
                    }
                }
                //正则和答案都至少要有一个，我们才加到答案集中去
                if( aswEntry.rgxPtnArray_.size() == 0 || aswEntry.aswArray_.size() == 0 ){
                    continue;
                }
                newAswList.add(aswEntry);
            }
            synchronized (answerList) {
                answerList = newAswList;
            }
        } catch (XPathExpressionException e) {
            throw new DataConfException(Param.SMART_ANSWER_FILE, 0, "智能回答文件格式错误:" + e.getMessage());
        }
    }
}
    
