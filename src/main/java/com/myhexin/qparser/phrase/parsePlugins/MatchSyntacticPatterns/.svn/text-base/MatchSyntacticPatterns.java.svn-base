package com.myhexin.qparser.phrase.parsePlugins.MatchSyntacticPatterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.BoundaryNode.SyntacticPatternExtParseInfo;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.SemanArgType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.IndexGroup;
import com.myhexin.qparser.phrase.SyntacticSemantic.group.KeywordGroup;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAddIndexOfStrInstance;
import com.myhexin.qparser.tech.TechMisc;

/**
 * @author chenhao
 *
 */
@Component
public class MatchSyntacticPatterns {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(MatchSyntacticPatterns.class.getName());
	public static final int SYNTACTIC_LIST_MAX_SIZE = 64;
	public static final int SYNTACTIC_LIST_MAX_SIZE_SAME_POS = 1;
	private static final Pattern CONNECT_WORD_BETWEEN_SAME = Pattern.compile("\\s|,|、|和|与|且");
	public static final int MAX_LOOP_LIMIT = 1000; //为防止死锁,设置一个最大值

	public ArrayList<ArrayList<SemanticNode>> matchSyntacticPatterns(ArrayList<SemanticNode> nodes, Environment ENV) {

		ArrayList<ArrayList<SemanticNode>> list = null;
		list = matchSyntacticPatternsFromBreadthFirst(nodes, ENV);
		return list;
	}

    /**
	 * @param partialSemanticNodesList
	 * @param partialMatchedSemantics
	 *            二维数组,存的是由关键词对应的句式匹配出的结果
	 *            比如一句话有三个关键词A{KEY:1824,KEY:1829,KEY:1421,KEY:1429}
	 *            B{KEY:542,KEY:0,KEY:550} C{KEY:2128,KEY:2160},那该变量存的就是
	 *            [1824,1829,1421,1429]
	 *            [542,0,550]
	 *            [2128,2160]
	 *            每个PartialMatchedSemantic存的就是该句式所匹配上的有BoundaryNode包住的一组节点
	 * @param posToProcess
	 * @param nodes
	 *            传进句式匹配插件的节点集合 处理 PartialMatchedSemantic,将一段一段的句式拼接起来
	 * 
	 */
	private List<PartialMatchedSemantic> handlePartialMatchedSemantic(
			List<PartialMatchedSemantic> partialSemanticNodesList,
			ArrayList<ArrayList<PartialMatchedSemantic>> partialMatchedSemantics, int posToProcess,
			List<SemanticNode> nodes) {
		int firstNotMatchNodePos = 0;
		int firstToMatchNodePos = 0;
		//递归处理结束,收尾工作
		if (posToProcess >= partialMatchedSemantics.size()) {
			if (!partialSemanticNodesList.isEmpty()) {
				for (PartialMatchedSemantic partialMatchedSemantic : partialSemanticNodesList) {
					//把末尾没有匹配上的append上去
					if (partialMatchedSemantic.getEndPos() < nodes.size()) {
						List<SemanticNode> unUsedNodes = nodes
								.subList(partialMatchedSemantic.getEndPos(),
								nodes.size());
						partialMatchedSemantic.mergeNodeWithOutBoundary(unUsedNodes);
					}
				}
			} else {
				PartialMatchedSemantic emptyMatchedSemantic = new PartialMatchedSemantic();
				emptyMatchedSemantic.mergeNodeWithOutBoundary(nodes);
				partialSemanticNodesList.add(emptyMatchedSemantic);
			}

			partialSemanticNodesList = evaluatePartialMatchedSemanticsSamePos(partialSemanticNodesList);
			return partialSemanticNodesList;
		}

		//核心处理部分
		List<PartialMatchedSemantic> newPartialSemanticNodesList = new ArrayList<PartialMatchedSemantic>();

		//首先把本次部分匹配的句式都插入
		for (PartialMatchedSemantic partialMatchedSemantic : partialMatchedSemantics.get(posToProcess)) {
				firstToMatchNodePos = partialMatchedSemantic.getStartPos();
				PartialMatchedSemantic partialSemanticNodes = new PartialMatchedSemantic();
				List<SemanticNode> nodesBetweenSyntactic = nodes.subList(0, firstToMatchNodePos);
				partialSemanticNodes.mergeNodeWithOutBoundary(nodesBetweenSyntactic);
				partialSemanticNodes.mergeNodeWithBoundary(partialMatchedSemantic.getPartialMatchednodes());
				newPartialSemanticNodesList.add(partialSemanticNodes);
		}

		for (PartialMatchedSemantic partialSemanticNodes : partialSemanticNodesList) {
			//每次先把自己插入到下一次会处理的集合里,因为本轮可能没有匹配上
			//那也要给它下一轮匹配的机会
			newPartialSemanticNodesList.add(partialSemanticNodes);
			firstNotMatchNodePos = partialSemanticNodes.getEndPos();
			for (PartialMatchedSemantic partialMatchedSemantic : partialMatchedSemantics.get(posToProcess)) {
				//newPartialSemanticNodesList.add(partialSemanticNodes);
				firstToMatchNodePos = partialMatchedSemantic.getStartPos();
				if (firstToMatchNodePos >= firstNotMatchNodePos) {
					PartialMatchedSemantic newNode = partialSemanticNodes.copyNode();
					//每次把当前部分匹配到的节点拼接到集合里
					//并且把两个句式之间未匹配到的节点也加进去
					List<SemanticNode> nodesBetweenSyntactic = nodes.subList(firstNotMatchNodePos, firstToMatchNodePos);
					newNode.mergeNodeWithOutBoundary(nodesBetweenSyntactic);
					newNode.mergeNodeWithBoundary(partialMatchedSemantic.getPartialMatchednodes());
					newPartialSemanticNodesList.add(newNode);
				} else {
					//之前做过排序,如果当前partialMatchedSemantic的起始位置比未匹配的都要小
					//之后的partialMatchedSemantic的起始位置肯定也小
					break;
				}
			}

		}


		//匹配过程中剪枝,相同长度的partialMatchedNode不用太多
		newPartialSemanticNodesList = evaluatePartialMatchedSemanticsSamePos(newPartialSemanticNodesList);

		return handlePartialMatchedSemantic(newPartialSemanticNodesList, partialMatchedSemantics, posToProcess + 1,
				nodes);
	}

	/**
	 * 匹配过程中剪枝,相同长度的partialMatchedNode不需要超过8个
	 * 
	 * @param newPartialSemanticNodesList 
	 * @return 
	 */
	public List<PartialMatchedSemantic> evaluatePartialMatchedSemanticsSamePos(
			List<PartialMatchedSemantic> partialSemanticNodesList) {

		if (partialSemanticNodesList != null) {
			Iterator<PartialMatchedSemantic> it = partialSemanticNodesList.iterator();
			while (it.hasNext()) {
				PartialMatchedSemantic partialMatchedSemantic = it.next();
				if (!checkMatchedByIndexlistCount(partialMatchedSemantic.getFinalResult())
						|| !checkMatchedByConstantlistCount(partialMatchedSemantic.getFinalResult())) {
					//list.remove(i);
					it.remove();
				}
			}
		}

		if (partialSemanticNodesList == null || partialSemanticNodesList.isEmpty()) {
			return partialSemanticNodesList;
		}
		List<PartialMatchedSemantic> rtnList = new ArrayList<PartialMatchedSemantic>();
		List<PartialMatchedSemantic> temp = new ArrayList<PartialMatchedSemantic>();
		Collections.sort(partialSemanticNodesList, new Comparator<PartialMatchedSemantic>() {
			@Override
			public int compare(PartialMatchedSemantic o1, PartialMatchedSemantic o2) {
				return o1.getEndPos() - o2.getEndPos();
			}
		});

		int currentEndPos = partialSemanticNodesList.get(0).getEndPos();
		int startIndex = 0;
		int i = 0;
		for (i = 0; i < partialSemanticNodesList.size(); i++) {
			PartialMatchedSemantic partialMatchedSemantic = partialSemanticNodesList.get(i);
			if (partialMatchedSemantic.getEndPos() != currentEndPos) {
				if ((i - startIndex) > SYNTACTIC_LIST_MAX_SIZE_SAME_POS) {
					temp = evaluateSyntacticPatterns(partialSemanticNodesList.subList(startIndex, i));
					rtnList.addAll(temp);
				} else {
					rtnList.addAll(partialSemanticNodesList.subList(startIndex, i));
				}
				startIndex = i;
				currentEndPos = partialMatchedSemantic.getEndPos();
			}

		}
		//循环结束要最后判断一次
		if ((i - startIndex) > SYNTACTIC_LIST_MAX_SIZE_SAME_POS) {
			temp = evaluateSyntacticPatterns(partialSemanticNodesList.subList(startIndex, i));
			rtnList.addAll(temp);
		} else {
			rtnList.addAll(partialSemanticNodesList.subList(startIndex, i));
		}
		return rtnList;
	}


