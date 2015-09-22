/**
 * 
 */
package com.myhexin.qparser.date;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import com.myhexin.qparser.except.UnexpectedException;

/**
 * @author Administrator
 *
 */
public class DateInfoNodeTest
{
    private DateInfoNode dateInfoNode= new DateInfoNode(2012,9,26);
    private Calendar calendar=Calendar.getInstance();

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#isWeekEnd()}.
     */
    @Test
    public void testIsWeekEnd()
    {
        boolean target=true;
        calendar.add(Calendar.DAY_OF_WEEK,-1);
        target=(calendar.get(Calendar.DAY_OF_WEEK)==6)?target:false;
        assertEquals(target,dateInfoNode.isWeekEnd());
    }


    
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#isMonthEnd()}.
     */
    @Test
    public void testIsMonthEnd()
    {
        
        assertEquals(false,dateInfoNode.isWeekEnd());
    }

    
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#isQuarterEnd()}.
     */
    @Test
    public void testIsQuarterEnd()
    {
       
        assertEquals(false,dateInfoNode.isQuarterEnd());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#isYearEnd()}.
     */
    @Test
    public void testIsYearEnd()
    {
       
        assertEquals(false,dateInfoNode.isYearEnd());
    }

    
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#isHalfYearEnd()}.
     */
    @Test
    public void testIsHalfYearEnd()
    {
//        ArrayList<String> quarterArrayList=new ArrayList<>();
//        quarterArrayList.add("0630");
//        quarterArrayList.add("1231");
//        String time=String.format("%02d%d",calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
//        Boolean flag=true;
//        flag=(quarterArrayList.contains(time))?true:false;
        Boolean flag=false;
        assertEquals(flag,dateInfoNode.isHalfYearEnd());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getNextQuarterEnd()}.
     */
    @Test
    public void testGetNextQuarterEnd()
    {
//        int monthe=calendar.get(Calendar.MONTH)+1;
//        int quarterEnd=(monthe <=3&&monthe>=1)?4:(monthe>=4&&monthe<=6)?9:(monthe>=7&&monthe<=9)?12:3;
        
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,12,31);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getNextQuarterEnd().toString());
        
        
        
    }

 
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getLatestReportQuarter()}.
     */
    @Test
    public void testGetLatestReportQuarter()
    {
//        int monthe=calendar.get(Calendar.MONTH)+1;
//        int quarterEnd=(monthe <=3&&monthe>=1)?4:(monthe>=4&&monthe<=6)?9:(monthe>=7&&monthe<=9)?12:3;
        
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,6,30);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getLatestReportQuarter().toString());
    }
    
    
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getLatestReportHalfYear()}.
     */
    @Test
    public void testGetLatestReportHalfYear()
    {
        
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,6,30);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getLatestReportHalfYear().toString());
    }
    
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getLatestReportYear()}.
     */
    @Test
    public void testGetLatestReportYear()
    {
        
        DateInfoNode dateInfoNode1=new DateInfoNode(2011,12,31);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getLatestReportYear().toString());
    }
    

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getLastQuarterEnd()}.
     */
    @Test
    public void testGetLastQuarterEnd()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,6,30);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getLastQuarterEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getNextMonthEnd()}.
     * @throws UnexpectedException 
     */
    @Test
    public void testGetNextMonthEnd() throws UnexpectedException
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,10,31);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getNextMonthEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getLastMonthEnd()}.
     * @throws UnexpectedException 
     */
    @Test
    public void testGetLastMonthEnd() throws UnexpectedException
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,8,31);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getLastMonthEnd().toString());
    }
    
    
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getQuarterEnd()}.
     * @throws UnexpectedException 
     */
    @Test
    public void testGetQuarterEnd() throws UnexpectedException
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,9,30);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getQuarterEnd().toString());
    }
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getQuarterStart()}.
     * @throws UnexpectedException 
     */
    @Test
    public void testGetQuarterStart() throws UnexpectedException
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,7,1);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getQuarterStart().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getMonthEnd()}.
     */
    @Test
    public void testGetMonthEnd()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,9,30);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getMonthEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getMonthStart()}.
     */
    @Test
    public void testGetMonthStart()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,9,1);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getMonthStart().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getNextWeekEnd()}.
     * @throws UnexpectedException 
     */
    @Test
    public void testGetNextWeekEnd() throws UnexpectedException
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,10,6);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getNextWeekEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getLastWeekEnd()}.
     * @throws UnexpectedException 
     */
    @Test
    public void testGetLastWeekEnd() throws UnexpectedException
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,9,22);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getLastWeekEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getWeekEnd()}.
     * @throws UnexpectedException 
     */
    @Test
    public void testGetWeekEnd() throws UnexpectedException
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,9,29);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getWeekEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getWeekStart()}.
     * @throws UnexpectedException 
     */
    @Test
    public void testGetWeekStart() throws UnexpectedException
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,9,23);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getWeekStart().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getNextHalfYearEnd()}.
     */
    @Test
    public void testGetNextHalfYearEnd()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2013,6,30);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getNextHalfYearEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getLastHalfYearEnd()}.
     */
    @Test
    public void testGetLastHalfYearEnd_afterSeven()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,6,30);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getLastHalfYearEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getLastHalfYearEnd()}.
     */
    @Test
    public void testGetLastHalfYearEnd_beforeSeven()
    {
        DateInfoNode dateInfoNode= new DateInfoNode(2012,5,26);
        DateInfoNode dateInfoNode1=new DateInfoNode(2011,12,31);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getLastHalfYearEnd().toString());
    }
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getHalfYearEnd()}.
     */
    @Test
    public void testGetHalfYearEnd_afterSeven()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,12,31);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getHalfYearEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getHalfYearEnd()}.
     */
    @Test
    public void testGetHalfYearEnd_beforeSeven()
    {
        DateInfoNode dateInfoNode= new DateInfoNode(2012,5,26);
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,6,30);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getHalfYearEnd().toString());
    }
    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getHalfYearStart()}.
     */
    @Test
    public void testGetHalfYearStart()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,7,1);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getHalfYearStart().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getNextYearEnd()}.
     */
    @Test
    public void testGetNextYearEnd()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2013,12,31);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getNextYearEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getLastYearEnd()}.
     */
    @Test
    public void testGetLastYearEnd()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2011,12,31);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getLastYearEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getYearEnd()}.
     */
    @Test
    public void testGetYearEnd()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,12,31);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getYearEnd().toString());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getYearStart()}.
     */
    @Test
    public void testGetYearStart()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,1,1);
        assertEquals(dateInfoNode1.toString(),dateInfoNode.getYearStart().toString());
    }

  

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getYear()}.
     */
    @Test
    public void testGetYear()
    {
        int year=2012;
        assertEquals(year,dateInfoNode.getYear());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getMonth()}.
     */
    @Test
    public void testGetMonth()
    {
        int month=9;
        assertEquals(month,dateInfoNode.getMonth());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getDay()}.
     */
    @Test
    public void testGetDay()
    {
        int date=26;
        assertEquals(date,dateInfoNode.getDay());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getWeek()}.
     */
    @Test
    public void testGetWeek()
    {
        int week=3;
        assertEquals(week,dateInfoNode.getWeek());
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#compareTo(com.myhexin.qparser.date.DateInfoNode)}.
     */
    @Test
    public void testCompareTo()
    {
        DateInfoNode dateInfoNode1=new DateInfoNode(2012,9,26);
        int target=0;
        assertEquals(target,dateInfoNode.compareTo(dateInfoNode1));
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#toString(java.lang.String)}.
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    @Test
    public void testToStringString() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
//        Field yearField=dateInfoNode.getClass().getDeclaredField("Year");
//        yearField.setAccessible(true);
//        int year =(int) yearField.get(dateInfoNode);
//        
//        Field monthField=dateInfoNode.getClass().getDeclaredField("Month");
//        monthField.setAccessible(true);
//        int month =(int) monthField.get(dateInfoNode);
//        
//        Field dayField=dateInfoNode.getClass().getDeclaredField("Day");
//        dayField.setAccessible(true);
//        int day =(int) dayField.get(dateInfoNode);      
        
        String targetString=String.format("%4d%s%02d%s%02d",2012,"-",9,"-",26);
        assertEquals(targetString,dateInfoNode.toString("-"));
        
    }



    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#equals(com.myhexin.qparser.date.DateInfoNode)}.
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    @Test
    public void testEqualsDateInfoNode() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException     
    {
//      Field yearField=dateInfoNode.getClass().getDeclaredField("Year");
//      yearField.setAccessible(true);
//      int year =(int) yearField.get(dateInfoNode);
//      
//      Field monthField=dateInfoNode.getClass().getDeclaredField("Month");
//      monthField.setAccessible(true);
//      int month =(int) monthField.get(dateInfoNode);
//      
//      Field dayField=dateInfoNode.getClass().getDeclaredField("Day");
//      dayField.setAccessible(true);
//      int day =(int) dayField.get(dateInfoNode); 
      
      assertEquals(true,dateInfoNode.equals(dateInfoNode));
      
      
      
    }



    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#isAfter(com.myhexin.qparser.date.DateInfoNode)}.
     */
    @Test
    public void testIsAfter()
    {
        DateInfoNode dateInfoNode2=new DateInfoNode(2012,9,25);
        assertEquals(true, dateInfoNode.isAfter(dateInfoNode2));
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#isEarlier(com.myhexin.qparser.date.DateInfoNode)}.
     */
    @Test
    public void testIsEarlier()
    {
        DateInfoNode dateInfoNode2=new DateInfoNode(2012,9,27);
        assertEquals(true, dateInfoNode.isEarlier(dateInfoNode2));
    }

    /**
     * Test method for {@link com.myhexin.qparser.date.DateInfoNode#getMonthday()}.
     */
    @Test
    public void testGetMonthday()
    {
        int sumDay=30;
        assertEquals(30,dateInfoNode.getMonthday());
    }

}
