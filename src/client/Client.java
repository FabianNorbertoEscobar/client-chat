package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import client.Conversation;
import client.Mode;
import client.Message;
import client.User;

public class Client extends Thread {
	
	public int mode;
	public int port;
	public String ip;
	private final Gson gson = new Gson();
	
	public ObjectInputStream input;
	public ObjectOutputStream output;
	
	public Socket socket;
	
	public User user;
	public Message message;
	public Map<String, Conversation> conversations;

	
	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.message = new Message();
		this.conversations = new HashMap<>();
		
		try {
			socket = new Socket(ip, port);
			this.ip = socket.getInetAddress().getHostAddress();
			input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		synchronized(this) {
			try {
				user = new User();
				while (!user.logged) {
					wait();
					
					switch (mode) {	
						case Mode.LOGIN:
							user.mode = mode;
							output.writeObject(gson.toJson(user));
							break;

						case Mode.LOGOUT:
							user.ip = this.ip;
							user.mode = mode;
							output.writeObject(gson.toJson(user));
							break;
							
						case Mode.PRIVATE:
						case Mode.BROADCAST:
							message.mode = mode;
							output.writeObject(gson.toJson(message));
							break;

						default:
							break;
					}
					
					output.flush();
				}

				user.ip = this.ip;
				output.writeObject(gson.toJson(user));
				notify();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setupMessage(Message _message) { // ??
		this.message.content = _message.content;
		this.message.sender = _message.sender;
		this.message.receiver = _message.receiver;
	}
}