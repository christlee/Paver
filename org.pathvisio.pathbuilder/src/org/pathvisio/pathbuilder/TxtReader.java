package org.pathvisio.pathbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
* TODO: description
* @author christ leemans
*
*/
public class TxtReader 
{
	List<Connection> cons;
	private static String NAME_SEP = ":";
	private static String ITEM_SEP = "\t";
	
	public TxtReader()
	{
		cons = new ArrayList<Connection>();
	}
	
	public boolean readFile(File file)
	{
		cons.clear();
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
			   	currentRow=line.split(ITEM_SEP);
			   	String[] start = currentRow[0].split(NAME_SEP);
			   	Xref startRef = new Xref(start[1], DataSource.getBySystemCode(start[0]));
			   	String arrow = "normal";
			   	
			   	String[] stop = currentRow[2].split(NAME_SEP);
			   	Xref stopRef = new Xref(stop[1], DataSource.getBySystemCode(stop[0]));
			   	
			   	Connection con = new Connection(startRef, stopRef, arrow);
			   	cons.add(con);
			   	   	
			}   
			fr.close();
		}		     
		catch(Exception e) 
		{
			System.out.println("Exception: " + e);
		}
		return true;
	}
	
	public List<Connection> getConnections()
	{
		return cons;
	}		
}

