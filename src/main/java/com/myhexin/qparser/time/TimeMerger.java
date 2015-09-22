package com.myhexin.qparser.time;

import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.myhexin.qparser.date.DateParser.DateTag;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.FakeTimeNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.time.parse.TimePatterns;
import com.myhexin.qparser.time.tool.TimeUtil;

@Component
public class TimeMerger {

	public void lightMerge(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		mergeTimeOnlyWithOtherTime(nodes);
	}

	public void merge(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		mergeTimeOnlyWithOtherTime(nodes);
		mergeTimeWithHeadAndTail(nodes);
		mergeTimeWithBedeck(nodes);
		mergeTimeWithConnect(nodes);
	}
	
	public void mergeFakeTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		mergeFakeTimeWithTail(nodes);
		mergeFakeTimeWithConnect(nodes);
	}

	public void mergePureNumAsTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		mergePureNumAsTimeWithColon(nodes);
	}

	private void mergePureNumAsTimeWithColon(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		for (int i = 1; i < nodes.size(); i++) {
			SemanticNode curNode = nodes.get(i);
			if (!":".equals(curNode.getText()))
				continue;
			int lastNodePos = NumUtil.getLastOrNextNode1(i, nodes, true);
			int nextNodePos = NumUtil.getLastOrNextNode1(i, nodes, false);
			if (lastNodePos < 0 || nextNodePos < 0) {
				continue;
			}

			SemanticNode lastNode = nodes.get(lastNodePos);
			SemanticNode nextNode = nodes.get(nextNodePos);

			boolean isFit = TimePatterns.HOUR_NUM.matcher(lastNode.getText()).matches();
			isFit = isFit && TimePatterns.MIN_SEC_NUM.matcher(nextNode.getText()).matches();
			if (!isFit) {
				continue;
			}
			TimeNode repNode = new TimeNode(lastNode.getText() + ":" + nextNode.getText());

			replace(nodes, lastNodePos, nextNodePos + 1, repNode);
			i = nodes.indexOf(repNode);
		}
	}

	public final void replace(ArrayList<SemanticNode> nodes, int start, int end, SemanticNode repNode) {

		nodes.set(start, repNode);
		for (int i = end - 1; i > start; i--) {
			nodes.remove(i);
		}

	}

	private void mergeFakeTimeWithTail(ArrayList<SemanticNode> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode curNode = nodes.get(i);
			boolean isFitTime = curNode.type == NodeType.TIME;
			isFitTime = isFitTime && ((TimeNode) curNode).isFake();
			if (!isFitTime)
				continue;
			int nextNodePos = NumUtil.getLastOrNextNode1(i, nodes, false);
			SemanticNode mergeNode = nextNodePos >= 0 ? nodes.get(nextNodePos) : null;
			boolean nextCanBeMerge = mergeNode != null && FAKE_TIME_END_WORDS.contains(mergeNode.getText());
			if (!nextCanBeMerge)
				continue;
			mergeAsOne(nodes, nextNodePos, i, true);
		}
	}

	/**
	 * 将src节点合并到dest节点成为一个节点。src节点的text拼接到dest前面或后面（取决于参数after）后，然后从QueryNodes
	 * 中删除。
	 */
	public void mergeAsOne(ArrayList<SemanticNode> nodes, int src, int dest, boolean after) {
		mergeAsOne(nodes, src, dest, after, "");
	}

	/**
	 * 与{@link #mergeAsOne(int, int, boolean)}同，但拼接时指定分割符。
	 */
	public void mergeAsOne(ArrayList<SemanticNode> nodes, int src, int dest, boolean after, String delimiter) {
		SemanticNode snSrc = nodes.get(src), snDest = nodes.get(dest);
		if (after) {
			snDest.setText(snDest.getText() + delimiter + snSrc.getText() );
		} else {
			snDest.setText(snSrc.getText() + delimiter + snDest.getText() );
		}
		snDest.addAsOneNode(snSrc);
		if (snDest.type == NodeType.UNKNOWN) {
			UnknownNode snDestUnknown = (UnknownNode) snDest;
		}
		nodes.remove(src);

	}

	private void mergeFakeTimeWithConnect(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode curNode = nodes.get(i);
			if (!isTagOf(curNode, DateTag.BETWEEN)) {
				continue;
			}
			int lastNodePos = NumUtil.getLastOrNextNode1(i, nodes, true);
			int nextNodePos = NumUtil.getLastOrNextNode1(i, nodes, false);
			boolean canMerge = lastNodePos >= 0 && nextNodePos >= 0;
			if (!canMerge)
				continue;
			SemanticNode lastNode = nodes.get(lastNodePos);
			SemanticNode nextNode = nodes.get(nextNodePos);

			canMerge = canMergeWithConnect(lastNode);
			canMerge = canMerge && canMergeWithConnect(nextNode);
			if (!canMerge)
				continue;
			boolean isFake = isFakeTimeNode(lastNode) || isFakeTimeNode(nextNode);
			if (!isFake)
				continue;
			String repNodeStr = lastNode.getText() + curNode.getText() + nextNode.getText();
			FakeTimeNode repNode = new FakeTimeNode(repNodeStr);
			replace(nodes, lastNodePos, nextNodePos + 1, repNode);
			i = nodes.indexOf(repNode);
		}
	}

	private boolean isFakeTimeNode(SemanticNode node) {
		boolean isFakeTime = node != null && node.type == NodeType.TIME;
		isFakeTime = isFakeTime && ((TimeNode) node).isFake();
		return isFakeTime;
	}

	/**
	 * 合并连续出现的数字型时间
	 * @param curChunkNodes
	 * @throws UnexpectedException 
	 */
	private void mergeTimeOnlyWithOtherTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode curNode = nodes.get(i);
			int nextNodePos = NumUtil.getLastOrNextNode1(i, nodes, false);//取下一个节点
			if (nextNodePos < 0) {
				continue;
			}
			SemanticNode nextNode = nodes.get(nextNodePos);
			boolean isFit = false;
			if (TimePatterns.ONLY_HOUR.matcher(curNode.getText()).matches()){
				isFit = minCanBeMerge(nodes,nextNodePos, nextNode);
				isFit = isFit || nextNode.getText().matches("半");
			}/*else if(curNode.isNumNode()){
				isFit = isFit || nextNode.text.matches("点半");
			}*/
			if(isFit){//合并
				TimeNode repNode = new TimeNode(curNode.getText() + nextNode.getText());
				replace(nodes, i, nextNodePos + 1, repNode);
				i = nodes.indexOf(repNode);
			}
		}
	}

	/**
	 * 形如"25分钟"是否可被合并到前面形如"10点"的节点内
	 * @param nextNodePos
	 * @param nextNode
	 * @return
	 * @throws UnexpectedException 
	 */
	private boolean minCanBeMerge(ArrayList<SemanticNode> nodes,int nextNodePos, SemanticNode nextNode) throws UnexpectedException {
		boolean isFit = TimePatterns.ONLY_MIN.matcher(nextNode.getText()).matches();
		boolean canBeTechMin = TimePatterns.CAN_BE_TECH_MIN.matcher(nextNode.getText()).matches();
		canBeTechMin = canBeTechMin && TimeUtil.nextHasTechNodes(nodes, nextNodePos);
		isFit = isFit && !canBeTechMin;
		return isFit;
	}

	/**
	 * 合并时间前后出现起始截止词
	 * @param curChunkNodes
	 * @throws UnexpectedException 
	 */
	private void mergeTimeWithHeadAndTail(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode curNode = nodes.get(i);
			boolean isFitTime = curNode.type == NodeType.TIME;
			isFitTime = isFitTime || TimePatterns.ONLY_HOUR.matcher(curNode.getText()).matches();
			isFitTime = isFitTime || TimePatterns.LENGTH_HOUR.matcher(curNode.getText()).matches();
			isFitTime = isFitTime || TimePatterns.LENGTH_MIN.matcher(curNode.getText()).matches();
			isFitTime = isFitTime || TimePatterns.LENGTH_SEC.matcher(curNode.getText()).matches();
			isFitTime = isFitTime || TimeUtil.isMatchTimePoint(curNode.getText());
			if (!isFitTime)
				continue;

			int lastNodePos = NumUtil.getLastOrNextNode1(i, nodes, true);
			int nextNodePos = NumUtil.getLastOrNextNode1(i, nodes, false);

			boolean lastCanBeMerge = lastOrNextCanBeMergeAsHeadOrTail(nodes,lastNodePos, curNode, true);
			boolean nextCanBeMerge = lastOrNextCanBeMergeAsHeadOrTail(nodes,nextNodePos, curNode, false);
			if (!lastCanBeMerge && !nextCanBeMerge)
				continue;
			int repStartPos = i;
			int repEndPos = i + 1;
			String repNodeStr = curNode.getText();
			if (lastCanBeMerge) {
				repNodeStr = nodes.get(lastNodePos).getText() + repNodeStr;
				repStartPos = lastNodePos;
			}
			if (nextCanBeMerge) {
				repNodeStr = repNodeStr + nodes.get(nextNodePos).getText();
				repEndPos = nextNodePos + 1;
			}
			TimeNode repNode = new TimeNode(repNodeStr);
			replace(nodes, repStartPos, repEndPos, repNode);
			i = nodes.indexOf(repNode);
		}
	}

	private boolean lastOrNextCanBeMergeAsHeadOrTail(ArrayList<SemanticNode> nodes, 
			int mergePos, SemanticNode curNode, boolean isHead) {
		SemanticNode mergeNode = mergePos >= 0 ? nodes.get(mergePos) : null;
		if (mergeNode == null)
			return false;

		boolean canBeMerge = isHead ? isTagOf(mergeNode, DateTag.HEAD) : isTagOf(mergeNode, DateTag.END);
		return canBeMerge;
	}

	private boolean isTagOf(SemanticNode node, DateTag tag) {
		if (node == null || tag == null)
			return false;
		boolean rtn = false;
		switch (tag) {
		case DATE:
			rtn = node.type == NodeType.DATE;
			break;
		case HEAD:
			rtn = TIME_HEAD_WORDS.contains(node.getText());
			break;
		case END:
			rtn = TIME_END_WORDS.contains(node.getText());
			break;
		case BETWEEN:
			rtn = TIME_BETWEEN_WORDS.contains(node.getText());
			break;
		case IRRELEVANT:
			rtn = !isTagOf(node, DateTag.DATE) && !isTagOf(node, DateTag.HEAD) && !isTagOf(node, DateTag.BETWEEN) && !isTagOf(node, DateTag.END);
			break;
		default:
			break;
		}
		return rtn;
	}

	/**
	 * 合并时间前出现的如“上下午”、“开收盘”等词
	 * @param curChunkNodes
	 * @throws UnexpectedException 
	 */
	private void mergeTimeWithBedeck(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode curNode = nodes.get(i);
			boolean isBedeck = TimePatterns.AM_OR_PM.matcher(curNode.getText()).matches();
			isBedeck = isBedeck || TimePatterns.OPEN_OR_CLOSE.matcher(curNode.getText()).matches();
			isBedeck = isBedeck || TimePatterns.STATES.matcher(curNode.getText()).matches();//尾盘
			if (!isBedeck)
				continue;
			mergeTimeWithBedeckFrom(nodes, i);
		}
	}

	private void mergeTimeWithBedeckFrom(ArrayList<SemanticNode> nodes, int curPos) throws UnexpectedException {
		int pos = curPos - 1;
		SemanticNode startNode = nodes.get(curPos);
		assert (TimePatterns.AM_OR_PM.matcher(startNode.getText()).matches() || TimePatterns.OPEN_OR_CLOSE.matcher(startNode.getText()).matches());
		boolean onlyNeedLengthTime = false;
		while (++pos < nodes.size()) {
			SemanticNode curNode = nodes.get(pos);

			if (TimePatterns.AM_OR_PM.matcher(curNode.getText()).matches()) {
				if (pos > curPos) {
					break;
				}
			} else if (TimePatterns.STATES.matcher(curNode.getText()).matches()) {
				if (pos > curPos) {
					mergeAsOne(nodes, pos, curPos, true);
					break;
				}
			} else if (TimePatterns.OPEN_OR_CLOSE.matcher(curNode.getText()).matches()) {
				onlyNeedLengthTime = true;
				if (pos > curPos) {
					mergeAsOne(nodes, pos, curPos, true);

					pos--;
				}
			} else if (curNode.type == NodeType.TIME) {
				if (onlyNeedLengthTime) {
					boolean needBreak = !TimePatterns.LENGTH_TYPE_RANGE_1.matcher(curNode.getText()).matches();
					needBreak = needBreak && !TimePatterns.LENGTH_TYPE_RANGE_NEED_BEDECK.matcher(curNode.getText()).matches();
					if (needBreak)
						break;
				}
				mergeAsOne(nodes, curPos, pos, false);
				break;
			} else if (curNode.type == NodeType.NUM) {
				boolean canMerge = onlyNeedLengthTime;
				if (TimePatterns.LENGTH_MIN.matcher(curNode.getText()).matches()) {
					canMerge = canMerge && !TimeUtil.nextHasTechNodes(nodes, pos);
				} else if (TimePatterns.LENGTH_HOUR.matcher(curNode.getText()).matches()) {
					canMerge = canMerge && !TimeUtil.nextHasTechNodes(nodes, pos);
				} else if (TimePatterns.ONLY_HOUR.matcher(curNode.getText()).matches()) {
					;//No Op
				} else {
					canMerge = false;
				}
				if (!canMerge)
					break;
				TimeNode repNode = new TimeNode(startNode.getText() + curNode.getText());
				nodes.set(curPos, repNode);
				//nodes.mergeAsGroup(pos, curPos);
				break;
			} else {
				break;
			}
		}
	}

	/**
	 * 合并连接词左右出现的时间
	 * @param curChunkNodes
	 * @throws UnexpectedException 
	 */
	private void mergeTimeWithConnect(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode curNode = nodes.get(i);
			if (!isTagOf(curNode, DateTag.BETWEEN)) {
				continue;
			}
			int lastNodePos = NumUtil.getLastOrNextNode1(i, nodes, true);
			int nextNodePos = NumUtil.getLastOrNextNode1(i, nodes, false);
			boolean canMerge = lastNodePos >= 0 && nextNodePos >= 0;
			if (!canMerge)
				continue;
			SemanticNode lastNode = nodes.get(lastNodePos);
			SemanticNode nextNode = nodes.get(nextNodePos);

			canMerge = canMergeWithConnect(lastNode);
			canMerge = canMerge && canMergeWithConnect(nextNode);
			if (!canMerge)
				continue;
			String repNodeStr = lastNode.getText() + curNode.getText() + nextNode.getText();
			TimeNode repNode = new TimeNode(repNodeStr);
			replace(nodes, lastNodePos, nextNodePos + 1, repNode);
			i = nodes.indexOf(repNode);
		}

	}

	private boolean canMergeWithConnect(SemanticNode curNode) {
		boolean canMerge = curNode.type == NodeType.TIME;
		boolean isNumAndCanMerge = curNode.type == NodeType.NUM && TimePatterns.ONLY_HOUR.matcher(curNode.getText()).matches();
		boolean isMatchTimePoint = TimeUtil.isMatchTimePoint(curNode.getText());
		canMerge = canMerge || isNumAndCanMerge || isMatchTimePoint;
		return canMerge;
	}

	private static HashSet<String> TIME_HEAD_WORDS = new HashSet<String>();
	private static HashSet<String> TIME_BETWEEN_WORDS = new HashSet<String>();
	private static HashSet<String> TIME_END_WORDS = new HashSet<String>();
	private static HashSet<String> FAKE_TIME_END_WORDS = new HashSet<String>();

	static {
		String[] heads = { "最近", "过去", "以往", "过往", "前", "截止", "截止到", "从", "持续", "未来", "近", "自", "后" };//add
		for (String word : heads) {
			TIME_HEAD_WORDS.add(word);
		}
		String[] betweens = { "至", "到", "~", "-", "、", "几个", "几", "与", "和" };
		for (String between : betweens) {
			TIME_BETWEEN_WORDS.add(between);
		}
		String ends[] = { "为止", "以来", "以内", "之前", "之后", "后", "内", "以前", "以后", "前" };
		for (String end : ends) {
			TIME_END_WORDS.add(end);
		}

		String fakeEnds[] = { "之前", "之后", "后", "以前", "以后", "前" };
		for (String end : ends) {
			FAKE_TIME_END_WORDS.add(end);
		}
	}

}
