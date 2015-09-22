package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.parsePostPlugins.output.imp.RelationshipOfOrigChunk;


public class PhraseParserPluginRelationshipOfOrigChunk extends PhraseParserPluginAbstract{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginRelationshipOfOrigChunk.class.getName());
	private String split_ = "_&_";
	
	public PhraseParserPluginRelationshipOfOrigChunk() {
		super("Orig_Chunk_Output");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return relationshipOfOrigChunk(nodes);
    }
    
    public ArrayList<ArrayList<SemanticNode>> relationshipOfOrigChunk(ArrayList<SemanticNode> nodes) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    	String result = RelationshipOfOrigChunk.relationshipOfOrigChunk(nodes, split_);
    	if (result!=null)
    		nodes.get(0).setOrigChunk( result);
    	rlist.add(nodes);
		return rlist;
    }
    
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]: %s\n", i, qlist.get(i).get(0).getOrigChunk()));
        }
        //sb.append(logsb_.toString());
        //logsb_  = new StringBuilder();
    }
}
