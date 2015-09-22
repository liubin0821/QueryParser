package resouremanager.dataIO;

import java.util.Collections;
import java.util.List;

public class HttpDataGetterResult implements DataGetterResult {
	HttpDataGetterResult(List<String> list){
		if(list == null)
			resultList = Collections.emptyList() ;
		else 
			resultList = list ;
	}
	
	final private List<String> resultList ;
	
	public List<String> data(){
		return resultList ;
	}
}
