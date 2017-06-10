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
	private Usuario usuario = new Usuario();
	private ConjuntoMensaje conjuntoMensaje = new ConjuntoMensaje();
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

				usuario = new Usuario();
				while (!usuario.isInicioSesion()) {

					wait();
					
					switch (getAccion()) {
					
						case Mode.LOGIN:
							usuario.setMode(Mode.LOGIN);
							salida.writeObject(gson.toJson(usuario));
							break;
							
						case Mode.PRIVATE:
							conjuntoMensaje.setMode(Mode.PRIVATE);
							salida.writeObject(gson.toJson(conjuntoMensaje));
							
							break;
							
						case Mode.BROADCAST:
							conjuntoMensaje.setMode(Mode.BROADCAST);
							salida.writeObject(gson.toJson(conjuntoMensaje));
							break;
							
						case Mode.DISCONNECT:
							usuario.setIp(getMiIp());
							usuario.setMode(Mode.DISCONNECT);
							salida.writeObject(gson.toJson(usuario));
							break;
							
						default:
							break;
					}
					
					salida.flush();
				}

				usuario.setIp(miIp);
				salida.writeObject(gson.toJson(usuario));
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

	public Usuario getUsuario() {
		return usuario;
	}
	
	public ConjuntoMensaje getConjuntoMensaje() {
		return conjuntoMensaje;
	}

	public void setConjuntoMensaje(ConjuntoMensaje fromJson) {
		this.conjuntoMensaje.setMensaje(fromJson.getMensaje());
		this.conjuntoMensaje.setUserEmisor(fromJson.getUserEmisor());
		this.conjuntoMensaje.setUserReceptor(fromJson.getUserReceptor());
	}

	public Map<String, Chat> getChatsActivos() {
		return chatsActivos;
	}

	public void setChatsActivos(Map<String, Chat> chatsActivos) {
		this.chatsActivos = chatsActivos;
	}
}