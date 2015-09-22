package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.myhexin.qparser.date.DateFrequencyInfo;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.TechPeriodType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.ConsistPeriodNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindToArgument.Source;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticPattern;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.model.IndexDefDateInfo;
import com.myhexin.qparser.resource.model.IndexIdNameMapInfo;
import com.myhexin.qparser.resource.model.RefCodeInfo;
import com.myhexin.qparser.resource.model.SemanticCondInfo;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.time.info.TimePoint;
import com.myhexin.qparser.time.info.TimeRange;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;

public abstract class ConditionBuilderAbstract {
	//private static Logger _logger = LoggerFactory.getLogger(ConditionBuilderAbstract.class.getName());

	public abstract List<ConditionModel> doBuild(ConditionParam param) throws BacktestCondException;

	//入口
	public List<ConditionModel> buildCondition(ConditionParam param) throws BacktestCondException {
		List<ConditionModel> rtn = doBuild(param);
		return rtn;
	}

	/*
	 * 创建比如排序,+/-/x/divide 这类
	 */
	protected ConditionOpModel buildParentOpModel(SemanticOpModel opModel) {
		if(opModel==null) {
			return null;
		}
		
		//排序, +/-/*/divide等parent类型的op
		if( StringUtils.isNotEmpty( opModel.getOpName() ) ) {
			ConditionOpModel opCond = new ConditionOpModel();
			opCond.setOpName(opModel.getOpName());
			opCond.setOpProperty(opModel.getOpProperty());
			return opCond;
		}else{
			return null;
		}
	}
	
	
	/**
	 * 从nodes中取到指标
	 * 
	 * 
	 * @param arg, 语义参数
	 * @param bNode, BoundaryNode
	 * @param nodes, List<SemanticNode>
	 * @param bStart, Boundary.start
	 * @return
	 */
	protected SemanticNode getElement(SemanticBindToArgument arg, List<SemanticNode> nodes,
			int semanticId, int bStart) {
		SemanticNode node = null;
		int elemId = arg.getElementId();

		if(arg.getType() == SemanArgType.INDEX) {
			node = getElementByPos(arg, nodes, bStart, elemId);
			//TODO 处理嵌套
			//判断arg绑定其它语义
			//if (arg.getBindToType() == BindToType.SEMANTIC) {

			//SemanticCondCompilerFactory factory = new SemanticCondCompilerFactory();
			//elemId = factory.getIndexId(annotation, elemId);
			//}

			//TODO 处理固定值
			//			if (arg.getSource() == Source.FIXED && defaultIndex != null && defaultIndex.trim().length() > 0) {
			//				ClassNodeFacade cNode = getClassNodeByDefaultIndex(defaultIndex);
			//				if (cNode != null && indexNode != null && indexNode.isFocusNode()) {
			//					((FocusNode) indexNode).setIndex(cNode);
			//					indexNode.setText(cNode.getText());
			//				}
			//			}
		} else {// if (arg.getType() == SemanArgType.CONSTANT || arg.getType() == SemanArgType.ANY) {
			node = getElementByPos(arg, nodes, bStart, elemId);
			SemanticPattern semanticPattern = PhraseInfo.getSemanticPattern(String.valueOf(semanticId));
			if (semanticPattern != null) {
				SemanticArgument semanticArgument = semanticPattern.getSemanticArgument(arg.getArgumentId(), true);

				//TODO 处理时间属性
				if (node == null) {
					String defaultValue = semanticArgument.getDefaultValue();
					node = createNodeByValueType(semanticArgument, defaultValue);
				}
				node = removeUnit(semanticArgument, node);
			}
		}

		
		return node;
	}
	
