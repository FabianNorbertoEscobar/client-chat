package client;

import java.io.Serializable;

public class PaqueteMensaje extends Paquete implements Serializable, Cloneable {
	
		private String userEmisor;
		private String userReceptor;
		private String msj;

		public PaqueteMensaje(){
		}

		public String getMensaje() {
			return msj;
		}

		public void setMensaje(String mensaje) {
			this.msj = mensaje;
		}

		public String getUserEmisor() {
			return userEmisor;
		}

		public void setUserEmisor(String idEmisor) {
			this.userEmisor = idEmisor;
		}

		public String getUserReceptor() {
			return userReceptor;
		}

		public void setUserReceptor(String idReceptor){
			this.userReceptor = idReceptor;
		}
		
		public Object clone() {
			Object obj = null;
			obj = super.clone();
			return obj;
		}
}
