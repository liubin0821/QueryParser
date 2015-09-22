package com.myhexin.qparser.smart;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartAnswerEntry {
    //触发当前智能回答的问句需要符合的正则Pattern
    public ArrayList<Pattern> rgxPtnArray_;
    //符合此pattern的智能答案的列表，我们可以将Pattern中匹配到的Group替换到每一个答案中去
    public ArrayList<String> aswArray_;
    private Random random_;
    
    public SmartAnswerEntry(){
        rgxPtnArray_ = new ArrayList<Pattern>();
        aswArray_ = new ArrayList<String>();
        random_ = new Random();
    }
    
    public void addRgx( String rgxPtnStr ){
        Pattern pattern = Pattern.compile( rgxPtnStr );
        rgxPtnArray_.add(pattern);
    }
    
    public void addAsw( String aswStr ){
        aswArray_.add(aswStr);
    }
    
    /**
     * 随机从当前pattern所可以对应的智能答案列表中随机挑选一个，返回给用户
     * @return
     */
    public String randomRawAsw(){
        return aswArray_.get(random_.nextInt(aswArray_.size()));
    }
    
    /**
     * 根据匹配产生的Matcher来进行回答
     * 此函数可重入
     * @param matcher
     * @return
     */
    public String answer( Matcher matcher ){
        String rlt = randomRawAsw();
        for( int i = 1; i <= matcher.groupCount(); i++ ){
            rlt = rlt.replaceAll( "#" + Integer.toString(i), matcher.group(i) );
        }
        return rlt;
    }
}
