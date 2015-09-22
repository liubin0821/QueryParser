package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.ConfigTextLine;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.resource.model.ResourceInterface;
//import com.myhexin.qparser.conf.FileResourceInfo;
//import com.myhexin.qparser.define.EnumDef.ResourceFilePostfixType;
//import com.myhexin.qparser.except.DataConfWarning;
//import com.myhexin.qparser.onto.BaseOnto;
//import com.myhexin.qparser.util.QTypeUtil;

class DomainNPatternInfo{
    private String txt_;
    private Query.Type type_;
    public DomainNPatternInfo(){
    }
    public String getTxt_() {
        return txt_;
    }
    public void setTxt_(String txt_) {
        this.txt_ = txt_;
    }
    public Query.Type getType_() {
        return type_;
    }
    public void setType_(Query.Type type_) {
        this.type_ = type_;
    }
}

public class NPatternInfos implements ResourceInterface{
    private static final String VALUE_SPL = "\t";
    private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
    private List<String> warningLogs = null; //new ArrayList<String>();
    private Map<Query.Type, Map<String,DomainNPatternInfo>> queryTypeToNPatternInfoMap = null; //new HashMap<Query.Type, Map<String,DomainNPatternInfo>>();
    
    public static NPatternInfos getInstance() {
    	return instance;
    }
    private static NPatternInfos instance = new NPatternInfos();
    private NPatternInfos() {
    }
    
    /**
     * 检验一个npattern是否存在在NPatternInfo当中
     * @param qtype
     * @param str
     * @return
     */
    public boolean checkExist( Query.Type qtype, String str ){
        Map<String,DomainNPatternInfo> npToDNP = queryTypeToNPatternInfoMap.get(qtype);
        return npToDNP.containsKey(str);
    }
    
    private void initMap( Map<Query.Type, Map<String,DomainNPatternInfo>> map ){
        Query.Type[] types = Query.Type.values();
        Query.Type nowType = null;
        for( int i=0; i<types.length; i++ ){
            nowType = types[i];
            map.put(nowType, new HashMap<String, DomainNPatternInfo>());
        }
    }
    
    @Override
    public void reload() {
    	warningLogs = new ArrayList<String>();
    	queryTypeToNPatternInfoMap = new HashMap<Query.Type, Map<String,DomainNPatternInfo>>();
    	List<ConfigTextLine>  infoLines = mybatisHelp.getDateParserInfoMapper().selectConfigFileLines("npatternInfo.txt");
    	
        initMap(queryTypeToNPatternInfoMap);
        for( ConfigTextLine line : infoLines ){
            if( line == null || line.getLine()==null || line.getLine().isEmpty() ){
                continue;
            }
            try {
                addStrToNPatternInfoMap(line.getLine());
            } catch (Exception e) {
                warningLogs.add(e.getMessage());
                continue;
            }
        }
    }
    
    private void addStrToNPatternInfoMap(String line){
        String[] values = line.split(VALUE_SPL);
        String logStr = null;
        if( values == null || values.length < 2 ){
            logStr = "[" + line +  "] value size is null or <2";
            warningLogs.add(logStr);
        }
        DomainNPatternInfo di = new DomainNPatternInfo();
        Query.Type type = null;
        try{
            type = getQTypeFromStr(values[1]);
        }catch(InternalError error){
            logStr = "value size is not ";
            warningLogs.add(logStr + error.getMessage());
        }
        di.setTxt_(values[0]);
        di.setType_(type);
        if( !queryTypeToNPatternInfoMap.containsKey(type) ){
        	queryTypeToNPatternInfoMap.put(type, new HashMap<String, DomainNPatternInfo>());
        }
        Map<String, DomainNPatternInfo> npToDNPMap = queryTypeToNPatternInfoMap.get(type);
        npToDNPMap.put(di.getTxt_(), di);
    }
    
    public static Query.Type getQTypeFromStr(String typeStr){
    	if(typeStr.matches("stock|STOCK|股票")) return Query.Type.STOCK ;
    	if(typeStr.matches("fund|FUND|基金")) return Query.Type.FUND ;
    	//if(typeStr.matches("fina|FINA|fina_prod|FINA_PROD|理财|理财产品")) return Query.Type.FINA_PROD ;
    	//if(typeStr.matches("trust|TRUST|信托")) return Query.Type.TRUST ;
    	if(typeStr.matches("search|SEARCH|搜索")) return Query.Type.SEARCH ;
    	//if(typeStr.matches("indus|industry|INDUSTRY|行业")) return Query.Type.INDUSTRY ;
    	//if(typeStr.matches("concept|CONCEPT|概念")) return Query.Type.CONCEPT ;
    	//if(typeStr.matches("region|REGION|地域")) return Query.Type.REGION ;
        //if (typeStr.matches("sector|SECTOR|板块")) return Query.Type.SECTOR;
        //if (typeStr.matches("person|PERSON|人物")) return Query.Type.PERSON;
        //if (typeStr.matches("bond|BOND|债券")) return Query.Type.BOND;
        //if (typeStr.matches("report|REPORT|研报")) return Query.Type.REPORT;
        //if (typeStr.matches("fund_mananger|fundmanager|FUND_MANAGER|FUNDMANAGER|基金经理")) return Query.Type.FUND_MANAGER;
        if (typeStr.matches("hkstock|HKSTOCK|港股")) return Query.Type.HKSTOCK;
        throw new InternalError("bad qtypestr" + typeStr);
    }
}
