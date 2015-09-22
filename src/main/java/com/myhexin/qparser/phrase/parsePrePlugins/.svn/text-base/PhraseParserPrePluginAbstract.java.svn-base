package com.myhexin.qparser.phrase.parsePrePlugins;

import java.util.Map;

import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.resource.ResourceInst;

public abstract class PhraseParserPrePluginAbstract {
    public String strTitle = "Pretreat_Plugin_Abstract";
    protected String pluginDesc = "#plugin# Detail of what the plugin do";
	
    public PhraseParserPrePluginAbstract(String title) {
        strTitle = title;
        
        Map<String, String> pluginDescMap = ResourceInst.getInstance().getPluginDescMap();
        if(pluginDescMap!=null && pluginDescMap.get(this.getClass().getSimpleName())!=null) {
        	pluginDesc = pluginDescMap.get(this.getClass().getSimpleName());
        }
    }
    
    public void init() {
        return;
    }
    
    public String getPluginDesc() {
    	return pluginDesc;
    }
    
    /**
     * TODO 刘小峰  这个接口有问题的,传入一个query,返回一个List
     * 
     * 返回多种可能性结果，则重写这个方法
     * 若不重写，则调用processSingle方法，并将结果包装成List<String>返回
     * processSingle方法默认返回原句
     * @param query
     * @return
     */
    public void process(ParserAnnotation annotation) {
    	String query = annotation.getQueryText();
    	//.trim()滥用,trim()会生成一个新String, query在第一个plugin就trim()掉
    	if (query == null || query.length() == 0) //
    		return;
    	
    	processSingle(annotation); 
    	String result = annotation.getQueryText();
    	if (result != null && result.length() > 0) {
    		annotation.setQueryText( result);
        }
    }
    
    /**
     * 只返回单种可能性结果，则重写这个方法
     * 若不重写，则原句返回
     * @param sb
     * @param queryList
     */
    public void processSingle(ParserAnnotation annotation) {
    	//String query = annotation.getQueryText();
    	//return query;
    }

    
    /**
     * 返回输入,输出
     * 
     * @param annotation
     * @return
     */
    public String getLogResult(ParserAnnotation annotation ) {
    	String queryText = annotation.getQueryText();
    	if(queryText!=null)
    		return String.format("[query]: %s\n", queryText);
    	else
    		return null;
    }
}

