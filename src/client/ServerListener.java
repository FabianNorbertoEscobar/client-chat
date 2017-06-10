package client;

import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import com.google.gson.Gson;


public class ServerListener extends Thread {

	private final Gson gson = new Gson();
	private Conversation conversation;
	
	private Client client;
	private ObjectInputStream input;

	public static ArrayList<String> users = new ArrayList<String>();

	public ServerListener(Client client) {
		this.client = client;
		this.input = client.input;
	}

	@Override
	public void run() {
		try {
			Container container;
			String line;

			ArrayList<String> oldUsers = new ArrayList<String>();
			ArrayList<String> diff = new ArrayList<String>();			

			while (true) {

				synchronized (this.input) {
					line = (String) this.input.readObject();	
				}

				container = gson.fromJson(line, Container.class);

				switch (container.mode) {
				
					case Mode.LOGIN:
						client.user.message = container.message;
						if(client != null && Integer.parseInt(container.message) == 0 ) {
							users = (ArrayList<String>) gson.fromJson(line, Users.class).map;
						} else {
							this.stop();							
						}

						break;
						
					case Mode.CONNECTION:
						users = (ArrayList<String>) gson.fromJson(line, Users.class).map;

						for (String name : users) {
							if(!oldUsers.contains(name)) {
								oldUsers.add(name);
							}
						}

						diff = new ArrayList<String>(oldUsers); // ??
						diff.removeAll(users);
						if(!diff.isEmpty()) {
							for (String name : diff) {
								if(client.conversations.containsKey(name)) {
									client.conversations.get(name).text.append(name + " is off");
								}
								oldUsers.remove(name);
							}
						}
						
						client.user.users = users;
						
						DefaultListModel<String> model = new DefaultListModel<String>();

						synchronized (client) {
							try {
								client.wait(300);
								PeopleList.listView.removeAll();
								if (client.user.users != null) {
									client.user.users.remove(client.user.name);

									for (String cad : client.user.users) {
										model.addElement(cad);
									}

									PeopleList.listView.setModel(model);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						break;

					case Mode.PRIVATE:
						
						client.setupMessage((Message) gson.fromJson(line, Message.class));
						
						String sender = client.message.sender;
						
						if(!(client.conversations.containsKey(sender))) {	
							conversation = new Conversation(client);
							conversation.setTitle(sender);
							conversation.setVisible(true);
							client.conversations.put(sender, conversation);
						}
						
						client.conversations.get(sender).text.append(sender + ": "  + client.message.content + "\n");
						client.conversations.get(sender).message.grabFocus();
						break;
						
					case Mode.BROADCAST:
						
						client.setupMessage((Message) gson.fromJson(line, Message.class));

						if(!client.conversations.containsKey("Room")) {	
							conversation = new Conversation(client);
							
							conversation.setTitle("Sala");
							conversation.setVisible(true);
							
							client.conversations.put("Sala", conversation);
						}

						client.conversations.get("Room").text.append(client.message.sender + ": "  + client.message.content + "\n");
						client.conversations.get("Room").message.grabFocus();

						break;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fallo la conexión con el servidor.");
			e.printStackTrace();
		}
	}
}