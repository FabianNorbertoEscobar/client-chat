package client;

public class Container implements Cloneable {

	public String ip;
	public int mode;
	public String message;
	
	public Container() {}
	
	public Container(String ip, int mode, String message) {
		this.ip = ip;
		this.mode = mode;
		this.message = message;
	}
	
	public Container(String mensaje, int comando) {
		this.message = mensaje;
		this.mode = comando;
	}

	public Container(int mode) {
		this.mode = mode;
	}
	
	public Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
}
