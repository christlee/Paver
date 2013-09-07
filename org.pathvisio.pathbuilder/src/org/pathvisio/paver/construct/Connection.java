package org.pathvisio.paver.construct;

import java.util.HashSet;
import java.util.Set;
import org.pathvisio.core.model.LineType;

public class Connection {
	private Node start;
	private Node end;
	private LineType lineType;
	
		
	public Connection(Node start, Node end, LineType lineType){
		this.start = start;
		this.end = end;
		this.lineType = lineType;
	}
	
	public Node getStart(){
		return start;
	}
	
	public Node getEnd(){
		return end;
	}
	
	public LineType getLineType(){
		return lineType;
	}
	
	public Set<Node> getNodes(){
		Set<Node> nodes = new HashSet<Node>();
		nodes.add(start);
		nodes.add(end);
		return nodes;
	}
	

}
