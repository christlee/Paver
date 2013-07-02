package org.pathvisio.pathbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.Xref;

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
		// the array to return
		String[] currentRow;
							
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

		currentRow=line.split(ITEM_SEP);
		if (currentRow.length!=3){
			window.showMessageDialog("Incorrect input!");
			return null;
		}
		String[] start = currentRow[0].split(NAME_SEP);
	   	Xref startRef = new Xref(start[1], DataSource.getBySystemCode(start[0]));
	   	String arrow = "normal";
		  	
	   	String[] stop = currentRow[2].split(NAME_SEP);
	   	Xref stopRef = new Xref(stop[1], DataSource.getBySystemCode(stop[0]));
	   	Connection con = new Connection(startRef, stopRef, arrow);
		
		return con;
	}
	
	public static Node readSingleNode(String line,InputWindow window){
		String[] att = line.split("\t");
		Node node = new Node();
		if (att.length==3){
			node.setName(att[0]);
			node.setId(att[1]);
			node.setSysCode(att[2]);
		}
		else if (att.length==2){
			node.setName(att[0]);
			node.setId(att[1]);
			if (!DataSourcePatterns.getDataSourceMatches(att[1]).isEmpty()){
				node.setSysCode(DataSourcePatterns.getDataSourceMatches(att[1]).toArray()[0].toString());
			}
		}
		else if (att.length==1){
			node.setName(att[0]);
			//if the name is a identifier, use this identifier
			if (!DataSourcePatterns.getDataSourceMatches(att[0]).isEmpty()){
				node.setId(att[0]);
				node.setSysCode(DataSourcePatterns.getDataSourceMatches(att[1]).toArray()[0].toString());
			}
			//else put in "NULL"
			else {
				node.setId("NULL");
				node.setSysCode("NULL");
			}
		}
		else {
			window.showMessageDialog("Incorrect input!");
			return null;
		}
		return node;
	}
}

