package partidos;

import java.time.LocalDateTime;
import java.util.Arrays;

import equipos.Equipo;

public class PartidoInterno extends Partido {

	private int puntuacionEquipo1;
	private int puntuacionEquipo2;
	private String[] habilitadosEquipo1;
	private String[] habilitadosEquipo2;
	private Equipo equipo1;
	private Equipo equipo2;

	public PartidoInterno(String idPartido, Equipo equipo1, Equipo equipo2, LocalDateTime fecha, String[] habilitados1,
			String[] habilitados2) {
		super(idPartido, fecha, equipo1.getDeporte(), equipo1.getCategoria());
		this.equipo1 = equipo1;
		this.equipo2 = equipo2;
		this.habilitadosEquipo1 = habilitados1;
		this.habilitadosEquipo2 = habilitados2;
	}

	public PartidoInterno(String idPartido, Equipo equipo1, Equipo equipo2, LocalDateTime fecha, String deporte, String categoria,
			String[] habilitados1, String[] habilitados2) {
		super(idPartido, fecha, deporte, categoria);
		this.equipo1 = equipo1;
		this.equipo2 = equipo2;
		this.habilitadosEquipo1 = habilitados1;
		this.habilitadosEquipo2 = habilitados2;
	}

	public void registrarMarcador(int puntuacion1, int puntuacion2) {
		this.puntuacionEquipo1 = puntuacion1;
		this.puntuacionEquipo2 = puntuacion2;
	}

	public int getPuntuacionEquipo1() {
		return puntuacionEquipo1;
	}

	public int getPuntuacionEquipo2() {
		return puntuacionEquipo2;
	}

	public Equipo getEquipo1() {
		return equipo1;
	}

	public Equipo getEquipo2() {
		return equipo2;
	}

	public String[] getHabilitadosEquipo1() {
		return habilitadosEquipo1;
	}

	public String[] getHabilitadosEquipo2() {
		return habilitadosEquipo2;
	}

	public Boolean estaHabilitado(String idJugador) {
		return Arrays.asList(habilitadosEquipo1).contains(idJugador) || Arrays.asList(habilitadosEquipo2).contains(idJugador);
	}

	public void reemplazarEquipo(String idEquipo, Equipo anonimo) {
		if (equipo1.getIdEquipo().equals(idEquipo)) {
			equipo1 = anonimo;
		}
		if (equipo2.getIdEquipo().equals(idEquipo)) {
			equipo2 = anonimo;
		}
	}

	@Override
	public void reemplazarJugador(String idJugador, String idAnonimo) {
		super.reemplazarJugador(idJugador, idAnonimo);
		habilitadosEquipo1 = reemplazar(habilitadosEquipo1, idJugador, idAnonimo);
		habilitadosEquipo2 = reemplazar(habilitadosEquipo2, idJugador, idAnonimo);
	}

	@Override
	public String getResumen() {
		return getEncabezado() + " | " + equipo1.getNombre() + " " + puntuacionEquipo1 + " - "
				+ puntuacionEquipo2 + " " + equipo2.getNombre() + getDetalle();
	}
}
