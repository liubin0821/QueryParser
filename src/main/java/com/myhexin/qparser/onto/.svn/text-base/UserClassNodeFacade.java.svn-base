package com.myhexin.qparser.onto;

import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.except.UnexpectedException;


public class UserClassNodeFacade extends ClassNodeFacade {

	public UserClassNodeFacade(){}
	public UserClassNodeFacade(String title){
		core.text = title;
	}
	
	public void setReportType(ReportType reportType) {
		core.setReportType(reportType);
	}
	
	public UserPropNode getFakePropByID(String idStr) throws UnexpectedException {
		return core.getFakePropByID(idStr);
	}
	
	public void addProp(PropNodeFacade prop) {
		core.addProp(prop.core);
	}
	protected UserClassNode core;
}
