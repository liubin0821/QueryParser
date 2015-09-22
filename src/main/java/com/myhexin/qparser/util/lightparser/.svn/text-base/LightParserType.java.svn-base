package com.myhexin.qparser.util.lightparser;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.FocusNode.Type;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;

public class LightParserType {
	
	public String label;
	public String desc;
	public List<SubType> subTypes;
	public List<Channel> channels;
	

	
	public LightParserType(String label) {
		this.label = label;
		subTypes = new ArrayList<SubType>();
		channels = new ArrayList<Channel>();
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public List<SubType> getSubTypes() {
		return subTypes;
	}

	public void setSubTypes(List<SubType> subTypes) {
		this.subTypes = subTypes;
	}
	
	public void addSubType(SubType subType) {
		this.subTypes.add(subType);
	}
	
	public String hasSubType(String txt) {
		if (subTypes == null || subTypes.size() == 0 
				|| txt == null || txt.trim().length() == 0)
			return null;
		for (SubType subType : subTypes) {
			String[] values = subType.value.split("\\|");
			for (String value : values)
				if (value.equals(txt))
					return subType.label;
		}
		return null;
	}
	
	public boolean hasSomeTypeIndex(SemanticNode sn, Query.Type type) {
		if (!(sn.type == NodeType.FOCUS && ((FocusNode) sn).hasIndex()))
			return false;
		else if (type == Query.Type.ALL)
			return true;
		FocusNode fn = (FocusNode) sn;
		for (FocusItem fi : fn.focusList)
			if (fi.getType() == Type.INDEX && fi.getIndex() != null && (fi.getIndex().getDomains().contains(type) || fi.getIndex().getDomains().contains(Query.Type.ALL)))
				return true;
		return false;
	}
	
	public ArrayList<String> hasSubType(SemanticNode sn) {
		if (!(sn.type == NodeType.FOCUS && ((FocusNode) sn).hasIndex()))
			return null;
		ArrayList<String> subTypesTemp = new ArrayList<String>();
		for (SubType subType : subTypes) {
			String[] values = subType.value.split("\\|");
			for (String value : values) {
				if (value.equals("ASTOCKINDEX") && hasSomeTypeIndex(sn, Query.Type.STOCK)
						|| value.equals("FUNDINDEX") && hasSomeTypeIndex(sn, Query.Type.FUND)
						|| value.equals("HKSTOCKINDEX") && hasSomeTypeIndex(sn, Query.Type.HKSTOCK)
						|| value.equals("HGHYINDEX") && hasSomeTypeIndex(sn, Query.Type.HGHY)
						|| value.equals("SEARCHINDEX") && hasSomeTypeIndex(sn, Query.Type.SEARCH)) {
					subTypesTemp.add(subType.label);
				}
			}
		}
		return subTypesTemp;
	}
	
	public String hasSubType(StrNode strNode) {
		if (subTypes == null || subTypes.size() == 0 
				|| strNode == null)
			return null;
        for (SubType subType : subTypes) {
			String[] values = subType.value.split("\\|");
			for (String value : values) {
				//测试无误后删除 wyh 20114.11.
				//if(strNode.hasOfwhatOrSubType(value) || strNode.hasOfwhatOrSubType("_"+value)) {
		        if(strNode.hasSubType(value) || strNode.hasSubType("_"+value)) {
		        	//由于之前并没有把B股、A股和新三板等股票分开，所以之前都为A股类型。
		        	if(subType.label.equals("ASTOCK"))
		        		//再通过股票代码来区分是B股、A股还是新三板
		        		return getStockTypeFromId(strNode);
		        	else
		                return subType.label;
		        }
			}
        }
		return null;
	}
	
    /**
     * 用于区分股票为B股、A股和新三板。其主要规则如下：
     * A股都是6位，上海  6开头，深证  0和3开头
     * B股也是6位，上海  9开头，深证  2开头
     * 新三板则主要是以40、430、830开头 
     * @param strNode
     * @return
     */
	public String getStockTypeFromId(StrNode strNode) {
		for (String id : strNode.getId().values()) {
			String stockId = id == "" ? strNode.getText() : id;
			if (stockId.startsWith("9") || stockId.startsWith("2")) {
				strNode.subType.add("BSTOCK");
				return "BSTOCK";
			} else if (stockId.startsWith("40") || stockId.startsWith("430")
			        || stockId.startsWith("830")) {
				strNode.subType.add("NEWTB");
				return "NEWTB";
			} else if (stockId.startsWith("6") || stockId.startsWith("0")
			        || stockId.startsWith("3")) {
				strNode.subType.add("ASTOCK");
				return "ASTOCK";
			} 
		}
		//标明股票代码配置有问题，请注意检查
		strNode.subType.add("ASTOCK");
		return "ASTOCK";
	
	}
	
	
	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}
	
	public void addChannel(Channel channel) {
		this.channels.add(channel);
	}
	
	public String hasChannel(String txt) {
		if (channels == null || channels.size() == 0  || txt == null || txt.trim().length() == 0)
			return null;
		for (Channel channel : channels) {
			String[] values = channel.value.split("\\|");
			if(values!=null) {
				for (String value : values)
					if (value.equals(txt))
						return channel.label;
			}
		}
		return null;
	}
}
