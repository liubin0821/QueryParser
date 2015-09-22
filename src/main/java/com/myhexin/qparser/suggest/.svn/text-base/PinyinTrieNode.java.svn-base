package com.myhexin.qparser.suggest;

import java.util.HashMap;

public class PinyinTrieNode {
    public String pchar_;
    public boolean endAble_;
    public HashMap<String, PinyinTrieNode> sonNodes_;
    
    public PinyinTrieNode( String pchar ){
        pchar_ = pchar;
        endAble_ = false;
        sonNodes_ = new HashMap<String, PinyinTrieNode>();
    }
    public PinyinTrieNode( String pchar, boolean endAble ){
        pchar_ = pchar;
        endAble_ = endAble;
        sonNodes_ = new HashMap<String, PinyinTrieNode>();
    }
}
