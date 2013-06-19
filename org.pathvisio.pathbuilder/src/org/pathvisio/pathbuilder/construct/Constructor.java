package org.pathvisio.pathbuilder.construct;

import java.awt.Color;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;

//TODO: This package should take care of putting the nodes and edges on the screen in a structurized manner
public class Constructor {
	
	IDMapperStack db;
	Pathway pwy;
	
	
	public Constructor(Pathway pwy, Set<Xref> refs, IDMapperStack db){
		int x = 50;
		int y = 60;
		this.db = db;
		this.pwy = pwy;
		for (Xref ref : refs){
			try {
				PathwayElement pel = createNode(ref);
				draw(pel,x,y);
				x = x + 50;
				pwy.add(pel);
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} 
	}
	PathwayElement createNode(Xref ref) throws IDMapperException{
		PathwayElement pel;
		Map<String, Set<String>> attributes;
		attributes = db.getAttributes(ref);
		pel = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		if (ref.getDataSource().isMetabolite()){
			pel.setDataNodeType(DataNodeType.METABOLITE);
			pel.setColor(Color.BLUE);
		}
		else {
			pel.setDataNodeType(DataNodeType.GENEPRODUCT);
		}
		pel.setElementID(ref.getId());
		pel.setDataSource(ref.getDataSource());
		pel.setTextLabel(attributes.get("Symbol").iterator().next());
		return pel;
	}
	
	void draw(PathwayElement pel, int y, int x){
		pel.setMCenterX(x);
		pel.setMCenterY(y);
		pel.setMHeight(20);
		pel.setMWidth(80);
	}

}
