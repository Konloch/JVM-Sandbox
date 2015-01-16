package the.bytecode.club.jvmsandbox;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.awt.Dimension;

public class GUI extends JFrame {
	public GUI() {
		setSize(new Dimension(515, 126));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Invoke Function - https://the.bytecode.club - @Konloch");
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Jar Location:");
		lblNewLabel.setBounds(10, 11, 106, 14);
		getContentPane().add(lblNewLabel);
		
		txtExamplejar = new JTextField();
		txtExamplejar.setText("example.jar");
		txtExamplejar.setBounds(159, 8, 340, 20);
		getContentPane().add(txtExamplejar);
		txtExamplejar.setColumns(10);
		
		JLabel lblMainClass = new JLabel("Main Class:");
		lblMainClass.setBounds(7, 42, 142, 14);
		getContentPane().add(lblMainClass);
		
		txtOrgexampleentrymain = new JTextField();
		txtOrgexampleentrymain.setText("org/example/Entry");
		txtOrgexampleentrymain.setBounds(159, 39, 341, 20);
		getContentPane().add(txtOrgexampleentrymain);
		txtOrgexampleentrymain.setColumns(10);
		
		JButton btnNewButton_1 = new JButton("Invoke");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String[] params = null;
					Class<?> c = JVMSandbox.loadIntoClassloader(txtExamplejar.getText(), txtOrgexampleentrymain.getText());
					Method method = c.getMethod("main", String[].class);
					method.invoke (null, (Object)params);
					JVMSandbox.gui.setVisible(false);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(10, 67, 490, 23);
		getContentPane().add(btnNewButton_1);
	}

	private static final long serialVersionUID = 3200663620236488765L;
	private JTextField txtExamplejar;
	private JTextField txtOrgexampleentrymain;
}
