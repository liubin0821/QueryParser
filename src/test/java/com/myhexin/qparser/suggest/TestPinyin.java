package com.myhexin.qparser.suggest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.myhexin.qparser.except.PinyinException;

public class TestPinyin {
    WebPinyin webPinyin_;
    
    @Before
    public void initPinyinEvn(){
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
            
            WebPinyin webPinyin = new WebPinyin();
            webPinyin.loadPinyinTrieTree("./data/allpinyin.txt");
            webPinyin_ = webPinyin;
        } catch (FileNotFoundException e ) {
            e.printStackTrace();
        }catch( IOException e){
            e.printStackTrace();
        }
    }
    
    //simple test
    @Test
    public void test1(){
        try {
            ArrayList<String> res = webPinyin_.split("shiyinglv");
            int aimSize = 3;
            String part1 = "shi";
            String part2 = "ying";
            String part3 = "lv";
            
            boolean sizeRes = (res.size() == aimSize);
            assertTrue(sizeRes);
            boolean p1Res = (res.get(0).equals(part1));
            assertTrue(p1Res);
            boolean p2Res = (res.get(1).equals(part2));
            assertTrue(p2Res);
            boolean p3Res = (res.get(2)).equals(part3);
            assertTrue(p3Res);
        } catch (PinyinException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    //test g r n
    public void test2(){
        try {
            ArrayList<String> res = webPinyin_.split("woainizhonghuarenmingongheguo");
            int aimSize = 10;
            String part0 = "wo";
            String part1 = "ai";
            String part2 = "ni";
            String part3 = "zhong";
            String part4 = "hua";
            String part5 = "ren";
            String part6 = "min";
            String part7 = "gong";
            String part8 = "he";
            String part9 = "guo";
            
            assertTrue(res.size() == aimSize);
            assertTrue(res.get(0).equals(part0));
            assertTrue(res.get(1).equals(part1));
            assertTrue((res.get(2)).equals(part2));
            assertTrue(res.get(3).equals(part3));
            assertTrue(res.get(4).equals(part4));
            assertTrue((res.get(5)).equals(part5));
            assertTrue(res.get(6).equals(part6));
            assertTrue(res.get(7).equals(part7));
            assertTrue((res.get(8)).equals(part8));
            assertTrue((res.get(9)).equals(part9));
        } catch (PinyinException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
  //test pi'ao
    public void test3(){
        try {
            ArrayList<String> res = webPinyin_.split("pi'ao");
            int aimSize = 2;
            String part0 = "pi";
            String part1 = "ao";
            
            assertTrue(res.size() == aimSize);
            assertTrue(res.get(0).equals(part0));
            assertTrue(res.get(1).equals(part1));
            
            res = webPinyin_.split("piao");
            aimSize = 1;
            part0 = "piao";
            assertTrue(res.size() == aimSize);
            assertTrue(res.get(0).equals(part0));
        } catch (PinyinException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
