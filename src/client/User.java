package client;

import java.util.ArrayList;

public class User extends Container {

	public String name;
	public boolean logged;
	public boolean state;
	public ArrayList<String> users;

	public User(){
		this.state = true; // ??
	}

	public User(String name){
		this.name = name;
		this.logged = false; // ??
		this.state = true;
	}
}