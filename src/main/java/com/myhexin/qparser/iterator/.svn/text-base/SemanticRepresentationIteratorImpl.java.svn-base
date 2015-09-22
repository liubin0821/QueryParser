package com.myhexin.qparser.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.define.EnumDef.DescNodeType;

public class SemanticRepresentationIteratorImpl implements Iterator<DescNode> {
	private List<DescNode> descNodes;
	private int current;
	private String chineseRepresentation;
	
	public SemanticRepresentationIteratorImpl(String chineseRepresentation) {
        this.chineseRepresentation = chineseRepresentation;
        this.descNodes = new ArrayList<DescNode>();
        this.current = -1;
        getDescNodes();
	}

	/**
	 * 用于判断是否还有下一个遍历
	 */
	public boolean hasNext() {
		if (current < descNodes.size() - 1) {
			return true;
		}
		return false;
	}

	/**
	 * 用于得到下一个遍历
	 */
	public DescNode next() {
		if (hasNext()) {
			current++;
			return descNodes.get(current);
		}
		return null;
	}
	
	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}
	
    /**
     * 用于过滤和生产List<DescNode>，其中由于对DescNode的处理比较复杂，所以建议先将其处理生产所需的链表然后再做处理。
     * 如果还按照句式和指标的方法来实现，则运行过程会相当复杂。在之前的版本上就是用这种方法。
     * 如果有空格，则将其取出。其表示无意义。
     */
	private void getDescNodes() {
		int last = 0;
		Pattern p = Pattern.compile("\\$(\\d+)");
		Matcher m = p.matcher(chineseRepresentation);
		while (m.find()) { // 寻找$1、$2等情况，并处理其前面的字符串
			String before = chineseRepresentation.substring(last, m.start()).trim();
			if (!before.isEmpty()) {
				List<DescNode> temDescNodes = keyWordSplitProcessing(before);
				descNodes.addAll(temDescNodes);
			}
			descNodes.add(new DescNodeNum(m.group(), DescNodeType.NUM));
			last = m.end();
		}
		// 处理最后面那一段字符串
		String text = chineseRepresentation.substring(last);
		if (!text.isEmpty()) {
			List<DescNode> temDescNodes = keyWordSplitProcessing(text);
			descNodes.addAll(temDescNodes);
		}
	}
	
    /**
     *	用于对$1与$2中间关键数据的处理函数。 
     * @param keyWordDescNodes
     */
	private List<DescNode> keyWordSplitProcessing(String keyWordDescNodes){
		List<DescNode> temDescNodes = new ArrayList<DescNode>();
		// 这里暂时不区分 比较符号(>=|<=|>|<|!=|=) 四则运算(+|-|*|/) 符号(-) 等
		Pattern bindToOtherBind = Pattern.compile(">=|<=|>|<|!=|=|\\+|-|\\*|/|-");
		Matcher bindToMatcher = bindToOtherBind.matcher(keyWordDescNodes);
		int last = 0;
		while (bindToMatcher.find()) {
			String before = keyWordDescNodes.substring(last, bindToMatcher.start());
			if (!before.isEmpty()) {
				temDescNodes.add(new DescNodeText(before, DescNodeType.TEXT));
			}
			String mainBindToSeq = bindToMatcher.group();
			temDescNodes.add(new DescNodeText(mainBindToSeq, DescNodeType.TEXT));
			last = bindToMatcher.end();
		}
		// 处理最后面那一段字符串
		String text = keyWordDescNodes.substring(last);
		if (!text.isEmpty()) {
			temDescNodes.add(new DescNodeText(text, DescNodeType.TEXT));
		}
        return temDescNodes;
	}
	
	private static void testSemanticRepresentationIteratorImpl(String text) {
		System.out.println("\n======\n"+text);
		SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl(text);
        while(it.hasNext()){
        	System.out.println(it.next().getText());
        }
	}
	
	// main方法用于简单测试。
	public static void main(String[] args){
		testSemanticRepresentationIteratorImpl("$1增长>=负$2");
		testSemanticRepresentationIteratorImpl("$1增长>=-$2");
		testSemanticRepresentationIteratorImpl("$1");
		testSemanticRepresentationIteratorImpl("$1增长");
		testSemanticRepresentationIteratorImpl("$1增长$2");
		testSemanticRepresentationIteratorImpl("$1增长>=$2");
		testSemanticRepresentationIteratorImpl("$1增长-$2");
		testSemanticRepresentationIteratorImpl("$1-$2");
		testSemanticRepresentationIteratorImpl("$1$2");
		testSemanticRepresentationIteratorImpl("知识类问句");
	}
}
