package resouremanager.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryParser;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.pattern.ExtendedRegex;

public class RegexTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		QueryParser.getParser("./conf/qparser.conf");
		BufferedReader br = new BufferedReader(new FileReader(new File("regexQuery.txt"))) ;
		String line = null ;
		int queryCount = 0 ;
		while((line = br.readLine()) != null){
			if(line.startsWith("#") || line.equals(""))
				continue ;
			if(line.startsWith("break"))
				break ;
			Query query = new Query(line);
			queryCount++ ;
            String result = ExtendedRegex.transText(query, Type.STOCK);
//            System.out.println("STD:" + result);
//            System.out.println(line + "\n") ;
            
            System.out.println("问句:" + line);
            System.out.println("最终理解成:" + result + "\n");
            
		}
		System.out.println("Total Query: " + queryCount);
		br.close() ;
	}

}
