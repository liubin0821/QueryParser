package com.myhexin.qparser;

import java.util.ArrayList;

import junit.framework.TestCase;

public class TestGenQID extends TestCase {
    
    public void testGenQID() {

    }
    
    private class TestThread extends Thread {
        ArrayList<String> rlt_ = null;
        
        public TestThread(ArrayList<String> rlt) {
            rlt_ = rlt;
        }
        
        @Override
        public void run() {
            for(int i = 0; i < 100; i++) {
                rlt_.add(makeQID_());
            }
        }
    }
    
    private static String makeQID_() {
        String ms = String.valueOf(System.currentTimeMillis());
        String ns = String.valueOf(System.nanoTime());
        String qid = ms.substring(1) + ns.substring(ns.length()-3);
        return qid;
    }
}
