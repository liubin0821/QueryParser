package com.myhexin.qparser.define;

import com.myhexin.qparser.define.EnumDef.Unit;

public class EnumConvert {

    public static Unit getUnitFromStr(String unitStr) {
    	Unit unit = Unit.UNKNOWN;
    	if (unitStr == null) 
            unit = Unit.UNKNOWN;
    	else if (unitStr.matches("^元|块$"))
			unit = Unit.YUAN;
    	else if (unitStr.matches("^港元|港币|港圆$"))
			unit = Unit.HKD;
    	else if (unitStr.matches("^美元|美金$"))
			unit = Unit.USD;
		else if (unitStr.equals("个"))
			unit = Unit.GE;
		else if (unitStr.equals("点"))
			unit = Unit.DIAN;
		else if (unitStr.equals("种"))
			unit = Unit.ZHONG;
		else if (unitStr.equals("次"))
			unit = Unit.CI;
		else if (unitStr.equals("倍"))
			unit = Unit.BEI;
		else if (unitStr.equals(""))
			unit = Unit.UNKNOWN;
		else if (unitStr.matches("^%|百分比$"))
			unit = Unit.PERCENT;
		else if (unitStr.equals("家"))
			unit = Unit.JIA;
		else if (unitStr.matches("^股|盘$"))
			unit = Unit.GU;
		else if (unitStr.equals("档"))
			unit = Unit.DANG;
		else if (unitStr.equals("户"))
			unit = Unit.HU;
		else if (unitStr.matches("^只|支$"))
			unit = Unit.ZHI;
		else if (unitStr.equals("手"))
			unit = Unit.SHOU;
		else if (unitStr.equals("岁"))
			unit = Unit.SUI;
		else if (unitStr.equals("位"))
			unit = Unit.WEI;
		else if (unitStr.matches("^天|日$"))
			unit = Unit.DAY;
		else if (unitStr.matches("^个?月$"))
			unit = Unit.MONTH;
		else if (unitStr.equals("年"))
			unit = Unit.YEAR;
		else if (unitStr.equals("周"))
			unit = Unit.WEEK;
		else if (unitStr.matches("^季|季度$"))
			unit = Unit.QUARTER;
		else if (unitStr.equals("半年"))
			unit = Unit.HALF_YEAR;
		else if (unitStr.equals("分钟"))
			unit = Unit.MUNITE;
		return unit;
    }
    
    public static String getStrFromUnit(Unit unit) {
    	String unitStr = "";
		switch(unit) {
		case YUAN:
			unitStr = "元";
			break;
		case HKD:
			unitStr = "港元";
			break;
		case USD:
			unitStr = "美元";
			break;
		case GE:
			unitStr = "个";
			break;
		case CI:
			unitStr = "次";
			break;
		case BEI:
			unitStr = "倍";
			break;
		case UNKNOWN:
			unitStr = "";
			break;
		case PERCENT:
			unitStr = "%";
			break;
		case JIA:
			unitStr = "家";
			break;
		case GU:
			unitStr = "股";
			break;
		case HU:
			unitStr = "户";
			break;
		case ZHI:
			unitStr = "只";
			break;
		case SHOU:
			unitStr = "手";
			break;
		case SUI:
			unitStr = "岁";
			break;
		case WEI:
			unitStr = "位";
			break;
		case DAY:
			unitStr = "日";
			break;
		case MONTH:
			unitStr = "月";
			break;
		case YEAR:
			unitStr = "年";
			break;
		case WEEK:
			unitStr = "周";
			break;
		case QUARTER:
			unitStr = "季度";
			break;
		case HALF_YEAR:
			unitStr = "半年";
			break;
		case MUNITE:
			unitStr = "分钟";
			break;
		default:
			unitStr = null;
		}
    	return unitStr;
    }
}


