package com.myhexin.qparser.onto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoSet;
import com.myhexin.qparser.define.EnumDef.IndexType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.node.SemanticNode;
/**
 * 
 * 请不要加public到这个class 上
 * 这个类代表语义网配置,在设计上的意图是保持一份只读的一份
 * 读写的代码请都加载ClassNodeFacade, 其中写的部分要在ClassNodeFacede中的成员变量上
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-3-23
 *
 */
class ClassNode extends BaseOntoNode{
    //属性信息
	protected ArrayList<PropNode> props = new ArrayList<PropNode>();
    //protected HashMap<PropType, ArrayList<PropNode>> classifiedProps = new HashMap<PropType, ArrayList<PropNode>>(); 
    protected ArrayList<PropNode> propsLevel1 = new ArrayList<PropNode>();//自身定义的属性信息
    
    //父类信息
    protected HashSet<String> categorysAll = new HashSet<String>();//类别  add by wyh 2013-12-31
    protected HashSet<String> categorysLevel1 = new HashSet<String>();//直接父类
    protected HashSet<Integer> categorysId1 = new HashSet<Integer>(); //直接父类id, 单独使用文本区分不了 id是后面增加的
    
	//领域信息
    protected HashSet<String> fieldsAll = new HashSet<String>();//领域
    protected HashSet<String> fieldsLevel1 = new HashSet<String>();//自身的领域
    protected HashSet<Query.Type> domains = new HashSet<Query.Type>(); // 可区分的领域，跟type对应
    
    protected ArrayList<ClassNode> superClass = new ArrayList<ClassNode>();
    protected IndexType indexType = IndexType.UNKNOW;
    protected ReportType reportType = null;
    protected ArrayList<String> alias = null;
    protected AmbiguityCondInfoSet ambiguityCondInfoSet = null;
    
    //############### DELETE ######################
    protected PropNode compareProp = null;
    protected ArrayList<ClassNode> subClass = null;
    protected ArrayList<SemanticNode> ofWhat = null;
    // 判断是否转换成了老系统指标
    int id = -1;
	//HashMap<String, RelationShip> relations = new HashMap<String, RelationShip>();
	//private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ClassNode.class);
	//private String indexID = null;
    
	ClassNode(String text) {
		super(text);
	}
	
    /*String getIndexID() {
		return indexID;
	}*/

	/*void setIndexID(String indexID) {
		this.indexID = indexID;
	}*/

	/**
     * 重设参与比较的prop，返回true表示重设成功，否则失败。
     * 
     * @author zd<zhangdong@myhexin.com>
     * @param propName
     * @return
     */
    /*boolean resetCompareProp(String propName) {
    	for (PropNode pn : props) {
            if (pn.getText().equals(propName)) {
                compareProp = pn;
                return true;
            }
        }
        return false;
    }*/

    /**
     * 获取参与比较的prop，如果为null则为指标本身参与比较
     * 
     * @return
     */
    /*PropNode getCompareProp() {
        return this.compareProp;
    }*/

    
    
    boolean isSuperClass(ClassNode cn){
    	if(cn == null) return false;
    	
    	if (cn.text.equals(this.text)) {
			return true;
		}
    	
    	for(ClassNode superNode : superClass){
    		if(superNode.isSuperClass(cn))
    			return true;
    	}
    	return false;
    }
    
    
    
    /**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-6 下午3:19:38
     */
    void getSuperClassInfo(ClassNodeFacade curCN,List<ClassNodeFacade> supClassList) {   	
    	if (supClassList ==null) {
			return;
		}
    	//添加父类信息
    	for(ClassNodeFacade cn : supClassList){       	
    		//得到父类的父类信息
    		curCN.core.fieldsAll.addAll(cn.core.fieldsLevel1);
    		curCN.core.categorysAll.addAll(cn.core.categorysLevel1);
    		addAllProps(curCN.core,cn.core.propsLevel1);
        	getSuperClassInfo(curCN.core,cn.core.getSuperClass());    		
    	}
    }
    
