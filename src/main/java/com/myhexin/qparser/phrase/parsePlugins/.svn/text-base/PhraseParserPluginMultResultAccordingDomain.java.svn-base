package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.util.Consts;

public class PhraseParserPluginMultResultAccordingDomain extends PhraseParserPluginAbstract {

	private static final int MAXCOUNT = 20;//递归计算最大次数,防止出现环

	public PhraseParserPluginMultResultAccordingDomain() {
		super("MultResult_According_Domain");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return deleteDomain(ENV, nodes);
	}

	private ArrayList<ArrayList<SemanticNode>> deleteDomain(Environment ENV, ArrayList<SemanticNode> nodes) {
		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);

		if (nodes == null || nodes.size() == 0)
			return tlist;

		if (nodes.get(0).getType() == NodeType.ENV) {
			Environment listEnv = (Environment) nodes.get(0);
			if (listEnv.containsKey("listDomain")) {
				Map.Entry<String, Double>[] sortedListDomain = (Entry<String, Double>[]) listEnv.get("listDomain", false);
				//没有领域,直接返回
				if (sortedListDomain.length == 0) {
					tlist.add(nodes);
					return tlist;
				}

				//每个最大的demain生成一个list
				Double maxScore = (Double) sortedListDomain[0].getValue();
				boolean doMultResultAccordingDomain = false;
				
				int count = 0;
				boolean event_domain_add=false;		//信息领域有没有被添加
				String else_max_score_domain = null; //分数最大的非信息领域的领域名称
				for (Map.Entry<String, Double> entry : sortedListDomain) {
					String domain = (String) entry.getKey();
					if(domain.equals(Consts.CONST_absXinxiDomain)){//信息领域的先不删除
						tlist.add(cloneNodes(nodes, domain));
						doMultResultAccordingDomain=true;
						event_domain_add = true;
						count++;
					}else {
						Double theScore = (Double)entry.getValue();
						if(theScore > maxScore) {
							else_max_score_domain = domain;
						}
						
						if (maxScore == 1 && maxScore.equals(theScore)) {
							tlist.add(cloneNodes(nodes, domain));
							doMultResultAccordingDomain=true;
							count++;
						}
					}
				}
				
				
				if(count==0) {//没有添加过
					tlist.add(nodes);
				}else if(count==1 && event_domain_add) {//只有信息领域
					
					//找出除信息领域外的分数最高的领域
					if(else_max_score_domain==null) {
						Map.Entry<String, Double> else_entry = null;
						maxScore = (Double) sortedListDomain[0].getValue();
						for (Map.Entry<String, Double> entry : sortedListDomain) {
							String domain = (String) entry.getKey();
							if(domain.equals(Consts.CONST_absXinxiDomain)) continue;
							
							Double theScore = (Double)entry.getValue();
							if(theScore > maxScore || else_entry==null) {
								else_entry = entry;
							}
						}
						//Environment listEnv = (Environment) cloneSn;
						//listEnv.put("listDomain", else_max_score_domain, true);
						if(else_entry!=null){
							Map.Entry[] newDomain = new Map.Entry[]{else_entry};
							listEnv.put("listDomain", newDomain, true);
						}
						tlist.add(nodes);
						doMultResultAccordingDomain=true;
					}else{
						tlist.add(cloneNodes(nodes, else_max_score_domain));
						doMultResultAccordingDomain=true;
					}
				}
				
				if(!doMultResultAccordingDomain)
					listEnv.put("listDomain", new Map.Entry[0],true);
			}
		}
		
		if (tlist.size() == 0)
			tlist.add(nodes);
		
