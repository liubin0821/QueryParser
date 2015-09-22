package com.myhexin.server.servlet;

import javax.servlet.http.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myhexin.server.service.Service;

public abstract class ParserAbstractServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected Logger logger;
	protected Service service;

	public ParserAbstractServlet() {
		super();
		
		logger = LoggerFactory.getLogger(this.getClass().getName());
	}

	// set和get方法
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}
}