	/**
	 * 广度优先遍历 每次从最小前移列表开始匹配 一个最小前移的匹配后，当前总匹配结果超过SYNTACTIC_LIST_MAX_SIZE个时，进行评价剪枝
	 * 评价剪枝针对超过SYNTACTIC_LIST_MAX_SIZE_SAME_POS个的，前移到相同位置的匹配结果进行
	 * 
	 * @param nodes
	 * @return
	 */
	private ArrayList<ArrayList<SemanticNode>> matchSyntacticPatternsFromBreadthFirst(ArrayList<SemanticNode> nodes,
			Environment ENV) {
		if (nodes == null || nodes.size() == 0)
			return null;
		ArrayList<ArrayList<PartialMatchedSemantic>> result = new ArrayList<ArrayList<PartialMatchedSemantic>>();
		for (int j = 0, length = nodes.size(); j < length; j++) {
			SemanticNode node = nodes.get(j);
			if (!node.isCombined && node.type == NodeType.FOCUS) {
				FocusNode keywordNode = (FocusNode) node;
				//遍历数组找到含有keyword的节点,用于匹配句式
				if (keywordNode.hasKeyword()) {
					ArrayList<FocusNode.FocusItem> items = keywordNode.getFocusItemList();
					ArrayList<PartialMatchedSemantic> resultPerKeyword = new ArrayList<PartialMatchedSemantic>();
					for (int k = 0; k < items.size(); k++) {
						//一个关键词可能对应多个句式,把所有可能性找出来,存到result里
						if (items.get(k).getType() == FocusNode.Type.KEYWORD) {
							SyntacticPattern pattern = PhraseInfo.getSyntacticPattern(items.get(k).getContent());
							if (pattern == null)
								continue;
							ArrayList<SemanticNode> copyNodes = new ArrayList<SemanticNode>();
							//拷贝节点,每次必须是独立的节点,不然会相互影响
							for (SemanticNode sn : nodes) {
								copyNodes.add(NodeUtil.copyNode(sn));
							}
							ArrayList<ArrayList<SemanticNode>> resultPerKeywordPerPattern = matchOneSyntacticPattern(
									copyNodes, 0, j, pattern, ENV);
							if (resultPerKeywordPerPattern != null) {
								for (ArrayList<SemanticNode> partialMatchedNodes : resultPerKeywordPerPattern) {
									PartialMatchedSemantic partialMatchedSemantic = new PartialMatchedSemantic(
											partialMatchedNodes);
									resultPerKeyword.add(partialMatchedSemantic);
								}
							}
						}
					}
					//排序,起始坐标越大越在前面
					Collections.sort(resultPerKeyword, new Comparator<PartialMatchedSemantic>() {
						@Override
						public int compare(PartialMatchedSemantic o1, PartialMatchedSemantic o2) {
							return o2.getStartPos() - o1.getStartPos();
						}
					});
					result.add(resultPerKeyword);
				}
			}
		}
		//拼接
		List<PartialMatchedSemantic> partialMatchedSemanticList = handlePartialMatchedSemantic(
				new ArrayList<PartialMatchedSemantic>(), result, 0, nodes);
		//最后的剪枝
		partialMatchedSemanticList = evaluateSyntacticPatterns(partialMatchedSemanticList);

		ArrayList<ArrayList<SemanticNode>> rtn = new ArrayList<ArrayList<SemanticNode>>();
		for (PartialMatchedSemantic partialMatchedSemantic : partialMatchedSemanticList) {
			rtn.add(partialMatchedSemantic.getFinalResult());
		}
		return rtn;
	}

	/**
	 * 检查当前节点node类型是否符合语义中argument要求的类型
	 * 首先检验是否符合SpecificIndex和SpecificIndexGroup要求
	 * 然后再调用checkIndexNodeValType方法判断是否为相应类型
	 * 
	 * @param node
	 * @param arg
	 * @return
	 */
	private boolean checkIndexNode(SemanticNode node, SemanticArgument arg) {
		boolean ret = false;
		if (node != null && arg != null && node.type == NodeType.FOCUS) {
			FocusNode focusNode = (FocusNode) node;
			if (focusNode.hasIndex()) {
				focusNode = (FocusNode) NodeUtil.copyNode(focusNode); //focusNode.copy();
				focusNode.focusList = new ArrayList<FocusNode.FocusItem>(focusNode.getFocusItemList());
				ArrayList<FocusNode.FocusItem> itemList = focusNode.getFocusItemList();
				boolean stop = false;
				String specificIndex;
				if ((specificIndex = arg.getSpecificIndex()) != null) {
					int i = 0;
					int size = itemList.size();
					while (i < size) {
						FocusNode.FocusItem item = itemList.get(i);
						if (item.getType() != FocusNode.Type.INDEX || !item.getContent().equals(specificIndex)) {
							itemList.remove(i);
							i--;
							size--;
						}
						i++;
					}
					if (itemList.size() > 0)
						stop = false;
					else
						stop = true;
				}

				String specificIndexGroupId;
				if (!stop && (specificIndexGroupId = arg.getSpecificIndexGroup()) != null) {
					IndexGroup ig = PhraseInfo.getIndexGroup(specificIndexGroupId);
					/*int i = 0;
					int size = itemList.size();
					while (i < size) {
					    FocusNode.FocusItem item = itemList.get(i);
					    if (item.getType() != FocusNode.Type.INDEX || !ig.contains(item.getContent())) {
					        itemList.remove(i);
					        i--;
					        size--;
					    }
					    i++;
					}*/
					//删除List用iterator
					Iterator<FocusNode.FocusItem> it = itemList.iterator();
					while (it.hasNext()) {
						FocusNode.FocusItem item = it.next();
						if (item == null) {
							it.remove();
							continue;
						}
						if (item.getType() != FocusNode.Type.INDEX || (ig != null && !ig.contains(item.getContent()))) {
							it.remove();
						}
					}

					if (itemList.size() > 0)
						stop = false;
					else
						stop = true;
				}

				if (!stop) {
					stop = !checkIndexNodeValType(node, arg.getAllAcceptValueTypes());
				}
				ret = !stop;
			}
		}
		return ret;
	}

	/**
	 * 检查当前index节点值属性类型是否符合语义中argument要求的类型 STRING -- STR NUMBER --
	 * 单位非PERCENTAGE\BEI的DOUBLE或LONG PERCENTAGE -- 单位为PERCENTAGE\BEI的DOUBLE
	 * UNDEFINED -- DOUBLE或LONG DATE -- DATE
	 * 
	 * @param node
	 * @param valueType
	 * @return
	 */
	private boolean checkIndexNodeValType(SemanticNode node, List<SemanticArgument.ValueType> valueTypes) {
		boolean ret = false;
		if (node != null && node.type == NodeType.FOCUS) {
			FocusNode focusNode = (FocusNode) node;
			if (focusNode.hasIndex()) {
				ArrayList<FocusNode.FocusItem> itemList = focusNode.getFocusItemList();
				int i = 0;
				int size = itemList.size();
				while (i < size) {
					FocusNode.FocusItem item = itemList.get(i);
					boolean isIndexNodeValTypeRight = false;
					if (item.getType() == FocusNode.Type.INDEX) {
						ClassNodeFacade cn = item.getIndex();
						//add by wyh 2015.01.05 没有值属性也能匹配
						for (SemanticArgument.ValueType valueType : valueTypes) {
							if (cn.getPropOfValue() == null && valueType == SemanticArgument.ValueType.UNDEFINED)
								isIndexNodeValTypeRight = true;

							if (cn != null && cn.getPropOfValue() != null)
								switch (valueType) {
								case STRING:
									if (cn.getPropOfValue().getValueType() == PropType.STR) {
										isIndexNodeValTypeRight = true;
									}
									break;
								case NUMBER:
									if (cn.getPropOfValue().getValueType() == PropType.LONG
											|| cn.getPropOfValue().getValueType() == PropType.DOUBLE
											&& !(cn.getValueUnits2().size() == 1 && (cn.getValueUnits2().contains(
													Unit.PERCENT) || cn.getValueUnits2().contains(Unit.BEI)))
											&& !(cn.getValueUnits2().size() == 2 && (cn.getValueUnits2().contains(
													Unit.PERCENT) && cn.getValueUnits2().contains(Unit.BEI)))) {
										isIndexNodeValTypeRight = true;
									}
									break;
								case PERCENTAGE:
									if (cn.getPropOfValue().getValueType() == PropType.DOUBLE
											&& (cn.getValueUnits2().contains(Unit.PERCENT)
													|| cn.getValueUnits2().contains(Unit.BEI) || cn.getValueUnits2()
													.size() == 0)) {
										isIndexNodeValTypeRight = true;
									}
									break;
								case DATE:
									if (cn.getPropOfValue().getValueType() == PropType.DATE) {
										//&& cn.getValueUnits2() != null && cn.getValueUnits2().contains(Unit.DAY)) {
										isIndexNodeValTypeRight = true;
									}
									break;
								case UNDEFINED:
									isIndexNodeValTypeRight = true;
									break;
								default:
									break;
								}
						}
					}
					if (item.getType() != FocusNode.Type.INDEX || isIndexNodeValTypeRight == false) {
						itemList.remove(i);
						i--;
						size--;
					}
					i++;
				}
				if (itemList.size() > 0)
					ret = true;
			}
		}
		return ret;
	}

	private boolean checkConstantNode(SemanticNode node, SyntacticElement ele) {
		if (node == null || ele == null || ele.getSyntElemValueType() == null)
			return true;

		if (ele.getSyntElemValueType() == ValueType.STRING) {
			StrNode sn = getStrValInstance(node);
			if (sn == null)
				return false;

			String subTypes = ele.getSyntElemSubType();
			if (subTypes == null)
				return false;
			for (String subType : subTypes.split("\\|"))
				if (sn.subType.contains(subType))
					return true;

			return false;

		}

		return true;
	}

