package org.pathvisio.pathbuilder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;

import org.pathvisio.core.model.Property;
import org.pathvisio.core.model.PropertyManager;
import org.pathvisio.core.preferences.GlobalPreference;
import org.pathvisio.core.preferences.Preference;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.gui.handler.PropertyView;

public class PbPlugin implements Plugin, BundleActivator {

	private static String PLUGIN_NAME = "PathBuilder";
	
	private selectAction action;
	private PvDesktop desktop;
	private JMenu subMenu;
	private static BundleContext context;

	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		action = new selectAction();
		subMenu = new JMenu();
		subMenu.setText(PLUGIN_NAME);
		subMenu.add(action);
		desktop.registerSubMenu("Plugins", subMenu);
		System.out.println(desktop.getSideBarTabbedPane().getWidth());
	}
	
	private class selectAction extends AbstractAction {

		selectAction()
		{
			putValue(NAME,"Build pathway");
		}
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			JFrame frame = new JFrame("Build pathway");
			
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.getContentPane().add(new InputWindow(desktop), BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(desktop.getFrame());
			frame.setVisible(true);
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