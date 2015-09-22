package com.myhexin.qparser.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.qparser.server.db.LogOperater;
import com.myhexin.qparser.server.db.ReplyParam;

public class ReplyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
			.getLogger(ReplyServlet.class.getName());
	private static LogOperater logOperater;
	private static String errorHead = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n<response>\n\r<errno>-1</errno>\n\r<errmsg>";
	private static String errorEnd = "</errmsg>\n</response>";

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logOperater = new LogOperater();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		String type = request.getParameter("type");
		response.setCharacterEncoding("GBK");
		response.setContentType("text/html;charset=gbk");
		logger_.info("received post [{}]", type);
		String qid;
		int flag = -1;
		String message = null;
		if ("ifindanswer".equals(type)) {
			String feedBack = request.getParameter("feedback");
			message = logOperater.addIfindAnswer(feedBack);
			if (message == null) {
				flag = 1;
			}
		} else if ("feedback".equals(type)) {
			qid = request.getParameter("qid");
			String feedBack = new String(request.getParameter("feedback")
					.getBytes("UTF-8"));
			String checklist = new String(request.getParameter("checklist"));
			String email = new String(request.getParameter("emailtxt")
					.getBytes("UTF-8"));
			String other = new String(request.getParameter("othertxt")
					.getBytes("UTF-8"));

			checklist = checklist == null ? "" : checklist;
			email = email == null ? "" : email;
			other = other == null ? "" : other;

			int rating = Integer.parseInt(request.getParameter("rating"));
			int acceptemail = Integer.parseInt(request
					.getParameter("acceptval"));

			String rcv = String
					.format("Recv feedBack qid-[{%s}] -feedback[{%s}] -checklist[{%s}] email-[{%s}] -other[{%s}] rating-[{%d}] acceptemail[{%d}]",
							qid, feedBack, checklist, email, other, rating,
							acceptemail);
			logger_.info(rcv);
			logOperater.addFeedBack(qid, feedBack, rating, checklist, email,
					other, acceptemail);

			flag = 2;
		} else if ("recentquery".equals(type)) {
			// logger_.
			String user = request.getParameter("user");
			message = logOperater.getRecentQuery(user, "10");
			flag = 3;
			logger_.info("get resolved query {}", message);
		} else {
			response.getWriter().print(
					"Miss parameter: set 'ifindanswer' or 'feedback'.\n");
			logger_.info("Reply type error[{}]", type);
		}
		response.getWriter().print("respons message:" + flag);
		if (message != null)
			response.getWriter().print(message);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String type = request.getParameter("type");
		response.setCharacterEncoding("GBK");
		response.setContentType("text/html;charset=gbk");
		logger_.info("received reply:[{}]", type);
		if (type.equals("stuffreply")) {
			String user = new String(request.getParameter("user").getBytes(
					"ISO-8859-1"), "UTF-8");
			logger_.info("user is:[{}]", user);
			response.getWriter().print(logOperater.getRecentReply(user, "10"));
		} else if (type.equals("changeauto")) {
			String switchFlag = request.getParameter("switchFlag");
			String msg = request.getParameter("autoMsg");
			String autoMsg = msg != null ? new String(
					msg.getBytes("ISO-8859-1"), "UTF-8") : null;
			logger_.info("changeauto:switchFlag [{}] autoMsg [{}]", switchFlag,
					autoMsg);
			response.getWriter().print(
					ReplyParam.changeAutoMeg(switchFlag, autoMsg));
		} else {
			response.getWriter().print(
					errorHead + "type should be:stuffreply or changeauto"
							+ errorEnd);
		}
	}
}