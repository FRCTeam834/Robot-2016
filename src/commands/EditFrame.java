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

public class EditFrame extends JDialog implements ActionListener {
		JLabel[] labels;
		JTextField[] fields;
		String[] values;
		JButton done = new JButton("Done");
		JPanel content = new JPanel();
		public EditFrame(String[] l, String[] vals) {
			this.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
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
			this.setSize(240, 40*fields.length + 70);
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