package com.myhexin.qparser.number;



import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;






import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.EnumDef.OperatorType;
import com.myhexin.qparser.except.*;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.NumNode.MoveType;
import com.myhexin.qparser.number.NumRange;

/**
 * @author admin
 *
 */
public class TestNumParserUtils {


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_staticparseChineseNumber() throws QPException {
		long num = NumUtil.parseChineseNumber("两");
		assertEquals(2l,num);
	}
	
	@Test(expected=NotSupportedException.class)
	public void test_staticparseChineseNumber2() throws QPException{
		long num = NumUtil.parseChineseNumber("你好");
		//assertEquals(-1l,num);
	}
	
	@Test
	public void test_staticparseChineseNumber3() throws QPException{
		long num = NumUtil.parseChineseNumber("百万");
		assertEquals(1000000l,num);	
	}
	
	@Test
	public void test_staticparseChineseNumber4() throws QPException{
		long num = NumUtil.parseChineseNumber("肆千");
		assertEquals(4000l,num);
	}
	
	@Test
	public void test_privateparseChineseNumber1() 
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		NumUtil nu = new NumUtil();

		Method m = nu.getClass().getDeclaredMethod("parseChineseNumber", new Class[]{String.class,int.class});
		m.setAccessible(true);
		Object rlt = m.invoke(nu, new Object[]{new String("零千"),new Integer(1)});
		assertEquals(0l,rlt);
		
		Object rlt1 = m.invoke(nu, new Object[]{new String("五亿"),new Integer(1)});
		assertEquals(500000000l,rlt1);
		
		Object rlt2 = m.invoke(nu, new Object[]{new String("三万"),new Integer(1)});
		assertEquals(30000l,rlt2);
		
		Object rlt3 = m.invoke(nu, new Object[]{new String("四千"),new Integer(1)});
		assertEquals(4000l,rlt3);
		
		Object rlt4 = m.invoke(nu, new Object[]{new String("二百"), new Integer(1)});
		assertEquals(200l,rlt4);
		
