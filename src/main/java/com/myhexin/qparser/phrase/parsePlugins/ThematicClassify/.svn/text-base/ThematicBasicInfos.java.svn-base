/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-3-20 下午8:41:48
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.ThematicClassify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.IndexIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBind;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

/**
 * 参数对象
 * syntactics : 句式ID
 * semantics ： 语义ID
 * dates :  日期节点
 * unknowns : unknownNode
 */
public class ThematicBasicInfos {
	
	public Map<String, ClassNodeFacade> textIndexMap; // = new HashMap<String, ClassNodeFacade>();
	public Set<String> syntactics = null; 
	public Set<String> semantics = null; 
	public ArrayList<DateNode> dates = null;
	public HashSet<SemanticNode> unknowns = null;

	
	
	/**
	 * lxf
	 * 用nodes初始化
	 * 
	 * @param nodes
	 */
	public ThematicBasicInfos(ArrayList<SemanticNode> nodes) {
		initTextIndexMap(nodes);
		init(nodes);
	}
	
	private void init(ArrayList<SemanticNode> nodes) {

		syntactics  = new HashSet<String>();
		semantics = new HashSet<String>();
		dates = new ArrayList<DateNode>();
		unknowns = new HashSet<SemanticNode>();
		
		int size = nodes.size();
		for (int i=0; i<size; i++) {
			SemanticNode sn = nodes.get(i);
			
			if (sn.type == NodeType.DATE) {
				dates.add((DateNode) sn);
			} else if(sn.type == NodeType.UNKNOWN){
				unknowns.add(sn);
			}
			
			if (sn.type != NodeType.BOUNDARY ) continue;
			
			BoundaryNode bn = (BoundaryNode) sn;
			if (bn.isStart()) continue;
			
			//BoundaryNode.SyntacticPatternExtParseInfo info = bn.getSyntacticPatternExtParseInfo(false);			
			String patternId = bn.getSyntacticPatternId();			
			syntactics.add(patternId);
			
			SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
			if(syntPtn != null){
				SemanticBind sb = syntPtn.getSemanticBind();
				if(sb != null){
					for (SemanticBindTo sbt : sb.getSemanticBindTos()) {
						semantics.add(sbt.getBindToId()+"");
					}
				}
			}
		}
	}


	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-3-14 上午9:58:35
	 * 
	 */
	private void initTextIndexMap(ArrayList<SemanticNode> nodes) {
		IndexIteratorImpl iter = new IndexIteratorImpl(nodes);
		
		textIndexMap = new HashMap<String, ClassNodeFacade>();
		while (iter.hasNext()) {
			ClassNodeFacade cn = iter.next().getIndex();
			if (cn != null)
				textIndexMap.put(cn.getText(), cn);
		}
	}
	
}