	/**
	 * 根据nodeIndex 从句子中拿到SemanticNode
	 * 
	 * @param bNode
	 * @param boundaryInfos
	 * @param nodes
	 * @param nodeIndex
	 * @return
	 */
	protected SemanticNode getElementByPos(SemanticBindToArgument argument, List<SemanticNode> nodes, int bStart,
			int elementId) {
		BoundaryNode bNode = (BoundaryNode) nodes.get(bStart);
		SemanticNode node = null;
		if (argument.getSource() == Source.FIXED && bNode.extInfo != null && bNode.extInfo.fixedArgumentMap != null) {
			node = bNode.extInfo.fixedArgumentMap.get(elementId);
		} else if (bNode.extInfo != null) {
			int newIndexId = bNode.extInfo.getElementNodePos(elementId);

			if (newIndexId == -1 && bNode.extInfo != null && bNode.extInfo.absentDefalutIndexMap != null) {
				node = bNode.extInfo.absentDefalutIndexMap.get(elementId);
			} else {
				newIndexId = bStart + newIndexId;
				node = nodes.get(newIndexId);
			}
		}

		return node;
	}



	public List<ConditionIndexModel> toConditionList(FocusNode fNode, String domain, Calendar backtestTime, String semanticInfo) {
		List<ConditionIndexModel> models = null;
		if(fNode!=null && fNode.hasIndex() && fNode.getIndex()!=null) {
			ClassNodeFacade cNode = fNode.getIndex();
			//Calendar[] timeByReportType = IndexDefDateInfo.getInstance().getDate(cNode.getText(),domain, backtestTime);
			ConditionIndexModel indexCond =  toCondition(fNode, domain, backtestTime);
			List<PropNodeFacade> props = cNode.getAllProps();
			if(props!=null) {
				//判断是不是"连续3天涨跌幅"类型的问句,如果是,要把连续3天拆成3个condition
				models = ConditionAndDateUtil.checkAndDupIndex(indexCond, props);
				/*if(ConditionAndDateUtil.shouldCheckReportPeriodAndDupIndex(semanticInfo,models) ){
					//报告期逻辑
					models = ConditionAndDateUtil.checkReportPeriodAndDupIndex(indexCond, props);
				}*/
			}
			
			if(models==null || models.size()==0){
				models = new ArrayList<ConditionIndexModel>(1);
				models.add(indexCond);
			}else{
				
			}
			
			//abs_指数属性指标,处理指数{@b}涨跌幅
			/*if(cNode!=null) {
				List<ConditionIndexModel> indexConds = checkAbsIndexPropIndex(cNode, domain, backtestTime);
				if(indexConds!=null) {
					models.addAll(indexConds);
				}
			}*/
		}
		return models;
	}
	
	
	//abs_指数属性指标,处理指数{@b}涨跌幅
	/*private List<ConditionIndexModel> checkAbsIndexPropIndex(ClassNodeFacade cNode, String domain, Calendar backtestTime) {
		List<ConditionIndexModel> models = null;
		List<PropNodeFacade> props = cNode.getAllProps();
		if(props==null || props.size()==0 ) return null;
		ConditionIndexModel indexCond =  null;
		ClassNodeFacade pcNode = null;
		for(PropNodeFacade prop : props) {
			if(prop.getText()!=null && prop.getValue()!=null && prop.getText().equals("abs_指数属性指标")) {
				SemanticNode node = prop.getValue();
				if(node.isFocusNode() && ((FocusNode)node).hasIndex() && ((FocusNode)node).getIndex()!=null) {
					pcNode = ((FocusNode)node).getIndex();
				}else if(node.isClassNode()){
					pcNode = (ClassNodeFacade)node;
				}
				if(pcNode!=null)
					indexCond =  toCondition(pcNode, domain, backtestTime, null);
				break;
			}
		}
		if(indexCond!=null && pcNode!=null) {
			List<PropNodeFacade> p_props = pcNode.getAllProps();
			if(p_props!=null) {
				//判断是不是"连续3天涨跌幅"类型的问句,如果是,要把连续3天拆成3个condition
				models = checkAndDupIndex(indexCond, p_props);
			}
			
			if(models==null){
				models = new ArrayList<ConditionIndexModel>(1);
				models.add(indexCond);
			}
		}
		
		return models;
	}*/

	
	public ConditionIndexModel toCondition(FocusNode fNode, String domain, Calendar backtestTime) {
		return toCondition(fNode, domain, backtestTime, null, null);
		
	}
	
	
	public ConditionIndexModel toCondition(FocusNode fNode, String domain, Calendar backtestTime, SemanticNode valueNode, String containOp) {
		if(fNode!=null && fNode.hasIndex() && fNode.getIndex()!=null) {
			return toCondition(fNode.getIndex(), domain, backtestTime, valueNode, containOp);
		}else{
			return null;
		}
	}
	/**
	 * 把FocusNode转成Condition
	 * 
	 * @param fNode
	 * @return
	 */
	public ConditionIndexModel toCondition(ClassNodeFacade cNode, String domain, Calendar backtestTime, SemanticNode valueNode, String containOp) {
		
		if(cNode!=null && cNode.getText()!=null) {
			ReportType type = cNode.getReportType();
			String text = cNode.getText();
			ConditionIndexModel indexCond = new ConditionIndexModel(cNode.getId(), text,domain);
			indexCond.setIndexId(cNode.getId());
			indexCond.setDomain(domain);
			indexCond.setClassNodeText(cNode.getText());
			String macroDomainName = getMacroDomainName(cNode,0);
			if(macroDomainName!=null) {
				indexCond.setIndexName(text, macroDomainName);
				indexCond.setDomain(macroDomainName);
			}else{
				indexCond.setIndexName(text, domain);
			}
			indexCond.setReportType(cNode.getReportType());
			PropNodeFacade vp = cNode.getPropOfValue();
			if(vp!=null)
				indexCond.setValueType(vp.getText());
			else{
				indexCond.setValueType(Consts.STR_BLANK);
			}
			List<PropNodeFacade> props = cNode.getAllProps();
			
			//保证timeByReportType一定不为NULL
			Calendar[] timeByReportType = IndexDefDateInfo.getInstance().getDate(indexCond.getIndexName(),cNode.getReportType(), domain, backtestTime);
			addPropsToIndexCond(indexCond, props, timeByReportType, valueNode, domain, type, containOp);
			
			if(macroDomainName!=null || Consts.CONST_absSmallMoneyGod.equals(macroDomainName) || Consts.CONST_absMarketEnv.equals(macroDomainName) ) {
				indexCond.setType(Consts.CONST_Type_Macro_Ecnomic);
			}
			return indexCond;
		}
		else{
			return null;
		}
	}
	
