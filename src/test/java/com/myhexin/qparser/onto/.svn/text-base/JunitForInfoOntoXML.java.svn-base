package com.myhexin.qparser.onto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Document;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.Util;

public class JunitForInfoOntoXML extends TestCase{
    public HashMap<String, SemanticNode> all_ = new HashMap<String, SemanticNode>();
    
    public JunitForInfoOntoXML() {
        try {
            Document doc;
            doc = Util.readXMLFile("./data/ifind_onto.xml", true);
            all_ = null;
        } catch (DataConfException e) {
            System.err.println(e.toString());
        }
       
    }
    public void testOntoDict() {
       
        SemanticNode spn1 = all_.get("_数值");
        assertTrue(spn1 != null);
        assertTrue(spn1.type == NodeType.PROP);
        SemanticNode spn2 = all_.get("_日期");
        assertTrue(spn2 != null);
        assertTrue( spn2.type == NodeType.PROP);
        
        SemanticNode cn1 = all_.get("收盘价");
        assertTrue(cn1 != null);
        assertTrue(cn1.type == NodeType.CLASS);
        SemanticNode cn2 = all_.get("首发上市日期");
        assertTrue(cn2 != null);
        assertTrue(cn2.type == NodeType.CLASS);
        
        PropNodeFacade pn1 = (PropNodeFacade)spn1;
        PropNodeFacade pn2 = (PropNodeFacade)spn2;
        
        assertTrue(((ClassNodeFacade) cn1).hasProp(pn1));
        assertTrue(((ClassNodeFacade) cn2).hasProp(pn2));

        Set<String> keys = all_.keySet();
        for (String key : keys) {
            if (all_.get(key).type != NodeType.CLASS) {
                continue;
            }
            boolean hasVal = false;
            ClassNodeFacade onto = (ClassNodeFacade) all_.get(key);
            for (int i = 0; i < onto.getAllProps().size(); i++) {
                boolean isNumVal = onto.hasProp(pn1);
                boolean isDateVal = onto.hasProp(pn2);
                
                if (onto.getAllProps().get(i).getText().startsWith("_")
                        || onto.getAllProps().get(i).getText().startsWith("~")) {
                    hasVal = true;
                    if (!isNumVal && !isDateVal) {
                       // System.out.println(String.format("%s\t%s", onto.text,
                       //         onto.getAllProps().get(i).text));
                    }
                }
            }
            if(!hasVal){
                String err = String.format("[ERR]“%s”指标缺少值", onto.getText());
                System.out.println(err);
            }
            assertTrue(hasVal);
        }
    }
    public void testOntoDict2() {
        SemanticNode cn = all_.get("应收账款周转天数");
        assertTrue(cn.type==NodeType.CLASS);
        ClassNodeFacade realCn = (ClassNodeFacade) cn;
        List<PropNodeFacade> props = realCn.getAllProps();
        List<PropNodeFacade> dateProps = realCn.getProps(PropType.DATE);
        PropNodeFacade dateProp = realCn.getProp(PropType.DATE);
        System.out.println("以下为指标所有参数按顺序输出");
        for(int i=0;i<props.size();i++){
            System.out.println(props.get(i).getText());
        }
        System.out.println("以下为指标所有时间参数按顺序输出");
        for(int i=0;i<dateProps.size();i++){
            System.out.println(dateProps.get(i).getText());
        }
        System.out.println("以下为指标取出时间参数");
        System.out.println(dateProp.getText());
    }
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public static TestSuite suite() {
        return new TestSuite(JunitForInfoOntoXML.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
