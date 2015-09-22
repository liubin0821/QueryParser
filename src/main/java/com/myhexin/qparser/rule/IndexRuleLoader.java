package com.myhexin.qparser.rule;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.util.Pair;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 载入指标rule转换
 */
public class IndexRuleLoader {
	public static HashMap<String, NumRange> indexNumRange = new HashMap<String, NumRange>();
	/**
	 * 这里有载入两类指标转换数据，同时将转换条件添加到SpecialWords中。两类转换情况如下：<br>
	 * 一、指标遇到特定文本字符进行的转换，例如：董事长遇到曾经转换为董事长历任，净利润遇到“复合”转为净利润复合涨涨率
	 * 二、数值指标遇到特定范围进行的转换，例如：净资产在0到100范围的转为每股净资产，股价在1亿以上的转为总市值等
	 * 目前判断的依据是，index-condition是否包含数值，这里还需要改写，配成明确指定的转换类型。TODO：明确指定转换类别
	 * @param doc
	 * @throws DataConfException
	 * @throws UnexpectedException
	 */
	public static void loadStockRuleData(Document doc) throws DataConfException, UnexpectedException{
		IndexRuleProcessor.setStockRule(loadIndexRuleData(doc, Query.Type.STOCK));
	}
	
	/**
	 * @param doc
	 * @throws DataConfException
	 * @throws UnexpectedException
	 */
	public static void loadFundRuleData(Document doc) throws DataConfException, UnexpectedException{
		IndexRuleProcessor.setFundRule(loadIndexRuleData(doc, Query.Type.FUND));
	}
	
	/**
	 * 这里有载入两类指标转换数据。
	 * 一、指标遇到特定文本字符进行的转换，例如：董事长遇到曾经转换为董事长历任，净利润遇到“复合”转为净利润复合涨涨率
	 * 二、数值指标遇到特定范围进行的转换，例如：净资产在0到100范围的转为每股净资产，股价在1亿以上的转为总市值等
	 * 目前判断的依据是，index-condition是否包含数值，这里可以改写，配成指定转换类型。TODO：明确指定转换类别
	 * 
	 * @param doc
	 * @param qType
	 * @return rule
	 * @throws DataConfException
	 * @throws UnexpectedException
	 */
	public static Map<String, Map<String, SemanticNode>> loadIndexRuleData(Document doc, Query.Type qType)
			throws DataConfException, UnexpectedException {
		Map<String, Map<String, SemanticNode>> indexTranRuleMap = 
				new HashMap<String, Map<String, SemanticNode>>();
		Map<String, EnumSet<SpecialWordType>> ruleSecialWords = 
				new HashMap<String,  EnumSet<SpecialWordType>>(); 
		Map<String, Pair<NumRange, ClassNodeFacade>> transByValRule =
				new HashMap<String, Pair<NumRange, ClassNodeFacade>>();
	    Pattern numValPat = Pattern.compile(".*\\d+.*");
		Element root = doc.getDocumentElement();
		NodeList ruleItems = root.getChildNodes();
		for (int rdx = 1; rdx < ruleItems.getLength(); rdx++) {
			Node ruleItem = ruleItems.item(rdx);
			NodeList ruleInfos = ruleItem.getChildNodes();
			String fromIndex = null;
			HashMap<String, SemanticNode> changeMap = new HashMap<String, SemanticNode>();
			Pair<NumRange, ClassNodeFacade> pair= null;
			for (int idx = 1; idx < ruleInfos.getLength(); idx++) {
				Node ruleInfo = ruleInfos.item(idx);
				String name = ruleInfo.getNodeName();
				if (name.equals("index-from")) {
					fromIndex = ruleInfo.getFirstChild().getNodeValue();
				} else if (name.equals("index-change")) {
					NodeList pairList = ruleInfo.getChildNodes();
					String toIndex = null;
					String codition = null;
					for (int pdx = 1; pdx < pairList.getLength(); pdx++) {
						Node tmp = pairList.item(pdx);
						String tmpName = tmp.getNodeName();
						if (tmpName.equals("index-condition")) {
							codition = tmp.getFirstChild().getNodeValue();
							if(numValPat.matcher(codition).matches()){
								NumRange numRange;
                                try {
                                    numRange = NumParser.getNumRangeFromStr(codition);
                                } catch (NotSupportedException e) {
                                    throw new IllegalStateException();
                                }
								pair = new Pair<NumRange, ClassNodeFacade>(numRange, null);
								pair.first = numRange;
								continue ;
							}
							ruleSecialWords.put(codition, EnumSet.of(SpecialWordType.RULE_WORD));
						} else if (tmpName.equals("index-to")) {
							toIndex = tmp.getFirstChild().getNodeValue();
						}
					}
					if(codition == null || toIndex == null){
						continue ;
					}
					
					if(pair != null){
						Collection collection = MemOnto.getOntoIndex(toIndex, ClassNodeFacade.class, qType);
						if (collection != null && collection.isEmpty() == false) {
							Iterator iterator = collection.iterator();
							while(iterator.hasNext()) {
								ClassNodeFacade cn = (ClassNodeFacade) iterator.next();
								if (cn != null) {
									indexNumRange.put(toIndex, pair.first);
								}
								//pair.second = MemOnto.getOntoIndex(toIndex, ClassNodeFacade.class, qType);
							}
						}
					}else{
						//changeMap.put(codition, MemOnto.getOnto(toIndex, SemanticNode.class, qType));
					}
				}
			}
			if(fromIndex== null){
				continue ;
			}
			
			Map<String, SemanticNode> fromIndexChangeMap = indexTranRuleMap.get(fromIndex);
			if(changeMap.size() > 0){
				if(fromIndexChangeMap == null){
					indexTranRuleMap.put(fromIndex, changeMap);
				}else{
					fromIndexChangeMap.putAll(changeMap);
				}
			}
			if(pair != null){
				transByValRule.put(fromIndex, pair);
			}
		}
		
		if(qType == Type.STOCK){
			IndexRuleProcessor.setStockTransByValRule(transByValRule);
		}else if(qType == Type.FUND){
			IndexRuleProcessor.setFundTransByValRule(transByValRule);
		}
		
		SpecialWords.addPtnRuleWordTypes(ruleSecialWords);
		return indexTranRuleMap;
	}
}
