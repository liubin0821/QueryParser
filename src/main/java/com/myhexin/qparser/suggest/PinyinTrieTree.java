package com.myhexin.qparser.suggest;

public class PinyinTrieTree {
    public final static String allpinyin = "abcdefghigklmnopqrstuvwxyz";
    public PinyinTrieNode root_;
    public final static String rootPC_ = "#";
    
    public PinyinTrieTree(){
        root_ = new PinyinTrieNode(rootPC_);
    }
    
    public void insertStr( String line ){
        int lineLen = line.length();
        PinyinTrieNode nowNode = root_;
        PinyinTrieNode next = null;
        for( int i = 0; i < lineLen; i++ ){
            String nowChar = Character.toString(line.charAt(i));
            if( nowNode.sonNodes_.containsKey(nowChar) ){
                next = nowNode.sonNodes_.get(nowChar);
            }else{
                next = new PinyinTrieNode(nowChar);
                if( i == lineLen - 1 ){
                    next.endAble_ = true;
                }
                nowNode.sonNodes_.put(nowChar, next);
            }
            nowNode = next;
        }
    }
}
