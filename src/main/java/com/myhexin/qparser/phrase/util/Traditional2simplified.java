package com.myhexin.qparser.phrase.util;

import java.util.HashMap;
import java.util.List;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.TraditionalSimplifiedMap;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;

public class Traditional2simplified {
    static HashMap<String, String> mapping = null;
    
    
    static{
    	loadData();
    }

    public static void loadData() {
    	if(mapping==null)
    		reloadData();
    }

    public static void reloadData() {
    	HashMap<String, String> temp = new HashMap<String, String>();
    	MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	    List<TraditionalSimplifiedMap> traditionalSimplifiedMapList = mybatisHelp.getTraditionalSimplifiedMapMapper().selectAll();
    	for(TraditionalSimplifiedMap traditionalSimplifiedMap : traditionalSimplifiedMapList){
    		temp.put(traditionalSimplifiedMap.getTraditional(), traditionalSimplifiedMap.getSimplified());
    	}
    	mapping=temp;
    }
    
    public static String toSimplified(String simplified) {
    	if (simplified == null)
    		return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < simplified.length(); i++) {
            char character = simplified.charAt(i);
            String out = mapping.get(String.valueOf(character));
            if (out == null) {
                sb.append(character);
            } else {
                sb.append(out);
            }            
        }
        return sb.toString();
    }
}
