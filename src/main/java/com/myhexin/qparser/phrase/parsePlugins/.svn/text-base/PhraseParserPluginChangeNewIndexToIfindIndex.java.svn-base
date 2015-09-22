package com.myhexin.qparser.phrase.parsePlugins;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.commons.collections.CollectionUtils;

import com.myhexin.qparser.define.EnumDef.DataSource;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginChangeNewIndexToIfindIndex extends
        PhraseParserPluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginChangeNewIndexToIfindIndex.class.getName());
	
	//索引
	public static  HashMap<String, LinkedHashSet<String>> indexs = new HashMap<String, LinkedHashSet<String>>(91000);
	public static  HashMap<String, String> ifind = new HashMap<String, String>(940000);
	
	public PhraseParserPluginChangeNewIndexToIfindIndex() {
		super("Change_New_Index_To_Ifind_Index");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return ChangeNewIndexToIfindIndex(nodes,ENV);
	}

	private ArrayList<ArrayList<SemanticNode>> ChangeNewIndexToIfindIndex(
	        ArrayList<SemanticNode> nodes,Environment ENV) {
		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
		for (SemanticNode sn : nodes) {
			if(sn.getType() != NodeType.FOCUS || !((FocusNode) sn).hasIndex())
				continue;
			
			doChange((FocusNode) sn);
			
		}
		tlist.add(nodes);
		return tlist;
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-28 下午9:20:34
     * @description:   	
     */
    private void doChange(FocusNode fn) {
    	ClassNodeFacade cn = fn.getIndex();
    	if (cn==null || !cn.getFieldsAll().contains("hghy"))
    		return;
    	LinkedHashSet<String> ifindTexts = new LinkedHashSet<String>();
    	for (PropNodeFacade pn : cn.getClassifiedProps(PropType.STR)) {
			if(pn.getValue()!=null)
				ifindTexts.add(pn.getValue().getText() );			
		}
    	
		LinkedHashMap<String,String> ifindIndexs =  getIfindIndex(ifindTexts,fn.getText() ,cn.getText() ); 
		String ifindIndex = "";
		int length = Integer.MAX_VALUE;
		for (String index : ifindIndexs.keySet()) {
			if(index.length()<length){
				ifindIndex = index;
				length = index.length();
			}				
		}
		
		HashSet<String> removes = new HashSet<String>();
		CollectionUtils.addAll(removes, ifindIndex.split(":"));
		for(PropNodeFacade pn : cn.getClassifiedProps(PropType.STR)){
			if(pn.getValue() !=null && removes.contains(pn.getValue().getText() ))
				cn.removeProp(pn);
		}
		if(ifindIndex==""){
			logger_.warn(cn.getText()+"未能转换为ifind对应指标, ifind中不存在相应指标");
			return;
		}		
		cn.setText(ifindIndex);
		cn.setDataSrc(DataSource.IFIND);
		cn.setIndexID(ifindIndexs.get(ifindIndex));
	    	
    }

    
    /**
     * @author: 	    吴永行 
     * @param cnText 
     * @param cnText 
     * @param fnText 
     * @dateTime:	  2014-4-29 上午10:38:16
     * @description:   	
     * 
     */
    @SuppressWarnings("unchecked")
    private LinkedHashMap<String,String> getIfindIndex(HashSet<String> ifindTexts, String fnText, String cnText) {
    	
    	LinkedHashMap<String, String> finalIfindIndex = new LinkedHashMap<String, String>();   	
    	LinkedHashMap<String, Integer> dictFn = new LinkedHashMap<String, Integer>();
    	LinkedHashMap<String, Integer> dictCn = new LinkedHashMap<String, Integer>();
     	   	
    	//focusNode.text获取
    	getIfindIndexBySegment(dictFn, fnText);    	
    	//使用cn.text获取
    	getIfindIndexBySegment(dictCn, cnText);
    	
    	for (String segment : ifindTexts) {    		
    		getIfindIndexBySegment(dictFn, segment);
    		getIfindIndexBySegment(dictCn, segment);
		} 
    	
    	ArrayList<String> idsCn = selectIfindIndexId(dictFn, dictCn);
    	   	
    	for (String id : idsCn) {
    		finalIfindIndex.put(ifind.get(id), id);
		}
	    return finalIfindIndex;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-5-4 上午10:51:28
     * @description:   	
     * @param dictFn
     * @param dictCn
     * @return
     * 
     */
    private final ArrayList<String> selectIfindIndexId(
            LinkedHashMap<String, Integer> dictFn,
            LinkedHashMap<String, Integer> dictCn) {
	    int maxFn = 0;
    	ArrayList<String> idsFn = new ArrayList<String>();
    	for (String key : dictFn.keySet()) {
    		if(dictFn.get(key)<maxFn)
    			continue;   		
			if(dictFn.get(key)>maxFn){
				idsFn.clear();
				maxFn = dictFn.get(key);
			}
			idsFn.add(key);
		}
    	
    	int maxCn = 0;
    	ArrayList<String> idsCn = new ArrayList<String>();
    	for (String key : dictCn.keySet()) {
    		if(dictCn.get(key)<maxCn)
    			continue;   		
			if(dictCn.get(key)>maxCn){
				idsCn.clear();
				maxCn = dictCn.get(key);
			}
			idsCn.add(key);
		}
    	
    	if(maxFn>=maxCn)
    		return idsFn;
    	else
    		return idsCn;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-5-4 上午10:42:51
     * @description:   	
     * @param dict
     * @param segment
     * 
     */
    private final void  getIfindIndexBySegment(LinkedHashMap<String, Integer> dict,
            String segment) {
	    if(indexs.get(segment) != null)
	    	for(String id : indexs.get(segment)){
	    		if(dict.containsKey(id))
	    			dict.put(id, dict.get(id)+1);
	    		else
	    			dict.put(id, 1);
	    	}
    }
	

	public static void loadIfindIndexDate(String fileName){
		try {
			getIfindIndexFromFileAndWriteToFile(fileName);  
			//getIfindIndexFromObjectFile();
        } catch (Exception e) {      
	        e.printStackTrace();
        }
	}

    //这个需要8.318秒
    private static void getIfindIndexFromFileAndWriteToFile(String fileName)
	        throws FileNotFoundException, IOException {
		//long a=System.currentTimeMillis();
		if (indexs.size() == 0)
			synchronized (PhraseParserPluginChangeNewIndexToIfindIndex.class) {
				if (indexs.size() != 0)
					return;

				BufferedReader br = new BufferedReader(new FileReader(fileName));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("#"))
						continue;
					String[] oneLine = line.split(",");
					if (oneLine.length == 2) {
						ifind.put(oneLine[0], oneLine[1]);
						for (String segment : oneLine[1].split(":")) {
							if (segment != "") {
								if (indexs.containsKey(segment))
									indexs.get(segment).add(oneLine[0]);
								else {
									LinkedHashSet<String> temp = new LinkedHashSet<String>();
									temp.add(oneLine[0]);
									indexs.put(segment, temp);
								}
							}
						}

					}
				}
				//System.out.println("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
			}
	}
}
