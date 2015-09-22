package com.myhexin.qparser.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class ServletOutputStreamMock extends ServletOutputStream {

	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}
	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}
	
	@Override
	public void write(byte[] b, int start, int len) throws IOException {
		out.write(b, start, len);
	}
	
	public byte[] toByteArray() {
		return out.toByteArray();
	}
}
