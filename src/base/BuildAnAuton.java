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
import java.net.URI;
import java.util.*;

public class BuildAnAuton extends JFrame implements ActionListener {
	private ArrayList<CommandBlock> commands = new ArrayList<CommandBlock>();
	
	JComponent workArea = new JComponent() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.draw(new Line2D.Double(0, this.getHeight()/(numThreads + 1), this.getWidth(), this.getHeight()/(numThreads + 1)));
			for(int j = 0; j < this.getWidth(); j+= 50)
				g2.draw(new Line2D.Double(j, this.getHeight()/(numThreads + 1) -10, j, this.getHeight()/(numThreads+1) + 10));
			
			for(int i = 1; i < numThreads; i++){
				CommandBlock reference = getFromMain(threadStarts[i]);
				
				int start = 0;
				if(reference == null) {
					CommandBlock last = getLastFromMain();
					if(last != null)
						start = last.getHitBox().x + last.WIDTH;
				}
				else
					start = reference.getHitBox().x;
				
				
				g2.draw(new Line2D.Double(start ,(i+1)*this.getHeight()/(numThreads + 1), this.getWidth(), (i+1)*this.getHeight()/(numThreads + 1)));
				
				for(int j = start; j < this.getWidth(); j+= 50)
					g2.draw(new Line2D.Double(j, (i+1)*this.getHeight()/(numThreads + 1) -10, j, (i+1)*this.getHeight()/(numThreads+1) + 10));
			}

