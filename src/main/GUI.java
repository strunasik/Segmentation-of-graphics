package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GUI {
	private File file = new File(System.getProperty("user.home") + "/Desktop");
	private int screenW = 710;
	private int screenH = 600;
	private JFrame frame;
	private KMeans kmeans;
	private JPanel screen;
	boolean flag;
	private String format;
	
	public GUI(){
		 	kmeans = new KMeans(); 
	        frame = new JFrame("NAI_s15159");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);			
		        
			try {
				kmeans.original = ImageIO.read(new File("Background.jpg"));
			} catch (IOException e1) { e1.printStackTrace();  };
			flag = false;
			
			screen = new JPanel(){
				public void paint(Graphics g) {
					super.paint(g);
					
					if(kmeans.result!=null&&!flag){
						g.drawImage(kmeans.result, 0, 0, screenW, screenH, this);
						return;
					}
					
					g.drawImage(kmeans.original, 0, 0, screenW, screenH, this);  
					
				}
			};
			screen.setPreferredSize(new Dimension(screenW, screenH));
			frame.add(screen, BorderLayout.CENTER);
			
			createMenuBar();
			createSettingsPanel();
			
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
	
			
	
	}

	private void createMenuBar() {
		Font font = new Font("TimesRoman", Font.PLAIN, 18);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		menuFile.setFont(font);
		
		JMenuItem itemOpen = new JMenuItem("Open");
		JMenuItem itemSave = new JMenuItem("Save");
		JMenuItem itemSaveAs = new JMenuItem("Save as ...");
		
		itemOpen.setFont(font);
		itemSave.setFont(font);
		itemSaveAs.setFont(font);

		menuBar.add(menuFile);
		
		menuFile.add(itemOpen);
		menuFile.addSeparator();
		menuFile.add(itemSave);
		menuFile.addSeparator();
		menuFile.add(itemSaveAs);

		itemOpen.setMnemonic('O');
		itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 2));
		itemSave.setMnemonic('S');
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 2));
		itemSaveAs.setMnemonic('A');
		itemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, 2));
		
		frame.setJMenuBar(menuBar);
		
		itemOpen.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        JFileChooser fileChooser = new JFileChooser(file);             
		        int ret = fileChooser.showOpenDialog(null);                
		        if (ret == JFileChooser.APPROVE_OPTION) {
	      		  	file = fileChooser.getSelectedFile();
	      		  	flag = true;
	      		  	kmeans.original = kmeans.loadImage(file);
	      		  	screenW = kmeans.original.getWidth();
	      		  	screenH = kmeans.original.getHeight();
	      		  	screen.setPreferredSize(new Dimension(screenW, screenH));
	      		  	frame.pack();
	      		  	screen.repaint();
	      		  	String path[] = file.getAbsolutePath().split("[.]");
	      		  	format = path[path.length-1];
		        }
		    }
		});
		
		itemSave.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e){
				  KMeans.saveImage(file, format, kmeans.result);
			  }
		});
		
		itemSaveAs.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e){
				  JFileChooser fileChooser = new JFileChooser(file);             
			      int ret = fileChooser.showSaveDialog(null);                
			      if (ret == JFileChooser.APPROVE_OPTION) {
			    	  file = fileChooser.getSelectedFile(); 
			    	  KMeans.saveImage(file, format, kmeans.result);
			      }
			  }
		  });
	}

	private void createSettingsPanel() {
		Font font = new Font("TimesRoman", Font.PLAIN, 18);
		
		JPanel settingsPanel = new JPanel(new FlowLayout());
		JLabel numberLabel = new JLabel("Clusters number");
		numberLabel.setFont(font);
		TextField numberField = new TextField();
		numberField.setText("5");
		numberField.setFont(font);
		numberField.setPreferredSize(new Dimension(50, 30));
		JLabel modeLabel = new JLabel("    Mode");
		modeLabel.setFont(font);
		JComboBox modeComboBox = new JComboBox();
		modeComboBox.setFont(font);
		modeComboBox.addItem("Synchronous");
		modeComboBox.addItem("Asynchronous");
		JButton startButton = new JButton("Start");
		startButton.setFont(font);
		
		settingsPanel.add(numberLabel);
		settingsPanel.add(numberField);
		settingsPanel.add(modeLabel);
		settingsPanel.add(modeComboBox);
		settingsPanel.add(startButton);
		
		
		frame.add(settingsPanel, BorderLayout.SOUTH);	
		
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flag = false;
				int clustersNumber = Integer.parseInt(numberField.getText());
				if(clustersNumber < 1){
					JOptionPane.showMessageDialog(frame, "Illegal number of clusters");
					return;
				}
				int mode = 0;
				if(modeComboBox.getSelectedItem().equals("Synchronous"))
					mode = 2;
				else if(modeComboBox.getSelectedItem().equals("Asynchronous"))
					mode = 1;
				if(kmeans.original==null){
					JOptionPane.showMessageDialog(frame, "Please, load a picture");
					return;
				}
				
				kmeans.calculate(kmeans.original, clustersNumber, mode, screen); 
				
			
			}
		});
	}

}
