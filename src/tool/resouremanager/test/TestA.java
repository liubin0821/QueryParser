package resouremanager.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import resouremanager.dataIO.HttpDataGetterResult;
import resouremanager.dataIO.HttpGetDataGetter;

public class TestA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HttpGetDataGetter getter = new HttpGetDataGetter("http://172.20.23.52:8090/RM/queryclass?qid=1&text=同花顺的股票") ;

		Pattern p = Pattern.compile("(?<=queryPattern\":\").*?(?=\",\"synonyms\")") ;
		
		HttpDataGetterResult result = getter.getData() ;
		for(String s : result.data()){
			System.out.println(s);
			Matcher m = p.matcher(s) ;
			while(m.find()){
				System.out.println(m.group());
			}
		}
		
		
		
	}

}
