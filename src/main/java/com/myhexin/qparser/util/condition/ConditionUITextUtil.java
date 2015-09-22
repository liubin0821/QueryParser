package com.myhexin.qparser.util.condition;

import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;

import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;

/**
 * 取得条件条
 * 就是wencai上显示的条件框里的内容
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-8-14
 *
 */
public class ConditionUITextUtil {
	
	private final static String WEEK_PERIOD = "周线";
	private final static String MONTH_PERIOD = "月线";
	private final static String YEAR_PERIOD = "年线";
	
	public static String getUITextOfZhubi(ConditionIndexModel indexCond) {
		StringBuilder buf = new StringBuilder();
		List<String> indexProperties = indexCond.getIndexProperties();
		
		String s1 = null;
		String time = null;
		String gushu = null;
		String tick = null;
		String yuan = null;
		String dang = null;
		for(String s : indexProperties) {
			if(s.startsWith("交易日期 ")) {
				s1 = s.substring(5);
			}else if(s.startsWith("交易日期")) {
				s1 = s.substring(4);
			}else if(s.startsWith("交易时间 ")) {
				time = s.substring(5);
			}else if(s.startsWith("交易时间")) {
				time = s.substring(4);
			}else if(s.startsWith("_整型数值(股)")) {
				gushu = s.substring("_整型数值(股)".length()+1) + "股";
			}else if(s.startsWith("_整型数值(次)")) {
				tick = s.substring("_整型数值(次)".length()) + "次";
				tick = tick.replace("(", ">");
			}else if(s.startsWith("_浮点型数值(元)")) {
				yuan = s.substring("_浮点型数值(元)".length()) + "元";
			}else if(s.startsWith("_整型数值(档)")){
				dang = s.substring("_整型数值(档)".length()) + "档";
			}
		}
		
		if(s1!=null) buf.append(dateToFormalDate(s1));
		if(time!=null) buf.append(timeToFormaltime(time));
		if(s1!=null) buf.append(indexCond.getIndexNameWithoutAtSign());
		if(gushu!=null) buf.append(gushu);
		if(tick!=null) buf.append(tick);
		if(yuan!=null) buf.append(yuan);
		if(dang!=null) buf.append(dang);
		return buf.toString();
	}
	
	public static String getUITextOfTechIndex(List<ConditionModel> conditionModels) {
		if(conditionModels!=null){
			String opName = null;
			String opProp = null;
			StringBuilder buf = new StringBuilder();
			for(ConditionModel cond : conditionModels) {
				if(cond.getConditionType() == ConditionModel.TYPE_INDEX) {
					buf.append(getUIText((ConditionIndexModel)cond, null, null, null));
				}else {
					ConditionOpModel opModel = (ConditionOpModel)cond;
					opName = opModel.getOpName();
					opProp = opModel.getOpProperty();
				}
			}
			
			if(opName!=null) {
				buf.append(opName);
			}
			if(opProp!=null) {
				buf.append(opProp);
			}
			return buf.toString();
		} 
		
		return null;
	}
	
	/**
	 * 算术运算的uitext
	 * 
	 * @param conditionOpModel
	 * @param conditionModels
	 * @return
	 */
	public static String getUITextOfArithmetic(List<ConditionModel> conditionModels) {
		if(conditionModels!=null && conditionModels.size()==3) {
			ConditionOpModel conditionOpModel = (ConditionOpModel)conditionModels.get(0);
			ConditionIndexModel i1 = (ConditionIndexModel)conditionModels.get(1);
			ConditionIndexModel i2 = (ConditionIndexModel)conditionModels.get(2);
			return getUIText(i1,null,null, null) + conditionOpModel.getOpName() +  getUIText(i2,null,null, null);
		}else if(conditionModels!=null){
			StringBuilder buf = new StringBuilder();
			for(ConditionModel cond : conditionModels) {
				if(cond.getConditionType() == ConditionModel.TYPE_INDEX) {
					buf.append(getUIText((ConditionIndexModel)cond, null, null, null));
				}
			}
			return buf.toString();
		} 
		
		return null;
	}
	
