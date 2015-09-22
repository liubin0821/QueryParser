package service.provider;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bench.BenchManager;
import bench.BenchQuery;
import bench.ParserAgent;
import bench.QueryClassResult;

import bench.db.DBBenchManager;

import com.google.gson.Gson;
import com.myhexin.qparser.Query;

/****
 * 给外部提供服务的集成servlet
 * 
 * @author - lu
 */
public class ProviderServlet extends HttpServlet {
	/** */
	private static final long serialVersionUID = 1L;
	private BenchManager benchManager;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		final String database = "ontoask";
		String parseConf = getServletContext().getRealPath("/conf/qparser.conf");
		
		try {
			ParserAgent.init(parseConf);
	    	benchManager = new DBBenchManager(database, false);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {

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

	private void doCommon(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {

		final String encoding = "utf-8";
		httpResponse.setCharacterEncoding(encoding);
		httpResponse.setContentType("text/html; charset=" + encoding);
		try {
			PrintWriter writer = httpResponse.getWriter();
			httpRequest.setCharacterEncoding(encoding);

			String oper = new String(httpRequest.getParameter("oper").getBytes(
					"ISO-8859-1"), "utf-8").trim();
			String text = new String(httpRequest.getParameter("text").getBytes(
					"ISO-8859-1"), "utf-8").trim();
			String cmdStr = oper + "\t" + text;
			CommandHandler cmdHander = new CommandHandler(cmdStr, benchManager);
			String result = cmdHander.handleCommand();
			writer.println(result);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
