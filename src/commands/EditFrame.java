package commands;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class EditFrame extends JDialog implements ActionListener {
		JLabel[] labels;
		JTextField[] fields;
		String[] values;
		JButton done = new JButton("Done");

		public EditFrame(String[] l, String[] vals) {
			this.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setLayout(new FlowLayout());
			values = vals;
			
			labels = new JLabel[l.length];
			fields = new JTextField[vals.length];
			
			for(int i = 0; i < l.length; i++) {
				labels[i] = new JLabel(l[i]);
				fields[i] = new JTextField(vals[i], 12);
				this.add(labels[i]);
				this.add(fields[i]);
			}
			this.add(done);
			done.addActionListener(this);
			pack();
			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for(int i = 0; i < values.length; i++) {
				values[i] = fields[i].getText();
			}
			System.out.println(values[0] + ""+ values[1]);
			this.dispose();
		}
		

}