	public static String getUITextOfSort(ConditionIndexModel indexCond, String opProp) {
		String str = getUIText(indexCond, null, null, null);
		return str  + opProp;
		/*if(opProp.indexOf(op)>=0){
			return str  + opProp;
		}else{
			return str  + opProp + op;
		}*/
	}
	public static String getUITextOfHangYeSort(ConditionIndexModel indexCond, String opProp, ConditionModel indexCondHangYe) {
		StringBuilder buf = new StringBuilder();
		if(indexCondHangYe!=null && indexCondHangYe.getUIText()!=null) {
			buf.append(indexCondHangYe.getUIText());
		}
		String str = getUIText(indexCond, null, null, null);
		if(str!=null) buf.append(str);
		if(opProp!=null) buf.append(opProp);
		return buf.toString();
	}
	
	public static String getUITextOfContain(ConditionIndexModel indexCond, String opName, String containValue, ClassNodeFacade classNode) {
		String str = getUIText(indexCond, null, null, classNode);
		return str  + opName + containValue;
	}
	
	/*public static String getUITextByClassNode(ClassNodeFacade classNode) {
		StringBuilder buf = new StringBuilder();
		List<PropNodeFacade> props = classNode.getAllProps();
		if(props!=null) {
			String s1 = null;
			String s2 = null;
			String num = null;
			for(PropNodeFacade prop : props) {
				String s = prop.getText();
				if(s.startsWith("交易日期")) {
					s1 = s.substring(5);
				}else if(s.startsWith("起始交易日期")) {
					s1 = s.substring(7);
				}else if(s.startsWith("截止交易日期")) {
					s2 = s.substring(7);
				}
			}
		}
	}*/
	
	public static String getUITextOfStrInstance(ConditionIndexModel indexCond, String op, String opProperty, ClassNodeFacade classNode) {
		if(opProperty!=null) {
			return opProperty;
		}else{
			return getUIText(indexCond, op, opProperty, classNode);
		}
	}
	
