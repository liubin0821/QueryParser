package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myhexin.DB.mybatis.mode.CondUseNewapi;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.ResourceInst;
import com.myhexin.qparser.resource.model.RefCodeInfo;
import com.myhexin.qparser.time.info.TimeRange;
import com.myhexin.qparser.time.tool.TimeUtil;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;
import com.myhexin.qparser.util.condition.model.ConditionOpModel;

public class ConditionBuilderUtil {
	
	
	/**
	 * 合并股票代码condition
	 * 由于股票代码有可能会被拆分成多个condition
	 * 比如
	 * and
	 * 	|-股票代码，包含300033
	 *  and
	 *   |-股票代码，包含400004
	 *   |-市盈率
	 *   
	 * 合并成这样
	 * and
	 * 	|-股票代码，包含300033,400004
	 *  and
	 *   |-股票代码，包含300033,400004
	 *   |-市盈率
	 * 
	 * @param conds
	 */
	public static void mergeStkcodeCondition(List<ConditionModel> conds) {
		if(conds==null || conds.size()<=1) return;
		
		List<ConditionIndexModel> stkcodeIndexConds = new ArrayList<ConditionIndexModel>(1);
		StringBuilder buf = new StringBuilder();
		
		//找到所有的stkcCodeIndexCondition
		for(Iterator<ConditionModel> it = conds.iterator(); it.hasNext();) {
			ConditionModel cond = it.next();
			if(cond.getConditionType() == ConditionModel.TYPE_INDEX) {
				ConditionIndexModel indexCond = (ConditionIndexModel)cond;
				if(indexCond.getIndexName()!=null && indexCond.getIndexName().equals("股票代码")) {
					stkcodeIndexConds.add(indexCond);
					List<String> props = indexCond.getIndexProperties();
					if(props!=null && props.size()>0) {
						for(String str : props) {
							if(str.startsWith("包含")) {
								if(buf.length()==0)
									buf.append(str.substring(2));
								else{
									buf.append(',').append(str.substring(2));
								}
							}
						}
					}
				}
			}
		}
		
		if(stkcodeIndexConds.size()>1) {
			List<String> indexProps = new ArrayList<String>(1);
			String s = "包含" + buf.toString();
			indexProps.add(s);
			int i=0;
			for(ConditionIndexModel cond : stkcodeIndexConds) {
				cond.setIndexProperties(indexProps);
				if(i==0) {
					cond.setUIText(cond.getIndexName() + s);
				}else{
					cond.setUIText(null);
				}
				i++;
			}
		}
		
	}
	
	
	/*
	 * liuxiaofeng, 2015/3/27
	 * 如果是信息领域,并且是相关事件的内容,那么背绑定的STR_INSTANCE不要单独转成Condition
	 * 比如:习近平出访韩国
	 * 解析结果: 姓名=习近平, 国事访问(人物=习近平, 国家=韩国), 国家=韩国
	 * 
	 * 那么,姓名和国家这两个STR_INSTANCE就不要单独出现在CONDITION中了
	 * 
	 * ??好像这个会有隐患，以后处理信息领域的需求时可能会是个坑
	 */
	public static boolean isNewsDomainAndCanSkip(String domainStr, BoundaryNode bNode, BoundaryInfos boundaryInfos, List<SemanticNode> nodes) {
		if(Consts.CONST_absXinxiDomain.equals(domainStr)) {
   			String patternId = null;
   			if(bNode!=null) patternId = bNode.getSyntacticPatternId();
   			
   			List<String> filterWords = RefCodeInfo.getInstance().getFilterWords();
   			
   			boolean canSkip = false;
   			if(patternId==null || patternId.equals("STR_INSTANCE") ) {
   				for(int j=boundaryInfos.bStart+1;j<boundaryInfos.bEnd;j++) {
	   				SemanticNode node = nodes.get(j);
	   				if(node!=null && node.type!=NodeType.BOUNDARY && node.isBoundToIndex() ) {
	   					canSkip = true;
	   					break;
	   				}else if(node!=null && !filterWords.contains(node.getText())){
	   					//事件指标名字必须在filterWords中
	   					canSkip = true;
	   					break;
	   				}
	   			}
   			}else{
   				for(int j=boundaryInfos.bStart+1;j<boundaryInfos.bEnd;j++) {
   					//事件指标名字必须在filterWords中
   					SemanticNode node = nodes.get(j);
	   				if(node!=null && node.type!=NodeType.BOUNDARY && !filterWords.contains(node.getText())) {
	   					canSkip = true;
	   					break;
	   				}
   				}
   			}
   			
   			return canSkip;
   		}else{
   			return false;
   		}
	}

