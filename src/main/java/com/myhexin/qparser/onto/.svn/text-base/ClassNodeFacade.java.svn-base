package com.myhexin.qparser.onto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoSet;
import com.myhexin.qparser.define.EnumDef.DataSource;
import com.myhexin.qparser.define.EnumDef.IndexType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.node.DBMeta;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.Util;

public class ClassNodeFacade extends OntoNode {
	
	
	//add by wyh 2014.2.19
    //是否绑定了东西 到该指标上  显示时使用
	private boolean isBoundValueToThis = false;
    private boolean isChangeToOld = false;
    private DataSource dataSrc = null;
    private List<Unit> facadeUnits;
    private Set<Query.Type> facadeDomains = null;
    private List<PropNodeFacade> all_props = null;
    
	protected ClassNode core;
	public ClassNodeFacade(){
		core = new ClassNode("");
		type = NodeType.CLASS;
	}
	public ClassNodeFacade(String title) {
		this();
		core.text = title;
		type = NodeType.CLASS;
		
	}
	
	public String getText() {
		return core.getText();
	}
	
	public int getId() {
		return core.getId();
	}
	
	public String toString() {
		return core.toString();
    	/*String s1= core.toString() + ", ";
    	if(all_props!=null) {
    		for(PropNodeFacade p : all_props) {
    			if(p.getValue()!=null) {
    				s1 += p.getText() + "=" + p.getValue() + ",";
    			}
    		}
    	}
    	return s1;*/
    }
	
	
	/*
	 * facadeDomains里头存的是STOCK,FUND,HKSTOCK,HGHY,SEARCH这些domain
	 * 
	 */
	private void check_domains() {
		if(facadeDomains==null || core.domains!=null) {
			facadeDomains = new HashSet<Query.Type>(core.domains);
		}
		
		if(facadeDomains==null ) {
			facadeDomains=new HashSet<Query.Type>();
		}
		
		/*if(facadeDomains.isEmpty()){
			facadeDomains.add(Query.Type.ALL);
		}*/
	}
	
	public Set<Query.Type> getDomains() {
		check_domains();
		
		return facadeDomains;
	}
	
	
	
	public boolean isDoubleIndex() {
		return core.isDoubleIndex();
	}
	
	public boolean hasNDayPrpop() {
		return core.hasNDayPrpop();
	}
    
	public boolean isSuperClass(ClassNodeFacade cn) {
		return core.isSuperClass(cn.core);
	}
    
    public AmbiguityCondInfoSet getAmbiguityCondInfoSet() {
    	return core.getAmbiguityCondInfoSet();
    }
    public ReportType getReportType() {
    	return core.getReportType();
    }
    
    public void removeProp(PropNodeFacade pn){
    	core.removeProp(pn.core);
    }
    
    public PropNodeFacade getNDateProp() {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	for(PropNodeFacade pf : all_props) {
    		if (pf.isValueProp() && pf.isNDate()){
	   			return pf;
	   		}
	   	}
	   	return null;
    	/*PropNode p= core.getNDateProp();
    	if(p!=null) {
    		return p.toFacade();
    	}else{
    		return null;
    	}*/
    }
 
    
    public PropNodeFacade getCompareProp() {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	/*for (PropNode pn : props) {
            if (pn.getText().equals(propName)) {
                compareProp = pn;
                return true;
            }
        }*/
        return null;
    	
    	/*PropNode p =  core.getCompareProp();
    	if(p!=null) {
    		return p.toFacade();
    	}else{
    		return null;
    	}*/
    }
    public String getUniqId() {
    	ClassNodeFacade cn = this;
		StringBuilder str = new StringBuilder(String.format("[%s:%s:%s]",  this.getDataSrc(), cn.getText(), core.domains));
		// 指标属性
		List<PropNodeFacade> pNodes = cn.getAllProps();
		if (pNodes != null && pNodes.size() > 0) {
			for (int i = 0; i < pNodes.size(); i++) {
				PropNodeFacade prop = pNodes.get(i);
				SemanticNode propValue = pNodes.get(i).getValue();
				if (propValue == null || propValue.getText() == null || propValue.getText().length() == 0) {
					continue;
				} else if (prop.isValueProp()) {
					continue;
				} else {
					str.append(String.format("param[%s;val=%s]", prop.getText(), propValue.getText()));
				}
			}
		}
					
        String uniqIdStr = str.toString();
        return Util.encoderByMd5(uniqIdStr);
	}
    
