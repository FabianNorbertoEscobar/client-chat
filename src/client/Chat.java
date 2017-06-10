package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;

public class Chat extends JFrame {

	private JPanel contentPane;
	private JTextField texto;
	private JTextArea chat;
	private Client client;

	public JTextArea getChat() {
		return chat;
	}

	public JTextField getTexto() {
		return texto;
	}

	public Chat(final Client client) {
		setBackground(Color.PINK);
		this.client = client;
		setTitle("Chat");
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 600, 483);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBackground(Color.PINK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 11, 577, 377);
		contentPane.add(scrollPane);
		
		chat = new JTextArea();
		chat.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
		chat.setEditable(false);
		scrollPane.setViewportView(chat);
		
		texto = new JTextField();
		texto.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				texto.requestFocus();
			}
		});
		
		texto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!texto.getText().equals("")) {
					chat.append("Me: " + texto.getText() + "\n");

					if(getTitle() != "Room"){
						client.setAccion(Mode.PRIVATE);
					} else {
						client.setAccion(Mode.BROADCAST);
					}
					
					client.getConjuntoMensaje().setUserEmisor(client.getUsuario().getUsername());
					client.getConjuntoMensaje().setUserReceptor(getTitle());
					client.getConjuntoMensaje().setMensaje(texto.getText());
					
					synchronized (client) {
						client.notify();
					}
					texto.setText("");
				}
				texto.requestFocus();
			}
		});

		JButton enviar = new JButton("Send");
		enviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!texto.getText().equals("")) {
					chat.append("Me: " + texto.getText() + "\n");

					if(getTitle() != "Room"){
						client.setAccion(Mode.PRIVATE);
					} else {
						client.setAccion(Mode.BROADCAST);
					}
					
					client.getConjuntoMensaje().setUserEmisor(client.getUsuario().getUsername());
					client.getConjuntoMensaje().setUserReceptor(getTitle());
					client.getConjuntoMensaje().setMensaje(texto.getText());
					
					synchronized (client) {
						client.notify();
					}
					texto.setText("");
				}
				texto.requestFocus();
			}
		});
		enviar.setBounds(460, 400, 127, 50);
		contentPane.add(enviar);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				client.getChatsActivos().remove(getTitle());
				if(!client.getChatsActivos().containsKey("Sala")) {
					Main.getBotonBroadcast().setEnabled(true);
				}
				dispose();
			}
		});
		texto.setBounds(10, 399, 438, 50);
		contentPane.add(texto);
		texto.setColumns(10);
	}
}