	// 取到StandardOutput
	public static String getStandardOutput(ArrayList<SemanticNode> nodes) {
		if (nodes != null && nodes.size() > 0 && nodes.get(0) != null
				&& nodes.get(0).getType() == NodeType.ENV) {
			Environment listEnv = (Environment) nodes.get(0);
			if (listEnv.getMultResult() != null) { return listEnv
					.getMultResult().getFirstOutput(); }
		}
		return null;
	}

	// 取到问句所属的领域
	public static String getDomainStr(ArrayList<SemanticNode> nodes) {
		String domain = null;
		if (nodes != null && nodes.size() > 0 && nodes.get(0) != null
				&& nodes.get(0).getType() == NodeType.ENV) {
			Environment listEnv = (Environment) nodes.get(0);
			if (listEnv.containsKey("listDomain")) {
				@SuppressWarnings("rawtypes")
				Map.Entry[] sortedListDomain = (Entry[]) listEnv.get(
						"listDomain", false);
				// List<String> domains = new ArrayList<String>();
				if (sortedListDomain != null && sortedListDomain.length > 0) {
					for (@SuppressWarnings("rawtypes")
					Map.Entry entry : sortedListDomain) {
						domain = entry.getKey() + "";
						if (domain != null && domain.length() > 0) break;
						// domains.add(entry.getKey()+"");
					}
				}
				// return domains;
			}
		}
		return domain;
	}
	
