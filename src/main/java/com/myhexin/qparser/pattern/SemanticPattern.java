/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package com.myhexin.qparser.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.ParseLog;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.QuestType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.AvgNode;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.OperatorNode;
import com.myhexin.qparser.node.QuestNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.SortNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TechOpNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;

/**
 * the Class SemanticPattern
 * 首先，根据label所在位置，将Query节点转化为相应内部模板串。然后利用label的pattern替换列表（
 * 按优先级排列）顺序匹配，并进行替换。<br>
 * 例如：去年分红大于0.3元的股票。假设，label是“分红”。节点序列“去年 分红 大于0.3元 的 股票”，依据时间、
 * label、数字、停用词、忽略词的内部泛化规则依次转化，并合并成一个泛化串，最后利用相应pattern模板进行匹配替换。
 * 
 */
public class SemanticPattern {
    private static Pattern PTN_NODE_INDEX =
    		Pattern.compile("^[^\\d]+(\\d+)$");
    
    /** Pattern中两个节点之间的间隔符号*/
    private static final String STR_NODE_SEP = PatternRegexLoader.STR_NODE_SEP;
    private static final String STR_ITEM_SEP = PatternRegexLoader.STR_ITEM_SEP;
    private static HashMap<String, ArrayList<PatternInfo>> 
    		STOCK_LABEL_PATTERNS_MAP = new HashMap<String, ArrayList<PatternInfo>>();
    private static HashMap<String, ArrayList<PatternInfo>> 
            FUND_LABEL_PATTERNS_MAP = new HashMap<String, ArrayList<PatternInfo>>();
    
    private Query query_; 
    private static HashMap<String, ArrayList<PatternInfo>> label2Patterns = null;
    
    public static void setLabel2PatternInfo(HashMap<String, ArrayList<PatternInfo>> info,
            Query.Type qtype) {
        if(qtype == Query.Type.STOCK) {
            STOCK_LABEL_PATTERNS_MAP = info;
        } else {
            FUND_LABEL_PATTERNS_MAP = info;
        }
    }
    
    public SemanticPattern(Query query) {
        this.query_ = query;
        if(query_.getType() == Query.Type.STOCK) {
            label2Patterns = STOCK_LABEL_PATTERNS_MAP;
        } else {
            label2Patterns = FUND_LABEL_PATTERNS_MAP;
        }
    }
    
    
    /**
     * 对Query进行Pattern匹配处理。分两类Pattern替换，分别是REP_LABEL和REP_ALL。
     * REP_LABEL只替换Label（触发词节点），REP_ALL则将匹配的Pattern全部进行替换。
     * 替换时，都采用最长匹配原则进行，即节点多的Pattern优先匹配。<br>
     * 采用词语文本触发方式进行，匹配时先进行Query的泛化，然后再利用预先编译还的Pattern进行正则匹配。<br>
     * 注意：pattern的label采用节点的字符串，而不管节点类型。替换时，pattern越长优先级越高；相同长度的pattern，信息
     * 越详尽越优先匹配；长度相同、信息详尽程度相同的，越后写的优先级越高
     * 
     * @param query
     * @throws DataConfException 
     * @throws UnexpectedException 
     */
    public void transByPattern() throws UnexpectedException{
    	for(int index =0; index < query_.getNodes().size(); ){
    		String label = query_.getNodes().get(index).getText();
    		ArrayList<PatternInfo> ptnInfos = label2Patterns.get(label);
    		
    		int nextIndex = transByPattern(query_.getNodes(), index, ptnInfos);
    		if(nextIndex >= 0){
    			index = nextIndex;
    		}else{
    			index++;
    		}
    	}
    }