    void getSuperClassInfo(ClassNode curCN,ArrayList<ClassNode> supClassList) {   	
    	if (supClassList ==null) {
			return;
		}
    	//添加父类信息
    	for(ClassNode cn : supClassList){       	
    		//得到父类的父类信息
    		curCN.fieldsAll.addAll(cn.fieldsLevel1);
    		curCN.categorysAll.addAll(cn.categorysLevel1);
    		addAllProps(curCN,cn.propsLevel1);
        	getSuperClassInfo(curCN,cn.getSuperClass());    		
    	}
    }

	/*List<PropNode> getAllProps() {
        return props;
    }*/

    /*PropNode getProp(PropType type) {
        for (PropNode prop : props) {
            if (prop.valueTypeIsMatchOf(type))
                return prop;
        }
        if (subClass != null) {
            for (ClassNode cn : subClass) {
                PropNode pn = cn.getProp(type);
                if (pn != null) {
                    return pn;
                }
            }
        }
        return null;
    }*/

    /*ArrayList<PropNode> getProps(PropType type) {
        ArrayList<PropNode> tmp_props = new ArrayList<PropNode>();
        for (int i = 0; i < props.size(); i++) {
            PropNode propI = props.get(i);
            if (propI.valueTypeIsMatchOf(type)) {
            	tmp_props.add(propI);
            }
        }
        if (subClass != null) {
            for (ClassNode cn : subClass) {
                ArrayList<PropNode> pn = cn.getProps(type);
                for (PropNode p : pn) {
                    if (!props.contains(p)) {
                        props.add(p);
                    }
                }
            }
        }
        props.trimToSize();
        return props;
    }*/

    /*PropNode getPropByUnit(Unit unit) {
        PropNode prop = null;
        ArrayList<PropNode> props = this.getProps(PropType.NUM);
        if (props != null) {
            for (int i = 0; i < props.size(); i++) {
                PropNode propI = props.get(i);
                DBMeta dbm = propI.getDbMeta().get(this);
                if (dbm.unit2ColName_.containsKey(unit)) {
                    prop = props.get(i);
                    break;
                }
            }
        }
        return prop;
    }*/

    /*PropNode getPropByName(String name) {
        for (int i = 0; i < props.size(); i++) {
            PropNode propI = props.get(i);
            if (name.equals(propI.getText())) {
                return propI;
            }
        }
        return null;
    }*/

    /*ArrayList<PropNode> getPropsByUnit(Unit unit) {
        ArrayList<PropNode> props = new ArrayList<PropNode>();
        ArrayList<PropNode> propsNum = this.getProps(PropType.NUM);
        for (int i = 0; i < propsNum.size(); i++) {
            PropNode propI = propsNum.get(i);
            ArrayList<Unit> units = propI.getUnitsByOfWhat(this);
            if (units.contains(unit)) {
                props.add(propI);
            }
        }
        return props;
    }*/

    ClassNode getSubClassByProp(PropNode pn) {
        ClassNode sub = null;
        if (!this.hasProp(pn)) {
            for (ClassNode cn : subClass) {
                if (cn.hasProp(pn)) {
                    sub = cn;
                    break;
                }
            }
        }
        return sub;
    }

    PropNode getNumDateProp() {
        for (int i = 0; i < props.size(); i++) {
            PropNode rtn = props.get(i);
            if (rtn.isNumProp() || rtn.isDateProp()) {
                return rtn;
            }
        }
        return null;
    }

    Unit getValueUnit() {
        PropNode valProp = getValueProp();
        if(valProp == null)
            return Unit.UNKNOWN;
        Unit unit = valProp.getUnitByOfWhat(this);
        unit = unit == null ? Unit.UNKNOWN : unit;
        return unit;
    }

    Unit getValueUnit2() {
        PropNode valProp = getValueProp();
        if(valProp == null)
            return Unit.UNKNOWN;
        Unit unit = valProp.getUnit();
        unit = unit == null ? Unit.UNKNOWN : unit;
        return unit;
    }

