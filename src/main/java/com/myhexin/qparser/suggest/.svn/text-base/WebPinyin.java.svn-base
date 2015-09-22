package com.myhexin.qparser.suggest;

import java.util.ArrayList;
import java.util.HashSet;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.PinyinException;
import com.myhexin.qparser.util.Util;

public class WebPinyin {
    private static final String pyInitConsStrs = "b|p|m|f|d|t|n|l|g|k|h|j|q|x|'|zh|ch|sh|r|z|c|s|y|w";
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(QuerySuggest.class.getName());

    private static final int MaxTreeCount = 2;
    private ArrayList<PinyinTrieTree> pyttHolder;
    private HashSet<String> pyInitCons;

    private int nowTrieTreeID = 0;

    public WebPinyin() {
        pyttHolder = new ArrayList<PinyinTrieTree>();
        for (int i = 0; i < MaxTreeCount; i++) {
            PinyinTrieTree pytt = new PinyinTrieTree();
            pyttHolder.add(pytt);
        }
        pyInitCons = new HashSet<String>();
        loadPinyinInitCons(pyInitCons);
    }

    public void loadPinyinInitCons(HashSet<String> pyInitCons) {
        String[] strs = pyInitConsStrs.split("\\|");
        for (int i = 0; i < strs.length; i++) {
            pyInitCons.add(strs[i]);
        }
    }

    public void loadPinyinTrieTree(String fileName) {
        try {
            ArrayList<String> allPinyinList = Util.readTxtFile(fileName);
            loadPinyinTrieTree(allPinyinList);
        } catch (DataConfException e) {
            // TODO Auto-generated catch block
            logger_.error(String.format("无法装载拼音文件:%s", e.getMessage()));
        }
    }

    //拼音trie树要保证同步
    public void loadPinyinTrieTree(ArrayList<String> list) {
        int newTrieTreeID = 0;
        synchronized(this){//我们只需要同步的是nowTrieTreeID
            newTrieTreeID = (nowTrieTreeID + 1)%MaxTreeCount;
        }
        PinyinTrieTree newPinyinTrieTree = new PinyinTrieTree();
        pyttHolder.set(newTrieTreeID, newPinyinTrieTree);
        for (String line : list) {
            line.trim();
            insertIntoPinyinTrieTree(line, pyttHolder.get(newTrieTreeID));
        }
        synchronized(this){
            nowTrieTreeID = newTrieTreeID;
        }
    }

    public void insertIntoPinyinTrieTree(String pyline, PinyinTrieTree pytt) {
        if (pyline == null || pyline.isEmpty()) {
            return;
        }
        pytt.insertStr(pyline);
    }

