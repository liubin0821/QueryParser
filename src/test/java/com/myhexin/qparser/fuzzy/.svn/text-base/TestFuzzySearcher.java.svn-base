package com.myhexin.qparser.fuzzy;

import static org.junit.Assert.*;
import org.junit.Test;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.util.Util;

public class TestFuzzySearcher {
    @Test 
    public void rescoreTest(){
        String firstStr = "上证A股";
        String s1Str = "上证B股";
        String s2Str = "上证AB股";
        double sScore = FuzzyScoreFactory.rescoreDensity(firstStr, firstStr );
        double s1Score = FuzzyScoreFactory.rescoreDensity(firstStr, s1Str );
        double s2Score = FuzzyScoreFactory.rescoreDensity(firstStr, s2Str );
        assertTrue( sScore > s2Score );
        assertTrue( s2Score > s1Score );
    }
}
