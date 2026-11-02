package usuarios;

import java.time.LocalDate;
import java.util.Arrays;

public abstract class Empleado extends Usuario {

	protected String codigoDescuentoTienda;
	protected Boolean[] turno;
	protected float puntosFidelidad;
	protected String[] sociosConCodigo;

	public Empleado(String username, String password, String codigoDescuentoTienda) {
		super(username, password);
		this.codigoDescuentoTienda = codigoDescuentoTienda;
		this.turno = new Boolean[24];
		Arrays.fill(turno, false);
		this.puntosFidelidad = 0;
		this.sociosConCodigo = new String[0];
	}

	public float getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public void setPuntosFidelidad(float puntos) {
		this.puntosFidelidad = puntos;
	}

	public Boolean[] getTurno() {
		return turno;
	}

	public void setTurno(LocalDate fecha, Boolean[] turno) {
		this.turno = Arrays.copyOf(turno, 24);
	}

	public String getCodigoDescuentoTienda() {
		return codigoDescuentoTienda;
	}

	public void agregarSocioConCodigo(String socioUsername) {
		if (Arrays.asList(sociosConCodigo).contains(socioUsername)) {
			return;
		}
		sociosConCodigo = Arrays.copyOf(sociosConCodigo, sociosConCodigo.length + 1);
		sociosConCodigo[sociosConCodigo.length - 1] = socioUsername;
	}

	public String[] getSociosConCodigo() {
		return sociosConCodigo;
	}
}
