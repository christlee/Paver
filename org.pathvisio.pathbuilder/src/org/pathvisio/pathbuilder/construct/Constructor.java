package org.pathvisio.pathbuilder.construct;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.pathvisio.core.data.GdbManager;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.view.MIMShapes;
import org.pathvisio.plugins.PathwayCommonsPppPlugin;
import org.pathvisio.plugins.Suggestion.SuggestionException;

//TODO: This package should take care of putting the nodes and edges on the screen in a structurized manner
public class Constructor {
	Map<String,Node> nodes;
	GdbManager db;
	Pathway pwy;
	Map<Node,PathwayElement> pels;
	Set<PathwayElement> lines;
	Point start;
	boolean newpwy;
	
	private static int HEIGHT = 20;
	private static int WEIGHT = 80;
	private static int PLUSX = 120;
	private static int PLUSY = 50;
	private static int CSIZE = 12;
	
	public Constructor(Pathway pwy, GdbManager db){
		this.db = db;
		this.pwy = pwy;
		start = new Point(50,50);
	}
	
	public Constructor(Pathway pwy, GdbManager db,boolean newpwy){
		this(pwy,db);
		this.newpwy = newpwy;
	}
	
	public void plotConnections(List<Connection> connections){
		nodes = new HashMap<String,Node>();
		pels = new HashMap<Node,PathwayElement>();
		lines = new HashSet<PathwayElement>();
		float j = 1;
		int index = 0;
		float x = (float)connections.size()/(float)CSIZE;
		int cols = (int)Math.ceil(x);
		
		if (newpwy){
			j = j - .5f * cols;
		
			if (j<-2.9f){
				j = -2.9f;
			}
		}
		
		//first complete columns of 12
		for (int k = 1;k<cols;k++){
			List<Connection> col = new ArrayList<Connection>();
			for (int l = 0; l<CSIZE;l++){
				col.add(connections.get(index));
				index++;
			}
			double startj = j-0.5;
			double endj = j+0.5;
			plotConColumn(col,startj,endj);
			j = j+2;
		}
		//then the last column, this one might be incomplete
		List<Connection> lastCol = new ArrayList<Connection>();
		int m = cols-1;
		for (int n = m*CSIZE ;n<connections.size();n++){
			lastCol.add(connections.get(index));
			index++;
		}
		double startj = j-0.5;
		double endj = j+0.5;
		plotConColumn(lastCol,startj,endj);
		
		for (Entry<Node,PathwayElement> entry : pels.entrySet()){
			pwy.add(entry.getValue());
		}
		for (PathwayElement line : lines){
			pwy.add(line);
		}
	}
	
