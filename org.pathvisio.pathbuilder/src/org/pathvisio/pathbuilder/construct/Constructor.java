package org.pathvisio.pathbuilder.construct;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.view.MIMShapes;
import org.pathvisio.pathbuilder.Connection;
import org.pathvisio.pathbuilder.Node;

//TODO: This package should take care of putting the nodes and edges on the screen in a structurized manner
public class Constructor {
	Map<String,Xref> nodes;
	IDMapperStack db;
	Pathway pwy;
	Map<Xref,PathwayElement> pels;
	Set<PathwayElement> lines;
	private static int HEIGHT = 20;
	private static int WEIGHT = 80;
	private static int X = 50;
	private static int Y = 50;
	private static int PLUSX = 120;
	private static int PLUSY = 50;
	
	
	public Constructor(Pathway pwy, IDMapperStack db){
		this.db = db;
		this.pwy = pwy;
		
	}
	
	public void plotConnections(List<Connection> connections){
		nodes = new HashMap<String,Xref>();
		int i = 0;
		int j = 0;
		pels = new HashMap<Xref,PathwayElement>();
		lines = new HashSet<PathwayElement>();
		for (Connection connection : connections){
			
			Xref start = connection.getStart();
			if (!nodes.containsValue(start)){
				j = 0;
				String sp = i + ":" + j;
				nodes.put(sp, start);
				try {
					PathwayElement pel = createNode(start);
					String label = start.getDataSource().getSystemCode() + ":" + start.getId();
					pel.setTextLabel(label);
					drawNode(i, 0, pel);
					pels.put(start,pel);
				} catch (IDMapperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Xref end = connection.getEnd();
			if (!nodes.containsValue(end)){
				j = 1;
				String sp = i + ":" + j;
				nodes.put(sp, end);
				try {
					PathwayElement pel = createNode(end);
					drawNode(i, j, pel);
					String label = end.getDataSource().getSystemCode() + ":" + end.getId();
					pel.setTextLabel(label);
					pels.put(end,pel);
				} catch (IDMapperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			PathwayElement line = drawLine(pels.get(start),pels.get(end),"start","end");
			
			lines.add(line);
			i++;
		}
		for (Entry<Xref,PathwayElement> entry : pels.entrySet()){
			pwy.add(entry.getValue());
		}
		for (PathwayElement line : lines){
			pwy.add(line);
		}
	}
	
	public void plotNodes(List<Node> nodes){
		int i = 0;
		for (Node node: nodes){
			try {
				PathwayElement pel = createNode(new Xref(node.getId(),DataSource.getBySystemCode(node.getSysCode())));
				pel.setTextLabel(node.getName());
				drawNode(i,0,pel);
				pwy.add(pel);
				i++;
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	void drawNode(int i, int j, PathwayElement pel){
		int x = X + PLUSX * j;
		int y = Y + PLUSY * i;
		pel.setMCenterX(x);
		pel.setMCenterY(y);
		pel.setMHeight(HEIGHT);
		pel.setMWidth(WEIGHT);
	}
	
	PathwayElement drawLine(PathwayElement startnode, PathwayElement endnode, String starttype, String endtype){
		PathwayElement line = PathwayElement.createPathwayElement(ObjectType.LINE);
		line.setGraphId(pwy.getUniqueGraphId());
		
		if (startnode.getMCenterX() < endnode.getMCenterX()) {
			line.setMStartY(startnode.getMCenterY());
			line.setMStartX(startnode.getMCenterX() + startnode.getMWidth() / 2);
			line.setMEndY(endnode.getMCenterY());
			line.setMEndX(endnode.getMCenterX() - endnode.getMWidth() / 2);
		} else if (startnode.getMCenterX() > endnode.getMCenterX()) {
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
		
		line.setStartLineType(LineType.LINE);
		line.setEndLineType(LineType.ARROW);
		return line;
	}
	
	void plot(){
		
	}
	
	PathwayElement createNode(Xref ref) throws IDMapperException{
		PathwayElement pel;
		pel = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		pel.setGraphId(pwy.getUniqueGroupId());
		pel.setDataNodeType(ref.getDataSource().getType());
		
		if (ref.getDataSource().isMetabolite()){
			pel.setColor(Color.BLUE);
		}
		pel.setElementID(ref.getId());
		pel.setDataSource(ref.getDataSource());
		return pel;
	}
}
