package com.myhexin.qparser.suggest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.PinyinException;
import com.myhexin.qparser.fuzzy.FuzzyAnalysis;
import com.myhexin.qparser.util.Util;

public class TrieTree {
    public static final int MAX_RETURN = 10;
    private static final String RES_SP_CHARS = "><=*";
    private static final int MAX_TREE_COUNT = 2;
    private int nowTreeID = 0;
    private TruckNode[] rootNodes_ = new TruckNode[MAX_TREE_COUNT];
    private WebPinyin webPinyin = new WebPinyin();
    StockTrie stockTrie;

    public static String SENT_SPL_TOKEN = "\t";
    public static String SENT_SPL_TOKEN_PATTERN = "\\t";
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(QuerySuggest.class.getName());

    public TrieTree() {
        for (int i = 0; i < MAX_TREE_COUNT; i++) {
            rootNodes_[i] = new TruckNode();
        }
    }

    public void updateStockList(String file) {
        if (this.stockTrie == null) {
            this.stockTrie = new StockTrie(file);
        } else {
            this.stockTrie.initialize(file);
        }
    }

    /**
     * 初始化
     * 
     * @param tempSentFileName
     *            ：模板文件的名称
     * @param pinyinFileName
     *            ：拼音文件的名称
     */
    public void initial(String tempSentFileName, String pinyinFileName,
            String stockListFile) {
        loadTrieTree(tempSentFileName);
        loadAllPinyin(pinyinFileName);
        this.stockTrie = new StockTrie(stockListFile);
    }

    /**
     * use temple file to init trie tree
     * 
     * @param fileName
     * @throws
     */
    public boolean loadTrieTree(String fileName) {
        try {
            ArrayList<String> list = Util.readTxtFile(fileName);
            return loadTrieTree(list);
        } catch (DataConfException e) {
            logger_.error("Illegal temple file [{}] ", fileName);
            return false;
        } catch( Exception e ){
            String logStr = Util.getExceptionTraceStr(e.getStackTrace());
            logger_.error(logStr);
            return false;
        }
    }

    public boolean loadTrieTree(ArrayList<String> list) {
        try {
            Iterator<String> iterator = list.iterator();
            String now = null;
            int newTreeID = 0;
            synchronized (this) {// 我们所需要保持同步的就是nowTreeID
                newTreeID = (nowTreeID + 1) % MAX_TREE_COUNT;
            }
            TruckNode newTruckNode = new TruckNode();
            rootNodes_[newTreeID] = newTruckNode;
            while (iterator.hasNext()) {
                now = iterator.next();
                String[] tempArray = now.split(WebTempCreater.TEMP_SPL_TOKEN);
                if (tempArray.length != 5) {
                    // log
                    continue;
                }
                SentNode sentNode = new SentNode(tempArray);
                insertIntoTrieTree(sentNode, rootNodes_[newTreeID]);
            }
            sortTrieTree(rootNodes_[newTreeID]);
            synchronized (this) {// 我们所需要保持同步的就是nowTreeID
                nowTreeID = newTreeID;
            }
            return true;
        }catch (Exception e) {
            String logStr = "加载问句TrieTree失败\n";
            String estr = Util.getExceptionTraceStr(e.getStackTrace());
            logger_.error(logStr + estr);
            return false;
        }
    }

    // 要想使用本trie树，就必须初始化拼音切分器WebPinyin
    public void loadAllPinyin(String fileName) {
        webPinyin.loadPinyinTrieTree(fileName);
    }

    public void sortTrieTree(TruckNode nowTruckNode) {
        nowTruckNode.sortSentNodeList();
        Iterator<Entry<String, TruckNode>> iterator = nowTruckNode.sonTruckNodesMap_
                .entrySet().iterator();
        Entry<String, TruckNode> nowEntry = null;
        TruckNode truckNode = null;
        HashSet<String> sortedNode = new HashSet<String>();
        while (iterator.hasNext()) {
            nowEntry = iterator.next();
            truckNode = nowEntry.getValue();
            if (sortedNode.contains(truckNode.key_)) {
                continue;
            } else {
                sortTrieTree(truckNode);
                sortedNode.add(truckNode.key_);
            }
        }
    }

