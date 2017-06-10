package client;

import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import com.google.gson.Gson;

import client.MiChat;
import client.VentanaContactos;
import client.Comando;
import client.Paquete;
import client.PaqueteDeUsuarios;
import client.PaqueteMensaje;

public class EscuchaServer extends Thread {

	private Cliente cliente;
	private ObjectInputStream entrada;
	private final Gson gson = new Gson();
	private MiChat chat;

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
				
					case Comando.INICIOSESION:
						cliente.getPaqueteUsuario().setMensaje(paquete.getMensaje());
						
						if(paquete.getMensaje().equals(Paquete.msjFracaso)) {
							JOptionPane.showMessageDialog(null, "Usuario existente, por favor logee con otro usuario.");
							this.stop();
						} else {
							usuariosConectados = (ArrayList<String>) gson.fromJson(objetoLeido, PaqueteDeUsuarios.class).getPersonajes();							
						}
						break;
						
					// CONEXION = SE CONECTO OTRO USUARIO, ENTONCES LE MANDO LA LISTA
					// A TODOS LOS USUARIOS ANTERIORES A EL
						
					case Comando.CONEXION:
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
									cliente.getChatsActivos().get(usuario).getChat().append("El usuario: " + usuario + " se ha desconectado\n");
								}
								usuariosAntiguos.remove(usuario);
							}
						}
						cliente.getPaqueteUsuario().setListaDeConectados(usuariosConectados);
						actualizarLista(cliente);
						break;
					
					// ACA RECIBI EL MENSAJE DEL OTRO CLIENTE
					case Comando.TALK:
						
						cliente.setPaqueteMensaje((PaqueteMensaje) gson.fromJson(objetoLeido, PaqueteMensaje.class));
						
						if(!(cliente.getChatsActivos().containsKey(cliente.getPaqueteMensaje().getUserEmisor()))) {	
							chat = new MiChat(cliente);
							
							chat.setTitle(cliente.getPaqueteMensaje().getUserEmisor());
							chat.setVisible(true);
							
							cliente.getChatsActivos().put(cliente.getPaqueteMensaje().getUserEmisor(), chat);
						}
						cliente.getChatsActivos().get(cliente.getPaqueteMensaje().getUserEmisor()).getChat().append(cliente.getPaqueteMensaje().getUserEmisor() + ": "  + cliente.getPaqueteMensaje().getMensaje() + "\n");
						cliente.getChatsActivos().get(cliente.getPaqueteMensaje().getUserEmisor()).getTexto().grabFocus();
						break;
						
					case Comando.CHATALL:
						
						cliente.setPaqueteMensaje((PaqueteMensaje) gson.fromJson(objetoLeido, PaqueteMensaje.class));
						if(!cliente.getChatsActivos().containsKey("Sala")) {	
							chat = new MiChat(cliente);
							
							chat.setTitle("Sala");
							chat.setVisible(true);
							
							cliente.getChatsActivos().put("Sala", chat);
							VentanaContactos.getBotonMc().setEnabled(false);
						}
						cliente.getChatsActivos().get("Sala").getChat().append(cliente.getPaqueteMensaje().getUserEmisor() + ": "  + cliente.getPaqueteMensaje().getMensaje() + "\n");
						cliente.getChatsActivos().get("Sala").getTexto().grabFocus();
						break;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fallo la conexión con el servidor.");
			e.printStackTrace();
		}
	}

	private void actualizarLista(final Cliente cliente) {
		DefaultListModel<String> modelo = new DefaultListModel<String>();
		synchronized (cliente) {
			try {
				cliente.wait(300);
				VentanaContactos.getList().removeAll();
				if (cliente.getPaqueteUsuario().getListaDeConectados() != null) {
					cliente.getPaqueteUsuario().getListaDeConectados().remove(cliente.getPaqueteUsuario().getUsername());
					for (String cad : cliente.getPaqueteUsuario().getListaDeConectados()) {
						modelo.addElement(cad);
					}
					VentanaContactos.getLblNumeroConectados().setText(String.valueOf(modelo.getSize()));
					VentanaContactos.getList().setModel(modelo);
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