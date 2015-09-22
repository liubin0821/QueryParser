package com.myhexin.qparser.ifind;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.util.Util;

import junit.framework.TestCase;
import junit.framework.TestSuite;


public class JunitForIndexGroup extends TestCase{

    public JunitForIndexGroup() {
        try {
        	//后面的null需要改成相应的condition文件(如果后面需要加的话)
            //MemOnto.loadStockOnto(Util.readXMLFile(
            //        "./data/ifind_onto.xml", true),null);
            IndexInfo.loadStockIndexInfo(Util.readXMLFile(
                    "./data/ifind_index.xml", true));
            IndexGroup.loadInfo(Util.readXMLFile(
                    "./data/ifind_index_group.xml", true));
        } catch (DataConfException e) {
            System.err.println(e.toString());
        }
    }

    public void testIndexGroupCase1() {
        assertTrue(IndexGroup.getIndexGroupsSize() == 28);
        System.out.println(String.format("group 文件中共有%d个Group,总数量没有问题",
                IndexGroup.getIndexGroupsSize()));

    }

    public void testIndexGroupCase2() {
        IndexGroup info = IndexGroup.getGroupInfo("市盈率");
        assertTrue(info != null);
        assertTrue(info.group.size()==5);
        assertTrue(IndexInfo.getIndex(info.group.get(0).getText(), Query.Type.STOCK).pubTitle.equals("市盈率"));
        System.out.println("“市盈率”没有问题");
    }

    public void testIndexGroupCase3() {
        IndexGroup info = IndexGroup.getGroupInfo("年化波动率");
        assertTrue(info != null);
        assertTrue(info.group.size()==14);
        assertTrue(IndexInfo.getIndex(info.group.get(3).getText(), Query.Type.STOCK).pubTitle.equals("年化波动率"));
        System.out.println("“年化波动率”没有问题");
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static TestSuite suite() {
        return new TestSuite(JunitForIndexGroup.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }


}
