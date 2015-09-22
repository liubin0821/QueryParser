package com.myhexin.qparser.suggest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class TruckNode implements TrieNodeAble {
    public String key_;
    public HashMap<String, TruckNode> sonTruckNodesMap_;// 用来存放其后续节点
    public HashSet<SentNode> sonSentNodesSet_;// 用来存放到此处所可以推荐出来的句子
    public ArrayList<SentNode> sonSentNodeList_;// 用来存放排完序后的句子

    public TruckNode() {
        key_ = "";
        sonTruckNodesMap_ = new HashMap<String, TruckNode>();
        sonSentNodesSet_ = new HashSet<SentNode>();
        sonSentNodeList_ = new ArrayList<SentNode>();
    }

    public void sortSentNodeList() {
        Iterator<SentNode> iterator = sonSentNodesSet_.iterator();
        SentNode nowSentNode = null;
        while (iterator.hasNext()) {
            nowSentNode = iterator.next();
            sonSentNodeList_.add(nowSentNode);
        }
        SentNodeSortByLenCom com = new SentNodeSortByLenCom();
        try {
            Collections.sort(sonSentNodeList_, com);
        } catch (java.lang.IllegalArgumentException e) {
            //this exception don't affect the performance
        }
    }
}