	//目前只有"abs_市场环境"领域才会被加进来
	public String getMacroDomainName(ClassNodeFacade cNode, int deep) {
		String text = cNode.getText();
		if(text!=null && (Consts.CONST_absMarketEnv.equals(text) || Consts.CONST_absSmallMoneyGod.equals(text)) )  {
			return text;
		}else if(deep>20){
			return null;
		}else if(cNode.getSuperClass().size()>0 ){
			for(ClassNodeFacade spClass : cNode.getSuperClass()){
				String _name = getMacroDomainName(spClass, deep++);
	    		if(_name!=null) {
	    			return _name;
	    		}
	    	}
		}
		
		return null;
	}
	
	
	
	private DateRange getDateRangeByTimes(Calendar[] backtestTime) {
		DateRange daterange = new DateRange();
		if(backtestTime!=null && backtestTime.length==1) {
			//time = DateInfoNode.toString(backtestTime[0], "-");
			DateInfoNode dateinfo = new DateInfoNode(backtestTime[0]);
			daterange.setFrom(dateinfo);
			daterange.setTo(dateinfo);
		}else if(backtestTime!=null && backtestTime.length==2 && !backtestTime[0].equals(backtestTime[1])) {
			//time = DateInfoNode.toString(backtestTime[0], "-") + "到" + DateInfoNode.toString(backtestTime[1], "-");
			daterange.setFrom(new DateInfoNode(backtestTime[0]));
			daterange.setTo(new DateInfoNode(backtestTime[1]));
		}
		return daterange;
	}
	
