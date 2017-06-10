package client;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Cliente extends Thread {
	private Socket cliente;
	private static String miIp;
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;
	private PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
	private PaqueteMensaje paqueteMensaje = new PaqueteMensaje();
	private Map<String, Chat> chatsActivos = new HashMap<>();

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
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		synchronized(this) {
			try {

				paqueteUsuario = new PaqueteUsuario();
				while (!paqueteUsuario.isInicioSesion()) {

					wait();
					
					switch (getAccion()) {
					
						case Comando.LOGIN:
							paqueteUsuario.setComando(Comando.LOGIN);
							salida.writeObject(gson.toJson(paqueteUsuario));
							break;
							
						case Comando.PRIVATE:
							paqueteMensaje.setComando(Comando.PRIVATE);
							salida.writeObject(gson.toJson(paqueteMensaje));
							
							break;
							
						case Comando.BROADCAST:
							paqueteMensaje.setComando(Comando.BROADCAST);
							salida.writeObject(gson.toJson(paqueteMensaje));
							break;
							
						case Comando.DISCONNECT:
							paqueteUsuario.setIp(getMiIp());
							paqueteUsuario.setComando(Comando.DISCONNECT);
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

	public Map<String, Chat> getChatsActivos() {
		return chatsActivos;
	}

	public void setChatsActivos(Map<String, Chat> chatsActivos) {
		this.chatsActivos = chatsActivos;
	}
}