/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-26 上午10:22:20
 * @description:   	
 * 
 */
package com.myhexin.qparser.date;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.number.NumRange;

public class DateFrequencyInfoTest {

	NumRange nr = new NumRange();
	private DateFrequencyInfo dfi = new DateFrequencyInfo(Unit.CI, nr, true);
	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-26 上午10:22:20
	 * @description:   	
	 * @throws java.lang.Exception
	 * 
	 */
	@Before
	public void setUp() throws Exception {
		nr.setNumRange("0", "2");
	}

	/**
	 * Test method for {@link com.myhexin.qparser.date.DateFrequencyInfo#DateFrequencyInfo(com.myhexin.qparser.define.EnumDef.Unit, com.myhexin.qparser.number.NumRange, boolean)}.
	 */

	/**
	 * Test method for {@link com.myhexin.qparser.date.DateFrequencyInfo#isUseless()}.
	 */
	@Test
	public void testIsUseless() {
		dfi.isUseless();
	}



	/**
	 * Test method for {@link com.myhexin.qparser.date.DateFrequencyInfo#getLength()}.
	 */
	@Test
	public void testGetLength() {
		dfi.getLength();
	}



	/**
	 * Test method for {@link com.myhexin.qparser.date.DateFrequencyInfo#getUnit()}.
	 */
	@Test
	public void testGetUnit() {
		dfi.getUnit();
	}


	/**
	 * Test method for {@link com.myhexin.qparser.date.DateFrequencyInfo#isSequence()}.
	 */
	@Test
	public void testIsSequence() {
		dfi.isSequence();
	}

	/**
	 * Test method for {@link com.myhexin.qparser.date.DateFrequencyInfo#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("length:0.00--2.00;unit:CI;isSequence:true", dfi.toString());
	}

	/**
	 * Test method for {@link com.myhexin.qparser.date.DateFrequencyInfo#toNumNode()}.
	 */
	@Test
	public void testToNumNode() {
		assertEquals(
				"NodeType:NUM  NodeText:0.00--2.00次  NumUnit:UNKNOWN  Num:(Num:0.0< num <2.0	Unit:null)",
				dfi.toNumNode().toString());
	}

	/**
	 * Test method for {@link com.myhexin.qparser.date.DateFrequencyInfo#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertEquals(true,dfi.equals(dfi));
		assertEquals(false,dfi.equals(null));
		assertEquals(false,dfi.equals(new Object()));
		

		DateFrequencyInfo dfi2 =null;
		
		
		dfi2 = new DateFrequencyInfo(Unit.CI, nr, true);
		assertEquals(true,dfi.equals(dfi2));
		
		dfi2.setSequence(false);
		assertEquals(false,dfi.equals(dfi2));
		
		dfi2.setUnit(Unit.BEI);
		assertEquals(false,dfi.equals(dfi2));
		
		NumRange nr2 = new NumRange();
		nr.setNumRange("0", "2");
		dfi2 = new DateFrequencyInfo(Unit.CI, nr2, true);
		assertEquals(false,dfi.equals(dfi2));
		

	}

}
