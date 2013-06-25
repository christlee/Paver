package org.pathvisio.pathbuilder.construct;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	
	
	public Constructor(Pathway pwy, List<Connection> connections, IDMapperStack db){
		this.db = db;
		this.pwy = pwy;
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
		plot();
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
		for (Entry<Xref,PathwayElement> entry : pels.entrySet()){
			pwy.add(entry.getValue());
		}
		for (PathwayElement line : lines){
			pwy.add(line);
		}
	}
	
	PathwayElement createNode(Xref ref) throws IDMapperException{
		PathwayElement pel;
		pel = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		pel.setGraphId(pwy.getUniqueGroupId());
		if (ref.getDataSource().isMetabolite()){
			pel.setDataNodeType(DataNodeType.METABOLITE);
			pel.setColor(Color.BLUE);
		}
		else {
			pel.setDataNodeType(DataNodeType.GENEPRODUCT);
		}
		pel.setElementID(ref.getId());
		pel.setDataSource(ref.getDataSource());
		String label = ref.getDataSource().getSystemCode() + ":" + ref.getId();
		pel.setTextLabel(label);
		return pel;
	}
}
