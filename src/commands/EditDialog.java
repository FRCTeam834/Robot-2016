package commands;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EditDialog extends JDialog implements ActionListener  {
		JLabel[] labels;
		JTextField[] fields;
		String[] values;
		JButton done = new JButton("Done");
		JPanel content = new JPanel();
		public EditDialog(String[] l, String[] vals) {
			this.setModal(true);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			values = vals;
			this.setLayout(new BorderLayout());
			labels = new JLabel[l.length];
			fields = new JTextField[vals.length];
			content.setLayout(new GridLayout(fields.length,1));

			for(int i = 0; i < l.length; i++) {
				labels[i] = new JLabel(l[i]);
				fields[i] = new JTextField(vals[i], 12);
				
				JPanel toAdd = new JPanel();
				toAdd.setLayout(new FlowLayout(FlowLayout.RIGHT));
				toAdd.add(labels[i]);
				toAdd.add(fields[i]);
				content.add(toAdd);
				
			}
			this.add(done, BorderLayout.SOUTH);
			this.add(content, BorderLayout.CENTER);
			done.addActionListener(this);
			
			int i = 0;
			int max = 0;
			for(int j = 0; j < labels.length; j++) {
				if(labels[j].getText().length() > max){
					max = labels[j].getText().length();
					i = j;					
				}
			}
			
			this.setSize((fields[i].getPreferredSize().width + labels[i].getPreferredSize().width + 15), 30*fields.length + 70);
			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			for(int i = 0; i < values.length; i++) {
				values[i] = fields[i].getText();
			}
			this.dispose();

 		}		

}