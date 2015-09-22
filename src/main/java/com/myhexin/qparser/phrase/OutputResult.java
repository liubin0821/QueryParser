/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-3-28 上午10:22:27
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.myhexin.qparser.Param;

public class OutputResult {

	public List<List<String>> syntacticOutputs = new ArrayList<List<String>>();

	public List<String> syntacticRelations = new ArrayList<String>();

	public List<String> allResult = new ArrayList<String>();

	public String getFirstOutput() {
		StringBuilder firstResult = new StringBuilder();
		int pos = 0;
		for (List<String> syntacticOutput : syntacticOutputs) {
			if (firstResult.length() > 0)
				firstResult.append(syntacticRelations.get(pos++));
			if(syntacticOutput.size()>0)
				firstResult.append(syntacticOutput.get(0));
		}
		return firstResult.toString();
	}

	public String get(int i) {
		if (i == 0) {
			return getFirstOutput();
		}
		return null;
	}

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-3-28 下午2:34:51
	 * @description:   	
	 * @return
	 * 
	 */
	public List<String> getAllResult() {
		if(allResult.size() ==0)
			calculateAllResult();
		
		return allResult;
	}
	
	private void calculateAllResult(){
		dealWithDuplicate();
		HashMap<Integer, ArrayList<StringBuilder>> results = new HashMap<Integer, ArrayList<StringBuilder>>();
		getResultByRecursion(results, syntacticOutputs.size()-1);
		
		if (results.size()<=0) {
			return;
		}
		
		for(StringBuilder sb : results.get(syntacticOutputs.size()-1)){
			if(Param.MULTRESULT_ONE_QUERY_MAX_NUM>0 && allResult.size()>= Param.MULTRESULT_ONE_QUERY_MAX_NUM)
				break;
			allResult.add(sb.toString());
		}
		results = null;
	}
	
	

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-9-24 上午11:20:44
     * @description:   	
     */
    private final void dealWithDuplicate() {
    	String preFirstsyntacticOutput = "";
    	for (List<String> syntacticOutput : syntacticOutputs) {
    		//如果和前一个重复   句式的第一个输出一致,则认为是重复的
    		if(preFirstsyntacticOutput.equals(syntacticOutput.get(0))){
    			for (int i = 1 ; i < syntacticOutput.size(); i++) {
    				syntacticOutput.remove(i);
				}
    		}
    		preFirstsyntacticOutput = syntacticOutput.get(0);
		}
    }

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-3-28 下午2:41:54
	 * @description:   	
	 * @param results
	 * @param i 
	 * 
	 */
	private ArrayList<StringBuilder> getResultByRecursion(
	        HashMap<Integer, ArrayList<StringBuilder>> results, int i) { 
		
		if (i == 0) {
			ArrayList<StringBuilder> result = new ArrayList<StringBuilder>();
			for (String syntacticOutput : syntacticOutputs.get(i)) {
				StringBuilder sb = new StringBuilder();
				sb.append(syntacticOutput);
				result.add(sb);
			}
			results.put(i, result);
			return result;
		} else if (i > 0 && i < syntacticOutputs.size()) {
			ArrayList<StringBuilder> result = new ArrayList<StringBuilder>();
			if (results.get(i - 1) == null)
				getResultByRecursion(results, i - 1);
			boolean first = true;
			ArrayList<String> temp = new ArrayList<String>();

			int pos =0;
			for (String syntacticOutput : syntacticOutputs.get(i)) {
				for (StringBuilder sb : results.get(i - 1)) {
					if(first){
						temp.add(sb.toString());
						result.add(sb.append(syntacticRelations.get(i-1) + syntacticOutput));
					}
					else{
						String tempResult =  temp.get(pos++);
						if(tempResult!=null)
							result.add(new StringBuilder().append(tempResult + syntacticRelations.get(i-1) + syntacticOutput));
					}
					
					if(Param.MULTRESULT_ONE_QUERY_MAX_NUM>0 && result.size()>=Param.MULTRESULT_ONE_QUERY_MAX_NUM){
						results.put(i, result);
						return result;
					}					
				}
				pos = 0;
				first = false;
				
			}
			results.put(i, result);
			return result;
		}

		return null;
	}

	@Override
    public String toString() {	    
	    return getAllResult().toString();
    }
	
	public OutputResult copy() {
		OutputResult rtn = new OutputResult();
		rtn.allResult= new ArrayList<String>(allResult);
		rtn.syntacticOutputs=new ArrayList<List<String>>(syntacticOutputs);
		rtn.syntacticRelations=new ArrayList<String>(syntacticRelations);

		return rtn;
	}
	
}