/**
 * 
 */
package com.myhexin.qparser.string;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.Test;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.qa.MockQueryObject;
import com.myhexin.qparser.tokenize.StringParser;

/**
 * @author RINA
 * 
 */
public class StringParserTest
{

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

    /**
     * Test method for
     * {@link com.myhexin.qparser.tokenize.StringParser#StringParser(com.myhexin.qparser.Query)}
     * .
     * 
     * @throws DataConfException
     * @throws NotSupportedException
     * @throws UnexpectedException
     */
    /*@Test
    public void testStringParser() throws DataConfException,
            UnexpectedException, NotSupportedException
    {
        ArrayList<ArrayList<String>> testList = testlist("2012年", "2011年",
                "2012年");
        ArrayList<String> test1 = new ArrayList<String>();
        ArrayList<String> test2 = new ArrayList<String>();
        test1.add("fuzzy_stop");
        test1.add("trigger");
        testList.add(test1);

        test2.add("fuzzy_stop");
        test2.add("str_val");
        testList.add(test2);

        QueryParser queryParser = QueryParser.getParser("./conf/qparser.conf");
        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();

        StringParser stringParser = new StringParser();

        Environment env = new Environment();
        env.put("qType", Query.Type.STOCK, true);
        stringParser.parse(env,query.getNodes());

    }*/

    /**
     * Test method for
     * {@link com.myhexin.qparser.tokenize.StringParser#mergeStrNodes(com.myhexin.qparser.Query)}
     * .
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws DataConfException
     * @throws NotSupportedException
     * @throws UnexpectedException
     */
    /*@Test
    public void testMergeStrNodes_StrNode() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException
    {
        ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();

        ArrayList<String> test_strval = new ArrayList<String>();

        ArrayList<String> test_strva2 = new ArrayList<String>();
        test_strval.add("大股东名称");
        test_strval.add("str_val");
        testList.add(test_strval);

        test_strva2.add("大股东名称");
        test_strva2.add("logic");
        testList.add(test_strva2);

        QueryParser queryParser = QueryParser.getParser("./conf/qparser.conf");

        MockQueryObject testQueryObj = new MockQueryObject(testList);
        Query query = testQueryObj.getQuery();
        ArrayList<SemanticNode> semanticNodes = query.getNodes();

        Method method = StringParser.class.getDeclaredMethod("mergeStrNodes",
                ArrayList.class, int.class);
        method.setAccessible(true);
        StringParser stringParser = new StringParser();

        try
        {
            method.invoke(stringParser, semanticNodes, new Object[]{query.getNodes(),-1});
        }
        catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.getTargetException();
        }

    }*/
    
    /**
     * Test method for
     * {@link com.myhexin.qparser.tokenize.StringParser#mergeStrNodes(com.myhexin.qparser.Query)}
     * .
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws DataConfException
     * @throws NotSupportedException
     * @throws UnexpectedException
     */
    /*@Test
    public void testMergeStrNodes_UnKonowNode() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException
    {

        QueryParser queryParser = QueryParser.getParser("./conf/qparser.conf");
        SemanticNode semanticNode1=new UnknownNode("111");
        SemanticNode semanticNode2=new UnknownNode("2222");
        ArrayList<SemanticNode> semanticNodes = new ArrayList<SemanticNode>() ;
        semanticNodes.add(semanticNode1);
        semanticNodes.add(semanticNode2);
        Query query=new Query("111");
        Method method = StringParser.class.getDeclaredMethod("mergeStrNodes",
                ArrayList.class, int.class);
        method.setAccessible(true);
        StringParser stringParser = new StringParser();

        try
        {
            method.invoke(stringParser, semanticNodes,  new Object[]{query.getNodes(),0});
        }
        catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.getTargetException();
        }

    }*/


}
