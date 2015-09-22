/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-10-8 下午3:12:34
 * @description:   	
 * 
 */
package com.myhexin.qparser;

import java.util.List;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.parsePostPlugins.output.Output;

public class QueryIndexWithProp extends QueryIndex {

	public static QueryIndex instance = new QueryIndexWithProp();

	
    protected String getIndexShowText(ClassNodeFacade index) {
	    if(index == null)
	    	return "";
	    return getIndexWithProps(index);
    }   
      

    /**
     * 
     * @author: 	    吴永行 
     * @dateTime:	  2014-10-8 上午11:02:15
     * @description:  显示指标 + 属性 	
     * @param sNode
     * @return
     */
	public  static String getIndexWithProps(ClassNodeFacade cn) {
		if (cn == null)
			return "";
		//是指标
		List<PropNodeFacade> pNodes = cn.getAllProps();
		boolean isFirstProp = true;
		StringBuffer sb = new StringBuffer();
		sb.append(cn.getText() + "{");//指标
		for (int i = 0; i < pNodes.size(); i++) {
			SemanticNode propValue = pNodes.get(i).getValue();
			if (propValue == null || propValue.getText() == null || propValue.getText().length() == 0) {
				continue;
			}
			//添加属性之间的","号
			if (isFirstProp)
				isFirstProp = false;
			else
				sb.append(",");
			sb.append(pNodes.get(i).getText() + ":");
			switch (propValue.getType()) {
			case DATE:
				Output.datePresention(sb, propValue);
				break;
			case STR_VAL:
				sb.append(propValue.getText());
				break;
			default:
				sb.append(propValue.getText());
				break;
			}
		}
		sb.append("}");
		return sb.toString();

	}
  
}
