package base;
import Testing.*;
import commands.*;

import java.awt.event.*;
import java.awt.geom.Line2D;

import javax.swing.*;
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
	private JLabel add = new JLabel("Add:");
	private JButton newCommand = new JButton("Command");
	private JButton newThread = new JButton("Thread");
	
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
		workArea.setPreferredSize(new Dimension(1000, 0));

		buttons.add(add);
		buttons.add(newCommand);
		buttons.add(newThread);
		newCommand.addActionListener(this);
		newThread.addActionListener(this);
		
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
				workArea.repaint();
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
					workArea.repaint();	

				}
			}
			public void mousePressed(MouseEvent e) {				
				for(int i = commands.size() - 1; i >= 0; i--) {
					Rectangle r = commands.get(i).getDragPortion();
					if(r.contains(e.getPoint())) {
						CommandBlock c = commands.get(i);
						c.unsnap();
						focus = i;
						xOffset = e.getX() - r.x;
						yOffset = e.getY() - r.y -1;//No idea why I had to add a -1
						new Thread(new Move(c)).start();
						break;
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
		workArea.repaint();	

	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == newCommand) {
			Object[] options = { 
				"Choose a Command", 
				MoveStraightCommand.class.toString().substring(15),
				MoveAlongCurveCommand.class.toString().substring(15),
				TurnCommand.class.toString().substring(15),
				LightsCommand.class.toString().substring(15),
				MoveFeederArmCommand.class.toString().substring(15),
				MoveBackArmCommand.class.toString().substring(15)	
			};
			String o;
			Object temp = JOptionPane.showInputDialog(null, "Choose a command to add", "Choose a command to add", 1, null, options, options[0]);
			
			if(temp != null) {
				o = (String) temp;
				try {
					commands.add(new CommandBlock((Command)Class.forName("commands." + 
					o).newInstance(), workAreaPane.getHorizontalScrollBar().getValue(), 0, Color.WHITE, Color.BLACK));
				}
				catch (ClassNotFoundException e1) { e1.printStackTrace();} 
				catch (InstantiationException e1) { e1.printStackTrace();}
				catch (IllegalAccessException e1) { e1.printStackTrace();}
			}
			workArea.repaint();	
			this.revalidate();
		}
		if(e.getSource() == save) {
			FileNameExtensionFilter fil = new FileNameExtensionFilter("Auton", "aut");
			fs.setFileFilter(fil);
			if(fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {				
				File f = fs.getSelectedFile();
				save(f);
			}
			workArea.repaint();	
			workArea.validate();

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
			String fName = JOptionPane.showInputDialog(this, "Enter the name of the program (exclude extension), \nand make sure you are connected to the roboRio",
							"Export to RobotRio", JOptionPane.DEFAULT_OPTION);
			
			export(new File(fName + ".autr"));
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
		catch(ClassNotFoundException exc) {}
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
			FTP ftp = new FTP(f.getName());
			ftp.save();
		}
		catch(IOException exc){exc.printStackTrace();}

	}
	
	public class Move implements Runnable {
		CommandBlock block;
		public Move (CommandBlock c ) {
			block = c;
		}
		
		public void run() {

			while(focus != -1) {
			try{
				Thread.sleep(10);
				int mousex = workArea.getMousePosition().x;
				int mousey = workArea.getMousePosition().y;
				
				block.setX(mousex - xOffset);
				block.setY(Math.abs(mousey - yOffset + 60 - workArea.getHeight()/2) < snapGap ? 
					workArea.getHeight()/2 - 60: 
					mousey - yOffset);

				if(workAreaPane.getViewport().getViewPosition().x +workAreaPane.getViewport().getExtentSize().width - 100  < block.getHitBox().x) {
					if(workAreaPane.getHorizontalScrollBar().getValue() + workAreaPane.getHorizontalScrollBar().getWidth() >= workArea.getPreferredSize().width - 1)
						workArea.setPreferredSize(new Dimension(workArea.getPreferredSize().width + 1, workArea.getPreferredSize().height) );
					workArea.revalidate();
					workAreaPane.getHorizontalScrollBar().setValue(workAreaPane.getHorizontalScrollBar().getValue() + 1);
					block.setX(commands.get(focus).getHitBox().x + 1);
				}
				else if(workAreaPane.getViewport().getViewPosition().x > block.getHitBox().x) {
					workArea.revalidate();
					workAreaPane.getHorizontalScrollBar().setValue(workAreaPane.getHorizontalScrollBar().getValue() - 1);
					block.setX(commands.get(focus).getHitBox().x - 1);
				}

				workArea.repaint();
				

			}
			catch(Exception e){}
			
			}

		}
		
	}

	public static void main(String[] args) {
		BuildAnAuton x = new BuildAnAuton();
		x.pack();
		x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		x.setVisible(true);
		if(args.length != 0) {
			x.open(new File(args[0]));
		}
	}
}
