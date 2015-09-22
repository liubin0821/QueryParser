package com.myhexin.qparser.tokenize;

import java.util.LinkedHashSet;
import java.util.List;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.OntoXmlReader;

public class StrNodeUtil {
    
    public static boolean hasSameOfWhat(Environment ENV,StrNode curNode, StrNode nodeJ) {
    	//无语  看看多简单
    	boolean result = false;
    	Query.Type qType = (Type) ENV.get("qType",false);
    	
    	
    	//用短的subType做外层循环
    	//用长的做内层contains操作,这样大的可以包含小的
    	//liuxiaofeng 2015/8/24 "000156或300315或300061或002108或000626或600639", 其中000626也是基金代码,包含更多的subType,没有合并,加了这个改动后正确合并
    	LinkedHashSet<String> shortSubType = null;
    	LinkedHashSet<String> longSubType = null;
    	if(curNode.subType!=null && nodeJ.subType!=null) {
    		if(curNode.subType.size()> nodeJ.subType.size()) {
    			shortSubType = nodeJ.subType;
    			longSubType = curNode.subType;
    		}else{
    			longSubType = nodeJ.subType;
    			shortSubType = curNode.subType;
    		}
    	}
    	
    	if(shortSubType==null || longSubType==null) {
    		return result;
    	}
    	
        for(String nodeType: shortSubType){
        	if(longSubType.contains(nodeType))
        		result = true;
        	//不包含但是, 是默认指标 且符合领域限制
        	else if(OntoXmlReader.subTypeToIndexContainKey(nodeType)){ 
                List<ClassNodeFacade> vector = OntoXmlReader.subTypeToIndexGet(nodeType);
				for (ClassNodeFacade sn : vector) {
					if(!sn.remainDomain(qType))
						continue;	
					return false;
				}
        		
        	}
        	
        	
        }
        return result;
    	
    }

}