	/**
	 * 对节点index触发的Pattern进行匹配替换。首先获得Query泛化的
	 * ， 然后依次利用Pattern进行匹配处理
	 * @param nodes 用户Query节点
	 * @param index 当前Label位置
	 * @param ptnInfos label的Pattern
	 * @return 下一个应该匹配的节点位置，如果匹配失败则为-1
	 * @throws UnexpectedException 
	 */
	private int transByPattern(ArrayList<SemanticNode> nodes, int index,
			ArrayList<PatternInfo> ptnInfos) throws UnexpectedException {
		if(ptnInfos == null || ptnInfos.size() == 0){
			return -1;
		}
		
		String queryPatternText = getQueryPatternText(nodes, index);
		
		if(queryPatternText == null || queryPatternText.equals("")){
			return -1;
		}
		
		int nextIndex = -1;
		for(PatternInfo ptnInfo : ptnInfos){
			Matcher m ;
			
			if(!(m = ptnInfo.matcher(queryPatternText)).find()){
				continue ;
			}
			
			query_.getLog().logMsg(ParseLog.LOG_PATTERN, ptnInfo+";\n");
			
			int count = ptnInfo.getPatSize();
			assert(count == m.groupCount());
			ArrayList<SemanticNode> newNodes = new ArrayList<SemanticNode>();
			
			//分两类匹配方式进行匹配
			if(ptnInfo.isRepAll()){//全部替换
				int startGroup = 1;
				int endGroup = count;
				int startIndex = -1;
				int endIndex = -1;
				//得到被替换节点序列的起止下标位置
				for(int idx = startGroup; idx <= endGroup; idx++){
					String nodeInfo = m.group(idx);
					if(nodeInfo != null){
						Matcher midx = PTN_NODE_INDEX.matcher(nodeInfo.split(STR_ITEM_SEP)[0]);
						if(!midx.matches()) { assert(false); }
						startIndex = Integer.parseInt(midx.group(1));
						if(startIndex >= 1000){
							startIndex = 0;
						}
						break ;
					}
				}
				for(int idx = endGroup; idx >= startGroup; idx--){
					String nodeInfo = m.group(idx);
					if(nodeInfo != null){
						Matcher midx = PTN_NODE_INDEX.matcher(nodeInfo.split(STR_ITEM_SEP)[0]);
	                    if(!midx.matches()) { assert(false); }
						endIndex = Integer.parseInt(midx.group(1));
						if(endIndex >= 1000){
							endIndex = nodes.size()-1;
						}
						break ;
					}
				}
				if(endIndex == -1 || startIndex==-1){
					continue ; 
				}
				//ArrayList<SemanticNode> list = ptnInfo.getRepInfos() ;
				for(SemanticNode repNode : ptnInfo.getRepInfos()){
					if(repNode.type == NodeType.CHANGE_NUM_NODE){
						ChangeNumNode tmpNode = (ChangeNumNode)(repNode);
						String nodeInfo = m.group(tmpNode.numNodePosition);
						if(nodeInfo == null){
							continue ;
						}
						Matcher midx = PTN_NODE_INDEX.matcher(nodeInfo.split(STR_ITEM_SEP)[0]);
	                      if(!midx.matches()) { assert(false); }
						int numNodePos = Integer.parseInt(midx.group(1));
						assert(nodes.get(numNodePos).type == NodeType.NUM);
						NumNode numNode = (NumNode)nodes.get(numNodePos);
						changeNumNode(numNode, tmpNode);
						//将改变后的数字节点加入到新的节点序列中，因为原来的数字节点会被替换掉
						newNodes.add(numNode);
					}else if(repNode.type == NodeType.CHANG_DEF){
						ChangeDefNode changeDefNode = (ChangeDefNode)repNode ;
						// change
						int changeGroupIndex = changeDefNode.changeGroupIndex ;
						SemanticNode sn = nodes.get(startIndex + changeGroupIndex -1) ;
						assert(sn.type == NodeType.CHANGE) ;
						ChangeNode changeNode = (ChangeNode)NodeUtil.copyNode(sn); //.copy() ;
						assert(changeNode != null) ;
						
						// change text
						StringBuilder textBuilder = new StringBuilder() ;
						for(int idx : changeDefNode.textIndexArr){
							textBuilder.append(nodes.get(startIndex + idx -1).getText()) ;
						}
						changeNode.setText(textBuilder.toString()) ;
						
						//defClass
						changeNode.defClass_ = changeDefNode.defClass_ ;
						newNodes.add(changeNode) ;
					}else if(repNode.type == NodeType.COPY_NODE){
						CopyNode tmpNode = (CopyNode)(repNode);
						//复制节点，即将原来某个节点复制到新节点序列中
						String nodeInfo = m.group(tmpNode.groupIndex);
						if(nodeInfo == null){
							continue ;
						}
						
						Matcher midx = PTN_NODE_INDEX.matcher(nodeInfo.split(STR_ITEM_SEP)[0]);
	                    if(!midx.matches()) { assert(false); }
						int copyPos = Integer.parseInt(midx.group(1));
						//因为添加了一个start和end，下标为1000，如果匹配到添加的节点，则不需要拷贝
						if(copyPos >= 1000){
							continue ;
						}
						//将原来节点复制到新节点序列中
						SemanticNode nodeToCopy = nodes.get(copyPos);
						newNodes.add(NodeUtil.copyNode(nodeToCopy));
						/*if(nodeToCopy instanceof NumNode || nodeToCopy instanceof DateNode) {
						    newNodes.add(nodeToCopy); //.copy()
						} else {
						    newNodes.add(nodeToCopy);
						}*/
					}else{
						newNodes.add(repNode);
					}
				}
				//加入新节点序列
				nodes.addAll(startIndex, newNodes);
				//删除原节点序列
				for(int j = endIndex; j >= startIndex; j--){
					nodes.remove(newNodes.size() + j);
				}
				//获得下一个匹配位置
				nextIndex = startIndex + newNodes.size();
			}else{
				for(SemanticNode repNode : ptnInfo.getRepInfos()){
					if(repNode.type == NodeType.CHANGE_NUM_NODE){
						ChangeNumNode changeNumNode = (ChangeNumNode)(repNode);
						String nodeInfo = m.group(changeNumNode.numNodePosition);
						if(nodeInfo == null){
							continue ;
						}
						
						Matcher midx = PTN_NODE_INDEX.matcher(nodeInfo.split(STR_ITEM_SEP)[0]);
						if(!midx.matches()){ assert(false); }
						int numNodePos = Integer.parseInt(midx.group(1));
						assert(nodes.get(numNodePos).type == NodeType.NUM);
		
						NumNode numNode = (NumNode)nodes.get(numNodePos);
						//只是改变数字大小，而不添加到新的节点序列里面，因为源节点还在
						changeNumNode(numNode, changeNumNode);
					}else if(repNode.type == NodeType.COPY_NODE){
						CopyNode tmpNode = (CopyNode)(repNode);
						//复制节点，即将原来某个节点复制到新节点序列中
						String nodeInfo = m.group(tmpNode.groupIndex);
						if(nodeInfo == null){
							continue ;
						}
						
						Matcher midx = PTN_NODE_INDEX.matcher(nodeInfo.split(STR_ITEM_SEP)[0]);
						if(!midx.matches()){ assert(false); }
						int copyPos = Integer.parseInt(midx.group(1));
						//因为添加了一个start和end，下标为1000，如果匹配到添加的节点，则不需要拷贝
						if(copyPos >= 1000){
							continue ;
						}
						//将原来节点复制到新节点序列中
						newNodes.add(nodes.get(copyPos));
					}else{
						newNodes.add(repNode);
					}
				}
				//添加新节点序列
				nodes.addAll(index, newNodes);
				//删除Label
				nodes.remove(index + newNodes.size());
				//返回下一个Label位置
				nextIndex = index + newNodes.size();
			}
			return nextIndex;
		}
		return -1;
	}
	
