package com.myhexin.server.test;

import java.util.List;

import com.myhexin.qparser.util.condition.ConditionParser;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;
import com.myhexin.server.test.QPTestSuite.TestVO;

public class ConditionTestCase extends QPTestCaseAbstract{

	
	@Override
	public boolean test(TestVO vo) {
		List<BackTestCondAnnotation> btResultList = ConditionParser.compileToCond(vo.query, vo.qType, vo.domain, vo.postDataStr);
			
		if(btResultList.size()==0 ) {
			errors.add(String.format("[%s] query=%s, error=%s",  ConditionTestCase.class.getSimpleName() ,vo.query,"condition result size=0"));
		}else{
			String condition  = btResultList.get(0).getResultCondJson();
			if(condition!=null && condition.equals(vo.exp_result)) {
				return true;
			}else{
				errors.add(String.format("[%s] query=%s, error=%s, \n\tpostDataStr=%s \n\trun_condition=%s \n\texp_condition=%s",  ConditionTestCase.class.getSimpleName() ,vo.query ,"condition doesn't match", vo.postDataStr, condition, vo.exp_result));
			}
		}
		return false;
	}
}
