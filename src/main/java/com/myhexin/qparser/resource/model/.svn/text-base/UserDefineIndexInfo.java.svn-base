package com.myhexin.qparser.resource.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.UserIndex;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.node.SemanticNode;


/**
 * 语义转Condition 配置信息
 * 
 * 用户自定义指标
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-7
 *
 */
public class UserDefineIndexInfo implements ResourceInterface{
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(UserDefineIndexInfo.class.getName());
	private static UserDefineIndexInfo instance = new UserDefineIndexInfo();
	private Map<String, Map<Unit,UserIndex>> userIndexMap;
	private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	private UserDefineIndexInfo() {
		
	}
	public static UserDefineIndexInfo getInstance() {
		return instance;
	}
	
	@Override
	public void reload() {
		List<UserIndex> userList = mybatisHelp.getUserIndexMapper().selectUserIndex();
		if(userList!=null) {
			Map<String, Map<Unit,UserIndex>> uM = new HashMap<String, Map<Unit,UserIndex>>();
			for(UserIndex index : userList) {
				index.parse();
				if(index.getNodes()==null || index.getNodes().size()==0) continue;
				
				try{
					Map<Unit,UserIndex> uMap = uM.get(index.getIndex_name());
					if(uMap==null) {
						uMap = new HashMap<Unit, UserIndex>();
						uM.put(index.getIndex_name(), uMap);
					}
					Unit unit = EnumConvert.getUnitFromStr(index.getUnit_str())==Unit.UNKNOWN?null:EnumConvert.getUnitFromStr(index.getUnit_str());
					uMap.put(unit, index);
				}catch(Exception e) {
					logger_.error("[Warning] UserIndex error: " + index.toString() + ":" + e.getMessage());
					e.printStackTrace();
				}
			}
			
			userIndexMap = uM;
		}
	}
	
	public List<SemanticNode> getUserDefineIndex(String indexName, String unit) {
		if(userIndexMap==null) 
			return null;
		
		Map<Unit,UserIndex> um = userIndexMap.get(indexName);
		
		if(um==null) 
			return null;
		
		UserIndex index = null;
		Unit unit1 = null;
		if(unit==null) {
			if(um!=null && um.size()>0) {
				index =  um.values().iterator().next();
			}
		}else{
			unit1 = EnumConvert.getUnitFromStr(unit)==Unit.UNKNOWN?null:EnumConvert.getUnitFromStr(unit);
			index =  um.get(unit1);
		}
		
		if(index!=null) {
			/*if(index.getNodes()==null) {
				index.parse();
				
				//如果还是为空,那么删除之
				if(index.getNodes()==null && unit1!=null) {
					um.remove(unit1);
					if(um.size()==0) {
						userIndexMap.remove(indexName);
					}
				}
			}*/
			return index.getNodes();
		}else{
			return null;
		}
	}
}
