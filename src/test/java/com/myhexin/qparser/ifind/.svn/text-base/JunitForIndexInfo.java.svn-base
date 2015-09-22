package com.myhexin.qparser.ifind;

import java.util.HashMap;

import org.w3c.dom.Document;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

import junit.framework.TestCase;
import junit.framework.TestSuite;


public class JunitForIndexInfo extends TestCase {
    public HashMap<String, IndexInfo> info_ = new HashMap<String, IndexInfo>();

    public JunitForIndexInfo() {
        Document doc;
        try {
            doc = Util.readXMLFile("./data/ifind_index.xml", true);
            IndexInfo.loadStockIndexInfo(doc);
        } catch (DataConfException e) {
            System.err.println(e.toString());
        }
    }

    public void testIndexDictCase2() {
        IndexInfo info1 = IndexInfo.getIndex("净利润", Query.Type.STOCK);
        assertTrue(info1 != null);
        assertTrue(info1.id.equals("01111"));
        assertTrue(info1.changes == null || info1.changes.size() == 0);
        assertTrue(info1.params != null);
        for (IFindParam ip : info1.params) {
            assertTrue(ip.toString().equals(
                    "title:报表类型;defVal:1;oldType:dt_integer")
                    || ip.toString().equals(
                            "title:报告期;defVal:20111231;oldType:dt_YQ"));
        }
        System.out.println("“净利润”没有问题");
    }

    public void testIndexDictCase3() {
        IndexInfo info2 = IndexInfo.getIndex("首发上市日期", Query.Type.STOCK);
        assertTrue(info2 != null);
        assertTrue(info2.id.equals("00427"));
        assertTrue(info2.changes == null || info2.changes.size() == 0);
        assertTrue(info2.params == null || info2.params.size() == 0);
        System.out.println("“首发上市日期”没有问题");
    }

    public void testIndexDictCase4() {
        IndexInfo info2 = IndexInfo.getIndex("市盈率(pe)", Query.Type.STOCK);
        assertTrue(info2 != null);
        assertTrue(info2.id.equals("00577"));
        assertTrue(info2.changes == null || info2.changes.size() == 0);
        assertTrue(info2.params != null);
        System.out.println("“市盈率(pe)”没有问题");
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static TestSuite suite() {
        return new TestSuite(JunitForIndexInfo.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
