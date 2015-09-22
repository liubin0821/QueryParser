package com.myhexin.qparser.fuzzy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

class SortByProductionItemScore implements Comparator<Object>{
    @Override
    public int compare(Object arg0, Object arg1) {
        // TODO Auto-generated method stub
        FuzzyProductionModelItem i1 = (FuzzyProductionModelItem)arg0;
        FuzzyProductionModelItem i2 = (FuzzyProductionModelItem)arg1;
        if( i1.getScore() < i2.getScore() ){
            return 1;
        }else{
            return 0;
        }
    }
}

class FuzzyProductionModelItem {
    private String wordpair_;
    private double score_;
    private int score_level_;

    public FuzzyProductionModelItem(String wordpair, double score) {
        wordpair_ = wordpair;
        score_ = score;
        setScoreLevel(score);
        
    }

    public void setWordPair(String wordpair) {
        wordpair_ = wordpair;
    }

    public void setScore(double score) {
        score_ = score;
        setScoreLevel( score );
    }

    public String getWordPair() {
        return wordpair_;
    }

    public double getScore() {
        return score_;
    }

    public int getScoreLevel() {
        return score_level_;
    }
    
    private void setScoreLevel( double score ){
        if( score < 1.0 ){
            score_level_ = 0;
        }else{
            score_level_ = Long.toString(Math.round(score)).length();
        }
    }
    
    public String toString(){
        String res = String.format("wordpair:%s    score:%s    score_level:%s", wordpair_, score_, score_level_);
        return res;
    }
}

public class FuzzyProductionModel {
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(FuzzySearcher.class.getName());

    public HashMap<String, FuzzyProductionModelItem> prodModelL2Map;
    public HashMap<String, FuzzyProductionModelItem> prodModelL1Map;

    public FuzzyProductionModel() {
        prodModelL2Map = new HashMap<String, FuzzyProductionModelItem>();
        prodModelL1Map = new HashMap<String, FuzzyProductionModelItem>();
    }

    public void fill(ArrayList<String> modelL2List, ArrayList<String> modelL1List) {
        try {
            // first load modelL2
            Iterator<String> iterator = modelL2List.iterator();
            while (iterator.hasNext()) {
                try {
                    String now = iterator.next();
                    now = now.trim();
                    if (now.length() == 0) {
                        continue;
                    }
                    String[] temp = now.split(" ");

                    FuzzyProductionModelItem newFuzzyProdModelItem = new FuzzyProductionModelItem(
                            temp[0], Double.parseDouble(temp[1]));
                    prodModelL2Map.put(newFuzzyProdModelItem.getWordPair(),
                            newFuzzyProdModelItem);
                } catch (Exception e) {
                    logger_.error("illegal production modelL2 file [{}]",
                            e.getMessage());
                }
            }
            // then load modelL1
            iterator = modelL1List.iterator();
            while (iterator.hasNext()) {
                try {
                    String now = iterator.next();
                    now = now.trim();
                    if (now.length() == 0) {
                        continue;
                    }
                    String[] temp = now.split(" ");
                    FuzzyProductionModelItem newFuzzyProdModelItem = new FuzzyProductionModelItem(
                            temp[0], Double.parseDouble(temp[1]));
                    prodModelL1Map.put(newFuzzyProdModelItem.getWordPair(),
                            newFuzzyProdModelItem);
                } catch (Exception e) {
                    logger_.error("illegal production modelL1 file [{}]",
                            e.getMessage());
                }
            }
        } catch (Exception e) {
            logger_.error(e.getMessage());
        }
    }
}
