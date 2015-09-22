package bench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import bench.BenchQuery.Status;
import bench.CommandHandler.Command;



/**
 * the Class SimiQuerySet
 * 相似问句集
 * 具有相似泛化树形式的问句构成SimiQuerySet，而SimiQuerySet内部继续以子树形式进行组织
 */
public class SimiQuerySet {
	private int treeCode = 0;
	private String tree = "";
	/** specificPtnCode到问句集合*/
	private HashMap<Integer, ArrayList<BenchQuery>> subCode2ListMap = 
    		new HashMap<Integer, ArrayList<BenchQuery>>();

	/** 编辑距离阈值 */ 
	private static final double EDIT_DIS_THRESHOLD = 0.2 ;
	/**
	 * @rm.param specificPtnCode
	 * @return ArrayList
	 */
	public ArrayList<BenchQuery> getSpecificPtnBqList(int specificPtnCode){
		return subCode2ListMap.get(specificPtnCode);
	}
    

    
	/**
	 * 加入到相应子pattern中，并且确定subTree的编号subCode
	 * @rm.param subTree 
	 * @rm.param query
	 * @rm.param forceAdd
	 * @rm.param fromBench 
	 * @return 添加成功与否
	 * @throws CommandException 
	 */
	public boolean addStdQuery(BenchQuery query, boolean forceAdd, boolean fromBench) throws CommandException{
		if(query.treeCode == 0 && !fromBench){
			String msg = "query: "+query.text+" 解析出错！\n";
        	System.out.println(msg);
        	throw new CommandException(Command.ADD, msg);
		}
		final int subCode = query.specificPtnCode;
		ArrayList<BenchQuery> simiSubs = getSubCode2ListMap().get(subCode);
    	if(simiSubs == null){
    		simiSubs = new ArrayList<BenchQuery>();
    		simiSubs.add(query);
    		subCode2ListMap.put(subCode, simiSubs);
    		return true;
    	}else{
    		if(isExactlySame(query, simiSubs)) {
    			query.status = Status.STD;
            	String msg = "query: "+query.text+" 重复丢弃！\n";
            	System.out.println(msg);
            	throw new CommandException(Command.ADD, msg);
            }else if(forceAdd){
            	query.status = Status.STD;
            	simiSubs.add(query);
                return true;
            }else{
            	for(BenchQuery bq : simiSubs) {
                	double editDis = editDisAlgor(query,bq);
                	if(editDis <= EDIT_DIS_THRESHOLD){
                		query.status = Status.DEL;
                		String msg = "query: "+query.text+"   query: "+bq.text
                				+"   编辑距离: "+editDis +"。两者太相似， 丢弃！";
                    	System.out.println(msg);
                    	throw new CommandException(Command.ADD, msg);
                	}  
                } 
            	query.status = Status.STD;
            	simiSubs.add(query);
                return true;
            }
    	}
	}
	
	/**
	 * @return 全部问句
	 */
	public ArrayList<BenchQuery> getStdQuery() {
		ArrayList<BenchQuery> resultList = new ArrayList<BenchQuery>();
		for(Entry<Integer, ArrayList<BenchQuery>> tmpPair : getSubCode2ListMap().entrySet()){
			resultList.addAll(tmpPair.getValue());
		}
		return resultList;
	}
	