		Object rlt5 = m.invoke(nu, new Object[]{new String("七十"), new Integer(1)});
		assertEquals(70l,rlt5);
	}
	
	@Test
	public void test_privateparseChineseNumber2() 
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		NumUtil nu = new NumUtil();

		Method m = nu.getClass().getDeclaredMethod("parseChineseNumber", new Class[]{String.class,int.class});
		m.setAccessible(true);
		
		Object rlt = m.invoke(nu, new Object[]{new String("九"),new Integer(1)});
		assertEquals(9l,rlt);
		
		Object rlt1 = m.invoke(nu, new Object[]{new String("五百五十五万六千六"), new Integer(1)});
		assertEquals(5556600l,rlt1);
		
	}
	
	@Test 
	public void test_getArabic1() throws QPException{
		assertEquals("5",NumUtil.getArabic("五"));
		assertEquals("5000000",NumUtil.getArabic("5百万"));
		assertEquals("5000000",NumUtil.getArabic("五百万"));
		 
	}
	
	@Test
	public void test_getNumUnit1(){
		Unit unit = Unit.UNKNOWN;
		assertEquals(unit,EnumConvert.getUnitFromStr(""));
		assertEquals(Unit.GU,EnumConvert.getUnitFromStr("股"));
		assertEquals(Unit.ZHI,EnumConvert.getUnitFromStr("支"));
	}
	
	@Test
	public void test_changeNRByOP(){
		/**
		 * to do after struct node function done
		 */
	}
	
	@Test
	public void test_changeDoubleByOP() 
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		NumUtil nu = new NumUtil();
		Method m = nu.getClass().getDeclaredMethod("changeDoubleByOp", new Class[]{Double.class,OperatorType.class,Double.class});
		m.setAccessible(true);
		OperatorType e = OperatorType.ADD;
		Object rlt = m.invoke(nu, new Object[]{new Double(10), e,new Double(10)});
		assertEquals(20.0,rlt);
		e = OperatorType.MULTIPLY;
		Object rlt1 = m.invoke(nu, new Object[]{new Double(5), e,new Double(10)});
		assertEquals(50.0,rlt1);
	}
	

	@Test(expected=UnexpectedException.class)
	public void test_beitopercent() throws UnexpectedException{
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		
		nr.setNumRange("100000", "100000");
		nr.setUnit("%");
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");
		NumRange nr1 = new NumRange();
		nr1.setNumRange("1000", "1000");
		test_nn.setNuminfo(nr1);
		NumUtil.changeBeiToPercent(test_nn);
		assertEquals(nn.toString(),test_nn.toString());
		assertEquals(nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
		assertEquals(nn.getUnit().toString(),test_nn.getUnit().toString());

	}
	
	@Test(expected=UnexpectedException.class)
	public void test_beitopercent1() throws UnexpectedException{
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		
		nr.setNumRange(null, 10.0);
		nr.setUnit("%");
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");
		NumRange nr1 = new NumRange();
		nr1.setNumRange(null, 0.1);
		test_nn.setNuminfo(nr1);
		NumUtil.changeBeiToPercent(test_nn);
		
		assertEquals("numnode not the same",nn.toString(),test_nn.toString());
		assertEquals("numinfo not the same",nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
		assertEquals(nn.getUnit().toString(),test_nn.getUnit().toString());

	}
	
	@Test(expected=UnexpectedException.class)
	/*
	 *  test {@link NumUtil.changeBeiToPercent()}
	 */
	public void test_beitopercent2() throws UnexpectedException{
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		
		nr.setNumRange(10.0,null); 
		nr.setUnit("%");
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");
		NumRange nr1 = new NumRange();
		nr1.setNumRange(0.1,null);
		nr1.setIncludeFrom(true);
		test_nn.setNuminfo(nr1);
		NumUtil.changeBeiToPercent(test_nn);
		
		assertEquals("numnode not the same",nn.toString(),test_nn.toString());
		assertEquals("numinfo not the same",nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
		assertEquals(nn.getUnit().toString(),test_nn.getUnit().toString());

	}
	
	@Test
	/*
	 * test (numType.equals(OperDef.QP_IN) && nr.getDoubleFrom() <= 0 condition
	 */
	public void test_changenumfordecrease1(){
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		
		nr.setNumRange(-10.0,0.0); 
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");

		test_nn.setNuminfo(nr);
		NumUtil.changeNumForDecrease(test_nn);
		assertEquals(nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
	}
	
	@Test
	/*
	 * test (numType.equals(OperDef.QP_LT) && nr.getDoubleTo() <= 0 condition
	 */
	public void test_changenumfordecrease2(){
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		
		nr.setNumRange(null,-0.1); 
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");

		test_nn.setNuminfo(nr);
		NumUtil.changeNumForDecrease(test_nn);
		assertEquals(nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
	}
	
	@Test
	/*
	 * test NumUtil.changeNumForDecrease 
	 * (numType.equals(OperDef.QP_LT) || numType
	 *			.equals(OperDef.QP_LE)) && nr.getDoubleTo() > 0)
	 */
	public void test_changenumfordecrease3(){
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		
		nr.setNumRange(-0.1,0.0); 
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");
		NumRange nr1 = new NumRange();
		nr1.setNumRange(null,0.1);
		test_nn.setNuminfo(nr1);
		NumUtil.changeNumForDecrease(test_nn);
		assertEquals(nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
	}
	
	@Test
	/*
	 * test NumUtil.changeNumForDescrease
	 * (numType.equals(OperDef.QP_GT) || numType
	 *			.equals(OperDef.QP_GE)) && nr.getDoubleFrom() <= 0
	 */
	public void test_changenumfordescrease4(){
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		
		nr.setNumRange(-0.1,0.0); 
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");
		NumRange nr1 = new NumRange();
		nr1.setNumRange(-0.1,null);
		test_nn.setNuminfo(nr1);
		NumUtil.changeNumForDecrease(test_nn);
		assertEquals(nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
	}
	
	@Test
	/*
	 * test NumUtil.changeNumForDescrease
	 * (numType.equals(OperDef.QP_EQ) || numType
	 *			.equals(OperDef.QP_NE)) && nr.getDoubleTo() > 0)
	 */
	public void test_changenumfordescrease5(){
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		
		nr.setNumRange(-0.1,-0.1); 
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");
		NumRange nr1 = new NumRange();
		nr1.setNumRange(0.1,0.1);
		test_nn.setNuminfo(nr1);
		NumUtil.changeNumForDecrease(test_nn);
		assertEquals(nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
	}
	
	@Test
	/*
	 * test NumUtil.changeNumForDescrease condition
	 * (numType.equals(OperDef.QP_IN) || numType
	 *			.equals(OperDef.QP_NI)) && nr.getDoubleFrom() > 0)
	 */
	public void test_changenumfordescrease6(){
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		
		nr.setNumRange(-0.2,-0.1); 
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");
		NumRange nr1 = new NumRange();
		nr1.setNumRange(0.1,0.2);
		test_nn.setNuminfo(nr1);
		NumUtil.changeNumForDecrease(test_nn);
		assertEquals(nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
	}
	
	@Test
	public void test_getunitstrbyunit(){
		assertEquals("户",EnumConvert.getStrFromUnit(Unit.HU));		
	}

	
	@Test(expected=NotSupportedException.class)
	public void test_makenumnodefromstr() throws NotSupportedException, UnexpectedException {
			NumUtil.makeNumNodeFromStr("你好");	
	}
	
	@Test
	public void test_makenumnodeformstr1() throws NotSupportedException, UnexpectedException{
		NumNode nd = new NumNode("10000");
		NumRange nr = new NumRange();
		nr.setNumRange(10000.0, 10000.0);
		nd.setNuminfo(nr);
		assertEquals(nd.getNuminfo().toString(),NumUtil.makeNumNodeFromStr("10000").getNuminfo().toString());
	}
	
	@Test(expected=UnexpectedException.class)
	/*
	 * test NumUtil.changeNRForMovableRange
	 * !valNum.getRangeType().equals(OperDef.QP_EQ) condition
	 */
	public void test_changenrformoveablerange1() throws UnexpectedException{
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		nr.setNumRange(0.1,0.2);
		nn.setNuminfo(nr);
		Unit ut = Unit.SHOU;
		MoveType mt = MoveType.DOWN;
		NumUtil.changeNRForMovableRange(nn, 0.0, ut, mt, false);
	}
	
	@Test
	/*
	 * test NumUtil.changeNRForMovableRange
	 * moveUnit == Unit.PERCENT condition
	 */
	public void test_changenrformoveablerange2() throws UnexpectedException{
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		nr.setNumRange(100.0,105.0);
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");
		NumRange nr1 = new NumRange();
		nr1.setNumRange(100.0,100.0);
		test_nn.setNuminfo(nr1);
		Unit ut = Unit.PERCENT;
		MoveType mt = MoveType.UP;
		NumUtil.changeNRForMovableRange(test_nn, 5.0, ut, mt, false);
		assertEquals(nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
	}
	
	@Test
	/*
	 * test NumUtil.changeNRForMovableRange
	 * else condition
	 */
	public void test_changenrformoveablerange3() throws UnexpectedException{
		NumNode nn = new NumNode("1000");
		NumRange nr = new NumRange();
		nr.setNumRange(100.0,105.0);
		nn.setNuminfo(nr);
		
		NumNode test_nn = new NumNode("1000");
		NumRange nr1 = new NumRange();
		nr1.setNumRange(100.0,100.0);
		test_nn.setNuminfo(nr1);
		Unit ut = Unit.DAY;
		MoveType mt = MoveType.UP;
		NumUtil.changeNRForMovableRange(test_nn, 5.0, ut, mt, false);
		assertEquals(nn.getNuminfo().toString(),test_nn.getNuminfo().toString());
	}
	
	
	public static JUnit4TestAdapter suite() {
		return new JUnit4TestAdapter(TestNumParserUtils.class);
	}
	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

}
