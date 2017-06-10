package client;

import java.io.Serializable;

public class Conjunto implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private String mensaje;
	private String ip;
	private int mode;

	public static String SUCCESS = "1";
	public static String FAILURE = "0";
	
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
	
	public Conjunto() {}
	
	public Conjunto(String mensaje, String nick, String ip, int mode) {
		this.mensaje = mensaje;
		this.ip = ip;
		this.mode = mode;
	}
	
	public Conjunto(String mensaje, int mode) {
		this.mensaje = mensaje;
		this.mode = mode;
	}

	public Conjunto(int mode) {
		this.mode = mode;
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
