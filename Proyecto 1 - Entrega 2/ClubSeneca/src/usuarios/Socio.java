package usuarios;

import java.util.Arrays;

public class Socio extends Usuario {

	private String nombre;
	private String edad;
	private String codigoDescuentoDado;
	private float puntosFidelidad;
	private String[] jugandoComo;

	public Socio(String username, String password, String nombre, String edad) {
		super(username, password);
		this.nombre = nombre;
		this.edad = edad;
		this.puntosFidelidad = 0;
		this.jugandoComo = new String[0];
	}

	public float getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public void setCodigoDescuento(String codigo) {
		this.codigoDescuentoDado = codigo;
	}

	public String getCodigoDescuentoDado() {
		return codigoDescuentoDado;
	}

	public void setPuntosFidelidad(float puntos) {
		this.puntosFidelidad = puntos;
	}

	public String getNombre() {
		return nombre;
	}

	public String getEdad() {
		return edad;
	}

	public String[] getJugandoComo() {
		return jugandoComo;
	}

	public void agregarJugador(String idJugador) {

		if (idJugador == null || idJugador.isBlank()) {
			throw new IllegalArgumentException(
					"El ID del jugador no es valido");
		}

		for (String id : jugandoComo) {
			if (idJugador.equals(id)) {
				return;
			}
		}

		jugandoComo = Arrays.copyOf(
				jugandoComo, jugandoComo.length + 1);

		jugandoComo[jugandoComo.length - 1] = idJugador;
	}

	public void quitarJugador(String idJugador) {
		jugandoComo = Arrays.stream(jugandoComo).filter(id -> !id.equals(idJugador)).toArray(String[]::new);
	}

	@Override
	public String getTipo() {
		return "Socio";
	}
}
