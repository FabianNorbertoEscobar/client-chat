package client;

import java.util.ArrayList;
import java.util.Map;

public class Users extends Container {
		public ArrayList<String> users;
		public Map<String, User> map;
		
		public Users(ArrayList<String> users) {
			this.users = users;
		}
}
