package com.myhexin.qparser.phrase;

import java.util.ArrayList;
import java.util.List;

public class KeyWordItem {

	public String keyword;
	public List<String> synIds;
	
	public KeyWordItem(String k)
	{
		this.keyword = k;
	}
	
	public void addSynIds(String id)
	{
		if (synIds == null)
		{
			synIds = new ArrayList<String>();
		}
		synIds.add(id);
	}
}
