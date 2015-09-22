package com.myhexin.qparser.onto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.node.DBMeta;
import com.myhexin.qparser.node.SemanticNode;

public class PropNodeFacade extends OntoNode  {

	private List<Unit> valueUnit_ = null;
	
	protected PropNode core;
	 /** zd added on 2013-05-30*/
    public PropNodeFacade(){
    	type = NodeType.PROP;
    }
    public PropNodeFacade(String title) {
    	core = new PropNode(title);
    	this.text = title;
    	type = NodeType.PROP;
    }
    
	public void setPropNode(PropNode prop) {
		this.core = prop;
	}
	
	public String getText() {
		return core.getText();
	}
	
	@Override
    public String getPubText() {
		if(core.text!=null)
			return core.text.charAt(0) == '_' ? core.text.substring(1) : core.text;
		else
			return null;
    }
	
	private SemanticNode value = null;

	private List<SemanticNode> values = new ArrayList<SemanticNode>();
    // 获得属性值
    public SemanticNode getValue() {
    	return value;
    }
    
    public void setValue(SemanticNode value) {
    	this.value = value;
    }
    
    public boolean valueTypeIsMatchOf(PropType propType) {
        return core.valueTypeIsMatchOf(propType);
    }
    
    public boolean valueTypeIsMatchOf(PropNodeFacade pn) {
    	return core.valueTypeIsMatchOf(pn.core);
    }
    
    public boolean isValueProp(){
        return  core.isValueProp() ;
    }
	
    public boolean isNumDate() {
        return core.isNumDate();
    }
    
    public boolean isBoolProp(){
    	return core.isBoolProp();
    }


    //TODO remove this function
    public boolean isDBProp() {
        return core.isDBProp();
    }
	
	public boolean isDateProp(){
		return core.isDateProp();
    }
    
    public boolean isNumProp() {
        return core.isNumProp();
    }
    
    public boolean isNDate(){
    	return core.isNDate();
    }
    
    public boolean isStrProp(){
    	return core.isStrProp();
    }
    
    public boolean isIndexProp(){
    	return core.isIndexProp();
    }
    
    public boolean isDoubleProp(){
        return core.isDoubleProp();
    }

    public boolean isLongProp(){
        return core.isLongProp();
    }

    public boolean isTechPeriodProp() {
        return core.isTechPeriodProp();
    }
    
	public boolean isConsistPeriodProp() {
		return core.isConsistPeriodProp();
	}
    
    public boolean isFake() {
    	return core.isFake();
    }
    
    public boolean subTypeContain(String subType) {
    	return core.subType.contains(subType);
    }
    
    public boolean isTextEqual(PropNodeFacade prop){
    	return core.isTextEqual(prop.core);
    }
    
    public PropType getValueType(){
    	return core.getValueType();
    }
    
   
    public int getUnitIndex(Unit unit) {
    	return core.getUnitIndex(unit);
    }
     
	@Override
	protected PropNodeFacade copy() {
		PropNodeFacade f = new PropNodeFacade();
		f.core = this.core;//.copy();
		//f.value = this.value;
		super.copy(f);
		return f;
		
	}
	
	public PropNodeFacade copyWithValues() {
		PropNodeFacade f = copy();
		f.value = value;
		f.valueUnit_ = valueUnit_;
		return f;
	}
	
	public void addValueUnit(Unit unit){
		if(valueUnit_==null) {
			valueUnit_ = new ArrayList<Unit>(core.getValueUnit());
		}
		valueUnit_.add(unit);
	}
	
	//TODO 这里是有问题的
	public Map<ClassNodeFacade,DBMeta> getDbMeta() {
		Map<ClassNode,DBMeta> map = core.getDbMeta();
		Map<ClassNodeFacade,DBMeta> new_map = new HashMap<ClassNodeFacade, DBMeta>();
		Set<ClassNode> kList = map.keySet();
		for(Iterator<ClassNode> it = kList.iterator();it.hasNext();) {
			ClassNode k  = it.next();
			DBMeta v = map.get(k);
			new_map.put(k.toFacade(), v);
		}
		return new_map;
	}
	
	public Unit getUnitByOfWhat(ClassNodeFacade ofWhat) {
		return core.getUnitByOfWhat(ofWhat.core);
	}

	
	/*public void addDbMeta(ClassNodeFacade node, DBMeta value) {
		core.addDbMeta(node.core, value);
	}*/

	 /*public void setValueProp(boolean isValueProp) {
		 core.setValueProp(isValueProp);
	 }*/
	 
	@Override
	public void parseNode(HashMap<String, String> k2v, Type qtype)
			throws QPException {
		
	}
	
	public String toString() {
		if(this.value!=null)
			return core.toString() +", value=" + value.getText();
		else
			return core.toString();
	}

	
	
	public void addOfWhat_forPhraseXmlReaderOnly(ClassNodeFacade onto) {
		core.addOfWhat(onto.core);
	}
	
	public void setValueType_forPhraseXmlReaderOnly(PropType valueType_) {
		core.setValueType(valueType_);
	}
	
    //unmodifiableXXX functions, 找到用到这些List的地方,把这些地方的代码逻辑移到这里
    //而不是返回unmodifiableXXX,unmodifiableCollections加了同步锁,影响性能
    //###########################################################
	public List<ClassNodeFacade> getOfWhat() {
    	List<ClassNode> list = core.getOfWhat();
    	if(list!=null)
    		return ClassNodeFacade.toClassNodeFacadeList(list);
    	else{
    		return null;
    	}
    }
	
    public List<Unit> getValueUnit() {
    	if(valueUnit_==null) {
			valueUnit_ = new ArrayList<Unit>(core.getValueUnit());
		}
    	return valueUnit_;
    	/*if(core.getValueUnit()!=null)
    		return Collections.unmodifiableList(core.getValueUnit());
    	else{
    		return null;
    	}*/
    }
    
    public List<Unit> getUnits() {
    	if(core.getUnits()!=null)
    		return Collections.unmodifiableList(core.getUnits());
    	else{
    		return null;
    	}
    }

	public Set<String> getSubType() {
		if(core.subType!=null)
			return Collections.unmodifiableSet(core.subType);
		else
			return null;
	}
	
	public List<SemanticNode> getValues() {
		return values ;
	}
	
	public void addValues(SemanticNode value) {
		this.values.add(value);
	}
	
	public void addValuesAsFisrtValue(SemanticNode value) {
		this.values.add(0,value);
	}
	
	public void setValues(List<? extends SemanticNode> nodes) {
		this.values.addAll(nodes);
	}
	
}
