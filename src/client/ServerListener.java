package client;

import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import com.google.gson.Gson;

import client.Chat;
import client.Main;
import client.Mode;
import client.Conjunto;
import client.Usuarios;
import client.ConjuntoMensaje;

public class ServerListener extends Thread {

	private final Gson gson = new Gson();
	private Chat chat;
	
	private Client client;
	private ObjectInputStream entrada;

	protected static ArrayList<String> usuariosConectados = new ArrayList<String>();

	private void actualizarLista(final Client client) {
		DefaultListModel<String> modelo = new DefaultListModel<String>();
		synchronized (client) {
			try {
				client.wait(200);
				Main.getList().removeAll();
				if (client.getUsuario().getListaDeConectados() != null) {
					client.getUsuario().getListaDeConectados().remove(client.getUsuario().getUsername());
					for (String cad : client.getUsuario().getListaDeConectados()) {
						modelo.addElement(cad);
					}
					Main.getLblNumeroConectados().setText(String.valueOf(modelo.getSize()));
					Main.getList().setModel(modelo);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<String> getUsuariosConectados() {
		return usuariosConectados;
	}
	
	public ServerListener(final Client client) {
		this.client = client;
		this.entrada = client.getEntrada();
	}

	@Override
	public void run() {
		try {
			Conjunto conjunto;
			ArrayList<String> usuariosAntiguos = new ArrayList<String>();
			ArrayList<String> diferenciaContactos = new ArrayList<String>();

			String objetoLeido;
			while (true) {

				synchronized (entrada) {
					objetoLeido = (String) entrada.readObject();	
				}
				conjunto = gson.fromJson(objetoLeido, Conjunto.class);

				switch (conjunto.getMode()) {
				
					case Mode.LOGIN:
						client.getUsuario().setMensaje(conjunto.getMensaje());
						
						if(conjunto.getMensaje().equals(Conjunto.FAILURE)) {
							this.stop();
						} else {
							usuariosConectados = (ArrayList<String>) gson.fromJson(objetoLeido, Usuarios.class).getPersonajes();							
						}
						break;
						
					case Mode.CONNECT:
						usuariosConectados = (ArrayList<String>) gson.fromJson(objetoLeido, Usuarios.class).getPersonajes();
						for (String usuario : usuariosConectados) {
							if(!usuariosAntiguos.contains(usuario)) {
								usuariosAntiguos.add(usuario);
							}
						}
						diferenciaContactos = new ArrayList<String>(usuariosAntiguos);
						diferenciaContactos.removeAll(usuariosConectados);
						if(!diferenciaContactos.isEmpty()) {
							for (String usuario : diferenciaContactos) {
								if(client.getChatsActivos().containsKey(usuario)) {
									client.getChatsActivos().get(usuario).getChat().append(usuario + " disconnected \n");
								}
								usuariosAntiguos.remove(usuario);
							}
						}
						client.getUsuario().setListaDeConectados(usuariosConectados);
						actualizarLista(client);
						break;

					case Mode.PRIVATE:
						
						client.setConjuntoMensaje((ConjuntoMensaje) gson.fromJson(objetoLeido, ConjuntoMensaje.class));
						
						if(!(client.getChatsActivos().containsKey(client.getConjuntoMensaje().getUserEmisor()))) {	
							chat = new Chat(client);
							
							chat.setTitle(client.getConjuntoMensaje().getUserEmisor());
							chat.setVisible(true);
							
							client.getChatsActivos().put(client.getConjuntoMensaje().getUserEmisor(), chat);
						}
						client.getChatsActivos().get(client.getConjuntoMensaje().getUserEmisor()).getChat().append(client.getConjuntoMensaje().getUserEmisor() + ": "  + client.getConjuntoMensaje().getMensaje() + "\n");
						client.getChatsActivos().get(client.getConjuntoMensaje().getUserEmisor()).getTexto().grabFocus();
						break;
						
					case Mode.BROADCAST:
						
						client.setConjuntoMensaje((ConjuntoMensaje) gson.fromJson(objetoLeido, ConjuntoMensaje.class));
						if(!client.getChatsActivos().containsKey("Room")) {	
							chat = new Chat(client);
							
							chat.setTitle("Room");
							chat.setVisible(true);
							
							client.getChatsActivos().put("Room", chat);
							Main.getBotonBroadcast().setEnabled(false);
						}
						client.getChatsActivos().get("Room").getChat().append(client.getConjuntoMensaje().getUserEmisor() + ": "  + client.getConjuntoMensaje().getMensaje() + "\n");
						client.getChatsActivos().get("Room").getTexto().grabFocus();
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}