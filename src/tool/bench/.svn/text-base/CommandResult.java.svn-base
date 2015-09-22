package bench;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import bench.CommandHandler.Command;



/**
 * the Class CommandResult
 */
public class CommandResult implements Serializable{
    private static final long serialVersionUID = -4746624151847696716L;
    
    /** 命令*/
    public Command cmd;
    /** 消息*/
    public String msg;
    /** 操作结果，以树为group*/
    public ArrayList<ResultGroup> rltGroups = new ArrayList<ResultGroup>();
    
    /**
     * @rm.param cmd
     * @rm.param msg
     */
    public CommandResult(Command cmd, String msg) {
        this.cmd = cmd;
        this.msg = msg;
    }
    
    /**
     * @rm.param cmd
     * @rm.param fmt
     * @rm.param args
     */
    public CommandResult(Command cmd, String fmt, Object... args) {
        this.cmd = cmd;
        this.msg= String.format(fmt, args);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        if(rltGroups.size() > 0) {
            sb.append(" 受影响的问句：\n");
            for(ResultGroup rg : rltGroups) {
                for(ResultItem ri : rg.items) {
                    sb.append(ri.isNewQuery() ? "NEW" : "OLD").append("\t");
                    sb.append(ri.text).append('\n');
                }
                sb.append(rg.tree).append("\n-----------------------------\n");
            }
        }
        return sb.toString();
    }
    
    /**
     * 排序，rltGroup按照treeCode排序
     */
    public void sort(){
    	for(ResultGroup group : rltGroups){
    		Collections.sort(group.items);
    	}
    	Collections.sort(rltGroups);
    }
    
    /**
     * the Class ResultGroup
     */
    public static class ResultGroup implements Serializable, Comparable<ResultGroup>{
        /** */
		private static final long serialVersionUID = -6621151247226429596L;
		/** 树结构编号*/
		public int treeCode=0;
		/** 树结构*/
		public String tree;
		/** */
		public int size = 0;
        /** 子问句*/
        public ArrayList<ResultItem> items = new ArrayList<ResultItem>();

		@Override
		public int compareTo(ResultGroup other) {
			return treeCode - other.treeCode;
		}
    }
    
    /**
     * the Class ResultItem
     *
     */
    public static class ResultItem implements Serializable, Comparable<ResultItem>{ 
    	/** */
		private static final long serialVersionUID = -4736220316209394952L;
		/** */
		public String qid = "0";
		/** */
		private String isNewQuery= "0";
		/** 子树形态*/
		public String queryPattern = "";
		/** pattern编号*/
		public int patternCode = 0;
        /** 问句*/
        public String text;
        /** 对问句采取的操作*/
        public String oper = "";
        /** 问句的未识别词语*/
        public String unknowns = "";
        /** 问句的patterns*/
        public String patterns = "";
        /** 问句的同义词*/
        public String trans = "";
        /** 问句日期*/
        public String date = "";
        /** 问句类型*/
        public String queryType = "股票";
        
        /**
         * @return 是否为新问句
         */
        public boolean isNewQuery(){
        	if("1".equals(isNewQuery)){
        		return true;
        	}
        	return false;
        }
        
        /**
         * 设为新的问句标志
         * @rm.param flag
         */
        public void set2New(){
        	isNewQuery = "1";
        }
        
        /**
         * 设为新的问句标志
         * @rm.param flag
         */
        public void set2Old(){
        	isNewQuery = "0";
        }

		@Override
		public int compareTo(ResultItem other) {
			int flag= this.patternCode - other.patternCode;
			if(flag != 0){
				return flag;
			}
			if(this.isNewQuery() && !other.isNewQuery() ){
				return -1;
			}else if(!isNewQuery() && other.isNewQuery() ){
				return 1;
			}
			return 0;
		}
    }
}