	private void addDateTimeProp_OfReportType(ConditionIndexModel indexCond, ReportType type, DateNode dateNode) {
		//+区间 转成 起始-截止交易日期
		if(type==ReportType.QUARTER || type==ReportType.FUTURE_QUARTER || type==ReportType.YEAR || type==ReportType.FUTURE_YEAR) {
			//如果是季度报,年报类型的指标,那么只返回3/31
			//fixbug, 1季度建筑装饰行业排名
			String fromStr = null;
			String toStr = null;
			
			if(dateNode!=null && dateNode.getDateinfo()!=null ) {
				if(dateNode.getDateinfo().getFrom()!=null)
				{
					DateInfoNode from = DateUtil.getLatestDateByReportType(type, dateNode.getDateinfo().getFrom());
					fromStr = from.toString("");
				}
				
				//20150330建筑装饰行业每股收益排名,20150330->20150331
				if(dateNode.getDateinfo().getTo()!=null)
				{
					DateInfoNode to = DateUtil.getLatestDateByReportType(type, dateNode.getDateinfo().getTo());
					toStr = to.toString("");
				}
					
			}
			
			if(fromStr==null){
				fromStr = dateNode.getFrom();
			}
			if(toStr==null){
				toStr = dateNode.getTo();
			}
			indexCond.addProp(DAY_RANGE_START, fromStr);
			indexCond.addProp(DAY_RANGE_END, toStr);
		}else{
			indexCond.addProp(DAY_RANGE_START, dateNode.getFrom());
			indexCond.addProp(DAY_RANGE_END, dateNode.getTo());
		}
		
		
	}
	
	/**
	 * 往indexcondition里添加属性,如果有分时属性也一起添加
	 * @param indexCond
	 * @param prop
	 * @param backtestTime
	 * @param hasTechProp
	 */
	protected void addDateTimeProp(ConditionIndexModel indexCond, PropNodeFacade prop, Calendar[] backtestTime,
			boolean hasMinProp, ReportType type) {
		if (prop.isDateProp() && prop.getValue() != null
				&& (prop.getValue().isDateNode() || prop.getValue().isTimeNode())) {
			SemanticNode valueNode = prop.getValue();

			//如果时间属性是null,但是回测时间不为null,强制设置为回测时间
			if (valueNode == null && backtestTime != null) {
				String time = null;
				DateRange daterange = getDateRangeByTimes(backtestTime);
				if(backtestTime!=null && backtestTime.length==1) {
					time = DateInfoNode.toString(backtestTime[0], "-");
				}else if(backtestTime!=null && backtestTime.length==2 && !backtestTime[0].equals(backtestTime[1])) {
					time = DateInfoNode.toString(backtestTime[0], "-") + "到" + DateInfoNode.toString(backtestTime[1], "-");
				}
				
				DateNode _valueNode = new DateNode(time);
				_valueNode.setDateinfo(daterange);
				valueNode = _valueNode;
			}

			if (valueNode.isDateNode()) {
				DateNode dateNode = (DateNode) valueNode;
				boolean isAddTime = false;
				if (N_DAY.equals(prop.getText())) {
					String value = valueNode.getText();
					indexCond.addProp(prop.getText(), value);
				} else if (DAY_RANGE.equals(prop.getText()) || REPORTDATE.equals(prop.getText()) || YEAR_LEVEL.equals(prop.getText())) {
					addDateTimeProp_OfReportType(indexCond, type, dateNode);
				} else if (DAY_TIME.equals(prop.getText())) {
					//+区间 转成 起始-截止交易日期
					String value = ConditionBuilderUtil.getTimeValue(valueNode);
					if (value != null) {
						indexCond.addProp(DAY_TIME, value);
						isAddTime = true;
					}
				} else {
					String from = dateNode.getFrom();
					String to = dateNode.getTo();
					if (from != null && to != null && !from.equals(to)) {
						addDateTimeProp_OfReportType(indexCond, type, dateNode);
					} else {
						if (YEAR_SEASON.equals(prop.getText())) {
							indexCond.addProp(DAY_TRADE_DAY, from); //年度季度=>交易日期
						}if (END_DAY.equals(prop.getText())) {
							indexCond.addProp(DAY_TRADE_DAY, from); //年度季度=>交易日期
						}else{
							indexCond.addProp(prop.getText(), from);
						}
					}
				}

				//添加分时属性
				TimeNode time = dateNode.getTime();
				if (time != null || hasMinProp) {
					addTimeProp(indexCond, time);
				}

				//添加出现次数属性
				DateFrequencyInfo frequencyInfo = dateNode.getFrequencyInfo();
				if(frequencyInfo!=null){
					long length = frequencyInfo.getLength().getLongFrom();
					indexCond.addProp(OCCURTIME, String.valueOf(length));
				}

				if (isAddTime == false && indexCond.getIndexName() != null && valueNode != null
						&& (indexCond.getIndexName().indexOf("逐笔") >= 0 || indexCond.getIndexName().indexOf("一笔") >= 0)) {
					String value = ConditionBuilderUtil.getTimeValue(valueNode);
					if (value != null)
						indexCond.addProp(DAY_TIME, value);
				}
			} else if (valueNode.isTimeNode()) {
				addTimeProp(indexCond, (TimeNode) valueNode);
			}

		}
	}

