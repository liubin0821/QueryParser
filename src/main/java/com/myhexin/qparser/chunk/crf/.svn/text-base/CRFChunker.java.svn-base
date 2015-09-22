package com.myhexin.qparser.chunk.crf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.define.EnumDef.UrlReqType;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.util.RequestItem;
import com.myhexin.qparser.util.ResponseItem;
import com.myhexin.qparser.util.URLReader;
import com.myhexin.qparser.util.Util;

public class CRFChunker {
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(CRFChunker.class.getName());
    
    private JsonParser jparser_ = new JsonParser();
    
    /**
     * 切分成功，返回切分结果，否则，返回null或者空
     * @param unchunkStr
     * @return
     */
    public List<Entry<String, Double>> run( String unchunkStr ){
        try{
            return remoteRun( unchunkStr );
        }catch( QPException e ){
            logger_.error(e.getMessage());
            return null;
        }catch( Exception e ){
            logger_.error(e.getMessage());
            return null;
        }
    }
    
    private List<Entry<String, Double>> remoteRun( String unchunkStr ) throws QPException{
        String logStr = null;
        String url = getCRFUrl();
        URLReader urlReader = new URLReader(url);
        RequestItem reqItem = new RequestItem(UrlReqType.CK_CRF);
        reqItem.setQueryStr(unchunkStr);
        ResponseItem rspItem = urlReader.run(reqItem);
        
        if( !rspItem.getRspOK() ){
            logStr = Util.linkStringArrayListBySpl(rspItem.getRspLogs(), "\n");
            logger_.error(logStr);
            return null;
        }
        String iwantJsonRlt = Util.linkStringArrayListBySpl(rspItem.getRspRlts(), "");
        List<Entry<String, Double>> rlt = parseJsonRlt(iwantJsonRlt);
        return rlt;
    }
    
    /**
     * 解析形如
     * {"qtime": 3.99603,"result" : [ { "value":"j值大于0小于10,_&_kdj底背离", "prob":0.918986} ]}
     * 这样的json格式的数据
     * @param jsonRlt
     * @return
     * @throws QPException
     */
    private List<Entry<String, Double>> parseJsonRlt( String jsonRlt ) throws QPException{
        JsonElement root = null;
        try{
            root = jparser_.parse(jsonRlt);
        }catch( Exception e ){
            throw new QPException(String.format("[%s]不是Json格式数据", jsonRlt));
        }
        JsonArray results = root.getAsJsonObject().get("result").getAsJsonArray();
        if( results.size() <= 0 ){
            throw new QPException(String.format("[%s]无chunk切分结果", jsonRlt));
        }
        List<Entry<String, Double>> rlt = new ArrayList<Entry<String, Double>>();
        for( int i=0; i< results.size(); i++ ){
            JsonObject firstResult = results.get(i).getAsJsonObject();
            String value = firstResult.get("value").getAsString();
            Double prob = firstResult.get("prob").getAsDouble();
            if( !isAcceptableProb(prob) ){
                continue;
            }
            Entry<String, Double> newEntry = new SimpleEntry<String, Double>(value, prob);
            rlt.add(newEntry);
        }
        return rlt;
    }
    
    private boolean isAcceptableProb( Double prob ){
        return prob >= Param.CRF_MIN_PROB;
    }
    
    private String getCRFUrl() throws QPException{
        final String crfUrlTemplate = "http://%s/cm?type=chunk&q=";
        String address;
        address = getCRFAddress();
        String url = String.format( crfUrlTemplate, address );
        return url;
    }
    
    private String getCRFAddress() throws QPException{
    	return Param.CRF_ENV_ADDRESS;
    	/*
        List<String> useableAddress = null;
        if( Param.CRF_ENV_TEST.equals(Param.CRF_ENV) ){
            useableAddress = Param.CRF_ENV_ADDRESS.get(Param.CRF_ENV_TEST);
        }else{
            useableAddress = Param.CRF_ENV_ADDRESS.get(Param.CRF_ENV_PROD);
        }
        int size = useableAddress.size();
        int addIndex = Util.getRdmInt(size);
        if( addIndex < 0 ){
            throw new QPException("未成功获得CRF地址");
        }
        return useableAddress.get(addIndex);*/
    }
}