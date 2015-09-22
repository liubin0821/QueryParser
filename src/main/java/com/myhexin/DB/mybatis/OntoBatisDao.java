package com.myhexin.DB.mybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.DB.mybatis.mode.Aliases;
import com.myhexin.DB.mybatis.mode.DataRelationV;
import com.myhexin.DB.mybatis.mode.IndexFieldV;
import com.myhexin.DB.mybatis.mode.IndexPropV2;
import com.myhexin.DB.mybatis.mode.Indexs;
import com.myhexin.DB.mybatis.mode.NormalRelation;
import com.myhexin.DB.mybatis.mode.ObjectRelationV;
import com.myhexin.DB.mybatis.mode.ResolveAliasesConflicts;
import com.myhexin.DB.mybatis.mode.SuperIndexV;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;


/**
 * 语义网DAO合集
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-2-13
 *
 */
public class OntoBatisDao {

	
	private MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	
	/*final Map<Integer, ArrayList<Aliases>> aliases,
	final Map<Integer, ArrayList<ResolveAliasesConflicts>> resolveAliases, 
	final Map<Integer, ArrayList<IndexFieldV>> field,
	final Map<Integer, ArrayList<SuperIndexV>> superIndex,
	final Map<Integer, ArrayList<IndexPropV2>> indexPropV2,
	final Map<Integer, ArrayList<DataRelationV>> dataRelationV,
	final Map<Integer, ArrayList<NormalRelation>> normalRelation,
	final Map<Integer, ArrayList<ObjectRelationV>> objectRelationV*/
	
	public Map<Integer, ArrayList<Aliases>> getAliaseMap() {
		Map<Integer, ArrayList<Aliases>>  aliseMap = new HashMap<Integer, ArrayList<Aliases>> ();
		List<Aliases> list = mybatisHelp.getAliasesMapper().selectAll();
		for (Aliases a : list)
			putInMap(aliseMap, a.getIndexId(), a);
		
		return aliseMap;
	}
	
	
	public Map<Integer, ArrayList<ResolveAliasesConflicts>>  getResolveAliasesMap() {
		Map<Integer, ArrayList<ResolveAliasesConflicts>> resolveAliases = new HashMap<Integer, ArrayList<ResolveAliasesConflicts>>();
		List<ResolveAliasesConflicts> list = mybatisHelp.getResolveAliasesConflictsMapper().selectAll();
		for(ResolveAliasesConflicts rac : list)
			putInMap(resolveAliases, rac.getAliaseId(), rac);
		
		return resolveAliases;
	}
	

	public Map<Integer, ArrayList<IndexFieldV>> getFieldMap() {
		Map<Integer, ArrayList<IndexFieldV>> fieldMap = new HashMap<Integer, ArrayList<IndexFieldV>>();
		for (IndexFieldV f : mybatisHelp.getIndexFieldVMapper().selectAll())
			putInMap(fieldMap, f.getIndexId(), f);
		return fieldMap;
	}
	
	public Map<Integer, ArrayList<SuperIndexV>> getSuperIndexMap() {
		Map<Integer, ArrayList<SuperIndexV>> superIndex = new HashMap<Integer, ArrayList<SuperIndexV>>();
		for (SuperIndexV si : mybatisHelp.getSuperIndexVMapper().selectAll())
			putInMap(superIndex, si.getIndexId(), si);
		return superIndex;
	}
	
	public Map<Integer, ArrayList<IndexPropV2>> getIndexPropV2() {
		Map<Integer, ArrayList<IndexPropV2>> indexPropV2 = new HashMap<Integer, ArrayList<IndexPropV2>>();
		for (IndexPropV2 ip : mybatisHelp.getIndexPropV2Mapper().selectAll())
			putInMap(indexPropV2, ip.getIndexId(), ip);
		
		return indexPropV2;
	}
	
	public Map<Integer, ArrayList<DataRelationV>> getDataRelationV() {
		Map<Integer, ArrayList<DataRelationV>> dataRelationV = new HashMap<Integer, ArrayList<DataRelationV>>();
		for (DataRelationV dr : mybatisHelp.getDataRelationVMapper().selectAll())
			putInMap(dataRelationV, dr.getPropId(), dr);
		
		return dataRelationV;
	}
	
	public Map<Integer, ArrayList<NormalRelation>> getNormalRelation() {
		Map<Integer, ArrayList<NormalRelation>> normalRelation = new HashMap<Integer, ArrayList<NormalRelation>>();
		for (NormalRelation nr : mybatisHelp.getNormalRelationMapper().selectAll())
			putInMap(normalRelation, nr.getPropId(), nr);
		return normalRelation;
	}
	
	public Map<Integer, ArrayList<ObjectRelationV>> getObjectRelationV() {
		Map<Integer, ArrayList<ObjectRelationV>> objectRelationV = new HashMap<Integer, ArrayList<ObjectRelationV>>();
		for (ObjectRelationV or : mybatisHelp.getObjectRelationVMapper().selectAll())
			putInMap(objectRelationV, or.getPropId(), or);
		
		return objectRelationV;
	}
	
	public List<Indexs> getIndexs() {
		return mybatisHelp.getIndexsMapper().selectAll();
	}
	
	private final <T> void putInMap(Map<Integer, ArrayList<T>> map, Integer id, T t) {
		if (map.containsKey(id))
			map.get(id).add(t);
		else {
			ArrayList<T> list = new ArrayList<T>();
			list.add(t);
			map.put(id, list);
		}
	}
}