    /**
     * 给定问句是否与给定问句集中某问句完全相同
     * @rm.param newQuery
     * @rm.param cmpAgainst
     * @return 是否完全相同
     */
    public boolean isExactlySame(BenchQuery newQuery, ArrayList<BenchQuery> cmpAgainst) {
        if(cmpAgainst == null){
        	return false;
        }
    	for(BenchQuery bq : cmpAgainst) {
            if(bq.equals(newQuery)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @rm.param query
     * @rm.param compQuery
     * @return 编辑距离0~1
     */
    public static double editDisAlgor(BenchQuery query,BenchQuery compQuery) {
    	String queryText = query.text;
    	String compQueryText = compQuery.text;
    	String reg = "股票有哪些|股票有那些|上市公司|那些股票|的股票|有哪些|有没有|有那些|哪些股|那些股|个股|各股|的股" +
				"|寻找|股票|哪些|那些|查找|成为|有关|名单|查|找|的|有|类";
    	
    	queryText = queryText.replaceAll(reg, "");
    	compQueryText = compQueryText.replaceAll(reg, "");
		char[] queryChars = queryText.toCharArray();
        char[] compQueryChars = compQueryText.toCharArray();
        int[][] distArr = new int[2][compQueryChars.length+1];
        int i,j,temp;
        for(i = 0;i <= compQueryChars.length;i++) distArr[0][i] = 0;//初始化边界值
        distArr[1][0] = 0;
        for(i = 1;i <= queryChars.length;i++){
        	for(j = 1;j <= compQueryChars.length;j++){
        		if(queryChars[i-1] == compQueryChars[j-1]){
        			temp = distArr[0][j-1]+1;
                }else{
                    temp = distArr[1][j-1];
                    if(distArr[0][j] > temp) temp = distArr[0][j];
                 }
        		distArr[1][j] = temp;
            }
        	for(j = 0;j <= compQueryChars.length;j++)
        		distArr[0][j] = distArr[1][j];
        }
        temp = distArr[1][compQueryChars.length];
        
        if(queryChars.length <= compQueryChars.length){
        	double editDis = Double.valueOf(compQueryChars.length-temp)/compQueryChars.length;
        	return (int)((editDis)*1000+0.01)/1000.0;
        }else{
        	double editDis = Double.valueOf(queryChars.length-temp)/queryChars.length;
        	return (int)((editDis)*1000+0.01)/1000.0;
        }
	}
    
	/**
	 * @rm.param query 
	 * @return 删除成功与否
	 */
	public boolean remove(BenchQuery query) {
		ArrayList<BenchQuery> simiSubs = getSubCode2ListMap().get(query.specificPtnCode);
		if(simiSubs == null){
			return true;
		}
		for(int idx = simiSubs.size() -1; idx >=0; idx--){
			BenchQuery tmp = simiSubs.get(idx);
			if(tmp.equals(query)){
				simiSubs.remove(idx);
				break;
			}
		}
		if(simiSubs.size() == 0){
			getSubCode2ListMap().remove(query.specificPtnCode);
		}
		return false;
	}

	/**
	 * 丢弃新添加的问句
	 * @return 新增加的问句
	 */
	public ArrayList<BenchQuery> discard() {
		ArrayList<BenchQuery> result = new ArrayList<BenchQuery>();
		for(Entry<Integer, ArrayList<BenchQuery>> tmpPair : getSubCode2ListMap().entrySet()){
			ArrayList<BenchQuery> simiSubs = tmpPair.getValue();
			for(int idx = simiSubs.size() -1 ; idx >=0; idx--){
				BenchQuery tmpQuery = simiSubs.get(idx);
				if(tmpQuery.status == Status.ERR){
					result.add(tmpQuery);
					simiSubs.remove(idx);
				}
			}
		}
		return result;
	}

	/**
	 * @return the subCode2ListMap
	 */
	public HashMap<Integer, ArrayList<BenchQuery>> getSubCode2ListMap() {
		return subCode2ListMap;
	}

	/**
	 * @rm.param subCode2ListMap the subCode2ListMap to set
	 */
	public void setSubCode2ListMap(HashMap<Integer, ArrayList<BenchQuery>> subCode2ListMap) {
		this.subCode2ListMap = subCode2ListMap;
	}

	/**
	 * @return the treeCode
	 */
	public int getTreeCode() {
		return treeCode;
	}

	/**
	 * @rm.param treeCode the treeCode to set
	 */
	public void setTreeCode(int treeCode) {
		this.treeCode = treeCode;
	}

	/**
	 * @return the tree
	 */
	public String getTree() {
		return tree;
	}

	/**
	 * @rm.param tree the tree to set
	 */
	public void setTree(String tree) {
		this.tree = tree;
	}
}
