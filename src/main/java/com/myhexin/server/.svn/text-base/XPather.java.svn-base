package com.myhexin.server;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathResult;

public class XPather {

    private List<Node> resultNodes = new ArrayList<Node>();
    
    private boolean withFormat = false;
    
    public void setWithFormat(boolean withFormat) {
        this.withFormat = withFormat;
    }
    
    static public XPather getInstance()
    {
        //should change it later
        return getInstance(false);
    }
    
    static public XPather getInstance(boolean withFormat)
    {
        XPather xPather =  new XPather();
        xPather.setWithFormat(withFormat);
        return xPather;
    }
    
    public boolean extract(String expr, Node node) {
        return extract(expr, node, false);
    }
    
    public boolean extract(String expr, Node node, boolean onlyValidNode) {
        resultNodes = new ArrayList<Node>();
        
        if (expr == null || expr.length() == 0)
        {
            return false;
        }
        
        XPathEvaluator xpathEvaluator 
            = new org.apache.xpath.domapi.XPathEvaluatorImpl();

        XPathResult result = (XPathResult) xpathEvaluator.evaluate(expr,
                node, null, XPathResult.ANY_TYPE, null);
        switch(result.getResultType())
        {
        case XPathResult.STRING_TYPE:
            break;
        case XPathResult.BOOLEAN_TYPE:
            break;
        case XPathResult.NUMBER_TYPE:
            break;
        default:
            boolean has = false;
            
            do {
                Node n = result.iterateNext();

                if (n == null) {
                    break;
                }
                has = true;
                resultNodes.add(n);                
            } while (true);
            
            return has;
        }
  
        return true;

        
    }

    public List<Node> getResultNodes()
    {
        return resultNodes;
    }
    
}
