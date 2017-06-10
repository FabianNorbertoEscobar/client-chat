package client;

import java.io.Serializable;

public class Paquete implements Serializable, Cloneable {
	private String mensaje;
	private String ip;
	private int mode;

	public static String SUCCESS = "1";
	public static String FAILURE = "0";
	
	public Paquete() {
	}
	
	public Paquete(String mensaje, String nick, String ip, int mode) {
		this.mensaje = mensaje;
		this.ip = ip;
		this.mode = mode;
	}
	
	public Paquete(String mensaje, int mode) {
		this.mensaje = mensaje;
		this.mode = mode;
	}

	public Paquete(int mode) {
		this.mode = mode;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getMensaje() {
		return mensaje;
	}

	public String getIp() {
		return ip;
	}

	public int getMode() {
		return mode;
	}
	
	public Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

}