    public void insertIntoTrieTree(TrieNodeAble node, TruckNode rootNode) {
        if (node instanceof SentNode) {
            SentNode sentNode = (SentNode) node;
            int listLen = sentNode.templeSplList_.size();
            TruckNode nowTruckNode = rootNode;// 当前所在的树节点
            TruckNode nextTruckNode = null;// 下一个会遍历到的树节点
            String nowSlice = null;
            String nowPinyinSlice = null;
            // 用来存放拼音前缀子串所对应的TruckNodes，以使我们在补全的过程中可以在即时输入部分拼音也可以补全
            ArrayList<TruckNode> subPinyinSliceNodes = null;
            for (int i = 0; i < listLen; i++) {
                nowSlice = sentNode.templeSplList_.get(i);
                nowPinyinSlice = sentNode.pinyinSplList_.get(i);

                // 判断是否要将该句子加入到该节点当中去，现在我们是遍历到第二个节点才加
                if (nowTruckNode.sonTruckNodesMap_.containsKey(nowSlice)) {
                    nextTruckNode = nowTruckNode.sonTruckNodesMap_
                            .get(nowSlice);
                } else if (nowTruckNode.sonTruckNodesMap_
                        .containsKey(nowPinyinSlice)) {
                    nextTruckNode = nowTruckNode.sonTruckNodesMap_
                            .get(nowPinyinSlice);
                    // 汉字指向其所对应拼音所指向的节点
                    nowTruckNode.sonTruckNodesMap_.put(nowSlice, nextTruckNode);
                } else {
                    TruckNode newTruckNode = new TruckNode();
                    newTruckNode.key_ = nowSlice;
                    nowTruckNode.sonTruckNodesMap_.put(nowSlice, newTruckNode);
                    nowTruckNode.sonTruckNodesMap_.put(nowPinyinSlice,
                            newTruckNode);
                    nextTruckNode = newTruckNode;
                }

                // 依次产生该拼音所对应的前缀子串所对应的truckNode
                subPinyinSliceNodes = new ArrayList<TruckNode>();
                for (int j = 1; j < nowPinyinSlice.length(); j++) {
                    String subPinyinSlice = nowPinyinSlice.substring(0, j);
                    TruckNode subPinyinTruckNode = nowTruckNode.sonTruckNodesMap_
                            .get(subPinyinSlice);
                    if (subPinyinTruckNode == null) {
                        subPinyinTruckNode = new TruckNode();
                        subPinyinTruckNode.key_ = subPinyinSlice;
                    }
                    subPinyinSliceNodes.add(subPinyinTruckNode);
                    nowTruckNode.sonTruckNodesMap_.put(subPinyinSlice,
                            subPinyinTruckNode);
                }

                if (i >= 1) {
                    nextTruckNode.sonSentNodesSet_.add(sentNode);
                    // 为拼音前缀子串truck节点添加指向问句的指针
                    if (null != subPinyinSliceNodes) {
                        for (TruckNode subPinyinTruckNode : subPinyinSliceNodes) {
                            subPinyinTruckNode.sonSentNodesSet_.add(sentNode);
                        }
                    }
                }
                nowTruckNode = nextTruckNode;
            }
        }
    }

    /**
     * 将字符串按照英文切分的形式切分成ArrayList，不管拼音
     * 
     * @param nowQuery
     * @return
     */
    public ArrayList<String> splitEnglish(String nowQuery) {
        return strToListEnglish(nowQuery);
    }

    /**
     * 将字符串按照拼音的切分形式切分为ArrayList
     * 
     * @param nowQuery
     * @return
     */
    public ArrayList<String> splitPinyin(String nowQuery) {
        ArrayList<String> rawList = strToListPinyin(nowQuery);
        ArrayList<String> resList = new ArrayList<String>();

        // 首先将中文和英文的部分切分开
        for (String now : rawList) {
            if (FuzzyAnalysis.isAlphabet(now.charAt(0))) {
                // 如果当前的字符串是字母，试着进行拼音切分
                try {
                    ArrayList<String> tempList = webPinyin.split(now);
                    if (tempList != null) {
                        for (int i = 0; i < tempList.size(); i++) {
                            resList.add(tempList.get(i));
                        }
                    }
                } catch (PinyinException e) {
                    for (int i = 0; i < now.length(); i++) {
                        resList.add(now.substring(i, i + 1));
                    }
                }
            } else {
                // 如果不是英文，则一定是汉字/特殊字符，直接追加
                resList.add(now);
            }
        }

        return resList;
    }