	/**
	 * 临时代码,新解析全面替代旧解析后将删除这段代码
	 * 1. op=行业sort
	 * 2. indexName=逐笔****
	 * 
	 * @param condList
	 * @param nodes
	 * @return
	 */
	public static boolean isNewQuery(List<ConditionModel> condList) {
		if(condList!=null && condList.size()>0) {
			for(ConditionModel cond : condList) {
				if(cond.getConditionType() == ConditionModel.TYPE_OP) {
					ConditionOpModel opModel = (ConditionOpModel)cond;
					if(opModel.getOpName()!=null && opModel.getOpName().equals("行业sort")) {
						return true;
					}
				}else if(cond.getConditionType() == ConditionModel.TYPE_INDEX) {
					ConditionIndexModel indexModel = (ConditionIndexModel)cond;
					if(indexModel.getIndexName()!=null && (indexModel.getIndexName().startsWith("一笔") || indexModel.getIndexName().startsWith("逐笔")) ) {
						return true;
					}
				}
				
				
				//				else if (cond.getConditionType() == ConditionModel.TYPE_INDEX) {
				//					ConditionIndexModel indexModel = (ConditionIndexModel) cond;
				//					for (String property : indexModel.getIndexProperties()) {
				//						//如果是分时指标,走新接口
				//						if (property.contains("分析周期")) {
				//							return true;
				//						}
				//					}
				//				}
			}
		}
		
		return false;
	}

	
	/*
	 * 遍历每个semanticNode, 看是不是要走新解析的指标
	 */
	public static boolean isZhishuQuery(ArrayList<ArrayList<SemanticNode>> qlist, int idx, String domain) {
		
		//先检查解析出来的领域是不是指数
		if(domain!=null && Consts.CONST_absZhishuDomain.equals(domain)) {
			return true;
		}
		
		
		//配置的开关,是不是所有都走新的接口
		Boolean isAllUseNewapi = ResourceInst.getInstance().isAllUseNewapi();
		if(isAllUseNewapi) {
			return true;
		}
		
		/*
		 * 因为我们要慢慢从老平台 迁移到新平台
		 * 所以有些不是指数领域的 问句，也要走指数领域的接口
		 * 下面这段代码就是干这个事情的, 根据指标判断是不是走 新的接口
		 * 
		 */
		if(qlist==null || qlist.size()<=0 || idx>=qlist.size()) {
			return false;
		}
		
		ArrayList<SemanticNode> nodes = qlist.get(idx);
		if(nodes!=null && nodes.size()>0) {
			List<String> indexNames = new ArrayList<String>();
			Iterator<BoundaryInfos> iterator = new SyntacticIteratorImpl(nodes);
			while (iterator.hasNext()) {
	    		BoundaryInfos boundaryInfos = iterator.next();
	    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
	    		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
	    		ArrayList<Integer> elelist;
	    		for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
	    			for (int pos : elelist) {
	    				SemanticNode defaultNode = null;
	    				if (pos == -1) {
	    					defaultNode = info.absentDefalutIndexMap.get(j);
	    				} else {
	    					//add by wyh 以绑定 不显示
	    					int index = boundaryInfos.bStart + pos;
	    					if (index>0 && index<nodes.size() && nodes.get(index)!=null && nodes.get(index).getType() == NodeType.FOCUS) {
	    						FocusNode fn = (FocusNode) nodes.get(index);
	    						if(fn.hasIndex() && (fn.isBoundToIndex() || fn.isBoundToSynt())) {
	    							defaultNode = fn;
	    						}
	    					}
	    				}
	    				if(defaultNode!=null && defaultNode.isFocusNode()) {
	    					FocusNode fNode = (FocusNode)defaultNode;
	    					if(indexNames.contains(fNode.getText())==false ) indexNames.add(fNode.getText());
	    					if(fNode.hasIndex() && fNode.getIndex()!=null) {
	    						if(indexNames.contains(fNode.getIndex().getText())==false )  indexNames.add(fNode.getIndex().getText());
	    					}
	    					ArrayList<FocusItem> focusItems = fNode.getFocusItemList();
	    					if(focusItems!=null && focusItems.size()>0) {
	    						for(FocusItem item : focusItems) {
	    							if(item.getIndex()!=null && item.getIndex().getText()!=null && (item.getIndex().isBoundToIndex() || item.getIndex().isBoundToSynt() || item.getIndex().isBoundValueToThis()) ) {
	    								if(indexNames.contains(item.getIndex().getText())==false )  indexNames.add(item.getIndex().getText());
	    							}
	    						}
	    					}
	    				}
	    			}
	    		}
			}
			
			
			for(SemanticNode node : nodes) {
				if(node.isFocusNode()) {
					FocusNode fNode= (FocusNode) node;
					if(indexNames.contains(fNode.getText())==false ) indexNames.add(fNode.getText());
					if(fNode.hasIndex() && fNode.getIndex()!=null) {
						if(indexNames.contains(fNode.getIndex().getText())==false )  indexNames.add(fNode.getIndex().getText());
					}
					ArrayList<FocusItem> focusItems = fNode.getFocusItemList();
					if(focusItems!=null && focusItems.size()>0) {
						for(FocusItem item : focusItems) {
							if(item.getIndex()!=null && item.getIndex().getText()!=null && (item.getIndex().isBoundToIndex() || item.getIndex().isBoundToSynt() || item.getIndex().isBoundValueToThis()) ) {
								if(indexNames.contains(item.getIndex().getText())==false )  indexNames.add(item.getIndex().getText());
							}
						}
					}
				}else if(node.isStrNode() || node.isIndexNode() || node.isClassNode()) {
					if(indexNames.contains(node.getText())==false )   indexNames.add(node.getText());
				}
			}
			
			List<CondUseNewapi> useNewapiInfos = ResourceInst.getInstance().getUseNewapiInfos();
			if(indexNames!=null && indexNames.size()>0 && useNewapiInfos!=null) {
				for(CondUseNewapi c : useNewapiInfos) {
					if(c.isZhishuQuery(indexNames)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/**判断是否是信息领域*/
	public static  boolean isEventQuery(ArrayList<ArrayList<SemanticNode>> qlist, int idx, String domain) {
		if(domain!=null && Consts.CONST_absXinxiDomain.equals(domain)) {
			return true;
		}
		return false;
	}
	
	

	/**
	 * 逐笔需求中把时间格式转成"092000000-150100000";
	 * 
	 * 
	 * @param node
	 * @return
	 */
	public static String getTimeValue(SemanticNode node){
		String value = null;
		if(node!=null && node.isDateNode()){
			DateNode dateNode = (DateNode) node;
			final String DEFAULT_TIME = "092000000-150100000";
			value = DEFAULT_TIME;
			if(dateNode != null){
				TimeNode timeNode = dateNode.getTime();
				if(timeNode!=null){
					TimeRange timeRange = dateNode.getTime().getRealRange();
					try{
						timeRange = TimeUtil.adjustTimeToTrade(timeRange);
					}catch(Exception e){
						//logger_.error(e.getMessage());
					}
					if(timeRange != null){
	    				String from = timeRange.getFrom().toString().replaceAll(":", "") + "000";
	    				String to = timeRange.getTo().toString().replaceAll(":", "") + "000";
	    				value = from+"-"+to;
					}
				}
			}
		}
		return value;
	}
	

	/**unicode解码*/
	public static String unicodeEsc2Unicode(String unicodeStr) {
		if (unicodeStr == null) {
			return null;
		}
		StringBuffer retBuf = new StringBuffer();
		int maxLoop = unicodeStr.length();
		for (int i = 0; i < maxLoop; i++) {
			if (unicodeStr.charAt(i) == '\\') {
				if ((i < maxLoop - 5)
						&& ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr
								.charAt(i + 1) == 'U')))
					try {
						retBuf.append((char) Integer.parseInt(
								unicodeStr.substring(i + 2, i + 6), 16));
						i += 5;
					} catch (NumberFormatException localNumberFormatException) {
						retBuf.append(unicodeStr.charAt(i));
					}
				else
					retBuf.append(unicodeStr.charAt(i));
			} else {
				retBuf.append(unicodeStr.charAt(i));
			}
		}
		return retBuf.toString();
	}
	
	
	/*private static String getChunkInfos_Index(ConditionIndexModel model) {
		StringBuilder buf = new StringBuilder();
		buf.append(model.getIndexName());
		String last = null;
		List<String> props = model.getIndexChunkProperties();
		if(props!=null && props.size()>0) {
			buf.append('[');
			for(String s: props) {
				if(s.equals(ConditionBuilderTechOpAsIndex.TECH_OP_CONTAIN)) {
					continue;
				}
				
				if(s.indexOf("(")>=0 || s.indexOf(">")>=0 || s.indexOf("<")>=0) {
					last = s;
					if (last.indexOf("(") >= 0) {
						last = last.replaceAll("[(]", ">");
					}
					continue;
				}
				buf.append(s);
			}
			buf.append(']');
		}
		if(last!=null) {
			buf.append(last);
		}
		return buf.toString();
	}*/
	
	/*public static List<String> getChunkInfos(List<ConditionModel> condList) {
		List<String> list = new ArrayList<String>();
		if(condList!=null) {
			for(int i=0;i<condList.size();i++) {
				ConditionModel model = condList.get(i);
				StringBuilder buf = new StringBuilder();
				String last = null;
				if(model.getConditionType() == ConditionModel.TYPE_INDEX) {	
					buf.append(getChunkInfos_Index((ConditionIndexModel) model));
				}else if(model.getConditionType() == ConditionModel.TYPE_OP) {
					ConditionOpModel index = (ConditionOpModel) model;
					if(index.getOpName().equals("and") ) continue;
					else if(index.getOpName().equals("sort") ) {
						if(last==null) last=Consts.STR_BLANK;
						last+=index.getOpName();
						if( index.getOpProperty()!=null) {
							last+=index.getOpProperty();
						}
					}else if((index.getOpName().equals("+") || index.getOpName().equals("-") || index.getOpName().equals("*") || index.getOpName().equals("/")) && index.getSonSize()==2) {
						ConditionModel model1 = null;
						ConditionModel model2 = null;
						if(i+1<condList.size()) {
							model1 = condList.get(i+1);
						}
						if(i+2<condList.size()) {
							model2 = condList.get(i+2);
						}
						if(model1!=null && model1 instanceof ConditionIndexModel)
							buf.append(getChunkInfos_Index((ConditionIndexModel) model1));
						
						buf.append(index.getOpName());
						
						if(model2!=null && model2 instanceof ConditionIndexModel)
							buf.append(getChunkInfos_Index((ConditionIndexModel) model2));
						
						if(index.getOpProperty()!=null) {
							buf.append(index.getOpProperty());
						}
						i+=2;
					}
				}
				if(last!=null) {
					buf.append(last);
				}
				list.add(buf.toString());
			}
		}
		return list;
	} */
	
	
	public static SemanticNode getNodeKeyValue(int indexId, BoundaryNode bNode, BoundaryInfos boundaryInfos, List<SemanticNode> nodes) {
		SemanticNode node = null;
		int newIndexId = bNode.extInfo.getElementNodePos(indexId);
    	if(newIndexId==-1 && bNode.extInfo!=null && bNode.extInfo.absentDefalutIndexMap!=null) {
    		node = bNode.extInfo.absentDefalutIndexMap.get(indexId);
    	}else{
    		indexId =boundaryInfos.bStart+ newIndexId;
    		if(indexId>=0 && indexId<nodes.size()) node = nodes.get(indexId);
    	}
    	return node;
	}
	
	public static SemanticNode getNodeFromFreeVar(List<SemanticNode> nodes, BoundaryInfos child_boundaryInfos) {
		BoundaryNode bNode = (BoundaryNode)nodes.get(child_boundaryInfos.bStart);
		if(bNode!=null && bNode.extInfo!=null) {
			SemanticNode node = null;
			int indexId = 1;
			int newIndexId = bNode.extInfo.getElementNodePos(indexId);
	    	if(newIndexId==-1 && bNode.extInfo!=null && bNode.extInfo.absentDefalutIndexMap!=null) {
	    		node = bNode.extInfo.absentDefalutIndexMap.get(indexId);
	    	}else{
	    		indexId =child_boundaryInfos.bStart+ newIndexId;
	    		if(indexId>=0 && indexId<nodes.size()) node = nodes.get(indexId);
	    	}
	    	return node;
		}
		return null;
	}
	public static SemanticNode getNodeOfStrInstPattern(List<SemanticNode> nodes, BoundaryInfos child_boundaryInfos) {
		BoundaryNode bNode = (BoundaryNode)nodes.get(child_boundaryInfos.bStart);
		if(bNode!=null && bNode.extInfo!=null) {
			SemanticNode node = null;
			int indexId = 1;
			int newIndexId = bNode.extInfo.getElementNodePos(indexId);
	    	if(newIndexId==-1 && bNode.extInfo!=null && bNode.extInfo.absentDefalutIndexMap!=null) {
	    		node = bNode.extInfo.absentDefalutIndexMap.get(indexId);
	    	}
	    	
	    	if(node==null) {
	    		node = bNode.extInfo.absentDefalutIndexMap.get(indexId);
	    	}
	    	
	    	return node;
		}
		return null;
	}
	
	private static Pattern techIndexPattern = Pattern.compile("(均线|macd|lwr|mtm|cci|rsi|wr|boll|vr|kdj|skdl|wr2值|wr1值|abs_线性指标|mamtm值|sar|阴线|阳线)");
	public static boolean isTechIndex(String text) {
		return techIndexPattern.matcher(text).find(); 
	}
	
	 //YEAR, HALF_YEAR, QUARTER, MONTH, FUTURE_QUARTER, DAILY, TRADE_DAILY, NATURAL_DAILY, FUTURE_DAILY, NOW
    //,FUTURE_YEAR,TIME
	/*public static Calendar getDateTimeByReportType(ClassNodeFacade cNode, Calendar backtestTime) {
		Calendar current = null; //Calendar.getInstance();
		if(backtestTime!=null) {
			current = backtestTime;
		}else{
			current = Calendar.getInstance();
		}
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH);
		int day = current.get(Calendar.DAY_OF_MONTH);
		
		ReportType type = cNode.getReportType();
		if(type==null ) {
			return current;
		}
		
		switch(type) {
		case YEAR: 
			year--;
			month =11;
			day=31;
			break;
		case HALF_YEAR: 
			if(month>5) {
				month=5;
				day=30;
			}else{
				year = year-1;
				month=11;
				day = day-31;
			}
			break;	
		case QUARTER: 
			month = (month/3)*3-1; //0,1,2=-1, 3,4,5=2,   6,7,8=5,   9,10,11=8
			if(month==-1) {
				year--;
				month=11;
				day=31;
			}else if(month==2) {
				day=31;
			}else if(month==5) {
				day=30;
			}else if(month==8) {
				day=30;
			}
			break;
		case MONTH: 
			if(month==0){//1月
				year--;
				month=11;
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
			break;
		case FUTURE_QUARTER: 
			month = (month/3)*3+2; //0,1,2=2, 3,4,5=5,   6,7,8=8,   9,10,11=11
			if(month==2) {
				day=31;
			}else if(month==5) {
				day=30;
			}else if(month==8) {
				day=30;
			}else if(month==11) {
				day=31;
			}
			break;
		case DAILY: 
			break;
		case TRADE_DAILY: 
			break;
		case NATURAL_DAILY: 
			break;
		default:
			break;
		}

		current.set(Calendar.YEAR, year);
		current.set(Calendar.MONTH, month);
		current.set(Calendar.DAY_OF_MONTH, day);
		return current;
	}*/
	
	/*public static void main(String[] args) {
		System.out.println(isTechIndex("5日均线"));
		System.out.println(isTechIndex("5日均线上移"));
		System.out.println(isTechIndex("macd金叉"));
		System.out.println(isTechIndex("kdj金叉"));
	}*/
	

	public static String createJson(List<ConditionModel> conds) {
		ConditionModel firstModel = null;
		for (ConditionModel cond : conds) {
			cond.checkAndSetProperties();

			if (firstModel == null)
				firstModel = cond;
		}

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return ConditionBuilderUtil.unicodeEsc2Unicode(gson.toJson(conds));
	}
	
	

	
	public static void createConditionHtml(StringBuilder buf, List<ConditionModel> conds, int index, int deep) {
		if(index<conds.size()) {
			ConditionModel cond = conds.get(index);
			if(cond.getConditionType() == ConditionModel.TYPE_INDEX) {
				ConditionIndexModel indexModel = (ConditionIndexModel) cond;
				for(int i=0;i<deep-1;i++) {
					buf.append("\t");
				}
				if(deep>0)
					buf.append("\\----");
				buf.append(indexModel.getIndexName()).append(" ");
				buf.append(indexModel.getIndexProperties());
				buf.append("\n");
			}else{
				ConditionOpModel op = (ConditionOpModel) cond;
				for(int i=0;i<deep-1;i++) {
					buf.append("\t");
				}
				if(deep>0)
					buf.append("\\----");
				buf.append(op.getOpName()).append("\n");
				if(op.getSonSize()>0) {
					int start = index+1;
					for(int i=0;i<op.getSonSize();i++){
						ConditionModel op1 = conds.get(start+i);
						if(op1.getConditionType()==ConditionModel.TYPE_INDEX)
							createConditionHtml(buf, conds, start+i, deep+1);
						else{
							createConditionHtml(buf, conds, start+i, deep+1);
							break;
						}
					}
					
					/*while(start<conds.size()) {
						int new_deep = deep+1;
						for(int i=0;i<op.getSonSize();i++)
							createConditionHtml(buf, conds, start+i, new_deep);
						
						start+=op.getSonSize();
					}*/
				}
			}
		}
	}
	
	public static String createConditionHtml(List<ConditionModel> conds) {
		StringBuilder buf = new StringBuilder();
		buf.append("\n");
		createConditionHtml(buf, conds, 0, 0);
		return buf.toString();
	}
	
	public static void addAndOp(List<ConditionModel> conds) {
		addAndOp(conds, false);
	}
	public static void addAndOp(List<ConditionModel> conds, boolean always) {
		if(conds.size()>0 ) {
			
			if(always) {
				ConditionOpModel andOp = new ConditionOpModel();
				andOp.setOpName("and");
				andOp.setSonSize(conds.size());
				conds.add(0,andOp);
			}else{
				ConditionModel firstCond = conds.get(0);
				if(firstCond.getConditionType()==ConditionModel.TYPE_INDEX) {
					ConditionOpModel andOp = new ConditionOpModel();
					andOp.setOpName("and");
					andOp.setSonSize(conds.size());
					conds.add(0,andOp);
				}
			}
		}
		
		/*List<ConditionModel> new_conds = new ArrayList<ConditionModel>();
		new_conds.addAll(conds);
		return new_conds;*/
	}
}
