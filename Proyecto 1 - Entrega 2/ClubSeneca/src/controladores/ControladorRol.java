package controladores;

import excepciones.PersistenciaException;

public abstract class ControladorRol {

	protected CentralClubSeneca central;

	public ControladorRol(CentralClubSeneca central) {
		this.central = central;
	}

	public abstract String getRol();

	public void cerrarSesion() throws PersistenciaException {
		central.cerrarSesion();
	}
}