	/**
	 * 检查当前值节点node类型是否符合语义中argument要求的类型 首先调用checkConstantNodeValType方法判断是否为相应类型
	 * 若为STRING -- STR_VAL 还需要检验SpecificIndex和SpecificIndexGroup
	 * 
	 * @param node
	 * @param arg
	 * @return
	 */
	private boolean checkConstantNode(SemanticNode node, SemanticArgument arg) {
		if (node == null || arg == null)
			return false;
		boolean ret = checkConstantNodeValType(node, arg.getAllAcceptValueTypes());
		if (ret == false)
			return ret;
		if (arg.isContainsValueType(ValueType.STRING)) {
			StrNode sn = getStrValInstance(node);
			//Change by wyh 2014.03.06 判断句式内部的str是否匹配上使用subType判断,而不是ofWhat
			if (sn != null) {
				String specificIndex;
				if ((specificIndex = arg.getSpecificIndex()) != null) {
					if (sn.subType.contains("_" + specificIndex))
						return true;
					else
						return false;
				}

				String specificIndexGroupId;
				if ((specificIndexGroupId = arg.getSpecificIndexGroup()) != null) {
					IndexGroup ig = PhraseInfo.getIndexGroup(specificIndexGroupId);
					for (String index : ig.getIndexs()) {
						//if (sn.hasOfwhat("_"+index))//如上注释
						if (sn.subType.contains("_" + index))
							return true;
					}
					return false;
				}

				return true;
			}
			return false;
		}

		return ret;
	}

	/**
	 * 检查当前值节点node类型是否符合语义中argument要求的类型 STRING -- STR_VAL NUMBER --
	 * 单位非PERCENTAGE\BEI的NUM PERCENTAGE --
	 * 单位为PERCENTAGE\BEI的NUM，或者为UNKNOWN且值范围在-1.0~1.0直接的NUM UNDEFINED --
	 * NUM或STR_VAL DATE -- DATE（暂时不用）
	 * 
	 * @param node
	 * @param valueType
	 * @return
	 */
	private boolean checkConstantNodeValType(SemanticNode node, List<SemanticArgument.ValueType> valueTypes) {
		if (node == null)
			return false;
		for (SemanticArgument.ValueType valueType : valueTypes) {
			switch (valueType) {
			case STRING:
				if (isStrValInstance(node)) {
					return true;
				}
				break;
			case NUMBER:
				if (node.type == NodeType.NUM) {
					NumNode num = (NumNode) node;
					if (num.getUnit() != Unit.PERCENT && num.getUnit() != Unit.BEI) {
						return true;
					}
				}
				break;
			case PERCENTAGE:
				if (node.type == NodeType.NUM) {
					NumNode num = (NumNode) node;
					if (num.getUnit() == Unit.PERCENT || num.getUnit() == Unit.BEI) {
						return true;
					}
					if (num.getUnit() == Unit.UNKNOWN && num.getFrom() > -1.0 && num.getTo() < 1.0) {
						return true;
					}
				}
				break;
			case DATE:
				if (node.type == NodeType.DATE) {
					DateNode dn = (DateNode) node;
					if (dn != null) {
						return true;
					}
				} else if (node.type == NodeType.NUM) {
					NumNode nn = (NumNode) node;
					if (nn != null && nn.isLongNum() && nn.getUnit() == Unit.UNKNOWN) {
						return true;
					}
				}
				break;
			case UNDEFINED:
				if (isStrValInstance(node) || node.type == NodeType.NUM || node.type == NodeType.DATE) {
					return true;
				}
				break;
			default:
				break;
			}
		}
		return false;
	}

	/**
	 * 检查语义中argument节点的限制 INDEX/INDEXLIST --
	 * 是否在SpecificIndex之内？是否在SpecificIndexGroup之内？值属性是否符合要求？ CONSTANT --
	 * 节点类型是否符合要求？ ANY -- 不再使用
	 * 
	 * @param node
	 * @param type
	 * @param arg
	 * @return
	 */
	private boolean checkNodeSemanticConstrains(SemanticNode node, SemanticArgument arg) {
		if (node == null || arg == null)
			return false;
		boolean ret = false;
		SemanArgType type = arg.getType();
		switch (type) {
		case INDEX:
		case INDEXLIST:
			ret = checkIndexNode(node, arg);
			break;
		case CONSTANT:
		case CONSTANTLIST:
			ret = checkConstantNode(node, arg);
			break;
		case ANY: // 暂时没有此项配置
			if (node.type == NodeType.FOCUS) {
				FocusNode focusNode = (FocusNode) node;
				if (focusNode.hasIndex()) {
					ret = checkIndexNode(node, arg);
				}
			} else if (isStrValInstance(node) || node.type == NodeType.NUM || node.type == NodeType.DATE) {
				ret = checkConstantNode(node, arg);
			}
			break;
		}
		return ret;
	}

	/**
	 * 检查节点是否与SyntacticPatternElement相匹配：
	 * 若为keyword，则先判断是否与keyword匹配，再判断是否与keyword group匹配
	 * 若为argument，则先判断arg是否符合语义节点的限制，arg2不为null则再判断arg2是否符合语义节点的限制
	 * 
	 * @param node
	 * @param ele
	 * @param arg
	 * @param arg2
	 *            目前情况arg2均为null
	 * @param syntacticPatternId
	 * @return
	 */
	private SemanticNode matchNodeWithSyntacticPatternElement(SemanticNode node, SyntacticElement ele,
			SemanticArgument arg, SemanticArgument arg2, String syntacticPatternId) {
		boolean matched = false;
		SemanticNode newNode = null;
		// 首先判断关键字
		if (ele.getType() == SyntacticElement.SyntElemType.KEYWORD && node.type == NodeType.FOCUS) {
			FocusNode focusNode = (FocusNode) node;
			if (focusNode.hasKeyword()) {
				String kw;
				String keywordGroupId;
				String text = focusNode.getPubText();
				if ((kw = ele.getKeyword()) != null) {
					if (kw.equals(text)) {
						matched = true;
					}
				} else if ((keywordGroupId = ele.getKeywordGroup()) != null) {
					KeywordGroup kwg = PhraseInfo.getKeywordGroup(keywordGroupId);
					if (kwg.contains(text)) {
						matched = true;
					}
				}
			}

			if (matched) {
				FocusNode nn = (FocusNode) NodeUtil.copyNode(focusNode); //focusNode.copy();
				nn.reset();
				nn.addFocusItem(FocusNode.Type.KEYWORD, String.valueOf(syntacticPatternId));
				nn.isBoundToSynt = true;
				newNode = nn;
			}
		} else if (ele.getType() == SyntacticElement.SyntElemType.ARGUMENT && arg != null) { // 其次判断参数
			SemanArgType type = arg.getType();
			if (type == null) {
				logger_.error("句式-" + syntacticPatternId + "参数存在错误，请核查");
				return null;
			}
			SemanticNode tnode = NodeUtil.copyNode(node); //.copy(); //clone();

			//add by wyh 2015.01.21 检查 BindToArgument
			matched = checkConstantNode(tnode, ele);
			if (!matched)
				return null;

			//检查SemanticArgument
			matched = checkNodeSemanticConstrains(tnode, arg);
			if (matched && arg2 != null) {
				matched = checkNodeSemanticConstrains(tnode, arg2);
			}
			if (matched) {
				tnode.isBoundToSynt = true;
				newNode = tnode;
			}
		}
		return newNode;
	}

	/**
	 * 获得默认指标
	 * 
	 * @param arg
	 * @return
	 */
	private SemanticNode getDefalutIndexNodeForAbsentElem(Environment ENV, SemanticArgument arg) {
		if (arg == null) {
			return null;
		}
		String defaultIndexStr = arg.getDefaultIndex();
		if (defaultIndexStr == null || defaultIndexStr.trim().length() == 0) {
			return null;
		}

		FocusNode newNode = null;
		try {
			//产生一个focusNode占位节点
			if (defaultIndexStr.equals("str_instance"))
				return new FocusNode(defaultIndexStr);

			newNode = new FocusNode(defaultIndexStr);
			String stringArray[] = defaultIndexStr.split("\\|");
			for (String temp : stringArray) {
				Collection collection = MemOnto.getOntoC(temp, ClassNodeFacade.class,
						(Query.Type) ENV.get("qType", false));
				if (collection == null || collection.isEmpty())
					continue;
				Iterator iterator = collection.iterator();
				while (iterator.hasNext()) {
					ClassNodeFacade cn = (ClassNodeFacade) iterator.next();
					if (cn != null) {
						if (!newNode.hasIndex())
							newNode.setIndex(cn);
						newNode.addFocusItem(FocusNode.Type.INDEX, temp, cn);
					}
				}
			}
			if (newNode == null || !newNode.hasIndex())
				return null;
		} catch (UnexpectedException e) {
			return null;
		}
		return newNode;
	}

