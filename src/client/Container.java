package client;

public class Container {

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
}
