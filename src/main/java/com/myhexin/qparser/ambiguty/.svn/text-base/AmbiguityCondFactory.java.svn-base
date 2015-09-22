package com.myhexin.qparser.ambiguty;

public class AmbiguityCondFactory {

    /**
     * init
     */
	public AmbiguityCondFactory(String t)
	{
        ;
	}
	
    static public AmbiguityCondAbstract create(String name)
    {
        AmbiguityCondAbstract cond = null;
        if(name == "unit")
        {
            cond = (AmbiguityCondAbstract)(new AmbiguityCondUnit());
        }
        else if (name == "props")
        {
            cond = (AmbiguityCondAbstract)(new AmbiguityCondProps());
        }
        else if (name == "words")
        {
            cond = (AmbiguityCondAbstract)(new AmbiguityCondWords());
        }
        else if(name == "indexs")
        {
            cond = (AmbiguityCondAbstract)(new AmbiguityCondIndexs());
        }
        else if (name == "numrange")
        {
            cond = (AmbiguityCondAbstract)(new AmbiguityCondNumRange());
        }
        else if (name == "defaultval")
        {
        	cond = (AmbiguityCondAbstract)(new AmbiguityCondDefaultVal());
        }
        return cond;
    }
}
