package org.pathvisio.pathbuilder.construct;

/**
 * 
 * 
 * @author christ leemans
 *
 */

public class Node implements Comparable<Node>{
	private final String sysCode;
	private final String id;
	private final String name;
	private final String rep;
	
	public Node(String name, String id, String sysCode){
		this.name = name;
		this.id = id;
		this.sysCode = sysCode;
		rep = name + id + sysCode;
	}
	public String getId(){
		return id;
	}
	public String getSysCode(){
		return sysCode;
	}
	public String getName(){
		return name;
	}
	public int hashCode() 
	{
		return rep.hashCode();
	}
	@Override
	public boolean equals(Object o){
		if (o instanceof Node){
			Node n = (Node) o;
			return rep.equals(n.rep);
		}
		else{
		return false;
		}
	}
	@Override
	public int compareTo(Node n) {
		return rep.compareTo(n.rep);
	}
}
