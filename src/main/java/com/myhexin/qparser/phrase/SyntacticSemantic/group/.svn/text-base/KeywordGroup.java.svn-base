package com.myhexin.qparser.phrase.SyntacticSemantic.group;

import java.util.ArrayList;
// TODO extend from StringGroup
public class KeywordGroup {

    public KeywordGroup(String id) {
        _id = id;
    }

    public String getId()
    {
        return _id;
    }
    public String getDescription()
    {
        return _description;
    }
    public void setDescription(String description)
    {
        _description = description;
    }
    public void addKeyword(String keyword)
    {
        // check duplicate keyword
        _keywords.add(keyword);
    }
    public ArrayList<String> getKeywords()
    {
        return _keywords;
    }
    public boolean contains(String keyword)
    {
	    for (String str : _keywords)
	    {
		    if (str != null && str.equals(keyword))
			    return true;
	    }
	    return false;
    }

    private String _id = null;
    private String _description = null;
    private ArrayList<String> _keywords = new ArrayList<String>();

}