	// 做缺省情况处理
	private ArrayList<ArrayList<SemanticNode>> matchSyntacticPatternElementsAbsent(List<SemanticNode> nodes,
			MatchPosInfo mpi, ArrayList<SemanticNode> matched, Environment ENV) {
		SyntacticElement ele = mpi.pattern.getSyntacticElement(mpi.curElementPos);
		if (ele == null) {
			return null;
		}
		SemanticArgument arg = ele.getArgument();
		ArrayList<ArrayList<SemanticNode>> list2 = null;
		// 做缺省处理
		if (mpi.inc != 0 && ele.getCanAbsent()) {
			ArrayList<SemanticNode> matched2 = new ArrayList<SemanticNode>();
			for (SemanticNode tmpnode : matched) {
				matched2.add(NodeUtil.copyNode(tmpnode));
				/*if(tmpnode.isBoundaryNode())
					matched2.add(tmpnode.copy() ); //.clone()
				else{
					matched2.add(tmpnode);
				}*/
			}
			BoundaryNode boundary = (BoundaryNode) matched2.get(0);
			BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
			if (extInfo.getElementNodePos(mpi.curElementPos) < 0) {
				extInfo.addElementNodePos(mpi.curElementPos, -1);
				if (ele.getType() == SyntacticElement.SyntElemType.KEYWORD) {
					extInfo.absentKeywordCount++;
				} else {
					extInfo.absentArgumentCount++;
					if (arg.getType() == SemanArgType.INDEX || arg.getType() == SemanArgType.INDEXLIST) {
						SemanticNode defaultNode = getDefalutIndexNodeForAbsentElem(ENV, arg);
						if (defaultNode != null) {
							extInfo.absentDefalutIndexMap.put(mpi.curElementPos, defaultNode);
						} else {
							//logger_.warn("stock_phrase_syntactic.xml - id: " + mpi.pattern.getId());
							//logger_.warn(arg.toString());
							//logger_.warn(arg.getDefaultIndex() + " in " + ENV.get("qType",false) + " is not an index or alias");
							return null;
						}
					}
				}
				if (mpi.ended) {
					TreeSet<Integer> sortedSet = new TreeSet<Integer>(extInfo.absentDefalutIndexMap.keySet());
					int defaultIndexOfStrInstancePos = 0;
					for (Integer pos : sortedSet) {
						if (extInfo.absentDefalutIndexMap.get(pos).getText().equals("str_instance")) {
							if (extInfo.defaultIndexOfStrNode.size() > defaultIndexOfStrInstancePos)
								extInfo.absentDefalutIndexMap.put(pos,
										extInfo.defaultIndexOfStrNode.get(defaultIndexOfStrInstancePos++));
							else {
								mpi.ended = false;
								return null;
							}
						}
					}

					BoundaryNode boundary2 = new BoundaryNode();
					boundary2.setType(BoundaryNode.END, mpi.pattern.getId());
					matched2.add(boundary2);
					list2 = new ArrayList<ArrayList<SemanticNode>>();
					list2.add(matched2);
				} else {
					MatchPosInfo mpi2 = mpi.clone().getAbsentNodePos().getNextElement();
					list2 = matchSyntacticPatternElements(nodes, mpi2, matched2, ENV);
				}
			}
		}
		return list2;
	}

	// 做indexlist处理
	private ArrayList<ArrayList<SemanticNode>> matchSyntacticPatternElementsIndexList(List<SemanticNode> nodes,
			MatchPosInfo mpi, ArrayList<SemanticNode> matched, Environment ENV) {
		ArrayList<ArrayList<SemanticNode>> list3 = null;
		SyntacticElement ele = mpi.pattern.getSyntacticElement(mpi.curElementPos);
		if (ele == null) {
			return null;
		}
		SemanticArgument arg = ele.getArgument();
		if (arg != null && arg.getType() == SemanArgType.INDEXLIST) {

			ArrayList<SemanticNode> matched3 = new ArrayList<SemanticNode>();
			for (SemanticNode tmpnode : matched) {
				matched3.add(NodeUtil.copyNode(tmpnode)); //.copy()); //.clone()
			}

			MatchPosInfo mpi2 = mpi.clone().getNextNodePos(true);
			//Indexlist之间的连词
			if (!mpi2.ended && mpi2.curNodePos > 0 && mpi2.curNodePos < nodes.size()
					&& CONNECT_WORD_BETWEEN_SAME.matcher(nodes.get(mpi2.curNodePos).getText()).matches())
				nodes.get(mpi2.curNodePos).isCombined = true;

			list3 = matchSyntacticPatternElements(nodes, mpi2, matched3, ENV);
		}
		return list3;
	}

	// 做constantlist处理
	private ArrayList<ArrayList<SemanticNode>> matchSyntacticPatternElementsConstantList(List<SemanticNode> nodes,
			MatchPosInfo mpi, ArrayList<SemanticNode> matched, Environment ENV) {
		ArrayList<ArrayList<SemanticNode>> list4 = null;
		SyntacticElement ele = mpi.pattern.getSyntacticElement(mpi.curElementPos);
		if (ele == null) {
			return null;
		}
		SemanticArgument arg = ele.getArgument();
		if (arg != null && arg.getType() == SemanArgType.CONSTANTLIST) {

			ArrayList<SemanticNode> matched4 = new ArrayList<SemanticNode>();
			for (SemanticNode tmpnode : matched) {
				matched4.add(NodeUtil.copyNode(tmpnode)); //.copy()); //.clone()
			}

			//MatchPosInfo mpi = new MatchPosInfo(mpi.start, mpi.alignNodePos, nextNodePos, mpi.pattern,mpi.alignElementPos, mpi.curElementPos, mpi.inc);
			MatchPosInfo mpi2 = mpi.clone().getNextNodePos(true);
			list4 = matchSyntacticPatternElements(nodes, mpi2, matched4, ENV);
		}
		return list4;
	}

