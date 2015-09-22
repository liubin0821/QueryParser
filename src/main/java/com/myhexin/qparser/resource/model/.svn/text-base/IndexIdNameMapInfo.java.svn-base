package com.myhexin.qparser.resource.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.DataLine;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;

/**
 * 指数name->IdList mapping
 * 
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-12
 * 
 */
public class IndexIdNameMapInfo implements ResourceInterface{
	//private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Parser.class.getName());
	private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	private static IndexIdNameMapInfo instance = new IndexIdNameMapInfo();
	//private Map<String, List<String>> idNameMap;
	private Map<String, String> name2IdMap; //<行业名称, ID>
	private Map<String, Map<String, String>> name2Name2IdMap; //<行业级别名称,<行业名, ID>>
	private IndexIdNameMapInfo() {
		
	}
	
	public static IndexIdNameMapInfo getInstance() {
		return instance;
	}
	
	@Override
	public void reload() {
		//data_lines_v
		//type_id	text	codeId	code_short_desc	label
		//4283	a股指数	id:1A0002	_上证综合型指数简称,null	_上证综合型指数简称
		List<DataLine> dateLines = mybatisHelp.getDataLinesMapper().getDataLines();
				
		//指数名称->ID映射关系
		Map<String, List<String>> indexNameIds = new HashMap<String, List<String>>();
				
		//dataLIne转换成Map
		Map<Integer, List<DataLine>> dateLineMap = new HashMap<Integer, List<DataLine>>();
		for(DataLine dl : dateLines) {
			//根据ID配置构造:指数名称=>ID的MAP
			String code = dl.getCodeWithoutColon();
			if(p.matcher(code).matches()==false) continue;
					
					
			List<String> indexnames = dl.getIndexName();
			for(String indexname : indexnames) {
						
				//类别,如同花顺行业指数
				if(code!=null && code.length()>0) {
					List<String> ids = indexNameIds.get(indexname);
					if(ids==null) {
						ids = new ArrayList<String>();
						indexNameIds.put(indexname, ids);
					}
							
					if(ids.contains(code)==false)
						ids.add(code);
				}
						
				//叶子节点,如所属同花顺行业@房地产
				String name1=  null;
				if(indexname!=null && indexname.equals("null")) {
					name1=  dl.getText();
				}else{
					name1=  indexname+"@"+dl.getText();
				}
						
				List<String> ids = indexNameIds.get(name1);
				if(ids==null) {
					ids = new ArrayList<String>();
					indexNameIds.put(name1, ids);
				}
				if(ids.contains(code)==false)
					ids.add(code);
			}
		}
				
		//清楚对象,虽然不需要
		dateLineMap.clear(); dateLineMap = null;
				
		Map<String, String> _name2IdMap = new HashMap<String, String>();
		Map<String, Map<String, String>> _name2Name2IdMap = new HashMap<String, Map<String, String>>();
		
		Set<Map.Entry<String, List<String>>> idsEntries = indexNameIds.entrySet();
		for(Map.Entry<String, List<String>> entry : idsEntries) {
			String name = entry.getKey();
			List<String> ids = entry.getValue();
			String idstr = idsListToString(ids);
			if(idstr!=null) {
				_name2IdMap.put(name, idstr);
				
				if(name.indexOf("@")>0) {
					String[] names = name.split("@");
					if(names.length==2) {
						Map<String, String> subNames = _name2Name2IdMap.get(names[0]);
						if(subNames==null) {
							subNames = new HashMap<String, String>();
							_name2Name2IdMap.put(names[0], subNames);
						}
						subNames.put(names[1], idstr);
					}
				}
			}
		}
		this.name2IdMap = _name2IdMap;
		this.name2Name2IdMap = _name2Name2IdMap;
		
		
		//debug_print(indexNameIds);
	}
	
	
	public String getIdListStr(String indexName, String name) {
		
		//像这三个取所有的ID的不用转ID
		if(indexName==null && (name.equals("所属申万行业") || name.equals("所属同花顺行业") || name.equals("指数")) ) {
			return null;
		}
		String ids= null;
		
		//把name分隔,有"上证A股,深圳A股"这种情况
		String[] subIndexNames = null;
		if(name!=null) {
			subIndexNames = name.split(",");
		}
		if(subIndexNames==null) subIndexNames = new String[]{name};
		
		
		//所属同花顺行业包含金属, 用所属同花顺行业@金属
		if(indexName!=null){
			StringBuilder buf = new StringBuilder();
			for(String n1 : subIndexNames) {
				String a_ids = name2IdMap.get(indexName + "@" + n1);
				if(a_ids!=null) {
					if(buf.length()>0) buf.append(">-<");
					buf.append(a_ids);
				}
			}
			if(buf.length()>0) {
				ids = buf.toString();
			}
		}
		
		
		//上面"所属同花顺行业@金属"没有找到ID
		//尝试只用"金属"去找
		if(ids==null) {
			StringBuilder buf = new StringBuilder();
			for(String n1 : subIndexNames) {
				String a_ids = name2IdMap.get(n1);
				if(a_ids!=null) {
					if(buf.length()>0) buf.append(">-<");
					buf.append(a_ids);
				}
			}
			if(buf.length()>0) {
				ids = buf.toString();
			}
		}
		
		//上面只用"金属"去找也找不到, 直接用indexName去找
		if(ids==null && indexName!=null) {
			if(subIndexNames!=null && subIndexNames.length>0) {
				StringBuilder buf = new StringBuilder();
				
				//名字部分匹配
				Map<String,String> idsMap = name2Name2IdMap.get(indexName);
				if(idsMap!=null) {
					Set<String> names = idsMap.keySet();
					for(String name11 : names) {
						for(String n1 : subIndexNames) {
							if(name11.contains(n1) || n1.contains(name11)) {
								String a_ids = idsMap.get(name11);
								if(buf.length()>0) buf.append(">-<");
								buf.append(a_ids);
								break;
							}
						}
					}
				}
				if(buf.length()>0) {
					ids = buf.toString();
				} 
			}else{
				if(ids==null && indexName!=null) {
					ids = name2IdMap.get(indexName);
				}
			}
		}
		
		if(ids==null && indexName!=null) {
			ids = name2IdMap.get(indexName);
		}
		return ids;
	}
	