	/**
	 * @param numNode
	 * @param changeNumNode
	 */
	private static void changeNumNode(NumNode numNode, ChangeNumNode changeNumNode) {
		double from = numNode.getFrom();
		double to = numNode.getTo();
		
		switch(changeNumNode.operatorType){
		case SUBTRACT:
			if(changeNumNode.isPassive){
				double tmpTo = to;
				if(from > NumRange.MIN_){
					to = changeNumNode.number - from;
				}else{
					to = NumRange.MAX_;
				}
				if(tmpTo < NumRange.MAX_){
					from = changeNumNode.number - tmpTo;
				}else{
					from = NumRange.MIN_;
				}
			}else{
				if(from > NumRange.MIN_){
					from -= changeNumNode.number;
				}
				if(to < NumRange.MAX_){
					to -= changeNumNode.number;
				}
			}
			break;
		case ADD:
			if(from > NumRange.MIN_){
				from += changeNumNode.number;
			}
			if(to < NumRange.MAX_){
				to += changeNumNode.number;
			}
			break;
		case MULTIPLY:
			if(changeNumNode.number < 0){
				double tmp = from;
				from = to;
				to = tmp;
			}
			if(from > NumRange.MIN_){
				from *= changeNumNode.number;
			}
			if(to < NumRange.MAX_){
				to *= changeNumNode.number;
			}
			break;
		case DIVIDE:
			if(changeNumNode.isPassive){
				double tmpTo = to;
				if(from > NumRange.MIN_ && from !=0){
					to = changeNumNode.number/from;
				}else{
					to = NumRange.MAX_;
				}
				if(tmpTo < NumRange.MAX_ && tmpTo != 0){
					from = changeNumNode.number/tmpTo;
				}else{
					from = NumRange.MIN_;
				}
				if(from > to){
					tmpTo =to;
					to =from;
					from = tmpTo;
				}
			}else{
				if(changeNumNode.number < 0){
					double tmp = from;
					from = to;
					to = tmp;
				}
				if(from > NumRange.MIN_){
					from /= changeNumNode.number;
				}
				if(to < NumRange.MAX_){
					to /= changeNumNode.number;
				}
			}
			break;
		}
		numNode.getNuminfo().setNumRange(from, to);
	}


