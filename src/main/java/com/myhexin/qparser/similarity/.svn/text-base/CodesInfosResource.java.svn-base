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
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.resource.model.ResourceInterface;
import com.myhexin.qparser.similarity.CodeInfo.CodeInfoProp;


/**
 * npatterninfo.txt加载到如下几个map中
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-1-4
 *
 */
public class CodesInfosResource implements ResourceInterface{
	private Map<Query.Type, List<CodeInfo>> filteredTypeToCodesInfo = new HashMap<Query.Type, List<CodeInfo>>();
	
	public static CodesInfosResource getInstance() {
		return instance;
	}
	private static CodesInfosResource instance = new CodesInfosResource();
	private CodesInfosResource() {
    }
    
    /*
     * 把每一行转换成CodeInfo
     * 
     * @param lines
     * @param type
     * @return
     */
    private List<CodeInfo> getCodeInfoListByConfigLines( List<ConfigTextLine> lines, Query.Type type ){
    	List<CodeInfo> codeInfoList = new ArrayList<CodeInfo>();
    	for( ConfigTextLine line : lines ){
            CodeInfo codeInfo = new CodeInfo();
            if(codeInfo.fill(line.getLine(), type)) {
            	codeInfoList.add(codeInfo);
                //addToIndex(codeInfo, type);
            }
        }
        return codeInfoList;
    }
    
    private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
    public void reload() {
    	List<ConfigTextLine>  fundCodes = mybatisHelp.getDateParserInfoMapper().selectConfigFileLines("fund_code_baseinfo.txt");
		List<ConfigTextLine>  stockCodes = mybatisHelp.getDateParserInfoMapper().selectConfigFileLines("stock_code_baseinfo.txt");
    
		reloadResource(fundCodes, Type.FUND);
		reloadResource(stockCodes, Type.STOCK);
    }
    
    
    /**
     * 重新加载资源
     * 
     * @param lines
     * @param qType
     */
    private void reloadResource(List<ConfigTextLine> lines, Type qType) {
    	if( lines == null ){
            return; 
        }
        if( qType == null ){
            return;
        }
        
        List<CodeInfo> codeInfoList = getCodeInfoListByConfigLines(lines, qType);
        //typeToCodesInfo.put(qType, codeInfoList);
        fillFilterCodesInfos( codeInfoList, qType);
    }
    
    public List<CodeInfo> getFiltedCodesInfos(Type qType){
        return filteredTypeToCodesInfo.get(qType);
    }

    /*
     * 如果是stock类型,
     * 
     * @param codeInfos
     * @param qtype
     */
    private void fillFilterCodesInfos(List<CodeInfo> codeInfos, Query.Type qtype ){
        if( qtype == Query.Type.STOCK ){
            List<CodeInfo> oldStockCodeInfos = codeInfos;
            List<CodeInfo> filterStockCodeInfos = filterStockCodeInfos(oldStockCodeInfos);
            filteredTypeToCodesInfo.put(Query.Type.STOCK, filterStockCodeInfos);
        }else if( qtype == Query.Type.FUND ){
        	filteredTypeToCodesInfo.put(Query.Type.FUND, codeInfos);
        }
    }
    
    private List<CodeInfo> filterStockCodeInfos( List<CodeInfo> rawStockCodeInfos ){
        List<CodeInfo> rlt = new ArrayList<CodeInfo>();
        for( CodeInfo codeInfo : rawStockCodeInfos ){
            if( isNeedStockCodeInfo(codeInfo) ){
                rlt.add(codeInfo);
            }
        }
        return rlt;
    }
    
    
    /*
     * 不是a股, false
     * 在市状态state=='' or state!=1,5,6, false
     * 
     */
    private boolean isNeedStockCodeInfo( CodeInfo codeInfo ){
        String abMarket = codeInfo.getStrPropValue(CodeInfoProp.abmarket);
        if( !"a".equals(abMarket) ){
            return false;
        }
        String state = codeInfo.getStrPropValue(CodeInfoProp.state);
        if( state == null ){
            return false;
        }
        if( !state.matches("[1|5|6]") ){
            return false;
        }
        return true;
        
    }
    
    
    //<type, List<CodeInfo> >
    //private Map<Query.Type, List<CodeInfo>> typeToCodesInfo = new HashMap<Query.Type, List<CodeInfo>>();
    
    /*public List<CodeInfo> _getCodesInfos(Type qType){
        return typeToCodesInfo.get(qType);
    }*/

    
    /*
     * 把没一行,根据类型,放到typeToPropNameToPropValueToCodeInfoList Map中
     * 
     * @param codeInfo
     * @param type
     */
    /*
     private void addToIndex(CodeInfo codeInfo, Query.Type type ){
    	//所有属性
        CodeInfoProp[] names = CodeInfoProp.values();
        
        //顶层map
        Map<CodeInfoProp, Map<String,List<CodeInfo>>> propNameToPropValueToCodeInfoListMap = typeToPropNameToPropValueToCodeInfoList.get(type);
        if(propNameToPropValueToCodeInfoListMap==null) {
        	propNameToPropValueToCodeInfoListMap = new  HashMap<CodeInfoProp, Map<String,List<CodeInfo>>>();
        }
        
        //遍历所有names
        for( int i=0; i<names.length; i++ ){
            CodeInfoProp nowName = names[i];
            if( !propNameToPropValueToCodeInfoListMap.containsKey(nowName) ){
            	propNameToPropValueToCodeInfoListMap.put(nowName, new HashMap<String,List<CodeInfo>>());
            }
            Map<String, List<CodeInfo>> valueToCodeInfos = propNameToPropValueToCodeInfoListMap.get(nowName); 
            String propValue = codeInfo.getStrPropValue(nowName);
            if( !valueToCodeInfos.containsKey(propValue) ) {
                valueToCodeInfos.put(propValue, new ArrayList<CodeInfo>());
            }
            List<CodeInfo> codeInfoList = valueToCodeInfos.get(propValue);
            codeInfoList.add(codeInfo);
        }
    }*/
    
    
    /*
     * 遗留代码，这个map较复杂
     * Query.Type
     * PropName
     * propValue, List<CodeInfo>
     * 
     */
    //private Map<Query.Type, Map<CodeInfoProp, Map<String,List<CodeInfo>>>> typeToPropNameToPropValueToCodeInfoList = new HashMap<Query.Type, Map<CodeInfoProp, Map<String, List<CodeInfo>>>>();
    
}
