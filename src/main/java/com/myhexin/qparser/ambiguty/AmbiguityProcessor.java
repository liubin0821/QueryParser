package com.myhexin.qparser.ambiguty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.DB.mybatis.mode.Aliases;
import com.myhexin.DB.mybatis.mode.Indexs;
import com.myhexin.DB.mybatis.mode.ResolveAliasesConflicts;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;

@Component
public class AmbiguityProcessor {

   	private ArrayList<AmbiguityCondAbstract> condList_ = null; //new ArrayList<AmbiguityCondAbstract>();
	
	public AmbiguityProcessor() {
		condList_ = getCondList();
		/*AmbiguityCondAbstract cond = null;
        cond = AmbiguityCondFactory.create("unit");
        if(cond != null) condList_.add(cond);
        cond = AmbiguityCondFactory.create("props");
        if(cond != null) condList_.add(cond);
        cond = AmbiguityCondFactory.create("words");
        if(cond != null) condList_.add(cond);
        cond = AmbiguityCondFactory.create("indexs");
        if(cond != null) condList_.add(cond);
        cond = AmbiguityCondFactory.create("numrange");
        if(cond != null) condList_.add(cond);
        cond = AmbiguityCondFactory.create("defaultval");
        if(cond != null) condList_.add(cond);*/
	}
	
	public static ArrayList<AmbiguityCondAbstract> getCondList() {
		ArrayList<AmbiguityCondAbstract> condList1_ = new ArrayList<AmbiguityCondAbstract>();
	
		AmbiguityCondAbstract cond = null;
		
        cond = AmbiguityCondFactory.create("unit");
        if(cond != null) condList1_.add(cond);
        
        cond = AmbiguityCondFactory.create("props");
        if(cond != null) condList1_.add(cond);
        
        cond = AmbiguityCondFactory.create("words");
        if(cond != null) condList1_.add(cond);
        
        cond = AmbiguityCondFactory.create("indexs");
        if(cond != null) condList1_.add(cond);
        
        cond = AmbiguityCondFactory.create("numrange");
        if(cond != null) condList1_.add(cond);
        
        cond = AmbiguityCondFactory.create("defaultval");
        if(cond != null) condList1_.add(cond);
        
		return condList1_;
	}

