package com.myhexin.qparser.suggest;

import java.util.ArrayList;
import java.util.Comparator;

class SentNodeSortByScoreCom implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        SentNode sentNode1 = (SentNode) o1;
        SentNode sentNode2 = (SentNode) o2;
        if (sentNode1.score_ > sentNode2.score_) {
            return -1;
        } else {
            return 1;
        }
    }
}

class SentNodeSortByLenCom implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        SentNode sentNode1 = (SentNode) o1;
        SentNode sentNode2 = (SentNode) o2;

        if (sentNode1.sentence_.length() > sentNode2.sentence_.length()) {
            return 1;
        } else {
            return -1;
        }
    }
}

public class SentNode implements TrieNodeAble, Cloneable {
    public String temple_;
    public ArrayList<String> templeSplList_;
    public String pinyinTemple_;
    public ArrayList<String> pinyinSplList_;
    public String patternTemple_;
    public ArrayList<String> patternSplList_;
    public ArrayList<String> indexList_;//用来存放问句中的指标名称 
    public String sentence_;
    public double score_;

    public SentNode() {
        temple_ = "";
        templeSplList_ = new ArrayList<String>();
        pinyinTemple_ = "";
        pinyinSplList_ = new ArrayList<String>();
        patternTemple_ = "";
        patternSplList_ = new ArrayList<String>();
        indexList_ = new ArrayList<String>();
        sentence_ = "";
        score_ = 0.0;
    }

    public SentNode(String[] paraments) {
        if (paraments.length != 5) {
            return;
        }
        temple_ = paraments[0];
        templeSplList_ = new ArrayList<String>();
        setTempleSplList_(temple_);
        
        pinyinTemple_ = paraments[1];
        pinyinSplList_ = new ArrayList<String>();
        setPinyinSplList_(pinyinTemple_);
        
        patternTemple_ = paraments[2];
        patternSplList_ = new ArrayList<String>();
        setPatternSplList(patternTemple_);
        
        indexList_ = new ArrayList<String>();
        setIndexList( patternSplList_ );
        	
        sentence_ = paraments[3];
        score_ = Double.parseDouble(paraments[4]);
    }

    private void setIndexList(ArrayList<String> patternSplList ) {
		// TODO Auto-generated method stub
    	 for( String pstr : patternSplList ){
         	if( pstr.startsWith(WebTempCreater.CLASS_MARKER ) ){
         		indexList_.add(pstr.substring(WebTempCreater.CLASS_MARKER.length(), pstr.length()));
         	}
         }
	}

	public SentNode(String temple, String tempPinYin, String tempPattern, String sentence,
            double score) {
        temple_ = temple;
        templeSplList_ = new ArrayList<String>();
        setTempleSplList_(temple);
        
        pinyinTemple_ = tempPinYin;
        pinyinSplList_ = new ArrayList<String>();
        setPinyinSplList_(pinyinTemple_);
        
        patternTemple_ = tempPattern;
        patternSplList_ = new ArrayList<String>();
        setPatternSplList(patternTemple_);
        
        indexList_ = new ArrayList<String>();
        setIndexList( patternSplList_ );
        
        sentence_ = sentence;
        score_ = score;
    }

    public ArrayList<String> getTempleSplList_() {
        return templeSplList_;
    }

    public void setTempleSplList_(String temple) {
        String[] temp = temple.split("");
        if (temp != null) {
            String nowStr = null;
            for (int i = 0; i < temp.length; i++) {
                nowStr = temp[i];
                if (nowStr != null && !nowStr.isEmpty()) {
                    templeSplList_.add(nowStr);
                }
            }
        }
    }

    public void setTempleSplList_() {
        String[] temp = temple_.split("");
        if (temp != null) {
            String nowStr = null;
            for (int i = 0; i < temp.length; i++) {
                nowStr = temp[i];
                if (nowStr != null && !nowStr.isEmpty()) {
                    templeSplList_.add(nowStr);
                }
            }
        }
    }

    public void setPinyinSplList_(){
        String[] temp = pinyinTemple_.split(WebTempCreater.PINYIN_SPL_TOKEN);
        if( temp != null ){
            String nowStr = null;
            for( int i = 0; i < temp.length; i++ ){
                nowStr = temp[i];
                if( nowStr != null && !nowStr.isEmpty() ){
                    pinyinSplList_.add(nowStr);
                }
            }
        }
    }
    
    public void setPinyinSplList_( String pinyinTempStr ){
        String[] temp = pinyinTempStr.split(WebTempCreater.PINYIN_SPL_TOKEN);
        if( temp != null ){
            String nowStr = null;
            for( int i = 0; i < temp.length; i++ ){
                nowStr = temp[i];
                if( nowStr != null && !nowStr.isEmpty() ){
                    pinyinSplList_.add(nowStr);
                }
            }
        }
    }
    
    public void setPatternSplList(){
        String[] temp = patternTemple_.split(WebTempCreater.PATTERN_SPL_TOKEN);
        if( temp != null ){
            String nowStr = null;
            for( int i = 0; i < temp.length; i++ ){
                nowStr = temp[i];
                if( nowStr != null && !nowStr.isEmpty() ){
                    patternSplList_.add(nowStr);
                }
            }
        }
    }
    
    public void setPatternSplList( String patternTempStr ){
        String[] temp = patternTempStr.split(WebTempCreater.PATTERN_SPL_TOKEN);
        if( temp != null ){
            String nowStr = null;
            for( int i = 0; i < temp.length; i++ ){
                nowStr = temp[i];
                if( nowStr != null && !nowStr.isEmpty() ){
                    patternSplList_.add(nowStr);
                }
            }
        }
    }
    
    public SentNode clone() {
        SentNode newNode = new SentNode();
        
        newNode.temple_ = temple_;
        newNode.setTempleSplList_(temple_);
        
        newNode.pinyinTemple_ = pinyinTemple_;
        newNode.setPinyinSplList_(newNode.pinyinTemple_);
        
        newNode.patternTemple_ = patternTemple_;
        newNode.setPatternSplList(newNode.patternTemple_);
        
        newNode.score_ = score_;
        newNode.sentence_ = sentence_;
        
        return newNode;
    }
}