	/**
	 * 依次匹配pattern句式的每个keyword关键字，根据keyword关键字前后查找
	 * 
	 * @param nodes
	 *            需要匹配的节点列表
	 * @param start
	 *            匹配的开始节点
	 * @param alignNodePos
	 *            第一个匹配的关键字所在的位置
	 * @param curNodePos
	 *            当前尝试匹配的节点所在的位置
	 * @param pattern
	 *            句式
	 * @param alignElementPos
	 *            第一个匹配的关键字在句式元素中的位置
	 * @param curElementPos
	 *            当前尝试匹配的句式元素的位置
	 * @param inc
	 *            匹配的方向
	 * @param matched
	 *            已匹配的元素的列表
	 * @return
	 */
	private ArrayList<ArrayList<SemanticNode>> matchSyntacticPatternElements(List<SemanticNode> nodes,
			MatchPosInfo mpi, ArrayList<SemanticNode> matched, Environment ENV) {

		//add by wyh 2014.11.17 已经绑定到指标的句式匹配忽略
		SemanticNode sn = mpi.curNodePos >= 0 && mpi.curNodePos < nodes.size() ? nodes.get(mpi.curNodePos) : null;

		ArrayList<ArrayList<SemanticNode>> list1 = null;
		ArrayList<ArrayList<SemanticNode>> list2 = null; // 保存缺省情况列表
		ArrayList<ArrayList<SemanticNode>> list3 = null; // 保存indexlist情况列表
		ArrayList<ArrayList<SemanticNode>> list4 = null; // 保存constantlist情况列表

		mpi.getNextInfo();

		SyntacticElement ele = mpi.pattern.getSyntacticElement(mpi.curElementPos);
		if (ele == null) {
			return null;
		}
		SemanticArgument arg = ele.getArgument();
		SemanticArgument arg2 = null;

		// 其次，如果此句式元素可缺省，做缺省处理
		if (mpi.inc != 0 && ele.getCanAbsent()) {
			list2 = matchSyntacticPatternElementsAbsent(nodes, mpi, matched, ENV);
		}

		// 然后，从当前节点开始尝试匹配当前句式元素
		boolean found = false;
		SemanticNode newNode = null;
		switch (mpi.inc) {
		case 0: // 对于第一个匹配上某个节点的情况，创建句式的开始节点
			if ((newNode = matchNodeWithSyntacticPatternElement(nodes.get(mpi.curNodePos), ele, arg, arg2,
					mpi.pattern.getId())) != null) {
				matched = new ArrayList<SemanticNode>();
				BoundaryNode boundary = new BoundaryNode();
				boundary.setType(BoundaryNode.START, mpi.pattern.getId());
				BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
				extInfo.addElementNodePos(mpi.curElementPos, mpi.curNodePos);
				if (ele.getType() == SyntacticElement.SyntElemType.KEYWORD)
					extInfo.presentKeywordCount++;
				else
					extInfo.presentArgumentCount++;

				//add by wyh 2015.01.06 支持str推出默认指标 "XX不包含str"
				if (newNode.type == NodeType.STR_VAL)
					addDefaultIndexOfStrInstance(ENV, extInfo, (StrNode) newNode);
				else if (newNode.type == NodeType.FOCUS && ((FocusNode) newNode).hasString())
					addDefaultIndexOfStrInstance(ENV, extInfo, ((FocusNode) newNode).getString());

				matched.add(boundary);
				matched.add(newNode);
				found = true;
			} else {
				found = false;
			}
			break;
		case 1:
		case -1: // 往左或往右尝试匹配
			int endPos = mpi.start - 1; // 往左到start-1
			if (mpi.inc == 1)
				endPos = nodes.size(); // 往右到end+1
			int tempCurNodePos = mpi.curNodePos;
			while (mpi.curNodePos != endPos) {
				// 时间指代，不跨越分隔符
				if (!nodes.get(mpi.curNodePos).isCombined && isSepWord(nodes.get(mpi.curNodePos).getText())) {
					BoundaryNode boundary = (BoundaryNode) matched.get(0);
					BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
					if (found == false && matchReferToIndexNode(extInfo, mpi.pattern)) {
						break;
					} else {
						extInfo.referToIndexNodeMap.clear();
						extInfo.referToIndexNodePosMap.clear();
						extInfo.existedIndexNode = null;
						extInfo.existedIndexNodeElementPos = -1;
						extInfo.existedIndexNodePos = -1;
					}
				}
				if (!nodes.get(mpi.curNodePos).isCombined
						&& (newNode = matchNodeWithSyntacticPatternElement(nodes.get(mpi.curNodePos), ele, arg, arg2,
								mpi.pattern.getId())) != null) {
					BoundaryNode boundary = (BoundaryNode) matched.get(0);
					BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
					extInfo.addElementNodePos(mpi.curElementPos, mpi.curNodePos);
					if (ele.getType() == SyntacticElement.SyntElemType.KEYWORD)
						extInfo.presentKeywordCount++;
					else
						extInfo.presentArgumentCount++;

					//add by wyh 2015.01.06 支持str推出默认指标 "XX不包含str"
					if (newNode.type == NodeType.STR_VAL)
						addDefaultIndexOfStrInstance(ENV, extInfo, (StrNode) newNode);
					else if (newNode.type == NodeType.FOCUS && ((FocusNode) newNode).hasString())
						addDefaultIndexOfStrInstance(ENV, extInfo, ((FocusNode) newNode).getString());
					found = true;

					if (arg.getType() == SemanArgType.INDEX) {
						extInfo.existedIndexNode = NodeUtil.copyNode(newNode); //.copy(); //.clone() // 时间节点可指代的指标
						extInfo.existedIndexNodeElementPos = mpi.curElementPos;
						extInfo.existedIndexNodePos = mpi.curNodePos;
						extInfo.referToIndexNodeMap.remove(mpi.curElementPos);
						extInfo.referToIndexNodePosMap.remove(mpi.curElementPos);
					}
				} else {
					SemanticNode referToNewNode = null; // 可指代指标的时间节点
					// 判断是否可为指代节点
					if (arg.getType() == SemanArgType.INDEX
							&& (referToNewNode = referToMatchNodeWithSyntacticPatternElement(nodes.get(mpi.curNodePos),
									ele, arg, arg2, mpi.pattern.getId())) != null) {

						//found = true;
						//newNode = nodes.get(mpi.curNodePos).clone();

						BoundaryNode boundary = (BoundaryNode) matched.get(0);
						BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary
								.getSyntacticPatternExtParseInfo(true);
						//if (curNodePos == extInfo.existedIndexNodePos - 1) {
						if (existedIndexNodePropMaybe(mpi.curNodePos, extInfo.existedIndexNodePos, nodes)) {

						} else {
							if (extInfo.referToIndexNodeMap.get(mpi.curElementPos) != null) {
								break;
							} else {
								extInfo.referToIndexNodeMap.put(mpi.curElementPos, referToNewNode);
								extInfo.referToIndexNodePosMap.put(mpi.curElementPos, mpi.curNodePos);
							}
						}
					}

				}

				if (found) {
					while (tempCurNodePos != mpi.curNodePos) {
						SemanticNode tempNode = NodeUtil.copyNode(nodes.get(tempCurNodePos)); //.copy(); //.clone()
						if (mpi.inc == -1)
							matched.add(1, tempNode);
						else
							matched.add(tempNode);
						tempCurNodePos += mpi.inc;
					}
					if (mpi.inc == -1)
						matched.add(1, newNode);
					else
						matched.add(newNode);
					break;
				}
				mpi.curNodePos += mpi.inc;
			}
			BoundaryNode boundary = (BoundaryNode) matched.get(0);
			BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
			if (found == false && extInfo.referToIndexNodeMap.get(mpi.curElementPos) != null) {
				mpi.curNodePos = extInfo.referToIndexNodePosMap.get(mpi.curElementPos);
				found = true;
				if (found) {
					while (tempCurNodePos != mpi.curNodePos) {
						SemanticNode tempNode = NodeUtil.copyNode(nodes.get(tempCurNodePos)); //.copy(); //.clone()
						if (mpi.inc == -1)
							matched.add(1, tempNode);
						else
							matched.add(tempNode);
						tempCurNodePos += mpi.inc;
					}
					if (mpi.curNodePos != endPos && mpi.curElementPos != 1
							&& mpi.curElementPos != mpi.pattern.getSyntacticElementMax() - 1)
						if (mpi.inc == -1)
							matched.add(1, extInfo.referToIndexNodeMap.get(mpi.curElementPos));
						else
							matched.add(extInfo.referToIndexNodeMap.get(mpi.curElementPos));
				} else {
					mpi.curNodePos += mpi.inc;
				}
			}
			if (mpi.ended && matchReferToIndexNode(extInfo, mpi.pattern)) {
				Iterator iter = extInfo.referToIndexNodeMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Integer referToIndexNodeElementPos = (Integer) entry.getKey();
					SemanticNode referToIndexNode = (SemanticNode) entry.getValue();
					extInfo.addElementNodePos(referToIndexNodeElementPos, -1);
					extInfo.absentArgumentCount++;
					SemanticNode referTo = NodeUtil.copyNode(extInfo.existedIndexNode); //.copy(); //.clone()
					bindNodeToIndex(referToIndexNode, referTo);

					extInfo.absentDefalutIndexMap.put(referToIndexNodeElementPos, referTo);
				}
				extInfo.referToIndexNodeMap.clear();
			}
			break;
		}

		// 之后，如果当前句式元素匹配成功，开始下一步处理
		if (found) {
			// 做indexlist处理
			if (arg != null && arg.getType() == SemanArgType.INDEXLIST) {
				list3 = matchSyntacticPatternElementsIndexList(nodes, mpi, matched, ENV);
			}

			// 做constantlist处理
			if (arg != null && arg.getType() == SemanArgType.CONSTANTLIST) {
				list4 = matchSyntacticPatternElementsConstantList(nodes, mpi, matched, ENV);
			}

			if (mpi.ended) { // 句式匹配结束，创建句式的结束节点
				BoundaryNode boundaryStart = (BoundaryNode) matched.get(0);
				BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundaryStart.getSyntacticPatternExtParseInfo(true);

				TreeSet<Integer> sortedSet = new TreeSet<Integer>(extInfo.absentDefalutIndexMap.keySet());
				int defaultIndexOfStrInstancePos = 0;
				for (Integer pos : sortedSet) {
					if (extInfo.absentDefalutIndexMap.get(pos).getText().equals("str_instance")) {
						if (extInfo.defaultIndexOfStrNode.size() > defaultIndexOfStrInstancePos)
							extInfo.absentDefalutIndexMap.put(pos,
									extInfo.defaultIndexOfStrNode.get(defaultIndexOfStrInstancePos++));
						else {
							mpi.ended = false;
							return null;
						}
					}
				}

				BoundaryNode boundaryEnd = new BoundaryNode();
				boundaryEnd.setType(BoundaryNode.END, mpi.pattern.getId());
				matched.add(boundaryEnd);
				int min = extInfo.getElementNodePosMin();
				int max = extInfo.getElementNodePosMax();
				if (extInfo.absentDefalutIndexMap.get(1) != null) {
					for (int i = matched.size() - 2 - (max - min + 1); i > 0; i--)
						matched.remove(i);
				} else {
					for (int i = matched.size() - 2; i > max - min + 1; i--)
						matched.remove(i);
				}
				list1 = new ArrayList<ArrayList<SemanticNode>>();
				list1.add(matched);
			} else { // 否则，继续匹配剩下的元素
				mpi.getNextNodePos(false);
				mpi.getNextElement();
				list1 = matchSyntacticPatternElements(nodes, mpi, matched, ENV);
			}
		}

		// 最后，将正常匹配的列表、缺省情况列表、indexlist情况列表、constantlist情况列表合并
		if (list1 == null && list2 == null && list3 == null && list4 == null) {
			return null;
		}

		ArrayList<ArrayList<SemanticNode>> list = new ArrayList<ArrayList<SemanticNode>>();
		if (list1 != null && list1.size() > 0)
			list.addAll(list1);
		if (list2 != null && list2.size() > 0)
			list.addAll(list2);
		if (list3 != null && list3.size() > 0)
			list.addAll(list3);
		if (list4 != null && list4.size() > 0)
			list.addAll(list4);

		return list;
	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2015-1-6 下午4:52:29
	 */
	private final void addDefaultIndexOfStrInstance(Environment ENV, BoundaryNode.SyntacticPatternExtParseInfo extInfo,
			StrNode newNode) {
		FocusNode fn = PhraseParserPluginAddIndexOfStrInstance.getIndexOfStrInstance(ENV, newNode);
		if (fn != null)
			extInfo.defaultIndexOfStrNode.add(fn);
	}

	// 判断是否可能为存在的节点的属性
	private boolean existedIndexNodePropMaybe(int curNodePos, int existedIndexNodePos, List<SemanticNode> nodes) {
		if (curNodePos < 0 || curNodePos >= nodes.size())
			return false;
		else if (existedIndexNodePos == -1)
			return false;
		int inc = curNodePos < existedIndexNodePos ? 1 : -1;
		for (int i = curNodePos + inc; i != existedIndexNodePos; i += inc) {
			SemanticNode sn = nodes.get(i);
			if (!Pattern.matches("(\\s|的)", sn.getText()))
				return false;
		}
		return true;
	}

