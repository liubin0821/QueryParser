package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.iterator.IndexIteratorImpl;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.bind.BindStrToIndex;

public class PhraseParserPluginFillIndexPropDefaultValue extends
        PhraseParserPluginAbstract {

	public PhraseParserPluginFillIndexPropDefaultValue() {
		super("Fill_Index_Prop_Default_Value");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return fillIndexPropDefaultValue(nodes);
	}

	/**
	 * 用于识别句中的周期节点
	 * 
	 * @param qlist
	 * @throws NotSupportedException
	 */
	private ArrayList<ArrayList<SemanticNode>> fillIndexPropDefaultValue(
	        ArrayList<SemanticNode> nodes) {
		ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>(1);
			
 		fillPropDefaultValues(nodes);
		
		qlist.add(nodes);
		return qlist;
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-10 下午4:07:41
     * @description:   	
     * @param nodes
     * 
     */
    private void fillPropDefaultValues(ArrayList<SemanticNode> nodes) {
    	IndexIteratorImpl iter = new IndexIteratorImpl(nodes);
    	while (iter.hasNext()) {			
			FocusNode fn = iter.next();
			ClassNodeFacade cn = fn.getIndex();		
			fillPropsDefaultValue(cn);
		}
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-10 下午4:11:36
     * @description:   	
     * @param cn
     * 
     */
    private void fillPropsDefaultValue(ClassNodeFacade cn) {
    	if (cn==null) return;
    	
    	for (PropNodeFacade pn : cn.getClassifiedProps(PropType.INDEX)) {
			if(fillOnePropDefaultValue(pn, cn))
				cn.setIsBoundValueToThis(true);
		}
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-10 下午4:14:16
     * @description:   	
     * @param pn
     * 
     */
    private boolean fillOnePropDefaultValue(PropNodeFacade pn, SemanticNode pnParent) {
    	//已经绑定
    	if (pn == null || pn.getValue()!=null ) return false;
    	
    	String defaultIndex = "";
    	for(String st:pn.getSubType()){
    		defaultIndex=st;
    		break;
    	}
    	//只有abs_kdj  abs_k线这样处理
    	if(defaultIndex == null || !defaultIndex.startsWith("abs_k")) return false;
    	
    	defaultIndex = defaultIndex.startsWith("abs_") ? defaultIndex.substring(4) : defaultIndex;

    	try {
	        FocusNode defaultFn = ParsePluginsUtil.getIndexFocusNodeByString(defaultIndex);
	        if(defaultFn != null && defaultFn.hasIndex()){
	        	BindStrToIndex.bindNodeToProp(defaultFn.getIndex(), pn, pnParent);
	        	return true;
	        }
        } catch (UnexpectedException e) {
        	e.printStackTrace();
        	return false;
        }
    	return false;
    }

  //把绑定信息,打印出来
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]:<BR>%s\n", i, _nodesHtmlAllBindings(qlist.get(i)) ));
        }
    }
}