    protected static List<PropNodeFacade> toFacadeList(List<PropNode> props) {
    	if(props!=null) {
			List<PropNodeFacade> new_props= new ArrayList<PropNodeFacade>(props.size());
			for(PropNode p : props) {
				new_props.add(p.toFacade());
			}
			return new_props;
		}
		return null;
    }
    
    protected static List<ClassNodeFacade> toClassNodeFacadeList(Collection<ClassNode> cnList) {
    	if(cnList!=null) {
			List<ClassNodeFacade> new_props= new ArrayList<ClassNodeFacade>(cnList.size());
			for(ClassNode cn : cnList) {
				new_props.add(cn.toFacade());
			}
			return new_props;
		}
		return null;
    }
    
    public boolean isDateIndex() {
    	return core.isDateIndex();
    }
    
    public boolean isLongIndex() {
    	return core.isLongIndex();
    }
    
    public boolean isFake() {
    	return core.isFake();
    }
    
    public boolean isStrIndex() {
    	return core.isStrIndex();
    }
    
    public void clearPropValues() {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	if(all_props!=null) {
    		for(PropNodeFacade pf : all_props) {
        		//if(pf.isValueProp()) {
        			pf.setValue(null);
        		//}
        	}
    	}
    	
    }
    
    public PropNodeFacade getPropOfValue() {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	
    	for(PropNodeFacade pf : all_props) {
    		if(pf.isValueProp()) {
    			return pf;
    		}
    	}
    	return null;
    	/*PropNode prop = core.getValueProp();
    	if(prop!=null)
    		return prop.toFacade();
    	else{
    		return null;
    	}*/
    }
    
    public PropNodeFacade getNumDateProp() {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	for(PropNodeFacade pf : all_props) {
    		 if (pf.isNumProp() || pf.isDateProp()) {
    			return pf;
    		}
    	}
    	return null;
    	/*PropNode p =  core.getNumDateProp();
    	if(p!=null) {
    		return p.toFacade();
    	}else{
    		return null;
    	}*/
    }
    
    public PropNodeFacade getProp(PropType type) {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	
    	for (PropNodeFacade prop : all_props) {
            if (prop.valueTypeIsMatchOf(type))
                return prop;
        }
    	
        if (core.subClass != null) {
            for (ClassNode cn : core.subClass) {
            	ClassNodeFacade fn = cn.toFacade();
                PropNodeFacade pn = fn.getProp(type);
                if (pn != null) {
                    return pn;
                }
            }
        }
        return null;
        
    	/*(PropNode p = core.getProp(type);
    	if(p!=null) {
    		return p.toFacade();
    	}else{
    		return null;
    	}*/
    }
    public IndexType getIndexType() {
    	return core.getIndexType();
    }
    
    public Unit getValueUnit() {
    	return core.getValueUnit();
    }
    
    public boolean isNumIndex() {
    	return core.isNumIndex();
    }
    public boolean hasProp(PropNodeFacade prop) {
    	return core.hasProp(prop.core);
    }
    
    public boolean hasTechAnalyPeriodProp() {
    	return core.hasTechAnalyPeriodProp();
    			
    }
    
    public boolean hasProp(String prop) {
    	return core.hasProp(prop);
    }

    public PropNodeFacade getPropByUnit(Unit unit) {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	PropNodeFacade prop = null;
        List<PropNodeFacade> temp_props = this.getProps(PropType.NUM);
        if (temp_props != null) {
            for (int i = 0; i < temp_props.size(); i++) {
            	PropNodeFacade propI = temp_props.get(i);
                DBMeta dbm = propI.getDbMeta().get(this);
                if (dbm.unit2ColName_.containsKey(unit)) {
                    prop = temp_props.get(i);
                    break;
                }
            }
        }
        return prop;
    	
    	/*PropNode p  = core.getPropByUnit(unit);
    	if(p!=null) {
    		return p.toFacade();
    	}else{
    		return null;
    	}*/
    }
    
    public PropNodeFacade getPropByName(String name) {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	 for (int i = 0; i < all_props.size(); i++) {
             PropNodeFacade propI = all_props.get(i);
             if (name.equals(propI.getText())) {
                 return propI;
             }
         }
         return null;
    	
    	/*PropNode p= core.getPropByName(key);
    	if(p!=null) {
    		return p.toFacade();
    	}else{
    		return null;
    	}*/
    }
    
    public boolean isChangeToOld() {
    	return this.isChangeToOld;
    }
    
    public boolean isBoolIndex() {
    	return core.isBoolIndex();
    }
    
