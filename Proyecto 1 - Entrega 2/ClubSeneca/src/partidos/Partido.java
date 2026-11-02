package partidos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import amonestaciones.Amonestacion;
import estadisticas.Estadistica;

public abstract class Partido {

	protected String idPartido;
	protected LocalDateTime fecha;
	protected String deporte;
	protected String categoria;
	protected Boolean cerrado;
	protected List<Estadistica> estadisticas;
	protected List<Amonestacion> amonestaciones;

	public Partido(String idPartido, LocalDateTime fecha, String deporte, String categoria) {
		this.idPartido = idPartido;
		this.fecha = fecha;
		this.deporte = deporte;
		this.categoria = categoria;
		this.cerrado = false;
		this.estadisticas = new ArrayList<>();
		this.amonestaciones = new ArrayList<>();
	}

	public String getIdPartido() {
		return idPartido;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public String getDeporte() {
		return deporte;
	}

	public String getCategoria() {
		return categoria;
	}

	public Boolean getCerrado() {
		return cerrado;
	}

	public void setCerrado() {
		cerrado = true;
	}

	public void agregarEstadistica(Estadistica estadistica) {
		estadisticas.add(estadistica);
	}

	public List<Estadistica> getEstadisticas() {
		return estadisticas;
	}

	public void agregarAmonestacion(String jugadorId, Amonestacion amonestacion) {
		amonestaciones.add(amonestacion);
	}

	public List<Amonestacion> getAmonestaciones() {
		return amonestaciones;
	}

	public void reemplazarJugador(String idJugador, String idAnonimo) {
		for (Estadistica estadistica : estadisticas) {
			if (idJugador.equals(estadistica.getUsuarioUsername())) {
				estadistica.setUsuarioUsername(idAnonimo);
			}
		}
	}

	protected static String[] reemplazar(String[] ids, String id, String nuevo) {
		String[] resultado = ids.clone();
		for (int i = 0; i < resultado.length; i++) {
			if (resultado[i].equals(id)) {
				resultado[i] = nuevo;
			}
		}
		return resultado;
	}

	protected String getEncabezado() {
		return fecha.toLocalDate() + " " + fecha.toLocalTime() + " | " + getClass().getSimpleName() + " " + idPartido + " | "
				+ deporte + " | " + categoria + (cerrado ? "" : " | sin cerrar");
	}

	protected String getDetalle() {
		StringBuilder texto = new StringBuilder();
		for (Estadistica estadistica : estadisticas) {
			texto.append("\n    ").append(estadistica.getUsuarioUsername()).append(": ").append(estadistica);
		}
		for (Amonestacion amonestacion : amonestaciones) {
			texto.append("\n    amonestación ").append(amonestacion.getIdJugador()).append(": ")
					.append(amonestacion.getDescripcion());
		}
		return texto.toString();
	}

	public String getResumen() {
		return getEncabezado() + getDetalle();
	}
}
