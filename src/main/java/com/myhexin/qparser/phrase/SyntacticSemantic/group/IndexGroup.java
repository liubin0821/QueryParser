package com.myhexin.qparser.phrase.SyntacticSemantic.group;

import java.util.ArrayList;

// TODO extend from StringGroup
public class IndexGroup {

    public IndexGroup(String id) {
        _id = id;
    }

    public IndexGroup() {
		super();
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public void set_description(String _description) {
		this._description = _description;
	}

	public void set_indexs(ArrayList<String> _indexs) {
		this._indexs = _indexs;
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
    public void addIndex(String index)
    {
        // TODO check duplicate index
        _indexs.add(index);
    }
    public ArrayList<String> getIndexs()
    {
        return _indexs;
    }

    public boolean contains(String index)
    {
	    for (String str : _indexs)
	    {
		    if (str.equals(index))
			    return true;
	    }
	    return false;
    }

    private String _id = null;
    private String _description = null;
    private ArrayList<String> _indexs = new ArrayList<String>();
}
