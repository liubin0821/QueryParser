package com.myhexin.qparser.conf;

import java.util.HashMap;
import java.util.HashSet;


import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;


public class IntervalIndexGroup {
    private HashSet<String> DAY_QUOTE = new HashSet<String>();
    private HashSet<String> WEEK_QUOTE = new HashSet<String>();
    private HashSet<String> MONTH_QUOTE = new HashSet<String>();
    private HashSet<String> YEAR_QUOTE = new HashSet<String>();
    private HashSet<String> INTERVAL_QUOTE = new HashSet<String>();
    private HashMap<String, HashMap<String, ClassNodeFacade>> IntervalIndexGroup = new HashMap<String, HashMap<String, ClassNodeFacade>>();

    public IntervalIndexGroup(Query.Type type) {
        loadNames(type);
        loadInfoMap(type);
    }

    public ClassNodeFacade getIntervalIndex(String index, String intervalType) {
        if(!isChangeableInterval(index)){
            return null;
        }
        HashMap<String, ClassNodeFacade> infoMap = IntervalIndexGroup.get(index);
        if(infoMap==null){
            return null;
        }
        return infoMap.get(intervalType);
    }

    private void loadInfoMap(Type type) {
        for (String index : DAY_QUOTE) {
            HashMap<String, ClassNodeFacade> infos = new HashMap<String, ClassNodeFacade>();
            String weekIndexStr = "周" + index;
            String monthIndexStr = "月" + index;
            String yearIndexStr = "年" + index;
            String intervalIndexStr = "区间" + index;
            if (WEEK_QUOTE.contains(weekIndexStr)) {
                ClassNodeFacade weekClass = null;
                try {
                    weekClass = MemOnto.getOnto(weekIndexStr, ClassNodeFacade.class,
                            type);
                } catch (UnexpectedException e) {
                    System.out.println("没找到：" + weekIndexStr);
                }
                if (weekClass == null) {
                    System.out.println("没找到：" + weekIndexStr);
                }
                infos.put("周", weekClass);
            }
            if (MONTH_QUOTE.contains(monthIndexStr)) {
                ClassNodeFacade monthClass = null;
                try {
                    monthClass = MemOnto.getOnto(monthIndexStr,
                            ClassNodeFacade.class, type);
                } catch (UnexpectedException e) {
                    System.out.println("没找到：" + monthIndexStr);
                }
                if (monthClass == null) {
                    System.out.println("没找到：" + monthIndexStr);
                }
                infos.put("月", monthClass);
            }
            if (YEAR_QUOTE.contains(yearIndexStr)) {
                ClassNodeFacade yearClass = null;
                try {
                    yearClass = MemOnto.getOnto(yearIndexStr, ClassNodeFacade.class,
                            type);
                } catch (UnexpectedException e) {
                    System.out.println("没找到：" + yearIndexStr);
                }
                if (yearClass == null) {
                    System.out.println("没找到：" + yearIndexStr);
                }
                infos.put("年", yearClass);
            }
            if (INTERVAL_QUOTE.contains(intervalIndexStr)) {
                ClassNodeFacade interClass = null;
                try {
                    interClass = MemOnto.getOnto(intervalIndexStr,
                            ClassNodeFacade.class, type);
                } catch (UnexpectedException e) {
                    System.out.println("没找到：" + intervalIndexStr);
                }
                if (interClass == null) {
                    System.out.println("没找到：" + intervalIndexStr);
                }
                infos.put("区间", interClass);
            }
            IntervalIndexGroup.put(index, infos);
        }
    }

    private void loadNames(Type type) {
        if (type == Type.FUND) {
            return;
        }
        for (String index : dayQuote) {
            DAY_QUOTE.add(index);
        }
        for (String index : weekQuote) {
            WEEK_QUOTE.add(index);
        }
        for (String index : monthQuote) {
            MONTH_QUOTE.add(index);
        }
        for (String index : yearQuote) {
            YEAR_QUOTE.add(index);
        }
        for (String index : intervalQuote) {
            INTERVAL_QUOTE.add(index);
        }
    }

    public boolean isChangeableInterval(String text) {
        return DAY_QUOTE.contains(text);
    }

    private String[] dayQuote = { "换手率", "涨跌", "成交量", "收盘价", "前收盘价", "开盘价",
            "最低价", "均价", "成交额", "涨跌幅", "振幅", "最高价" };
    // 其他无对应指标 "交易状态","相对发行价涨跌幅","停牌原因","相对发行价涨跌"
    private String[] weekQuote = { "周换手率", "周最低价日", "周平均换手率", "周涨跌幅", "周涨跌",
            "周前收盘价", "周最高收盘价日", "周开盘价", "周最高收盘价", "周成交额", "周收盘价", "周最低价",
            "周最高价日", "周均价", "周最低收盘价日", "周最高价", "周最低收盘价", "周成交量", "周振幅" };

    private String[] monthQuote = { "月振幅", "月成交量", "月最高价日", "月涨跌幅", "月换手率",
            "月最高收盘价", "月前收盘价", "月最低收盘价日", "月成交额", "月最低收盘价", "月最低价日", "月涨跌",
            "月收盘价", "月平均换手率", "月最低价", "月最高收盘价日", "月均价", "月开盘价", "月最高价" };

    private String[] yearQuote = { "年成交量", "年最低价日", "年最低收盘价", "年最低收盘价日",
            "年换手率", "年收盘价", "年涨跌", "年振幅", "年均价", "年最高收盘价日", "年开盘价", "年最高收盘价",
            "年最高价日", "年成交额", "年最低价", "年前收盘价", "年涨跌幅", "年平均换手率", "年最高价" };

    private String[] intervalQuote = { "n日日均涨跌幅", "n日成交量", "n日日均换手率", "n日涨跌幅",
            "n日振幅", "n日换手率", "n日最低流通a市值", "n日成交额", "相对大盘n日涨跌幅", "区间涨跌",
            "区间最高收盘价", "区间成交额", "区间最低收盘价", "相对大盘区间涨跌幅", "区间前收盘价", "区间振幅",
            "区间最高收盘价日", "区间最高价日", "区间最高价", "区间日均换手率", "区间日均涨跌幅", "区间日均成交额",
            "区间开盘价", "区间最低价日", "区间涨跌幅", "区间换手率", "区间最低收盘价日", "区间成交量", "区间成交均价",
            "区间最低价", "区间收盘价", "区间日均成交量", "区间交易日数" };

}
