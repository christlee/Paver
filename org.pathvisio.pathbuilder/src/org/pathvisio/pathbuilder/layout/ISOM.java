package org.pathvisio.pathbuilder.layout;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;
import org.pathvisio.gui.SwingEngine;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;

public class ISOM extends LayoutAbstract{

	ISOMLayout<String,String> l; 
	Transformer<String,Point2D> in;
	
	
	public ISOM(SwingEngine swingEngine,boolean selection){
		super.swingEngine = swingEngine;
		super.pwy = swingEngine.getEngine().getActivePathway();
		super.selection = selection;

		createDSMultigraph();
		l = new ISOMLayout<String,String>( g );
		setDimension(l);
		l.initialize();
		while(!l.done()){
			l.step();
		}
		
		drawNodes((AbstractLayout<String,String>) l);
		//remove overlap;
//		removeOverlap.remove(pwy);
		//re-draw the lines
		drawLines();
	}

}
