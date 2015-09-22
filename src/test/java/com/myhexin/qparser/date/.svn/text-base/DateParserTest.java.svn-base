/**
 * 
 */
package com.myhexin.qparser.date;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



import com.myhexin.qparser.date.DateParser;
import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ParseLog;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.qa.MockQueryObject;

/**
 * @author yiming wu
 * 
 */
public class DateParserTest
{

    public ArrayList<String> readFile()
    {
        ArrayList<String> lineList = new ArrayList<String>();
        String useridr = System.getProperty("user.dir");
        String fileDirString = useridr
                + "/src/test/com/myhexin/qparser/date/tradedates.txt";

        File file = new File(fileDirString);
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null)
            {
                // 显示行号
                lineList.add(tempString);
                line++;
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return lineList;
    }

    private static ArrayList<SemanticNode> getSemanticNodes()
    {
        DateInfoNode dateInfoNodeform_1=new DateInfoNode(2012,5,6);
        DateInfoNode dateInfoNodeto_1=new DateInfoNode(2012,5,7);
        DateRange dateRange=new DateRange(dateInfoNodeform_1,dateInfoNodeto_1);
        DateNode dateNode1=new DateNode("2011年");
        dateNode1.setDateinfo(dateRange);
        DateNode dateNode2=new DateNode("2012年");
        dateNode2.setDateinfo(dateRange);
        ArrayList<SemanticNode>semanticNodes=new ArrayList<SemanticNode>();
        semanticNodes.add(dateNode1);
        semanticNodes.add(dateNode2);
        return semanticNodes;
    }
    private static ArrayList<ArrayList<String>> testlist(String text1,
            String text2, String text3)
    {
        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ar_1 = new ArrayList<String>();
        ar_1.add(text1);
        ar_1.add("date");
        ArrayList<String> ar_2 = new ArrayList<String>();
        ArrayList<String> ar_3 = new ArrayList<String>();

        ar_2.add(text2);
        ar_2.add("date");
        testList.add(ar_2);

        ar_3.add(text3);
        ar_3.add("date");

        testList.add(ar_1);
        testList.add(ar_2);
        testList.add(ar_3);
        return testList;
    }

    public void CompareableDateNode(DateNode targetDateNode,
            DateNode resultDateNode)
    {
        ArrayList<String> targetArrayList = new ArrayList<String>();
        ArrayList<String> resultArrayList = new ArrayList<String>();
        targetArrayList.add(targetDateNode.getText());
        targetArrayList.add(targetDateNode.getFrom().toString());
        targetArrayList.add(targetDateNode.getTo().toString());
        resultArrayList.add(resultDateNode.getText());
        resultArrayList.add(resultDateNode.getFrom().toString());
        resultArrayList.add(resultDateNode.getTo().toString());
        assertEquals(targetArrayList, resultArrayList);

    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testParser()
    {
        Query query = new Query("今天");

        Query query1 = new Query("今天");
        ParseLog parselog_ = new ParseLog(query);
        parselog_.logMsg("[ERROR]", "aaaa");
        DateParser dp = new DateParser();
        dp.parse(query.getNodes(), null);
        assertEquals(query.text, query1.text);

    }

    @Test
    public void testdealWithReportDateWithOutYear()
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();

        ArrayList<String> ar_1 = new ArrayList<String>();

        ar_1.add("1.5万");
        ar_1.add("num");

        baseList.add(ar_1);
        testList.add(ar_1);
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();

        MockQueryObject baseQueryObj = new MockQueryObject(baseList);
        Query basequery = baseQueryObj.getQuery();

        DateParser dp = new DateParser();
        assertEquals(query.getNodes().toString(), basequery.getNodes().toString());
    }

    @Test
    public void test_dealWithReportDateWithOutYear1()
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();

        ArrayList<String> ar_1 = new ArrayList<String>();

        ar_1.add("2012年");
        ar_1.add("date");
        baseList.add(ar_1);
        testList.add(ar_1);
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();

        DateNode dn = (DateNode) query.getNodes().get(0);
        DateRange dr = new DateRange();

        dr.setMaxUnit(Unit.YEAR);
        dn.setDateinfo(dr);