			for(CommandBlock c:commands)
				c.paint(g2);

		}
		
	};
	
	private JScrollPane workAreaPane = new JScrollPane();
	
	private JMenuBar menu = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem save = new JMenuItem("Save");
	private JMenuItem load = new JMenuItem("Load");
	private JMenuItem export = new JMenuItem("Export");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem help = new JMenuItem("Help");
	JFileChooser fs = new JFileChooser();

	
	private JPanel buttons = new JPanel();
	private JLabel add = new JLabel("Add:");
	private JButton newCommand = new JButton("Command");
	private JButton newThread = new JButton("Thread");
	private JLabel delete = new JLabel("Delete:");
	private JButton delThread = new JButton("Thread");

	private JPanel threadPanel = new JPanel();
	private JTextField[] txtThreadStarts = new JTextField[1];
	
	private int xOffset;
	private int yOffset;
	private int focus = -1;
	
	private int snapGap = 30;
	private int numThreads = 1;
	private int[] threadStarts = {0};
	
	private HandleThreadChange threadChangeList = new HandleThreadChange();
	
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
		buttons.add(delete);
		buttons.add(delThread);
		
		newCommand.addActionListener(this);
		newThread.addActionListener(this);
		delThread.addActionListener(this);
		
		threadPanel.setLayout(new GridLayout(1, 1));
		txtThreadStarts[0] = new JTextField(3);
		txtThreadStarts[0].setVisible(false);
		threadPanel.add(txtThreadStarts[0]);
		
		workArea.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				for(int i = commands.size() - 1; i >= 0; i--) {
					if(commands.get(i).getEditPortion().contains(e.getPoint())) {
						commands.get(i).edit();
						workArea.repaint();	
						workArea.requestFocus();
						workArea.setLocation(workArea.getX() + 5, workArea.getY() + 5);
						workArea.setLocation(workArea.getX() - 5, workArea.getY() - 5);
						return;
					}
					if(commands.get(i).getDelPortion().contains(e.getPoint())) {
						if(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(null, "Delete Command?", "Delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE))
							commands.remove(i);
						workArea.repaint();	
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
		help.addActionListener(this);
		add(workAreaPane, BorderLayout.CENTER);
		add(threadPanel, BorderLayout.WEST);
		add(buttons, BorderLayout.SOUTH);

		validate();
	}

	public void place(int f) {
		if(f != -1) {
			CommandBlock temp = commands.get(f);
			int xtoswap = temp.getHitBox().x;
			int indexToPlace = 0;
			commands.remove(temp);
			for (int i = 0; i < commands.size(); i++)
				if(xtoswap > commands.get(i).getHitBox().x)
					indexToPlace += 1;
			commands.add(indexToPlace, temp);
		}
		workArea.repaint();	

	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == newCommand) {
			Object[] options = { 
				"Choose a Command", 
				MoveStraightCommand.class.toString().substring(15),
				MoveUntilProximityCommand.class.toString().substring(15),
				MoveAlongCurveCommand.class.toString().substring(15),
				MoveToPointCommand.class.toString().substring(15),
				TurnCommand.class.toString().substring(15),
				LightsCommand.class.toString().substring(15),
				DelayCommand.class.toString().substring(15),
				MoveFeederArmCommand.class.toString().substring(15),
				MoveBackArmCommand.class.toString().substring(15),
				ShootCommand.class.toString().substring(15),
				FeederCommand.class.toString().substring(15),

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
		else if(e.getSource() == save) {
			FileNameExtensionFilter fil = new FileNameExtensionFilter("Auton", "aut");
			fs.setFileFilter(fil);
			if(fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {				
				File f = fs.getSelectedFile();
				save(f);
			}
			workArea.repaint();	
			this.revalidate();

		}
		else if(e.getSource() == load) {
			FileNameExtensionFilter fil = new FileNameExtensionFilter("Auton", "aut");
			fs.setFileFilter(fil);
			if(fs.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File f = fs.getSelectedFile();
				open(f);
			}
			
		}
		else if(e.getSource() == export) {
			String fName = JOptionPane.showInputDialog(this, "Enter the name of the program (exclude extension), \nand make sure you are connected to the roboRio",
							"Export to RobotRio", JOptionPane.DEFAULT_OPTION);
			
			export(new File(fName + ".autr"));
		}
		else if(e.getSource() == newThread) {
			if(numThreads < 4) {
				int tempStart = Integer.parseInt(JOptionPane.showInputDialog("Enter which command(Integer) to run with"));
				
				numThreads += 1;
				threadStarts = Arrays.copyOf(threadStarts, numThreads);
				txtThreadStarts = Arrays.copyOf(txtThreadStarts, numThreads);
				threadPanel.setLayout(new GridLayout(numThreads , 1));
				txtThreadStarts[numThreads-1] = new JTextField(3);				
				txtThreadStarts[numThreads-1].addActionListener(threadChangeList);
				txtThreadStarts[numThreads-1].setText(Integer.toString(tempStart));
				threadStarts[numThreads-1] = tempStart - 1;
				threadPanel.add(txtThreadStarts[numThreads-1]);
				
				this.revalidate(); 
				this.repaint();
			}
		}
		else if(e.getSource() == help) {
			System.out.println("clicked");
			try {
				java.awt.Desktop.getDesktop().browse(URI.create("https://raw.githubusercontent.com/FRCTeam834/Robot-2015/master/Build-an-Auton%20Tutorial?token=AP8A9G4dGEWEL4EyhQ6bmfoD9VNr27wvks5Wz55nwA%3D%3D"));
			} catch (IOException e1) {
				
			}
		}
		else if(e.getSource() == delThread) {
			if(numThreads > 1) {
				numThreads -= 1;
				threadPanel.remove(txtThreadStarts[numThreads]);
				threadStarts = Arrays.copyOf(threadStarts, numThreads);
				txtThreadStarts = Arrays.copyOf(txtThreadStarts, numThreads);
				threadPanel.setLayout(new GridLayout(numThreads , 1));
				this.revalidate();
				this.repaint();
			}
			
		}
	}
	
	public void save(File f) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream (f));
			oos.writeObject(this.getSize());
			oos.writeObject(workArea.getPreferredSize());
			oos.writeObject(commands);
			oos.writeObject(txtThreadStarts);
			oos.close();
		}
		catch(IOException exc) {
			exc.printStackTrace();
		}
	}
	public void open(File f) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			Dimension windowSize = (Dimension) ois.readObject();
			Dimension workAreaSize = (Dimension) ois.readObject();
			
			commands = (ArrayList<CommandBlock>) ois.readObject();
			txtThreadStarts = (JTextField[]) ois.readObject();
			numThreads = txtThreadStarts.length;
			threadStarts = Arrays.copyOf(threadStarts, numThreads);
			for(int i = 1; i< txtThreadStarts.length; i++) {
				threadStarts[i] = Integer.parseInt(txtThreadStarts[i].getText()) - 1;
			}
			threadPanel.setLayout(new GridLayout(numThreads, 1));
			threadPanel.removeAll();
			for(int i = 0; i< txtThreadStarts.length; i++) {
				threadPanel.add(txtThreadStarts[i]);
				txtThreadStarts[i].addActionListener(threadChangeList);
			}
			
			
			workArea.setPreferredSize(workAreaSize);
			this.setSize(windowSize);
			
			this.revalidate();
			this.repaint();
			ois.close();
		}
		catch(IOException exc){exc.printStackTrace();}
		catch(ClassNotFoundException exc) {}
	}
	public void export(File f) {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream (f));

			oos.writeInt(numThreads);
			
			
			for(int i = 0; i < numThreads; i++) {

				ArrayList<Command> program = new ArrayList<>();
				try {
				threadStarts[i] = Integer.parseInt(txtThreadStarts[i].getText()) - 1;
				}catch (Exception e){}
				oos.writeInt(new Integer(threadStarts[i]));
				
				for(CommandBlock c: commands)
					if (c.getSnapped() == i)
						program.add(c.getCommand());
				oos.writeObject(program);
	
			}
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
				int mousex = MouseInfo.getPointerInfo().getLocation().x - workArea.getLocationOnScreen().x;//workArea.getMousePosition().x;
				int mousey = MouseInfo.getPointerInfo().getLocation().y - workArea.getLocationOnScreen().y;//workArea.getMousePosition().y;
				//System.out.println(workArea.getMousePosition().x + ", " + workArea.getMousePosition().y);
				
				
				block.setX(mousex - xOffset);
				
				int y = mousey-yOffset;
				
				if(Math.abs(y + 60 - (workArea.getHeight())/(numThreads + 1))< snapGap) {
					block.snap(0);
					y = workArea.getHeight()/(numThreads+1) - 60;
				}

				for(int i = 1; i < numThreads; i++) {
					CommandBlock reference = getFromMain(threadStarts[i]);
					int start = reference == null ? 0 : reference.getHitBox().x;

					if(Math.abs(y + 60 - ((i+1)* workArea.getHeight())/(numThreads + 1))< snapGap && mousex - xOffset >= start) {
						block.snap(i);
						y = (i+1)* workArea.getHeight()/(numThreads+1) - 60;
					}
				}
				block.setY(y);
				
				
				if(workAreaPane.getViewport().getViewPosition().x +workAreaPane.getViewport().getExtentSize().width - 100  < block.getHitBox().x) {
					if(workAreaPane.getHorizontalScrollBar().getValue() + workAreaPane.getHorizontalScrollBar().getWidth() >= workArea.getPreferredSize().width - 1
							|| workAreaPane.getWidth() > workArea.getPreferredSize().width) {
						workArea.setPreferredSize(new Dimension(workArea.getPreferredSize().width + 1, workArea.getPreferredSize().height) );
					}
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
			catch(Exception e){e.printStackTrace();}
			
			}

		}
	
	}

	public class HandleThreadChange implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			for(int i = 1; i < numThreads; i++) {
				JTextField temp = txtThreadStarts[i];
				if(e.getSource().equals(temp)) {
					int newStart = Integer.parseInt(temp.getText()) - 1;
					threadStarts[i] = newStart;
					workArea.repaint();
				}
			}
		}
		
	}
	
	private CommandBlock getFromMain(int i) {
		int counter = 0;
		for(CommandBlock c: commands)
			if(c.getSnapped() == 0) {
				if(counter == i)
					return c;
				counter++;
			}
		return null;
	}
	
	private CommandBlock getLastFromMain() {
		CommandBlock last = null;
		for(CommandBlock c: commands)
			if(c.getSnapped() == 0)
				last = c;
		return last;
	}
	
	public static void main(String[] args) {
		BuildAnAuton x = new BuildAnAuton();
		x.pack();
		x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		x.setVisible(true);
		if(args.length != 0)
			x.open(new File(args[0]));
	}
}
