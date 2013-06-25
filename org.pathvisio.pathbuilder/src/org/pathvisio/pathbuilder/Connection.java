package org.pathvisio.pathbuilder;

import java.util.HashSet;
import java.util.Set;

import org.bridgedb.Xref;

public class Connection {
	private Xref start;
	private Xref end;
	private String arrowtype;
	
	Connection(Xref start, Xref end, String arrowtype){
		this.start = start;
		this.end = end;
		this.arrowtype = arrowtype;
	}
	
	public Xref getStart(){
		return start;
	}
	
	public Xref getEnd(){
		return end;
	}
	
	public String getArrowtype(){
		return arrowtype;
	}
	
	public Set<Xref> getNodes(){
		Set<Xref> nodes = new HashSet<Xref>();
		nodes.add(start);
		nodes.add(end);
		return nodes;
	}
}
