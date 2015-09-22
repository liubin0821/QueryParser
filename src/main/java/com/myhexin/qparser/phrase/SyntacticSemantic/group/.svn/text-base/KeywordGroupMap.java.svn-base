package com.myhexin.qparser.phrase.SyntacticSemantic.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class KeywordGroupMap {

    public KeywordGroupMap() {
        keywordGroupMap_ = new HashMap<String, KeywordGroup>();
    }

    public KeywordGroup getKeywordGroup(String id)
    {
    	return getKeywordGroup(id, true);
    }

    public void setKeywordGroupMap_(HashMap<String, KeywordGroup> keywordGroupMap_) {
		this.keywordGroupMap_ = keywordGroupMap_;
	}

	public KeywordGroup getKeywordGroup(String id, boolean create)
    {
        KeywordGroup keywordGroup;
        keywordGroup = keywordGroupMap_.get(id);
        if(keywordGroup == null && create)
        {
            keywordGroup = new KeywordGroup(id);
            keywordGroupMap_.put(id, keywordGroup);
        }
        return keywordGroup;
    }
    
    public List<String> getKeywordsByDesc(String desc) {
		List<String> keywords = new ArrayList<String>();
		Iterator iter = keywordGroupMap_.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			KeywordGroup val = (KeywordGroup)entry.getValue();
			if (val != null && val.getDescription() != null && val.getDescription().contains(desc)) {
				keywords.addAll(val.getKeywords());
			} 
		}
		return keywords;
    }
    
    private HashMap<String, KeywordGroup> keywordGroupMap_ = null;
}
