package org.pathvisio.pathbuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.preferences.Preference;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.pathbuilder.layout.ISOM;

public class PbPlugin implements Plugin, BundleActivator {

	private static String PLUGIN_NAME = "PathBuilder";
	private static String BUILD = "Build new Pathway";
	private static String ADD = "Add to Pathway";
	private static String LAYOUT = "Layout All";
	private static String LAYSEL = "Layout Selection";
	
	protected List<LineType> lineTypes;
	private selectAction build;
	private selectAction layout;
	private selectAction add;
	private JFrame frame;
	private PvDesktop desktop;
	private JMenu subMenu;
	private selectAction laysel;
	private static BundleContext context;

	public static enum Action
	{
		BUILD,
		ADD,
		LAYOUT,
		LAYSEL;
		
	}
	
	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		build = new selectAction(Action.BUILD);
		layout = new selectAction(Action.LAYOUT);
		add = new selectAction(Action.ADD);
		laysel = new selectAction(Action.LAYSEL);
		subMenu = new JMenu();
		subMenu.setText(PLUGIN_NAME);
		subMenu.add(build);
		subMenu.add(add);
		subMenu.add(layout);
		subMenu.add(laysel);
		
	
		desktop.registerSubMenu("Plugins", subMenu);
	}

	
	private class selectAction extends AbstractAction {

		private Action action;
		selectAction(Action action)
		{
			this.action = action;
			switch(action)
			{
			case BUILD:
				putValue(NAME,BUILD);
				break;
			case LAYOUT:
				putValue(NAME,LAYOUT);
				break;
			case ADD:
				putValue(NAME,ADD);
				break;
			case LAYSEL:
				putValue(NAME,LAYSEL);
				break;
			}
		}
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			switch(action)
			{
			case BUILD:
				frame = new JFrame("Build pathway");
			
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.getContentPane().add(new InputWindow(desktop,frame,true), BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(desktop.getFrame());
				frame.setVisible(true);
				break;
			case LAYOUT:
				new ISOM(desktop.getSwingEngine(),false);
				break;
			case ADD:
				frame = new JFrame("Add to existing pathway");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.getContentPane().add(new InputWindow(desktop,frame,false), BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(desktop.getFrame());
				frame.setVisible(true);
				break;
			case LAYSEL:
				new ISOM(desktop.getSwingEngine(),true);
				break;
			}
		}
	}
	
	public static enum PbPreference implements Preference
	{
		PB_PLUGIN_TXT_FILE(System.getProperty("user.home") + File.separator + "text_file.txt");

		private final String defaultVal;
		
		PbPreference (String _defaultVal) { defaultVal = _defaultVal; }
		
		@Override
		public String getDefault() { return defaultVal; }
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