	private void addTimeProp(ConditionIndexModel indexCond, TimeNode time) {
		if (time != null && time.getRealRange() != null) {
			String indexName = indexCond.getIndexName();
			//逐笔问句就算有分时属性也不在指标前加"分时"
			if (!indexName.startsWith("一笔") && !indexName.startsWith("逐笔")) {
				indexCond.setIndexName("分时" + indexName);
			}
			TimeRange range = time.getRealRange();
			TimePoint from = range.getFrom();
			TimePoint to = range.getTo();
			if (from != null && to != null && !from.toStringWithOutSecond().equals(to.toStringWithOutSecond())) {
				indexCond.addProp(TIME_RANGE_START, from.toStringWithOutSecond());
				indexCond.addProp(TIME_RANGE_END, to.toStringWithOutSecond());

				indexCond.addProp(RANGEOFFSET, "[" + -1 * to.minus(from) + ",0]");
			} else {
				indexCond.addProp(DAY_TIME, from.toStringWithOutSecond());
				indexCond.addProp(RANGEOFFSET, "[0,0]");
			}
		}
	}

	
	protected void addPropsToIndexCond(ConditionIndexModel indexCond, List<PropNodeFacade> props, Calendar[] backtestTime, SemanticNode strNode, String domain, ReportType type, String containOp) {
		if(props!=null) {
			boolean hasMinProp = false;
			//遍历查看是否有分时技术属性
			for (PropNodeFacade prop : props) {
				if (prop.isTechPeriodProp()) {
					SemanticNode node = prop.getValue();
					if (node != null && node.isTechPeriodNode()) {
						TechPeriodNode techPeriodNode = (TechPeriodNode) node;
						hasMinProp = techPeriodNode.getPeriodType() == TechPeriodType.MIN;
					}
					break;
				}
			}
			//List<String> propStrs = new ArrayList<String>();
			for(PropNodeFacade prop : props) {
				if(SemanticCondInfo.getInstance().isAllowOutputProp(prop.getText())) {
					//时间属性,要考虑回测时间,特殊处理
					SemanticNode valueNode = prop.getValue();
					String value = null;
					if(prop.isDateProp() ) {
						if(valueNode==null) {
							String date = DateInfoNode.toString(backtestTime[0], "-");
							TimePoint time = DateInfoNode.ToTimePoint(backtestTime[0]);
							DateNode _dateValue = new DateNode(date);
							TimeNode timeNode = new TimeNode(time.toStringWithOutSecond());
							TimeRange range = new TimeRange(time, time);
							timeNode.setTimeRange(range);
							
							DateRange daterange = getDateRangeByTimes(backtestTime);
							_dateValue.setDateinfo(daterange);
							if (hasMinProp) {
								_dateValue.setTime(timeNode);
							}
							valueNode = _dateValue;
							prop.setValue(_dateValue);
						}
						addDateTimeProp(indexCond, prop, backtestTime, hasMinProp, type);
					} else if (prop.isTechPeriodProp()) {
						if(valueNode!=null && valueNode.isTechPeriodNode()) {
							TechPeriodNode tp_node  = (TechPeriodNode)valueNode;
							indexCond.addProp(prop.getText(), String.valueOf(tp_node.getPeriodType()) );
							indexCond.addProp(Consts.TECH_PERIOD_VALUE, tp_node.getNumNodeStr());
						}
					} else if (prop.isConsistPeriodProp()) {
						if (valueNode != null && valueNode.isConsistPeriodNode()) {
							ConsistPeriodNode cp_node = (ConsistPeriodNode) valueNode;
							indexCond.addProp(Consts.CONSIST_PERIOD_VALUE, String.valueOf(cp_node.getPeriodNum()));
						}
					} else if (valueNode != null) {
						if(valueNode.isStrNode()) {
							value = valueNode.getText();
						}else if(valueNode.isFocusNode()) {
							FocusNode fNode = (FocusNode) valueNode;
							if(fNode.hasIndex() && fNode.getIndex()!=null) {
								value = fNode.getIndex().getText();
							}else{
								value = fNode.getText();
							}
						}else{
							value = valueNode.getText();
						}
						
						if(prop.getText()!=null && prop.getText().equals("abs_人")) {
							indexCond.setIndexName(value + indexCond.getIndexName());
							value= null;
						}
						
						//这个要特殊处理
						if(prop.getText()!=null && prop.getText().equals("abs_指数属性指标") && valueNode.isClassNode()) {
							value = null;
							continue;
						}
						//propStrs.add(prop.getText() + " " + value);
					}/*else if(SemanticCondInfo.getInstance().isAlwaysOutputProp(prop.getText())){
						//是日期类型的属性
						if(prop.isDateProp()) {
							try {
								if(backtestTime==null)
									value = DateUtil.getLatestTradeDate(true).toString("");
								else{
									value = DateInfoNode.toString(backtestTime, "");//backtestTime.getFrom().toString("");
								}
							} catch (UnexpectedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}*/
					if(value!=null)
					{
						if(CONTAIN.equals(prop.getText())) {
							indexCond.addProp(prop.getText(),value, true);
						}else{
							indexCond.addProp(prop.getText(),value);
						}
					}
				}
			}
			
			if (hasMinProp) {
				completeDateAndTime(indexCond);
			}

			String containStr1 = null;
			if(strNode!=null) {
				containStr1 = strNode.getText();
			}
			
			
			//指数的才去取ID
			if(RefCodeInfo.getInstance().shouldConvert2Id(indexCond.getIndexName())==false ) {
				//概念的不需要取ID,直接返回"所属概念包含迪士尼"
				if(containStr1!=null) indexCond.addProp(containOp, containStr1, false);
			}else{
				//包含属性,优先使用StrNode
				String containValue = null;
				if(containStr1!=null) {
					containValue = IndexIdNameMapInfo.getInstance().getIdListStr(indexCond.getIndexName(), containStr1);
					if(containValue==null) containValue = containStr1;
				}else{ //这个contain是不可少的
					//bug：地狱指数今日成交额最大的，地狱指数没有ID
					if(indexCond.getClassNodeText()!=null)
						containValue = IndexIdNameMapInfo.getInstance().getIdListStr(null, indexCond.getClassNodeText());
					
					if(containValue==null)
						containValue = IndexIdNameMapInfo.getInstance().getIdListStr(null, indexCond.getIndexName());
				}
				
				//把概念指数,行业指数=>指数 包含IDLIST
				if(containValue!=null) {
					indexCond.addProp(containOp, containValue, false);
					if(CONST_INDEX.equals(indexCond.getIndexName())==false && indexCond.getIndexName()!=null && indexCond.getIndexName().indexOf(CONST_INDEX)>0) {
						indexCond.setIndexName(CONST_INDEX);
					}
				}
			}
		}
	}
	