	public static String getUIText(ConditionIndexModel indexCond, String op, String opProperty, ClassNodeFacade classNode) {
		
		if(indexCond.getClassNodeText()!=null && indexCond.getClassNodeText().indexOf("指数")>0) {
			return indexCond.getClassNodeText();
		}
		
		//指数Condition不需要uiText
		if(indexCond.getIndexName()!=null && indexCond.getIndexName().equals("指数")) {
			return Consts.STR_BLANK;
		}
		
		StringBuilder buf = new StringBuilder();
		//取日期和数字
		List<String> indexProperties = indexCond.getIndexProperties();
		if(indexProperties!=null && indexProperties.size()>0) {
			String s1 = null; //交易日期,起始交易日期 
			String s2 = null; //截止交易日期 
			String periodProp = null; //分析周期
			String num = null; //比较操作符中的number
			
			for(String s : indexProperties) {
				if(s.startsWith("交易日期 ")) {
					s1 = s.substring(5);
				}else if(s.startsWith("起始交易日期 ")) {
					s1 = s.substring(7);
				}else if(s.startsWith("截止交易日期 ")) {
					s2 = s.substring(7);
				}else if(s.startsWith("交易日期")) {
					s1 = s.substring(4);
				}else if(s.startsWith("起始交易日期")) {
					s1 = s.substring(6);
				}else if(s.startsWith("截止交易日期")) {
					s2 = s.substring(6);
				}else if(s.startsWith("分析周期 ")) {
					periodProp = s.substring(5);
				}else if(s.startsWith("分析周期")) {
					periodProp = s.substring(4);
				}else if(s.startsWith("(") || s.startsWith(">") || s.startsWith("<") || s.startsWith("=")) {
					num = s;
					if(num.startsWith("(")) num=num.replace("(", ">");
				}
			}
			
			String indexNameToBeUsed = null;
			if(classNode!=null && classNode.getText()!=null) {
				indexNameToBeUsed = getIndexNameByClassNode(classNode); 
			}
			if(indexNameToBeUsed==null){
				indexNameToBeUsed = denoiseIndexName(indexCond.getIndexNameWithoutAtSign()); //去除：符号后面的指标说明
			}
			boolean isLatestIndex = false; //是不是"最新xxx"指标
			if(indexNameToBeUsed!=null && indexNameToBeUsed.startsWith("最新")) {
				isLatestIndex = true;
			}
			
			//添加交易日期,起始交易日期
			if(isLatestIndex==false && StringUtils.isNotBlank(s1)) {
				if(buf.indexOf(WEEK_PERIOD) == -1 && buf.indexOf(MONTH_PERIOD) == -1 && buf.indexOf(YEAR_PERIOD) == -1) {
					buf.append(dateToFormalDate(s1));
				}
			}
			
			//添加分析周期
			if(isLatestIndex==false &&  StringUtils.isNotBlank(periodProp)) {
				String periodDesc = getPeriodDesc(periodProp);
				buf.append(periodDesc);
				int yearPos = buf.indexOf("年");
				int monthPos = buf.indexOf("月");
				int dayPos = buf.indexOf("日");
				if(StringUtils.isNotEmpty(periodDesc) && yearPos > 3 && yearPos < monthPos && monthPos < dayPos ) {
					buf.replace(yearPos - 4, dayPos + 1, StringUtils.EMPTY); //如果是周线，月线，年线的话，不应该含有具体的日期
				}
			}
			
			//截止交易日期 
			if(isLatestIndex==false &&  s2!=null && s2.equals(s1)==false) {
				if(buf.indexOf(WEEK_PERIOD) == -1 && buf.indexOf(MONTH_PERIOD) == -1 && buf.indexOf(YEAR_PERIOD) == -1) {
					buf.append("到").append(dateToFormalDate(s2));
				}
			}
			
			//添加指标
			if(indexNameToBeUsed!=null) buf.append(indexNameToBeUsed);
			/*if(classNode!=null && classNode.getText()!=null) {
				//String domainPrefix = indexCond.getIndexNameAtSignPrefix();
				//if(domainPrefix!=null) {
				//	buf.append(domainPrefix);
				//}
				String text = getIndexNameByClassNode(classNode); 
				if(text!=null && text.length()>0) {
					buf.append(text);
				}
			}else{
				String formattedIndexName = denoiseIndexName(indexCond.getIndexNameWithoutAtSign()); //去除：符号后面的指标说明
				buf.append(formattedIndexName);
			}*/
			
			//添加数字,其实这个num里包含了比较操作符了
			if(num!=null) {
				buf.append(num);
			}
			
			//排名等op
			if(op!=null) {
				buf.append(op);
			}
			
			//排名等op的属性,比如从大到小排序
			if(opProperty!=null) {
				buf.append(opProperty);
			}
		}else{
			//指标
			String text = getIndexNameByClassNode(classNode); 
			if(text==null) {
				text = indexCond.getIndexNameWithoutAtSign();
			}
			if(text==null || text.equals("指数")==false) {
				buf.append(text);
			}
		}
		String uiText = buf.toString();
		Matcher matcher = DatePatterns.DUPLICATE_DAY.matcher(uiText);
		if (matcher.find()) {
			return matcher.group(4);
		}
		return uiText;
	}
	
	/**
	 * 去除:后面的指标说明
	 * @author huangmin
	 *
	 * @param indexName
	 * @return
	 */
	private static String denoiseIndexName(String indexName) {
		if(StringUtils.isNotBlank(indexName) && indexName.contains(":")) {
			int commPos = indexName.indexOf(":");
			return indexName.substring(0, commPos);
		}
		
		return indexName;
	}

	private static String getIndexNameByClassNode(ClassNodeFacade classNode) {
		if(classNode!=null && classNode.getText()!=null) {
			StringBuilder buf = new StringBuilder();
			List<PropNodeFacade> props = classNode.getAllProps();
			boolean isSpecialAttr = false;
			if(props!=null) {
				for(PropNodeFacade p : props ) {
					if(p.getValue()!=null && p.getText()!=null && p.getText().equals("abs_人")) {
						buf.append(p.getValue().getText());
						buf.append(classNode.getText());
						isSpecialAttr = true;
						break;
					}else if(p.getValue()!=null && p.getText()!=null && p.getText().equals("abs_指数属性指标")) {
						buf.append(classNode.getText());
						buf.append(p.getValue().getText());
						isSpecialAttr = true;
						break;
					}
				}
			}
			if(isSpecialAttr==false) {
				buf.append(classNode.getText());
			}
			return buf.toString();
		}else{
			return null;
		}
	}
	
