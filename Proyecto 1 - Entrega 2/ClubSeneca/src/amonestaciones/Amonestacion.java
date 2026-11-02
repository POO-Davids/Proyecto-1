package amonestaciones;

public abstract class Amonestacion {

	protected String descripcion;
	protected String idJugador;

	public Amonestacion(String idJugador, String descripcion) {
		this.idJugador = idJugador;
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getIdJugador() {
		return idJugador;
	}

	public abstract Boolean generaSuspension(int maximoAmarillas);
}
