package com.myhexin.qparser.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.util.Consts;

public final class StrNode extends SemanticNode {

    @Override
    protected SemanticNode copy() {
    	StrNode rtn = new StrNode();
    	rtn.defaultIndexSubtype=defaultIndexSubtype;
    	if(id!=null) {
    		rtn.id=new HashMap<String, String>();
    		rtn.id.putAll(id);
    	}
    	
    	rtn.info=info;
    	rtn.props=props;
    	rtn.isTag=isTag;
    	rtn.isTagedBySelf=isTagedBySelf;
    	if(subType!=null)
    		rtn.subType=new LinkedHashSet<String>(subType);
    	if(noAmbiguitySubType!=null)
        rtn.noAmbiguitySubType=new LinkedHashSet<String>(noAmbiguitySubType);
    	
		super.copy(rtn);
		return rtn;
	}
	private StrNode() {}
	
    public StrNode(String text) {
        super(text);
        type = NodeType.STR_VAL;
        subType = new LinkedHashSet<String>();
        noAmbiguitySubType = new LinkedHashSet<String>();
    }

    @SuppressWarnings("unchecked")
	public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws QPException {
        this.info = k2v.get("infos");
        
		if(info != null){
			String[] temp = info.split("\\|");
        	for(String ids : temp){
				if(ids.contains("id:")){
					String[] idKeyValue = ids.split("\\]\\:id\\:");// "[_公司简称]:id:600030"  按照"]:id:"分割
					if(idKeyValue.length != 2) continue;
					setId(idKeyValue[0].substring(1), idKeyValue[1]);
				}
        	}
        	
        	//保存兼容, 词典全量更新后删除
        	//if(temp.length>0)
        	//	setId("", k2v.get("infos").contains("id:") ? k2v.get("infos").substring("id:".length()) : "")  ;
		}
        if (!k2v.containsKey("prop")) {
            throw new BadDictException("string has no prop field", NodeType.STR_VAL, text);
        }
        String missedIndex = "";
        
        this.props = k2v.get("prop");
        for (String str : k2v.get("prop").split("\\|")) {
        	subType.add(str);//添加subType显示
			
        }
        
    }

    /**
     * 若字符串节点可绑定指标包含“法人代表”、“省份”、“城市”、“股票代码”、“股票简称”、“所属概念”<br>
     * 则不再进行模糊查询
     * 
     * @return 若可模糊查询，返回true；反正，返回false
     */
    public boolean needFuzzySearch() {
        for (String st : subType) {
            if (noFuzzySearchProps.contains(st)) {
                return false;
            }
        }
        return true;
    }

    public String getPubText() {
        String rtn = text.replace(MiscDef.STR_AND, "和");
        rtn = text.replace(MiscDef.STR_OR, "或");

        if (rtn.charAt(0) == '-') {
            return "不包含" + rtn.substring(1);
        } else if (rtn.charAt(0) == '+') {
            return rtn.substring(1);
        } else {
            return rtn;
        }
    }
    public String toString() {
        String out = super.toString();
        out += " subType:"+subType.toString();
        out += info==null ? "" : " info:"+info.toString();
        out += id==null ? "" : " id:"+id.toString();
        return out;
    }

    private static ArrayList<String> noFuzzySearchProps;
    static {
        noFuzzySearchProps = new ArrayList<String>();
        noFuzzySearchProps.add(MiscDef.FAREN_PROP);
        noFuzzySearchProps.add(MiscDef.RPOVINCE_PROP);
        noFuzzySearchProps.add(MiscDef.CITY_PROP);
        noFuzzySearchProps.add(MiscDef.STK_CODE_PROP);
        noFuzzySearchProps.add(MiscDef.STK_NAME_PROP);
        noFuzzySearchProps.add(MiscDef.STK_CONCEPT_PROP);
    }

    
    public final boolean hasSubType(String st) {
        return subType.contains(st);
    }
    
    public boolean hasSubType(Set<String> propSubType,boolean setDefaultIndexSubtype) {
	    if(propSubType == null) return false;
	    for(String st : propSubType)
	    	if(subType.contains(st)){
	    		if(setDefaultIndexSubtype) setDefaultIndexSubtype(st);
	    		return true;
	    	}
	    return false;
    }
    
