package conf.stock.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import tool.task.UpdateIndexValTask;
import tool.task.UpdateMaxMinValTask;

import com.myhexin.qparser.util.Util;

import conf.stock.StockIndexConfUpdate;
import conf.tool.AbstracToolTimerTask;
import conf.tool.ToolTimerTask;

public class StockIndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StockIndexConfUpdate stockIndexConfUpdate = null;
	private static Document doc = null;
	static String PATH = null; // 真实路径
	private static int count = 0; // 未写入文件的更新个数

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		stockIndexConfUpdate = new StockIndexConfUpdate();
		PATH = this.getServletContext().getRealPath(
				"/data/stock/stock_index.xml");
		try {
			doc = Util.readXMLFile(PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AbstracToolTimerTask.TimerType month = AbstracToolTimerTask.TimerType.MONTH ;
		ToolTimerTask  toolTask = 
				new ToolTimerTask.Builder(month).delay(10000).period(3600000).build() ; 
		/** 写入最大值最小值 , 每一小时(3600000)写入文件 **/
		UpdateMaxMinValTask maxminTask = new UpdateMaxMinValTask(
				stockIndexConfUpdate, doc, PATH);
		toolTask.schedule(maxminTask, false) ;

		/** 更新index的value,每一个月的第一天的零时零分零秒更新 **/
		UpdateIndexValTask indexvalTask = new UpdateIndexValTask(
				stockIndexConfUpdate, doc, PATH);
		toolTask.schedule(indexvalTask, true) ;
	}

	public void destroy() {
//		if (doc != null && PATH != null)
//			stockIndexConfUpdate.writeToXML(doc, PATH);
	}

	@Override
	public void doGet(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		doCommon(httpRequest, httpResponse);
	}

	@Override
	public void doPost(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		doCommon(httpRequest, httpResponse);
	}

	protected void doCommon(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		// ToDo
		final String encoding = "utf-8";
		httpResponse.setCharacterEncoding(encoding);
		httpResponse.setContentType("text/html; charset=" + encoding);

		try {
			PrintWriter writer = httpResponse.getWriter();
			httpRequest.setCharacterEncoding(encoding);
			String sIndexTitle = new String(httpRequest.getParameter(
					"indexTitle").getBytes("ISO-8859-1"), "utf-8");
			String sMaxVal = new String(httpRequest.getParameter("maxval")
					.getBytes("ISO-8859-1"), "utf-8");
			String sMinVal = new String(httpRequest.getParameter("minval")
					.getBytes("ISO-8859-1"), "utf-8");
			String res = stockIndexConfUpdate.addValueNode(doc, sIndexTitle,
					sMaxVal, sMinVal);

			/** 超过100个写入文件 **/
			if (count > 10) {
				stockIndexConfUpdate.writeToXML(doc, PATH);
				count = 0; // 置零
			} else {
				count++;
			}

//			stockIndexConfUpdate.writeToXML(doc, PATH);

			writer.println(res + "  title=[" + sIndexTitle + "]" + "  maxval=["
					+ sMaxVal + "]" + "  minval=[" + sMinVal + "]"
					+ "Version=v1.1");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static int count(){
		return count ;
	}
	public static void countClear(){
		count = 0 ;
	}
}
