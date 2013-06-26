package org.pathvisio.pathbuilder;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

public class Node {
	String sysCode;
	String id;
	String name;
	
	Node(){
		sysCode = "";
		id = "";
		name = "";
	}
	public void setName(String name){
		this.name = name;
	}
	public void setId(String id){
		this.id = id;
	}
	public void setSysCode(String sysCode){
		this.sysCode = sysCode;
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
}
