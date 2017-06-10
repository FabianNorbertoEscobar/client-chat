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

import client.Comando;
import client.Paquete;
import client.PaqueteUsuario;
import java.awt.Color;
import java.awt.Font;

public class Main extends JFrame {
	private String user = null;
	private Cliente cliente;
	private PaqueteUsuario paqueteUsuario;
	
	private JPanel contentPane;
	private DefaultListModel<String> modelo = new DefaultListModel<String>();
	private static JList<String> list = new JList<String>();
	private JTextField jTFMiNombre;
	private static JLabel lblNumeroConectados = new JLabel("");
	private static JButton botonMc;

	private String ipScanned = "localhost";
	private String usernameScanned = "";
	private int puertoScanned = 3000;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {

		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 399, 300);
		setLocationRelativeTo(null);
		
		JTextField ip = new JTextField(5);
		JTextField puerto = new JTextField(5);
		JTextField username = new JTextField(5);
		
		ip.setText(ipScanned);
		puerto.setText(String.valueOf(puertoScanned));
		
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridLayout(3,3));
		loginPanel.add(new JLabel("IP: "));
		loginPanel.add(ip);
		loginPanel.add(new JLabel("Port: "));
		loginPanel.add(puerto);
		loginPanel.add(new JLabel("User: "));
		loginPanel.add(username);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (cliente != null) {
					synchronized (cliente) {
						cliente.setAccion(Comando.DISCONNECT);
						cliente.notify();
					}
					setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				}
				System.exit(0);
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.PINK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 377, 186);
		contentPane.add(scrollPane);

		botonMc = new JButton("Room");
		botonMc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Integer.valueOf(lblNumeroConectados.getText()) != 0) {
					if(!cliente.getChatsActivos().containsKey("Sala")) {
						Chat chat = new Chat(cliente);
						cliente.getChatsActivos().put("Sala", chat);
						chat.setTitle("Sala");
						chat.setVisible(true);
						botonMc.setEnabled(false);
					}
				}
			}
		});
		botonMc.setBounds(253, 220, 134, 46);
		contentPane.add(botonMc);
		
		jTFMiNombre = new JTextField();
		jTFMiNombre.setHorizontalAlignment(SwingConstants.LEFT);
		jTFMiNombre.setEditable(false);
		jTFMiNombre.setBounds(67, 320, 242, 22);
		contentPane.add(jTFMiNombre);
		jTFMiNombre.setColumns(10);
		list.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

		list.setModel(modelo);
		scrollPane.setViewportView(list);

		if (user == null) {
			int result = JOptionPane.showConfirmDialog(null, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION);

			if (result == JOptionPane.OK_OPTION) {
				ipScanned = ip.getText();
				puertoScanned = Integer.valueOf(puerto.getText());
				user = username.getText();
				if (user != null) {
					cliente = new Cliente(ipScanned, puertoScanned);
					cliente.start();
					
                    while(cliente.getState() != Thread.State.WAITING) {	
                    }
					logIn(cliente);		
					EscuchaServer em = new EscuchaServer(cliente);
					em.start();
					
					synchronized (this) {
						try {
							this.wait(200);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					
					if(cliente.getPaqueteUsuario().getMensaje().equals(Paquete.msjExito)) {
						setTitle(user);
						jTFMiNombre.setText(user);
						actualizarLista(cliente);
					} else {
						try {
							cliente.getSalida().close();
							cliente.getEntrada().close();
							cliente.getSocket().close();
							cliente.stop();
							user = null;
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}	
				}
			}
		}

		JLabel lblUsuariosConectados = new JLabel("Usuarios Conectados:");
		lblUsuariosConectados.setBounds(10, 323, 138, 16);
		contentPane.add(lblUsuariosConectados);

		
		lblNumeroConectados.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNumeroConectados.setBounds(253, 338, 56, 16);
		contentPane.add(lblNumeroConectados);
		lblNumeroConectados.setText(String.valueOf(modelo.getSize()));

		JLabel lblMiUser = new JLabel("Mi User: ");
		lblMiUser.setBounds(20, 320, 56, 16);
		contentPane.add(lblMiUser);

		JLabel label = new JLabel("");
		label.setBounds(130, 267, 56, 16);
		contentPane.add(label);
		
		JButton btnPrivate = new JButton("Private");
		btnPrivate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list.getSelectedValue() != null) {
					if(!cliente.getChatsActivos().containsKey(list.getSelectedValue())) {
						if (cliente != null) {
							Chat chat = new Chat(cliente);
							cliente.getChatsActivos().put(list.getSelectedValue(), chat);
							chat.setTitle(list.getSelectedValue());
							chat.setVisible(true);
						}	
					}
				}
			}
		});

		btnPrivate.setBounds(14, 220, 134, 46);
		contentPane.add(btnPrivate);
	}

	private void logIn(final Cliente cliente) {
		cliente.setAccion(Comando.LOGIN);
		cliente.getPaqueteUsuario().setUsername(user);
		synchronized (cliente) {
			cliente.notify();
		}
	}

	private void actualizarLista(final Cliente cliente) {
		if(cliente != null) {
			synchronized (cliente) {
				modelo.removeAllElements();
				if (cliente.getPaqueteUsuario().getListaDeConectados() != null) {
					cliente.getPaqueteUsuario().getListaDeConectados().remove(cliente.getPaqueteUsuario().getUsername());
					for (String cad : cliente.getPaqueteUsuario().getListaDeConectados()) {
						modelo.addElement(cad);
					}
					lblNumeroConectados.setText(String.valueOf(modelo.getSize()));
					list.setModel(modelo);
				}
			}
		}
	}
	
	public PaqueteUsuario getPaqueteUsuario() {
		return paqueteUsuario;
	}
	
	public static JLabel getLblNumeroConectados() {
		return lblNumeroConectados;
	}
	
	public static JList<String> getList() {
		return list;
	}
	
	public static JButton getBotonMc() {
		return botonMc;
	}
}