	/**
	 * 补全交易日期和交易时间
	 * 分钟线时间必须要有日期和时间
	 * @param conditionIndexModel
	 */
	private void completeDateAndTime(ConditionIndexModel conditionIndexModel) {
		boolean hasDate = false;
		boolean hasTime = false;

		for (String property : conditionIndexModel.getIndexProperties()) {
			if (property.contains("日期")) {
				hasDate = true;
			}
			if (property.contains("时间")) {
				hasTime = true;
			}
		}

		if (!hasDate) {
			DateInfoNode dateInfoNode = new DateInfoNode(Calendar.getInstance());
			String day;
			try {
				day = DateUtil.rollTradeDate(dateInfoNode, 0).toString();
				conditionIndexModel.addProp(DAY_TRADE_DAY, day);
			} catch (UnexpectedException e) {
				e.printStackTrace();
			}
		}
		if (!hasTime) {
			conditionIndexModel.addProp(TIME_RANGE_START, "9:30");
			conditionIndexModel.addProp(TIME_RANGE_END, "15:00");
		}

	}
	

	public final static String CONTAIN = "包含";
	public final static String CONST_INDEX = "指数";
	public final static String N_DAY = "n日";
	public final static String DAY_RANGE = "+区间";
	public final static String REPORTDATE = "报告期";
	public final static String YEAR_LEVEL = "年度";
	public final static String DAY_TIME = "交易时间";
	public final static String YEAR_SEASON = "!年度季度";
	public final static String END_DAY = "截止日期";
	public final static String DAY_RANGE_START = "起始交易日期";
	public final static String TIME_RANGE_START = "起始交易时间";
	public final static String DAY_RANGE_END = "截止交易日期";
	public final static String TIME_RANGE_END = "截止交易时间";
	public final static String DAY_TRADE_DAY = "交易日期";
	public final static String RANGEOFFSET = "区间偏移";
	public final static String OCCURTIME = "出现次数";