	/*public List<String> getIdList(String name) {
		if(idNameMap==null) return null;
		
		return idNameMap.get(name);
	}*/
	
	
	/*public boolean containsIndex(String name) {
		if(name2IdMap==null) return false;
		return name2IdMap.containsKey(name);
	}*/
	
    
    private String idsListToString(List<String> idList) {
    	if(idList!=null && idList.size()>0) {
			StringBuilder buf = new StringBuilder();
			int i=0;
			for(;i<idList.size()-1;i++) { //String s : idList) {
				String s = idList.get(i);
				if(s!=null && s.length()>0)
					buf.append(s).append(">-<");
			}
			String s = idList.get(i);
			if(s!=null && s.length()>0)
				buf.append(s);
			
			//有的情况,idList最后一个不是ID,是>-<
			if(buf.length()>3 && buf.charAt(buf.length()-1)=='<' && buf.charAt(buf.length()-2)=='-' && buf.charAt(buf.length()-3)=='>') {
				return buf.substring(0,buf.length()-3);
			}else
				return buf.toString();
		} else{
			return null;
		}
    }
    
    /*private void debug_print(Map<String, List<String>> indexNameIds) {
    	List<String> keys  = new ArrayList<String>(indexNameIds.keySet());
    	Collections.sort(keys);
    	
    	for(Iterator<String> it=keys.iterator();it.hasNext();) {
    		String k =it.next();
    		List<String> v = indexNameIds.get(k);
    		//if(k.equals("概念指数"))
    			System.out.println(k + ": " + v);
    	}
    	
    	System.out.println("1111111111");
    }*/
    
    private final static Pattern p = Pattern.compile("([0-9A-Za-z\\.]{1,10})");
}
