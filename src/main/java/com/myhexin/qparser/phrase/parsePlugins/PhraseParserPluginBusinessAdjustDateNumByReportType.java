package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.BindToType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.resource.model.SemanticCondInfo;
import com.myhexin.qparser.util.cfgdate.CfgDateLogicInstance;
import com.myhexin.qparser.util.condition.ConditionBuilderUtil;

public class PhraseParserPluginBusinessAdjustDateNumByReportType extends PhraseParserPluginAbstract{
	private final static CfgDateLogicInstance cfgDateInstance = CfgDateLogicInstance.getInstance();
	private final static SemanticCondInfo semanticConds = SemanticCondInfo.getInstance();
	
	public PhraseParserPluginBusinessAdjustDateNumByReportType() {
        super("Business_Adjust_Date_or_Num");
    }

	@Override
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		//System.out.println("PhraseParserPluginBusinessAdjustDateNumByReportType:" + annotation.getNodes());
		return adjustDateNum(annotation.getNodes(),annotation.getEnv(), annotation.getBacktestTime());
    }
	
	private final static Pattern yyyyYear = Pattern.compile("^([0-9]{4})(年?)$");//
	private final static Pattern yyyyYearmmMonth = Pattern.compile("^([0-9]{4})(年|-|)([0-9]{1,2})(月|)$");
	
	private final static Pattern continueNYear = Pattern.compile("^(最近|连续|近)([0-9]{1,2})(年?)$");//
	private final static Pattern continueOnly = Pattern.compile("^(最近|连续)$");//
	private final static Pattern continueNSeason = Pattern.compile("^(最近|连续|近)([0-9]{1,2})(个?)(季度|季)$");//
	//private final static Pattern continueNMonth = Pattern.compile("^(连续|近)([0-9]{1,2})(个?)(月)$");//
	private final static Pattern continueNDays = Pattern.compile("^(最近|连续|近)([0-9]{1,2})(天|日)$");//
	
	private List<DateInfoNode> getDateInfosOfTradeDay(int daycount, Calendar backtestTime) {
		DateInfoNode info = new DateInfoNode(backtestTime);
		try {
			List<DateInfoNode> infos = new ArrayList<DateInfoNode>(daycount);
			for(int i=daycount-1;i>=0;i--) {
				DateInfoNode info1;
				info1 = DateUtil.rollTradeDate(info, i);
				infos.add(info1);
			}
			return infos;
		} catch (UnexpectedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<DateInfoNode> getDateInfosOfYear(int yearcount, Calendar backtestTime) {
		int year = 0;
		if(backtestTime!=null) {
			year = backtestTime.get(Calendar.YEAR);
		}else{
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		List<DateInfoNode> infos = new ArrayList<DateInfoNode>(yearcount);
		for(int i=yearcount-1;i>=0;i--){
			infos.add(new DateInfoNode(year-(i+1),12,31));
		}
		return infos;
	}
	
	private List<DateInfoNode> getDateInfosOfQuarter(int count, Calendar backtestTime) {
		DateInfoNode dateinfo = new DateInfoNode(backtestTime);
		try {
			List<DateInfoNode> infos = new ArrayList<DateInfoNode>(count);
			for(int i=count;i>=0;i--) {
				DateInfoNode q1 = DateUtil.getNewDateByReportType(dateinfo, ReportType.QUARTER, i);
				infos.add(q1);
			}
			return infos;
		} catch (UnexpectedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<DateInfoNode> getDateInfosOfHalfYear(int count, Calendar backtestTime) {
		DateInfoNode dateinfo = new DateInfoNode(backtestTime);
		try {
			List<DateInfoNode> infos = new ArrayList<DateInfoNode>(count);
			for(int i=count;i>=0;i--) {
				DateInfoNode q1 = DateUtil.getNewDateByReportType(dateinfo, ReportType.HALF_YEAR, i);
				infos.add(q1);
			}
			return infos;
		} catch (UnexpectedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//TODO 跨年的时候要重启一下,节省资源免得每次请求都要Calendar.getInstance一次
	private final static int this_year = Calendar.getInstance().get(Calendar.YEAR);
	static boolean isThisYear(SemanticNode node) {
		DateRange date = null;
		if(node.isDateNode()) {
			DateNode dateNode = (DateNode)node;
			date = dateNode.getDateinfo();
		}
		if(date!=null && date.getFrom()!=null && date.getFrom().getYear()==this_year) {
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * 根据reportType, 和问句中时间的text,决定是否修改dateRange
	 * 
	 */
	private void adjustDateTime(ClassNodeFacade classNode, String propName, DateNode dateNode, Calendar backtestTime) {
		List<DateInfoNode> infos = null;
		String text = null;
		if(dateNode!=null)
			text = dateNode.getText();
		if( classNode.getReportType() == ReportType.DAILY || classNode.getReportType() == ReportType.TRADE_DAILY || classNode.getReportType() == ReportType.NATURAL_DAILY) {
			Matcher continueNDaysMatcher = null;
			
			//如果问句中日期是xxxx年的, 那么DateRange=201x-01-01 ~ 201x-12-31
			//直接转成DateRange=201x-12-31 ~ 201x-12-31
			if(text!=null && yyyyYear.matcher(text).matches() || yyyyYearmmMonth.matcher(text).matches()) {
				DateRange dr = dateNode.getDateinfo();
				if(dr!=null)
					dr.setDateRange(dr.getTo(), dr.getTo());
				else{
					dr = new DateRange();
					dr.setDateRange(dr.getTo(), dr.getTo());
					dateNode.setDateinfo(dr);
				}
			}else if(text!=null &&  (continueNDaysMatcher=continueNDays.matcher(text)).matches()) {
				int daycount =0;
				try{
				String nyear = continueNDaysMatcher.group(2);
				daycount = Integer.parseInt(nyear);
				}catch(Exception e){}
				if(daycount==0) daycount=3;
				
				infos = getDateInfosOfTradeDay(daycount, backtestTime);
			}else if(text!=null &&  continueOnly.matcher(text).matches()) {
				infos = getDateInfosOfTradeDay(3, backtestTime);
			}
		}else if(classNode.getReportType() == ReportType.QUARTER) {
			//报告期指标的时间调整逻辑
			Matcher continueNYearMatcher = null;
			Matcher continueNSeasonMatcher  = null;
			
			//TODO 报告期逻辑也可以搬到这里
			if(propName!=null && propName.equals("报告期") && (dateNode==null || isThisYear(dateNode))) {
				String dates = cfgDateInstance.getDatePeriod(classNode.getText(), propName + "_optimize", null);
				if(dates!=null) {
					String[] arr_dates = dates.split(",");
					if(arr_dates!=null && arr_dates.length>0) {
						infos = new ArrayList<DateInfoNode>(arr_dates.length);
						for(int i=arr_dates.length-1;i>=0;i--) {
							DateInfoNode info = new DateInfoNode(arr_dates[i]);
							infos.add(info);
						}
					}
				}
			}else if(text!=null && (continueNYearMatcher=continueNYear.matcher(text)).matches()) { //连续n年
				//1. 连续n年,要转成2012-12-31, 2013-12-31, 2014-12-31
				int yearnum =0;
				try{
				String nyear = continueNYearMatcher.group(2);
				yearnum = Integer.parseInt(nyear);
				}catch(Exception e){}
				if(yearnum==0) yearnum=3;
				infos = getDateInfosOfYear(yearnum, backtestTime);
			}else if(text!=null && continueOnly.matcher(text).matches()) { 				//连续
				infos =  getDateInfosOfQuarter(3, backtestTime);
			}else if(text!=null &&  (continueNSeasonMatcher = continueNSeason.matcher(text)).matches()) { //连续n个季度
				int seasoncount =0;
				try{
					String seasonstr = continueNSeasonMatcher.group(2);
					seasoncount = Integer.parseInt(seasonstr);
				}catch(Exception e){}
				if(seasoncount==0) seasoncount=3;
				
				infos =  getDateInfosOfQuarter(seasoncount, backtestTime);
			}
		}else if(classNode.getReportType() == ReportType.HALF_YEAR) {
			Matcher continueNDaysMatcher = null;
			if(text!=null && continueOnly.matcher(text).matches()) { 				//连续
				infos =  getDateInfosOfHalfYear(3, backtestTime);
			}if(text!=null && (continueNDaysMatcher=continueNDays.matcher(text)).matches()) { 				
				//连续n天,按正确逻辑,这里是不会有n天,这种情况,是因为时间解析插件会根据字母意思转成n天
				int daycount =0;
				try{
					String nyear = continueNDaysMatcher.group(2);
					daycount = Integer.parseInt(nyear);
				}catch(Exception e){}
				if(daycount==0) daycount=3;
				infos =  getDateInfosOfHalfYear(daycount, backtestTime);
			}
		}

		
		if(infos!=null && infos.size()>0) {
			if(dateNode==null) {
				DateNode newdateNode = new DateNode(infos.toString());
				DateRange dr = new DateRange();
				dr.setDateinfos(infos);
				newdateNode.setDateinfo(dr);
				
				//是报告期指标的,并且DateProp属性值=null的,设置一下DatePropValue
				if(propName!=null && propName.equals("报告期") && dateNode==null ) {
					setDatePropValueOfSeasonPeriodProp(classNode, newdateNode);
				}
			}else{
				DateRange dr = dateNode.getDateinfo();
				if(dr!=null) {
					dr.setDateinfos(infos);
				}else{
					dr = new DateRange();
					dr.setDateinfos(infos);
					dateNode.setDateinfo(dr);
				}
			}
		}
		
		
		
	
	}

	
	
	static class DatePropValue {
		private String propName;
		private DateNode dateNode;
	}
	
	/*
	 * 从ClassNodeFacade中取到时间
	 * 
	 */
	private DatePropValue getDatePropValue(ClassNodeFacade classNode) {
		List<PropNodeFacade> props = classNode.getAllProps();
		if(props!=null) {
			for(PropNodeFacade prop : props) {
				if(prop.isDateProp() && prop.getValue()!=null && prop.getValue().isDateNode()) {
					DatePropValue p = new DatePropValue();
					p.propName = prop.getText();
					p.dateNode  = (DateNode) prop.getValue();
					return p;
				}else if(prop.isDateProp() && prop.getText().equals("报告期")){
					DatePropValue p = new DatePropValue();
					p.propName = prop.getText();
					p.dateNode  = (DateNode) prop.getValue();
					return p;
				}
			}
		}
		return null;
	}
	
	private void setDatePropValueOfSeasonPeriodProp(ClassNodeFacade classNode, DateNode dateNode) {
		List<PropNodeFacade> props = classNode.getAllProps();
		if(props!=null) {
			for(PropNodeFacade prop : props) {
				if(prop.isDateProp() && prop.getText().equals("报告期") && prop.getValue()==null ) {
					prop.setValue(dateNode);
					break;
				}	
			}
		}
	}
	
	
	/* 调整日期
	 * 1. 遍历nodes 
	 * SyntacticIteratorImpl iterator = new SyntacticIteratorImpl(nodes);
	 * 
	 * 2. 找出focusNode
	 * 
	 * 3. 如果该FocusNode.classNode.props中有绑定到日期
	 * 
	 * 4. 调用adjustDateTime()方法， 日期调整的逻辑请看adjustDateTime()方法注释
	 * 
	 * 
	 * 
	 * @param nodes
	 * @param ENV
	 * @return
	 */
    private ArrayList<ArrayList<SemanticNode>> adjustDateNum(ArrayList<SemanticNode> nodes,Environment ENV, Calendar backtestTime) {
        ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);		 
 
        try{
        	SyntacticIteratorImpl iterator = new SyntacticIteratorImpl(nodes);
  			while (iterator.hasNext()) {
  				BoundaryInfos boundaryInfos = iterator.next();
  				String patternId = boundaryInfos.syntacticPatternId;
  				if(patternId==null)  {
  					for(int i=boundaryInfos.bStart;i<boundaryInfos.bEnd && i<nodes.size();i++) {
  						SemanticNode node = nodes.get(i);
  						if(node!=null && node.isFocusNode() && ((FocusNode)node).hasIndex() && ((FocusNode)node).getIndex()!=null) {
  							ClassNodeFacade cNode = ((FocusNode)node).getIndex();
  							DatePropValue dateNode = getDatePropValue(cNode);
  							if(dateNode!=null) adjustDateTime(cNode, dateNode.propName, dateNode.dateNode, backtestTime);
  						}
  					}
  				}else if(patternId.equals("FREE_VAR") || patternId.equals("STR_INSTANCE")) {
  					BoundaryNode bNode = (BoundaryNode)nodes.get(boundaryInfos.bStart);
  					if(bNode!=null && bNode.extInfo!=null) {
  						SemanticNode node = null;
  						int indexId = 1;
  						int newIndexId = bNode.extInfo.getElementNodePos(indexId);
  				    	if(newIndexId==-1 && bNode.extInfo!=null && bNode.extInfo.absentDefalutIndexMap!=null) {
  				    		node = bNode.extInfo.absentDefalutIndexMap.get(indexId);
  				    	}else{
  				    		indexId =boundaryInfos.bStart+ newIndexId;
  				    		if(indexId>=0 && indexId<nodes.size()) node = nodes.get(indexId);
  				    	}
  				    	if(node!=null && node.isFocusNode() && ((FocusNode)node).hasIndex() && ((FocusNode)node).getIndex()!=null) {
  				    		ClassNodeFacade cNode = ((FocusNode)node).getIndex();
  				    		DatePropValue dateNode = getDatePropValue(cNode);
  				    		if(dateNode!=null) adjustDateTime(cNode, dateNode.propName, dateNode.dateNode, backtestTime);
  						}
  					}
  				}else if(patternId.equals("KEY_VALUE")) {
  					BoundaryNode bNode = (BoundaryNode)nodes.get(boundaryInfos.bStart);
  					SemanticNode keyNode = ConditionBuilderUtil.getNodeKeyValue(1, bNode, boundaryInfos, nodes);
  			    	SemanticNode valueNode = ConditionBuilderUtil.getNodeKeyValue(2, bNode, boundaryInfos, nodes);
  			    	
  			    	if(keyNode!=null && keyNode.isFocusNode() && ((FocusNode)keyNode).hasIndex() && ((FocusNode)keyNode).getIndex()!=null) {
  			    		ClassNodeFacade cNode = ((FocusNode)keyNode).getIndex();
  			    		DatePropValue dateNode = getDatePropValue(cNode);
  			    		if(dateNode!=null)
  			    			adjustDateTime(cNode, dateNode.propName, dateNode.dateNode, backtestTime);
  			    	}
  			    	if(valueNode!=null && valueNode.isFocusNode() && ((FocusNode)valueNode).hasIndex() && ((FocusNode)valueNode).getIndex()!=null) {
  			    		ClassNodeFacade cNode = ((FocusNode)valueNode).getIndex();
  			    		DatePropValue dateNode = getDatePropValue(cNode);
  			    		if(dateNode!=null) adjustDateTime(cNode, dateNode.propName, dateNode.dateNode, backtestTime);
					}
  				}else{
  					BoundaryNode bNode = (BoundaryNode)nodes.get(boundaryInfos.bStart);
  					SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId); //句式
  					if (syntPtn == null) {
  						continue;
  					}
  					SemanticBind semanticBind = syntPtn.getSemanticBind();
  					ArrayList<SemanticBindTo> bindToList = semanticBind.getSemanticBindTos();
  					if (bindToList != null) {
  						for (SemanticBindTo sbt : bindToList) {
  							int semanticId = sbt.getBindToId();
  							ArrayList<SemanticBindToArgument> args = sbt.getSemanticBindToArguments();
  							for (SemanticBindToArgument arg : args) {
  								SemanticNode node = null;
  								int elementId = arg.getElementId();
  								if (arg.getType() == SemanArgType.INDEX || arg.getType() == SemanArgType.INDEXLIST) { //是指标,创建指标condition
  									if(arg.getBindToType() == BindToType.SYNTACTIC_ELEMENT){
  										if (arg.getSource() == Source.FIXED && bNode.extInfo != null && bNode.extInfo.fixedArgumentMap != null) {
  											node = bNode.extInfo.fixedArgumentMap.get(elementId);
  										} else if (bNode.extInfo != null) {
  											int newIndexId = bNode.extInfo.getElementNodePos(elementId);

  											if (newIndexId == -1 && bNode.extInfo != null && bNode.extInfo.absentDefalutIndexMap != null) {
  												node = bNode.extInfo.absentDefalutIndexMap.get(elementId);
  											} else {
  												newIndexId = boundaryInfos.bStart + newIndexId;
  												node = nodes.get(newIndexId);
  											}
  										}
  									}
  								}
  								
  								if(node!=null && node.isFocusNode() && ((FocusNode)node).hasIndex() && ((FocusNode)node).getIndex()!=null) {
  									ClassNodeFacade cNode = ((FocusNode)node).getIndex();
  									DatePropValue dateNode = getDatePropValue(cNode);
  									
  									//报告期逻辑只在没有过滤条件的句式语义中生效,比如有>,<这些条件的不走报告期逻辑
  									if(dateNode!=null && semanticConds.isCompareSemantic(semanticId)==false) {
  										adjustDateTime(cNode, dateNode.propName, dateNode.dateNode, backtestTime);
  									}
  		  						}
  							}
  						}
  					}
  				}
  			}
        }catch(Exception e) {
        	e.printStackTrace();
        }
        tlist.add(nodes);
        return tlist;
    }	
}
