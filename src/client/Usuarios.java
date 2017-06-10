package client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Usuarios extends Paquete implements Serializable, Cloneable {

		private static final long serialVersionUID = 1L;

		private ArrayList<String> usuarios;
		private Map<String, Usuario> personajesConectados;

		public Usuarios(){
		}

		public Map<String, Usuario> getPersonajesConectados() {
			return personajesConectados;
		}

		public Usuarios(ArrayList<String> usuarios){
			this.usuarios = usuarios;
		}

		public ArrayList<String> getPersonajes(){
			return usuarios;
		}

		@Override
		public Object clone() {
			Object obj = null;
			obj = super.clone();
			return obj;
		}
}