    public boolean splitAble(String pinyinStr) {
        int len = pinyinStr.length();
        boolean res = true;
        for (int i = 0; i < len; i++) {
            char nowChar = pinyinStr.charAt(i);
            if (('a' > nowChar || nowChar > 'z') && nowChar != '\'') {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 判断输入的字串是否是有效的全拼子串
     * 
     * @param list
     * @param start
     * @param end
     * @return
     */
    public boolean isAvailableFullPinyinStr(String[] list, int start, int end) {
        boolean res = true;
        PinyinTrieNode nowNode = pyttHolder.get(nowTrieTreeID).root_;
        PinyinTrieNode nextNode = null;

        for (int p = start; p <= end; p++) {
            if (nowNode.sonNodes_.containsKey(list[p])) {
                nextNode = nowNode.sonNodes_.get(list[p]);
                nowNode = nextNode;
            } else {
                res = false;
                break;
            }
            if ((p == end) && (!nextNode.endAble_)) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 判断该部分拼音串是否是有效的拼音串或拼音字串
     * 
     * @param list
     *            整个字串
     * @param start
     *            判断的起始位置
     * @param end
     *            判断的结束位置
     * @return
     */
    public boolean isAvailablePinyinStr(String[] list, int start, int end) {
        boolean res = true;
        PinyinTrieNode nowNode = pyttHolder.get(nowTrieTreeID).root_;
        PinyinTrieNode nextNode = null;

        for (int p = start; p <= end; p++) {
            if (nowNode.sonNodes_.containsKey(list[p])) {
                nextNode = nowNode.sonNodes_.get(list[p]);
                nowNode = nextNode;
            } else {
                res = false;
                break;
            }
        }
        return res;
    }

    public boolean isPinyinIni(String[] list, int p) {
        return pyInitCons.contains(list[p]);
    }

    // must be call after loadPinyinTrieTree
    public ArrayList<String> split(String pinyinStr) throws PinyinException {
        if (pinyinStr == null || pinyinStr.isEmpty()) {
            return null;
        }
        // 检测一下是否可以切分
        if (!splitAble(pinyinStr)) {
            return null;
        }

        // 用来存放结果
        ArrayList<String> retStrs = new ArrayList<String>();
        // 用来存储输出的音节切分
        String[] raw = pinyinStr.replaceAll("\\s*", "").split("");
        StringBuilder sb = new StringBuilder();

        int pr = 0;// 原始输入串的位置
        int ppr = 0;// 原始输入串前一个切分串的尾位置

        int state = 0;
        while (true) {
            if (state == 0) {
                state = 1;
                continue;
            } else if (state == 1) {
                // 检验是否可以读入一个字符
                if (pr < raw.length-1) {
                    pr++;
                }else{
                    //解析完成，退出
                    retStrs.add(sb.toString());
                    break;
                }
                if ((raw[pr].equals("i") || raw[pr].equals("u") || raw[pr]
                        .equals("v")) && (sb.length() == 0)) {
                    // 判断是否是不可能的起始串
                    state = 12;
                    continue;
                } else if (isAvailablePinyinStr(raw, ppr + 1, pr)) {
                    // 当前的串是有效的串
                    state = 13;
                    continue;
                } else if (isPinyinIni(raw, pr)) {
                    // 最后输入的是声母
                    // 判断该声母前拼音串是否是有效，如果有效则在该声母前断开
                    if (isAvailablePinyinStr(raw, ppr + 1, pr - 1)) {
                        // 断开
                        retStrs.add(sb.toString());
                        sb = new StringBuilder();
                        ppr = pr-1;
                        state = 13;
                        continue;
                    } else {
                        // 跳到出错
                        state = 12;
                        continue;
                    }
                } else {
                    // 最后的输入不是声母
                    state = 6;
                    continue;
                }
            } else if (state == 6) {
                // 判断拼音串的倒数第二个字母是否是g, r, n
                if (!(raw[pr - 1].equals("g") || raw[pr - 1].equals("r") || raw[pr - 1].equals("n"))) {
                    state = 11;
                    continue;
                } else {
                    state = 7;
                    continue;
                }
            } else if( state == 7 ){
              //判断g,r,n前的拼音是否为有效拼音
                if (isAvailableFullPinyinStr(raw, ppr+1, pr - 2)) {
                    state = 8;
                    continue;
                } else {
                    //g, n, r其边的不可以组成一个合法的拼音串或子串
                    state = 10;
                    continue;
                }
            }else if( state == 8 ){
                if (isAvailablePinyinStr(raw, pr - 1, pr)) {
                    // g,n,r和其后边的可以组成一个合法的拼音串或子串，在其前切分
                    sb.delete(sb.length() - 1, sb.length());
                    retStrs.add(sb.toString());
                    ppr = pr-2;
                    sb = new StringBuilder();
                    sb.append(raw[pr - 1]);
                    state = 13;
                    continue;
                }else{
                    state = 9;
                    continue;
                }
            }else if( state == 9 ){
                //如果g,r,n前面的拼音为有效拼音，但g,r,n和其最后的字符组成的拼音无效，判断是否可以在g,r,n后面断开
                if( isAvailableFullPinyinStr( raw, ppr+1, pr-1) ){
                  //有效，断开
                    retStrs.add(sb.toString());
                    ppr = pr-1;
                    sb = new StringBuilder();
                    state = 13;
                    continue;
                }else{
                    state = 12;
                    continue;
                }
            }else if( state == 10 ){
                //当前状态说明之前的字串无效，我们看一下加上g，n，r是否有效
                if( isAvailableFullPinyinStr(raw, ppr+1, pr-1) ){
                    //有效，断开
                    retStrs.add(sb.toString());
                    ppr = pr-1;
                    sb = new StringBuilder();
                    state = 13;
                    continue;
                }else{
                    //无效，出错
                    state = 12;
                    continue;
                }
            }
            else if (state == 11) {
                // 判断是否可以从倒数第二个字符后切分
                if( isAvailableFullPinyinStr(raw, ppr+1, pr -1) ){
                    //可以切分
                    retStrs.add(sb.toString());
                    ppr = pr-1;
                    sb = new StringBuilder();
                    state = 13;
                    continue;
                }else{
                    state = 12;
                }
            } else if (state == 12) {
                // 输入出错,非合法的拼音串,单独划分
                PinyinException exception = new PinyinException("");
                throw exception;
            } else if (state == 13) {
                // 接受当前字符，跳转到状态1
                sb.append(raw[pr]);
                state = 1;
                continue;
            }
        }
        return retStrs;
    }

    public static void main(String[] args) {
        WebPinyin webPinyin = new WebPinyin();
        webPinyin.loadPinyinTrieTree("./data/allpinyin.txt");
        
        ArrayList<String> res;
        try {
            res = webPinyin.split("dagud");
            for( int i = 0; i < res.size(); i++ ){
                System.out.println(res.get(i));
            }
        } catch (PinyinException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