        MockQueryObject baseQueryObj = new MockQueryObject(baseList);
        Query basequery = baseQueryObj.getQuery();
        DateNode dn1 = (DateNode) basequery.getNodes().get(0);
        DateRange dr1 = new DateRange();

        dr1.setDateUnit(Unit.YEAR);
        dn1.setDateinfo(dr1);
        DateParser dp = new DateParser();
}

    @Test
    public void test_dealWithReportDateWithOutYear2()
    {

    }

    @Test
    /*
     * test @link DateParser.findLastDateNodeForReportDateWithOutYear
     * nodes.get(i).type != NodeType.DATE branch
     */
    public void test_findLastDateNodeForReportDateWithOutYear()
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ar_1 = new ArrayList<String>();
        ar_1.add("2012年");
        ar_1.add("num");
        testList.add(ar_1);
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        Method m = dp.getClass().getDeclaredMethod(
                "findLastDateNodeForReportDateWithOutYear",
                new Class[] { int.class });
        m.setAccessible(true);
        Object dn = m.invoke(dp, new Object[] { query.getNodes(),new Integer(1) });

        assertEquals(null, dn);
    }

    @Test
    /*
     * test @link DateParser.findLastDateNodeForReportDateWithOutYear
     * if (checkNodeIsRelativeDate(curNode)) branch
     */
    public void test_findLastDateNodeForReportDateWithOutYear1()
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ar_1 = new ArrayList<String>();
        ar_1.add("前2年");
        ar_1.add("date");
        testList.add(ar_1);
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        Method m = dp.getClass().getDeclaredMethod(
                "findLastDateNodeForReportDateWithOutYear",
                new Class[] { int.class });
        m.setAccessible(true);
        Object dn = m.invoke(dp, new Object[] { query.getNodes(),new Integer(1) });

        assertEquals(dn, null);

    }

    @Test
    /*
     * test @link DateParser.findLastDateNodeForReportDateWithOutYear
     * return DateNode branch
     */
    public void test_findLastDateNodeForReportDateWithOutYear2()
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ar_1 = new ArrayList<String>();
        ar_1.add("2012年");
        ar_1.add("date");
        testList.add(ar_1);
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateNode dn = (DateNode) query.getNodes().get(0);
        DateRange dr = new DateRange();

        dr.setMaxUnit(Unit.YEAR);
        dn.setDateinfo(dr);
        DateNode dn1 = new DateNode("2012年");
        dn1.setDateinfo(dr);
        DateParser dp = new DateParser();
        Method m = dp.getClass().getDeclaredMethod(
                "findLastDateNodeForReportDateWithOutYear",
                new Class[] { int.class });
        m.setAccessible(true);
        Object rtdn = m.invoke(dp, new Object[] { query.getNodes(),new Integer(1) });
        assertEquals(rtdn.toString(), dn1.toString());

    }

    @Test
    public void test_checkNodeIsRelativeDate() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException
    {
        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ar_1 = new ArrayList<String>();
        ar_1.add("2012年");
        ar_1.add("date");
        testList.add(ar_1);
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        Method m = dp.getClass().getDeclaredMethod("checkNodeIsRelativeDate",
                new Class[] { SemanticNode.class });
        m.setAccessible(true);
        Object bool = m.invoke(dp, new Object[] { query.getNodes(),new DateNode("2012年") });

        assertEquals(false, bool);
    }

    @Test
    public void test_addYear2ReportDate() throws NoSuchMethodException,
            SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {
        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ar_1 = new ArrayList<String>();
        ar_1.add("2012年");
        ar_1.add("date");
        testList.add(ar_1);
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        Method m = dp.getClass().getDeclaredMethod("addYear2ReportDate",
                new Class[] { int.class, DateRange.class });
        m.setAccessible(true);

        DateInfoNode base_to = new DateInfoNode(2012, 11, 20);
        DateInfoNode base_from = new DateInfoNode(2012, 11, 2);
        DateRange dr = new DateRange(base_from, base_to);
        m.invoke(dp, new Object[] { query.getNodes(),new Integer(2012), dr });
        assertEquals(dr.getFrom().toString(), base_from.toString());
        assertEquals(dr.getTo().toString(), base_to.toString());

    }

    @Test
    public void test_changeDateBySequence() throws NoSuchMethodException,
            SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            DataConfException
    {
        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ar_1 = new ArrayList<String>();
        ar_1.add("两年");
        ar_1.add("date");
        testList.add(ar_1);
        DateNode dn_ori = new DateNode("2年");
        dn_ori.oldNodes.add(dn_ori);

        DateUtil.loadTradeDate(readFile());
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        Method m = dp.getClass().getDeclaredMethod("changeDateBySequence",
                new Class[] { DateNode.class });
        m.setAccessible(true);
        m.invoke(dp, new Object[] { query.getNodes(),dn_ori });
        Calendar ca = Calendar.getInstance();

        String thisYear = String.format("%d-%02d-%02d",
                ca.get(Calendar.YEAR) - 1, 12, 31);
        String lastYear = String.format("%d-%02d-%02d",
                ca.get(Calendar.YEAR) - 2, 1, 1);

        assertEquals(dn_ori.getDateinfo().toString(), lastYear + " -- "
                + thisYear);
    }

    @Test
    public void test_changeDateBySequence1() throws NoSuchMethodException,
            SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            DataConfException
    {
        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ar_1 = new ArrayList<String>();
        ar_1.add("两年");
        ar_1.add("date");
        testList.add(ar_1);
        DateNode dn = new DateNode("两年");
        DateNode dn1 = new DateNode("两年");
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        Method m = dp.getClass().getDeclaredMethod("changeDateBySequence",
                new Class[] { DateNode.class });
        m.setAccessible(true);
        m.invoke(dp, new Object[] {query.getNodes(), dn });
        assertEquals(dn.toString(), dn1.toString());
    }

    @Test
    public void test_addFrequencyInfoTmp() throws NoSuchMethodException,
            SecurityException, IllegalAccessException,
            IllegalArgumentException, DataConfException, NoSuchFieldException
           
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年3月", "出现", "4月");
        DateUtil.loadTradeDate(readFile());
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        Method m = dp.getClass().getDeclaredMethod("addFrequencyInfoTmp");
        m.setAccessible(true);
        try
        {
            m.invoke(dp,new Object[]{query.getNodes()});
        }
        catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            
        }

    }

   

    @Test()
    public void test_dealWithReportDateWithOutYear()
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年3月", "出现", "4月");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        Method m = dp.getClass().getDeclaredMethod(
                "dealWithReportDateWithOutYear");
        m.setAccessible(true);
        m.invoke(dp,new Object[]{query.getNodes()});

    }

    @Test()
    public void test_connectRelativeDateWithOtherDate()
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, DataConfException, InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "2011年",
                "2012年");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        Method m = dp.getClass().getDeclaredMethod(
                "connectRelativeDateWithOtherDate");
        m.setAccessible(true);

        m.invoke(dp,new Object[]{query.getNodes()});

    }

    @Test()
    public void test_addDate() throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, DataConfException, InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "2011年",
                "2012年");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        Method m = dp.getClass().getDeclaredMethod("addDate");
        m.setAccessible(true);

        m.invoke(dp,new Object[]{query.getNodes()});

    }

    @Test()
    public void test_changeNumTypeDateByCompare() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "2011年",
                "2012年");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());

        Method m = dp.getClass().getDeclaredMethod(
                "changeNumTypeDateByCompare", DateNode.class);
        m.setAccessible(true);
        DateInfoNode dInfoNode_from = new DateInfoNode(1998, 1, 1);
        DateInfoNode dInfoNode_to = new DateInfoNode(2011, 1, 1);
        // 构造DateRange的对象
        DateRange dRange = new DateRange(dInfoNode_from, dInfoNode_to);
        DateNode dateNode = new DateNode("10年");
        dateNode.setDateinfo(dRange);

        m.invoke(dp, dateNode);

    }

    @Test()
    public void test_checkDateInfo() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "2011年",
                "2012年");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        DateInfoNode dInfoNode_from = new DateInfoNode(1998, 1, 1);
        Method m = dp.getClass().getDeclaredMethod("checkDateInfo",
                DateInfoNode.class);
        m.setAccessible(true);

        assertEquals(true, m.invoke(dp, dInfoNode_from));

    }

    
    @Test()
    public void test_checkForChangeNumDate() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "2011年",
                "2012年");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        DateInfoNode dInfoNode_from = new DateInfoNode(1998, 1, 1);
        Method m = dp.getClass().getDeclaredMethod("checkForChangeNumDate",
                SemanticNode.class);
        m.setAccessible(true);
       
        assertEquals(false, m.invoke(dp,query.getNodes().get(0)));

    }
    
    @Test()
    public void test_compareWithLength() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "2011年",
                "2012年");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        Method m = dp.getClass().getDeclaredMethod("compareWithLength"
);
        m.setAccessible(true);
       m.invoke(dp);
       

    }
    
    @Test()
    public void test_findLastDateNodeForRelativeDate() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException
            
    {

       
        Query query = new Query("22");
        query.getQueryNode().setNodes(getSemanticNodes());
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        Method m = dp.getClass().getDeclaredMethod("findLastDateNodeForRelativeDate",
                int.class);
        m.setAccessible(true);
        try
        {
            m.invoke(dp,1);
        }
        catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.getTargetException();
        }
    
       

    }
    
    @Test()
    public void test_getDateTreeNode() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "2011年",
                "2012年");
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        ArrayList<DateRange>dateRanges=new ArrayList<DateRange>();
        
        DateInfoNode dInfoNode_from = new DateInfoNode(1998, 1, 1);
        DateInfoNode dInfoNode_to = new DateInfoNode(2011, 1, 1);
        // 构造DateRange的对象
        DateRange dRange = new DateRange(dInfoNode_from, dInfoNode_to);
        dateRanges.add(dRange);
        Method m = dp.getClass().getDeclaredMethod("getDateTreeNode",
                ArrayList.class);
        m.setAccessible(true);


    }
    
    
    
    @Test()
    public void test_makeNumRangeByDateNode() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "2011年",
                "2012年");
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        ArrayList<DateRange>dateRanges=new ArrayList<DateRange>();
        
        DateInfoNode dInfoNode_from = new DateInfoNode(1998, 1, 1);
        DateInfoNode dInfoNode_to = new DateInfoNode(2011, 1,6);
        // 构造DateRange的对象
        DateRange dRange = new DateRange(dInfoNode_from, dInfoNode_to);
        dateRanges.add(dRange);
        Method m = dp.getClass().getDeclaredMethod("makeNumRangeByDateNode",
                DateNode.class);
        m.setAccessible(true);
        DateNode dateNode=new DateNode("2012年8月");
        dateNode.setDateinfo(dRange);

        NumRange numRange=new NumRange();
        numRange.setNumRange(157.00, 157.00);
        numRange.setBothInclude(true);
        assertEquals(numRange.toString(), m.invoke(dp,dateNode).toString());
       

    }
    
    
    @Test()
    public void test_reparseSequenceDateOfUnitDay() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "11年",
                "2012年");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        Method m = dp.getClass().getDeclaredMethod("reparseSequenceDateOfUnitDay");
        m.setAccessible(true);
       assertEquals(null,m.invoke(dp));
       

    }
    
    @Test()
    public void test_tagDate() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("2012年", "11年",
                "2012年");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        Method m = dp.getClass().getDeclaredMethod("tagDate");
        m.setAccessible(true);
       assertEquals(null,m.invoke(dp));
       

    }
    
    
    @Test()
    public void test_tagDateByOperAndDate() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("=2012年", "=",
                "=2012年");
        ArrayList<String> ar_1 = new ArrayList<String>();
        ar_1.add("=2013年");
        ar_1.add("date");
        testList.add(ar_1);
        
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        Method m = dp.getClass().getDeclaredMethod("tagDateByOperAndDate");
        m.setAccessible(true);
       assertEquals(null,m.invoke(dp));
       

    }
    
    
    @Test()
    public void test_tryConnect2RelativeDate() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("=2012年", "=",
                "=2012年");
        ArrayList<String> ar_1 = new ArrayList<String>();
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        
        DateInfoNode dInfoNode_from = new DateInfoNode(1998, 1, 1);
        DateInfoNode dInfoNode_to = new DateInfoNode(2011, 1,6);
        // 构造DateRange的对象
        DateRange dRange1 = new DateRange(dInfoNode_from, dInfoNode_to);
        
        DateInfoNode dInfoNode_from2 = new DateInfoNode(1998, 1, 1);
        DateInfoNode dInfoNode_to2 = new DateInfoNode(2011, 1,6);
        // 构造DateRange的对象
        DateRange dRange2 = new DateRange(dInfoNode_from2, dInfoNode_to2);
        DateNode dateNode1=new DateNode("2012年");
        dateNode1.setDateinfo(dRange1);
        DateNode dateNode2=new DateNode("3月");
        dateNode2.setDateinfo(dRange2);      
        Method m = dp.getClass().getDeclaredMethod("tryConnect2RelativeDate",
                DateNode.class,DateNode.class);
        m.setAccessible(true);
        m.invoke(dp,dateNode1,dateNode2);
       

    }
    
    
    @Test()
    public void test_tryTagDate2SequenceDate() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

        ArrayList<ArrayList<String>> testList = testlist("连续", "持续两年",
                "持续三年");
        ArrayList<String> ar_1 = new ArrayList<String>();
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        ArrayList<SemanticNode>semanticNodes=query.getNodes();
        DateInfoNode dInfoNode_from = new DateInfoNode(1998, 1, 1);
        DateInfoNode dInfoNode_to = new DateInfoNode(2011, 1,6);
        // 构造DateRange的对象
        DateRange dRange1 = new DateRange(dInfoNode_from, dInfoNode_to);
        
        DateInfoNode dInfoNode_from2 = new DateInfoNode(1998, 1, 1);
        DateInfoNode dInfoNode_to2 = new DateInfoNode(2011, 1,6);
        // 构造DateRange的对象
        DateRange dRange2 = new DateRange(dInfoNode_from2, dInfoNode_to2);
        DateNode dateNode1=new DateNode("2012年");
        dateNode1.setDateinfo(dRange1);
        DateNode dateNode2=new DateNode("3月");
        dateNode2.setDateinfo(dRange2);      
        Method m = dp.getClass().getDeclaredMethod("tryTagDate2SequenceDate",
                int.class,ArrayList.class,Direction.class);
        m.setAccessible(true);
        m.invoke(dp,0,semanticNodes,Direction.BOTH);
       

    }
    
    @Test
    public void test_checkdate() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchMethodException, DataConfException,
            InvocationTargetException
    {

      
        
        Query query = new Query("111");
        query.getQueryNode().setNodes(DateParserTest.getSemanticNodes());
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
       
        Method m = dp.getClass().getDeclaredMethod("checkDate");
        m.setAccessible(true);
        m.invoke(dp);
       //assertEquals(null,m.invoke(dp));
       

    }
    
    
    @Test
    public void test_addFrequencyInfoToDateByOtherDateOrNumber() throws DataConfException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Query query = new Query("111");
        query.getQueryNode().setNodes(DateParserTest.getSemanticNodes());
        DateParser dp = new DateParser();
        DateUtil.loadTradeDate(readFile());
        DateInfoNode dateInfoNodeform_1=new DateInfoNode(2012,5,6);
        DateInfoNode dateInfoNodeto_1=new DateInfoNode(2012,5,7);
        DateRange dateRange=new DateRange(dateInfoNodeform_1,dateInfoNodeto_1);
        DateNode dateNode1=new DateNode("2011年");
        dateNode1.setDateinfo(dateRange);
        DateNode dateNode2=new DateNode("2012年");
        dateNode2.setDateinfo(dateRange);
        NumNode numNode=new NumNode("222",11,112);
        Method m = dp.getClass().getDeclaredMethod("addFrequencyInfoToDateByOtherDateOrNumber",DateNode.class,SemanticNode.class);
        m.setAccessible(true);
        m.invoke(dp,dateNode1,numNode);
    }
    public static JUnit4TestAdapter suite()
    {
        return new JUnit4TestAdapter(DateParserTest.class);
    }

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }
}
