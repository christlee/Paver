package org.pathvisio.pathbuilder.connect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.bridgedb.IDMapperException;
import org.pathvisio.core.data.GdbManager;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.pathbuilder.construct.Constructor;
import org.pathvisio.plugins.HmdbPppPlugin;
import org.pathvisio.plugins.PathwayCommonsPppPlugin;
import org.pathvisio.plugins.PppPlugin;
import org.pathvisio.plugins.Suggestion.SuggestionException;

import org.pathvisio.plugins.Suggestion;

public class PPPConnect {

	GdbManager db;
	Pathway pwy;
	Constructor c;

	PPPConnect(Pathway pwy, GdbManager db){
		this.db = db;
		this.pwy = pwy;
		c = new Constructor(pwy,db);
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
			
			for (PathwayElement match : matches.getDataObjects()){
				if (match.getObjectType().equals(ObjectType.DATANODE)){
					try {
						for (PathwayElement element : pels){
							if (db.getCurrentGdb().getAttributes(match.getXref()).equals(db.getCurrentGdb().getAttributes(element.getXref()))){
								
								PathwayElement l = c.drawLine(node,element,LineType.ARROW);
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