	public void plotConColumn(List<Connection> connections,double startj, double endj){
		int i = 0;
		for (Connection connection : connections){
			Node start = connection.getStart();
			if (!nodes.containsValue(start)){
				String sp = i + ":" + startj;
				nodes.put(sp, start);
				try {
					PathwayElement pel = createNode(start);
					drawNode(i, startj, pel);
					pels.put(start,pel);
				} catch (IDMapperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Node end = connection.getEnd();
			if (!nodes.containsValue(end)){
				String sp = i + ":" + endj;
				nodes.put(sp, end);
				try {
					PathwayElement pel = createNode(end);
					drawNode(i, endj, pel);
					pels.put(end,pel);
				} catch (IDMapperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			PathwayElement line = drawLine(pels.get(start),pels.get(end),connection.getLineType());
			
			lines.add(line);
			i++;
		}
	}
	
	public void plotNodes(List<Node> nodes){
		float j = 0;
		int index = 0;
		float x = (float)nodes.size()/(float)CSIZE;
		
		int cols = (int)Math.ceil(x);
		if (newpwy){
			j = j - cols/2;
			if (j<-3){
				j = -3;
			}
		}
		
		//first complete columns of 12
		for (int k = 1;k<cols;k++){
			List<Node> col = new ArrayList<Node>();
			for (int l = 0; l<CSIZE;l++){
				col.add(nodes.get(index));
				index++;
			}
			plotNodeColumn(col,j);
			j++;
		}
		//then the last column
		List<Node> lastCol = new ArrayList<Node>();
		int m = cols-1;
		for (int n = m*CSIZE ;n<nodes.size();n++){
			lastCol.add(nodes.get(index));
			index++;
		}
		plotNodeColumn(lastCol,j);
		
	}
	
	public void plotNodeColumn(List<Node> nodes,float j){
		int i = 0;
		for (Node node: nodes){
			try {
				PathwayElement pel = createNode(node);
				pel.setTextLabel(node.getName());
				drawNode(i,j,pel);
				pwy.add(pel);
				i++;
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	void drawNode(int i, double j, PathwayElement pel){
		int x = (int)(start.x + PLUSX * j);
		
		int y = start.y + PLUSY * i;
		pel.setMCenterX(x);
		pel.setMCenterY(y);
		pel.setMHeight(HEIGHT);
		pel.setMWidth(WEIGHT);
	}
	
	public PathwayElement drawLine(PathwayElement startnode, PathwayElement endnode, LineType ltype){
		PathwayElement line = PathwayElement.createPathwayElement(ObjectType.LINE);
		line.setGraphId(pwy.getUniqueGraphId());
		
		if (startnode.getMCenterX() < endnode.getMCenterX()) {
			line.setMStartY(startnode.getMCenterY());
			line.setMStartX(startnode.getMCenterX() + startnode.getMWidth() / 2);
			line.setMEndY(endnode.getMCenterY());
			line.setMEndX(endnode.getMCenterX() - endnode.getMWidth() / 2);
		} else if (startnode.getMCenterX() > endnode.getMCenterX()) {
			line.setMStartY(startnode.getMCenterY());
			line.setMStartX(startnode.getMCenterX() - startnode.getMWidth() / 2);
			line.setMEndY(endnode.getMCenterY());
			line.setMEndX(endnode.getMCenterX() + endnode.getMWidth() / 2);
		} else if (startnode.getMCenterY() > endnode.getMCenterY()){
			line.setMStartX(startnode.getMCenterX());
			line.setMStartY((startnode.getMCenterY() - startnode.getMHeight() /2));
			line.setMEndX(startnode.getMCenterX());
			line.setMEndY(endnode.getMCenterY() + startnode.getMHeight() /2);
		} else {
			line.setMStartX(startnode.getMCenterX());
			line.setMStartY(startnode.getMCenterY() + startnode.getMHeight() / 2);
			line.setMEndX(endnode.getMCenterX());
			line.setMEndY(endnode.getMCenterY() - endnode.getMHeight() / 2);
		}
		line.getMStart().linkTo(startnode);
		line.getMEnd().linkTo(endnode);
		
		MIMShapes.registerShapes();
		if (isBothWays(ltype)){
			line.setStartLineType(ltype);
		}
		else{
			line.setStartLineType(LineType.LINE);
		}
		
		line.setEndLineType(ltype);
		return line;
	}
	
	
	public void setStartX(int x){
		start.setLocation(x,start.y);
	}
	
	PathwayElement createNode(Node node) throws IDMapperException{
		PathwayElement pel;
		pel = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		pel.setGraphId(pwy.getUniqueGroupId());
		pel.setTextLabel(node.getName());
		if (!node.getId().equals("NULL")){
			pel.setElementID(node.getId());
		}
		if (!node.getSysCode().equals("NULL")){
			DataSource source = DataSource.getBySystemCode(node.getSysCode());
//			if (source.getType().equals(GENE)){
//				pel.setDataNodeType("GeneProduct");
//			}
//			else if (source.getType().equals(METABOLITE)){
//				pel.setDataNodeType("Metabolite");
//			}
//			pel.setDataNodeType(source.getType());
			
			if (source.isMetabolite()){
				pel.setColor(Color.BLUE);
				pel.setDataNodeType(DataNodeType.METABOLITE);
			}
			else{
				pel.setDataNodeType(DataNodeType.GENEPRODUCT);
			}
			pel.setDataSource(source);
		}
		
		return pel;
	}
	public static boolean isBothWays(LineType lt){
		if (lt.equals(MIMShapes.MIM_BINDING)||
			lt.equals(MIMShapes.MIM_COVALENT_BOND)||
			lt.equals(MIMShapes.MIM_GAP)){
			return true;
		}
		else return false;
	}

	public void findCons() throws SuggestionException {
		PathwayCommonsPppPlugin pwcPpp = new PathwayCommonsPppPlugin(db, "ALL");
		List<PathwayElement> pels = new ArrayList<PathwayElement>();
		for (PathwayElement pel : pwy.getDataObjects()){
			if (pel.getObjectType().equals(ObjectType.DATANODE)){
				pels.add(pel);
			}
		}
		
		
		for (PathwayElement node : pels){
			Pathway matches = pwcPpp.doSuggestion(node);
			File tmp;
			try {
				tmp = File.createTempFile("pwcppp", ".gpml");
				matches.writeToXml(tmp, true);

				BufferedReader br = new BufferedReader(new FileReader(tmp));
				String line;
				while ((line = br.readLine()) != null)
				{
					System.out.println (line);
				

				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ConverterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (PathwayElement match : matches.getDataObjects()){
				System.out.println(match.getTextLabel());
				
				if (match.getObjectType().equals(ObjectType.DATANODE)){
					try {
						for (PathwayElement element : pels){
							if (db.getCurrentGdb().getAttributes(match.getXref()).equals(db.getCurrentGdb().getAttributes(element.getXref()))){
								
								PathwayElement l = drawLine(node,element,LineType.ARROW);
								pwy.add(l);
							}
						}
						
					} catch (IDMapperException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	}
}
