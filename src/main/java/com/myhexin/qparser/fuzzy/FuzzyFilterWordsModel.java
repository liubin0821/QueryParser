package com.myhexin.qparser.fuzzy;

import java.util.ArrayList;
import java.util.HashMap;

public class FuzzyFilterWordsModel {
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(FuzzySearcher.class.getName());

    public HashMap<String, Integer> skipMap;
    public HashMap<String, Integer> halfSkipMap;

    public FuzzyFilterWordsModel() {
        skipMap = new HashMap<String, Integer>();
        halfSkipMap = new HashMap<String, Integer>();
    }

    public void fill(ArrayList<String> list) {
        try {
            String ls = System.getProperty("line.separator");
            for (String line : list) {
                line = line.replace(ls, "");
                if (line.startsWith("skiplist")) {
                    String[] strs = line.split(":");
                    if (strs.length > 1) {
                        String paramStr = strs[1];
                        String[] params = paramStr.split("\\|");
                        for (int i = 0; i < params.length; i++) {
                            skipMap.put(params[i], 1);
                        }
                    }
                } else if (line.startsWith("halfskiplist")) {
                    String[] strs = line.split(":");
                    if (strs.length > 1) {
                        String[] params = strs[1].split("|");
                        for (int i = 0; i < params.length; i++) {
                            halfSkipMap.put(params[i], 1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger_.error("illegal fuzzy filter words file [{}]",
                    e.getMessage());
        }
    }
}
