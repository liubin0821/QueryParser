package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.UnknownNode;

public class UserIndex implements Serializable{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String index_name;
    private String unit_str;
    private String query_text;
    private String node_result;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIndex_name() {
		return index_name;
	}
	public void setIndex_name(String index_name) {
		this.index_name = index_name;
	}
	public String getQuery_text() {
		return query_text;
	}
	public void setQuery_text(String query_text) {
		this.query_text = query_text;
	}
	public String getNode_result() {
		return node_result;
	}
	public void setNode_result(String node_result) {
		this.node_result = node_result;
	}
	public String getUnit_str() {
		return unit_str;
	}
	public void setUnit_str(String unit_str) {
		this.unit_str = unit_str;
	}
    
	private List<SemanticNode> nodes;
	public List<SemanticNode> getNodes() {
		return nodes;
	}
	
	public void parse() {
		if(node_result!=null) {
			String[] nodes = node_result.split("__#__");
			if(nodes!=null) {
				List<SemanticNode> _nodes = new ArrayList<SemanticNode>(nodes.length);
				for(String s : nodes) {
					String[] as = s.split("#");
					if(as!=null && as.length==2 && as[0]!=null && as[1]!=null) {
						SemanticNode n1 = null;
						if(as[1]!=null && as[1].equals("NUM")) {
							//25%#NUM
							n1 = new NumNode(as[0]);
						}else{
							n1 = new UnknownNode(as[0]);
						}
						
						if(n1!=null)
							_nodes.add(n1);
					}
					
					
				}
				this.nodes = _nodes;
			}
		}
	}
    
}