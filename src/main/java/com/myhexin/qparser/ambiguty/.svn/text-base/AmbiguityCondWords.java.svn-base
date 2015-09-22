package com.myhexin.qparser.ambiguty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.DB.mybatis.mode.Aliases;
import com.myhexin.DB.mybatis.mode.ResolveAliasesConflicts;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.onto.ClassNodeFacade;

public class AmbiguityCondWords extends AmbiguityCondAbstract {

	/**
	 * init
	 */
	public AmbiguityCondWords() {
		super("words");
	}

	/**
	 * 匹配一个条件。
	 * 形如： ....,boundarynode1<start>,.......,boundarynode1<end>,n1,.....,boundarynode2<start>,....,n3,...,boundarynode2<end>,......,n2,boundarynode3<start>,.......
	 * 
	 * @param nodes	query node list
	 * @param start	前一个boundarynode的结束位置+1，即n1
	 * @param end	 当前非boundarynode的结束位置，即n2
	 * @param bStart	boundary start 当前boundarynode的起始位置，即boundarynode2<start>
	 * @param bEnd	boundary end 当前boundarynode的结束位置，即boundarynode2<end>
	 * @param cNode	可能的class node
	 * @param cPos 当前指标的位置，即n3
	 * @return int 返回匹配结果 －1 完全不合适，应该删除 0 － 100 正确的可能性，100为最大
	 */
	public double matchNode(ArrayList<SemanticNode> nodes, int start, int end, int bStart, int bEnd, ClassNodeFacade cNode, int cPos, String alias) {
		// 信号包括：
		// 1、消除歧义关键字是否至少匹配一个以上
		// 2、消除歧义关键字在boundary内外匹配上的个数
		// 3、消除歧义关键字匹配上的远近
		// System.out.println("type:"+type_);
		//如果句式之间用“和”或者“，和”之类的连接词(如n1是“和”)，则“和”之后的句式仍然需要计算分数，以便正确的消歧。
		ArrayList<SemanticNode> tempNodes = nodes;
		int andPos = checkAndLogic(nodes, start, bStart );
		if(andPos > 0) {
			tempNodes = new ArrayList<SemanticNode>();
			CollectionUtils.addAll(tempNodes, new Object[nodes.size()]); //生成一致的长度
			Collections.copy(tempNodes, nodes); //拷贝当前的nodes，进行和后面句式的分数计算操作
			removePreBoundaryNodeByAnd(tempNodes, andPos );
			int nodeOffset = nodes.size() - tempNodes.size();
			//修正指针的位置
			end -=nodeOffset;
			bStart -=nodeOffset;
			bEnd -=nodeOffset;
			cPos -=nodeOffset;
			start = repairStartPos(bStart, tempNodes);
		}
		
		double ret = 50;
		AmbiguityCondInfoWords condInfoWords = null;
		String stringArray[] = alias.split("\\|");
		for (String temp : stringArray) {
			condInfoWords = (AmbiguityCondInfoWords) getCondInfo(cNode, temp, type_);
			if (condInfoWords != null)
				break;
		}
		if (condInfoWords == null)
			return ret + 3.0;
		// System.out.println("words-cNode:" + cNode.text + condInfoWords.words_);
		int boundaryIn = 0; // 边界内有几个匹配上
		int boundaryOut = 0; // 边界外有几个匹配上
		int containNum = 0; // 列表中有几个匹配上
		double distances = 0;
		for (String word : condInfoWords.words_) {
			int boundaryInTemp = 0;
			int boundaryOutTemp = 0;
			double distancesTemp = 0;
			
			int inc = 0;
			int pos = cPos;
			while (true) {
				pos += inc;
				if (pos < start) {
					pos = cPos;
					inc = 1;
					continue;
				} else if (pos > end) {
					break;
				}
				SemanticNode sNode = tempNodes.get(pos);
				String text = sNode.getText(); // 处理时间节点的问题：根据单位
				if (sNode.type == NodeType.DATE) {
					try {
						DateNode dNode = (DateNode) sNode;
						TimeNode timeNode = dNode.getTime();
						text = (dNode).getUnitStrOfDate();
						if (dNode.isCombined || dNode.getDateinfo()==null )
							continue;
						if (dNode.getDateinfo().getLength() != 0 && dNode.getDateinfo().getLength() != 1
								&& dNode.isSequence == false && dNode.getFrequencyInfo() == null || timeNode != null
								&& timeNode.getHourDifference() > 0)
							text = "区间";
						if (dNode.getDateinfo().getFrom().isAfter(DateUtil.getNow()))
							text = "预测";
						if (dNode.getDateinfo().getFrom().equals(dNode.getDateinfo().getTo()) 
								&& dNode.getDateinfo().getFrom().equals(DateUtil.getNow())
								&& isTradeTimeNow()) {
							text = "最新";
						}
					} catch (UnexpectedException e) {
						text = sNode.getText();
					}
				}
				if (pos != cPos && text.contains(word)) {
					int distance = pos > cPos ? pos - cPos : cPos - pos;
					// System.out.println("i-cPos:" + i + "-" + cPos);
					if (pos < bStart || pos > bEnd)
						distance = distance - 1;
					distance = distance==0 ? 1 : distance;
					distancesTemp += 1.0 / distance;

					if (pos >= bStart && pos <= bEnd)
						boundaryInTemp++;
					else
						boundaryOutTemp++;
				}
				
				if (inc == 0) {
					pos = cPos;
					inc = -1;
					continue;
				} else if (inc == 1 && (sNode.isBoundToSynt || end != tempNodes.size()-1 && pos > bEnd && isSepWord(text))) {
                	break;
                } else if (inc == -1 && (sNode.isBoundToSynt || start != 0 && pos < bStart && isSepWord(text))) {
                	pos = cPos;
                	inc = 1;
                	continue;
                }
			}
			boundaryIn += boundaryInTemp;
			boundaryOut += boundaryOutTemp;
			distances += distancesTemp;
			if (boundaryInTemp + boundaryOutTemp > 0)
				containNum++;
		}
		int wordsSize = end - start - 1;
		int boundarySize = bEnd - bStart - 1;
		if (containNum > 0) // 只要匹配上一个，则有50分的基础分
			ret = 60;
		// 附加的分数
		double scoreAdd = 15.0 * containNum / wordsSize + 5.0* (boundaryIn + boundaryOut) / wordsSize + 10.0 * boundaryIn / (boundarySize);
		scoreAdd += condInfoWords.words_.size() > 0 ? 5.0 * containNum / condInfoWords.words_.size() : 3.0;
		scoreAdd += 15 * distances;
		// System.out.println("words score add:"+scoreAdd+"-"+10*distances);
		ret = ret + scoreAdd;
		return ret <= 100 ? ret : 100;
	}

