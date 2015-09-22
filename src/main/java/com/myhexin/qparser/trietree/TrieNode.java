package com.myhexin.qparser.trietree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 构建内存词典的Trie树结点
 * 
 * @author linpeikun
 * @version 1.0, 10/22/2013
 */
class TrieNode {
	public TrieNode father = null; // 该结点的父指针
    public TrieNode prefix = null; // 该结点的前缀指针
    public LinkedList<String> okey = null; // 标记该结点是否为模式串的终点,若是则将模式串的引用赋给它
    public char value = (char)0; // 父结点到该结点边上的字符
    // 用一Map存储子节点以便快速查询
    public Map<Character, TrieNode> children = new HashMap<Character, TrieNode>();
	
	public TrieNode(){
	}
	
	public TrieNode(char k){
		this.value=k;
	}
}
