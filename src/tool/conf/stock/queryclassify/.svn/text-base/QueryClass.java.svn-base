package conf.stock.queryclassify;

import java.util.ArrayList;

public class QueryClass {

	private QueryClass(Builder builder){
		this.name = builder.name ;
		this.clsLevel = builder.clsLevel ;
//		this.subIFIND = builder.subIFIND ;
		this.children = builder.children ;
		this.father = builder.father ;
		this.level = builder.level ;
	}
	private QueryClass(){
		
	}
	/**
	 * @param args
	 */	
	String name ;	
//	String subIFIND  ;
	ArrayList<QueryClass> children = null ;
	ClassLevel clsLevel ;
	QueryClass father = null;
	int level ;
	QueryClass directSon ;
	
	
	public static class Builder{
		private String name ;	
		private ClassLevel clsLevel ;
		private int level ;
		
//		private String subIFIND ;	
		private ArrayList<QueryClass> children =  new ArrayList<QueryClass>();
		private QueryClass father ;
		
		public Builder(String name , ClassLevel clsLevel){
			this.name = name ;
			this.clsLevel = clsLevel ;
		}
		public Builder(String name , int level){
			this.name = name ;
			this.level = level ;
		}
		public Builder(){}
		
//		public Builder subIFIND(String subIFIND){
//			this.subIFIND = subIFIND ;
//			return this ;
//		}
		public Builder children(QueryClass child){
			this.children.add(child) ;
			return this ;
		}
		public Builder father(QueryClass father){
			this.father = father ;
			return this ;
		}
		
		public QueryClass build(){
			return new QueryClass(this) ;
		}
	}
	
	public static enum ClassLevel{
		ONE , TWO , IFIND , IFIND_SUB ;
	}

	public boolean equal(QueryClass other) {
		if (this == other)
			return true;
		if (this.name.equals(other.name))
			return true;
		return false;
	}
	
	public QueryClass getFirstAncestor(QueryClass curQc) {
		if (curQc.level <= 0)
			return null;

		while (curQc.father != null && curQc.father.level > 0)
			curQc = curQc.father;
		return curQc;
	}

}
