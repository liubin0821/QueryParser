package com.myhexin.qparser.onto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;
import org.w3c.dom.Document;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.OntoTypeErrorException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.SemanticNode;

/**
 * 本体信息在内存中的表示。本类提供一系列方法来获取指定的本体对象。 
 * !!!特别注意!!! 这些本体对象虽然是只读对象，但由于随时可能被外界更新，使用者不要
 * 长久持有其引用（如作为类静态引用）
 */
public class MemOnto {
	// 加载stock_onto.xml中的指标
    public static MultiValueMap allOnto_ = new MultiValueMap();
    // 加载stock_onto_old_system.xml中的指标
    public static MultiValueMap allOntoOldIndex_ = new MultiValueMap();
    
    // 以下几项暂时不起作用
    
    public static Map<String, SemanticNode> stkOnto_ = Collections.<String, SemanticNode>emptyMap();
    private static Map<String, SemanticNode> fundOnto_ = Collections.<String, SemanticNode>emptyMap();
    private static Map<String, SemanticNode> stkUserOnto_ = Collections.<String, SemanticNode>emptyMap();
    private static Map<String, SemanticNode> fundUserOnto_ = Collections.<String, SemanticNode>emptyMap();
    
    
	public static void init() {
        allOnto_ = new MultiValueMap();
        allOntoOldIndex_ = new MultiValueMap();
    }
    
    public static void reload(MultiValueMap rtns, MultiValueMap rtnsold) {
		allOnto_ = rtns;
		allOntoOldIndex_ = rtnsold;
	}
    
    // 对stock_onto.xml解析结果返回值的处理
    public static void loadAllOnto(Document doc,Document docCondition) throws DataConfException {
    	MultiValueMap rtns = OntoXmlReader.loadOnto(doc, docCondition);
    	allOnto_ = rtns;
    }
    
    // 对stock_onto_old_system.xml解析结果返回值的处理
    public static void loadAllOntoOldIndex(Document doc,Document docCondition) throws DataConfException {
    	MultiValueMap rtns = OntoXmlReaderOldSystem.loadOnto(doc, docCondition);
    	allOntoOldIndex_ = rtns;
    }
    
    /**
     * 此方法已停用 
     * @param ontoName
     * @param c
     * @param type
     * @deprecated
     * @return
     */
    public static <T> T getOnto(String ontoName, Class<T> c, Query.Type type) throws UnexpectedException {
    	T rtns = null;
        return rtns;
    }
    
    
    /**
     * 取第一个本体
     * 刘小峰 2014/11/20 在开发转回测condition时添加词方法
     * 
     * @param ontoName
     * @param c
     * @param type
     * @return
     * @throws UnexpectedException
     */
    public static <T> T getOntoFirstOne(String ontoName, Class<T> c, Query.Type type) throws UnexpectedException {
	    T cn = null;
		try {
			Collection<T> list = MemOnto.getOntoC(ontoName, c, type);
			Iterator<T> it = null;
			if(list!=null ) {
				it = list.iterator();
				if(it.hasNext())
				{
					cn = it.next();
					/*if(cn instanceof ClassNodeFacade) {
						ClassNodeFacade cn1 = (ClassNodeFacade )cn;
						cn1.clearPropValues();
						return cn1;
					}*/
				}
			}
		} catch (Exception e) {
			//指标拼接
			//TODO 如果出错再处理这里
			//classOnto = joinIndex(indexTreeData);
			e.printStackTrace();
		}
		
		return cn;
    }

    public static <T> Collection<T> getOntoC(String ontoName, Class<T> c, Query.Type type) {
    	Collection<T> rtns = null;
        try {
        	if (c.isAssignableFrom(ClassNodeFacade.class))
        		rtns = getOnto_(ontoName, c, type, allOnto_);
        	else if (c.isAssignableFrom(PropNodeFacade.class))
        		rtns = getOntoProp(ontoName, c, type);
        } catch (UnexpectedException e) {
            rtns = null;
        }
        return rtns;
    }
    
    public static <T> Collection<T> getOntoIndex(String ontoName, Class<T> c, Query.Type type) {
    	Collection<T> rtns = null;
        try {
            rtns = getOnto_(ontoName, c, type, allOnto_);
        } catch (UnexpectedException e) {
            rtns = null;
            return rtns;
        }
        if (rtns == null || rtns.isEmpty())
        	return null;
        Collection<T> rtnsTemp = new ArrayList<T>();
        Iterator iterator = rtns.iterator();
        while (iterator.hasNext()) {
        	ClassNodeFacade cn = (ClassNodeFacade) iterator.next();
        	cn.clearPropValues();
        	if (cn.getText().equals(ontoName))
        		rtnsTemp.add((T) cn);
        }
        return rtnsTemp;
    }
    
    public static <T> Collection<T> getOntoAlias(String ontoName, Class<T> c, Query.Type type) {
    	Collection<T> rtns = null;
        try {
            rtns = getOnto_(ontoName, c, type, allOnto_);
        } catch (UnexpectedException e) {
            rtns = null;
        }
        return rtns;
    }
    
    public static <T> Collection<T> getOntoProp(String ontoName, Class<T> c, Query.Type type) {
    	Collection<T> rtns = null;
        try {
            rtns = getOnto_(ontoName, c, type, allOnto_);
        } catch (UnexpectedException e) {
            rtns = null;
        }
        return rtns;
    }
    
