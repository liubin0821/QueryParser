package com.myhexin.qparser.resource.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.myhexin.DB.mybatis.mode.RefCode;
import com.myhexin.qparser.phrase.util.Consts;

public class DomainInfo implements ResourceInterface{

	private static DomainInfo instance = new DomainInfo();
	private DomainInfo() {
		
	}
	
	public static DomainInfo getInstance() {
		return instance;
	}
	
	
	public boolean contains(String name) {
		if(includeDomainNames==null) return false;
		
		return includeDomainNames.contains(name);
	}
	
	
	private Set<String> includeDomainNames;

	@Override
	public void reload() {
		Set<String> includeDomainNames =  getIncludeDomainNames();
		if(includeDomainNames==null) includeDomainNames = new HashSet<String>();
		if(!includeDomainNames.contains(Consts.CONST_absStockDomain)) {
			includeDomainNames.add(Consts.CONST_absStockDomain);
		}
		if(!includeDomainNames.contains(Consts.CONST_absZhishuDomain)) {
			includeDomainNames.add(Consts.CONST_absZhishuDomain);
		}
		
		this.includeDomainNames = includeDomainNames;
	}
	
	
    private Set<String> getIncludeDomainNames() {
		//把要返回的domain找出来
    	Set<String> includedomainNames  = new HashSet<String>();
			List<RefCode> refCodes = RefCodeInfo.getInstance().get(new Integer(1001));
			if(refCodes!=null) {
				for(RefCode r : refCodes ) {
					includedomainNames.add(r.getCode_value());
				}
			}
		return includedomainNames;
	}
}
