package bench;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * the Class BenchQuery
 *
 */
public class BenchQuery implements Comparable<BenchQuery>{
	/** 由MD5摘要算法取前16个小写字符*/
	public String qid ;
	
	/** 问句字符串 */
	public String text;
	
	/** 问句类型 分：股票信息、股票财务、基金信息、基金财务*/
	public QueryType type = QueryType.STOCK;
	
	public QueryType oldType = QueryType.STOCK ;
	
	/** 问句状态，std表示标准测试集中，add表示添加，del表示删除*/
	public Status status = Status.STD;
	
	/** 计算的节点*/
	public String trans = "";
	
	/** 问句中的指标，多个指标用“;”隔开*/
//	public String indexs = "";
	/** 问句中的pattern转换，多个用";"隔开*/
	public String patterns = "";
	
	/** 粗粒度泛化得到的pattern */
	public String generalPtn = "" ;
	
	/** 细粒度泛化得到的pattern */
	public String specificPtn = "" ;
	
	/** 含有附带StrLen的pattern*/
	public String specificPtnWithStrLen = "" ;
	
	/** 未识别词语*/
	public String unknowns = "";
	
	/** 泛化树结构*/
	/** 子树编号*/
	public int specificPtnCode = 0;
	
	/** 泛化树编号*/
	public int treeCode = 0;
	
	/** 上一次问句的泛化编号*/
	public int oldTreeCode = 0;
	
	/** 入库时间*/
	public String date = "";
	
	private static char nums[] = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
	/**
	 * @rm.param text
	 */
	public BenchQuery(String text) {
		this.text = text.trim().replaceAll("[\\s\\t]+", " ").toLowerCase();
		md5Qid();
		date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
	
	public int hashCode(){
		return text.hashCode();
	}
	
	/**
	 * @rm.param other
	 * @return text相同则相等
	 */
	public boolean equals(BenchQuery other){
		if(this.qid.equals(other.qid)){
			return true;
		}
		return false;
	}
	
	/**
	 * @return 树是否变化
	 */
	public boolean isTreeCodeChanged(){
		return treeCode != oldTreeCode;
	}
	/**
	 * @return type 是否变化
	 */
	public boolean isTypeChanged(){
		return type != oldType ;
	} 
	
	/**
	 * the Class QueryType
	 *'股票_文字型','股票_财务型','股票_技术型','基金_文字型','基金_财务型','其它'
	 */

	public static enum QueryType {
		/**全部*/
		ALL("全部"),
	    /**股票财务型*/
	    STOCK_CAIWU("股票_财务型"),
	    /**股票文字型*/
	    STOCK_STR("股票_文字型"),
	    /**股票技术型*/
	    STOCK_TEC("股票_技术型"),
	    /**基金财务型*/
	    FUND_CAIWU("基金_财务型"),
	    /**基金文字型*/
	    FUND_STR("基金_文字型"),

	    /**股票*/
	    STOCK("股票"),
	    /**基金*/
	    FUND("基金"),
	    /**不详*/
	    UNKNOWN("不详");
	    
	    private QueryType(String name) {
	        this.name = name;
	    }
	    
	    /**
	     * @rm.param type
	     * @return 问句类型
	     */
	    public static QueryType formQueryType(String type){
	    	if(type == null){
	    		return UNKNOWN;
	    	}else if(type.equals("股票财务")){
	    		return STOCK_CAIWU;
	    	}else if(type.equals("股票行情")){
	    		return STOCK_STR;
	    	}else if(type.equals("基金财务")){
	    		return FUND_CAIWU;
	    	}else if(type.equals("基金行情")){
	    		return FUND_STR;
	    	}else if(type.equals("股票")){
	    		return STOCK;
	    	}else if(type.equals("基金")){
	    		return FUND;
	    	}
	    	return UNKNOWN;
	    }
	    
	    public String toString() { return name; }
	    
	    private String name;
	}
	
	/**
	 * the Class QueryType
	 *
	 */
	public static enum Status {
	    /**已经被刪除，或不在库中*/
	    DEL("del"),
	    /**在标准测试集中*/
	    STD("ok"),
	    /**问句解析出错时，需开发解决*/
	    ERR("err");
	    
	    private Status(String name) {
	        this.name = name;
	    }
	    
	    /**
	     * @rm.param status
	     * @return Status
	     */
	    public static Status toStatus(String status){
	    	if("del".equals(status)){
	    		return DEL;
	    	}else if("err".equals(status)){
	    		return ERR;
	    	}
	    	return STD;
	    }
	    
	    public String toString() { return name; }
	    
	    private String name;
	}
	
	/**
	 * 由md5摘要算法取, 后改为 : md5 + hashcode
	 */
	private void md5Qid(){
		try {		 
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
            byte bytes[] = md.digest();
            StringBuilder builder = new StringBuilder();
            for(int idx = 0 ; idx < 8; idx++){
            	byte bt = (byte)(bytes[idx] ^ bytes[idx + 8]);
            	String hex = Integer.toHexString(bt & 0xFF); 
            	if(hex.length() < 2){
            		builder.append(nums[Math.abs(text.hashCode()) % nums.length]);
            	}
            	builder.append(hex);
            }
            // 加入hashcode
            this.qid = builder.append(text.hashCode()).toString() ;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(BenchQuery other) {
		int flag = treeCode - other.treeCode;
		if(flag != 0){
			return flag;
		}
		flag = specificPtnCode - other.specificPtnCode;
		if(flag != 0){
			return flag;
		}
		return this.text.compareTo(other.text);
	}
	
//	public static void main(String[] args){
//		BenchQuery bq = new BenchQuery("推荐买入家数在5家以上") ;
//		System.out.println(bq.qid.length());
//		System.out.println(bq.qid);
//	}
}
