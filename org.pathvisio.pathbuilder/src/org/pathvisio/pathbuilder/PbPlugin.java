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
import org.pathvisio.pathbuilder.connect.ConnectorManager;
import org.pathvisio.pathlayout.LayoutManager.Layout;

public class PbPlugin implements Plugin, BundleActivator {

	private static String PLUGIN_NAME = "PathBuilder";
	private Layout layout;
	
	protected List<LineType> lineTypes;
	private JFrame frame;
	private PvDesktop desktop;
	private JMenu subMenu;
	private static BundleContext context;

	public static enum Action
	{
		BUILD("Build new Pathway"),
		ADD("Add to Pathway"),
		CONNECT("Connect Nodes"),
		LAYOUT("Layout All"),
		LAYSEL("Layout Selection");
		
		private Action(final String text) {
	        this.text = text;
	    }

	    private final String text;

	    @Override
	    public String toString() {
	        return text;
	    }
	}
	
	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		
		subMenu = new JMenu();
		subMenu.setText(PLUGIN_NAME);
		for (Action a: Action.values()){
			subMenu.add(new selectAction(a));
		}
		
		layout = Layout.PREFUSE;
	
		desktop.registerSubMenu("Plugins", subMenu);
	}

	
	private class selectAction extends AbstractAction {

		private Action action;
		selectAction(Action action)
		{
			putValue(NAME,action.text);
			this.action = action;
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
				layout.useSelection(false);
				layout.doLayout(desktop.getSwingEngine());
				break;
			case CONNECT:
				frame = new JFrame("Select Source");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.getContentPane().add(new ConnectorManager(desktop.getSwingEngine(),frame), BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(desktop.getFrame());
				frame.setVisible(true);
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
				layout.useSelection(true);
				layout.doLayout(desktop.getSwingEngine());
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