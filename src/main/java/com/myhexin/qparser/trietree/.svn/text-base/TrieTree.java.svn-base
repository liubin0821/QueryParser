package com.myhexin.qparser.trietree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * trie树
 * 
 * 功能：构建trie树
 */
public class TrieTree {
	private TrieNode root;
	
	public TrieTree() {
		root = new TrieNode();
	}
	
	public TrieTree(String[] patterns) {
		root = new TrieNode();
		insertString(patterns);
		generatePrefix();
	}
	
	private void printTree(TrieNode node, int tabIndex) {
		if(node!=null) {
			if(tabIndex>0) {
				
				for(int i=0;i<tabIndex;i++) {
					System.out.print("\t");
				}
				System.out.print("|");
				System.out.print("----");
			}
			System.out.print(node.value);
			
			if(node.children.isEmpty()==false) {
				Set<Character> kList = node.children.keySet();
				for(Iterator<Character> it = kList.iterator(); it.hasNext();) {
					Character c = it.next();
					TrieNode child =  node.children.get(c);
					if(child!=null) {
						System.out.println();
						printTree(child, tabIndex+1);
					}
				}
			}else{
				if(node.okey.size()>0) {
					System.out.println(node.okey);
				}else{
					System.out.println();
				}
			}
		}
	}
	
	public void printTree() {
		if(root!=null) {
			printTree(root, 0);
		}
	}
	
	public TrieTree(HashSet<String> dict) {
		root = new TrieNode();
		insertString(dict);
		generatePrefix();
	}
	
	/**
     * 在单词树中插入单词
     * @param s:需要插入的单词
     */
    private void insertString(String s){
    	if (s == null || s.equals(""))
    		return ;
    	TrieNode p = root;
        for(int i = 0; i < s.length(); ++i){
            char value = s.charAt(i);
            TrieNode child = getChild(p, value);
            if(child == null){
            	child = new TrieNode();
            	child.father = p;
            	child.value = value;
            	child.prefix = root;
            	p.children.put(value, child);
            }
            p = child;
        }
        if(p.okey == null)
        	p.okey = new LinkedList<String>();
        p.okey.addLast(s);
    }
    
	/**
     * 在单词树中插入单词数组
     * @param patterns:需插入的单词数组
     */
    private void insertString(String[] patterns) {
    	for (String str : patterns)
    		insertString(str);
    }
    
    private void insertString(HashSet<String> dict) {
    	for (String str : dict)
    		insertString(str);
    }
    
    /**
     * 获得字符为value的子节点
     * @param p
     * @param value
     * @return
     */
    private TrieNode getChild(TrieNode p, char value) {
    	if (p == null || p.children == null || p.children.size() == 0)
    		return null;
    	return p.children.get(value);
    }
    
    /**
     * 给一课单词树添加前缀指针
     * 算法：采用广度优先算法遍历树中结点并调用setPrefix设置其前缀指针，
     * 保证给当前结点设置前缀指针时比它深度小的结点的前缀指针都已设置完毕
     */
    private void generatePrefix(){
        LinkedList<TrieNode> l = new LinkedList<TrieNode>();
        l.addLast(root);
        while(l.size() > 0){
        	TrieNode p = (TrieNode)l.removeFirst();
            Iterator iter = p.children.entrySet().iterator();
            while(iter!=null && iter.hasNext()){
            	Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				TrieNode child = (TrieNode)val;
                setPrefix(child);
                l.addLast(child);
            }
        }
    }
    
    /**
     * 设置当前结点的前缀指针
     * 算法：假设当前结点为p
     * q<-p->father->prefix
     * index<-此时q的父亲到q边上字母对应的下标
     * While q!=Root and q->children[index]=null Do
     *     q<-q.prefix
     * If q->children[index]!=null Then p->prefix=q->children[index]
     * 
     */
    private void setPrefix(TrieNode cur){
    	TrieNode q = cur.father.prefix;
        if(q == null)
        	return;
        while(q.father != null && getChild(q, cur.value) == null){
            q = q.prefix;
        }
        if(getChild(q, cur.value) != null){
        	cur.prefix = getChild(q, cur.value);
            if(cur.prefix.okey != null && cur.prefix.okey.size() > 0){
                if(cur.okey == null)
                	cur.okey = new LinkedList<String>();
                cur.okey.addAll(cur.prefix.okey);
            }
        }
    }
    
