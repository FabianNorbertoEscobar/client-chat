package client;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

public class Cliente extends Thread {
	private Socket cliente;
	private static String miIp;
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;
	private PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
	private PaqueteMensaje paqueteMensaje = new PaqueteMensaje();
	private Map<String, MiChat> chatsActivos = new HashMap<>();

	private int accion;
	
	private final Gson gson = new Gson();
	
	private String ip;
	private int puerto;
	
	public Cliente(String newIp, int newPort) {
		
		this.ip = newIp;
		this.puerto = newPort;
		
		try {
			cliente = new Socket(ip, puerto);
			miIp = cliente.getInetAddress().getHostAddress();
			entrada = new ObjectInputStream(cliente.getInputStream());
			salida = new ObjectOutputStream(cliente.getOutputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Fallo al iniciar la aplicación. "
					+ "Revise la conexión con el servidor.");
			System.exit(1);
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		synchronized(this) {
			try {
				// Creo el paquete que le voy a enviar al servidor
				paqueteUsuario = new PaqueteUsuario();
				while (!paqueteUsuario.isInicioSesion()) {
					
					// Espero a que el usuario seleccione alguna accion
					wait();
					
					switch (getAccion()) {
					
						case Comando.INICIOSESION:
							paqueteUsuario.setComando(Comando.INICIOSESION);
							// Le envio el paquete al servidor
							salida.writeObject(gson.toJson(paqueteUsuario));
							break;
							
						case Comando.TALK:
							paqueteMensaje.setComando(Comando.TALK);
							// Le envio el paquete al servidor
							salida.writeObject(gson.toJson(paqueteMensaje));
							
							break;
							
						case Comando.CHATALL:
							paqueteMensaje.setComando(Comando.CHATALL);
							// Le envio el paquete al servidor
							salida.writeObject(gson.toJson(paqueteMensaje));
							break;
							
						case Comando.DESCONECTAR:
							paqueteUsuario.setIp(getMiIp());
							paqueteUsuario.setComando(Comando.DESCONECTAR);
							// Le envio el paquete al servidor
							salida.writeObject(gson.toJson(paqueteUsuario));
							break;
							
						default:
							break;
					}
					
					salida.flush();
				}

				paqueteUsuario.setIp(miIp);
				salida.writeObject(gson.toJson(paqueteUsuario));
				notify();
				
			} catch (IOException | InterruptedException e) {
				JOptionPane.showMessageDialog(null, "Fallo la conexión del Cliente.");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void setAccion(int accion) {
		this.accion = accion;
	}
	
	public int getAccion() {
		return accion;
	}
	
	public Socket getSocket() {
		return cliente;
	}

	public void setSocket(final Socket cliente) {
		this.cliente = cliente;
	}

	public static String getMiIp() {
		return miIp;
	}

	public void setMiIp(final String miIp) {
		this.miIp = miIp;
	}

	public ObjectInputStream getEntrada() {
		return entrada;
	}
	
	public void setEntrada(final ObjectInputStream entrada) {
		this.entrada = entrada;
	}
	
	public ObjectOutputStream getSalida() {
		return salida;
	}
	
	public void setSalida(final ObjectOutputStream salida) {
		this.salida = salida;
	}

	public PaqueteUsuario getPaqueteUsuario() {
		return paqueteUsuario;
	}
	
	public PaqueteMensaje getPaqueteMensaje() {
		return paqueteMensaje;
	}

	public void setPaqueteMensaje(PaqueteMensaje fromJson) {
		this.paqueteMensaje.setMensaje(fromJson.getMensaje());
		this.paqueteMensaje.setUserEmisor(fromJson.getUserEmisor());
		this.paqueteMensaje.setUserReceptor(fromJson.getUserReceptor());
	}

	public Map<String, MiChat> getChatsActivos() {
		return chatsActivos;
	}

	public void setChatsActivos(Map<String, MiChat> chatsActivos) {
		this.chatsActivos = chatsActivos;
	}
}