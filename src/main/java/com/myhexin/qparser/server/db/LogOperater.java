package com.myhexin.qparser.server.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.myhexin.datasrc.MySqlClient;
import com.myhexin.qparser.util.Util;

import java.sql.ResultSet;

public class LogOperater {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
			.getLogger(LogOperater.class.getName());
	private MySqlClient mySqlClient_;
	static final int TOMCAT = 0;

	public LogOperater() {
		mySqlClient_ = MySqlClient.newMySqlClient(TOMCAT);
	}

	public void addQuery(String qid, String query, String user,
			String parse_result) {
		getConnect();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		String sqlString = String
				.format("INSERT INTO `ifindlog` (`qid`, `qtime`,"
						+ " `query`, `user`, `parse_result`) VALUES ('%s','%s','%s','%s','%s')",
						qid, time, query, user, parse_result);
		mySqlClient_.execute(sqlString);
		logger_.info("Output DB [{}:{}]", qid, query);
		close();
	}

	public void addFeedBack(String qid, String feedBack, int rating,
			String checklist, String email, String other, int acceptemail) {
		getConnect();
		String sqlString = "UPDATE `ifindlog` SET `rating`='" + rating
				+ "', `feedBack`='" + feedBack + "', `checklist`='" + checklist
				+ "', `email`='" + email + "', `othertext`='" + other
				+ "', `acceptemail`='" + acceptemail + "' WHERE `qid`='" + qid + "'";
		mySqlClient_.execute(sqlString);
		logger_.info("log feedBack [{}]", sqlString);
		close();
	}

	public String addIfindAnswer(String feedBack) {
		String msg = null;
		String qid = getSubXMLString("backid", feedBack);
		if (qid == null) {
			msg = "\nmiss <backid> node";
			logger_.info(msg);
		} else {
			getConnect();
			logger_.info("qid:[{}]", qid);
			String sqlString = "UPDATE `ifindlog` SET ifind_result='"
					+ feedBack + "' WHERE `qid`='" + qid + "'";
			mySqlClient_.execute(sqlString);
			close();
			logger_.info("log ifind answer [{}]", sqlString);
		}
		return msg;
	}

	private String getSubXMLString(String nodename, String feedBack) {
		String nodeValue = null;
		int index = feedBack.indexOf("</" + nodename + ">");
		if (index > 0) {
			nodeValue = feedBack.substring(
					feedBack.indexOf("<" + nodename + ">") + 2
							+ nodename.length(), index);
		}
		return nodeValue;
	}

	public String getRecentQuery(String user, String limit) {
		StringBuilder queries = new StringBuilder();
		getConnect();
		String sqlString = "SELECT query,qtime,resolved FROM `ifindlog` where  user='"
				+ user + "'" + " and resolved=1 order by qtime desc";
		if (limit != null)
			sqlString += " limit " + limit;
		try {
			logger_.info(sqlString);
			ResultSet rs = mySqlClient_.executeQuery(sqlString);
			while (rs.next()) {
				queries.append("\n" + rs.getString("query"));
			}
		} catch (Exception e) {
			logger_.error("sql Exception:[{}]", e.getMessage());
		} finally {
			close();
		}
		if (queries.length() > 1)
			return queries.toString();
		return null;
	}

	public String getRecentReply(String user, String limit) {
		String reply = null;
		getConnect();
		if (user == null) {
			reply = XMLConstruct(ERROR, "miss user");
			logger_.info("recentReply error:miss user");
		} else {
			String sqlString = null;
			if (ReplyParam.isOn) {
				// sqlString =
				// "select qtime,query,reply from ifindlog where  user='" + user
				// + "'and  ifind_result='' "
				// +
				// "union select qtime,query,reply from ifindlog where  user='"
				// + user
				// + "'and rating='0' " + "order by qtime desc ";
				sqlString = "select qtime,query,reply from ifindlog where  user='"
						+ user
						+ "' and  ifind_result=''and qtime>'"
						+ ReplyParam.AUTO_TIME
						+ "'"
						+ "union select qtime,query,reply from ifindlog where  user='"
						+ user
						+ "'and rating='0' and qtime>'"
						+ ReplyParam.AUTO_TIME
						+ "'"
						+ "union select qtime,query,reply from ifindlog where  user='"
						+ user
						+ "' and reply!='' and  ifind_result='' "
						+ "union select qtime,query,reply from ifindlog where  user='"
						+ user
						+ "' and reply!=''and rating='0' order by qtime desc";
			} else {
				sqlString = "select qtime,query,reply from ifindlog where  user='"
						+ user
						+ "' and reply!='' and  ifind_result='' "
						+ "union select qtime,query,reply from ifindlog where  user='"
						+ user
						+ "' and reply!=''and rating='0' "
						+ "order by qtime desc ";
			}
			sqlString += limit != null ? (" limit " + limit) : "";
			logger_.info("stuffreply sql:[{}]", sqlString);
			try {
				ResultSet rs = mySqlClient_.executeQuery(sqlString);
				reply = XMLConstruct(REPLYRESULT, rs);
			} catch (Exception e) {
				logger_.error("sql Exception:[{}]", e.getMessage());
			} finally {
				close();
			}
		}
		return reply;
	}

	private static final int ERROR = -1;
	private static final int REPLYRESULT = 0;

	private String XMLConstruct(int flag, Object object) {
		StringBuilder replyXml = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"GBK\"?>\n<response>\n\r");
		if (flag == ERROR) {
			replyXml.append("<errno>-1</errno>\n\r<errmsg>" + (String) object
					+ "</errmsg>\n\r");
		}
		if (flag == REPLYRESULT) {
			replyXml.append("<errno>0</errno>\n\r<errmsg/>\n\r");
			ResultSet rs = object instanceof ResultSet ? (ResultSet) object
					: null;
			if (rs != null) {
				replyXml.append("<rows>\n\r");
				addRows(replyXml, rs);
				replyXml.append("</rows>\n");
			}
		}
		replyXml.append("</response>");
		return replyXml.toString();
	}

	private void addRows(StringBuilder replyXml, ResultSet rs) {
		try {
			while (rs.next()) {
				replyXml.append("<row>\n\r\r");
				replyXml.append("<sent>"
						+ Util.escapeXML(rs.getString("query"))
						+ "</sent>\n\r\r");
				String replyString = rs.getString("reply");
				if (ReplyParam.isOn) {
					if (replyString.length() < 1) {
						String qtime = rs.getString("qtime");
						if (qtime.compareTo(ReplyParam.AUTO_TIME) > 0)
							replyString = ReplyParam.AUTO_MSG;
					}
				}
				replyXml.append("<reply>" + Util.escapeXML(replyString)
						+ "</reply>\n\r</row>\n\r");
			}
		} catch (Exception e) {
			logger_.error("reply ResultSet parse Error:[{}]", e.getMessage());
		}
	}

	private void getConnect() {
		mySqlClient_.getConnectionFromPool();
	}

	private void close() {
		mySqlClient_.closeCon();
	}
}