		return tlist;
	}

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-12-25 下午4:01:18
	 * @description:   	
	 * @param nodes
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<SemanticNode> cloneNodes(ArrayList<SemanticNode> nodes, String domain) {
		ArrayList<SemanticNode> cloneNodes = new ArrayList<SemanticNode>();

		for (SemanticNode sn : nodes) {
			SemanticNode cloneSn =  NodeUtil.copyNode(sn); //.copy();
			boolean hasReasonable = false;
			switch (cloneSn.getType()) {
			case BOUNDARY:
				BoundaryNode bNode = (BoundaryNode)cloneSn;
				BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
				if(info == null) break;
				
				
				for (int j = 1; j < info.fixedArgumentMap.size()+1; j++) {
					SemanticNode sNode = info.fixedArgumentMap.get(j);
					if (sNode == null || sNode.type != NodeType.FOCUS)
						continue;
					FocusNode fNode = (FocusNode) sNode;
					ArrayList<FocusNode.FocusItem> itemList = fNode.getFocusItemList();
					for (Iterator<FocusNode.FocusItem> it = itemList.iterator();it.hasNext();) {
						FocusNode.FocusItem item = it.next();
						if (item.getType() == FocusNode.Type.INDEX) {
							ClassNodeFacade cn = item.getIndex();
							if (isClassNodeFacadeReasonable(cn, domain, 0)==false) {
								it.remove();
							}
						}
					}
				}
				
				
				//默认指标absentDefalutIndexMap中,也要删除不在该领域的指标
				ArrayList<Integer> elelist=null;
				for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
					for (int pos : elelist) {
						SemanticNode sNode = null;
						if (pos == -1) {					// 默认指标
							sNode = info.absentDefalutIndexMap.get(j);
						}
						if (sNode == null || sNode.type != NodeType.FOCUS)
							continue;
						FocusNode fNode = (FocusNode) sNode;
						if (!fNode.hasIndex())
							continue;
						ArrayList<FocusNode.FocusItem> itemList = fNode.getFocusItemList();
						for (Iterator<FocusNode.FocusItem> it = itemList.iterator();it.hasNext();) {
							FocusNode.FocusItem item = it.next();
							if (item.getType() == FocusNode.Type.INDEX) {
								ClassNodeFacade cn = item.getIndex();
								if (isClassNodeFacadeReasonable(cn, domain, 0)==false) {
									it.remove();
								}
							}
						}
					}
				}
				break;
			case FOCUS:
				//hasReasonable = false;
				FocusNode fn = (FocusNode) cloneSn;
				ArrayList<FocusNode.FocusItem> itemList = fn.getFocusItemList();
				for (Iterator<FocusNode.FocusItem> it = itemList.iterator();it.hasNext();) {
					FocusNode.FocusItem item =  it.next();
					if (item.getType() == FocusNode.Type.INDEX) {
						ClassNodeFacade cn = item.getIndex();
						if (isClassNodeFacadeReasonable(cn, domain, 0)==false) {
							it.remove();
						}
					}
				}
				//liuxiaofeng 2015/6/18 为什么重复copy?注释掉
				//if (hasReasonable == false)
				//	cloneSn =  NodeUtil.copyNode(sn);//.copy();
				break;
			case STR_VAL:
				StrNode strNode = (StrNode) cloneSn;
				for (String subtype : (LinkedHashSet<String>) strNode.subType.clone()) {
					hasReasonable = false;
					if (!OntoXmlReader.subTypeToIndexContainKey(subtype))
						continue;
					for (ClassNodeFacade cn : OntoXmlReader.subTypeToIndexGet(subtype)) {
						if (isClassNodeFacadeReasonable(cn, domain, 0)) {
							hasReasonable = true;
							break;
						}
					}
					if (!hasReasonable){
						strNode.subType.remove(subtype);
						if(strNode.getDefaultIndexSubtype().equals(subtype))
							strNode.setDefaultIndexSubtype("");
					}

				}
				//liuxiaofeng 2015/6/18 为什么重复copy?注释掉
				//if (strNode.subType.size() == 0)
				//	cloneSn =  NodeUtil.copyNode(sn); //.copy();
				break;
			case ENV:
				Environment listEnv = (Environment) cloneSn;
				if (listEnv.containsKey("listDomain")) {
					Map.Entry[] sortedListDomain = (Entry[]) listEnv.get("listDomain", false);

					for(Map.Entry entry : sortedListDomain)
						if(entry.getKey().equals(domain)){
							Map.Entry[] newDomain = new Map.Entry[]{entry};
							listEnv.put("listDomain", newDomain, true);
						}
				}
				break;
			default:
				break;
			}
			cloneNodes.add(cloneSn);

		}
		return cloneNodes;
	}

	/**
	 * 不是参数domain领域的指标就删除
	 * 
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-12-25 下午4:58:24
	 * @description:   	
	 * @param cn
	 * @param domain
	 * @return
	 */
	private boolean isClassNodeFacadeReasonable(ClassNodeFacade cn, String domain, Integer count) {
		if (cn.getText().equals(domain))
			return true;

		if (cn.getSuperClass().size() == 0 || count > MAXCOUNT)
			return false;

		for (ClassNodeFacade spClass : cn.getSuperClass() ) {
			if (isClassNodeFacadeReasonable(spClass, domain, ++count))
				return true;
		}
		return false;
	}

}