    public void processAll(ArrayList<SemanticNode> nodes, StringBuilder logsb) {
    	if (nodes == null || nodes.size() == 0)
    		return;
    	
    	ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>();
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext()) {
			tlist.add(nodes);
			return; // 没有句式
		}
        
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
    		processSyntactic(nodes, bNode, boundaryInfos.start, boundaryInfos.end, boundaryInfos.bStart, boundaryInfos.bEnd, logsb);
    	}
    }
    
    private void processSyntactic(ArrayList<SemanticNode> nodes, BoundaryNode bNode, int start , int end, int bStart, int bEnd, StringBuilder logsb) {
    	DecimalFormat df = new DecimalFormat("0.00");
    	BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        if(info == null)
            return;
		ArrayList<Integer> elelist;
		int defaultStart = 0;
		
		if(logsb!=null) {
			logsb.append("## 0ms ## Ambiguity Processing...\n");
		}
		
		for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
			for (int pos : elelist) {
				int i = 0;
				SemanticNode sNode = null;
				
				if (pos != -1) {			// 显式指标
					defaultStart = pos;
					i = bStart + pos;
					sNode = nodes.get(i);
				} else {					// 默认指标
					i = bStart + defaultStart;
					sNode = info.absentDefalutIndexMap.get(j);
				}
				if (sNode == null || sNode.type != NodeType.FOCUS)
					continue;
				FocusNode fNode = (FocusNode) sNode;
				if (!fNode.hasIndex())
					continue;
				double maxScore = 0;
				double secScore = 0;
				ClassNodeFacade maxScoreIndex = null;
				for (FocusItem fi : fNode.getFocusItemList()) {
					if (fi.getType() != FocusNode.Type.INDEX)
						continue;
					ClassNodeFacade index = fi.getIndex();
					double scoreTotal = 0;
					if(index!=null && logsb!=null)
						logsb.append("Index: " + index + ": ");

					for (AmbiguityCondAbstract cond : condList_) {
						String alias = fNode.getText();
						// 修改，增加当前位置信息
						double score = cond.matchNode(nodes, start, end, bStart, bEnd, index, i, alias);
						
						if(logsb!=null) {
							//出现 异常 logStr放在append外面试试
							String logStr = cond + ":" + df.format(score) + ";";
							logsb.append(logStr);
						}
						
						
						if (score == -1) {
							scoreTotal = -1;
							break;
						}
						scoreTotal += score;
					}
					if (scoreTotal == -1) {
						fi.setScore(-1);
						removeAlias(fNode, index.getText(), fi);
					} else {
						fi.setScore( scoreTotal / condList_.size() );
						if (fi.getScore() > secScore)
							secScore = fi.getScore() ;
						if (fi.getScore() > maxScore) {
							secScore = maxScore;
							maxScore = fi.getScore() ;
							maxScoreIndex = index;
						}
					}
					if(logsb!=null) {
						logsb.append("\n");
					}
				}
				//int left = 0;
				//left = removeLowScoreIndexItems(fNode, secScore);
				//if (left > 5) {
				removeLowScoreIndexItems(fNode, maxScore);
				//}
				if (maxScoreIndex != null) {
					fNode.setIndex(maxScoreIndex, true);
				}
			}
		}
		
		// 对于语义固定值参数的处理，这部分代码可能需要调整
		for (int j = 1; j < info.fixedArgumentMap.size()+1; j++) {
			int i = bStart;
			SemanticNode sNode = info.fixedArgumentMap.get(j);
			if (sNode == null || sNode.type != NodeType.FOCUS)
				continue;
			FocusNode fNode = (FocusNode) sNode;
			if (!fNode.hasIndex())
				continue;
			double maxScore = 0;
			double secScore = 0;
			ClassNodeFacade maxScoreIndex = null;
			for (FocusItem fi : fNode.getFocusItemList()) {
				if (fi.getType() != FocusNode.Type.INDEX)
					continue;
				ClassNodeFacade index = fi.getIndex();
				double scoreTotal = 0;
				if(logsb!=null) logsb.append("Index: " + index + ": ");

				for (AmbiguityCondAbstract cond : condList_) {
					String alias = fNode.getText();
					// 修改，增加当前位置信息
					double score = cond.matchNode(nodes, start, end,
							bStart, bEnd, index, i, alias);
					if(logsb!=null) logsb.append(cond + ":" + df.format(score) + ";");
					if (score == -1) {
						scoreTotal = -1;
						break;
					}
					scoreTotal += score;
				}
				if (scoreTotal == -1) {
					fi.setScore(-1 );
					removeAlias(fNode, index.getText(), fi);
				} else {
					fi.setScore(scoreTotal / condList_.size());
					if (fi.getScore() > secScore)
						secScore = fi.getScore();
					if (fi.getScore() > maxScore) {
						secScore = maxScore;
						maxScore = fi.getScore();
						maxScoreIndex = index;
					}
				}
				if(logsb!=null) logsb.append("\n");
			}
			int left = 0;
			left = removeLowScoreIndexItems(fNode, secScore);
			if (left > 5) {
				left = removeLowScoreIndexItems(fNode, maxScore);
			}
			if (maxScoreIndex != null)
				fNode.setIndex(maxScoreIndex, true);
		}
    }

    private int removeLowScoreIndexItems(FocusNode fNode, double maxScore) {
        int left = 0;
        for(int j=0; j < fNode.getFocusItemList().size(); j++) {
            FocusItem fi = fNode.getFocusItemList().get(j);
            if(fi.getType() != FocusNode.Type.INDEX) {
                continue;
            }
            if(fi.isCanDelete() == true) {
            	continue;
            }
            if(fi.getScore() < maxScore) {
                removeAlias(fNode, fi.getIndex().getText(), fi);
                continue;
            }
            left += 1;
        }
        return left;
    }

    /**
     * @deprecated
     * 
     */
    public void removeAlias(FocusNode fNode, String indexTitle) {
        fNode.markFocusItemCanDelete(FocusNode.Type.INDEX, indexTitle);
    }
    
    public void removeAlias(FocusNode fNode, String indexTitle, FocusItem fi) {
        fNode.markFocusItemCanDelete(FocusNode.Type.INDEX, indexTitle, fi);
    }

    /**
     * 仅仅为了兼容 OntoXmlReaderOldSystem
     */
    @Deprecated
	public void parseConfig(Node configNode, ClassNodeFacade cn) {
    	NodeList children = configNode.getChildNodes();
    	for (int i = 0; i < children.getLength(); i ++) {
    		Node node = children.item(i);
    		if (!node.getNodeName().equals("alias")) {
    			continue;
    		}
    		if (node.getAttributes() == null) {
    			continue;
    		}
    		Attr attrTitle = (Attr)node.getAttributes().getNamedItem("title");
    		if (attrTitle == null || attrTitle.getValue().length() == 0) {
    			continue;
    		}
    		String alias = attrTitle.getValue();
    		NodeList subAlias = node.getChildNodes();
    		for (int j = 0; j < subAlias.getLength(); j ++) {
    			if (subAlias.item(j).getNodeName().equals("ResolvingConflicts")) {
    				parseCondConfig(subAlias.item(j), cn, alias);
    			}
    		}
		    cn.setAlias_ForAmbiguity(alias);
    	}
    }
	
	public void parseConfigNew(Indexs index,ClassNodeFacade cn,List<Aliases> aliasesList, Map<Integer, ArrayList<ResolveAliasesConflicts>> ra){		
		if(aliasesList == null)
			return;
		
		for(Aliases alias : aliasesList){
			parseCondConfig(alias,cn,ra.get(alias.getId()));
			cn.setAlias_ForAmbiguity(alias.getLabel());
		}
	}
	
    /**
     * 仅仅为了兼容 OntoXmlReaderOldSystem
     */
	@Deprecated
    private void parseCondConfig(Node configNode, ClassNodeFacade cn, String alias) {
        for(AmbiguityCondAbstract cond : condList_) {
            cond.parseConfig(configNode, cn, alias);
        }
    }

    private void parseCondConfig(Aliases alias, ClassNodeFacade cn,List<?> list) {
        for(AmbiguityCondAbstract cond : condList_) {
            cond.parseConfig(alias, cn,list);
        }
    }
}
