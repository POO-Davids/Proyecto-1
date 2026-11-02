package partidos;

import java.time.LocalDateTime;
import java.util.Arrays;

import amonestaciones.Amonestacion;
import competencia.EquipoAfueraCompetencia;
import competencia.EquipoPropioCompetencia;

public class PartidoCompetencia extends Partido {

	private int puntuacionEquipoPropio;
	private int puntuacionEquipoAfuera;
	private String competenciaId;
	private String escenario;
	private String[] jugadoresHabilitados;
	private int maximoAmarillas;
	private EquipoAfueraCompetencia equipoAfuera;
	private EquipoPropioCompetencia equipoPropio;

	public PartidoCompetencia(String idPartido, String competenciaId, LocalDateTime fecha, String deporte, String categoria,
			EquipoPropioCompetencia equipoPropio, EquipoAfueraCompetencia equipoAfuera, String escenario,
			String[] jugadoresHabilitados, int maximoAmarillas) {
		super(idPartido, fecha, deporte, categoria);
		this.competenciaId = competenciaId;
		this.equipoPropio = equipoPropio;
		this.equipoAfuera = equipoAfuera;
		this.escenario = escenario;
		this.jugadoresHabilitados = jugadoresHabilitados;
		this.maximoAmarillas = maximoAmarillas;
	}

	public void registrarMarcador(int propio, int afuera) {
		this.puntuacionEquipoPropio = propio;
		this.puntuacionEquipoAfuera = afuera;
	}

	@Override
	public void agregarAmonestacion(String jugadorId, Amonestacion amonestacion) {
		super.agregarAmonestacion(jugadorId, amonestacion);
		equipoPropio.agregarAmonestacion(jugadorId, amonestacion);
	}

	public int getPuntuacionEquipoPropio() {
		return puntuacionEquipoPropio;
	}

	public int getPuntuacionEquipoAfuera() {
		return puntuacionEquipoAfuera;
	}

	public String getCompetenciaId() {
		return competenciaId;
	}

	public String getEscenario() {
		return escenario;
	}

	public String[] getJugadoresHabilitados() {
		return jugadoresHabilitados;
	}

	public int getMaximoAmarillas() {
		return maximoAmarillas;
	}

	public EquipoAfueraCompetencia getEquipoAfuera() {
		return equipoAfuera;
	}

	public EquipoPropioCompetencia getEquipoPropio() {
		return equipoPropio;
	}

	public Boolean estaHabilitado(String idJugador) {
		return Arrays.asList(jugadoresHabilitados).contains(idJugador);
	}

	@Override
	public void reemplazarJugador(String idJugador, String idAnonimo) {
		super.reemplazarJugador(idJugador, idAnonimo);
		jugadoresHabilitados = reemplazar(jugadoresHabilitados, idJugador, idAnonimo);
	}

	@Override
	public String getResumen() {
		return getEncabezado() + " | competencia " + competenciaId + " | " + equipoPropio.getNombre() + " "
				+ puntuacionEquipoPropio + " - " + puntuacionEquipoAfuera + " " + equipoAfuera.getNombre() + " (rival) | escenario: "
				+ escenario + getDetalle();
	}
}