    public PropNodeFacade getPropOfTech(PropType type, boolean isParam) {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	for(PropNodeFacade pf : all_props) {
    		if (pf.valueTypeIsMatchOf(type)) {
                if (isParam && !MiscDef.TRADE_DATE_PROP.equals(pf.getText()))
                    return pf;
                if (!isParam && MiscDef.TRADE_DATE_PROP.equals(pf.getText()))
                    return pf;
            }
    	}
    	return null;
    	/*PropNode p = core.getTechProp(type, isParam);
    	if(p!=null) {
    		return p.toFacade();
    	}else{
    		return null;
    	}*/
    }
    
	
	@Override
	protected ClassNodeFacade copy() {
		ClassNodeFacade f = new ClassNodeFacade();
		f.core = this.core; //.copy();
		f.isBoundValueToThis = this.isBoundValueToThis;
		f.isChangeToOld = isChangeToOld;
		f.dataSrc = dataSrc;
		//f.clearPropValues();
		super.copy(f);
		return f;
	}
	
	protected ClassNodeFacade copyWithValues() {
		ClassNodeFacade f = copy();
		if(this.all_props!=null) {
			f.all_props = new ArrayList<PropNodeFacade>(this.all_props.size());
			for(PropNodeFacade p : this.all_props) {
				f.all_props.add(p.copyWithValues());
			}
		}
		
		if(this.facadeUnits!=null) {
			f.facadeUnits = this.facadeUnits;
		}
		
		if(this.facadeDomains!=null) {
			f.facadeDomains = this.facadeDomains;
		}
		super.copy(f);
		return f;
	}

	@Override
	public void parseNode(HashMap<String, String> k2v, Type qtype)
			throws QPException {
		// TODO Auto-generated method stub
		
	}

    public boolean isBoundValueToThis() {
    	return isBoundValueToThis;
    }
    
    public void setIsBoundValueToThis(boolean flag) {
    	this.isBoundValueToThis = flag;
    }
    //
    public boolean remainDomain(String label) {
    	if (label == null || label.equals(""))
    		return true;
    	return remainDomain(Query.Type.valueOf(label.toUpperCase()));
    }
    
    public boolean remainDomain(Query.Type type) {
    	return doRemainDomain(type);
    }
    
    private boolean doRemainDomain(Query.Type type) {
    	check_domains();
    	
    	if (type == null)
    		return true;
    	// 如果没有设定领域，默认为Type.ALL，不设定
		if (facadeDomains.isEmpty() || facadeDomains.contains(Query.Type.ALL)) {
			facadeDomains = new HashSet<Query.Type>();
			facadeDomains.add(type);
		} else if (facadeDomains.contains(type)) {
			facadeDomains = new HashSet<Query.Type>();
			facadeDomains.add(type);
		} else if (type == Query.Type.ALL){
			
		} else {
			return false;
		}
		return true;
    }

    public void addProp_forPhraseXmlReaderSemanticPatternUseOnly(PropNodeFacade prop) {
    	core.addProp(prop.core);
    }
    
    public void setAlias_ForAmbiguity(String aliasName) {
    	core.setAlias(aliasName);
    }

    public void setDataSrc(DataSource dataSrc) {
    	this.dataSrc = dataSrc;
	}

    public DataSource getDataSrc() {
	    return dataSrc;
	}
    
    public void setIsChangeToOld(boolean isChangeToOld) {
    	this.isChangeToOld = isChangeToOld;
    }
    
    
    public void addValueUnit2(Unit unit) {
    	if(facadeUnits==null && core.getValueUnits2()!=null) {
    		facadeUnits = new ArrayList<Unit>(core.getValueUnits2());
    	}
    	if(facadeUnits==null) {
    		facadeUnits = new ArrayList<Unit>();
    	}
    	facadeUnits.add(unit);
    }

    public List<Unit> getValueUnits2() {
    	if(facadeUnits==null && core.getValueUnits2()!=null) {
    		facadeUnits = new ArrayList<Unit>(core.getValueUnits2());
    	}
    	return facadeUnits;
    }
    /**
     * @deprecated
     * @param indexID
     */
    public void setIndexID(String indexID) {
    	//core.setIndexID(indexID);
    	//ifindIndexId = indexID;
    }
    //private String ifindIndexId = null;
    
    
    
    
    //unmodifiableXXX functions, 找到用到这些List的地方,把这些地方的代码逻辑移到这里
    //而不是返回unmodifiableXXX,unmodifiableCollections加了同步锁,影响性能
    //###########################################################
    