    public boolean hasSubType(String[] propSubType,boolean setDefaultIndexSubtype) {
      if(propSubType == null) return false;
      for(String st : propSubType)
        if(subType.contains(st)){
          if(setDefaultIndexSubtype) setDefaultIndexSubtype(st);
          return true;
        }
      return false;
    }

    public boolean hasDefaultIndex() {
        for(String st: subType) {
            if(OntoXmlReader.subTypeToIndexContainKey(st))
                return true;
        }
        return false;
    }
    
    public void addNoAmbiguitySubType(String subtype) {
      this.noAmbiguitySubType.add(subtype);
    }
    
    public final boolean hasNoAmbiguitySubType(String st) {
      return noAmbiguitySubType.contains(st);
    }
    
    public void setNoAmbiguityType(String type) {
      this.noAmbiguityType = type;
    }
    
    public String getNoAmbiguityType() {
      return noAmbiguityType;
    }

    public LinkedHashSet<String> subType = null;
    public LinkedHashSet<String> noAmbiguitySubType = null;
    /** 字符串的更多信息 */
    public String info = "";
    public String props = "";
    private HashMap<String, String> id = null;
    public boolean isTag = false;
    private String defaultIndexSubtype="";
    private String noAmbiguityType = "";

    public void setDefaultIndexSubtype(String defaultIndexSubtype){
    	this.defaultIndexSubtype=defaultIndexSubtype;
    }
    
	/**
     * @author: 	    吴永行 
     * @dateTime:	  2015-1-12 下午7:29:11
     */
    public void setDefaultIndexSubtype(PropNodeFacade pn) {
    	for(String strSubType : pn.getSubType())
    		if(pn.getSubType().contains(strSubType)){
    			setDefaultIndexSubtype(strSubType);
    			return;
    		}
    }
    
    public String getDefaultIndexSubtype(){
    	return defaultIndexSubtype;
    }
    /** 该strNode是否由OverlapTrigger标记 */
    public boolean isTagedBySelf = false;
    
    public String getInfo() {
    	if(info == null)
    		info="";
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getProps() {
    if(props == null)
      props="";
    return props;
  }
	
	public void setProps(String props) {
    this.props = props;
  }
	
	public String getId(String subtype) {
		if(this.id == null)
			return Consts.STR_BLANK;
		String typeId = id.get(subtype);
		if(typeId==null) {
			return Consts.STR_BLANK;
		}else{
			return typeId;
		}
	}
	
	public HashMap<String, String> getId() {
		if(this.id == null)
		{
			id = new HashMap<String, String>();
		}
		return this.id;
	}
	
	public void setId(String subTYpe, String id) {
		if (this.id == null) {
			this.id = new HashMap<String, String>();
		}
		if(this.id.containsKey(subTYpe))
			this.id.put(subTYpe, this.id.get(subTYpe)+"|"+id);
		else
			this.id.put(subTYpe, id);
	}
	
	public void setIdForce(String subTYpe, String id) {
    if (this.id == null) {
      this.id = new HashMap<String, String>();
    }
    this.id.put(subTYpe, id);
  }
	
	/*@SuppressWarnings("unchecked")
    @Override
    public StrNode clone() {     
		StrNode cloneNode = (StrNode) super.clone();
		cloneNode.subType=(LinkedHashSet<String>) this.subType.clone();
		cloneNode.defaultIndexSubtype=this.defaultIndexSubtype;
        return cloneNode;
    }*/
    
    /*
     * 比较各属性值是否相等
     * text			内容
     * isCombined	是否被合并
     * isBound		是否被绑定
     * ofWhat		为哪一类
     */
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StrNode))
			return false;
		final StrNode strNode = (StrNode) obj;
		
		if (!this.text.equals(strNode.text))
			return false;
		if (this.isCombined != strNode.isCombined)
			return false;
		if (this.isBoundToIndex() != strNode.isBoundToIndex())
			return false;
		if (this.subType.size() != strNode.subType.size())
			return false;
		for (String st : this.subType)
			if (!strNode.subType.contains(st))
				return false;
		return true;
    }


}
