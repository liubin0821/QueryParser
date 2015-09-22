package com.myhexin.server;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public abstract class TemplateServlet extends HttpServlet{
	protected static final long serialVersionUID = 1L;
	protected Configuration cfg_;
	protected static HashSet<String> notExistsTempleteSet ;
	@Override
	public void init() throws ServletException {
		cfg_ = new Configuration();
		notExistsTempleteSet = new HashSet<String>();
		cfg_.setClassForTemplateLoading(Thread.currentThread().getClass(), "/webapp/template");
		cfg_.setObjectWrapper(new DefaultObjectWrapper());
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("utf-8");
		//response.setContentType("application/json; charset=utf-8");
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("utf-8");
		//response.setContentType("application/json; charset=utf-8");
	}
}