	/**
	 * 修正上一个句式的结束位置。
	 * 
	 * @author huangmin
	 *
	 * @param bStart
	 * @param nodes
	 * @return
	 */
	private int repairStartPos(int bStart, ArrayList<SemanticNode> nodes) {
		int startPos = 0;
		for(int i = bStart; i>=0; i--) {
			SemanticNode snode = nodes.get(i);
			if(snode instanceof BoundaryNode && ((BoundaryNode)snode).isEnd()) {
				startPos = i + 1;
				break;
			}
		}
		
		return startPos;
	}

	/**
	 * 删除“和”连接的前面那个句式，以便将后面的句式作为前面的句式进行计算分数。
	 * 
	 * @author huangmin
	 *
	 * @param nodes
	 * @param andPos 
	 * 							和 的节点位置
	 */
	private void removePreBoundaryNodeByAnd(ArrayList<SemanticNode> nodes,
			int andPos) {
		for(int i = nodes.size() -1; i>=0; i--) {
			SemanticNode snode = nodes.get(i);
			if(i > andPos) { //和节点后面的节点一律保留
				continue;
			}else if(snode instanceof BoundaryNode && ((BoundaryNode)snode).isStart()) {
				nodes.remove(i);
				break;
			}else {
				nodes.remove(i);
			}
		}
	}

	/**
	 * 看看两个句式之间是否用“和”之类的连接词连接。
	 * 
	 * @author huangmin
	 *
	 * @param nodes
	 * @param start 
	 * 							起始位置，有可能是“和”的位置，也有可能"和"的位置介于start和bStart之间
	 * @param bStart 
	 * 							句式节点的起始位置
	 * @return
	 */
	private int checkAndLogic(ArrayList<SemanticNode> nodes, int start,
			int bStart) {
		int andPos = 0;
		if(nodes != null && nodes.size() > 0 && start > 1) {
			for(int i = start; i < bStart; i++) {
				SemanticNode snode = nodes.get(i);
				if("和".equals(snode.getText())) {
					andPos = i; //记录当前“和”节点的位置
				}
			}
		}
		
		return andPos;
	}

	/**
	 * parse config file and set config conditionInfo to ClassNodeFacade
	 */
	public void parseConfig(Aliases alias, ClassNodeFacade cn,List<?> list){
		AmbiguityCondInfoWords condInfo = new AmbiguityCondInfoWords();
		
		if(list == null) return;
		
		for (Object rac : list) {
			condInfo.words_.add(((ResolveAliasesConflicts)rac).getWord());
		}
		
		addCondInfo(cn, alias.getLabel(), type_, condInfo);
		return;
	}
	
	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-11-6 上午11:24:04
	 * @description:  为了兼容OntoXmlReaderOldSystem 	
	 */
	@Deprecated
	public void parseConfig(Node confNode, ClassNodeFacade cn, String alias) {
		AmbiguityCondInfoWords condInfo = new AmbiguityCondInfoWords();
		// confNode是 <ResolvingConflicts><word>累计</word></ResolvingConflicts>
		// 这样一个节点
		NodeList children = confNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (!node.getNodeName().equals("word")) {
				continue;
			}
			String str = node.getFirstChild().getNodeValue();
			condInfo.words_.add(str);
		}
		addCondInfo(cn, alias, type_, condInfo);
		return;
	}
	


	private boolean isTradeTimeNow() {
		Date now = new Date();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyyMMdd");
		String time1 = sdfdate.format(now) + "091500";
		String time2 = sdfdate.format(now) + "160000";
		cal1.setTime(now);

		try {
			cal2.setTime(sdf.parse(time1));
			cal3.setTime(sdf.parse(time2));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cal2.before(cal1) && cal3.after(cal1)) { // 在时间段内
			return true;
		}
		return false;
	}
}
