package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

public class PhraseParserPluginAddIndexOfStrInstance extends PhraseParserPluginAbstract {

	public PhraseParserPluginAddIndexOfStrInstance() {
		super("Add_Index_Of_Str_Instance");
	}
	


	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		return addIndexOfStrInstance(annotation.getNodes(),annotation.getEnv());
	}

	/**
	 * 将STR_INSTANCE类型的指标补回
	 * @param ENV 
	 */
	public ArrayList<ArrayList<SemanticNode>> addIndexOfStrInstance(ArrayList<SemanticNode> nodes, Environment ENV) {
		if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext()) {
			tlist.add(nodes);
			return tlist; // 没有句式
		}
        
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    		int bStart = boundaryInfos.bStart;
    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
    		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
			if (info == null)
				return null;
			boolean shouldAdd = IMPLICIT_PATTERN.STR_INSTANCE.toString()
					.equals(bNode.getSyntacticPatternId());
			//对句式配置里明确标为"STR_INSTANCE"类型的，也将句式中的StrNode的指标补回
			SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(bNode
					.getSyntacticPatternId());
			shouldAdd = shouldAdd
					|| syntPtn != null
					&& IMPLICIT_PATTERN.STR_INSTANCE.toString().equals(
							syntPtn.getSyntacticType());
			if (!shouldAdd)
				continue;
			ArrayList<Integer> elelist;
			for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
				boolean isAdded = false;
				for (int pos : elelist) {
					if (pos == -1)
						continue;
					SemanticNode sn = nodes.get(bStart + pos);
					StrNode strNode = null;
			        if (sn.type == NodeType.STR_VAL)
			        	strNode = (StrNode)sn;
			        else if (sn.type == NodeType.FOCUS) {
			        	FocusNode focusNode = (FocusNode) sn;
			        	if (focusNode.hasString())
			        		strNode = focusNode.getString();
			        }
			        
			        if (strNode!=null) {
						FocusNode node = getIndexOfStrInstance(ENV,strNode);
						if (node != null && node.getIndex() != null) {
							info.addElementNodePos(1, -1);
							info.absentArgumentCount++;
			                info.absentDefalutIndexMap.put(1, node);
							info.addElementNodePos(2, 1);
							isAdded = true;
							break;
						}
					}
				}
				if (isAdded)
					break;
			}
    	}
		
		tlist.add(nodes);
		return tlist;
	}

	public static FocusNode getIndexOfStrInstance(Environment ENV,StrNode strNode) {
		if (strNode == null)
			return null;
		FocusNode node = null;
		
		//2014.9.22  吴永行修改   从subTypeToIndex中得到对应的默认指标  准备移除ofwhat相关信息
		Query.Type qType = (Type) ENV.get("qType",false);
		for (String subType : strNode.subType) {
			try {
				if (OntoXmlReader.subTypeToIndexContainKey(subType)) {
					@SuppressWarnings("unchecked")
                    List<ClassNodeFacade> vector = OntoXmlReader.subTypeToIndexGet(subType);
					for (ClassNodeFacade snc : vector) {
						if(!snc.remainDomain(qType))
							continue;						
						if ((node == null || node.getIndex() == null)) {
							node = new FocusNode(snc.getText());
							node.setIndex(snc);
							if(strNode.getDefaultIndexSubtype().equals(""))
								strNode.setDefaultIndexSubtype(subType);
						}
						node.addFocusItem(FocusNode.Type.INDEX, snc.getText(), snc);
					}
				}
			} catch (UnexpectedException e) {
				;
			}
		}				
				
		return node;
	}
	
	//把绑定信息,打印出来
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]:<BR>%s\n", i, this_nodesHtml(qlist.get(i)) ));
        }
    }
    
    //给STR_INSTANCE添加INDEX,打印出添加的Index情况
    private String this_nodesHtml(ArrayList<SemanticNode> nodes) {
    	StringBuilder  buf = new StringBuilder();
       	for(SemanticNode sn : nodes) {
       		if(sn.getType() == NodeType.BOUNDARY && ((BoundaryNode)sn).isStart()) {
       			BoundaryNode bNode = (BoundaryNode)sn;
       			BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
       			buf.append(sn.toString()).append("<BR>");
       			if (info == null)
    			{	
    				continue;
    			}
    			if (!bNode.getSyntacticPatternId().equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())) 
    			{
    				continue;
    			}
    			
    			if(info.absentDefalutIndexMap.isEmpty()==false) {
    				Iterator<Integer> it = info.absentDefalutIndexMap.keySet().iterator();
    				while(it.hasNext()) {
    					Integer k = it.next();
    					SemanticNode v = info.absentDefalutIndexMap.get(k);
    					
    					buf.append("&nbsp;&nbsp;&nbsp;&nbsp;[absentDefalutIndexMap] ").append(k).append(":").append(v.toString()).append("<BR>");
    				}
    			}
    			
       			
       		}else{
       			buf.append(sn.toString()).append("<BR>");
       		}
       	}
       	return buf.toString();
    }
    
}
