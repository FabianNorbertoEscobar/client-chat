package client;

import java.io.Serializable;
import java.util.List;

public class Usuario extends Conjunto implements Serializable, Cloneable {

	private String username;
	private boolean inicioSesion;
	private boolean estado;
	private List<String> listaDeConectados;

	public Usuario(){
		estado = true;
	}

	public List<String> getListaDeConectados() {
		return listaDeConectados;
	}

	public void setListaDeConectados(List<String> listaDeConectados) {
		this.listaDeConectados = listaDeConectados;
	}

	public Usuario(String user){
		username = user;
		inicioSesion = false;
		estado = true;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean isInicioSesion() {
		return inicioSesion;
	}

	public void setInicioSesion(boolean inicioSesion) {
		this.inicioSesion = inicioSesion;
	}
	
	public boolean getEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	
	public Object clone() {
		Object obj = null;
		obj = super.clone();
		return obj;
	}	
}