    /**
     * 对于传入的问句字符串列表进行过滤，过滤掉引号之类的特殊字符串
     * 
     * @param list
     * @return
     */
    public ArrayList<String> specialCharacterFilter(ArrayList<String> list) {
        ArrayList<String> rlt = new ArrayList<String>();
        for (String str : list) {
            str = str.replace("\"", "");
            rlt.add(str);
        }
        return rlt;
    }

    /**
     * 问句补全的主体
     * 
     * @param list
     *            用于补全匹配的list
     * @param originalQuery
     *            用户输入的最原始问句
     * @param prefixTransSB
     *            根据用户的输入，在进行补全遍历的过程当中将拼音恢复成其所对应的汉字
     * @return
     */
    public ArrayList<String> completeBody(ArrayList<String> list,
            String originalQuery) {
        // 补全的结果
        ArrayList<String> res = new ArrayList<String>();

        TruckNode nowTruckNode = rootNodes_[nowTreeID];
        TruckNode nextTruckNode = null;
        String nowSplStr = null;

        for (int i = 0; i < list.size(); i++) {
            nowSplStr = list.get(i);
            if (nowSplStr == null || nowSplStr.isEmpty()) {
                continue;
            }

            if (nowTruckNode.sonTruckNodesMap_.containsKey(nowSplStr)) {
                nextTruckNode = nowTruckNode.sonTruckNodesMap_.get(nowSplStr);
            } else {
                // 没有找到了，返回空的结果集
                return res;
            }

            // use a map to filter
            HashSet<String> filtSet = new HashSet<String>();
            ArrayList<String> accurateList = new ArrayList<String>();// 完全以用户输入开头的问句列表
            ArrayList<String> fuzzyList = new ArrayList<String>();// 不完全以用户输入开头的问句列表

            // 仅当遍历到用户输入串结尾的时候，才将其所对应的句子添加进来
            if (i == list.size() - 1) {
                Iterator<SentNode> iterator = nextTruckNode.sonSentNodeList_
                        .iterator();
                SentNode nowSentNode = null;

                // 先将和用户问句完全匹配的找出来
                while (iterator.hasNext()) {
                    nowSentNode = iterator.next();
                    // 每个模板只给出一个句子
                    if (filtSet.contains(nowSentNode.temple_)) {
                        continue;
                    }

                    if (nowSentNode.sentence_.startsWith(originalQuery)) {
                        accurateList.add(nowSentNode.sentence_);
                        filtSet.add(nowSentNode.temple_);
                    } else {
                        fuzzyList.add(nowSentNode.sentence_);
                    }
                }
                // 凑够10个提示句子
                final int maxCount = 10;
                int nowCount = 0;
                // 先添加前缀完全匹配的句子中
                for (int j = 0; j < accurateList.size(); j++) {
                    if (nowCount >= 10) {
                        break;
                    }
                    res.add(accurateList.get(j));
                    nowCount++;
                }
                // 再从前缀不完全匹配的句子中挑选出一些句子附加上去
                int j = 0;
                int jumpLen = fuzzyList.size() / 10;
                jumpLen = (jumpLen <= 0) ? 1 : jumpLen;
                while ((j < fuzzyList.size()) && (nowCount < maxCount)) {
                    res.add(fuzzyList.get(j));
                    nowCount++;
                    j += jumpLen;
                }
            }
            nowTruckNode = nextTruckNode;
        }
        return specialCharacterFilter(res);
    }

    /**
     * 考虑拼音的补全
     * 
     * @param nowQuery
     * @return
     */
    public ArrayList<String> pinyinComplete(String nowQuery) {
        ArrayList<String> list = splitPinyin(nowQuery);
        return completeBody(list, nowQuery);
    }

    /**
     * 不考虑拼音的补全
     * 
     * @param nowQuery
     * @return
     */
    public ArrayList<String> simpleComplete(String nowQuery) {
        ArrayList<String> list = splitEnglish(nowQuery);
        return completeBody(list, nowQuery);
    }