	// 判断时间是否可以指代指标
	private boolean matchReferToIndexNode(SyntacticPatternExtParseInfo extInfo, SyntacticPattern pattern) {
		if (extInfo.existedIndexNode != null) {
			extInfo.referToIndexNodeMap.remove(extInfo.existedIndexNodeElementPos);
			extInfo.referToIndexNodePosMap.remove(extInfo.existedIndexNodeElementPos);
		}
		if (extInfo.referToIndexNodeMap == null || extInfo.referToIndexNodeMap.size() == 0
				|| extInfo.existedIndexNode == null)
			return false;
		Iterator iter = extInfo.referToIndexNodeMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer referToIndexNodeElementPos = (Integer) entry.getKey();
			SemanticNode referToIndexNode = (SemanticNode) entry.getValue();
			SyntacticElement ele = pattern.getSyntacticElement(referToIndexNodeElementPos);
			if (ele == null
					|| ele.getArgument() == null
					|| matchNodeWithSyntacticPatternElement(extInfo.existedIndexNode, ele, ele.getArgument(), null,
							pattern.getId()) == null) {
				extInfo.referToIndexNodeMap.remove(referToIndexNodeElementPos);
				extInfo.referToIndexNodePosMap.remove(referToIndexNodeElementPos);
				break;
			}

			if (referToIndexNode.type == NodeType.DATE && extInfo.existedIndexNode.type == NodeType.FOCUS
					&& ((FocusNode) extInfo.existedIndexNode).hasIndex()) {
				DateNode dn = (DateNode) referToIndexNode;
				ClassNodeFacade index = ((FocusNode) extInfo.existedIndexNode).getIndex();
				if (dn != null && index != null) {
					List<PropNodeFacade> propList = index.getClassifiedProps(PropType.DATE);
					if (propList == null) {
						extInfo.referToIndexNodeMap.remove(referToIndexNodeElementPos);
						extInfo.referToIndexNodePosMap.remove(referToIndexNodeElementPos);
						break;
					}

					boolean matched = false;
					// 优先看n日时间节点和n日属性可否绑定
					if (referToIndexNode.getText().matches(TechMisc.REGEX_N_DAY_NAME) && index.getNDateProp() != null
							&& index.getNDateProp().getValue() == null) {
						matched = true;
					}

					for (PropNodeFacade pn : propList) {
						// 日期的值属性不绑定
						if (pn.isValueProp())
							continue;

						if (pn.isDateProp() && pn.getValue() == null) {
							matched = true;
							break;
						}
					}
					if (matched == false) {
						extInfo.referToIndexNodeMap.remove(referToIndexNodeElementPos);
						extInfo.referToIndexNodePosMap.remove(referToIndexNodeElementPos);
					}
				}
			}
		}
		if (extInfo.referToIndexNodeMap == null || extInfo.referToIndexNodeMap.size() == 0)
			return false;
		else
			return true;
	}

	// 将时间与其指代的指标绑定
	public static boolean bindNodeToIndex(SemanticNode referToIndexNode, SemanticNode existedIndexNode) {
		if (referToIndexNode.type == NodeType.DATE && existedIndexNode.type == NodeType.FOCUS
				&& ((FocusNode) existedIndexNode).hasIndex()) {
			DateNode dn = (DateNode) referToIndexNode;
			ClassNodeFacade index = ((FocusNode) existedIndexNode).getIndex();
			if (dn != null && index != null) {
				List<PropNodeFacade> propList = index.getClassifiedProps(PropType.DATE);
				if (propList == null)
					return false;

				PropNodeFacade nDateProp = index.getNDateProp();
				// 优先绑定n日时间节点和n日属性
				if (referToIndexNode.getText().matches(TechMisc.REGEX_N_DAY_NAME) && nDateProp != null
						&& nDateProp.getValue() == null) {
					return bindNodeToProp(referToIndexNode, nDateProp, index);
				}

				for (PropNodeFacade pn : propList) {
					// 日期的值属性不绑定
					if (pn.isValueProp())
						continue;

					if (pn.isDateProp() && pn.getValue() == null) {
						return bindNodeToProp(referToIndexNode, pn, index);
					}
				}
			}
		}
		return false;
	}

	public static Boolean bindNodeToProp(SemanticNode sn, PropNodeFacade pn, SemanticNode pnParent) {
		if (sn.type == NodeType.DATE && ((DateNode) sn).isCombined)
			return false;
		pn.setValue(sn);
		sn.setIsBoundToIndex(true);
		sn.setBoundToIndexProp(pnParent, pn);
		return true;
	}

	// 判断是否可指代指标
	// 判定规则：需要指标节点，当前节点为时间节点
	private SemanticNode referToMatchNodeWithSyntacticPatternElement(SemanticNode node, SyntacticElement ele,
			SemanticArgument arg, SemanticArgument arg2, String syntacticPatternId) {
		SemanticNode referToNode = null;
		// 首先判断关键字
		if (ele.getType() == SyntacticElement.SyntElemType.KEYWORD && node.type == NodeType.FOCUS) {
			return null;
		} else if (ele.getType() == SyntacticElement.SyntElemType.ARGUMENT && arg != null) { // 其次判断参数
			SemanArgType type = arg.getType();
			if (type == null) {
				logger_.error("句式-" + syntacticPatternId + "参数存在错误，请核查");
				return null;
			}
			if (type == SemanArgType.INDEX && node.type == NodeType.DATE) {
				referToNode = node;
			}
		}
		return referToNode;
	}

	/**
	 * 查看当前pattern能否匹配上
	 * 
	 * @param nodes
	 *            所有节点
	 * @param start
	 *            匹配的起始位置
	 * @param keywordNodePos
	 *            关键字节点所在的句式中的位置
	 * @param pattern
	 *            当前的pattern句式
	 * @return
	 */
	private ArrayList<ArrayList<SemanticNode>> matchOneSyntacticPattern(List<SemanticNode> nodes, int start,
			int keywordNodePos, SyntacticPattern pattern, Environment ENV) {

		ArrayList<ArrayList<SemanticNode>> fullList = null;

		for (int i = 1; i < pattern.getSyntacticElementMax(); i++) {
			SyntacticElement ele = pattern.getSyntacticElement(i);
			if (ele.getType() == SyntacticElement.SyntElemType.KEYWORD) {
				MatchPosInfo mpi = new MatchPosInfo(start, keywordNodePos, keywordNodePos, pattern, i, i, 0);
				ArrayList<ArrayList<SemanticNode>> list = matchSyntacticPatternElements(nodes, mpi, null, ENV);
				// 对于匹配结果进行进一步的限定
				list = checkSyntacticPatternElementsLists(list, ENV);
				if (list != null && list.size() > 0) {
					if (fullList == null) {
						fullList = new ArrayList<ArrayList<SemanticNode>>();
					}
					fullList.addAll(list);
				}
				if (!ele.getCanAbsent())
					break;
			}
		}

		return fullList;
	}

    /*
	 * 对单个句式的匹配结果进行进一步的限定，以剔除那些不符合key_value的情况
	 */
	private ArrayList<ArrayList<SemanticNode>> checkSyntacticPatternElementsLists(
			ArrayList<ArrayList<SemanticNode>> list, Environment ENV) {
		if (list == null || list.size() == 0)
			return null;
		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
		for (ArrayList<SemanticNode> nodes : list) {
			ArrayList<ArrayList<SemanticNode>> rnodes = CheckSyntacticPatternElements.checkSyntacticPatternElements(
					nodes, ENV);
			if (rnodes != null && rnodes.size() > 0)
				rlist.addAll(rnodes);
		}
		return rlist;
	}

	/**
	 * 匹配IMPLICIT_PATTERN.KEY_VALUE/FREE_VAR/STR_INSTANCE三种情况 indexNodePos>=0 &
	 * valueNodePos>=0 -> KEY_VALUE indexNodePos>=0 & valueNodePos=-1 ->
	 * FREE_VAR indexNodePos=-1 & valueNodePos -> STR_INSTANCE
	 * 
	 * @param nodes
	 * @param list
	 * @param indexNodePos
	 * @param valueNodePos
	 * @param copyBefore
	 * @param copyAfter
	 */
	private static void insertImplicitBinaryRelation(List<SemanticNode> nodes, ArrayList<SemanticNode> list,
			int indexNodePos,
			int valueNodePos, int copyBefore, int copyAfter) {
		int startpos;
		if (indexNodePos >= 0 && (valueNodePos < 0 || indexNodePos < valueNodePos))
			startpos = indexNodePos;
		else if (valueNodePos >= 0 && (indexNodePos < 0 || indexNodePos > valueNodePos))
			startpos = valueNodePos;
		else
			return;

		int endpos = indexNodePos > valueNodePos ? indexNodePos : valueNodePos;

		for (int j = copyBefore; j < startpos; j++) {
			list.add(NodeUtil.copyNode(nodes.get(j))); //.copy()); //.clone()
		}

		IMPLICIT_PATTERN patternId = null;
		if (indexNodePos >= 0 && valueNodePos >= 0) {
			patternId = IMPLICIT_PATTERN.KEY_VALUE;
		} else if (indexNodePos >= 0) {
			patternId = IMPLICIT_PATTERN.FREE_VAR;
		} else if (valueNodePos >= 0) {
			patternId = IMPLICIT_PATTERN.STR_INSTANCE;
		} else {
			return;
		}

		BoundaryNode boundary = new BoundaryNode();
		boundary.setType(BoundaryNode.START, patternId.toString());
		list.add(boundary);
		BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);

		int count = 0;
		for (int j = startpos; j <= endpos; j++) {
			if (indexNodePos == j) {
				FocusNode indexNode = (FocusNode) NodeUtil.copyNode(nodes.get(indexNodePos));//.copy();
				indexNode.focusList = new ArrayList<FocusNode.FocusItem>(indexNode.getFocusItemList());
				int k = 0;
				int size = indexNode.focusList.size();
				while (k < size) {
					FocusNode.FocusItem item = indexNode.focusList.get(k);
					if ((patternId == IMPLICIT_PATTERN.KEY_VALUE && item.getType() != FocusNode.Type.INDEX)
							|| (patternId == IMPLICIT_PATTERN.FREE_VAR && (item.getType() != FocusNode.Type.INDEX && item
									.getType() != FocusNode.Type.STRING))) {
						//itemList.remove(k);
						indexNode.removeFocusItem(item.getType(), item.getContent(), item);
						k--;
						size--;
					}
					k++;
				}
				extInfo.addElementNodePos(1, count + 1);
				extInfo.presentArgumentCount++;
				indexNode.isBoundToSynt = true;
				list.add(indexNode);
			} else if (valueNodePos == j) {
				SemanticNode sn = NodeUtil.copyNode(nodes.get(valueNodePos)); //.copy();
				if (nodes.get(j).type == NodeType.FOCUS) {
					FocusNode strNode = (FocusNode) NodeUtil.copyNode(nodes.get(j)); //.copy(); //.clone()
					strNode.focusList = new ArrayList<FocusNode.FocusItem>(strNode.getFocusItemList());
					//ArrayList<FocusNode.FocusItem> itemList = strNode.getFocusItemList();
					int k = 0;
					int size = strNode.focusList.size();
					while (k < size) {
						FocusNode.FocusItem item = strNode.focusList.get(k);
						if (item.getType() != FocusNode.Type.STRING) {
							//itemList.remove(k);
							strNode.removeFocusItem(item.getType(), item.getContent(), item);
							k--;
							size--;
						}
						k++;
					}
					sn = strNode;
				}
				if (patternId == IMPLICIT_PATTERN.KEY_VALUE) {
					extInfo.addElementNodePos(2, count + 1);
				} else {
					extInfo.addElementNodePos(1, count + 1);
				}
				extInfo.presentArgumentCount++;
				sn.isBoundToSynt = true;
				list.add(sn);
			} else {
				list.add(nodes.get(j)); //.clone()
			}
			count++;
		}

		boundary = new BoundaryNode();
		boundary.setType(BoundaryNode.END, patternId.toString());
		list.add(boundary);

		for (int j = endpos + 1; j <= copyAfter; j++) {
			list.add(NodeUtil.copyNode(nodes.get(j))); //.copy()); //.clone()
		}
	}

	/**
	 * 判断是否属于KEY_VALUE类型
	 * 
	 * @param nodes
	 * @param indexNodePos
	 *            KEY即指标的pos
	 * @param valueNodePos
	 *            VALUE即值的pos
	 * @param isDelete
	 * @return
	 */
	private static boolean matchValueNodeToIndexNode(List<SemanticNode> nodes, int indexNodePos, int valueNodePos,
			boolean isDelete) {
		if (indexNodePos == -1 || valueNodePos == -1)
			return false;
		SemanticNode isn = nodes.get(indexNodePos);
		SemanticNode vsn = nodes.get(valueNodePos);
		if ((isn == null || isn.type != NodeType.FOCUS) || vsn == null)
			return false;
		FocusNode fn = (FocusNode) isn;
		if (!fn.hasIndex())
			return false;

		boolean ret = false;
		ArrayList<FocusNode.FocusItem> items = fn.getFocusItemList();
		for (int k = 0; k < items.size();) {
			if (items.get(k).getType() == FocusNode.Type.INDEX) {
				ClassNodeFacade index = items.get(k).getIndex();
				if (index.isStrIndex()) {
					StrNode strNode = getStrValInstance(vsn);
					if (strNode != null) {
						if (matchStrNode(index, strNode)) {
							ret = true;
							if (!isDelete)
								return ret;
							k++;
							continue;
						}
					}
				} else if (index.isNumIndex() || index.isLongIndex() || index.isDoubleIndex()) {
					if (vsn.type == NodeType.NUM) {
						NumNode nNode = (NumNode) vsn;
						Unit unit1 = nNode.getUnit();
						List<Unit> unit2 = index.getValueUnits2();
						if (isUnitEquals(unit1, unit2)) {
							ret = true;
							if (!isDelete)
								return ret;
							k++;
							continue;
						}
					}
				} else if (index.isDateIndex()) {
					if (vsn.type == NodeType.DATE) {
						DateNode dNode = (DateNode) vsn;
						if (isDateTypeEquals(index, dNode)) {
							ret = true;
							if (!isDelete)
								return ret;
							k++;
							continue;
						}
					} else if (vsn.type == NodeType.NUM) {
						NumNode nNode = (NumNode) vsn;
						if (isNumTypeEquals(index, nNode)) {
							ret = true;
							if (!isDelete)
								return ret;
							k++;
							continue;
						}
					}
				}
			}
			if (isDelete)
				items.remove(k);
			else
				k++;
		}
		return false;
	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2014-12-15 下午7:48:28
	 * @description:
	 * @param index
	 * @param strNode
	 * @return
	 */
	private static final boolean matchStrNode(ClassNodeFacade index, StrNode strNode) {
		for (PropNodeFacade pn : index.getClassifiedProps(PropType.STR))
			if (pn.isValueProp() && strNode.hasSubType(pn.getSubType(), true)) {
				return true;
			}

		return false;
	}

	/**
	 * 判断是否属于STR_VAL 1、节点类型为StrNode 2、节点类型为FOCUS，但包含StrNode，如：预增
	 * 
	 * @param node
	 * @return
	 */
	private boolean isStrValInstance(SemanticNode node) {
		if (node == null || node.isCombined == true || (node.type != NodeType.STR_VAL && node.type != NodeType.FOCUS))
			return false;
		StrNode strNode = null;
		if (node.type == NodeType.STR_VAL)
			strNode = (StrNode) node;
		else if (node.type == NodeType.FOCUS) {
			FocusNode focusNode = (FocusNode) node;
			if (focusNode.hasString())
				strNode = focusNode.getString();
		}
		//Change by wyh 2014.03.06 判断句式内部的str是否匹配上使用subType判断,而不是ofWhat
		if (strNode != null /*&& strNode.hasValueOfwhat()*/)
			return true;
		return false;
	}

	private static StrNode getStrValInstance(SemanticNode node) {
		if (node == null || node.isCombined == true)
			return null;
		StrNode strNode = null;
		if (node.type == NodeType.STR_VAL) {
			strNode = (StrNode) node;
		} else if (node.type == NodeType.FOCUS) {
			FocusNode focusNode = (FocusNode) node;
			if (focusNode.hasString()) {
				strNode = focusNode.getString();
			}
		}
		return strNode;
	}

	/**
	 * 匹配IMPLICIT_PATTERN.KEY_VALUE/FREE_VAR/STR_INSTANCE的情况
	 * 注意：1、预增等既可为keyword又可为str_val的情形；2、是、否、10%等没必要当做str_val的情况
	 * 
	 * @param nodes
	 * @param start
	 *            匹配的开始位置
	 * @param end
	 *            匹配的截止位置
	 * @return
	 */
	public static ArrayList<SemanticNode> matchImplicitBinaryRelation(List<SemanticNode> nodes, int start, int end) {
		ArrayList<SemanticNode> list = new ArrayList<SemanticNode>();
		int indexNodePos = -1;
		int newIndexNodePos = -1;
		int valueNodePos = -1;
		int newValueNodePos = -1;
		int copyBefore = start;
		int copyAfter = -1;

		for (int i = start; i < end; i++) {
			IMPLICIT_PATTERN implicit = null;
			SemanticNode node = nodes.get(i);
			//			if (node.isBoundToIndex())
			//				continue;

			switch (node.getType()) {
			case FOCUS:
				FocusNode focusNode = (FocusNode) node;
				if (focusNode.hasIndex()) {
					if (indexNodePos < 0) {
						indexNodePos = i;
					} else {
						// 字符串既可能是index又可能是value的处理，如：政府津贴
						FocusNode tempFocusNode = (FocusNode) nodes.get(indexNodePos);
						if (tempFocusNode.hasIndex() && focusNode.hasString()
								&& matchValueNodeToIndexNode(nodes, indexNodePos, i, false)) {
							valueNodePos = i;
						} else if (tempFocusNode.hasString() && focusNode.hasIndex()
								&& matchValueNodeToIndexNode(nodes, i, indexNodePos, false)) {
							valueNodePos = indexNodePos;
							indexNodePos = i;
						} else {
							newIndexNodePos = i;
						}
					}
				} else if (focusNode.hasString()) {
					if (!focusNode.getString().hasDefaultIndex())
						break;
					if (valueNodePos >= 0 && canBeMatchedStrInstance(nodes.get(valueNodePos))) {
						newValueNodePos = i;
					} else {
						valueNodePos = i;
					}
				}
				break;
			case STR_VAL:
				/*if (!((StrNode) node).hasValueOfwhat())
					break;*/
			case NUM:
			case DATE:
				if (valueNodePos >= 0 && canBeMatchedStrInstance(nodes.get(valueNodePos))) {
					newValueNodePos = i;
				} else {
					valueNodePos = i;
				}
				break;
			default:
				break;
			}
			if (indexNodePos < 0) {
				if (valueNodePos >= 0 && i == end - 1 && canBeMatchedStrInstance(nodes.get(valueNodePos))) {
					implicit = IMPLICIT_PATTERN.STR_INSTANCE; // STR_INSTANCE
				} else if (newValueNodePos >= 0) {
					implicit = IMPLICIT_PATTERN.STR_INSTANCE; // STR_INSTANCE
				}
			} else {
				if (newIndexNodePos >= 0) {
					implicit = IMPLICIT_PATTERN.FREE_VAR; // FREE_VAR
				} else {
					if (valueNodePos >= 0) {
						if (!matchValueNodeToIndexNode(nodes, indexNodePos, valueNodePos, false)) { // not match
							if (indexNodePos > valueNodePos) {
								if (canBeMatchedStrInstance(nodes.get(valueNodePos))) {
									implicit = IMPLICIT_PATTERN.STR_INSTANCE; // STR_INSTANCE
								} else {
									valueNodePos = -1;
									/*
									implicit = IMPLICIT_PATTERN.FREE_VAR;
									valueNodePos = -1;
									newIndexNodePos = -1;
									*/
								}
							} else {
								implicit = IMPLICIT_PATTERN.FREE_VAR; // FREE_VAR
								if (!canBeMatchedStrInstance(nodes.get(valueNodePos))) {
									valueNodePos = -1;
								}
								newIndexNodePos = -1;
							}
						} else {
							implicit = IMPLICIT_PATTERN.KEY_VALUE; // match KEY_VALUE
						}
					} else if (indexNodePos >= 0 && i == end - 1) {
						implicit = IMPLICIT_PATTERN.FREE_VAR;
					}
				}
			}
			if (implicit != null) {
				if (implicit == IMPLICIT_PATTERN.STR_INSTANCE) {
					if (canBeMatchedStrInstance(nodes.get(valueNodePos))) {
						insertImplicitBinaryRelation(nodes, list, -1, valueNodePos, copyBefore, copyAfter);
						copyBefore = valueNodePos + 1;
					} else {
						implicit = null;
					}
					valueNodePos = newValueNodePos;
					newValueNodePos = -1;
				} else if (implicit == IMPLICIT_PATTERN.FREE_VAR) {
					insertImplicitBinaryRelation(nodes, list, indexNodePos, -1, copyBefore, copyAfter);
					copyBefore = indexNodePos + 1;
					indexNodePos = newIndexNodePos;
					newIndexNodePos = -1;
				} else if (implicit == IMPLICIT_PATTERN.KEY_VALUE) {
					insertImplicitBinaryRelation(nodes, list, indexNodePos, valueNodePos, copyBefore, copyAfter);
					copyBefore = indexNodePos > valueNodePos ? indexNodePos + 1 : valueNodePos + 1;
					indexNodePos = newIndexNodePos;
					newIndexNodePos = -1;
					valueNodePos = newValueNodePos;
					newValueNodePos = -1;
				}
			}
		}
		copyAfter = end - 1;
		if (indexNodePos >= 0 || (valueNodePos >= 0 && canBeMatchedStrInstance(nodes.get(valueNodePos)))) {
			insertImplicitBinaryRelation(nodes, list, indexNodePos, valueNodePos, copyBefore, copyAfter);
		} else {
			for (int j = copyBefore; j < end; j++) {
				list.add(NodeUtil.copyNode(nodes.get(j))); //.copy()); //.clone()
			}
		}
		return list;
	}

	/**
	 * 判断是否可以转化为STRINSTANCE，只有有意义的字符串转化为STRINSTANCE才有意义
	 * @param nodes
	 * @param valueNodePos
	 * @return
	 */
	private static boolean canBeMatchedStrInstance(SemanticNode vsn) {
		if (vsn == null)
			return false;
		if (vsn.type == NodeType.NUM || vsn.type == NodeType.DATE) {
			return false;
		} else {
			StrNode strNode = getStrValInstance(vsn);
			if (strNode == null)
				return false;

			for (String st : strNode.subType)
				if (OntoXmlReader.subTypeToIndexGet(st) != null)
					return true;

			//测试无误后删除 wyh 20114.11.13			
			/*if (strNode != null && strNode.ofWhat != null) {
				for (SemanticNode ofwhat : strNode.ofWhat)
					if (ofwhat.text.contains(MiscDef.STR_COND_VALUE_PROP_MARK)) // 处理非值属性，如“前复权”的情况
						return true;
				return false;
			}*/
		}
		return false;
	}


	/**
	 * 在匹配结束后，如果语义要求的参数是indexlist，且匹配的参数个数小于mincount限制，移除该匹配 应在匹配过程中做更好
	 */
	private boolean checkMatchedByIndexlistCount(ArrayList<SemanticNode> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode sNode = nodes.get(i);
			if (sNode.type == NodeType.BOUNDARY && ((BoundaryNode) sNode).isStart()) {
				BoundaryNode bNode = (BoundaryNode) sNode;
				String patternId = bNode.getSyntacticPatternId();
				if (BoundaryNode.getImplicitPattern(patternId) != null) {
					continue;
				}
				SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
				BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);

				for (int j = 1; j < syntPtn.getSyntacticElementMax(); j++) {
					SyntacticElement element = syntPtn.getSyntacticElement(j);
					SemanticArgument argument = element.getArgument();
					if (argument != null && argument.getType() == SemanArgType.INDEXLIST) {
						List<Integer> poses = info.getElementNodePosList(j);
						int num = argument.getListElementMinCount();
						if (num > poses.size()) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * 在匹配结束后，如果语义要求的参数是constantlist，且匹配的参数个数小于mincount限制，移除该匹配 应在匹配过程中做更好
	 */
	private boolean checkMatchedByConstantlistCount(ArrayList<SemanticNode> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode sNode = nodes.get(i);
			if (sNode.type == NodeType.BOUNDARY && ((BoundaryNode) sNode).isStart()) {
				BoundaryNode bNode = (BoundaryNode) sNode;
				String patternId = bNode.getSyntacticPatternId();
				if (BoundaryNode.getImplicitPattern(patternId) != null) {
					continue;
				}
				SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
				BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);

				for (int j = 1; j < syntPtn.getSyntacticElementMax(); j++) {
					SyntacticElement element = syntPtn.getSyntacticElement(j);
					SemanticArgument argument = element.getArgument();
					if (argument != null && argument.getType() == SemanArgType.CONSTANTLIST) {
						List<Integer> poses = info.getElementNodePosList(j);
						int num = argument.getListElementMinCount();
						if (num > poses.size()) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private List<PartialMatchedSemantic> evaluateSyntacticPatterns(List<PartialMatchedSemantic> list) {
		//System.out.println("evaluateSyntacticPatterns(Before): " + list.size());
		// 移除包含indexlist且不符合最小限制的
		if (list != null) {
			Iterator<PartialMatchedSemantic> it = list.iterator();
			for (; it.hasNext();) {
				ArrayList<SemanticNode> nodes = it.next().getFinalResult();
				boolean indexListCountResult = checkMatchedByIndexlistCount(nodes);
				boolean constantListCountResult = checkMatchedByConstantlistCount(nodes);

				if (indexListCountResult == false || constantListCountResult == false) {
					//list.remove(i);
					it.remove();
				}
			}
		}

		if (list == null || list.size() <= 1) {
			return list;
		}

		PartialMatchedSemanticComparator comparator = new PartialMatchedSemanticComparator();
		Collections.sort(list, comparator);

		// 保留评分最高的
		ArrayList<PartialMatchedSemantic> ret = null;// new ArrayList<ArrayList<SemanticNode>>(1);
		if (list != null && list.size() > 0) {
			//ret.add(list.get(0));
			ret = new ArrayList<PartialMatchedSemantic>();
			for (int i = 0; i < list.size(); i++) {
				PartialMatchedSemantic oneq = list.get(i);
				int compareScore = comparator.compare(list.get(0), oneq);
				if (i == 0 || compareScore == 0) {
					ret.add(oneq);
				} else {
					break;
				}
			}
		}
		return ret;
	}

	// KEY_VALUE匹配时单位的验证
	// 1、单位相同-true 2、其中一个单位UNKNOWN-true 3、百分比或倍-true
	private static boolean isUnitEquals(Unit unit1, List<Unit> unit2) {
		//System.out.println(unit1 + ":" + unit2);
		if (unit2 == null)
			return false;
		else if (unit2.size() == 0)
			return true;
		boolean ret = false;
		if (unit2.contains(unit1)) {
			ret = true;
		} else if (unit1 == Unit.UNKNOWN || unit2.contains(Unit.UNKNOWN)) {
			ret = true;
		} else if ((unit2.contains(Unit.PERCENT) || unit2.contains(Unit.BEI))
				&& (unit1 == Unit.PERCENT || unit1 == Unit.BEI)) {
			ret = true;
		} else {
			ret = false;
		}
		return ret;
	}

	// KEY_VALUE匹配时时间类型的验证
	// 暂时没有比较严格的判断逻辑，保留接口
	private static boolean isDateTypeEquals(ClassNodeFacade index, DateNode dNode) {
		return true;
		//return false;
	}

	// KEY_VALUE匹配时时间类型与Num节点的验证
	// index：时间类型指标，有单位
	// NumNode：整型数值，无单位
	private static boolean isNumTypeEquals(ClassNodeFacade index, NumNode nNode) {
		if (index.isDateIndex() && index.getValueUnits2() != null && index.getValueUnits2().contains(Unit.DAY)
				&& nNode.isLongNum() && nNode.getUnit() == Unit.UNKNOWN)
			return true;
		return false;
	}

	// 判断是否为分隔符
	private static boolean isSepWord(String text) {
		if (text == null)
			return false;
		if (Pattern.matches("(,|;|\\.|。)", text))
			return true;
		return false;
	}
}
