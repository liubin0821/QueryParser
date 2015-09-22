package com.myhexin.qparser.util.condition.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.myhexin.qparser.phrase.util.Consts;

public class BackTestCondAnnotationComparator implements Comparator<BackTestCondAnnotation> {
	private final static Map<String, Integer> domainSortValueMap = new HashMap<String,Integer>();
	static {
		domainSortValueMap.put(Consts.CONST_absStockDomain, 1);
		domainSortValueMap.put(Consts.CONST_absZhishuDomain, 2);
		domainSortValueMap.put(Consts.CONST_absXinxiDomain, 3);
	}
	
	@Override
	public int compare(BackTestCondAnnotation o1, BackTestCondAnnotation o2) {
		String domainStr1 = o1.getDomainStr();
		String domainStr2 = o2.getDomainStr();
		if(domainStr1==null) {
			return 1;
		}
		
		if(domainStr2==null) {
			return -1;
		}
		
		Integer v1 = domainSortValueMap.get(domainStr1);
		Integer v2 = domainSortValueMap.get(domainStr2);
		
		if(v1==null) {
			return 1;
		}
		if(v2==null) {
			return -1;
		}
		
		return v1-v2;
	}
}
