package com.myhexin.server.test;

import java.util.ArrayList;
import java.util.List;

public abstract class QPTestCaseAbstract implements  QPTestCase {

	protected List<String> errors= new ArrayList<String>();
	
	public List<String> errors() {
		return errors;
	}
	
}
