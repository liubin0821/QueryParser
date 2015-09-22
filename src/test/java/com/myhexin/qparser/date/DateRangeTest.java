/**
 * 
 */
package com.myhexin.qparser.date;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.myhexin.qparser.define.EnumDef.DateType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.OperDef;

/**
 * @author Administrator
 * 
 */
public class DateRangeTest
{
    private DateInfoNode dateInfoNode1 = new DateInfoNode(1980, 1, 1);
    private DateInfoNode dateInfoNode2 = new DateInfoNode(2050, 12, 31);
    private DateRange targetdateRange = new DateRange();

    /**
     * Test method for
     * {@link com.myhexin.qparser.date.DateRange#DateRange(com.myhexin.qparser.date.DateInfoNode, com.myhexin.qparser.date.DateInfoNode)}
     * .
     */
    @Test
    public void testDateRangeDateInfoNodeDateInfoNode()
    {

        targetdateRange.setDateRange(dateInfoNode1, dateInfoNode2);
        DateRange dateRange = new DateRange(null, null);
        assertEquals(targetdateRange.toString(), dateRange.toString());

    }

    /**
     * Test method for
     * {@link com.myhexin.qparser.date.DateRange#setDateRange(com.myhexin.qparser.date.DateInfoNode, com.myhexin.qparser.date.DateInfoNode)}
     * .
     */
    @Test
    public void testSetDateRange()
    {
        DateRange dateRange = new DateRange();
        dateRange.setDateRange(null, null);
        assertEquals(targetdateRange.toString(), dateRange.toString());

    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#clone()}.
     */
    @Test
    public void testClone()
    {
        assertEquals(targetdateRange.toString(),
                targetdateRange.clone().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#toDateString()}
     * .
     */
    @Test
    public void testToDateString_DisperseDatesIsNull()
    {
        String dateStr = String.format("%s TO %s",
                targetdateRange.getFrom().toString(),
                targetdateRange.getTo().toString());
        assertEquals(dateStr, targetdateRange.toDateString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#getRangeType()}
     * .
     */
    @Test
    public void testGetRangeType_TO()
    {
        DateInfoNode dateInfoNode1 = new DateInfoNode(1981, 1, 1);
        DateInfoNode dateInfoNode2 = new DateInfoNode(2050, 12, 31);
        DateRange targetDateRange = new DateRange(dateInfoNode1, dateInfoNode2);
        assertEquals(OperDef.QP_GT, targetDateRange.getRangeType());
    }

    /**
     * Test method for
     * {@link com.myhexin.qparser.date.DateRange#setHasYear(boolean)}.
     */
    @Test
    public void testSetHasYear()
    {
                 targetdateRange.setHasYear(true);
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#hasYear()}.
     */
    @Test
    public void testHasYear()
    {
        assertEquals(true,targetdateRange.hasYear());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#isLength()}.
     */
    @Test
    public void testIsLength()
    {
        assertEquals(false,targetdateRange.isLength());
    }


    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#getLength()}.
     */
    @Test
    public void testGetLength()
    {
        assertEquals(0, targetdateRange.getLength());
    }


    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#isReport()}.
     */
    @Test
    public void testIsReport()
    {
        assertEquals(false,targetdateRange.isReport());
    }

 

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#isTradeDay()}.
     */
    @Test
    public void testIsTradeDay()
    {
        assertEquals(false,targetdateRange.isTradeDay());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#getDateType()}.
     */
    @Test
    public void testGetDateType()
    {
        assertEquals(DateType.BOTH,targetdateRange.getDateType());
    }



    /**
     * Test method for
     * {@link com.myhexin.qparser.date.DateRange#isCanBeAdjust()}.
     */
    @Test
    public void testIsCanBeAdjust()
    {
        assertEquals(true,targetdateRange.isCanBeAdjust());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#countDay()}.
     */
    @Test
    public void testCountDay()
    {
        DateInfoNode dateInfoNode1 = new DateInfoNode(2011, 12, 31);
        DateInfoNode dateInfoNode2 = new DateInfoNode(2011, 12, 30);
        DateRange targetDateRange = new DateRange(dateInfoNode1, dateInfoNode2);
        assertEquals(1,targetDateRange.countDay());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#countYear()}.
     */
    @Test
    public void testCountYear()
    {assertEquals(70,targetdateRange.countYear());
        
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#countMonth()}.
     */
    @Test
    public void testCountMonth()
    {
        assertEquals(851,targetdateRange.countMonth());
    }

  

    /**
     * Test method for
     * {@link com.myhexin.qparser.date.DateRange#getDisperseDates()}.
     */
    @Test
    public void testGetDisperseDates()
    {
        assertEquals(null,targetdateRange.getDisperseDates());
    }


    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#isRelative()}.
     */
    @Test
    public void testIsRelative()
    {
        assertEquals(false,targetdateRange.isRelative());
    }



    /**
     * Test method for {@link com.myhexin.qparser.date.DateRange#getaxUnit()}.
     */
    @Test
    public void testGetMaxUnit()
    {
        assertEquals(null, targetdateRange.getMaxUnit());
    }

}
