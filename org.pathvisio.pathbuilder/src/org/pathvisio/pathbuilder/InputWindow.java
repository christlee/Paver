package org.pathvisio.pathbuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.bridgedb.DataSource;
import org.pathvisio.core.data.GdbManager;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.view.MIMShapes;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.util.BrowseButtonActionListener;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.pathbuilder.PbPlugin.PbPreference;
import org.pathvisio.pathbuilder.construct.Connection;
import org.pathvisio.pathbuilder.construct.Constructor;
import org.pathvisio.pathbuilder.construct.Node;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class InputWindow extends JPanel implements ActionListener{
	
	private JButton btnBrowseFile;
	private JTextField txtFile;
	private JButton btnBuild;
	private JSuggestArea inputText;
	private JRadioButton byFileButton;
	private JRadioButton manualButton;
	private JRadioButton conButton;
	private JRadioButton nodesButton;
	private SwingEngine swingEngine;
	private JLabel nodeNames;
	private JLabel connectionNames;
	private JFrame frame;
	private int side;
	private List<LineType> lineTypes;
	private boolean newpwy;
	
	static String byFileString = "From File";
    static String manualString = "Manually";
    static String conString = "Connections";
    static String nodesString = "Nodes";
    
    /**
	 * The input window.<p>
	 * Class creates a frame with the elements to enable user input.
	 */
	InputWindow(PvDesktop desktop,JFrame frame,boolean newpwy){
		this.swingEngine = desktop.getSwingEngine();
		this.frame = frame;
		this.newpwy = newpwy;
		side=desktop.getSideBarTabbedPane().getWidth();
		findLineTypes();
		
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
        
        manualButton = new JRadioButton(manualString);
//        manualButton.setMnemonic(KeyEvent.VK_C);
        manualButton.setActionCommand(manualString);
        manualButton.addActionListener(this);
        
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
        sourceGroup.add(manualButton);

        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(conButton);
        typeGroup.add(nodesButton);
        
        
        Box sourceBox = Box.createVerticalBox();
        sourceBox.add(byFileButton);
        sourceBox.add(manualButton);
        
        Box typeBox = Box.createVerticalBox();
        typeBox.add(conButton);
        typeBox.add(nodesButton);
        
        settingsPanel.add(sourceBox,cc.xy(3,2));
        settingsPanel.add(typeBox,cc.xy(6, 2));
        
		//browser Panel
		browsePanel.setBorder (BorderFactory.createTitledBorder (etch, "input File"));
		browsePanel.setLayout(browseLayout);
				
		browsePanel.add(new JLabel ("text file:"), cc.xy(2,2));
		browsePanel.add(txtFile, cc.xy(4,2));
				
		btnBrowseFile = new JButton("Browse");
		btnBrowseFile.addActionListener(new BrowseButtonActionListener (txtFile, this, JFileChooser.FILES_ONLY));
		browsePanel.add(btnBrowseFile, cc.xy(6,2));
				
		btnBuild = new JButton("GO!");
	
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
		
		inputText = new JSuggestArea(7,10,frame);
		
		if (conButton.isSelected()){
			
		}
		
		
	    JScrollPane scrollingArea = new JScrollPane(inputText);
	    
	    inputPanel.setBorder (BorderFactory.createTitledBorder(etch, "Manual input"));
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
	
	InputWindow(){
		
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
		List<Connection> cons = new ArrayList<Connection>();
		List<Node> nodes = new ArrayList<Node>();
		if (byFileButton.isSelected()){
			final File textF = new File(txtFile.getText());
			//check if the file exists then read in file
			if (!textF.exists())
			{
				showMessageDialog("Text File not found");
				return;
			}
			PreferenceManager.getCurrent().set(PbPreference.PB_PLUGIN_TXT_FILE, txtFile.getText());
			TxtReader read = new TxtReader(textF);
			read.addWindow(this);
			if (conButton.isSelected()){
				cons = read.getConnections();	
			}
			else {
				nodes = read.getNodes();
			}
		}
		else if (conButton.isSelected()){
			cons = getCons();
		}
		else {
			nodes = getNodes();
		}
		GdbManager db = swingEngine.getGdbManager();
		int startX;
		if (newpwy){
			swingEngine.newPathway();
			startX = (int)(.5* swingEngine.getFrame().getWidth()-side);
		}
		else {
			startX = swingEngine.getEngine().getActiveVPathway().getVWidth();
		}
		Pathway pwy = swingEngine.getEngine().getActivePathway();
		Constructor construct = new Constructor(pwy, db,newpwy);
		construct.setStartX(startX);
		
		if (conButton.isSelected()){
			construct.plotConnections(cons);
		}
		else {
			construct.plotNodes(nodes);
		}
		frame.dispose();
	}
	
	private void findLineTypes(){
		lineTypes = new ArrayList<LineType>();
		lineTypes.add(LineType.ARROW);
		for (Field field: MIMShapes.class.getDeclaredFields()){
			if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
				field.getGenericType().equals(LineType.class)){
				try {
					lineTypes.add((LineType)field.get(null));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	protected List<LineType> getLineTypes(){
		return lineTypes;
	}
	
	private List<Node> getNodes(){
		List<Node> nodes = new ArrayList<Node>();
		String[] geneList = inputText.getText().split("\r\n|\r|\n");
		for (int i=0; i<geneList.length;i++){
			Node node = TxtReader.readSingleNode(geneList[i],this);
			nodes.add(node);
		}
		return nodes;
	}
	
	private List<Connection> getCons(){
		List<Connection> cons = new ArrayList<Connection>();
		String[] geneList = inputText.getText().split("\r\n|\r|\n");
		for (int i=0; i<geneList.length;i++){
			Connection con = TxtReader.readConnection(geneList[i],this);
			cons.add(con);
		}
		return cons;
	}
	
	public void actionPerformed(ActionEvent e) { 
		if(e.getActionCommand()==manualString){
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
        	inputText.clearSuggestions();
        	
        	Vector<String> linetypes = new Vector<String>();
 			for (LineType linetype: lineTypes){
 				linetypes.add(linetype.getName());
 			}
 			inputText.setSuggestSecond(linetypes, "\t");
 		
 			Vector<String> syscodes = new Vector<String>();
 			for (String name: DataSource.getFullNames()){
 				String syscode = DataSource.getByFullName(name).getSystemCode();
 				if (!syscode.isEmpty()){
 					syscodes.add(syscode);
 				}
 			}
 			inputText.setSuggestFirst(syscodes, ":");
 			inputText.setSuggestThird(syscodes, ":");
        	 
        } else if (e.getActionCommand()==nodesString){
        	connectionNames.setVisible(false);
        	nodeNames.setVisible(true);
        	txtFile.setColumns(1);
        	
        	inputText.clearSuggestions();
        	Vector<String> datasources = new Vector<String>();
        	for (String name: DataSource.getFullNames()){
        		if (!name.isEmpty()){
        			datasources.add(name);
        		}
        		String syscode = DataSource.getByFullName(name).getSystemCode();
        		if (!syscode.isEmpty()){
        			datasources.add(syscode);
        		}
        	}
        	inputText.setSuggestThird(datasources, "\n");
        }
    }
	
	public void showMessageDialog(String message) 
	{
		JOptionPane.showMessageDialog(this, message, message, JOptionPane.ERROR_MESSAGE);
	}
}
