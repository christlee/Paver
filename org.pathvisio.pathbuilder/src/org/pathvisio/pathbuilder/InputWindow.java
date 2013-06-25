package org.pathvisio.pathbuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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

public class InputWindow extends JPanel implements ActionListener{
	
	private JButton btnBrowseFile;
	private JTextField txtFile;
	private JButton btnBuild;
	private JTextArea inputText;
	private JRadioButton byFileButton;
	private JRadioButton byTableButton;
	private JRadioButton conButton;
	private JRadioButton nodesButton;
	private SwingEngine swingEngine;
	private JLabel nodeNames;
	private JLabel connectionNames;
	
	static String byFileString = "By File";
    static String byTableString = "By Table";
    static String conString = "Connections";
    static String nodesString = "Nodes";
	
	InputWindow(SwingEngine swingEngine){
		this.swingEngine = swingEngine;
		
		txtFile = new JTextField();
		txtFile.setText(PreferenceManager.getCurrent().get(PbPreference.PB_PLUGIN_TXT_FILE));
		txtFile.setEnabled(false);
		
		FormLayout settingsLayout = new FormLayout(
				"4dlu, pref, pref, fill:pref:grow, pref, pref, 4dlu",
				"4dlu, fill:pref:grow, 4dlu");
		
		FormLayout browseLayout = new FormLayout(
				"4dlu, pref, 4dlu, fill:pref:grow, 4dlu, pref, 4dlu",
				"4dlu, pref, 4dlu, pref, 4dlu");
		
		FormLayout inputLayout = new FormLayout(
				"4dlu, fill:200dlu:grow, 4dlu, pref, 4dlu",
				"4dlu, pref, 4dlu, fill:pref:grow, 4dlu");
		
		FormLayout panelLayout = new FormLayout(
				"4dlu, fill:pref:grow, 4dlu",
				"4dlu, pref, 4dlu, pref, 4dlu, fill:pref:grow, 4dlu");
		
		CellConstraints cc = new CellConstraints();
		Border etch = BorderFactory.createEtchedBorder();
		
		JPanel settingsPanel = new JPanel();
		JPanel browsePanel = new JPanel();
		JPanel inputPanel = new JPanel();
		JPanel panel = new JPanel();
		
		//settings Panel
		settingsPanel.setBorder(BorderFactory.createTitledBorder(etch, "Settings"));
		settingsPanel.setLayout(settingsLayout);
		
		settingsPanel.add(new JLabel("<html> Input : <br> Source </html>"), cc.xy(2, 2));
		settingsPanel.add(new JLabel("<html> Input : <br> Type </html"), cc.xy(5, 2));
		
		
		
		byFileButton = new JRadioButton(byFileString);
//        byFileButton.setMnemonic(KeyEvent.VK_C);
        byFileButton.setActionCommand(byFileString);
        byFileButton.addActionListener(this);
        
        byTableButton = new JRadioButton(byTableString);
//        byTableButton.setMnemonic(KeyEvent.VK_C);
        byTableButton.setActionCommand(byTableString);
        byTableButton.addActionListener(this);
        
        conButton = new JRadioButton(conString);
 //       conButton.setMnemonic(KeyEvent.VK_C);
        conButton.setActionCommand(conString);
        conButton.addActionListener(this);
        
        nodesButton = new JRadioButton(nodesString);
 //       nodesButton.setMnemonic(KeyEvent.VK_C);
        nodesButton.setActionCommand(nodesString);
        nodesButton.addActionListener(this);
		
        ButtonGroup sourceGroup = new ButtonGroup();
        sourceGroup.add(byFileButton);
        sourceGroup.add(byTableButton);

        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(conButton);
        typeGroup.add(nodesButton);
        
        
        Box sourceBox = Box.createVerticalBox();
        sourceBox.add(byFileButton);
        sourceBox.add(byTableButton);
        
        Box typeBox = Box.createVerticalBox();
        typeBox.add(conButton);
        typeBox.add(nodesButton);
        
        settingsPanel.add(sourceBox,cc.xy(3,2));
        settingsPanel.add(typeBox,cc.xy(6, 2));
        
		//browser Panel
		browsePanel.setBorder (BorderFactory.createTitledBorder (etch, "GO!"));
		browsePanel.setLayout(browseLayout);
				
		browsePanel.add(new JLabel ("text file:"), cc.xy(2,2));
		browsePanel.add(txtFile, cc.xy(4,2));
				
		btnBrowseFile = new JButton("Browse");
		btnBrowseFile.addActionListener(new BrowseButtonActionListener (txtFile, this, JFileChooser.FILES_ONLY));
		browsePanel.add(btnBrowseFile, cc.xy(6,2));
				
		btnBuild = new JButton("Read Text file");
	
		btnBuild.addActionListener(new ActionListener()
		{
			public void actionPerformed (ActionEvent ae)
			{
				doBuild();
			}
		});
		nodeNames = new JLabel("<html> Enter the genes here <br> " +
	    		"(label [tab] ID [tab] DataSource)                      </html>");
		connectionNames = new JLabel("<html> Enter connections here <br> " +
	    		"(SystemCode[:]ID [tab] LineType [tab] SystemCode[:]ID) </html>");
		inputText = new JTextArea(7,10);
	    JScrollPane scrollingArea = new JScrollPane(inputText);
	    
	    inputPanel.setBorder (BorderFactory.createTitledBorder(etch, "input table"));
	    inputPanel.setLayout(inputLayout);
	    
	    inputPanel.add(nodeNames,cc.xy(2, 2));
	    inputPanel.add(connectionNames, cc.xy(2,2));
	    inputPanel.add(scrollingArea,cc.xy(2, 4));
	    inputPanel.add(btnBuild, cc.xy(4,4, "center, bottom"));
	    
	    inputText.addMouseListener(new MouseAdapter()
	    {
			public void mousePressed(MouseEvent e)  
		    {  
				if (e.getButton() == MouseEvent.BUTTON3)  
				{  
					JPopupMenu m = processMouseEvent();
					if(m != null) {
						m.show(inputText, e.getX(), e.getY());
					}
				}  
		    }  		    
	    }
	    );
	    
        byFileButton.doClick();
        conButton.doClick();
	    
        panel.setLayout(panelLayout);
        
	    panel.add(settingsPanel, cc.xy(2, 2));
	    panel.add(browsePanel, cc.xy(2, 4));
		panel.add(inputPanel, cc.xy(2, 6));
		
		add(panel, BorderLayout.CENTER);
	}
	
