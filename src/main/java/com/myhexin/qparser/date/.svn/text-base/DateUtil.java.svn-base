package com.myhexin.qparser.date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.GrowthType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.define.UnitDef;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.Pair;
import com.myhexin.qparser.util.Util;

public class DateUtil {
	
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(DateUtil.class.getName());
    
    
    
    private static String[] allTradeDatesArr = null;
    private static int[] tradeDateInts = null;
    private static Map<String, Integer> tradeDateIndexMap = null;
    
	
	/**
     * 获取某年某个月有多少天
     * 
     * @param int yearnum 年
     * @param int monthnum 月
     * @return int 该年该月天数
     */
    public static int getMonthDayCount(int yearnum, int monthnum) {
    	if (yearnum<=0 || monthnum<1 || monthnum>12) {
    		logger_.error("计算某月天数时参数错误,yearnum="+yearnum+" monthnum="+monthnum);
			return -1;
		}
        int daycount;
        if ((yearnum % 4 == 0 && yearnum % 100 != 0) || yearnum % 400 == 0) {
            if (monthnum == 1 || monthnum == 3 || monthnum == 5
                    || monthnum == 7 || monthnum == 8 || monthnum == 10
                    || monthnum == 12) {
                daycount = 31;
            } else if (monthnum == 2) {
                daycount = 29;
            } else {
                daycount = 30;
            }
        } else {
            if (monthnum == 1 || monthnum == 3 || monthnum == 5
                    || monthnum == 7 || monthnum == 8 || monthnum == 10
                    || monthnum == 12) {
                daycount = 31;
            } else if (monthnum == 2) {
                daycount = 28;
            } else {
                daycount = 30;
            }
        }
        return daycount;
    }

    /**
     * 根据月份获取该月所属季度
     * @param monthnum 月份
     * @return 所属季度(1-4)
     */
    public static int getQuarterFromMonth(int monthnum) {
    	if(monthnum<1 || monthnum>12){
    		logger_.error("月份获取该月所属季度 月份参数错误,monthnum="+monthnum);
    		return -1;
    	}
        int season = 0;
        if (monthnum <= 3) {
            season = 1;
        } else if (monthnum <= 6) {
            season = 2;
        } else if (monthnum <= 9) {
            season = 3;
        } else {
            season = 4;
        } 
        return season;
    }

    /**
     * 获取季度第一个月的月数
     * @param seasonnum 哪一季
     * @return 该季第一个月
     */
	public static final int getQuarterStart(int seasonnum) {
		if (seasonnum < 1 || seasonnum > 4) {
			logger_.error("获取季度第一个月的月数,参数错误,seasonnum=" + seasonnum);
			return -1;
		}
		switch (seasonnum) {
		case 1:
			return 1;
		case 2:
			return 4;
		case 3:
			return 7;			
		default:
			return 10;
		}
	}

    /**
     * 获取季度最后一月的月数
     * @param seasonnum 哪一季
     * @return 该季最后一月
     */
	public static final int getQuarterEnd(int seasonnum) {
		if (seasonnum < 1 || seasonnum > 4) {
			logger_.error("获取季度最后一月的月数,参数错误,seasonnum=" + seasonnum);
			return -1;
		}
		switch (seasonnum) {
		case 1:
			return 3;
		case 2:
			return 6;
		case 3:
			return 9;
		default:
			return 12;	
		}
	}
    
   

    /**
     * 获取DateNode的单位
     * @param dn 需获取单位的DateNode
     * @return 该DateNode的单位
     * @throws UnexpectedException
     */
    public static final String getDateUnit(DateNode dn)
            throws UnexpectedException {
        if (dn.getDateinfo() == null) {
            throw new UnexpectedException(
                    "DateNode does not have DateRange:%s", dn.getText());
        }
        String dateUnitStr = null;
        String text = dn.getText();
        Unit unit = dn.getDateinfo().getDateUnit();
        if (unit != null) {
            // 若DateRange已有识别的单位，则用DateRange的
            dateUnitStr = EnumConvert.getStrFromUnit(unit);
            if (dateUnitStr == null) {
                throw new UnexpectedException(
                        "DateNode does not have right Unit:%s", unit);
            }
            return dateUnitStr;
        }
        //特别说明
        //测试时为能发现没有单位但是有下面这些单位的时间
        if (text.indexOf("号") != -1 || text.indexOf("日") != -1
                || text.indexOf("天") != -1 || text.indexOf("交易日") != -1) {
            dateUnitStr = "日";
        } else if (text.endsWith("周")) {
            dateUnitStr = "周";
        } else if (text.indexOf("周") != -1) {
            dateUnitStr = "日";
        } else if (text.indexOf("月") != -1) {
            dateUnitStr = "月";
        } else if (text.indexOf("季") != -1 || text.indexOf("中报") != -1) {
            dateUnitStr = "季";
        } else if (text.indexOf("年") != -1) {
            dateUnitStr = "年";
        }
        if (dateUnitStr != null) {
            return dateUnitStr;
        }
        DateRange dr = dn.getDateinfo();
        dateUnitStr = getUnitOfDateByDateRange(dr);
        return dateUnitStr;
    }


    /**
     * 检查已取得的DateNode的单位是否正确，若否，其进行调整
     * @param date 已取得的DateNode的单位
     * @param dr DateNode 的DateRange信息
     * @return 调整后的DateNode的单位
     */
    @SuppressWarnings("unused")
    public static String checkUnitOfDate(String date, DateRange dr) {
        if (date == null || dr==null) {
			logger_.error("checkUnitOfDate中参数date,dr都不能为空");
			return null;
		}
    	assert (date != null && dr != null);
        if (date.endsWith("日")) {
            return date;
        } else if (date.endsWith("周")) {
            date = isWeek(dr) ? date : "日";
        } else if (date.endsWith("月")) {
            date = isMonth(dr) ? date : "日";
        } else if (date.endsWith("季")) {
            date = isQuarter(dr) ? date : isMonth(dr) ? "月" : "日";
        } else if (date.endsWith("年")) {
            date = isYear(dr) ? date : isQuarter(dr) ? "季" : isMonth(dr) ? "月"
                    : "日";
        } else {
			return null;
        }
        return date;
    }

    /**
     * 检查DateRange是否符合“年”对时间 范围的要求
     * @param dr 被检查的DateRange
     * @return 符合则返回true，反之返回false
     */
    private static boolean isYear(DateRange dr) {
        int fromD = dr.getFrom().getDay();
        int fromM = dr.getFrom().getMonth();
        int toM = dr.getTo().getMonth();
        int toD = dr.getTo().getDay();
        if (fromM == 1 && fromD == 1 && toM == 12 && toD == 31) {
            return true;
        }
        return false;
    }

    /**
     * 检查DateRange是否符合“季”对时间 范围的要求
     * @param dr 被检查的DateRange
     * @return 符合则返回true，反之返回false
     */
    private static boolean isQuarter(DateRange dr) {
        int fromD = dr.getFrom().getDay();
        int fromM = dr.getFrom().getMonth();
        boolean fromIsQuarterStart = (fromM == 1 || fromM == 4 || fromM == 7 || fromM == 10)
                && fromD == 1;
        boolean toIsQuarterEnd =dr.getTo().isQuarterEnd();
        if (fromIsQuarterStart && toIsQuarterEnd) {
            return true;
        }
        return false;
    }

    /**
     * 检查DateRange是否符合“月”对时间 范围的要求
     * 
     * @param dr
     *            被检查的DateRange
     * @return 符合则返回true，反之返回false
     */
    private static boolean isMonth(DateRange dr) {
        int fromD = dr.getFrom().getDay();
        int toY = dr.getTo().getYear();
        int toM = dr.getTo().getMonth();
        int toD = dr.getTo().getDay();
        if (fromD == 1 && toD == getMonthDayCount(toY, toM)) {
            return true;
        }
        return false;
    }

    /**
     * 检查DateRange是否符合“周”对时间 范围的要求 即，起止时间分别为周一和周日
     * @param dr 被检查的DateRange
     * @return 符合则返回true，反之返回false
     */
    private static boolean isWeek(DateRange dr) {
        assert (dr != null);
        int fromW = dr.getFrom().getWeek();
        int toW = dr.getTo().getWeek();
        if (fromW == 7 && toW == 6) {
            return true;
        }
        return false;
    }