    public String complete(String nowQuery) {
        // step1: 首先进行大写转小写
        String lowerCaseQuery = nowQuery.toLowerCase();

        ArrayList<String> resPinyin = null;
        ArrayList<String> resSimple = null;
        // step2: 首先试着进行拼音补全
        resPinyin = pinyinComplete(lowerCaseQuery);
        // step3: 再进行简单补全
        resSimple = simpleComplete(lowerCaseQuery);

        HashSet<String> ret = new HashSet<String>();
        if (resPinyin != null) {
            ret.addAll(resPinyin);
        }
        if (resSimple != null) {
            ret.addAll(resSimple);
        }
        if (ret.size() < MAX_RETURN) {
            List<String> stockSuggestions = this.stockTrie
                    .getSuggestion(lowerCaseQuery);
            if (stockSuggestions != null) {
                ret.addAll(stockSuggestions.subList(0, MAX_RETURN - ret.size()));
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String sentence : ret) {
            sb.append(sentence);
            sb.append(SENT_SPL_TOKEN);
        }
        // trim tailing SENT_SPL_TOKEN
        return sb.toString().replaceAll(SENT_SPL_TOKEN_PATTERN + "*$", "");
    }

    /**
     * 在拼音切分当中会使用到的将字符串转换为切分模板 逻辑：中文单字切分，英文联合切分，数字忽略，其它字符值仅保留RES_SP_CHARS中的字符
     * 
     * @param ustr
     * @return
     */
    public static ArrayList<String> strToListPinyin(String ustr) {
        ArrayList<String> res = new ArrayList<String>();
        StringBuilder tmp = new StringBuilder();
        //
        for (int i = 0; i < ustr.length(); i++) {
            char uchar = ustr.charAt(i);
            if (FuzzyAnalysis.isChinese(uchar)) {
                // 是汉字
                if (tmp.length() > 0) {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
                res.add(Character.toString(uchar));
            } else if (FuzzyAnalysis.isAlphabet(uchar)) {
                // 是英文
                tmp.append(uchar);
            } else if (FuzzyAnalysis.isNumber(uchar)) {
                // 是数字
                if (tmp.length() > 0) {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
            } else {
                // 是特殊字符
                if (tmp.length() > 0) {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
                if (RES_SP_CHARS.indexOf(uchar) > 0) {
                    // 是可保留的特殊字符
                    res.add(Character.toString(uchar));
                } else {
                    // 是不可保留的特殊字符,什么都不做
                }
            }
        }
        if (tmp.length() > 0) {
            res.add(tmp.toString());
        }
        return res;
    }

    /**
     * 在英文切分当中会使用到的将字符串转换为切分模板 逻辑：中文单字切分，英文单独切分，数字忽略，其它字符值仅保留RES_SP_CHARS中的字符
     * 
     * @param ustr
     * @return
     */
    public static ArrayList<String> strToListEnglish(String ustr) {
        ArrayList<String> res = new ArrayList<String>();
        StringBuilder tmp = new StringBuilder();
        //
        for (int i = 0; i < ustr.length(); i++) {
            char uchar = ustr.charAt(i);
            if (FuzzyAnalysis.isChinese(uchar)) {
                // 是汉字
                if (tmp.length() > 0) {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
                res.add(Character.toString(uchar));
            } else if (FuzzyAnalysis.isAlphabet(uchar)) {
                // 是英文
                if (tmp.length() > 0) {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
                res.add(Character.toString(uchar));
            } else if (FuzzyAnalysis.isNumber(uchar)) {
                // 是数字
                if (tmp.length() > 0) {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
            } else {
                // 是特殊字符
                if (tmp.length() > 0) {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
                if (RES_SP_CHARS.indexOf(uchar) > 0) {
                    // 是可保留的特殊字符
                    res.add(Character.toString(uchar));
                } else {
                    // 是不可保留的特殊字符,什么都不做
                }
            }
        }
        if (tmp.length() > 0) {
            res.add(tmp.toString());
        }
        return res;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        org.apache.log4j.PropertyConfigurator
                .configure("./conf/log4j.properties");

        try {
            FileReader r = new FileReader("./data/web_tempsentence.txt");
            BufferedReader br = new BufferedReader(r);
            String now = null;
            ArrayList<String> array = new ArrayList<String>();
            while ((now = br.readLine()) != null) {
                array.add(now);
            }
            TrieTree newTrieTree = new TrieTree();
            newTrieTree.loadTrieTree(array);
            newTrieTree.loadAllPinyin("./data/allpinyin.txt");
            newTrieTree.updateStockList("./data/stock_list.txt");
            // quick test roe, ro, shiying, 市盈, 2009年, 200
            // System.out.println(newTrieTree.complete("市"));//应该不出来
            // System.out.println(newTrieTree.complete("市盈"));
            String[] cases = new String[] { "heil", "qiqi", "黑龙", "600200" };
            for (String test : cases) {
                System.out.println("original: [" + test + "] output:"
                        + newTrieTree.complete(test));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
