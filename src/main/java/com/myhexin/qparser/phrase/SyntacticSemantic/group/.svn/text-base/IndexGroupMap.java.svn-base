package com.myhexin.qparser.phrase.SyntacticSemantic.group;

import java.util.ArrayList;
import java.util.HashMap;

public class IndexGroupMap {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(IndexGroupMap.class.getName());
	
    public IndexGroupMap() {
        indexGroupMap_ = new HashMap<String, IndexGroup>();
    }

    public IndexGroup getIndexGroup(String id)
    {
	    return getIndexGroup(id, true);
    }

    public IndexGroup getIndexGroup(String id, boolean create)
    {
        IndexGroup indexGroup;
        indexGroup = indexGroupMap_.get(id);
        if(indexGroup == null && create)
        {
            indexGroup = new IndexGroup(id);
            indexGroupMap_.put(id, indexGroup);
        }
        return indexGroup;
    }
    
    public void setIndexGroupMap_(HashMap<String, IndexGroup> indexGroupMap_) {
		this.indexGroupMap_ = indexGroupMap_;
	}

	private HashMap<String, IndexGroup> indexGroupMap_ = null;
	
	// 新增，两个indexGroup求交集
	public String intersectionOfIndexGroupAB(String idA, String idB) {
		//logger_.warn("Intersection Of IndexGroup " + idA + "&" + idB);
		IndexGroup indexGroupA = indexGroupMap_.get(idA);
		IndexGroup indexGroupB = indexGroupMap_.get(idB);
		String id = null;
		if (indexGroupA.getIndexs().containsAll(indexGroupB.getIndexs())) {
			id = idB;
		}
		else if (indexGroupB.getIndexs().containsAll(indexGroupA.getIndexs())) {
			id = idA;
		}
		else {
			ArrayList<String> temp = (ArrayList<String>) indexGroupA.getIndexs().clone();
			temp.retainAll(indexGroupB.getIndexs());
			if (temp != null && temp.size() > 0) {
				String cid = idA + "&" + idB;
				IndexGroup indexGroup = new IndexGroup(id);
				indexGroup.setDescription("Intersection Of IndexGroup " + idA + "&" + idB);
				indexGroup.set_indexs(temp);
				indexGroupMap_.put(cid, indexGroup);
				id = cid;
			}
		}
		//logger_.warn(idA + ": " + indexGroupA.getIndexs());
		//logger_.warn(idB + ": " + indexGroupB.getIndexs());
		//logger_.warn(id == null ? "null" : (id  + ": " + indexGroupMap_.get(id).getIndexs()));
		return id;
	}
}
