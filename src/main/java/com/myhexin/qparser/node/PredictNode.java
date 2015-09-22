package com.myhexin.qparser.node;

import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;

public final class PredictNode extends SemanticNode {
	@Override
	protected SemanticNode copy() {
		PredictNode rtn = new PredictNode(super.text);
		rtn.isPassive=isPassive;
		rtn.objs_=objs_;
		rtn.objValue_=objValue_;
		rtn.props_=props_;
		rtn.sbjs_=sbjs_;

		super.copy(rtn);
		return rtn;
	}
	private PredictNode(){}
    public PredictNode(String text) {
        super(text);
        type = NodeType.PREDICT;
    }
    
    public void initByNormPredict(PredictNode norm){
        this.sbjs_ = norm.objs_;
        this.objs_ = norm.sbjs_;
        this.props_ = norm.props_;
        this.isPassive = true;
    }
    
    public PropNodeFacade getProp(PropType type){
        for (int i = 0; i < props_.size(); i++) {
            PropNodeFacade propI = props_.get(i);
            if (propI.valueTypeIsMatchOf(type))
                return props_.get(i);
        }
        return null;
    }

    public ArrayList<PropNodeFacade> getProps(PropType type) {
        ArrayList<PropNodeFacade> props = new ArrayList<PropNodeFacade>();
        for (PropNodeFacade prop : props_) {
            if (prop.valueTypeIsMatchOf(type))
                props.add(prop);
        }
        return props;

    }
    
    public PropNodeFacade getPropsByUnit(Unit unit){
        PropNodeFacade prop = null;
        ArrayList<PropNodeFacade>  props = this.getProps(PropType.NUM);
        if(props!=null){
            for(PropNodeFacade pn :props ){
                if(pn.getUnitIndex(unit)!=-1){
                    prop = pn;
                    break;
                }
            }
        }
        return prop;
    }
    
    public PropNodeFacade getNumDateProp(){
        for(int i = 0; i < props_.size(); i++){
            PropNodeFacade rtn = props_.get(i);
            if(rtn.isNumProp() ||
                    rtn.isDateProp()){
                return rtn;
            }
        }
        return null;
    }
    
    public void addProp(PropNodeFacade prop){ props_.add(prop); }
    public void addSbj(ClassNodeFacade sbj){ sbjs_.add(sbj); }
    public void addObj(ClassNodeFacade obj){ objs_.add(obj); }

    public boolean hasProp(SemanticNode prop){
        for(PropNodeFacade p : props_){
            //yes, comparing references
            if(p == prop) return true;
        }
        return false;
    }
    
    public boolean hasSbj(ClassNodeFacade sbj){
        for(ClassNodeFacade p : sbjs_){
            //yes, comparing references
            if(p == sbj) return true;
        }
        return false;
    }
    
    public boolean hasObj(ClassNodeFacade obj){
        for(ClassNodeFacade p : objs_){
            //yes, comparing references
            if(p == obj) return true;
        }
        return false;
    }
    public void addObjValue(String objStr) {
        this.objValue_.add(objStr);
    }

    public String  getObjValue(int i) {
        String objStr = objValue_.get(i);
        return objStr;
    }

    public boolean hasObjValue(String objStr){
        for(String p : objValue_){
            //yes, comparing references
            if(p.equals(objStr)) return true;
        }
        return false;
    }
    
    public void minSizeOfElems(){
        sbjs_.trimToSize();
        objs_.trimToSize();
        objValue_.trimToSize();
        props_.trimToSize();
    }
    
    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException {
    }
    
    private ArrayList<ClassNodeFacade> sbjs_ = new ArrayList<ClassNodeFacade>();
    private ArrayList<ClassNodeFacade> objs_ = new ArrayList<ClassNodeFacade>();
    private ArrayList<String> objValue_ = new ArrayList<String>();//add
    private ArrayList<PropNodeFacade> props_ = new ArrayList<PropNodeFacade>();
    public boolean isPassive = false;
  
    
}
