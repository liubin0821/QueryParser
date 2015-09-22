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

public class TestTrieTree {
    TrieTree trieTree_;
    @Before
    public void initPinyinEvn() {
        TrieTree newTrieTree = new TrieTree();
        newTrieTree.initial("./data/web_tempsentence.txt",
                "./data/allpinyin.txt", "./data/stock_list.txt");
        trieTree_ = newTrieTree;
    }

    // test part of english，应该有结果
    @Test
    public void test1() {
            String res = trieTree_.complete("ro");
            assertTrue(res.length() > 0);
    }
    
    // tests part of pinyin 应该有结果
    @Test
    public void test2() {
            String res = trieTree_.complete("shiyi");
            assertTrue(res.length() > 0);
    }
    
    //测试全部拼音 应该有结果
    @Test
    public void test3() {
            String res = trieTree_.complete("shiying");
            assertTrue(res.length() > 0);
    }
    
    //测试数字加汉字，应该有结果
    @Test
    public void test4() {
            String res = trieTree_.complete("2008年市盈");
            assertTrue(res.length() > 0);
    }
    
    //测试数字加拼音，应该有结果
    @Test
    public void test5() {
            String res = trieTree_.complete("2008nianshi");
            assertTrue(res.length() > 0);
    }
    
    //测试数字加汉字加拼音加汉字，应该有结果
    @Test
    public void test6() {
            String res = trieTree_.complete("2008nian市ying");
            assertTrue(res.length() > 0);
    }
    
    //测试英文加拼音，应该没有结果
    @Test
    public void test7() {
            String res = trieTree_.complete("roedayu");
            assertTrue(res.length() == 0);
    }
    
    //测试全汉字和数字时的排序，要将和输入相同起始串的排在前边
    @Test
    public void test8() {
            String res = trieTree_.complete("2012年新发");
            assertTrue(res.length()  > 0);
            res.startsWith("2012");
    }
    //测试拼音前缀，应该可以获得数据
    @Test
    public void test9(){
        String res = trieTree_.complete("dagud");
        assertTrue( res.length() > 0 );
        res = trieTree_.complete("dagudo");
        assertTrue( res.length() > 0 );
        res = trieTree_.complete("dagudon");
        assertTrue( res.length() > 0 );
        res = trieTree_.complete("dagudong");
        assertTrue( res.length() > 0 );
    }
}
