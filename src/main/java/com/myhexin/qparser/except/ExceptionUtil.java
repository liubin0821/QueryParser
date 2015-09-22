package com.myhexin.qparser.except;


public class ExceptionUtil {
    public static String getStackTrace(Exception e) {
       /* StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        Throwable cause = e.getCause();
        if(cause != null) {
            pw.println(" Cause by:");
            cause.printStackTrace(pw);
        }
        return sw.getBuffer().toString();*/
        
        StringBuilder buf = new StringBuilder();
        buf.append(e.getMessage()).append('\n');
        StackTraceElement[] elems = e.getStackTrace();
		for(int i=0;i<elems.length && i<30;i++) {
			buf.append("\t" + elems[i]).append('\n');
		}
		return buf.toString();
    }
}
