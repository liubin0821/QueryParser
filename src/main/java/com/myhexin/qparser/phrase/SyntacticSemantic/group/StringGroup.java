package com.myhexin.qparser.phrase.SyntacticSemantic.group;

import java.util.ArrayList;

public class StringGroup {

    public StringGroup(String id) {
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
    public void addString(String string)
    {
        // check duplicate string
        _strings.add(string);
    }
    public ArrayList<String> getStrings()
    {
        return _strings;
    }

    private String _id = null;
    private String _description = null;
    private ArrayList<String> _strings = new ArrayList<String>();

}
