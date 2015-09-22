/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-4-4 下午2:59:04
 * @description:  特别说明,本类只能做存Object,  取Object的操作
 *                禁止添加其他任何操作,   如果有,我会全部清理掉   添加的人自己去想办法 	
 * 
 */
package com.myhexin.qparser.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.QPException;

public class Environment extends SemanticNode{
	@Override
	protected SemanticNode copy() {
		Environment rtn = new Environment();
		if(ENV!=null) {
			rtn.ENV = new HashMap<String, Object>(ENV);
			/*Iterator<String> it = ENV.keySet().iterator();
			while(it.hasNext()) {
				String k = it.next();
				Object v = ENV.get(k);
				rtn.ENV.put(k, v);
			}*/
		}
		if(finalKey!=null)
			rtn.finalKey=new HashSet<String>(finalKey);
		
		//TODO Env 不需要copy SemanticNode中内容
		//super.copy(rtn);
		return rtn;
	}
	
	
	private static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Environment.class.getName());
	protected HashMap<String, Object> ENV = null;//环境
	protected HashSet<String> finalKey = new HashSet<String>();//环境
	
	/*@SuppressWarnings("unchecked")
    public Environment clone(){
		Environment copy = (Environment) super.clone();
		
		copy.ENV = (HashMap<String, Object>) this.ENV.clone();
		copy.finalKey = (HashSet<String>) this.finalKey.clone();
		
		return copy;
	}*/

	public Environment(){
		super("ENV");
		type = NodeType.ENV;
		ENV = new HashMap<String, Object>();
	}
	
	public Environment(HashMap<String, Object> ENV) {
		if (ENV == null) {
			logger_.error("Environment构造函数参数不能为null !");
			throw new UnsupportedOperationException();
		}
		type = NodeType.ENV;
		this.ENV = ENV;
	}
	
	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-9-25 下午4:23:32
	 * @description:  取到环境中的东西,  	
	 * @param key
	 * @param remove  取完后是否删除
	 * @return
	 */
	public Object get(Object key,boolean remove) {
		if (!remove) {
			return ENV.get(key);
		}
		
		if(finalKey.contains(key)){
			logger_.error("Environment不能移除被设定为不能修改的内容:");
			throw new UnsupportedOperationException();
		}
		Object temp = ENV.get(key);
		ENV.remove(key);		
		return temp;
	}
	
	@SuppressWarnings("unchecked")
    public final <T> T get(Object key,Class<T> type ,boolean remove) {
		Object temp = get(key,remove);		
		return temp==null ? null : (T)temp;
	}

	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-9-25 下午4:24:20
	 * @description:  在环境中存放东西 	
	 * @param key
	 * @param value
	 * @param overWrite 如果已经存在是否覆盖
	 * @return
	 */
	public Object put(String key, Object value, boolean overWrite) {
		if (finalKey.contains(key)) {
			logger_.error("Environment中" + key + "已经存在,参数设置不能重复添加");
			throw new UnsupportedOperationException("\nEnvironment中" + key
			        + "已经存在,不能重复添加");
		}

		if (overWrite) {
			return ENV.put(key, value);
		} 

		if (ENV.containsKey(key)) {
			logger_.error("Environment中" + key + "已经存在,参数设置不能重复添加");
			throw new UnsupportedOperationException("\nEnvironment中" + key
			        + "已经存在,不能重复添加");
		}
		return ENV.put(key, value);

	}
	
	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-9-25 下午4:25:33
	 * @description:  移除环境中的东西 	
	 * @param key
	 * @return
	 */
	public Object remove(Object key) {
		if (finalKey.contains(key)) {
			logger_.error("Environment中不可更改的" + key + "不能使用该函数移除\n" +
					"请使用removeFinal");
			throw new UnsupportedOperationException("\nEnvironment中" + key + "已经存在,不能重复添加");
		}
		return ENV.remove(key);
	}
	
	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-9-25 下午4:26:36
	 * @description:  向环境中添加一系列东西 	
	 * @param map
	 * @param overWrite
	 */
	@Deprecated
	public
	void putAll(Map<String,Object> map,boolean overWrite){
		for (String key : map.keySet()) {
			put(key, map.get(key), overWrite);
		}
	}
	
	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-4-28 上午10:53:33
	 * @description:  不能被再次put的东西,也不能没移除 	
	 *
	 */
	public  Object putFinal(String key, Object value) {
			if (finalKey.contains(key)) {
				logger_.error("Environment中不可更改的" + key + "已经存在, 不能再次put");
				throw new UnsupportedOperationException("\nEnvironment中" + key + "已经存在,不能重复添加");
			}
			finalKey.add(key);
			return ENV.put(key, value);
	}

		



	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-9-25 下午4:27:36
	 * @description:  移除被限制不可修改的东西 	
	 * @param key
	 * @return
	 */
	public Object removeFinal(Object key) {
		finalKey.remove(key);
		return ENV.remove(key);
	}


	public int size() {

		return ENV.size();
	}


	public boolean isEmpty() {

		return ENV.isEmpty();
	}


	public boolean containsKey(Object key) {

		return ENV.containsKey(key);
	}


	public boolean containsValue(Object value) {

		return ENV.containsValue(value);
	}

	@Override
    public void parseNode(HashMap<String, String> k2v, Type qtype) throws QPException {
    }
	
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	if(ENV.containsKey("listDomain")){
    		for(Map.Entry entry : (Entry[])ENV.get("listDomain"))
    		{
    			sb.append("-"+(String)entry.getKey());
    		}
    	}

        return super.toString()+" domain:"+ (sb.length()>0?sb.substring(1):"");
    }

    public String getFirstDomain() {
    	Double max=0.0;
		String domain = null;
    	if(ENV.containsKey("listDomain")){
    		for(Map.Entry entry : (Entry[])ENV.get("listDomain"))
    		{
    			String k = (String)entry.getKey();
    			Double v = (Double) entry.getValue();
    			if(v!=null && k!=null && max<v) {
    				domain = k;
    			}
    		}
    	}
    	return domain;
    }
}
