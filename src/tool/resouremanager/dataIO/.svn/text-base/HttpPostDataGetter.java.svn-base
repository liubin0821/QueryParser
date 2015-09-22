package resouremanager.dataIO;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;



public class HttpPostDataGetter extends HttpDataGetter {

	public HttpPostDataGetter(String Url, String content) {
		super(Url);
		// TODO Auto-generated constructor stub
		if(content != null)
			this.content = content ;
	}
	public HttpPostDataGetter(String Url){
		this(Url, null) ;
	}
	
	/** 正文内容,即?后面的参数 */
	private String content = "" ;
	
	public void reSetContent(String newContent){
		if(content != null)
			this.content = newContent ;
	}
	
	private void connect() throws IOException{
		if(this.Url.isEmpty())
			return ;
		URL postURL = new URL(this.Url);
		HttpURLConnection con = (HttpURLConnection) postURL
                .openConnection();
		//协议设定
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setConnectTimeout(5000);// 连接超时时间  
		con.setReadTimeout(5000); // 读取超时
		con.setRequestMethod("POST");
		con.setUseCaches(false);
		con.setInstanceFollowRedirects(true);
		con.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
		DataOutputStream out = new DataOutputStream(con
                .getOutputStream());
        // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写入流里面
		String encodeContent = URLEncoder.encode(this.content, "utf-8") ;
        out.writeBytes(encodeContent); 
        out.flush();
        out.close(); // flush and close
		this.connection = con ;			
	}
	
	@Override
	public HttpDataGetterResult getData() {
		List<String> dataList = new ArrayList<String>() ;
		try {
			this.connect() ;
			if(connection == null)
			{
				System.err.println("连接失败");
				return null ;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					this.connection.getInputStream(), "utf-8"));
			String line = null ;
			while((line = reader.readLine()) != null){
				dataList.add(line) ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 设置编码,否则中文乱码

		return new HttpDataGetterResult(dataList);
	}
	
	public static void main(String[] args){
		
		HttpPostDataGetter getter = new HttpPostDataGetter("http://172.20.0.52/zfontology/interface/script/synwordOutput.php?dic=all") ;
//		getter.reSetUrl("http://x.10jqka.com.cn/stockpick/search?tid=stockpick&w=近5日换手率排名前10的股票&qs=error_sl&x=Xi%5BYPX%5EIX%5C%5CXZXDXY%5B%5DDX%5D%5C&y=145&ts=1") ;
//		getter.reSetContent("") ;
		getter.reSetUrl("http://172.20.0.52/thsft/newdagudong/stocksInfo.txt") ;
		System.out.println(getter.Url());
		HttpDataGetterResult res= getter.getData() ;
		int count = 0 ;
		for(String s : res.data()){
			if(count++ > 10)
				break ;
			System.out.println(s);
		}
	}
}