    ArrayList<Unit> getValueUnits() {
        PropNode valProp = getValueProp();
        if(valProp == null)
            return null;
        ArrayList<Unit> units = valProp.getUnitsByOfWhat(this);
        return units;
    }

    // 存在三种情况
    // 1、没有值属性，返回null；2、值属性单位为unit，返回new ArrayList<Unit>()；3、值属性存在单位，返回ArrayList<Unit>
    ArrayList<Unit> getValueUnits2() {
        PropNode valProp = getValueProp();
        if(valProp == null)
            return null;
        ArrayList<Unit> units = valProp.getUnits();
        return units;
    }
    
    boolean isThereSubTypeProps() {
    	for (int i = 0; i < props.size(); i++) {
            PropNode rtn = props.get(i);
            if (!(rtn.subType.size()>0)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得本类的值属性
     * 
     * @return
     */
    PropNode getValueProp() {
        for (int i = 0; i < props.size(); i++) {
            PropNode rtn = props.get(i);
            if (rtn.isValueProp()) {
                return rtn;
            }
        }
        return null;
    }

    boolean isNumIndex() {
    	if(this.getValueProp() == null)
            return false;
        return this.getValueProp().isNumProp();
    }

    boolean isDoubleIndex() {
    	if(this.getValueProp() == null)
            return false;
        return this.getValueProp().isDoubleProp();
    }

    boolean isLongIndex() {
    	if(this.getValueProp() == null)
            return false;
        return this.getValueProp().isLongProp();
    }

    boolean isDateIndex() {
    	if(this.getValueProp() == null)
            return false;
        return this.getValueProp().isDateProp();
    }

    boolean isStrIndex() {
    	if(this.getValueProp() == null)
            return false;
        return this.getValueProp().isStrProp();
    }

    boolean isBoolIndex() {
    	if(this.getValueProp() == null)
            return false;
        return this.getValueProp().getText().equals(MiscDef.BOOL_VAL_PROP);
    }

    PropNode getTechProp(PropType type, boolean isParam) {
        for (PropNode prop : props) {
            if (prop.valueTypeIsMatchOf(type)) {
                if (isParam && !MiscDef.TRADE_DATE_PROP.equals(prop.getText()))
                    return prop;
                if (!isParam && MiscDef.TRADE_DATE_PROP.equals(prop.getText()))
                    return prop;
            }
        }
        return null;
    }

    boolean hasProp(PropNode prop) {
        for (PropNode p : props) {
            if (p == prop) // yes, comparing references
                return true;
        }
        return false;
    }

    boolean hasProp(String prop) {
        boolean rtn = false;
        for (PropNode p : props) {
            if (p.getText().equals(prop))
                rtn = true;
            continue;
        }
        return rtn;
    }
    
    final boolean hasTechAnalyPeriodProp(){
    	return hasProp(MiscDef.TECH_ANALY_PERIOD);
    }

    void addProp(PropNode prop) {
    	props.add(prop);
    }
    
    void addPropLevel1(PropNode prop) {
    	propsLevel1.add(prop);
    }
    
    void addAllProps(ArrayList<PropNode> propList) {
    	addAllProps(this,propList);
    }
    
    void addAllProps(ClassNode CurCN,ArrayList<PropNode> propList) {
    	for (PropNode prop : propList) {
			if (!CurCN.hasProp(prop.getText())) {
				CurCN.props.add(prop);
			}
		}    
    }
    
    /*boolean remainDomain(String label) {
    	if (label == null || label.equals(""))
    		return true;
    	return remainDomain(Query.Type.valueOf(label.toUpperCase()));
    }*/
    
    /*boolean remainDomain(Query.Type type) {
    	if (type == null)
    		return true;
    	// 如果没有设定领域，默认为Type.ALL，不设定
		if (domains.isEmpty() || domains.contains(Query.Type.ALL)) {
			domains = new HashSet<Query.Type>();
			domains.add(type);
		} else if (domains.contains(type)) {
			domains = new HashSet<Query.Type>();
			domains.add(type);
		} else if (type == Query.Type.ALL){
			
		} else {
			return false;
		}
		return true;
    }*/

    void addSubClass(ClassNode sub) {
    	subClass.add(sub);
    }

    void addOfWhat(SemanticNode onto) {
    	assert (onto.type == NodeType.PREDICT || onto.type == NodeType.PROP);
        if (ofWhat == null) {
            ofWhat = new ArrayList<SemanticNode>();
        }
        this.ofWhat.add(onto);
    }

    void setSuperClass(ArrayList<ClassNode> superClass) {
    	this.superClass = superClass;
    }

    ArrayList<ClassNode> getSuperClass() {
        return superClass;
    }

    void minSizeOfProps() {
        if (props != null) {
            props.trimToSize();
        }
    }

    public String toString() {
    	return "NodeType:INDEX NodeText:" + text + " Domains: " + domains;
    }

    void setReportType(ReportType reportType) {
    	this.reportType = reportType;
    }

    ReportType getReportType() {
        return reportType;
    }

    void setIndexType(IndexType indexType) {
    	this.indexType = indexType;
    }

    IndexType getIndexType() {
        return indexType;
    }



    boolean isFake() {
        return this instanceof UserClassNode;
    }
    
    void removeProp(PropNode pn){
    	props.remove(pn);
    	propsLevel1.remove(pn);
    }

   
    ArrayList<SemanticNode> getOfWhat() {
    	return ofWhat;
    }
    
    /*boolean isChangeToOld() {
    	return isChangeToOld;
    }*/
    
    void addSuperClass(ClassNode node) {
    	superClass.add(node);
    }
    
    /*void setIsChangeToOld(boolean isChangeToOld) {
    	this.isChangeToOld = isChangeToOld;
    }*/
    
    HashSet<String> getCategorysAll() {
    	return categorysAll;
    }
    
    HashSet<String> getCategorysLevel1() {
    	return categorysLevel1;
    }
    
    void addCategorysLevel1(String str) {
    	categorysLevel1.add(str);
    }
    
    HashSet<String> getFieldsAll() {
    	return fieldsAll;
    }
    
    HashSet<Query.Type> getDomains() {
    	return domains;
    }
    
    void setDomains(HashSet<Query.Type> domain) {
    	this.domains = domain;
    }
    //categorysId1 大部分时候没用,  因此大部分时候不建立
    HashSet<Integer> getCategorysId1() {
    	if(categorysId1==null)
    		categorysId1 =  new HashSet<Integer>();
    	return categorysId1;
    }

    void addFieldsLevel1(String str){
    	fieldsLevel1.add(str);
    	fieldsAll.add(str);
    }
    
	void addCategorysId1(Integer categoryId) {
		if(categorysId1==null)
			this.categorysId1 = new HashSet<Integer>();
    	this.categorysId1.add(categoryId);
    }

    int getId() {
    	return id;
    }

	void setId(int id) {
    	this.id = id;
    }

	/*ArrayList<PropNode> getClassifiedProps(PropType... propTypes) {
		// 1、是否应该在读指标配置文件的时候构建？使用的时候临时构建是否会影响效率，还是说不想浪费内存空间？
		if(props!=null && props.size()>0 && classifiedProps.size()==0){
			getClassifiedProps(props);
		}
		ArrayList<PropNode> returnList = new ArrayList<PropNode>();
		for (PropType pt : propTypes) {
			if(classifiedProps.get(pt)!=null)
				returnList.addAll(classifiedProps.get(pt));
		}
		//如果没有prop返回null  同上面的返回一致
    	return returnList;
	}*/
    
    PropNode getNDateProp() {
    	for (PropNode pn : props) {
			if (pn.isValueProp() && pn.isNDate())
				return pn;
		}
    	return null;
    }
    
	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-1-2 上午10:25:25
	 * @description:  把属性进行分类,保存在 classifiedProps中	
	 * 
	 */
	/*private void getClassifiedProps(ArrayList<PropNode> propList) { //used in clone
		for (PropNode pn : propList) {
			
			if (classifiedProps.containsKey(pn.getValueType())) {
				ArrayList<PropNode> list = classifiedProps.get(pn.getValueType());
				boolean include = false;
				for(PropNode prop : list) {
					if(pn.getText()!=null && pn.getText().equals(prop.getText()) )  {
						include = true;
						break;
					}
				}
				if(include==false) {
					list.add(pn);
				}
			} else {
				ArrayList<PropNode> temp = new ArrayList<PropNode>();
				temp.add(pn);
				classifiedProps.put(pn.getValueType(), temp);
			}
		}
	}*/
	
	void setAlias(String aliasName) {
		if (this.alias == null) {
            this.alias = new ArrayList<String>();
        }
        if (!this.alias.contains(aliasName)) {
            this.alias.add(aliasName);
        }
    }

    ArrayList<String> getAlias() {
        return this.alias;
    }

    boolean hasAlias(String aliasName) {
        return this.alias.contains(aliasName);
    }
    
    AmbiguityCondInfoSet getAmbiguityCondInfoSet() {
        if(ambiguityCondInfoSet == null)
            ambiguityCondInfoSet = new AmbiguityCondInfoSet();
        return this.ambiguityCondInfoSet;
    }
    
    // 对需要区分的领域的处理
    void addDomain(String label) {
    	if (label == null || label.equals(""))
			return ;
		try {
			Query.Type type = Query.Type.valueOf(label.toUpperCase());
			domains.add(type);
		} catch (Exception e) {
			; // 这边只取能区分领域的field加入domains
		}
	}
    
    void addDomain(Query.Type type) {
    	if (type != null)
    		domains.add(type);
    }
    
    /*boolean remainDomain(String label) {
    	if (label == null || label.equals(""))
    		return true;
    	return remainDomain(Query.Type.valueOf(label.toUpperCase()));
    }
    
    boolean remainDomain(Query.Type type) {
    	if (type == null)
    		return true;
    	// 如果没有设定领域，默认为Type.ALL，不设定
		if (domains.isEmpty() || domains.contains(Type.ALL)) {
			domains = new HashSet<Query.Type>();
			domains.add(type);
		} else if (domains.contains(type)) {
			domains = new HashSet<Query.Type>();
			domains.add(type);
		} else if (type == Query.Type.ALL){
			
		} else {
			return false;
		}
		return true;
    }*/
    String getText() {
    	return text;
    }

    
    /*
     * 比较各属性值是否相等
     * text			内容
     * isCombined	是否被合并
     * isBound		是否被绑定
     * props		属性是否相同
     */
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ClassNode))
			return false;
		final ClassNode cn = (ClassNode) obj;
		
		if (!this.text.equals(cn.text))
			return false;
		/*if (this.isCombined != cn.isCombined)
			return false;
		if (this.isBoundToIndex != cn.isBoundToIndex)
			return false;
		*/
		if (this.props.size() != cn.props.size())
			return false;
		if (!(this.domains.containsAll(cn.domains) && cn.domains.containsAll(this.domains))) {
			return false;
		}
		for (PropNode pn : this.props)
			if (!cn.hasProp(pn.getText()))
				return false;
		return true;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-5-14 下午1:54:58
     * @description:   	
     * @return
     * 
     */
    boolean hasNDayPrpop() {
	    for (PropNode pn : props) {
			if(pn.isNDate())
				return true;
		}
	    return false;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-12-17 上午10:27:13
     * @description:   	
     */
    void clear() {
    	/*for(PropNode pn : getAllProps())
    		if(pn.getValue()!=null)
    			pn.setValue(null);*/
    }
    
    ClassNodeFacade toFacade() {
    	ClassNodeFacade f = new ClassNodeFacade();
    	f.core = this;
    	return f;
    }

}
