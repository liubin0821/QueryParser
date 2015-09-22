package resouremanager.dataIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HttpGetDataGetter extends HttpDataGetter {
	
	
	public HttpGetDataGetter(String Url) {
		super(Url);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public HttpDataGetterResult getData(){
		List<String> resList = new ArrayList<String>() ;
		try {
			if(this.Url.isEmpty())
				return null;
			URL URL = new URL(this.Url);
			HttpURLConnection con = (HttpURLConnection) URL
	                .openConnection();
			con.connect();
			connection = con ;	
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(this.connection.getInputStream(),"utf-8"));//设置编码,否则中文乱码
			String line = null;
			while ((line = reader.readLine()) != null){
				resList.add(new String(line.getBytes(), "utf-8")) ;
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			this.connection.disconnect() ;
		}
		return new HttpDataGetterResult(resList) ;
	}
	
	public static void main(String[] args){
		HttpGetDataGetter getter = new HttpGetDataGetter("http://x.10jqka.com.cn/stockpick/search?tid=stockpick&w=近5日换手率排名前10的股票&qs=error_sl&x=Xi%5BYPX%5EIX%5C%5CXZXDXY%5B%5DDX%5D%5C&y=145&ts=1") ;
//		getter.reSetUrl("http://192.168.23.52:8090/bench/queryclass?qid=1&text=同花顺") ;
		getter.reSetUrl("http://172.20.23.52:8090/RM/queryclass?qid=1&text=同花顺的股票") ;
		
		HttpDataGetterResult result = getter.getData() ;
		for(String s : result.data()){
			System.out.println(s);
		}
		
		
	}
}
