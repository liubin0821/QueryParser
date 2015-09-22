package com.myhexin.qparser.onto;

import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.except.UnexpectedException;


public class UserPropNodeFacade extends PropNodeFacade{

	protected UserPropNode core;
	public UserPropNodeFacade(String title) {
		super(title);
	}
	public String getIdByFakeClass(UserClassNodeFacade fakeClass) {
		return core.getIdByFakeClass(fakeClass.core);
	}
	
	public void setValueType(PropType valueType_) {
		core.setValueType(valueType_);
	}

	public void addID(UserClassNodeFacade fakeClass, String idNumStr) throws UnexpectedException {
		core.addID(fakeClass.core, idNumStr);
	}
	
	public void addOfWhat(ClassNodeFacade onto) {
		core.addOfWhat(onto.core);
	}
}
