package client;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class PeopleList extends JFrame {
	
	public static JList<String> listView = new JList<String>();
	
	public String name;
	private Client client;
	private User user;
	
	private JPanel content;
	private DefaultListModel<String> model;
	private JTextField nameField;
	
	private JButton connectButton;
	
	private JButton conversationButton;
	private static JButton broadcastButton;
	
	private String ip;
	private int port;
	
	/**
	 * Launch the application.
	 
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaContactos frame = new VentanaContactos();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	*/

	public PeopleList() {
		model = new DefaultListModel<String>();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(120, 120, 300, 300);

		JTextField ipField = new JTextField("localhost");
		JTextField portField = new JTextField("3000");
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2,2));
		panel.add(new JLabel("IP: "));
		panel.add(ipField);
		panel.add(new JLabel("PORT: "));
		panel.add(portField);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (client != null) { // ??
					synchronized (client) {
						client.mode = Mode.DISCONNECT;
						client.notify();
					}

					dispose(); // ??
				}

				System.exit(0);
			}
		});
		
		content = new JPanel();
		content.setBorder(new EmptyBorder(5, 5, 5, 5)); // ??
		setContentPane(content);
		content.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 20, 300, 200);
		content.add(scrollPane);

		conversationButton = new JButton("Chat");
		conversationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(listView.getSelectedValue() != null) {
					if(client != null && !client.conversations.containsKey(listView.getSelectedValue())) {
						Conversation conversation = new Conversation(client);
						client.conversations.put(listView.getSelectedValue(), conversation);
						conversation.setTitle(listView.getSelectedValue());
						conversation.setVisible(true);
					}
				}
			}
		});

		broadcastButton = new JButton("Multi Chat");
		broadcastButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!client.conversations.containsKey("Room")) {
					Conversation conversation = new Conversation(client);
					client.conversations.put("Room", conversation);
					conversation.setTitle("Room");
					conversation.setVisible(true);
				}
			}
		});
		broadcastButton.setBounds(200, 200, 100, 30);
		content.add(broadcastButton);
		
		nameField = new JTextField();
		nameField.setBounds(80, 200, 250, 30);
		content.add(nameField);
		nameField.setColumns(8);

		listView.setModel(model);
		scrollPane.setViewportView(listView);


		
		connectButton = new JButton("Conectar");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (user == null) {
					int result = JOptionPane.showConfirmDialog(null, panel, "Enter", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION) {
						ip = ipField.getText();
						port = Integer.valueOf(portField.getText());

						Login login = new Login();
						login.setTitle("Login");
						login.setVisible(true);

						login.addWindowListener(new WindowAdapter() {
							@Override
							public void windowClosed(WindowEvent e) {
								name = login.username;

								if (name != null) {
									client = new Client(ip, port);
									client.start();
									
			                        while(client.getState() != Thread.State.WAITING) {	}

			                        client.mode = Mode.LOGIN;
			                		client.user.name = name;

			                		synchronized (client) {
			                			client.notify();
			                		}
			                        
									ServerListener listener = new ServerListener(client);
									listener.start();
									
									synchronized (this) {
										try {
											this.wait(200);
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
									}
									
									if(Integer.parseInt(client.user.message) == 1) {
										setTitle(name);
										nameField.setText(name);

										if(client != null) {
											synchronized (client) {
												model.removeAllElements();
												if (client.user.users != null) {
													client.user.users.remove(client.user.name);
													for (String cad : client.user.users) {
														model.addElement(cad);
													}
													
													listView.setModel(model);
												}
											}
										}
										
										connectButton.setEnabled(false);
									} else {
										try {
											client.socket.close();
											client.input.close();
											client.output.close();
											
											client.stop();
											user = null;
										} catch (Exception j) {
											j.printStackTrace();
										}
									}	
								}
							}
						});
					}
				}
			}
		});
		
		connectButton.setBounds(10, 250, 100, 30);
		content.add(connectButton);

		JLabel usersLabel = new JLabel("Users: ");
		usersLabel.setBounds(10, 200, 120, 20);
		content.add(usersLabel);
	}

}
