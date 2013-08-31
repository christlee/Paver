package org.pathvisio.pathbuilder.connect;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bridgedb.IDMapperException;
import org.pathvisio.core.data.GdbManager;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.pathbuilder.connect.PPPFactory.Ppp;
import org.pathvisio.pathbuilder.construct.Constructor;
import org.pathvisio.plugins.Suggestion;
import org.pathvisio.plugins.Suggestion.SuggestionException;


//TODO: Create methods to make the connection between the data nodes
public class ConnectorManager extends JPanel {

	private SwingEngine se;
	private JFrame frame;
	private List<JCheckBox> checks;
	private List<Suggestion> suggestions;
	private JButton go;
	private Pathway pwy;
	private GdbManager gdb;
	
	public ConnectorManager(SwingEngine se,JFrame frame){
		this.frame = frame;
		this.se = se;
		pwy = se.getEngine().getActivePathway();
		gdb = se.getGdbManager();
		checks = new ArrayList<JCheckBox>();
		suggestions = new ArrayList<Suggestion>();
		Box all = Box.createVerticalBox();
		
		for (Ppp value : Ppp.values()){
			JCheckBox cb = new JCheckBox(value.toString());
			checks.add(cb);
			
			Box b = Box.createVerticalBox();
			b.add(cb);
			all.add(b);
		}
		
		go = new JButton("GO!");
		
		go.addActionListener(new ActionListener()
		{
			public void actionPerformed (ActionEvent ae)
			{
				go();
			}
		});
		
		all.add(go);
		
		add(all, BorderLayout.CENTER);
	}
	
	private void go(){
		frame.dispose();
		for (JCheckBox cb : checks){
			if (cb.isSelected()){
				for(Ppp p :Ppp.values()) {
					if (p.toString().equals(cb.getText())){
						suggestions.add(PPPFactory.newPPP(gdb,p));
						System.out.println(p);
					}
				}
				
			}
		}
		try {
			findCons();
		} catch (SuggestionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void findCons() throws SuggestionException {
		
		
		List<PathwayElement> pels = new ArrayList<PathwayElement>();
		for (PathwayElement pel : pwy.getDataObjects()){
			if (pel.getObjectType().equals(ObjectType.DATANODE)){
				try{
					pel.getXref();
					pels.add(pel);
				}
				catch(NullPointerException e){
					System.out.println("null pointer caught!");
				}
			}
		}
		List<PathwayElement> lines = new ArrayList<PathwayElement>();
		Constructor c = new Constructor(pwy, gdb);
		
		for (PathwayElement node : pels){
			for (Suggestion s : suggestions){
				
				Pathway matches = s.doSuggestion(node);
				for (PathwayElement match : matches.getDataObjects()){
					if (match.getObjectType().equals(ObjectType.DATANODE)){
						try {
							for (PathwayElement element : pels){
								if (gdb.getCurrentGdb().getAttributes(match.getXref()).equals(gdb.getCurrentGdb().getAttributes(element.getXref()))){
									PathwayElement l;
									if (node.hashCode()>element.hashCode()){
										l = c.drawLine(node,element,LineType.ARROW);
									}
									else{
										l = c.drawLine(element, node, LineType.ARROW);
									}
									if (!lines.contains(l)){
										lines.add(l);
										pwy.add(l);
									}
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
}
