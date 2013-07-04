package org.pathvisio.pathbuilder;

import java.util.HashSet;
import java.util.Set;

public class Connection {
	private Node start;
	private Node end;
	private String arrowtype;
	
	Connection(Node start, Node end, String arrowtype){
		this.start = start;
		this.end = end;
		this.arrowtype = arrowtype;
	}
	
	public Node getStart(){
		return start;
	}
	
	public Node getEnd(){
		return end;
	}
	
	public String getArrowtype(){
		return arrowtype;
	}
	
	public Set<Node> getNodes(){
		Set<Node> nodes = new HashSet<Node>();
		nodes.add(start);
		nodes.add(end);
		return nodes;
	}
}
