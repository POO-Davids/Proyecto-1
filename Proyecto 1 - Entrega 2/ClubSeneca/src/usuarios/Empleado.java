package usuarios;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.Arrays;

public abstract class Empleado extends Usuario {

	protected String codigoDescuentoTienda;
	protected Boolean[] turno;
	private HashMap<LocalDate, Boolean[]> turnosPorFecha;
	protected float puntosFidelidad;
	protected String[] sociosConCodigo;

	public Empleado(String username, String password, String codigoDescuentoTienda) {
		super(username, password);
		this.codigoDescuentoTienda = codigoDescuentoTienda;
		this.turno = new Boolean[24];
		Arrays.fill(turno, false);
		this.turnosPorFecha = new HashMap<>();
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
		return Arrays.copyOf(turno, 24);
	}

	public Boolean[] getTurno(LocalDate fecha) {

		if (fecha == null) {
			return getTurno();
		}

		Boolean[] horas = turnosPorFecha.get(fecha);

		if (horas == null) {
			return getTurno();
		}

		return Arrays.copyOf(horas, 24);
	}

	public void setTurno(LocalDate fecha, Boolean[] nuevoTurno) {

		if (nuevoTurno == null || nuevoTurno.length != 24) {
			throw new IllegalArgumentException(
					"Un turno debe tener 24 posiciones");
		}

		for (Boolean hora : nuevoTurno) {
			if (hora == null) {
				throw new IllegalArgumentException(
						"Las horas no pueden ser nulas");
			}
		}

		if (fecha == null) {
			this.turno = Arrays.copyOf(nuevoTurno, 24);
		} else {
			turnosPorFecha.put(
					fecha, Arrays.copyOf(nuevoTurno, 24));
		}
	}

	public void quitarTurnoDeFecha(LocalDate fecha) {
		turnosPorFecha.remove(fecha);
	}

	public HashMap<LocalDate, Boolean[]> getTurnosPorFecha() {

		HashMap<LocalDate, Boolean[]> copia = new HashMap<>();

		for (LocalDate fecha : turnosPorFecha.keySet()) {
			copia.put(fecha,
					Arrays.copyOf(turnosPorFecha.get(fecha), 24));
		}

		return copia;
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
