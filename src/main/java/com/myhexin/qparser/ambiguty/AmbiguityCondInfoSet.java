package com.myhexin.qparser.ambiguty;

import java.util.HashMap;


public class AmbiguityCondInfoSet {

	private HashMap<String, AmbiguityCondInfoAbstract> condInfoSet_ = null;
    final private String split = "_X_";
	
    /**
     * init
     */
	public AmbiguityCondInfoSet()
	{
        init();
	}

    public boolean init()
    {
        condInfoSet_ = new HashMap<String, AmbiguityCondInfoAbstract>();
        if(condInfoSet_ == null)
            return false;
        return true;
    }

    public String getKey(String aliasName, String type)
    {
        return type + split + aliasName;
    }

    public void addCondInfo(String aliasName, String type, AmbiguityCondInfoAbstract cond)
    {
    	//ModifyLog.static_warn("AmbiguityCondInfoSet", "AmbiguityCondInfoSet.addCondInfo");
        if(condInfoSet_ == null && !init())
            return;
        String key = getKey(aliasName, type);
        condInfoSet_.put(key, cond);
    }

    public AmbiguityCondInfoAbstract getCondInfo(String aliasName, String type)
    {
        if(condInfoSet_ == null)
            return null;
        String key = getKey(aliasName, type);
        AmbiguityCondInfoAbstract cond = condInfoSet_.get(key);
        if(cond != null && cond.type_ == type)
            return cond;
        return null;
    }
    
    public AmbiguityCondInfoSet copy()
	{
		AmbiguityCondInfoSet infos = new AmbiguityCondInfoSet();
		if(condInfoSet_!=null) {
			infos.condInfoSet_ = new HashMap<String, AmbiguityCondInfoAbstract>();
			infos.condInfoSet_.putAll(this.condInfoSet_);
		}
		
		return infos;
	}
}