	public JPopupMenu processMouseEvent()
	{
		JPopupMenu menu = new JPopupMenu();
		JMenuItem copyItem = new JMenuItem("Copy" );
		copyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent)
			{
				inputText.copy();
			}
		});
		menu.add(copyItem);
		JMenuItem pasteItem = new JMenuItem("Paste" );
		pasteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent)
			{
				inputText.paste();
			}
		});
		menu.add(pasteItem);	
		return menu;
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
	
	public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand()); 
		if(e.getActionCommand()==byTableString){
        	 inputText.setEnabled(true);
        	 btnBrowseFile.setEnabled(false);
        	 txtFile.setEnabled(false);
        	 
         } else if (e.getActionCommand()==byFileString){
        	 inputText.setEnabled(false);
        	 btnBrowseFile.setEnabled(true);
        	 txtFile.setEnabled(true);
        	 
         } else if (e.getActionCommand()==conString){
        	 nodeNames.setVisible(false);
        	 connectionNames.setVisible(true);
        	 
        	 
         } else if (e.getActionCommand()==nodesString){
        	 connectionNames.setVisible(false);
        	 nodeNames.setVisible(true);
        	 txtFile.setColumns(1);
         }
    }
	
	public void showMessageDialog(String message) 
	{
		JOptionPane.showMessageDialog(this, message, message, JOptionPane.ERROR_MESSAGE);
	}
}
