package com.myhexin.qparser.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.except.DataConfException;


/**
 * 
 * @author hexin
 *
 */
public class IndexInteraction {
	public static Map<String, ArrayList<String>> indexInteractions_ =  null;
	static{
		loadData();
	}
	
	/**
     * 我们还是用xml来进行智能回答问句的存储吧
     * 
     * @param doc
     * @throws DataConfException
     * @throws  
     */
	public static void reloadData(){
		HashMap<String, ArrayList<String>> temp = new HashMap<String, ArrayList<String>>();
		MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
		List<com.myhexin.DB.mybatis.mode.IndexInteraction> indexInteractionList = mybatisHelp.getIndexInteractionMapper().selectAll();
		for(com.myhexin.DB.mybatis.mode.IndexInteraction indexInteraction : indexInteractionList){
			String key = indexInteraction.getKey();
			String interaction = indexInteraction.getInteraction();
			ArrayList<String> list = null;
			if(temp.containsKey(key)){
				list = temp.get(key);
			}else{
				list = new ArrayList<String>();
			}
			list.add(interaction);
			temp.put(key, list);
		}
		indexInteractions_=temp;
		
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-11-13 上午10:28:25
     * @description:   	
     */
    public static void loadData() {
    	if(indexInteractions_==null)
    		reloadData();
    }

}