	protected final static Pattern VAR_OPER_PATTERN = Pattern
			.compile("^(\\$1)(&lt;|&lt;=|<|=|==|<=|>|>=|&gt;|&gt;=|不包含|包含)(\\$2)$");
	protected final static Pattern CMP_OPER_PATTERN = Pattern
			.compile("^(.*)(&lt;|&lt;=|<|=|==|<=|>|>=|&gt;|&gt;=|不包含|包含)(.*)$");
	protected final static Pattern ARITH_OPER_PATTERN = Pattern.compile("^(.*)(/|\\+|-|\\*)(.*)$");
	protected final static Pattern PERCENTAGE_PATTERN = Pattern.compile("^(.*)(%)$");
	protected final static Pattern CONST_1YIGU = Pattern.compile("^([0-9]{1,10})(亿股)$");
	protected final static Pattern CONST_1YI = Pattern.compile("^([0-9]{1,10})(亿)$");
	protected final static Pattern CONST_0WYuan = Pattern.compile("^([0-9]{1,10})(0)(万)(股)?$");
	protected final static Pattern CONST_NUMBER = Pattern.compile("^([0-9]{1,10})$");

	public SemanticNode createNodeByValueType(SemanticArgument arg, String defaultValue) {
		if (arg.isContainsValueType(ValueType.DATE)) {
			DateNode node = new DateNode();
			//TODO add date info
			return node;
		} else if (arg.isContainsValueType(ValueType.NUMBER, ValueType.DOUBLE_NUM, ValueType.LONG_NUM,
				ValueType.PERCENTAGE)) {
			NumNode numNode = new NumNode(defaultValue);
			String newValue = null;
			if (arg.isContainsValueType(ValueType.PERCENTAGE)) {
				Matcher m1 = PERCENTAGE_PATTERN.matcher(defaultValue);
				if (m1.matches()) {
					newValue = m1.group(1);
				}
			}

			if (newValue == null) {
				Matcher m = CONST_NUMBER.matcher(defaultValue);
				if (m.matches()) {
					newValue = m.group(1);
				}
			}

			if (newValue == null) {
				Matcher m = CONST_1YIGU.matcher(defaultValue);
				if (m.matches()) {
					String value = m.group(1);
					newValue = value + "00000000";
				}
			}

			if (newValue == null) {
				Matcher m = CONST_1YI.matcher(defaultValue);
				if (m.matches()) {
					String value = m.group(1);
					newValue = value + "00000000";
				}
			}

			if (newValue == null) {
				Matcher m = CONST_0WYuan.matcher(defaultValue);
				if (m.matches()) {
					String value = m.group(1);
					newValue = value + "0";
				}
			}

			if (newValue == null)
				newValue = "0";

			NumRange numinfo = new NumRange();
			numinfo.setFrom(newValue);
			numinfo.setTo(newValue);
			//numinfo.setUnit(unit)
			numNode.setNuminfo(numinfo);

			return numNode;
		} else if (arg.isContainsValueType(ValueType.STRING)) {
			StrNode strNode = new StrNode(defaultValue);
			return strNode;
		} else {
			return null;
		}
	}

