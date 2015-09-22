package com.myhexin.qparser.onto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.node.DBMeta;
import com.myhexin.qparser.node.SemanticNode;

/**
 * 
 * 请不要加public到这个class 上
 * 这个类代表语义网配置,在设计上的意图是保持一份只读的一份
 * 读写的代码请都加载PropNodeFacade, 其中写的部分要在PropNodeFacade中的成员变量上
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-3-23
 *
 */
class PropNode extends BaseOntoNode{
	//protected static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PropNode.class.getName());
    
    boolean isValueProp = false;
	boolean hasFuzzyVal_ = false;
    PropType valueType_ = PropType.UNKNOWN;
    protected ArrayList<Unit> valueUnit_ = new ArrayList<Unit>();
    protected HashMap<ClassNode,DBMeta> dbMeta_ = new HashMap<ClassNode,DBMeta>();
    protected ArrayList<ClassNode> ofWhat_ = new ArrayList<ClassNode>();
    protected ArrayList<ClassNode> valueClass_ = new ArrayList<ClassNode>();
    double min = 0;
    double max = 0;
   
    
    
    PropNode(String text) {
       super(text);
    }
    
    PropNodeFacade toFacade() {
    	PropNodeFacade f = new PropNodeFacade("");
    	f.setPropNode(this);
    	return f;
    }
    
    String getText() {
    	return text;
    }

    void addOfWhat(ClassNode onto) {
    	if (ofWhat_ == null) {
            ofWhat_ = new ArrayList<ClassNode>();
        }
        this.ofWhat_.add(onto);
    }

    void addClassValue(ClassNode onto) {
    	if (valueClass_ == null) {
            valueClass_ = new ArrayList<ClassNode>();
        }
        this.valueClass_.add(onto);
    }

    boolean hasClassValue(ClassNode cn) {
        if (valueClass_ == null)
            return false;
        for (ClassNode t : valueClass_) {
            if (t == cn)
                return true;
        }
        return false;
    }

    boolean isNumDate() {
        return valueType_ == PropType.NUM || valueType_ == PropType.DOUBLE
                || valueType_ == PropType.LONG || valueType_ == PropType.DATE;
    }
    
    boolean isBoolProp(){
    	return valueType_ == PropType.BOOL;
    }

    //TODO remove this function
    boolean isDBProp() {
        return false;//dataSource_==DataSource.DB;
    }
    
