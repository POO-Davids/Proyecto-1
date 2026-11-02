package jugadores;

import java.util.ArrayList;
import java.util.List;

import entrenamientos.AsistenciaJugador;
import estadisticas.Estadistica;
import usuarios.Entrenador;
import usuarios.Usuario;

public abstract class Jugador {

	protected String idJugador;
	protected Boolean esEntrenador;
	protected int partidosSuspendido;
	protected int amonestaciones;
	protected Estadistica estadisticaAcumulada;
	protected Usuario usuario;
	protected List<AsistenciaJugador> asistencia;

	public Jugador(String idJugador, Usuario usuario) {
		this.idJugador = idJugador;
		this.usuario = usuario;
		this.esEntrenador = usuario instanceof Entrenador;
		this.partidosSuspendido = 0;
		this.asistencia = new ArrayList<>();
	}

	public String getIdJugador() {
		return idJugador;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Boolean getEsEntrenador() {
		return esEntrenador;
	}

	public abstract String getDeporte();

	public Estadistica getEstadisticaAcumulada() {
		return estadisticaAcumulada;
	}

	public void acumularEstadistica(Estadistica estadistica) {
		estadisticaAcumulada.sumar(estadistica);
	}

	public void aumentarAmonestaciones() {
		amonestaciones++;
	}

	public int getAmonestaciones() {
		return amonestaciones;
	}

	public void agregarAsistencia(AsistenciaJugador asistencia) {
		this.asistencia.add(asistencia);
	}

	public List<AsistenciaJugador> getAsistencias() {
		return asistencia;
	}

	public void suspender(int partidos) {
		partidosSuspendido += partidos;
	}

	public Boolean estaSuspendido() {
		return partidosSuspendido > 0;
	}

	public int getPartidosSuspendido() {
		return partidosSuspendido;
	}

	public void cumplirFechaSuspension() {
		if (partidosSuspendido > 0) {
			partidosSuspendido--;
		}
	}
}
