package client;

import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import com.google.gson.Gson;

import client.Chat;
import client.Main;
import client.Comando;
import client.Paquete;
import client.PaqueteDeUsuarios;
import client.PaqueteMensaje;

public class EscuchaServer extends Thread {

	private Cliente cliente;
	private ObjectInputStream entrada;
	private final Gson gson = new Gson();
	private Chat chat;

	protected static ArrayList<String> usuariosConectados = new ArrayList<String>();

	public EscuchaServer(final Cliente cliente) {
		this.cliente = cliente;
		this.entrada = cliente.getEntrada();
	}

	@Override
	public void run() {
		try {
			Paquete paquete;
			ArrayList<String> usuariosAntiguos = new ArrayList<String>();
			ArrayList<String> diferenciaContactos = new ArrayList<String>();

			String objetoLeido;
			while (true) {

				synchronized (entrada) {
					objetoLeido = (String) entrada.readObject();	
				}
				paquete = gson.fromJson(objetoLeido, Paquete.class);

				switch (paquete.getComando()) {
				
					case Comando.LOGIN:
						cliente.getPaqueteUsuario().setMensaje(paquete.getMensaje());
						
						if(paquete.getMensaje().equals(Paquete.msjFracaso)) {
							this.stop();
						} else {
							usuariosConectados = (ArrayList<String>) gson.fromJson(objetoLeido, PaqueteDeUsuarios.class).getPersonajes();							
						}
						break;
						
					case Comando.CONNECT:
						usuariosConectados = (ArrayList<String>) gson.fromJson(objetoLeido, PaqueteDeUsuarios.class).getPersonajes();
						for (String usuario : usuariosConectados) {
							if(!usuariosAntiguos.contains(usuario)) {
								usuariosAntiguos.add(usuario);
							}
						}
						diferenciaContactos = new ArrayList<String>(usuariosAntiguos);
						diferenciaContactos.removeAll(usuariosConectados);
						if(!diferenciaContactos.isEmpty()) {
							for (String usuario : diferenciaContactos) {
								if(cliente.getChatsActivos().containsKey(usuario)) {
									cliente.getChatsActivos().get(usuario).getChat().append(usuario + " disconnected \n");
								}
								usuariosAntiguos.remove(usuario);
							}
						}
						cliente.getPaqueteUsuario().setListaDeConectados(usuariosConectados);
						actualizarLista(cliente);
						break;

					case Comando.PRIVATE:
						
						cliente.setPaqueteMensaje((PaqueteMensaje) gson.fromJson(objetoLeido, PaqueteMensaje.class));
						
						if(!(cliente.getChatsActivos().containsKey(cliente.getPaqueteMensaje().getUserEmisor()))) {	
							chat = new Chat(cliente);
							
							chat.setTitle(cliente.getPaqueteMensaje().getUserEmisor());
							chat.setVisible(true);
							
							cliente.getChatsActivos().put(cliente.getPaqueteMensaje().getUserEmisor(), chat);
						}
						cliente.getChatsActivos().get(cliente.getPaqueteMensaje().getUserEmisor()).getChat().append(cliente.getPaqueteMensaje().getUserEmisor() + ": "  + cliente.getPaqueteMensaje().getMensaje() + "\n");
						cliente.getChatsActivos().get(cliente.getPaqueteMensaje().getUserEmisor()).getTexto().grabFocus();
						break;
						
					case Comando.BROADCAST:
						
						cliente.setPaqueteMensaje((PaqueteMensaje) gson.fromJson(objetoLeido, PaqueteMensaje.class));
						if(!cliente.getChatsActivos().containsKey("Room")) {	
							chat = new Chat(cliente);
							
							chat.setTitle("Sala");
							chat.setVisible(true);
							
							cliente.getChatsActivos().put("Room", chat);
							Main.getBotonMc().setEnabled(false);
						}
						cliente.getChatsActivos().get("Room").getChat().append(cliente.getPaqueteMensaje().getUserEmisor() + ": "  + cliente.getPaqueteMensaje().getMensaje() + "\n");
						cliente.getChatsActivos().get("Room").getTexto().grabFocus();
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void actualizarLista(final Cliente cliente) {
		DefaultListModel<String> modelo = new DefaultListModel<String>();
		synchronized (cliente) {
			try {
				cliente.wait(300);
				Main.getList().removeAll();
				if (cliente.getPaqueteUsuario().getListaDeConectados() != null) {
					cliente.getPaqueteUsuario().getListaDeConectados().remove(cliente.getPaqueteUsuario().getUsername());
					for (String cad : cliente.getPaqueteUsuario().getListaDeConectados()) {
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
}