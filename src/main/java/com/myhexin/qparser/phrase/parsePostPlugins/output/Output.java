/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-5-13 下午1:46:25
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePostPlugins.output;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.CompareType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;

public abstract class Output {

	protected static final Pattern SpecialRepresionDate = Pattern.compile("^(199\\d|200\\d|201\\d)?(0[1-9]|1[0-2])?(0[1-9]|[12]\\d|3[01])?$");
	protected static Pattern PersonIndexShowContain = Pattern.compile(".+姓名$|.+国籍$|.+学历$|.+生肖$|.+婚姻状况$|.+毕业学校$|.+是否为官员$|.+工作过的企业$|.+所获政府奖励$|.+性别$");

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2013-12-9 下午2:50:21
     * @description:   	
     * 
     */
    public static boolean datePresention(StringBuffer sb, SemanticNode sn) {
    	// 新增：连续时间的处理3
    	boolean sequenceOneTime = false;
    	if(sn.type != NodeType.DATE) return sequenceOneTime;
    	DateNode dn = (DateNode) sn;
    	
    	if (dn.getFrequencyInfo() != null) {
    		NumRange nr = dn.getFrequencyInfo().getLength();
    		Unit unit = dn.getFrequencyInfo().getUnit();
    		sb.append(dn.getText());
    		if (nr.getLongFrom() == nr.getLongTo()) {
    			sb.append ("有" + nr.getLongFrom() + EnumConvert.getStrFromUnit(unit)) ;
    		}
    		else if (nr.getLongFrom() != nr.getLongTo()) {
    			if (nr.getLongTo() <= dn.getDateinfo().getLength())
    				sb.append ("有" + nr.getLongFrom() + "-" +nr.getLongTo() + EnumConvert.getStrFromUnit(unit));
    			else
    				sb.append ("有大于等于" + nr.getLongFrom() + EnumConvert.getStrFromUnit(unit));
    		}	
    		return sequenceOneTime;
    	} 
    	
    	if (dn.isSequence && !sn.getText().contains("连续")) {
    		//一次以上  的情况排除
    		if (dn.getDateinfo().getLength() > 1 || dn.compare != CompareType.EQUAL){
    			sb.append("连续"+sn.getText());				
    			sb.append(getComparePresention(dn));
    		}
    		else {
    			sb.append(sn.getText());
    			sequenceOneTime = true;
    		}
    		return sequenceOneTime;
    	}
    	else if ( dn.compare != CompareType.EQUAL) {
    		sb.append(sn.getText() + getComparePresention(dn));
    		return sequenceOneTime;
    	}    		
    	//对特定格式时间进行显示
    	if(StringUtils.isNotEmpty(dn.getForShow())) {
    		sb.append(dn.getForShow());
    	}else {
    		sb.append(showSpecialDate(sn.getText()));
    	}
    	return sequenceOneTime;
    }

	static final String getComparePresention(DateNode dn) {
    	if (dn.compare == CompareType.LONGER) {
    		return "以上";
    	} else if (dn.compare == CompareType.SHORTER) {
    		return "以下";
    	}
    	else return "";
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2013-12-9 下午8:20:50
     * @description:  特定格式的日期,转换成标准形式显示,不是特定格式的日期,直接返回原字符串 	
     * 
     */
    static String showSpecialDate(String text) {
    	Matcher m = SpecialRepresionDate.matcher(text);
    	if (m.matches()) {
    		String year = m.group(1);
    		String mouth = m.group(2);
    		String day = m.group(3);
    
    		return (year != null ? year + "年" : "")
    				+ (mouth != null ? mouth + "月" : "")
    				+ (day != null ? day + "日" : "");
    	}
    	return text;
    }

	protected static String getPropsText(SemanticNode sNode) {
    	if (sNode == null) {
    		return "";
    	}
    	// 处理连续回写的逻辑
    	StringBuffer sb = new StringBuffer();
    	if (sNode.type != NodeType.FOCUS) {
    		if (sNode.type == NodeType.DATE)
    			datePresention(sb, sNode);
    		else
    			sb.append(sNode.getText());
    		return sb.toString();
    	} else {
    		FocusNode fNode = (FocusNode) sNode;
    		ClassNodeFacade cNode = fNode.getIndex();
    		// 不是指标
    		if (cNode == null) {
    			sb.append(sNode.getText());
    			return sb.toString();
    		}
    		// 是指标
    		List<PropNodeFacade> pNodes = cNode.getAllProps();
    		
    		StringBuffer sbProps = new StringBuffer();
    		StringBuffer sbIndexValue = new StringBuffer();
    		sbIndexValue.append(cNode.getText());
    		for (int i = 0; i < pNodes.size(); i++) {
    			PropNodeFacade prop = pNodes.get(i);
    			SemanticNode propValue = pNodes.get(i).getValue();
    			if (propValue == null || propValue.getText() == null || propValue.getText().length() == 0) {
    				continue;
    			} else if (prop.isValueProp()) {
    				//特定的指标
    				if(PersonIndexShowContain.matcher(sbIndexValue.toString()).matches())
    					sbIndexValue.append("包含");
    				else if (propValue.getText().equals("是") || propValue.getText().equals("否"))
    					sbIndexValue.append("为");
    				else
    					sbIndexValue.append("是");
    				sbIndexValue.append(propValue.getText());
    			} else {
    				switch (propValue.getType()) {
    				case DATE:
    					datePresention(sbProps, propValue);
    					break;
    				case STR_VAL:
    					sb.append(propValue.getText());
    					break;
    				default:
    					sb.append(propValue.getText());
    					break;
    				}
    				sbProps.append("的");
    			}
    		}
    		sb.append(sbProps.toString());
    		sb.append(sbIndexValue.toString());
    		return sb.toString();
    	}
    }

	protected static boolean isContainProps(SemanticNode sn) {
    	if (sn.type == NodeType.FOCUS && ((FocusNode) sn).hasIndex()) {
    		FocusNode fn = (FocusNode) sn;
    		ClassNodeFacade cn = fn.getIndex();
    		if (cn.getAllProps() != null) {
    			for (PropNodeFacade pn : cn.getAllProps()) {
    				if (pn.getValue() != null)
    					return true;
    			}
    		}
    	}
    	return false;
    }

	public Output() {
	}

}