    /**
     * 根据DateNode 的DateRange信息 取得DateNode的单位
     * @param dr DateNode 的DateRange信息
     * @return 取得的DateNode的单位
     */
    public static String getUnitOfDateByDateRange(DateRange dr) {
    	if(dr == null){
    		logger_.error("根据DateNode 的DateRange信息 取得DateNode的单位函数参数dr为空");
    		return null;
    	}
        //assert (dr != null);
        String date = isYear(dr) ? "年" : isMonth(dr) ? "月" : "日";
        return date;
    }

    /**
     * 返回两个时间间相差多少天
     * @param from 从何时起
     * @param to 到何时止
     * @return 两个时间间相差天数
     */
    public static final int daysBetween(DateInfoNode from, DateInfoNode to) {

    	if (from==null || to == null) {
			return 0;
		}
        java.util.Calendar calst = java.util.Calendar.getInstance();
        java.util.Calendar caled = java.util.Calendar.getInstance();
        calst = changeDateInfoNodeToCalendar(from);
        caled = changeDateInfoNodeToCalendar(to);
        // 设置时间为0时
        calst.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calst.set(java.util.Calendar.MINUTE, 0);
        calst.set(java.util.Calendar.SECOND, 0);
        caled.set(java.util.Calendar.HOUR_OF_DAY, 0);
        caled.set(java.util.Calendar.MINUTE, 0);
        caled.set(java.util.Calendar.SECOND, 0);
        // 得到两个日期相差的天数
        int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst
                .getTime().getTime() / 1000)) / 3600 / 24;
        return days;
    }
    
    
	public static final int tradeDateBetween(DateInfoNode from, DateInfoNode to) {
		if (from == null || to == null)
			return -1;

		try {
			int count = 0;
			DateInfoNode next = from;
			while (isEarly(next, to) || next.equals(to)) {
				if (isTradeDate(next.toString(""))) {
					count++;
				}
				next = DateUtil.getNewDate(next, CHANGE_BY_DAY, 1);

			}
			return count;
		} catch (UnexpectedException e) {
			logger_.error("tradeDateBetween发生错误");
			return -1;
		}
	}

    /**
     * DateInfoNode转换为Calendar
     * @param node 需转换DateInfoNode
     * @return 转换出的Calendar
     */
    public static Calendar changeDateInfoNodeToCalendar(DateInfoNode node) {
    	if (node == null) {
			logger_.warn("DateInfoNode转换为Calendar 参数 node 为空");
    		return null;
		}
        int tyear = node.getYear();
        int tmonth = node.getMonth() - 1;
        int tday = node.getDay();
        Calendar date = Calendar.getInstance();
        date.set(tyear, tmonth, tday);
        date.set(java.util.Calendar.HOUR_OF_DAY, 0);
        date.set(java.util.Calendar.MINUTE, 0);
        date.set(java.util.Calendar.SECOND, 0);
        date.set(java.util.Calendar.HOUR_OF_DAY, 0);
        date.set(java.util.Calendar.MINUTE, 0);
        date.set(java.util.Calendar.SECOND, 0);
        return date;
    }

    /**
     * 判断两个时间哪个较早
     * @param form 被比较时间
     * @param to 比较时间
     * @return form是否早于to
     */
    public static boolean isEarly(DateInfoNode form, DateInfoNode to) {
        return changeDateInfoNodeToCalendar(form).before(changeDateInfoNodeToCalendar(to));
    }

    /**
     * 判断两个时间哪个较晚
     * @param from 被比较时间
     * @param to 比较时间
     * @return form是否晚于to
     */
    public static boolean isLatter(DateInfoNode from, DateInfoNode to) {
        return changeDateInfoNodeToCalendar(from).after(changeDateInfoNodeToCalendar(to));
    }

    /**
     * 返回两个时间间相差多少月
     * @param from 从何时起
     * @param to 到何时止
     * @return 两个时间间相差月数
     */
    public static int monthsBetween(DateInfoNode from, DateInfoNode to) {
    	if(from==null || to == null){
    		logger_.error("monthsBetween,返回两个时间间相差多少月 参数为空");
    		return -1;
    	}
        // count month
        if (!DateUtil.isEarly(from, to)) {
            DateInfoNode step;
            step = from;
            from = to;
            to = step;
        }
        int yearB = yearsBetween(from, to);
        int fromM = from.getMonth();
        int toM = to.getMonth();
        int monthB = (toM - fromM) + 12 * yearB;
        return monthB;
    }

    /**
     * 返回两个时间间相差多少年
     * @param from 从何时起
     * @param to 到何时止
     * @return 两个时间间相差年数
     */
    public static int yearsBetween(DateInfoNode from, DateInfoNode to) {
    	if(from==null || to == null){
    		logger_.error("yearsBetween,返回两个时间间相差多少年  参数为空");
    		return -1;
    	}
        int fromY = from.getYear();
        int toY = to.getYear();
        if (toY - fromY < 0) {
            return fromY - toY;
        } else {
            return toY - fromY;
        }
    }

    /**
     * 根据时间长度拆分时间段
     * @param dr 被拆分时间
     * @param num 时间单位个数
     * @param unit 时间单位
     * @return 若被拆解时间短于该时间长度，返回null；否则返回拆分结果
     * @throws UnexpectedException 
     */
    public static ArrayList<DateRange> getSplitByRange(DateRange dr, int num,
            Unit unit, boolean excludeNonTradeDate) throws UnexpectedException {
    	if(dr==null){
    		logger_.error("getSplitByRange根据时间长度拆分时间段中,参数dr为空");
    		return null;
    	}
        ArrayList<DateRange> dateL = new ArrayList<DateRange>();
        if (dr.getRangeType().equals(OperDef.QP_EQ)) {
            if (excludeNonTradeDate) {
                dr.setDateRange(dr.getFrom().getLastTradeDay(), dr.getTo()
                        .getLastTradeDay());
            }
            dateL.add(dr);
            return dateL;
        }
        String tagStr = null;
        if (unit == Unit.YEAR) {
            tagStr = CHANGE_BY_YEAR;
        } else if (unit == Unit.MONTH) {
            tagStr = CHANGE_BY_MONTH;
        } else if (unit == Unit.DAY) {
            tagStr = CHANGE_BY_DAY;
        } else {
            throw new UnexpectedException("Unexpected date unit :%s", unit);
        }
        DateInfoNode diT = dr.getFrom();
        DateInfoNode diF = null;
        DateInfoNode tagDin = diT;
        DateInfoNode drEnd = dr.getTo();
        for ( diF = dr.getFrom(); ; ) {
            tagDin = getNewDate(diF, tagStr, num);
            diT = getNewDate(tagDin, CHANGE_BY_DAY, -1);
            DateRange newDR = new DateRange();
            newDR.setDateRange(diF, diT);
            if(!isLatter(drEnd,diT)) {//drEnd<=diT
            	break;
            }
            diF = tagDin;
            dateL.add(newDR);
        }
        DateRange newDR = new DateRange();
		if (!isLatter(diF, drEnd)) {//diF<=dr.getTo()
			newDR.setDateRange(diF, dr.getTo());
			dateL.add(newDR);
		}

        if (dateL.size() == 0) {
            return null;
        } else if (unit != Unit.DAY || num != 1) {
            // 拆分单位不为天，或长度不为1
            return dateL;
        }
        if (excludeNonTradeDate) {
            getEffectiveDates(dateL);
        }
        return dateL;
    }

    /**
     * 根据时间自身的相对信息调整
     * 如“昨天收盘价大于前一天的”
     * @param dateVal 需调整的时间
     */
   public static void adjustRelativeDate(DateNode dateVal,String backtestTime) {
       if (dateVal.getRelatedDate() == null) {
           // 若无相关标杆时间信息，则不需调整
           return;
       }
       DateRange relatedDateRange = dateVal.getRelatedDate().getDateinfo();
       String till = relatedDateRange.getRangeType().equals(OperDef.QP_LT) ? relatedDateRange
               .getTo().toString("") : relatedDateRange.getFrom().toString("");
       String newDateStr =null;
       if (DatePatterns.RELATIVE_DATE.matcher(dateVal.getText()).matches()) {
           newDateStr = String.format("截止%s%s", till, dateVal.getText());
       }
       DateRange newDate = null;
       String tip  = null;
       try{
           newDate = DateCompute.getDateInfoFromStr(newDateStr, backtestTime);
       }catch(NotSupportedException e){
           tip = String.format(MsgDef.NOT_ADJUST_RELATIVE_DATE_FMT, dateVal.getText());
       }
       assert(newDate==null||tip==null);
       if(newDate!=null){
           dateVal.setDateinfo(newDate);
       }else{
       }
   }
    
    public static ArrayList<DateRange> getSplitByRange(DateRange dr, int num,
            Unit month) throws UnexpectedException {
        return getSplitByRange(dr, num, month, true);
    }

    private static void getEffectiveDates(ArrayList<DateRange> dateL) throws UnexpectedException {
        if(dateL==null) return;
    	int count = 0;
        DateInfoNode oldStart = dateL.get(0).getFrom();
        //统计起始日期不合法的数量
        for (int i = 0; i < dateL.size(); i++) {
            DateRange drI = dateL.get(i);
            String dateStr = drI.getFrom().toString().replace("-", "");
            boolean isEffect = isTradeDate(dateStr);
            boolean isTodayAndNoData = dateStr.equals(getNow().toString(""))
                    && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 16;
            if (isEffect && !isTodayAndNoData) {
                continue;
            }
            dateL.remove(i--);
            count++;
        }
        if (count == 0) {
            return;
        }

        //从起始日期之前补充上不合法的天数
        for (int i = 1; i <= count; i++) {
            int change = -i;
            DateRange newDR = new DateRange();
            DateInfoNode newDIF = getNewDate(oldStart, CHANGE_BY_DAY, change);
            String newDateStr = newDIF.toString().replace("-", "");
            boolean isEffect = isTradeDate(newDateStr);
            if (!isEffect) {
                count++;
                continue;
            }
            newDR.setDateRange(newDIF, newDIF);
            dateL.add(0, newDR);
        }
    }

   
    /**
     * 根据nChangeValue直接得到日期
     * 
     * @param node
     * @param changeUnit
     * @param nChangeValue
     * @return
     * @throws UnexpectedException
     */
    public static DateInfoNode getNewTradeDateByDay(DateInfoNode node, String changeUnit, int nChangeValue) throws UnexpectedException {
    	if(node == null)
    		return null;
    	
    	//因为交易日期的存储是从最近的日期到之前的日期,所以要正负倒一下
    	nChangeValue = 0-nChangeValue;
    	
    	//如果是日期级别的
    	if (changeUnit.equals(DateUtil.CHANGE_BY_DAY))  {
    		int dateValue = node.getYear()*10000 + node.getMonth()*100 + node.getDay();
        	//System.out.println("\tdateValue="+ dateValue);
    		int index = getDayIndexOfArray(dateValue);
    		//System.out.println("\tindex="+ index);
        	int newIndex = -1;	
        	if(index>=0 &&  index <tradeDateInts.length ) {
        		//处理周六周日的情况
        		int currDate = tradeDateInts[index];
        		if(dateValue!=currDate) {
        			//newIndex = index + nChangeValue -1; 
        			if(nChangeValue>0) {
        				newIndex = index + nChangeValue -1; 
        			}else{
        				newIndex = index + nChangeValue; 
        			}//处理周六周日的情况 结束
        		}else
        			newIndex = index + nChangeValue;
        	}else{
        		if(index <0) {
        			newIndex = 0;
    			}else {
    				newIndex = allTradeDatesArr.length-1;
    			}
        	}
        	//System.out.println("\tnewIndex="+ newIndex);
        	int newDate = -1;
    		if(newIndex>=0 &&  newIndex <tradeDateInts.length ) {
    			newDate = tradeDateInts[newIndex];
    		}else{
    			if(newIndex <0) {
    				newDate = tradeDateInts[0];
    			}else {
    				newDate = tradeDateInts[allTradeDatesArr.length-1];
    			}
    		}
    		//System.out.println("\tnewDate="+ newDate);
    		if(newDate>0) {
    			DateInfoNode newNode = new DateInfoNode();
    	        int year = newDate/10000;
    	        int month = newDate%10000/100;
    	        int day = newDate%100;
    	        newNode.setDateInfo(year, month, day);
    	        return newNode;
    		}else{
    			return null;
    		}
        	
    	}else{
    		throw new UnexpectedException("还不支持Date Unit:%s",changeUnit);
    	}
    }
    
    
    /**
     * 根据时间基点和需变化的单位和单位长度，获取新时间
     * @param node 时间基点
     * @param changeUnit 时间变化单位 包括{@link DateUtil.CHANGE_BY_YEAR}
     *            {@link DateUtil.CHANGE_BY_MONTH}
     *            {@link DateUtil.CHANGE_BY_DAY}
     * @param changeValue 时间变化单位个数，正则向前，负则向后
     * @return 新的DateInfoNode
     * @throws UnexpectedException 
     */
    public static DateInfoNode getNewDate(DateInfoNode node, String changeUnit, int changeValue) throws UnexpectedException {
    	if(node == null)
    		return null;
        Calendar date = changeDateInfoNodeToCalendar(node);
        if (changeUnit.equals(DateUtil.CHANGE_BY_YEAR)) {
            date.add(Calendar.YEAR, changeValue);
        } else if (changeUnit.equals(DateUtil.CHANGE_BY_MONTH)) {
            date.add(Calendar.MONTH, changeValue);
        } else if (changeUnit.equals(DateUtil.CHANGE_BY_DAY)) {
            date.add(Calendar.DATE, changeValue);
        } else {
            throw new UnexpectedException("Unexpected Date Unit:%s",changeUnit);
        }
        DateInfoNode newNode = new DateInfoNode();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DATE);
        newNode.setDateInfo(year, month, day);
        return newNode;
    }
    
    
    /**
	 * 根据时间基点和需变化的单位和单位长度，获取新时间
	 * 
	 * @param node
	 *            时间基点
	 * @param changeUnit
	 *            时间变化单位 包括 YEAR MONTH DAY QUARTER WEEK
	 * @param n2Change
	 *            时间变化单位个数，正则向前，负则向后
	 * @return 新的DateInfoNode
	 * @throws UnexpectedException
	 */
	public static DateInfoNode getNewDate(DateInfoNode node, Unit unit,
			int changeValue) throws UnexpectedException {
		if (node == null)
			return null;

		Calendar date = changeDateInfoNodeToCalendar(node);
		switch (unit) {
		case YEAR:
			date.add(Calendar.YEAR, changeValue);
			break;
		case MONTH:
			date.add(Calendar.MONTH, changeValue);
			break;
		case DAY:
			date.add(Calendar.DATE, changeValue);
			break;
		case QUARTER:
			date.add(Calendar.MONTH, changeValue * 3);
			break;
		case WEEK:
			date.add(Calendar.DATE, changeValue * 7);
			break;
		default:
			logger_.error("根据时间基点和需变化的单位和单位长度  参数 unit=\"" + unit.name()
					+ "\"尚未支持");
			return null;
		}
		DateInfoNode newNode = new DateInfoNode();
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH) + 1;
		int day = date.get(Calendar.DATE);
		newNode.setDateInfo(year, month, day);
		return newNode;
	}
    
    /**
     * 根据时间基点和需变化的报告期和报告期数量，获取新时间
     * @param date 时间基点 
     * @param report 报告期类型{@link ReportType}
     * @param changeValue 报告期类型已经确定调整方向，报告期数量不能为负
     * @return 新的DateInfoNode
     * @throws UnexpectedException 
     */
    public static DateInfoNode getNewDateByReportType(DateInfoNode date,
            ReportType report, int changeValue) throws UnexpectedException {
    	if(date == null)
    	{
    		logger_.error("getNewDateByReportType 根据时间基点和需变化的报告期和报告期数量，" +
    				"获取新时间参数date不能为空");
    		return null;
    	}
        if (changeValue < 0) {
            // 为报告期数量，不可为负
            throw new UnexpectedException("Unexpected Change Number:%d",
                    changeValue);
        }
        switch (report) {
        case DAILY:
            return getNewDateByRTDaily(date, changeValue, false);
        case QUARTER:
            return getNewDateByRTQuarter(date, changeValue, false);
        case HALF_YEAR:
            return getNewDateByRTHalfYear(date, changeValue,false);
        case YEAR:
            return getNewDateByRTYear(date, changeValue,false);
        case FUTURE_DAILY:
            return getNewDateByRTDaily(date, changeValue, true);
        case FUTURE_QUARTER:
            return getNewDateByRTQuarter(date, changeValue, true);
        default:
            throw new UnexpectedException("Unexpected Report Type:%s", report);
        }
    }

    private static DateInfoNode getNewDateByRTYear(DateInfoNode date,
            int changeNum, boolean toFuture) {
        date = date.isYearEnd() ? date : toFuture ? date.getYearEnd()
                : getNextOrLastYearEnd(date, toFuture);
        if (changeNum == 0) {
            return date;
        }
        int count = 0;
        while (count < changeNum) {
            date = getNextOrLastYearEnd(date, toFuture);
            count++;
        }
        return date;
    }

    private static DateInfoNode getNextOrLastYearEnd(DateInfoNode date,
            boolean toFuture) {
        DateInfoNode next = date.getNextYearEnd();
        DateInfoNode last = date.getLastYearEnd();
        DateInfoNode newDate = toFuture ? next : last;
        return newDate;
    }

    private static DateInfoNode getNewDateByRTHalfYear(DateInfoNode date,
            int changeNum, boolean toFuture) {
        date = date.isHalfYearEnd() ? date : toFuture ? date.getHalfYearEnd()
                : getNextOrLastHalfYearEnd(date, toFuture);
        if (changeNum == 0) {
            return date;
        }
        int count = 0;
        while (count < changeNum) {
            date = getNextOrLastHalfYearEnd(date, toFuture);
            count++;
        }
        return date;
    }

    private static DateInfoNode getNextOrLastHalfYearEnd(DateInfoNode date,
            boolean toFuture) {
        DateInfoNode next = date.getNextHalfYearEnd();
        DateInfoNode last = date.getLastHalfYearEnd();
        DateInfoNode newDate = toFuture ? next : last;
        return newDate;
    }

    

    private static DateInfoNode getNewDateByRTQuarter(DateInfoNode date,
            int changeNum, boolean toFuture) {
        date = date.isQuarterEnd() ? date : toFuture ? date.getQuarterEnd()
                : getNextOrLastQuarterEnd(date, toFuture);
        if (changeNum == 0) {
            return date;
        }
        int count = 0;
        while (count < changeNum) {
            date = getNextOrLastQuarterEnd(date, toFuture);
            count++;
        }
        return date;
    }
    
    
    
   
    
    private static DateInfoNode getNextOrLastQuarterEnd(DateInfoNode date,
            boolean toFuture) {
        DateInfoNode next = date.getNextQuarterEnd();
        DateInfoNode last = date.getLastQuarterEnd();
        DateInfoNode newDate = toFuture ? next : last;
        return newDate;
    }

    private static DateInfoNode getNewDateByRTDaily(DateInfoNode date,
            int changeNum, boolean toFuture) throws UnexpectedException {
        boolean dateIsTrade = isTradeDate(date.toString(""));
        date = dateIsTrade?date:toFuture?rollTradeDate(date, 1):rollTradeDate(date, -1);
        if (changeNum == 0 ) {
            return date;
        }
        int change = toFuture ? changeNum : -changeNum;
        DateInfoNode newDate = rollTradeDate(date, change);
        return newDate;
    }
    
    //dateStr should in format yyyy-MM-dd
    public static DateInfoNode getDateInfoNode(String dateStr) {
    	try{
    	int year  = Integer.parseInt(dateStr.substring(0,4));
    	int mongth  = Integer.parseInt(dateStr.substring(4,6));
    	int day  = Integer.parseInt(dateStr.substring(6));
    	return new DateInfoNode(year, mongth, day);
    	}catch(Exception e) {
    		return getNow();
    	}
    }

    public static DateInfoNode getNow() {
        DateInfoNode now = new DateInfoNode();
        Calendar cnow = Calendar.getInstance();
        now.setDateInfo(cnow.get(Calendar.YEAR), cnow.get(Calendar.MONTH) + 1,
                cnow.get(Calendar.DATE));
        return now;
    }
    
    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    public static boolean isTradeDate(Calendar strDate) {    	
    	String s= format.format(strDate.getTime());
    	return isTradeDate(s);
    }

    public static boolean isTradeDate(String strDate) {    	
    	/*if(allTradeDates_ ==null)
    		logger_.error("allTradeDates_为空");
        return allTradeDates_.contains(strDate);*/
    	if(tradeDateIndexMap==null) {
    		logger_.error("allTradeDates_为空");
    	}
    	return tradeDateIndexMap.containsKey(strDate);
    }
    
    public static void loadTradeDate() throws DataConfException{
    	MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
    	List<String> tradeDates  =mybatisHelp.getTradeDateAllMapper().selectAll();
    	
    	//排序一次,保证从小到大排序
    	Collections.sort(tradeDates, new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}});
    	
    	DateUtil.loadTradeDate(tradeDates);
    }

    public static void loadTradeDate(List<String> lines) throws DataConfException {
    	if (lines == null) 
    		return;
    	
        if (lines.size() < 5398) {
            throw new DataConfException("trade dates", -1, "There should be at least 5398 trade dates"+ " by the end of 2012!");
        }
        
        
        allTradeDatesArr = new String[lines.size()];
        tradeDateInts = new int[lines.size()];
        tradeDateIndexMap = new HashMap<String, Integer>();
        
        for (int iLine = 0; iLine < lines.size(); iLine++) {
            String strDate = lines.get(iLine).trim();
            int intDate = Integer.parseInt(strDate);
            if (strDate.length() != 8 || intDate < 19901219 || intDate > 20200101) {
                throw new DataConfException("trade dates", -1, "Invalid trade date: [%s] ", strDate);
            }
            //allTradeDates_.add(strDate);
            allTradeDatesArr[iLine] = strDate;
            tradeDateInts[iLine] = intDate;
            tradeDateIndexMap.put(strDate, iLine);
        }
    }
    
    //找到日期在数组中的index
    //TODO 优化,用二分查找
    private static int getDayIndexOfArray(int intDate) {
    	//int intDate = Integer.parseInt(strDate);
    	int i=0;
    	for(;i<tradeDateInts.length;i++) {
    		if(tradeDateInts[i]>intDate) {
    			continue;
    		}else{
    			return i;
    		}
    	}
    	return i;
    }

    public static DateNode getSameLengthButEarlierDateNode(DateNode dateVal,
            GrowthType growthType, ReportType reportType)
            throws UnexpectedException {
    	if(dateVal==null){
    		logger_.error("getSameLengthButEarlierDateNode中参数dateVal为空");
    		return null;
    	}
        String unit = dateVal.getUnitStrOfDate();
        DateRange dr = dateVal.getDateinfo();
        
        if(unit==null || dr==null){
        	logger_.error("getSameLengthButEarlierDateNode中参数" +
        			"dateVal的UnitStrOfDate或Dateinfo为空");
        	return null;
        }
        //assert (unit != null && dr != null);
        String type = dateVal.getRangeType();
        if (!type.equals(OperDef.QP_EQ) && !type.equals(OperDef.QP_IN)) {
            return null;
        }
        unit = (reportType == null || reportType == ReportType.DAILY 
                || reportType == ReportType.FUTURE_DAILY) ? unit
                : (reportType == ReportType.QUARTER 
                        || reportType == ReportType.FUTURE_QUARTER) ? "报告期"
                        : reportType == ReportType.HALF_YEAR ? "半年" : "年";
        Pair<Pair<String, Integer>, DateRange> allInfos = 
            getSameLengthButEarlierDateRange(dr, unit, growthType);
        DateRange newDr = allInfos.second;
        unit = allInfos.first.first;
        int change = allInfos.first.second;

        String laseText = String.format("%s的前%d%s", dateVal.getText(), change, unit);
        DateNode lastDateVal = new DateNode(laseText);
        lastDateVal.setDateinfo(newDr);
        return lastDateVal;
    }

    private static Pair<Pair<String, Integer>, DateRange> getSameLengthButEarlierDateRange(
            DateRange dr, String unit, GrowthType growthType)
            throws UnexpectedException {
        String type = dr.getRangeType();
        if(type==null || 
          (!type.equals(OperDef.QP_EQ) && !type.equals(OperDef.QP_IN))){
        	logger_.error("getSameLengthButEarlierDateRange()中参数dr中," +
        			"RangeType不是OperDef.QP_EQ, 也不是OperDef.QP_IN");
        	return null;
        }
        //assert (type.equals(OperDef.QP_EQ) || type.equals(OperDef.QP_IN));
        int change = -1;
        DateRange newDr = null;
        if (growthType == GrowthType.YEAR_ON_YEAR) {
            DateInfoNode from = dr.getFrom();
            DateInfoNode to = dr.getTo();
            DateInfoNode newFrom = DateUtil
                    .getNewDate(from, CHANGE_BY_YEAR, -1);
            //若当前时间单位为年，则前推时间的截止点为上年年末；否则由当前时间前推一年
            DateInfoNode newTo = dr.getDateUnit() == Unit.YEAR ? to.isYearEnd()?DateUtil
                    .getNewDateByReportType(to, ReportType.YEAR, 1):DateUtil
                    .getNewDateByReportType(to, ReportType.YEAR, 0) : DateUtil
                    .getNewDate(to, CHANGE_BY_YEAR, -1);
            newDr = new DateRange(newFrom, newTo);
            change = 1;
            unit = "年";
        } else {
            DateInfoNode toNew = getNewDate(dr.getFrom(), CHANGE_BY_DAY, -1);
            DateInfoNode fromNew = new DateInfoNode();
            if (unit.equals("日") || unit.equals("周")) {
                if (type.equals(OperDef.QP_EQ)) {
                    fromNew = rollTradeDate(dr.getFrom(), -1);
                    assert (fromNew != null);
                    change = 1;
                    unit = "交易日";
                } else {
                    toNew = rollTradeDate(dr.getFrom(), -1);
                    change = DateUtil.daysBetween(dr.getFrom(), dr.getTo());
                    fromNew = getNewDate(toNew, CHANGE_BY_DAY, -change);
                    change = unit.equals("周") ? change / 7 + 1 : change;
                }
            } else if (unit.equals("季") || unit.equals("报告期") || dr.isReport()) {
                toNew = dr.getFrom().getLastQuarterEnd();
                fromNew = dr.isReport() ? toNew : toNew.getQuarterStart();
                unit =  dr.isReport()?"报告期":unit;
                change = 1;
            }else if (unit.equals("月")) {
                // 若为报告期，则也为季度
                int mb = DateUtil.monthsBetween(dr.getFrom(), dr.getTo()) + 1;
                fromNew = mb == 0 ? getNewDate(dr.getFrom(), CHANGE_BY_MONTH,
                        -1) : getNewDate(dr.getFrom(), CHANGE_BY_MONTH, -mb);
                change = mb == 0 ? 1 : mb;
            } else if (unit.equals("半年")) {
                fromNew = getNewDate(
                        getNewDate(dr.getFrom(), CHANGE_BY_MONTH, -6),
                        CHANGE_BY_DAY, 1);
            } else if (unit.equals("年")) {
                int yb = DateUtil.yearsBetween(dr.getFrom(), dr.getTo()) + 1;
                fromNew = yb == 0 ? getNewDate(
                        getNewDate(toNew, CHANGE_BY_YEAR, -1), CHANGE_BY_DAY, 1)
                        : getNewDate(getNewDate(toNew, CHANGE_BY_YEAR, -yb),
                                CHANGE_BY_DAY, 1);
                change = yb == 0 ? 1 : yb;
            } else {
                throw new UnexpectedException("Unexpected date unit :%s",unit);
            }
            newDr = new DateRange(fromNew, toNew);
        }

        return new Pair<Pair<String, Integer>, DateRange>(
                new Pair<String, Integer>(unit, change), newDr);
    }

    public static String replaceYearWord(String text) {
        int nowYear = DateUtil.getNow().getYear();
        if (text.contains("今年")) {
            String year = String.valueOf(nowYear) + "年";
            text = text.replace("今年", year);
        }
        if (text.contains("明年")) {
            String year = String.valueOf(nowYear + 1) + "年";
            text = text.replace("明年", year);
        }
        if (text.contains("后年")) {
            String year = String.valueOf(nowYear + 2) + "年";
            text = text.replace("后年", year);
        }
        if (text.contains("去年")) {
            String year = String.valueOf(nowYear - 1) + "年";
            text = text.replace("去年", year);
        }
        /*if (text.contains("上年")) {
            String year = String.valueOf(nowYear - 1) + "年";
            text = text.replace("上年", year);
        }*/
        if (text.contains("大前年")) {
            String year = String.valueOf(nowYear - 3) + "年";
            text = text.replace("大前年", year);
        }
        if (text.contains("前年")) {
            String year = String.valueOf(nowYear - 2) + "年";
            text = text.replace("前年", year);
        }
        return text;
    }

    public static DateInfoNode getLatestTradeDate() throws UnexpectedException {
        return getLatestTradeDate(true);
    }
    
    /**
     * 获取最近的一个交易日，返回DateInfoNode形式日期
     * 
     * @param needDecideByHourOfDay 需不需要根据现在几点进行判断
     * @return 最近的一个交易日
     * @throws UnexpectedException 
     */
    public static DateInfoNode getLatestTradeDate(boolean needDecideByHourOfDay)
            throws UnexpectedException {
        DateInfoNode now = getNow();
        boolean nowIsTD = isTradeDate(now.toString(""));
        
        //今天小时大于9:00就算进去 for wudan 手机部门需求
        boolean isOver16 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 9;
        boolean nowIsLatest = nowIsTD
                && (needDecideByHourOfDay && isOver16 || !needDecideByHourOfDay);
        if (nowIsLatest) {
            return now;
        } else {
            return rollTradeDate(now, -1);
        }
    }

    /**
     * 根据给定日期，向前或向后找到第n个交易日
     * @param din 给定日期
     * @param nChange 要找到第几个交易日 ,为正向未来寻找，为负向过去寻找
     * @return 找到的交易日
     * @throws UnexpectedException 
     */
    public static DateInfoNode rollTradeDate(DateInfoNode din, int nChange) throws UnexpectedException {
    	if(din==null){
    		logger_.error("rollTradeDate根据给定日期，向前或向后找到第n个交易日参数din为空");
    		return null;
    	}
        if (nChange == 0) {
            if (isTradeDate(din.toString(""))) {
                return din;
            } else {
                return rollTradeDate(din, -1);
            }
        }else{
        	return getNewTradeDateByDay(din, DateUtil.CHANGE_BY_DAY, nChange);
        }
    }

    /**
     * 按单位将DateRange分为整个的时间长度 如“2010年5月至2012年8月”若按“年”拆分的话，需拆分为以下时间段：
     * “2010年1月1日至2010年12月31日”、“2011年1月1日至2011年12月31日”
     * @param dr 被拆分的时间范围
     * @param unit 时间单位
     * @return 拆分出的各个时间段
     * @throws UnexpectedException
     */
    public static ArrayList<DateRange> splitDateRangeByUnit(DateRange dr,
            Unit unit) throws UnexpectedException {
        if (dr == null || dr.getRangeType().equals(OperDef.QP_LT)
                || dr.getRangeType().equals(OperDef.QP_GT)) {
            throw new UnexpectedException("“%s” can not split by unit %s",
                    dr==null?null:dr.toString(), unit);
        }

        if (unit == Unit.DAY) {
            // 单位为“天”，直接拆分
            return getSplitByRange(dr, 1, Unit.DAY, false);
        } else if (unit == Unit.WEEK) {
            return splitDateRangeByWholeWeek(dr);
        } else if (unit == Unit.MONTH) {
            return splitDateRangeByWholeMonth(dr);
        } else if (unit == Unit.QUARTER) {
            return splitDateRangeByWholeQuarter(dr);
        } else if (unit == Unit.YEAR) {
            return splitDateRangeByWholeYear(dr);
        } else if (unit == Unit.HALF_YEAR) {
            return splitDateRangeByHalfYear(dr);
        } else {
            throw new UnexpectedException("未知时间单位");
        }
    }

    /**
     * 将时间周期按整季度拆分
     * @param dr 被拆分时间
     * @param toIsQuartorEnd 是否指定每个拆分出的时间段的截止时间均为当季的最后一天
     * @return 拆分出的时间段。若无法按整季度拆分，则返回null
     * @throws UnexpectedException 
     */
    private static ArrayList<DateRange> splitDateRangeByWholeQuarter(
            DateRange dr) throws UnexpectedException {

        DateInfoNode start = dr.getFrom().getQuarterStart();
        DateInfoNode end = dr.getTo().isQuarterEnd() ? dr.getTo() : dr.getTo()
                .getLastQuarterEnd();
        if (end.isEarlier(start)) {
            return null;
        }
        DateRange splitDR = new DateRange(start, end);
        ArrayList<DateRange> dates = getSplitByRange(splitDR, 3, Unit.MONTH);
        if (dates == null) {
            throw new UnexpectedException(
                    "Range not long enough for quarter:%s",
                    dr.toString());
        }
        return dates;
    }

    /**
     * 将时间周期按整月拆分,还可指定每个拆分出的时间段的截止时间均为当月的最后一天
     * @param dr 被拆分时间
     * @param toIsEndOfMonth 是否指定每个拆分出的时间段的截止时间均为当月的最后一天
     * @return 拆分出的时间段。若无法按整月拆分，则返回null
     * @throws UnexpectedException 
     */
    private static ArrayList<DateRange> splitDateRangeByWholeMonth(DateRange dr)
            throws UnexpectedException {

        DateInfoNode start = dr.getFrom().getMonthStart();
        DateInfoNode end = dr.getTo().isMonthEnd() ? dr.getTo() : dr.getTo()
                .getLastMonthEnd();
        if (end.isEarlier(start)) {
            return null;
        }
        DateRange splitDR = new DateRange(start, end);
        ArrayList<DateRange> dates = getSplitByRange(splitDR, 1, Unit.MONTH);
        if (dates == null) {
            throw new UnexpectedException("Range not long enough for month:%s",
                    dr.toString());
        }
        return dates;
    }

    /**
     * 将时间周期按整周拆分，每周的开始为周日，截止为周六
     * @param dr 被拆分时间
     * @param toIsWeekEnd 是否指定每个拆分出的时间段的截止时间均为当周的最后一天(周六)
     * @return 拆分出的时间段。若无法按整周拆分，则返回null
     * @throws UnexpectedException 
     */
    private static ArrayList<DateRange> splitDateRangeByWholeWeek(DateRange dr) throws UnexpectedException {
        DateInfoNode start = dr.getFrom().getWeekStart();
        DateInfoNode end = dr.getTo().isWeekEnd() ? dr.getTo() : dr.getTo()
                .getLastWeekEnd();
        if (end.isEarlier(start)) {
            return null;
        }
        DateRange splitDR = new DateRange(start, end);
        ArrayList<DateRange> dates = getSplitByRange(splitDR, 7, Unit.DAY);
        if (dates == null) {
            throw new UnexpectedException(
                    "Range not long enough for week:%s",
                    dr.toString());
        }
        return dates;
    }
    /**
     * 将时间按半年拆分
     * @param dr 被拆分时间
     * @return 拆分出的时间段。若无法按整年拆分，则返回null
     * @throws UnexpectedException 
     */
    private static ArrayList<DateRange> splitDateRangeByHalfYear(DateRange dr)
            throws UnexpectedException {
        DateInfoNode start = dr.getFrom().getHalfYearStart();
        DateInfoNode end = dr.getTo().isHalfYearEnd() ? dr.getTo() : dr.getTo()
                .getLastHalfYearEnd();
        if (end.isEarlier(start)) {
                return null;
        }
        DateRange splitDR = new DateRange(start, end);
        ArrayList<DateRange> dates = getSplitByRange(splitDR, 6, Unit.MONTH);
        if (dates == null) {
            throw new UnexpectedException(
                    "Range not long enough for half year:%s",
                    splitDR.toString());
        }
        return dates;
    }
    /**
     * 将时间周期按整年拆分
     * @param dr 被拆分时间
     * @param toIsYearEnd 是否指定每个拆分出的时间段的截止时间均为当年的最后一天
     * @return 拆分出的时间段。若无法按整年拆分，则返回null
     * @throws UnexpectedException 
     */
    private static ArrayList<DateRange> splitDateRangeByWholeYear(DateRange dr)
            throws UnexpectedException {
        DateInfoNode start = dr.getFrom().getYearStart();
        DateInfoNode end = dr.getTo().isYearEnd() ? dr.getTo() : dr.getTo()
                .getLastYearEnd();
        if (end.isEarlier(start)) {
            return null;
        }
        DateRange splitDR = new DateRange(start, end);
        ArrayList<DateRange> dates = getSplitByRange(splitDR, 1, Unit.YEAR);
        if (dates == null) {
            throw new UnexpectedException("Range not long enough for year:%s",
                    dr.toString());
        }
        return dates;
    }

    /**
     * 取得一段时间内所有的交易日
     * @param dateinfo 一段确定的时间，这段时间必须为有限的
     * @return 这段时间内所有的交易日。若未找到，则返回null。
     * @throws UnexpectedException
     */
    public static ArrayList<DateRange> getAllTradeDayInDateRange(
            DateRange dateinfo) throws UnexpectedException {
    	if(dateinfo == null){
    		logger_.error("getAllTradeDayInDateRange取得一段时间内所有的交易日参数dateinfo为空");
    		return null;
    	}
        String rangeType = dateinfo.getRangeType();
        if(!rangeType.equals(OperDef.QP_EQ) && !rangeType
                .equals(OperDef.QP_IN)){
        	logger_.error("getRangeType不是QP_EQ,也不是QP_IN");
        	return null;
        }
        //assert (rangeType.equals(OperDef.QP_EQ) || rangeType.equals(OperDef.QP_IN));
        DateInfoNode next = dateinfo.getFrom();
        DateInfoNode to = dateinfo.getTo();
        ArrayList<DateRange> tradeDays = new ArrayList<DateRange>();
        while (isEarly(next, to) || next.equals(to)) {
            if (isTradeDate(next.toString(""))) {
                tradeDays.add(new DateRange(next, next));
            }
            next = getNewDate(next, CHANGE_BY_DAY, 1);
        }
        if (tradeDays.size() == 0) {
            return null;
        }
        return tradeDays;
    }

    /**
     * 比较两个时间单位的大小，如“年”大于“月”
     * @param dateUnit 
     * @param dateUnit2
     * @return 若dateUnit 比dateUnit2大，则返回true
     */
	public static boolean isBigerUnit(Unit dateUnit, Unit dateUnit2) {
		if (!isDateUnit(dateUnit) || !isDateUnit(dateUnit2)) {
			logger_.warn("比较两个时间单位的大小,有参数不是日期单位dateUnit=" + dateUnit
					+ ", dateUnit2=" + dateUnit2);
			return false;
		}
		//assert (isDateUnit(dateUnit) && isDateUnit(dateUnit2));
		boolean isBiger = dateUnit == Unit.YEAR && dateUnit2 != Unit.YEAR
				|| dateUnit == Unit.QUARTER && dateUnit2 != Unit.YEAR
				&& dateUnit2 != Unit.QUARTER || dateUnit == Unit.MONTH
				&& (dateUnit2 == Unit.WEEK || dateUnit2 == Unit.DAY)
				|| dateUnit == Unit.WEEK && dateUnit2 == Unit.DAY;
		return isBiger;
	}

    /**
     * 判断单位是不是时间单位
     * @param dateUnit
     * @return 若dateUnit是时间单位，则返回true
     */
    public static boolean isDateUnit(Unit dateUnit) {
        return dateUnit == Unit.YEAR || dateUnit == Unit.QUARTER
                || dateUnit == Unit.MONTH || dateUnit == Unit.WEEK
                || dateUnit == Unit.DAY||dateUnit == Unit.HALF_YEAR
                || dateUnit == Unit.MUNITE; 
    }

    public static ArrayList<DateRange> splitDateRangeByReportType(
            DateRange dateinfo, ReportType reportType)
            throws UnexpectedException {
    	if (reportType == null) {
			logger_.error("splitDateRangeByReportType函数中,参数dateinfo为空");
			return null;
		}
        //assert (reportType != null);
        if (dateinfo.getRangeType().equals(OperDef.QP_GT)
                || dateinfo.getRangeType().equals(OperDef.QP_LT)) {
            return null;
        }
        if (reportType == ReportType.YEAR) {
            return splitDateRangeByUnit(dateinfo, Unit.YEAR);
        } else if (reportType == ReportType.HALF_YEAR) {
            return splitDateRangeByHalfYear(dateinfo);
        } else if (reportType == ReportType.QUARTER
                || reportType == ReportType.FUTURE_QUARTER) {
            return splitDateRangeByUnit(dateinfo, Unit.QUARTER);
        } else if (reportType == ReportType.DAILY
                || reportType == ReportType.FUTURE_DAILY) {
            return getSplitByRange(dateinfo, 1, Unit.DAY, true);
        } else {
            throw new UnexpectedException("Unexpected ReportType:%s",
                    reportType);
        }
    }

    public static DateNode makeDateNodeFromStr(String valStr,String backtestTime)
            throws NotSupportedException, UnexpectedException {
        DateNode valNode = new DateNode(valStr);
        DateRange dateRange = DateCompute.getDateInfoFromStr(valStr, backtestTime);
        valNode.setDateinfo(dateRange);
        return valNode;
    }

    public static DateRange getLastTradeDateInFirstAndLastWeek(
            DateRange dateRange) throws UnexpectedException {
        if(dateRange==null){
        	logger_.error("getLastTradeDateInFirstAndLastWeek函数中参数dateRange为空");
            throw new UnexpectedException("DateRange is Null");
        }
        DateInfoNode from = dateRange.getFrom();
        DateInfoNode to = dateRange.getTo();
        DateInfoNode now = getNow();
        DateInfoNode firstWeekEnd = from.getWeekEnd();
        DateInfoNode lastWeekEnd = to.getWeekEnd();
        DateInfoNode nowWeekEnd = now.getWeekEnd();
        DateInfoNode newTo = nowWeekEnd.equals(lastWeekEnd) ? getLatestTradeDate()
                : rollTradeDate(lastWeekEnd, -1);
        DateInfoNode newFrom = nowWeekEnd.equals(firstWeekEnd) ? getLatestTradeDate()
                : rollTradeDate(firstWeekEnd, -1);
        return new DateRange(newFrom, newTo);
    }
    
    /**
     * 获得当前节点的左边或右边的节点，忽略空白节点
     * @param pos
     * @param nodes
     * @param toLeft
     * @return
     */
    public static SemanticNode getLastOrNextNodeSkipBlankAndHiddenSign(int pos,
            ArrayList<SemanticNode> nodes, boolean toLeft) {
        int i = pos;
        while(toLeft?--i>-1:++i<nodes.size()){
            if(DatePatterns.BLANK.matcher(nodes.get(i).getText()).matches()
                    ||DatePatterns.ADD_MARKER.matcher(nodes.get(i).getText()).matches()){
                continue;
            }
            return nodes.get(i);
        }
        return null;
    }

    public static final String CHANGE_BY_DAY = "daytag";
    public static final String CHANGE_BY_MONTH = "monthtag";
    public static final String CHANGE_BY_YEAR = "yeartag";

    //private static HashSet<String> allTradeDates_;

    public static void main(String[] args) throws DataConfException,
            UnexpectedException {
    	String folder = "D:/MySoftwareBook/eclipse/workspace/QueryParser_OLD_trunk";
        DateUtil.loadTradeDate(Util.readTxtFile(folder + "/data/tradedates.txt"));
        System.out.println(rollTradeDate(getNow(),10));
        
        DateInfoNode node = new DateInfoNode(2015,5,9);
        System.out.println("0=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, 0) );
        System.out.println("1=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, 1) );
        System.out.println("2=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, 2) );
        System.out.println("3=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, 3) );
        System.out.println("5=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, 5) );
        System.out.println("10=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, 10) );
        System.out.println("100=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, 100) );
        System.out.println("1000=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, 1000) );
        System.out.println("-1=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, -1) );
        System.out.println("-2=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, -2) );
        System.out.println("-3=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, -3) );
        System.out.println("-5=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, -5) );
        System.out.println("-10=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, -10) );
        System.out.println("-100=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, -100) );
        System.out.println("-1000=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, -1000) );
        
        node = new DateInfoNode(2012,10,13);
        System.out.println("0=" + getNewTradeDateByDay(node, DateUtil.CHANGE_BY_DAY, 0) );
        
        
        System.out.println(getDateInfoNode("2015-05-13"));
        
    }

    /**
     * 从时间字符串字产证时间节点
     * @param text
     * @return 时间节点
     * @throws NotSupportedException
     * @throws UnexpectedException 
     */
    public static DateNode getDateNodeFromStr(String text,String backtestTime) 
    		throws NotSupportedException, UnexpectedException{
    	DateRange range = DateCompute.getDateInfoFromStr(text, backtestTime);
    	DateNode dateNode = new DateNode(text);
    	dateNode.setDateinfo(range);
    	return dateNode;
    }
    
    /**
     * 从时间字符串产生时间节点，并且调整到交易日
     * @param text
     * @return
     * @throws NotSupportedException
     * @throws UnexpectedException
     */
    public static DateNode getDateNodeFromStrAndAdjustToTrade(String text,String backtestTime) 
    		throws NotSupportedException, UnexpectedException{
    	DateNode dateNode = getDateNodeFromStr(text, backtestTime);
    	dateNode = DateCompute.adjustToTradeDate(dateNode);
    	return dateNode;
    }
    
	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-23 上午11:34:54
	 * @description:  对时间list进行组合,大于unit部分使用splitYears中的时间
	 * 					小于等于unit部分使用stableDate中时间
	 *
	 */
	public static ArrayList<DateRange> getSplitDateByOtherDay(
			ArrayList<DateRange> splitYears, DateRange stableDate, Unit unit)
			throws UnexpectedException
	{
		if(splitYears==null || stableDate==null || unit==null){
			logger_.error("getSplitDateByOtherDay参数不能为空");
			return null;
		}
		ArrayList<DateRange> splitDates = new ArrayList<DateRange>();
		for (int i = 0; i < splitYears.size(); i++)
		{
			splitDates.add(createNewDateRangeByTwoDateRange(splitYears.get(i), stableDate,
					unit));
		}
		return splitDates;
	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2013-12-10 下午8:01:35
	 * @description: 把 dateRange中单位小于等于unit部分设置成stableDate中的时间
	 * 
	 */
	final static DateRange createNewDateRangeByTwoDateRange(DateRange bigDateRange,
			DateRange smallDateRange, Unit unit) throws UnexpectedException
	{
		if (bigDateRange == null || smallDateRange == null)
			return null;
		switch (unit)
		{
		case YEAR:
			bigDateRange.getFrom().setYear(smallDateRange.getFrom().getYear());
			bigDateRange.getTo().setYear(smallDateRange.getTo().getYear());
		case QUARTER:
		case MONTH:
			bigDateRange.getFrom().setMonth(smallDateRange.getFrom().getMonth());
			bigDateRange.getTo().setMonth(smallDateRange.getTo().getMonth());
		case DAY:
			bigDateRange.getFrom().setDay(smallDateRange.getFrom().getDay());
			bigDateRange.getTo().setDay(smallDateRange.getTo().getDay());
			break;

		default:
			throw new UnexpectedException("createNewDateRange中,该单位的转换尚未支持,联系开发");
		}
		return bigDateRange;
	}
	
	public static DateRange getDateFromNowByChangeNumberAndUnit(String changeByDay, int count) throws UnexpectedException
	{
		DateInfoNode pointonly = DateUtil.getNewDate(DateUtil.getNow(),changeByDay, count);
		DateRange range = new DateRange();
		range.setDateRange(pointonly, pointonly);
		return range;
	}
	
	
	public static DateRange getDateFromNowByChangeNumberAndUnit(String backtestTime, String changeByDay, int count) throws UnexpectedException
	{
		DateInfoNode date = DateUtil.getDateInfoNode(backtestTime);
		DateInfoNode pointonly = DateUtil.getNewDate(date,changeByDay, count);
		DateRange range = new DateRange();
		range.setDateRange(pointonly, pointonly);
		return range;
	}
	
	public static DateRange dateSeveralDaysOrWeeksOrMonthOrYearsBeforeOrAfterNow(
			String changeStr, String dateUnitStr, String tagStr,
			DateInfoNode now, boolean bSingleDay,String backtestTime) throws NotSupportedException,
			UnexpectedException
	{
		if (now == null || changeStr==null || dateUnitStr ==null ) {
			logger_.equals("dateSeveralDaysOrWeeksOrMonthOrYearsBeforeOrAfterNow" +
					"中参数now为空,或者changeStr为空,或者dateUnitStr为空");
			return null;
		}
		DateRange range = new DateRange();
		boolean isHalf = changeStr.equals("半");
		int change = isHalf ? 0 : Integer.valueOf(changeStr);
		String dateUnit = null;
		Unit unit = null;
		boolean isYear = dateUnitStr.equals(UnitDef.YEAR);
		boolean isQuarter = dateUnitStr.equals(UnitDef.QUARTER_1);
		boolean isMonth = dateUnitStr.equals(UnitDef.MONTH_1);
		boolean isWeek = dateUnitStr.equals(UnitDef.WEEK_1)
				|| dateUnitStr.equals(UnitDef.WEEK_2)
				|| dateUnitStr.equals(UnitDef.WEEK_3);
		boolean isDay = dateUnitStr.equals(UnitDef.DAY_1)
				|| dateUnitStr.equals(UnitDef.DAY_2);

		if (isYear)
		{
			unit = Unit.YEAR;
			dateUnit = isHalf ? DateUtil.CHANGE_BY_MONTH
					: DateUtil.CHANGE_BY_YEAR;
			change = isHalf ? 6 : change;
		}
		else if (isQuarter)
		{
			unit = Unit.QUARTER;
			dateUnit = isHalf ? DateUtil.CHANGE_BY_DAY
					: DateUtil.CHANGE_BY_MONTH;
			change = isHalf ? 45 : change * 3;
		}
		else if (isMonth)
		{
			unit = Unit.MONTH;
			dateUnit = isHalf ? DateUtil.CHANGE_BY_DAY
					: DateUtil.CHANGE_BY_MONTH;
			change = isHalf ? 15 : change;
		}
		else if (isWeek)
		{
			unit = Unit.WEEK;
			dateUnit = DateUtil.CHANGE_BY_DAY;
			change = isHalf ? 4 : change * 7;
		}
		else if (isDay)
		{
			unit = Unit.DAY;
			if (isHalf)
			{
				throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
						changeStr + dateUnitStr + tagStr);
			}
			dateUnit = DateUtil.CHANGE_BY_DAY;
		}
		else
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					changeStr + dateUnitStr + tagStr);
		}

		if (tagStr == null || tagStr.length() == 0)
		{
			// 若没有前后标记词，则将其转换为“近xx天”，进行解析
			try
			{
				range = DateCompute.getDateInfoFromStr("近" + changeStr + dateUnitStr, backtestTime);
			} catch (NotSupportedException e)
			{
				throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
						changeStr + dateUnitStr + tagStr);
			}
			return range;
		}
		range.setIsLength(true);
		boolean isBefore = tagStr.equals("前") || tagStr.equals("之前")
				|| tagStr.equals("以前");
		change = isBefore ? -1 * change : change;
		DateInfoNode newDin = DateUtil.getNewDate(now, dateUnit,
				change);

		if (bSingleDay)
		{
			range.setDateRange(newDin, newDin);
			range.setDateUnit(unit);
			range.setIsLength(true);
			range.setLength(1);
		}
		else
		{
			int head = isBefore ? -1 : 1;
			DateInfoNode headDin = DateUtil.getNewDate(now,
					dateUnit, head);
			range.setDateRange(headDin, newDin);
			range.setDateUnit(unit);
			range.setIsLength(true);
			range.setLength(Math.abs(change));
		}
		return range;
	}
	
	public static DateRange getReportWithYearNum(int year, int report)
	{
		if (report<1 || report>4) {
			logger_.error("getReportWithYearNum中参数报告期小于1或者大于4");
			return null;
		}
		//assert (report >= 1 && report <= 4);
		DateInfoNode pointOnly = report == 1 ? new DateInfoNode(year, 3, 31)
				: report == 2 ? new DateInfoNode(year, 6, 30)
						: report == 3 ? new DateInfoNode(year, 9, 30)
								: new DateInfoNode(year, 12, 31);
		DateRange range = new DateRange(pointOnly, pointOnly);
		range.setIsReport(true);
		return range;
	}
	
	public static DateRange getQuarterWithYearNum(int year, int quarter)
	{
		if (quarter<1 || quarter>4) {
			logger_.error("getQuarterWithYearNum中参数季度小于1或者大于4");
			return null;
		}
		//assert (quarter >= 1 && quarter <= 4);
		DateInfoNode pointhead = quarter == 1 ? new DateInfoNode(year, 1, 1)
				: quarter == 2 ? new DateInfoNode(year, 4, 1)
						: quarter == 3 ? new DateInfoNode(year, 7, 1)
								: new DateInfoNode(year, 10, 1);
		DateInfoNode pointend = quarter == 1 ? new DateInfoNode(year, 3, 31)
				: quarter == 2 ? new DateInfoNode(year, 6, 30)
						: quarter == 3 ? new DateInfoNode(year, 9, 30)
								: new DateInfoNode(year, 12, 31);
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.QUARTER);
		return range;
	}
	
	public static ArrayList<DateRange> getSplitDateForReportOrQuarter(
			ArrayList<DateRange> splitYears, int quarter, boolean forReport)
			throws UnexpectedException
	{
		if (splitYears == null || splitYears.size() == 0)
		{
			throw new UnexpectedException("split years is null");
		}
		ArrayList<DateRange> splitDates = new ArrayList<DateRange>();
		ArrayList<Integer> years = new ArrayList<Integer>();
		for (int i = 0; i < splitYears.size(); i++)
		{
			int year = splitYears.get(i).getTo().getYear();
			if (years.contains(year))
			{
				throw new UnexpectedException("年份有重复:%d", year);
			}
			years.add(year);
			DateRange addDate = forReport ? DateUtil.getReportWithYearNum(year, quarter)
					: DateUtil.getQuarterWithYearNum(year, quarter);
			splitDates.add(addDate);
		}
		return splitDates;
	}
	
	
	/**
	 * year, 4digits number
	 * month, 0-11
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getMonthEndDay(int year, int month) {
		int day=31;
		if(month==0){//1月
			day=31;
		}else if(month==3 || month==5 || month==8 || month==10) {//4,6,9,11月
			day=30;
		}else if(month==2 || month==4 || month==6 || month==7 || month==9 || month==11) { //3,5,7,8,10,12
			day=31;
		}else if(month==1) { //2月
			if( (year%100!=0 && year%4==0) || year%400==0) {
				day=29;
			}else{
				day=28;
			}
		}
		return day;
	}
    
	
	public static DateInfoNode getLatestDateByReportType(ReportType type, DateInfoNode date) {
		if(type==ReportType.QUARTER || type==ReportType.FUTURE_QUARTER ) {
			int year = date.getYear();
			int month = date.getMonth();
			int day = date.getDay();
			
			if(month<=3) {
				month = 3;
				day = 31;
			}else if(month<=6) {
				month = 6;
				day = 30;
			}else if(month<=9) {
				month = 9;
				day = 30;
			}else if(month<=12) {
				month = 12;
				day = 31;
			}
			date.setDateInfo(year, month, day);
		}else if(type==ReportType.YEAR || type==ReportType.FUTURE_YEAR) {
			int year = date.getYear();
			date.setDateInfo(year, 12, 31);
		}
		return date;
	}
}