	/**
	 * 暂时只处理百分比的节点
	 * @param arg
	 * @param node
	 * @return
	 */
	public SemanticNode removeUnit(SemanticArgument arg, SemanticNode node) {
		if (node.type == NodeType.NUM) {
			NumNode numNode = (NumNode) node;
			if (numNode.getUnit() == Unit.PERCENT) {
				NumRange numRange = numNode.getNuminfo();
				if (numRange != null) {
					Double from = numRange.getDoubleFrom();
					Double to = numRange.getDoubleTo();
					node.setText(String.valueOf(from / 100));
				}
			}
		}
		return node;
	}

	public String toString() {
		return this.getClass().getSimpleName() + "@" + this.hashCode();
	}

	//TODO 临时的函数,等陈宏，徐翔做好了实时计算,去掉这个方法
	private final static String JUN_XIAN = "均线";

	protected void modifyTechOpIndexAsIndex(ConditionIndexModel indexCond, FocusNode fNode, String domain) {
		List<ConditionIndexModel> indexConds = new ArrayList<ConditionIndexModel>(1);
		indexConds.add(indexCond);
		modifyTechOpIndexAsIndex(indexConds, fNode, domain);
	}

	protected void modifyTechOpIndexAsIndex(List<ConditionIndexModel> indexConds, FocusNode fNode, String domain) {
		if (indexConds == null || indexConds.size() == 0)
			return;

		if (fNode.hasIndex() == false || fNode.getIndex() == null)
			return;

		PropNodeFacade nDayProp = null;
		for (ConditionIndexModel index : indexConds) {
			if (index.getIndexName() != null && index.getIndexName().indexOf(JUN_XIAN) >= 0) {

				if (nDayProp == null) {
					ClassNodeFacade cNode = fNode.getIndex();
					List<PropNodeFacade> props = cNode.getAllProps();
					if (props != null) {
						for (PropNodeFacade pNode : props) {
							if (pNode.getValue() != null && pNode.getText().indexOf(N_DAY) >= 0) {
								nDayProp = pNode;
								break;
							}
						}
					}
				}

				if (nDayProp != null) {
					index.setIndexName(nDayProp.getValue().getText() + index.getIndexName(), domain);
				}
			}
		}
	}

}