	private static String dateToFormalDate(String date) {
		if(date!=null && date.length()==8) {
			return date.substring(0,4) + "年" + date.substring(4,6) + "月" + date.substring(6) + "日";
		}
		return date;
	}
	
	/**
	 * 获取周期的中文表示法，比如WEEK 转换为 周线
	 * @param periodUnit
	 * @return
	 */
	private static String getPeriodDesc(String periodUnit) {
		String periodDesc = StringUtils.EMPTY;
		switch(periodUnit) {
		case "WEEK": 
			periodDesc = WEEK_PERIOD;
			break;
		case "YEAR": 
			periodDesc = YEAR_PERIOD;
			break;
		case "MONTH": 
			periodDesc = MONTH_PERIOD;
			break;
		}
		
		return periodDesc;
	}
	
	/*private static String dateToFormaltime(String date) {
		if(date!=null && date.length()==8) {
			String year = date.substring(0,4);
			String mm = date.substring(4,6);
			String dd = date.substring(6);
			return year  +"年" + mm + "月" + dd + "日";
		}else{
			return date;
		}
	}*/
	
	private static String timeToFormaltime(String time) {
		//092000000-
		
		if(time!=null && time.length()==19) {
			StringBuilder buf = new StringBuilder();
			String[] a = time.split("-");
			if(a!=null && a.length==2) {
				if(a[0]!=null && a[0].length()==9) {
					String s = a[0].substring(0,2);
					buf.append(s).append("点");
					s = a[0].substring(2,4);
					if(s.equals("00")==false) buf.append(s).append("分");
					s = a[0].substring(4,6);
					if(s.equals("00")==false) buf.append(s).append("秒");
					s = a[0].substring(6);
					if(s.equals("000")==false) buf.append(s).append("毫秒");
					buf.append("到");
					s = a[1].substring(0,2);
					buf.append(s).append("点");
					s = a[1].substring(2,4);
					if(s.equals("00")==false) buf.append(s).append("分");
					s = a[1].substring(4,6);
					if(s.equals("00")==false) buf.append(s).append("秒");
					s = a[1].substring(6);
					if(s.equals("000")==false) buf.append(s).append("毫秒");
					
				}
			}
			return buf.toString();
		}else{
			return time;
		}
	}

	/*static List<String[]> getIndexInfo(List<ConditionModel> conds) {
		if(conds==null) {
			return null;
		}
		
		List<String[]> list = new ArrayList<String[]>();
		for(ConditionModel cond : conds) {
			if(cond.getConditionType() == ConditionModel.TYPE_INDEX ) {
				ConditionIndexModel indexCond = (ConditionIndexModel) cond;
				String[] str = new String[4];
				str[0] = indexCond.getIndexNameWithoutAtSign();
				
				//取日期和数字
				List<String> indexProperties = indexCond.getIndexProperties();
				if(indexProperties!=null && indexProperties.size()>0) {
					String s1 = null;
					String s2 = null;
					String num = null;
					for(String s : indexProperties) {
						if(s.startsWith("交易日期")) {
							s1 = s.substring(5);
						}else if(s.startsWith("起始交易日期")) {
							s1 = s.substring(7);
						}else if(s.startsWith("截止交易日期")) {
							s2 = s.substring(7);
						}else if(s.startsWith("(") || s.startsWith(">") || s.startsWith("<") || s.startsWith("=")) {
							StringBuilder buf = new StringBuilder();
							for(int i=1;i<s.length();i++) {
								char c = s.charAt(i);
								if((c>='0' && c<='9') || c=='%' || c=='.') {
									buf.append(c);
								}
							}
							num = buf.toString();
						}
					}
					
					str[1] = s1;
					str[2] = s2;
					str[3] = num;
					
					list.add(str);
				}
			}
		}
		
		return list;
	}*/
}
