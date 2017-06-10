package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {

	private JPanel content;
	private JTextField nameField;
	public String username;

	/**
	 * Create the frame.
	 */
	public Login() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 120);
		
		content = new JPanel();
		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(content);
		content.setLayout(null);

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				username = nameField.getText();
				dispose();
			}
		});
		
		loginButton.setBounds(12, 45, 97, 25);
		content.add(loginButton);
		
		nameField = new JTextField();
		nameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!nameField.getText().equals("")) {
					username = nameField.getText();
				}
				dispose();
			}
		});
		
		nameField.setBounds(139, 13, 116, 22);
		content.add(nameField);
		nameField.setColumns(10);
		
		JLabel nameLabel = new JLabel("Logged: ");
		nameLabel.setBounds(10, 20, 120, 20);
		content.add(nameLabel);
	}
}
