package com.myhexin.qparser.suggest2;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

//import com.myhexin.suggest.HelloWorld ;
public class SingleCharTokenizerTest {

    @Before
    public void initn() {

    }

    @Test
    public void test1() {
        String str = "在2022年12mo.nth上市+的天井greateライ*ト123456中";
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> expectList = new ArrayList<String>();
        expectList.add("在");
        expectList.add("2022");
        expectList.add("年");
        expectList.add("12");
        expectList.add("mo");
        expectList.add(".");
        expectList.add("nth");
        expectList.add("上");
        expectList.add("市");
        expectList.add("+");
        expectList.add("的");
        expectList.add("天");
        expectList.add("井");
        expectList.add("greate");
        expectList.add("ラ");
        expectList.add("イ");
        expectList.add("*");
        expectList.add("ト");
        expectList.add("123456");
        expectList.add("中");
        SingleCharTokenizer.tokenize(str, list);
        assertTrue( list.size() == expectList.size() );
        for( int i = 0; i < list.size(); i++ ){
            assertTrue( list.get(i).equals(expectList.get(i)) );
        }
    }
}
