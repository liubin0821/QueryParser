package com.myhexin.qparser.phrase;

import java.util.ArrayList;
import java.util.HashMap;

public class IndexTechopMapping {
    private static HashMap<String, HashMap<String, String>> indexTechopMapping = new HashMap<String, HashMap<String, String>>();

    /**
     * 系统启动时加载的文件
     * 
     * @author zd<zhangdong@myhexin.com>
     * @param readTxtFile
     */
    public static void loadMapping(ArrayList<String> readTxtFile) {
        for (String line : readTxtFile) {
            line = line.toLowerCase().trim();
            String[] splits = line.split("\\t");
            if (!indexTechopMapping.containsKey(splits[0])) {
                indexTechopMapping.put(splits[0], new HashMap<String, String>());
            }
            indexTechopMapping.get(splits[0]).put(splits[1], null);
        }
    }

    /**
     * 判断指标index是否含有operator这个形态
     * 
     * @author zd<zhangdong@myhexin.com>
     * @param index
     * @param operator
     * @return
     */
    public static boolean hasOp(String index, String operator) {
        boolean rtn = false;
        rtn = indexTechopMapping.containsKey(index) && indexTechopMapping.get(index).containsKey(operator);
        return rtn;
    }

    /**
     * 获取指定指标的所有形态
     * 
     * @author zd<zhangdong@myhexin.com>
     * @param index
     * @return
     */
    public static ArrayList<String> getOps(String index) {
        ArrayList<String> rtn = null;
        if (indexTechopMapping.containsKey(index)) {
            rtn = new ArrayList<String>(indexTechopMapping.get(index).keySet());
        }
        return rtn;
    }

}
