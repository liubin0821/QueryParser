package com.myhexin.qparser.fuzzy;

import java.util.ArrayList;

public class FuzzyModel {
    public static FuzzyFilterWordsModel FUZZY_FILTER_WORDS = new FuzzyFilterWordsModel();
    public static FuzzyProductionModel FUZZY_PROD_MODEL = new FuzzyProductionModel();
    public static void loadFuzzyProductionModel( 
            ArrayList<String> fuzzyProductionModelL2List,
            ArrayList<String> fuzzyProductionModelL1List){
        FUZZY_PROD_MODEL.fill(fuzzyProductionModelL2List, fuzzyProductionModelL1List);
    }
    public static void loadFilterWordsModel(
            ArrayList<String> fuzzyFilterWordsList){
        FUZZY_FILTER_WORDS.fill(fuzzyFilterWordsList);
    }
}
