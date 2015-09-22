package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.util.Consts;

public class PhraseParserPluginInferenceDomain extends PhraseParserPluginAbstract {

	private static final int MAXCOUNT = 20;//递归计算最大次数,防止出现环
	public PhraseParserPluginInferenceDomain() {
		super("Inference_Domain");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return inferenceDomain(ENV, nodes);
	}

	private ArrayList<ArrayList<SemanticNode>> inferenceDomain(Environment ENV,
	        ArrayList<SemanticNode> nodes) {
		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>();

		if (nodes == null || nodes.size() == 0)
			return tlist;

		
		//找出nodes中所有的指标,为后面找领域做准备
		List<FocusNode> indexList = new ArrayList<FocusNode>();
		for (SemanticNode sn : nodes) {
			FocusNode fn = null;
			StrNode st = null;
			switch (sn.getType()) {
			case STR_VAL:
				st = (StrNode) sn;
				fn = PhraseParserPluginAddIndexOfStrInstance.getIndexOfStrInstance(ENV, st);
				break;
			case FOCUS:
				fn = (FocusNode) sn;
				if (!fn.hasIndex() && fn.hasString()) {
					st = fn.getString();
					fn = PhraseParserPluginAddIndexOfStrInstance.getIndexOfStrInstance(ENV, st);
				}
				break;
			default:
				break;
			}
			if (fn != null && (fn.hasIndex() || fn.hasIndex()))
				indexList.add(fn);
		}//找指标结束

		
		
		List<Set<String>> demains = new ArrayList<Set<String>>();
		for (FocusNode fn : indexList) {
			//找出一个指标的所有指标和别名
			ArrayList<FocusNode.FocusItem> itemList = fn.getFocusItemList();
			List<ClassNodeFacade> oneFocusIndexs = new ArrayList<ClassNodeFacade>();
			for (int j = 0, size = itemList.size(); j < size; j++) {
				FocusNode.FocusItem item = itemList.get(j);
				if (item.getType() == FocusNode.Type.INDEX) {
					ClassNodeFacade cn = item.getIndex();
					oneFocusIndexs.add(cn);
				}
			}//找结束
			
			//找出领域
			demains.add(getOneFocusDomain(oneFocusIndexs));
		}
		
		//从ENV中取出指定的domain
		String[] assignDomains = null;
		if(nodes.get(0).getType()==NodeType.ENV){
			Environment listEnv = (Environment) nodes.get(0);
			Environment qEnv = (Environment)listEnv.get("queryEnv", false);
			if(qEnv!=null) {
				String domainStr = (String) qEnv.get("domain", false);
				if(domainStr!=null) {
					assignDomains = domainStr.split(",");
				}
			}
		}

        Map.Entry<String, Double>[] listDomain = getListDomain(demains, assignDomains);
		
		
		//把剩下的domain放回ENV
		if(nodes.get(0).getType()==NodeType.ENV){
			Environment listEnv = (Environment) nodes.get(0);
			listEnv.put("listDomain", listDomain, true);
		}
		tlist.add(nodes);
		return tlist;
	}

	
	private boolean isMacroDomainIndex(String text) {
		if(Consts.CONST_absMarketEnv.equals(text) || Consts.CONST_absSmallMoneyGod.equals(text)) {
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 如果问句中指定了domain, 去掉查找到的domain中的其他domain
	 * 
	 * 
	 * @param demains	向上查找到的domain
	 * @param  assignDomains 问句中指定的domain
	 * 
     * @author: 	    吴永行 
     * @dateTime:	  2014-12-25 下午1:52:26
     */
    @SuppressWarnings("rawtypes")
    private Map.Entry<String, Double>[]  getListDomain(List<Set<String>> demains, String[] assignDomains) {
    	
    	Set<String> assignDomainSet = new HashSet<String>();
    	if(assignDomains != null) CollectionUtils.addAll(assignDomainSet, assignDomains);
  
    	
    	LinkedHashMap<String, Double> listDomain = new LinkedHashMap<String, Double>();
	    for(Set<String> oneFocusDomain : demains){
	    	for(String demainText : oneFocusDomain){
	    		
	    		//abs_市场环境-领域是百搭领域,特殊处理的,适用于所有领域
	    		if(isMacroDomainIndex(demainText)==false && assignDomainSet.size()>0 && !assignDomainSet.contains(demainText)) 
	    			continue;
	    		
	    		if(listDomain.containsKey(demainText))
	    			listDomain.put(demainText, listDomain.get(demainText)+1);
	    		else
	    			listDomain.put(demainText,(double) 1);
	    	}
	    }
	    
	    //按照Entry.value排序,并且如果相同value,股票领域排前面
	    Map.Entry<String, Double>[] sortedListDomain = getSortedHashtableByValue(listDomain);
	    int size = sortedListDomain.length;
	    
	    //股票领域往前移动
	    /*for(int i=1; i<size; i++){
	    	if(sortedListDomain[i].getKey().equals(Consts.CONST_absStockDomain)){
	    		Double counts=(Double) sortedListDomain[i].getValue();
	    		while(i>0 && counts.equals((Double)sortedListDomain[i-1].getValue())){ //和前一个交换
	    			Map.Entry temp        = sortedListDomain[i-1];
	    			sortedListDomain[i-1] = sortedListDomain[i];
	    			sortedListDomain[i--] = temp;
	    		}
	    		break;
	    	}
	    }*/
	    
	    
	    //拿到abs_市场领域, abs_小财神分数
	    Double markEnvSize = listDomain.get(Consts.CONST_absMarketEnv);
	    Double smallMoneyGod = listDomain.get(Consts.CONST_absSmallMoneyGod);
	    if(markEnvSize==null) markEnvSize=0.0;
	    if(smallMoneyGod!=null) markEnvSize+=smallMoneyGod;
	    
	    size = demains.size();
	    for(Map.Entry<String, Double> entry : sortedListDomain)
	    {
	    	//市场领域分数是百搭,给所有领域加上
	    	Double newValue = entry.getValue() + markEnvSize;
	    	entry.setValue(newValue / size);
	    }
	    return sortedListDomain;
    }
    
    
  //按照Entry.value排序,并且如果相同value,股票领域排前面
    @SuppressWarnings({ "unchecked", "rawtypes" })  
    public static Map.Entry<String, Double>[] getSortedHashtableByValue(Map h) {  	  
        Set set = h.entrySet();  	  
        Map.Entry<String, Double>[] entries = (Map.Entry<String, Double>[]) set.toArray(new Map.Entry[set.size()]);  	  
        Arrays.sort(entries, new Comparator() {  
            public int compare(Object arg0, Object arg1) {  
            	Double key1 = (Double) ((Map.Entry) arg0).getValue();  
            	Double key2 = (Double) ((Map.Entry) arg1).getValue();  
            	
            	String domainName1 = (String)((Map.Entry) arg0).getKey();
            	String domainName2 = (String)((Map.Entry) arg1).getKey();
            	
            	if(domainName1.equals(domainName2)) {
            		return ((Comparable) key2).compareTo(key1);  
            	}else{
            		int ret = ((Comparable) key2).compareTo(key1);
            		if(ret!=0) {
            			return ret;
            		}else{
            			if(domainName1.equals(Consts.CONST_absStockDomain)) {
                			return -1;
                		}
                		
                		if(domainName2.equals(Consts.CONST_absStockDomain)) {
                			return 1;
                		}
                		return domainName1.compareTo(domainName2);
            		}
            		
            	}
            }  	  
        }); 
        return entries;  
    }

    
    /*public static void testGetSortedHashtableByValue(String[] args) {
    	LinkedHashMap<String, Double> listDomain = new LinkedHashMap<String, Double>();
    	
    	
    	listDomain.put("abs_信息领域",(double) 5);
    	listDomain.put("abs_股票领域",(double) 5);
    	listDomain.put("abs_指数领域",(double) 5);
    	 Map.Entry<String, Double>[] sortedListDomain = getSortedHashtableByValue(listDomain);
    	 for(Map.Entry<String, Double> entry : sortedListDomain) {
    		 System.out.println(entry.getKey() + " = "  +entry.getValue());
    	 }
    }*/
    
    /*
     * 往上找出指标的领域 
     * 
     * @param oneFocusIndexs
     * @return
     */
	private Set<String> getOneFocusDomain(List<ClassNodeFacade> oneFocusIndexs) {
		HashSet<String> OneFocusDomain = new HashSet<String>();
		for (ClassNodeFacade cn : oneFocusIndexs) {
			getOneClassDomain(cn, OneFocusDomain,0);

		}
		return OneFocusDomain;
	}

	/**
	 * @author: 	    吴永行 
	 * @param i 
	 * @dateTime:	  2014-12-25 上午10:29:16
	 */
	private void getOneClassDomain(ClassNodeFacade cn, HashSet<String> oneFocusDomain, int count) {
    	
		if(cn.getSuperClass().size()==0 || count>MAXCOUNT) {
    		oneFocusDomain.add(cn.getText());
    		return;
    	}
    	
    	for(ClassNodeFacade spClass : cn.getSuperClass()){
    		getOneClassDomain(spClass,oneFocusDomain,++count);
    	}
    }
}