    /**
     * 多串匹配主算法
     * 算法：
     * create CharPrefixTree
     * q<-root
     * For i<-0 to n-1 Do
     *     index<-text[i]-'a';
     *     while q!=root and q->children[index] Do
     *         q<-q->prefix
     *     If q->children[index]!=null Then q<-q->children[index];
     *     If q->Okey Then Display Matching Message
     * End For
     * 
     * 构造单词前缀树总时间复杂度为O(Sum{Li|Li=patterns[i].length});
     * 多串匹配算法时间复杂度为O(text.length)
     * @param patterns:模式串
     * @param text:正文
     */
    public void mpMatching(String text) {
    	TrieNode q = root;
        for(int i = 0; i < text.length(); ++i){
            char value = text.charAt(i);
            while(q != null && q.father != null && getChild(q, value) == null) {
            	q = q.prefix;
            }
            if(getChild(q, value) != null)
            	q = getChild(q, value);
            if(q != null && q.okey != null && q.okey.size() > 0){
                Iterator iter = q.okey.iterator();
                while(iter.hasNext()){
                    String pattern = (String)iter.next();
                    System.out.println("pattern:" + pattern + " position:" + (i - pattern.length() + 1) + "-" + i);
                }
            }
        }
    }
    
    // 多串匹配，判断是否至少匹配上一个
    public boolean mpMatchingOne(String text) {
    	if(text==null)
    		return false;
    	
    	TrieNode q = root;
        for(int i = 0; i < text.length(); ++i){
            char value = text.charAt(i);
            while(q != null && q.father != null && getChild(q, value) == null) {
            	q = q.prefix;
            }
            if(getChild(q, value) != null)
            	q = getChild(q, value);
            if(q != null && q.okey != null && q.okey.size() > 0){
            	//System.out.println("[Tire Match] " + q.okey + " | " + text);
                return true;
            }
        }
        return false;
    }
    
    // 多串匹配，返回匹配的结果
    public Map<String, List<Bound>> mpMatchingResult(String text) {
    	Map<String, List<Bound>> bounds = new HashMap<String, List<Bound>>();
    	TrieNode q = root;
        for(int i = 0; i < text.length(); ++i){
            char value = text.charAt(i);
            while(q != null && q.father != null && getChild(q, value) == null) {
            	q = q.prefix;
            }
            if(getChild(q, value) != null)
            	q = getChild(q, value);
            if(q != null && q.okey != null && q.okey.size() > 0){
                Iterator iter = q.okey.iterator();
                while(iter.hasNext()){
                    String pattern = (String)iter.next();
                    Bound bound = new Bound((i - pattern.length() + 1), i);
                    if (bounds.containsKey(pattern))
                    	bounds.get(pattern).add(bound);
                    else {
                    	List<Bound> boundList = new ArrayList<Bound>();
                    	boundList.add(bound);
                    	bounds.put(pattern, boundList);
                    }
                    //System.out.println("pattern:" + pattern + " position:" + (i - pattern.length() + 1) + "-" + i);
                }
            }
        }
        return bounds;
    }
    
    public void printMatchingResult(Map<String, List<Bound>> mr) {
        Iterator iter = mr.entrySet().iterator();
        while(iter!=null && iter.hasNext()){
        	Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			List<Bound> bounds = (List<Bound>)val;
			System.out.println((String)key+":"+bounds);
        }
    }
    
    /**
     * 匹配上的边界
     * @author linpeikun
     *
     */
    class Bound {
    	public int start;
    	public int end;
    	public Bound() {
    		
    	}
    	public Bound(int start, int end) {
    		this.start = start;
    		this.end = end;
    	}
    	
    	public String toString() {
    		return start + ":" + end;
    	}
    }
}
