/**
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.iterator.ChunkInfos;
import com.myhexin.qparser.iterator.ChunkIteratorImpl;
import com.myhexin.qparser.iterator.ChunkSemicolonIteratorImpl;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.MatchSyntacticPatterns.MatchSyntacticPatterns;

/**
 * @author chenhao
 *
 */
public class PhraseParserPluginNewMatchSyntacticPatternsByChunk extends PhraseParserPluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
			.getLogger(PhraseParserPluginMatchSyntacticPatternsByChunk.class.getName());
	public static final int SYNTACTIC_LIST_MAX_SIZE = 64;
	public static final int SYNTACTIC_LIST_MAX_SIZE_SAME_POS = 8;
	private final static boolean DEBUG = false;

	//@Autowired(required = true)
	private MatchSyntacticPatterns matchSyntacticPatterns = new MatchSyntacticPatterns();

	public PhraseParserPluginNewMatchSyntacticPatternsByChunk() {
		super("Match_Syntactic_Patterns_Breadth_First_By_Chunk");
	}

	public void setMatchSyntacticPatterns(MatchSyntacticPatterns matchSyntacticPatterns) {
		this.matchSyntacticPatterns = matchSyntacticPatterns;
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {

		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return matchSyntacticPatternsByChunk(nodes, ENV);
	}

	// 新增的代码，分chunk句式匹配，并构建笛卡尔积
	private ArrayList<ArrayList<SemanticNode>> matchSyntacticPatternsByChunk(ArrayList<SemanticNode> nodes,
			Environment ENV) {
		//用逗号等分割句式暂时有问题,修复以后再切换回ChunkIteratorImpl
		ChunkIteratorImpl iterator = new ChunkSemicolonIteratorImpl(nodes);
		ArrayList<ArrayList<SemanticNode>> matchedList = new ArrayList<ArrayList<SemanticNode>>();

		//这个什么意思?
		matchedList.add(new ArrayList<SemanticNode>());

		while (iterator.hasNext()) {
			ChunkInfos ci = iterator.next();
			int start = ci.start;
			int end = ci.end;

			ArrayList<ArrayList<SemanticNode>> tempAllList = new ArrayList<ArrayList<SemanticNode>>();
			if (start >= 0 && start < nodes.size() && end >= start && end + 1 <= nodes.size()) {
				ArrayList<SemanticNode> subList = new ArrayList<SemanticNode>(nodes.subList(start, end + 1));
				ArrayList<ArrayList<SemanticNode>> tempList = matchSyntacticPatterns.matchSyntacticPatterns(subList,
						ENV);

				if (tempList != null) {
					if (DEBUG) { //提交时将DEBUG设置成false
						for (ArrayList<SemanticNode> newmatched : tempList) {
							for (SemanticNode nm : newmatched) {
								logger_.debug("new matched : " + nm);
							}
						}
					}

					//笛卡尔积
					//前面的是matchedList, 后面的是newmatched
					for (ArrayList<SemanticNode> matched : matchedList) {
						for (ArrayList<SemanticNode> newmatched : tempList) {
							ArrayList<SemanticNode> tempNodes = new ArrayList<SemanticNode>();
							for (SemanticNode sn : matched) {
								tempNodes.add(sn); //.clone()
							}
							//添加句式分隔节点
							if (tempNodes.size() > 0 && newmatched.size() > 0) {
								tempNodes.add(new UnknownNode(";"));
							}
							for (SemanticNode sn : newmatched) {
								tempNodes.add(sn); //.clone()
							}
							tempAllList.add(tempNodes);
						}
					}
				}
				if (tempAllList != null && tempAllList.size() > 0)
					matchedList = tempAllList;
			}
		}

		if (DEBUG) { //提交时将DEBUG设置成false
			for (ArrayList<SemanticNode> matched : matchedList) {
				for (SemanticNode nm : matched) {
					logger_.debug("final matched : " + nm);
				}
			}
		}

		if (matchedList == null || (matchedList.size() == 1 && matchedList.get(0).size() == 0))
			return null;

		ArrayList<ArrayList<SemanticNode>> finalList = new ArrayList<ArrayList<SemanticNode>>(matchedList.size());
		for (ArrayList<SemanticNode> list : matchedList) {
			ArrayList<SemanticNode> newList = new ArrayList<SemanticNode>();
			for (SemanticNode node : list) {
				newList.add(NodeUtil.copyNode(node)); //.copy());
			}
			finalList.add(newList);
		}

		return finalList;
	}
}