    public List<PropNodeFacade> getAllProps() {
		if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
		return all_props;
		/*List<PropNodeFacade> pList = toFacadeList(props);
		if(pList!=null)
			return Collections.unmodifiableList(pList);
		else{
			return null;
		}*/
	}
	
	public List<String>  getAlias() {
		if(core.getAlias()!=null)
			return Collections.unmodifiableList(core.getAlias());
		else{
			return null;
		}
	}
	
	public Set<String> getCategorysLevel1() {
		if(core.getCategorysLevel1()!=null)
			return Collections.unmodifiableSet(core.getCategorysLevel1());
		else
			return null;
	}
	 
	public Set<Integer> getCategorysId1() {
		if(core.getCategorysId1()!=null)
			return Collections.unmodifiableSet(core.getCategorysId1());
		else{
			return null;
		}
	}

    public List<Unit> getValueUnits() {
    	if(core.getValueUnits()!=null)
    		return Collections.unmodifiableList(core.getValueUnits());
    	else{
    		return null;
    	}
    }
    
    public List<SemanticNode> getOfWhat() {
    	//return core.getOfWhat();
    	if(core.getOfWhat()!=null)
    		return Collections.unmodifiableList(core.getOfWhat());
    	else{
    		return null;
    	}
    }

    
    public List<ClassNodeFacade> getSuperClass() {
    	ArrayList<ClassNode> list  = core.getSuperClass();
    	List<ClassNodeFacade> pList = toClassNodeFacadeList(list);
    	if(pList!=null)
    		return Collections.unmodifiableList(pList);
    	else{
    		return null;
    	}
    }

    
    public List<PropNodeFacade> getPropsByUnit(Unit unit) {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	
    	ArrayList<PropNodeFacade> props = new ArrayList<PropNodeFacade>();
        List<PropNodeFacade> propsNum = this.getProps(PropType.NUM);
        for (int i = 0; i < propsNum.size(); i++) {
        	PropNodeFacade propI = propsNum.get(i);
            ArrayList<Unit> units = propI.core.getUnitsByOfWhat(this.core);
            if (units.contains(unit)) {
                props.add(propI);
            }
        }
        return props;
    	
    	/*List<PropNode> list = core.getPropsByUnit(unit);
    	List<PropNodeFacade> pList = toFacadeList(list);
    	if(pList!=null)
    		return Collections.unmodifiableList(pList);
    	else{
    		return null;
    	}*/
    }
    
    public List<PropNodeFacade> getClassifiedProps(PropType... propTypes) {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	
    	
    	List<PropNodeFacade> returnList = new ArrayList<PropNodeFacade>();
    	for(PropNodeFacade pn : all_props) {
    		for (PropType pt : propTypes) {
    			if(pn.getValueType()==pt) {
    				returnList.add(pn);
    				break;
    			}
    		}
    	}
		
		//如果没有prop返回null  同上面的返回一致
    	return returnList;
    	
    	//List<PropNode> list= core.getClassifiedProps(propTypes);
    	/*List<PropNodeFacade> pList = toFacadeList(list);
    	if(pList!=null)
    		return Collections.unmodifiableList(pList);
    	else{
    		return null;
    	}*/
    }

    
    public Set<String> getCategorysAll() {
    	return core.getCategorysAll();
    }

    
    public Set<String> getFieldsAll() {
    	return core.getFieldsAll();
    }
    
    
    
    public List<PropNodeFacade> getProps(PropType type) {
    	if(all_props==null) {
			if(core.props!=null)
				all_props = toFacadeList(core.props); 
		}
    	List<PropNodeFacade> props = new ArrayList<PropNodeFacade>();
         for (int i = 0; i < all_props.size(); i++) {
        	 PropNodeFacade propI = all_props.get(i);
             if (propI.valueTypeIsMatchOf(type)) {
                 props.add(propI);
             }
         }
         if (core.subClass != null) {
             for (ClassNode cn : core.subClass) {
            	 ClassNodeFacade fn = cn.toFacade();
                 List<PropNodeFacade> pns = fn.getProps(type);
                 for (PropNodeFacade p : pns) {
                     if (!props.contains(p)) {
                         props.add(p);
                         all_props.add(p);
                     }
                 }
             }
         }
         //props.trimToSize();
         return props;
    	
    	
    	/*List<PropNode> list = core.getProps(type);
    	List<PropNodeFacade> pList = toFacadeList(list);
    	if(pList!=null)
    		return Collections.unmodifiableList(pList);
    	else{
    		return null;
    	}*/
    }
    
}
