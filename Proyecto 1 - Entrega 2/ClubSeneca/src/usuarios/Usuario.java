package usuarios;

import java.util.ArrayList;
import java.util.List;

import excepciones.ElementoNoEncontradoException;
import tiendas.Bebida;

public abstract class Usuario {

	protected String username;
	protected String password;
	protected List<Bebida> bebidasSinConsumir;

	public Usuario(String username, String password) {
		this.username = username;
		this.password = password;
		this.bebidasSinConsumir = new ArrayList<>();
	}

	public Boolean verificarLogin(String username, String password) {
		return this.username.equals(username) && this.password.equals(password);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public abstract String getTipo();

	public void agregarBebidaSinConsumir(Bebida bebida) {
		bebidasSinConsumir.add(bebida);
	}

	public void consumirBebida(String nombre) throws ElementoNoEncontradoException {
		for (Bebida bebida : bebidasSinConsumir) {
			if (bebida.getNombre().equalsIgnoreCase(nombre)) {
				bebidasSinConsumir.remove(bebida);
				return;
			}
		}
		throw new ElementoNoEncontradoException("No tiene la bebida " + nombre + " sin consumir");
	}

	public Boolean tieneBebidaCaliente() {
		for (Bebida bebida : bebidasSinConsumir) {
			if (bebida.getEsCaliente()) {
				return true;
			}
		}
		return false;
	}

	public List<Bebida> getBebidasSinConsumir() {
		return bebidasSinConsumir;
	}
}
