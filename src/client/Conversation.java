package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import client.Client;
import client.Mode;

public class Conversation extends JFrame {

	public JTextArea text;
	public JTextField message;

	private Client client;
	private JPanel content;	
	
	
	/**
	 * Create the frame. 
	 */
	public Conversation(Client client) {
		this.client = client;

		setTitle("Conversation");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(200, 200, 600, 400);
		content = new JPanel();
		content.setBorder(new EmptyBorder(20, 20, 20, 20));

		setContentPane(content);
		content.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(20, 20, 400, 300);
		content.add(scrollPane);
		
		text = new JTextArea();
		text.setEditable(false);
		scrollPane.setViewportView(text);
		
		message = new JTextField();

		message.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		JButton send = new JButton("Send");
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		send.setBounds(300, 200, 100, 50);
		content.add(send);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				client.conversations.remove(getTitle());
				dispose();
			}
		});

		message.setBounds(20, 200, 400, 50);
		content.add(message);
		message.setColumns(8);
	}
	
	private void sendMessage() {
		sendMessage();
		text.append("Me: " + text.getText() + "\n");

		client.mode = getTitle() == "Room" ? Mode.BROADCAST : Mode.PRIVATE;
		client.message.sender = client.user.name;
		client.message.receiver = getTitle();
		client.message.content = message.getText();
		
		synchronized (client) {
			client.notify();
		}

		message.setText("");					
		message.requestFocus();
	}
}
