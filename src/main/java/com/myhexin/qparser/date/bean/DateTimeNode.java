package com.myhexin.qparser.date.bean;



public class DateTimeNode {
	
	
	public final static int TYPE_YEAR=1;
	public final static int TYPE_MONTH=2;
	public final static int TYPE_DAY=3;
	public final static int TYPE_WEEK=4;
	public final static int TYPE_SEASON=5;
	public final static int TYPE_XUN=6;
	public final static int TYPE_TRADEDAY=7;
	public final static int TYPE_KEY_WORD=8;
	public final static int TYPE_SEP_WORD=9;
	public final static int TYPE_DAYNUM=10;
	
	/*public static Map<Integer, String> typeMap = new HashMap<Integer, String>();
	static {
		typeMap.put(TYPE_YEAR, "年");
		typeMap.put(TYPE_MONTH, "月");
		typeMap.put(TYPE_DAY, "日");
		typeMap.put(TYPE_WEEK, "��");
		typeMap.put(TYPE_SEASON, "��");
		typeMap.put(TYPE_XUN, "Ѯ");
		typeMap.put(TYPE_TRADEDAY, "��");
		typeMap.put(TYPE_TO_WORD, "��");
	}
	
	public static String type(int type){
		
		return null;
	}
	*/
	
	public int type=-1;
	public int value=-1;
	public String text;
	public int index=-1; 
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(value).append(",").append(text);
		if(subFrom!=null && subTo!=null) {
			buf.append('[');
			buf.append(subFrom.value).append(",").append(subFrom.text).append(",");
			buf.append(subTo.value).append(",").append(subTo.text).append(",");
			buf.append(']');
		}
		return buf.toString();
	}
	
	
	
	private DateTimeNode subFrom;
	private DateTimeNode subTo;

	public DateTimeNode getSubFrom() {
		return subFrom;
	}
	public void setSubFrom(DateTimeNode subFrom) {
		this.subFrom = subFrom;
	}
	public DateTimeNode getSubTo() {
		return subTo;
	}
	public void setSubTo(DateTimeNode subTo) {
		this.subTo = subTo;
	}
	
	
}
