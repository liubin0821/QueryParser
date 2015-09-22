package com.myhexin.qparser.qa;

import java.util.ArrayList;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.node.AvgNode;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FakeDateNode;
import com.myhexin.qparser.node.FakeNumNode;
import com.myhexin.qparser.node.GeoNode;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NegativeNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.OperatorNode;
import com.myhexin.qparser.node.QuestNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.SortNode;
import com.myhexin.qparser.node.SpecialNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TechOpNode;
import com.myhexin.qparser.node.TriggerNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
//import java.util.Map;
//import java.util.HashMap;

public class MockQueryObject {
	private ArrayList arrList ;
		
	public  MockQueryObject(ArrayList<ArrayList<String>> arrayList){
		this.arrList = arrayList;
	}
				
	public static NumRange getNumRange(String from, String to, String unit){		   
		NumRange nr = new NumRange();
		nr.setNumRange(from, to);
		nr.setUnit(unit);
		return nr;
	}
	
	public static NumRange getNumRange(double from, double to, String unit){		   
		NumRange nr = new NumRange();
		nr.setNumRange(from, to);
		nr.setUnit(unit);
		return nr;
	}
		
	public Query getQuery(){
		Query query = new Query("");
		SemanticNode sn;
		//System.out.println("------------testList as below---------------");		
		for(int i = 0; i < this.arrList.size(); i++)
		{
		     ArrayList<String> tmp = (ArrayList<String>)(this.arrList.get(i));
		     //System.out.println(tmp);
		     String text = tmp.get(0);
		     String type = tmp.get(1);
		     //System.out.println("test:" + text);
		     sn = parseNode(type, text);
	         query.getNodes().add(sn);
		}
		return query;
	}
	
	public static NumNode getNumNodeWithRange(String text, double from, double to){
		 NumNode nn =  new NumNode(text, from, to);
		 return nn;
	}
	
	public static NumNode getNumNodeWithNumRange(String text, NumRange nr){
         NumNode nn = new NumNode(text);
         nn.setNuminfo(nr);
         return nn;
	}
	
    public SemanticNode parseNode(String type, String text){
    	SemanticNode sn;
        if (type.equals("class")) {
        	 sn = new ClassNodeFacade(text);
        } else if (type.equals("date")) {
            sn = new DateNode(text);
        } else if (type.equals("num")) {
            sn = new NumNode(text);
        } else if (type.equals("vagueDate")) {
            sn = new FakeDateNode(text);
        } else if (type.equals("vagueNum")) {
            sn = new FakeNumNode(text);
        }else if (type.equals("special")) {
            sn = new SpecialNode(text);
        }else if (type.equals("change")) {
            sn = new ChangeNode(text);
        } else if (type.equals("trigger")) {
           sn = new TriggerNode(text);
        } else if (type.equals("operator")) {
            sn = new OperatorNode(text);
        } else if (type.equals("techOp")){
            sn = new TechOpNode(text);
        } else if (type.equals("geoname")) {
            sn = new GeoNode(text);
        } else if (type.equals("logic")) {
            sn = new LogicNode(text);
        } else if (type.equals("qword")) {
            sn = new QuestNode(text);
        } else if (type.equals("prop")) {
            sn = new PropNodeFacade(text);
        } else if (type.equals("value")) {
            sn = new StrNode(text);
        } else if (type.equals("sort")) {
            sn = new SortNode(text);
        } else if (type.equals("avg")) {
            sn = new AvgNode(text);
        } else if (type.equals("neg")) {
            sn = new NegativeNode(text);
        }else if(type.equals("str_val")){
        	sn = new StrNode(text);
        }else {
        	//System.out.println("in unknown loop");
        	//System.out.println(text);
        	sn = new UnknownNode(text);
        	//System.out.println("unknown loop over");
        }
        return sn;
         	 
     }
        		
}