	/**
	 * 获取Query节点index处的泛化形式，注意与匹配Pattern正则式保持一致，即要求与
	 * {@link PatternRegexLoader#makePatternInfo(String[], PatternInfo, Query.Type)}保持一致。<br>
     * 另一点，如果想增强pattern的处理功能，譬如支持新的节点或对于某类节点匹配更多信息，则
     * 要求pattern资源文件、资源文件导入、Query的泛化三者同步进行！
	 * @param nodes Query节点
	 * @param index 触发词位置
	 * @return 泛化后的形式
	 * @throws UnexpectedException 
	 */
	public String getQueryPatternText(ArrayList<SemanticNode> nodes, int index) throws UnexpectedException {
		StringBuilder text = new StringBuilder(100) ;
		text.append(STR_NODE_SEP + "STR1000" + STR_ITEM_SEP + "START");
		
		//每个可以泛化的节点，前面三项依次是：节点标志、(节点类型、位置)。后面几项根据不同节点而不同，
		//但要求与Pattern保持一致。
		for(int j = 0; j < nodes.size(); j++){
			if(j == index){
				text.append(STR_NODE_SEP + "LABEL" + j) ;
			}else{
				SemanticNode nodeJ = nodes.get(j);
				switch(nodeJ.getType() ){
				case CLASS:
					ClassNodeFacade ClassNodeFacade = (ClassNodeFacade)nodeJ;
					if(ClassNodeFacade.isFake()) {
						text.append(STR_NODE_SEP + "USERINDEX" +j + 
								STR_ITEM_SEP + nodeJ.getText() + STR_ITEM_SEP) ;
						break ;
					} else {
						text.append(STR_NODE_SEP + "INDEX" +j + 
								STR_ITEM_SEP + nodeJ.getText() + STR_ITEM_SEP) ;
						Unit indexUnit = Unit.UNKNOWN;
						if(ClassNodeFacade.isNumIndex()){
							text.append("N") ;
							indexUnit =ClassNodeFacade.getValueUnit();
							if(indexUnit == null){
								indexUnit = Unit.UNKNOWN;
							}
						}else if(ClassNodeFacade.isDateIndex()){
							text.append("D") ;
						}else if(ClassNodeFacade.getProp(PropType.STR) != null){
							text.append("S") ;
						}else if(ClassNodeFacade.isBoolIndex()){
							text.append("B") ;
						}else{
							text.append("_") ;
						}
						
						switch(indexUnit){
						case YUAN: text.append(STR_ITEM_SEP + "元");break;
						case HKD: text.append(STR_ITEM_SEP + "港元");break;
						case USD: text.append(STR_ITEM_SEP + "美元");break;
						case BEI: text.append(STR_ITEM_SEP + "倍");break;
						case PERCENT: text.append(STR_ITEM_SEP + "%");break;
						case GU: text.append(STR_ITEM_SEP + "股");break;
						case SHOU: text.append(STR_ITEM_SEP + "手");break;
						case ZHI: text.append(STR_ITEM_SEP + "只");break;
						case JIA: text.append(STR_ITEM_SEP + "家");break;
						case HU: text.append(STR_ITEM_SEP + "户");break;
						case GE: text.append(STR_ITEM_SEP + "个");break;
						case WEI: text.append(STR_ITEM_SEP + "位");break;
						default:
							text.append(STR_ITEM_SEP + "无") ;
						}
						break;
					}
				case PROP:
					text.append(STR_NODE_SEP + "PROP" + j + 
							STR_ITEM_SEP + nodeJ.getText() + STR_ITEM_SEP) ;
					PropNodeFacade PropNodeFacade = (PropNodeFacade)nodeJ;
					if(PropNodeFacade.isNumProp()){
						text.append("N") ;
					}else if(PropNodeFacade.isDateProp()){
						text.append("D") ;
					}else if(PropNodeFacade.isStrProp()){
						text.append("S") ;
					}else if(PropNodeFacade.isBoolProp()){
						text.append("B") ;
					}else{
						text.append("_") ;
					}
					break;
				case NUM:
					text.append(  STR_NODE_SEP + "NUM" + j) ;
					NumNode numNode = (NumNode)nodeJ;
					Unit numUnit = numNode.getUnit();
					text.append(STR_ITEM_SEP + numNode.getText() 
							  + STR_ITEM_SEP + numUnit) ;
					break;
				case DATE:
					DateNode dateNode = (DateNode)nodeJ;
					Unit tmpUnit = Unit.UNKNOWN;
					String dateUnit = "U";
					String dateSequence = "N";
					String dateCycle = "S";
					String dateFrequence = "N" ;
					try {
						tmpUnit = dateNode.getUnitOfDate();
						switch(tmpUnit){
						case YEAR:dateUnit="Y";break;
						case QUARTER:dateUnit="Q";break;
						case MONTH:dateUnit="M";break;
						case WEEK:dateUnit="W";break;
						case DAY:dateUnit="D";break;
						default: break;
						}
					} catch (UnexpectedException e) {
					}
					if(dateNode.isSequence){
						dateSequence = "S";
					}
					if(dateNode.hasSeveralDateCycle()){
						dateCycle = "M";
					}
					if(dateNode.getFrequencyInfo() != null){
						dateFrequence = "F" ;
					}
					
					text.append(STR_NODE_SEP + "DATE" + j
							+ STR_ITEM_SEP + dateNode.getText()
							+ STR_ITEM_SEP + dateUnit 
							+ STR_ITEM_SEP + dateSequence
							+ STR_ITEM_SEP + dateCycle
							+ STR_ITEM_SEP + dateFrequence) ;
					break;
				case SORT:
					SortNode sortNode = (SortNode)nodeJ ;
					String sortType = "降" ;  //默认为“降”
					if(sortNode.isDescending_()){
						sortType = "升" ;
					}
					text.append(STR_NODE_SEP + "SORT"  + j 
							+ STR_ITEM_SEP + sortNode.getText()
							+ STR_ITEM_SEP + sortType) ;
					break;
				case LOGIC:
					LogicNode logicNode = (LogicNode)nodeJ ;
					String logicType = "与" ;    //默认为“与”
					if(logicNode.logicType == LogicType.OR){
						logicType = "或" ; 
					}
					text.append(STR_NODE_SEP + "LOGIC"  + j 
							+ STR_ITEM_SEP + logicNode.getText()
							+ STR_ITEM_SEP + logicType) ;
					break;
				case OPERATOR:
					OperatorNode operNode = (OperatorNode)nodeJ;
					text.append(STR_NODE_SEP + "OPERATOR" + j
							+ STR_ITEM_SEP + operNode.getText()) ;
					switch(operNode.operatorType){
					case ADD: text.append(STR_ITEM_SEP + "加") ; break;
					case SUBTRACT: text.append(STR_ITEM_SEP + "减") ; break;
					case MULTIPLY: text.append(STR_ITEM_SEP + "乘") ; break;
					case DIVIDE: text.append(STR_ITEM_SEP + "除") ; break;
					case RATE: text.append(STR_ITEM_SEP + "比") ; break;
					}
					break;
				case QWORD:
					QuestNode questNode = (QuestNode)nodeJ;
					text.append(STR_NODE_SEP + "QWORD" + j
							+ STR_ITEM_SEP + questNode.getText()) ;
					switch(questNode.questType){
					case HOW_MUCH : text.append(STR_ITEM_SEP + "价")  ; break ;
					case WHAT : text.append(STR_ITEM_SEP + "什么")  ; break ;
					case WHO : text.append(STR_ITEM_SEP + "谁")  ; break ;
					case WHERE : text.append(STR_ITEM_SEP + "哪")  ; break ;
					case WHEN : text.append(STR_ITEM_SEP + "何时")  ; break ;
					case WHICH : text.append(STR_ITEM_SEP + "哪个")  ; break ;
					default : text.append(STR_ITEM_SEP + "UN")  ; break ;
					}
					break;
				case AVG:
					AvgNode avgNode = (AvgNode)nodeJ ;
					String avgText = avgNode.getText() ; 
					text.append(STR_NODE_SEP + "AVG" + j 
							  + STR_ITEM_SEP + avgText) ;
					break;
				case TECHOPERATOR:
					TechOpNode techopNode = (TechOpNode)nodeJ ;
					String techopText = techopNode.getText() ;
					text.append(STR_NODE_SEP + "TECHOP" + j
							  + STR_ITEM_SEP + techopText) ;
					break;
				case STR_VAL:
					StrNode strNode = (StrNode)nodeJ;
					String ofwhat = "无";
					//测试无误后删除 wyh 20114.11.
					/*if(strNode.ofWhat != null && strNode.ofWhat.size() >= 1){
						ofwhat = "";
						for(SemanticNode node : strNode.ofWhat){
							ofwhat += node.text;
						}
					}*/
					text.append(STR_NODE_SEP + "STR" + j
							+ STR_ITEM_SEP + nodeJ.getText() + STR_ITEM_SEP + ofwhat) ;
					break;
				case TECH_PERIOD :
					TechPeriodNode techperiodNode = (TechPeriodNode)nodeJ ;
					text.append(STR_NODE_SEP + "TECHPERIOD" + j) ;
					switch(techperiodNode.getPeriodType()){
					// DAY|WEEK|MONTH|YEAR|MIN_1|MIN_5|MIN_15|MIN_30|MIN_60
					case DAY: text.append(STR_ITEM_SEP + "D") ; break;
					case WEEK: text.append(STR_ITEM_SEP + "W") ; break;
					case MONTH: text.append(STR_ITEM_SEP + "M") ; break;
					case YEAR: text.append(STR_ITEM_SEP + "Y") ; break;
					case MIN_1: text.append(STR_ITEM_SEP + "m1") ; break;
					case MIN_5: text.append(STR_ITEM_SEP + "m5") ; break;
					case MIN_15: text.append(STR_ITEM_SEP + "m15") ; break;
					case MIN_30: text.append(STR_ITEM_SEP + "m30") ; break;
					case MIN_60: text.append(STR_ITEM_SEP + "m60") ; break;
					}
					break ;
				case CHANGE :
					ChangeNode changNode = (ChangeNode)nodeJ ;
					String changeText = changNode.getText() ;
					text.append(STR_NODE_SEP + "CHANGE" + j
							  + STR_ITEM_SEP + changeText) ;
					break ;
				default:
					//只要词语在pattern就一定需要匹配
					if(/*SpecialWords.hasWord(nodeJ.text, SpecialWordType.PATTERN_WORD) 
							|| */nodeJ.getText().length() > 1 && !SpecialWords.hasWord(nodeJ.getText(),
	                        SpecialWordType.TB_COMMON_SKIP)){
						text.append(STR_NODE_SEP + "STR" + j
								+ STR_ITEM_SEP + nodeJ.getText() + STR_ITEM_SEP + "无") ;
					}
					break;
				}
			}
		}
		text.append(STR_NODE_SEP + "STR1000" + STR_ITEM_SEP + "END") ;
		return text.toString();
	}
}