    public static <T> Collection<T> getOntoOld(String ontoName, Class<T> c, Query.Type type) {
    	Collection<T> rtns = null;
        try {
        	if (c.isAssignableFrom(ClassNodeFacade.class))
        		rtns = getOnto_(ontoName, c, type, allOntoOldIndex_);
        	else if (c.isAssignableFrom(PropNodeFacade.class))
        		rtns = getOntoOldProp(ontoName, c, type);
        } catch (UnexpectedException e) {
            rtns = null;
        }
        return rtns;
    }
    
    private static <T> Collection<T> getOntoOldIndex(String ontoName, Class<T> c, Query.Type type) {
    	Collection<T> rtns = null;
        try {
        	rtns = getOnto_(ontoName, c, type, allOntoOldIndex_);
        } catch (UnexpectedException e) {
            rtns = null;
        }
        return rtns;
    }
    
    private static <T> Collection<T> getOntoOldAlias(String ontoName, Class<T> c, Query.Type type) {
    	Collection<T> rtns = null;
        try {
        	rtns = getOnto_(ontoName, c, type, allOntoOldIndex_);
        } catch (UnexpectedException e) {
        	rtns = null;
        }
        return rtns;
    }
    
    private static <T> Collection<T> getOntoOldProp(String ontoName, Class<T> c, Query.Type type) {
    	Collection<T> rtns = null;
        try {
        	rtns = getOnto_(ontoName, c, type, allOntoOldIndex_);
        } catch (UnexpectedException e) {
            rtns = null;
        }
        return rtns;
    }
    
    /**
     * 获得指定本体实例
     * @param <T> 本体类型
     * @param ontoName 本体名称
     * @param c 类型实例
     * @return 对应的本体实例，若无（无对应名称或类型不符）则返回<code>null</code>
     * @throws UnexpectedException 所需的本体不存在。注意：当若在“股票”本体中不存在但
     * 在“基金”本体中存在或反之，则抛其子类{@link OntoTypeErrorException}
     */
    public static <T> T getSysOnto(String ontoName, Class<T> c, Query.Type type) throws UnexpectedException{
        return getOnto_(ontoName, c, type, stkOnto_, fundOnto_);
    }
    
    public static <T> T getUserOnto(String ontoName, Class<T> c, Query.Type type)throws UnexpectedException {
        return getOnto_(ontoName, c, type, stkUserOnto_, fundUserOnto_);
    }

    @SuppressWarnings("unchecked")
    private static <T> Collection<T> getOnto_(String ontoName, Class<T> c, Query.Type type, MultiValueMap onto) throws UnexpectedException {
        Collection collection = onto.getCollection(ontoName);
        if (collection == null)
        	return null;
        Iterator iterator = collection.iterator();
        Collection rtns = new ArrayList<T>();
        while (iterator.hasNext()) {
        	Object rtn = iterator.next();
	        if (rtn != null) {
	            if (c.isAssignableFrom(rtn.getClass())) {
	            	if (c.isAssignableFrom(PropNodeFacade.class))
	            		rtns.add(rtn);
	            	else if (c.isAssignableFrom(ClassNodeFacade.class))
	            	{
	            		ClassNodeFacade cn = (ClassNodeFacade) rtn;
	            		cn.clearPropValues();
	            		if(cn.getDomains()!=null)
	            			cn.core.setDomains(new HashSet<Query.Type>(cn.getDomains() ));
	            		
	            		if(cn.remainDomain(type))
	            			rtns.add(rtn);
	            	}
	            }/* else {
	                throw new UnexpectedException(
	                        "Type not match for [%s]: expect %s while got %s",
	                        ontoName, c.getName(), rtn.getClass().getName());
	            }*/
	        } else {
	        	throw new UnexpectedException(
	                    "Type not match for [%s]: expect %s",
	                    ontoName, c.getName());
	        } 
        }
        return rtns;
    }
    
    
    @SuppressWarnings("unchecked")
    private static <T> T getOnto_(String ontoName, Class<T> c, Query.Type type, Map<String, SemanticNode> stockOnto, Map<String, SemanticNode> fundOnto)throws UnexpectedException {
        SemanticNode rtn = null;
        if (type == Type.STOCK)
        	rtn = stockOnto.get(ontoName);
        else if (type == Type.FUND)
        	rtn = fundOnto.get(ontoName);
        
        if (rtn != null) {
        	//rtn = NodeUtil.copyNode(rtn);
            if (c.isAssignableFrom(rtn.getClass())) {
                return (T) rtn;
            } else {
                throw new UnexpectedException(
                        "Type not match for [%s]: expect %s while got %s",
                        ontoName, c.getName(), rtn.getClass().getName());
            }
        } else {
            if (type == Query.Type.STOCK && Param.SUPPORT_FUND
                    && fundOnto.get(ontoName) != null) {
                throw new OntoTypeErrorException(String.format(
                        MsgDef.ONTO_NOT_FOUND_FMT, ontoName, "基金", "基金"));
            } else if (type == Query.Type.STOCK && Param.SUPPORT_STOCK
                    && stockOnto.get(ontoName) != null) {
                throw new OntoTypeErrorException(String.format(
                        MsgDef.ONTO_NOT_FOUND_FMT, ontoName, "股票", "股票"));
            } else {
                return null;
            }
        } 
    }
	
    
    /*
     * 此方法已停用
     * @param readXMLFile
     * @param object
     * @deprecated
 
	public static void loadStockOnto(Document readXMLFile, Object object) {
	
	}*/
}
