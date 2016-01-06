package base;

import Testing.*;
import commands.*;
import commands.TurnCommand;

import java.awt.event.*;
import java.awt.geom.Line2D;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class BuildAnAuton extends JFrame implements ActionListener {
	private ArrayList<CommandBlock> commands = new ArrayList<CommandBlock>();
	
	JComponent workArea = new JComponent() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.draw(new Line2D.Double(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2));
			
			for(int i = 0; i < this.getWidth(); i+= 50) {
				g2.draw(new Line2D.Double(i, this.getHeight()/2 -10, i, this.getHeight()/2 + 10));
			}
			
			for(CommandBlock c:commands) {
				c.paint(g2);
			}
		}
		
		public Dimension getPreferredSize() {
			return new Dimension(super.getPreferredSize().width, super.getPreferredSize().height);
		}
	};
	
	private JScrollPane workAreaPane = new JScrollPane();
	
	private JMenuBar menu = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem save = new JMenuItem("Save");
	private JMenuItem load = new JMenuItem("Load");
	private JMenuItem export = new JMenuItem("Export");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem help = new JMenu("Help");
	JFileChooser fs = new JFileChooser();

	
	private JPanel buttons = new JPanel();
	private JButton add = new JButton("add");
	
	private int xOffset;
	private int yOffset;
	private int focus = -1;
	
	private int snapGap = 60;
	
	public BuildAnAuton() {
		
		setLayout(new BorderLayout());
		workAreaPane.setBackground(new Color(240, 240, 240));
		workAreaPane.setViewportView(workArea);
		workAreaPane.getViewport().setBackground(new Color(240, 240, 240));
		workAreaPane.setBackground(new Color(240, 240, 240));
		workAreaPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		workAreaPane.setPreferredSize(new Dimension(500, 350));
		workArea.setPreferredSize(new Dimension(450, 0));

		buttons.add(add);
		
		add.addActionListener(this);

		workArea.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				for(int i = commands.size() - 1; i >= 0; i--) {
					if(commands.get(i).getEditPortion().contains(e.getPoint())) {
						commands.get(i).edit();
						return;
					}
					if(commands.get(i).getDelPortion().contains(e.getPoint())) {
						if(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(null, "Delete Command?", "Delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE))
							commands.remove(i);
						return;
					}
				
				}
				
			}
			public void mouseEntered(MouseEvent e) {
				
			}
			public void mouseExited(MouseEvent e) {
				
			}
			public void mouseReleased(MouseEvent e) {
				if(focus != -1) {
				int temp = focus;
				focus = -1;
				if(Math.abs(commands.get(temp).getHitBox().y + 60 - workArea.getHeight()/2) < snapGap){

					commands.get(temp).setY(workArea.getHeight()/2 - 60);
					commands.get(temp).snap();
				}
				if(commands.get(temp).getHitBox().x < 0){
					commands.get(temp).setX(0);
				}
				place(temp);
				}
			}
			public void mousePressed(MouseEvent e) {
				
				for(int i = commands.size() - 1; i >= 0; i--) {
					Rectangle r = commands.get(i).getDragPortion();
					if(r.contains(e.getPoint())) {
						commands.get(i).unsnap();
						focus = i;
						xOffset = e.getX() - r.x;
						yOffset = e.getY() - r.y -1;//No idea why I had to add a -1
						break;
					}
				}
			}
			
		}); 

		Thread t = new Thread(new Runnable() {
			public void run() {
				while(true) {
					workArea.repaint();	
					
					if(focus != -1) {
						
					
					try{
						Thread.sleep(10);
						commands.get(focus).setX(workArea.getMousePosition().x - xOffset);
						commands.get(focus).setY(Math.abs(workArea.getMousePosition().y - yOffset + 60 - workArea.getHeight()/2) < snapGap ? 
						workArea.getHeight()/2 - 60: 
						workArea.getMousePosition().y - yOffset);

						if(workAreaPane.getViewport().getViewPosition().x +workAreaPane.getViewport().getExtentSize().width - 100  < commands.get(focus).getHitBox().x) {
							workArea.setPreferredSize(new Dimension(workArea.getPreferredSize().width + 1, workArea.getPreferredSize().height) );
							workArea.revalidate();
							workAreaPane.getHorizontalScrollBar().setValue(workAreaPane.getHorizontalScrollBar().getValue() + 1);
							commands.get(focus).setX(commands.get(focus).getHitBox().x + 1);
						}
					}
					catch(Exception e){}
					
					}
				}
			}
		});
		
		
		
		menu.add(fileMenu);
		fileMenu.add(save);
		fileMenu.add(load);
		fileMenu.add(export);
		menu.add(helpMenu);
		helpMenu.add(help);
		add(menu, BorderLayout.NORTH);
	
		save.addActionListener(this);
		load.addActionListener(this);
		export.addActionListener(this);

		add(buttons, BorderLayout.SOUTH);
		add(workAreaPane, BorderLayout.CENTER);
	
		validate();
		t.start();
	}

	public void place(int f) {
		if(f != -1) {
			CommandBlock temp = commands.get(f);
			int xtoswap = temp.getHitBox().x;
			int indexToPlace = 0;
			commands.remove(temp);
			for (int i = 0; i < commands.size(); i++) {
				if(xtoswap > commands.get(i).getHitBox().x) {
					indexToPlace += 1;
				}
			}
			commands.add(indexToPlace, temp);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == add) {
			Object[] options= {TurnCommand.class.toString().substring(6)};
			String o;
			Object temp = JOptionPane.showInputDialog(this, "Choose a command to add", "Choose a command to add", 1, null, options, options[0]);
			if(temp != null) {
				o = (String) temp;
				try {
					commands.add(new CommandBlock((Command) Class.forName(o).newInstance(), Color.WHITE, Color.BLACK));
				}
				catch (ClassNotFoundException e1) {} 
				catch (InstantiationException e1) {}
				catch (IllegalAccessException e1) {}
			}
			
			
		}
		if(e.getSource() == save) {
			FileNameExtensionFilter fil = new FileNameExtensionFilter("Auton", "aut");
			fs.setFileFilter(fil);
			if(fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {				
				File f = fs.getSelectedFile();
				save(f);
			}
		}
		if(e.getSource() == load) {
			FileNameExtensionFilter fil = new FileNameExtensionFilter("Auton", "aut");
			fs.setFileFilter(fil);
			if(fs.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File f = fs.getSelectedFile();
				open(f);
			}
			
		}
		if(e.getSource() == export) {
			FileNameExtensionFilter fil = new FileNameExtensionFilter("Program", "autr");
			fs.setFileFilter(fil);
			if(fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {				
				File f = fs.getSelectedFile();
				export(f);
			}

		}
	}
	
	public void save(File f) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream (f));
			oos.writeObject(this.getSize());
			oos.writeObject(commands);
			oos.close();
		}
		catch(IOException exc) {
			exc.printStackTrace();
		}
	}
	public void open(File f) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			Dimension size = (Dimension) ois.readObject();
			commands = (ArrayList<CommandBlock>) ois.readObject();
			CommandBlock last = commands.get(commands.size() -1);
			workArea.setPreferredSize(new Dimension(last.getHitBox().x + 100, workArea.getPreferredSize().height));
			this.setSize(size);
			ois.close();
		}
		catch(IOException exc){exc.printStackTrace();}
		catch(ClassNotFoundException exc) {System.out.println("Error 2");}
	}
	public void export(File f) {
		ArrayList<Command> program = new ArrayList<Command>();
		for(CommandBlock c: commands) {
			if(c.isSnapped())
				program.add(c.getCommand());
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream (f));
			oos.writeObject(program);
			oos.close();
		}
		catch(IOException exc){exc.printStackTrace();}

	}
}
