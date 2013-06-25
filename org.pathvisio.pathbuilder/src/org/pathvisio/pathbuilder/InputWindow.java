package org.pathvisio.pathbuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.bridgedb.IDMapperStack;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.desktop.util.BrowseButtonActionListener;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.pathbuilder.PbPlugin.PbPreference;
import org.pathvisio.pathbuilder.construct.Constructor;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class InputWindow extends JPanel{
	private JButton btnBrowseFile;
	private JTextField txtFile;
	private JButton btnBuild;
	
	private SwingEngine swingEngine;
	
	
	
	InputWindow(SwingEngine swingEngine){
		this.swingEngine = swingEngine;
		
		txtFile = new JTextField();
		txtFile.setText(PreferenceManager.getCurrent().get(PbPreference.PB_PLUGIN_TXT_FILE));
		txtFile.setEnabled(false);
		
		FormLayout layout = new FormLayout(
				"4dlu, pref, 4dlu, fill:pref:grow, 4dlu, pref, 4dlu",
				"4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu");
		
		CellConstraints cc = new CellConstraints();
		Border etch = BorderFactory.createEtchedBorder();
		JPanel panel = new JPanel();
		
				
		panel.setBorder (BorderFactory.createTitledBorder (etch, "Read Text File"));
				
		panel.setLayout(layout);
				
		panel.add(new JLabel ("text file:"), cc.xy(2,2));
		panel.add(txtFile, cc.xy(4,2));
				
		btnBrowseFile = new JButton("Browse");
		btnBrowseFile.addActionListener(new BrowseButtonActionListener (txtFile, this, JFileChooser.FILES_ONLY));
		panel.add(btnBrowseFile, cc.xy(6,2));
				
		Box readBox = Box.createHorizontalBox();
				
		btnBuild = new JButton("Read Text file");
		readBox.add(btnBuild);
		btnBuild.addActionListener(new ActionListener()
		{
			public void actionPerformed (ActionEvent ae)
			{
				doBuild();
			}
		});
				
		panel.add(readBox, cc.xy(4,4, "center, top"));
		add(panel, BorderLayout.CENTER);
	}
	
	private void doBuild() 
	{	
		final File textF = new File(txtFile.getText());
		//check if the file exists then read in file
		if (!textF.exists())
		{
			showMessageDialog("Text File not found");
			return;
		}
		PreferenceManager.getCurrent().set(PbPreference.PB_PLUGIN_TXT_FILE, txtFile.getText());
		TxtReader read = new TxtReader();
		read.readFile(textF);
		List<Connection> cons = read.getConnections();
		
		IDMapperStack db = swingEngine.getGdbManager().getCurrentGdb();
		Pathway pwy = swingEngine.getEngine().getActivePathway();
		new Constructor(pwy, cons, db);
	}
	
	public void showMessageDialog(String message) 
	{
		JOptionPane.showMessageDialog(this, message, message, JOptionPane.ERROR_MESSAGE);
	}
}