    int getUnitIndex(Unit unit){
        int index = -1;
        for (int i = 0; i < valueUnit_.size(); i++) {
            if (valueUnit_.get(i) == unit) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    final boolean isValueProp(){
        return  isValueProp ;
    }
    
    
    public String toString() {
    	return text +", isValueProp=" + isValueProp ;
    }

    int getOfwhatIndex(SemanticNode node) {
        int index = -1;
        if (node.type == NodeType.CLASS || node.type == NodeType.PREDICT) {
            String name = node.getText();
            for (int i = 0; i < ofWhat_.size(); i++) {
                if (ofWhat_.get(i).getText().equals(name)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
    
    DBMeta getDBMeta(SemanticNode node) {

        int ofWhatIndex = this.getOfwhatIndex(node);
        DBMeta db = null;
        if (ofWhatIndex == -1) {
            String err = "所查询node“" + node.getText() + "”不是prop的ofWhat";
            //logger_.warn("UE:"+err);
        }else{
            db= dbMeta_.get(node);
        }
        return db;
    }

    Unit getUnitByOfWhat(ClassNode ofWhat) {
        //System.out.println("\t\t\t\t:ofwhat:"+ofWhat); 
        DBMeta db = dbMeta_.get(ofWhat);
        //System.out.println("\t\t\t\t:ofwhat:"+ofWhat+" db: "+ db); 
        if(db == null) { return null; }
        for(Unit pu : db.unit2ColName_.keySet()) {
            if(pu != Unit.UNKNOWN) return pu;
        }
        return null;
    }

    Unit getUnit()
    {
        for(Unit pu: valueUnit_)
        {
            if(pu != Unit.UNKNOWN) return pu;
        }
        return null;
    }

    ArrayList<Unit> getUnits()
    {
        ArrayList<Unit> units = new ArrayList<Unit>();
        for(Unit pu: valueUnit_)
        {
            if(pu != Unit.UNKNOWN) units.add(pu);
        }
        return units;
    }
    
    /**
     * 取得对应OfWhat的所有可能单位
     * @param ofWhat
     * @return
     */
    ArrayList<Unit> getUnitsByOfWhat(ClassNode ofWhat) {
        DBMeta db = dbMeta_.get(ofWhat);
        ArrayList<Unit> units = new ArrayList<Unit>();
        if (db == null) {
            return units;
        }
        for (Unit unit : db.unit2ColName_.keySet()) {
            if (!units.contains(unit))
                units.add(unit);
        }
        return units;
    }
    
    double getMin() {
		return min;
	}

	void setMin(double min) {
		this.min = min;
	}

	double getMax() {
		return max;
	}

	void setMax(double max) {
		this.max = max;
	}
	
    
    void setValueType(PropType valueType_) {
    	this.valueType_ = valueType_;
    }

    PropType getValueType() {
        return valueType_;
    }
    
    List<Unit> getValueUnit(){
    	return valueUnit_;
    }
    
    boolean isDateProp(){
        return valueType_==PropType.DATE;
    }
    
    boolean isNumProp() {
        return valueType_ == PropType.NUM || valueType_ == PropType.DOUBLE
                || valueType_ == PropType.LONG;
    }
    
    boolean isNDate(){
    	return  text.equals("n日");
    }
    
    boolean isStrProp(){
        return valueType_== PropType.STR;
    }
    
    boolean isIndexProp(){
    	return valueType_ == PropType.INDEX;
    }
    
    boolean isDoubleProp(){
        return valueType_==PropType.DOUBLE;
    }

    boolean isLongProp(){
        return valueType_==PropType.LONG;
    }

    boolean isTechPeriodProp() {
        return valueType_ == PropType.TECH_PERIOD;
    }
    
	boolean isConsistPeriodProp() {
		return valueType_ == PropType.CONSIST_PERIOD;
	}
    
    boolean isFake() {
        return this instanceof UserPropNode;
    }
    
    Map<ClassNode,DBMeta> getDbMeta() {
    	return dbMeta_;
    }
    
    List<ClassNode> getOfWhat() {
    	return ofWhat_;
    }
    
    void addValueUnit(Unit unit){
    	this.valueUnit_.add(unit);
    }
    
    void addDbMeta(ClassNode node, DBMeta value) {
    	dbMeta_.put(node, value);
    }
    
    
    void setValueProp(boolean isValueProp) {
    	this.isValueProp = isValueProp;
    }
    
    /** wyh added on 2013-12-30  2014.11.11*/
    protected LinkedHashSet<String> subType = new LinkedHashSet<String>();    
    
	/** 判断两个prop的text是否相等，可以当做prop相等判断 */
    boolean isTextEqual(PropNode pn) {
        assert (pn.text != null && this.text != null);
        return this.text.equals(pn.text);
    }
    
    boolean valueTypeIsMatchOf(PropType propType) {
        return propType == PropType.NUM && isNumProp()
        || valueType_ == propType;
    }
    
    boolean valueTypeIsMatchOf(PropNode pn) {
    	return isNumProp() && pn.valueType_ == PropType.NUM
    	        || valueType_ == pn.valueType_;
    }
    //protected SemanticNode value = null;
    // 获得属性值
    /*SemanticNode getValue() {
    	return value;
    }*/
    
    /*void setValue(SemanticNode value) {
    	this.value = value;
    }*/
	/*if(value!=null)
	{
		rtn.value=value.copy();
	}*/
    /*PropNode copy() {
		PropNode rtn = new PropNode(super.text);
		if(dbMeta_!=null)
			rtn.dbMeta_.putAll(dbMeta_);
		rtn.hasFuzzyVal_=hasFuzzyVal_;
		rtn.isValueProp=isValueProp;
		rtn.max=max;
		rtn.min=min;
		
		if(ofWhat_!=null)
			rtn.ofWhat_.addAll(ofWhat_);
		
		if(subType!=null)
			rtn.subType.addAll(subType);

		
		if(valueClass_!=null)
			rtn.valueClass_.addAll(valueClass_);
		rtn.valueType_=valueType_;
		
		if(valueUnit_!=null)
		rtn.valueUnit_.addAll(valueUnit_);

		return rtn;
	}*/
}
