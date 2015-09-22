/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-3-13 下午7:45:31
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.ThematicClassify;

import java.util.ArrayList;
import java.util.HashSet;

import com.myhexin.qparser.define.EnumDef.IndexType;

public class IndexThematicCondition extends ThematicCondition {
	private String indexName;
	private String[] indexNames;
	private ArrayList<IndexType> indexTypes = null; //new ArrayList<IndexType>();
	private HashSet<String> indexs = null;

	public void setIndexName(String indexName) {
		this.indexName = indexName;
		this.indexNames = indexName.split("\\|");
		fillIndexs();
	}
	private void fillIndexs() {
		if(indexNames!=null){
			indexs = new HashSet<String>();
	    	for (String indexName : indexNames) {
	    		indexs.add(indexName);
	    	}
		}
    	
    }
	
	public void addIndexType(IndexType type) {
		if(indexTypes==null ) indexTypes= new ArrayList<IndexType>();
		indexTypes.add(type);
	}
	
	public String getIndexName() {
		return indexName;
	}

	public ArrayList<IndexType> getIndexTypes() {
		return indexTypes;
	}

	@Override
    public boolean isMatch(ThematicBasicInfos tbi) {
		if(indexNames==null) {
			return false;
		}
		for (String indexName : indexNames) {
		    if (tbi.textIndexMap.containsKey(indexName)) {
				return true;
			}
		}
	    return false;
    }

	@Override
    public boolean isContain(String something) {
		return indexs.contains(something);
    }

}