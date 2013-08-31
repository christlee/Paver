package org.pathvisio.pathbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.pathvisio.core.model.LineType;
import org.pathvisio.pathbuilder.construct.Connection;
import org.pathvisio.pathbuilder.construct.Node;

/**
* TODO: description
* @author christ leemans
*
*/
public class TxtReader 
{
	File file;
	InputWindow window;
	private static String NAME_SEP = ":";
	private static String ITEM_SEP = "\t";
	
	public TxtReader(File file)
	{
		this.file = file;
		window = new InputWindow();
	}
	
	public void addWindow(InputWindow window){
		this.window = window;
	}
	
	public List<Connection> getConnections()
	{
		List<Connection> cons = new ArrayList<Connection>();
		// line string
		String line; 
		
					
		try 
		{
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
					
			// continue reading lines until EOF is reached
			while((line = br.readLine()) != null)
			{
			   	Connection con = TxtReader.readConnection(line, window);
			   	cons.add(con);
			   	   	
			}   
			fr.close();
		}		     
		catch(Exception e) 
		{
			System.out.println("Exception: " + e);
		}		
	return cons;
	}
	public List<Node> getNodes(){
		List<Node> nodes = new ArrayList<Node>();
		// line string
		String line; 
							
		try 
		{
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
							
			// continue reading lines until EOF is reached
			while((line = br.readLine()) != null)
			{
				Node node = readSingleNode(line,window);
				
				nodes.add(node);
					   	   	
			}   
			fr.close();
		}		     
		catch(Exception e) 
		{
			System.out.println("Exception: " + e);
		}
		
		return nodes;
	}
	
	public static Connection readConnection(String line, InputWindow window){
		// the array to return
		String[] currentRow;
		List<LineType> lineTypes = window.getLineTypes();

		currentRow=line.split(ITEM_SEP);
		if (currentRow.length!=3){
			window.showMessageDialog("Incorrect input!");
			return null;
		}
		String[] start = currentRow[0].split(NAME_SEP);
		String startname = start[0] + ":" + start[1];
		Node startRef = new Node(startname,start[1], start[0]);
	   	String ltstring = currentRow[1];
	   	LineType ltype = LineType.ARROW;
	   	for (LineType lt: lineTypes){
	   		if (lt.getName().equals(ltstring)){
	   			ltype = lt;
	   		}
	   	}
	   	String[] stop = currentRow[2].split(NAME_SEP);
	   	String stopname = stop[0] + ":" + stop[1];
	   	Node stopRef = new Node(stopname, stop[1], stop[0]);
	   	Connection con = new Connection(startRef, stopRef, ltype);
		
		return con;
	}
	
	public static Node readSingleNode(String line,InputWindow window){
		String[] att = line.split("\t");
		if (att.length==3){
			//it could be a system code
			String sysCode = att[2];
			DataSource s = DataSource.getByFullName(att[2]);
			try {
				//but first try if it's a full name
				//NullPointer if it's indeed empty
				s.getSystemCode().isEmpty();
				sysCode = s.getSystemCode();
			}
			catch (NullPointerException e){
			}
			return new Node(att[0],att[1],sysCode);
		}
		else if (att.length==2){
			String name = att[0];
			String sysCode;
			String id = att[1];
			if (!DataSourcePatterns.getDataSourceMatches(att[1]).isEmpty()){
				int l = DataSourcePatterns.getDataSourceMatches(id).toArray().length;
				DataSource[] sources = new DataSource[l];
				DataSourcePatterns.getDataSourceMatches(id).toArray(sources);
				sysCode = sources[0].getSystemCode();
				
			}
			else {
				sysCode = "NULL";
			}
			return new Node(name,id,sysCode);
		}
		else if (att.length==1){
			String name = att[0];
			String id;
			String sysCode;
			//if the name is a identifier, use this identifier
			if (!DataSourcePatterns.getDataSourceMatches(name).isEmpty()){
				id = name;
				int l = DataSourcePatterns.getDataSourceMatches(id).toArray().length;
				DataSource[] sources = new DataSource[l];
				DataSourcePatterns.getDataSourceMatches(id).toArray(sources);
				sysCode=sources[0].getSystemCode();
			}
			//else put in "NULL"
			else {
				id = "NULL";
				sysCode = "NULL";
			}
			return new Node(name,id,sysCode);
		}
		else {
			window.showMessageDialog("Incorrect input!");
			return null;
		}
	}
}

