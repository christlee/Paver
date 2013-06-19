package org.pathvisio.pathbuilder;


import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;

import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.gui.SwingEngine;

import org.pathvisio.pathbuilder.construct.Constructor;

public class Activator implements Plugin, BundleActivator {

	private static String PLUGIN_NAME = "PathBuilder";
	
	private selectAction action;
	private PvDesktop desktop;
	private SwingEngine swingEngine;
	private JMenu subMenu;
	private static BundleContext context;

	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		swingEngine = desktop.getSwingEngine();
		action = new selectAction();
		subMenu = new JMenu();
		subMenu.setText(PLUGIN_NAME);
		subMenu.add(action);
		desktop.registerSubMenu("Plugins", subMenu);
		
	}
	
	private class selectAction extends AbstractAction {

		selectAction()
		{
			putValue(NAME,"Build pathway");
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			Set<Xref> refs = new HashSet<Xref>();
			
			Xref refa = new Xref("183", DataSource.getBySystemCode("L"));
			Xref refb = new Xref("HMDB01035", DataSource.getBySystemCode("Ch"));
			refs.add(refa);
			refs.add(refb);
			IDMapperStack db = swingEngine.getGdbManager().getCurrentGdb();
			Pathway pwy = swingEngine.getEngine().getActivePathway();
			Constructor construct = new Constructor(pwy, refs, db);
		}
		
	}

	
	static BundleContext getContext() {
		return context;
	}
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception 
	{
		context.registerService(Plugin.class.getName(), this, null);	
	}

	@Override
	public void stop(BundleContext context) throws Exception 
	{
		done();
	}
	@Override
	public void done() {
		desktop.unregisterSubMenu("Plugins", subMenu);
		
	}
}