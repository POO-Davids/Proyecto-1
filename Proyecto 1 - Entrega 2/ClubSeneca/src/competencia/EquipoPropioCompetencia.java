package competencia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import amonestaciones.Amonestacion;
import equipos.Equipo;

public class EquipoPropioCompetencia extends EquipoCompetencia {

	private String[] jugadoresInscritos;
	private String equipoId;
	private Equipo equipo;
	private HashMap<String, List<Amonestacion>> amonestacionesAcumuladasJugadoresCompetencia;
	private String jugadorMasPuntos;
	private int puntosJugadorMasPuntos;

	public EquipoPropioCompetencia(Equipo equipo, String[] nomina) {
		super(equipo.getIdEquipo());
		this.equipo = equipo;
		this.equipoId = equipo.getIdEquipo();
		this.jugadoresInscritos = new String[0];
		this.amonestacionesAcumuladasJugadoresCompetencia = new HashMap<>();
		for (String idJugador : nomina) {
			inscribirJugador(idJugador);
		}
	}

	public void inscribirJugador(String idJugador) {
		if (estaInscrito(idJugador)) {
			return;
		}
		jugadoresInscritos = Arrays.copyOf(jugadoresInscritos, jugadoresInscritos.length + 1);
		jugadoresInscritos[jugadoresInscritos.length - 1] = idJugador;
	}

	public Boolean estaInscrito(String idJugador) {
		return Arrays.asList(jugadoresInscritos).contains(idJugador);
	}

	public void agregarAmonestacion(String idJugador, Amonestacion amonestacion) {
		amonestacionesAcumuladasJugadoresCompetencia.computeIfAbsent(idJugador, id -> new ArrayList<>()).add(amonestacion);
	}

	public List<Amonestacion> getAmonestaciones(String idJugador) {
		return amonestacionesAcumuladasJugadoresCompetencia.getOrDefault(idJugador, new ArrayList<>());
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public String getEquipoId() {
		return equipoId;
	}

	public String[] getJugadoresInscritos() {
		return jugadoresInscritos;
	}

	void registrarPuntosJugador(String idJugador, int puntos) {
		if (jugadorMasPuntos == null || puntos > puntosJugadorMasPuntos) {
			jugadorMasPuntos = idJugador;
			puntosJugadorMasPuntos = puntos;
		}
	}

	@Override
	public String getNombre() {
		return equipo.getNombre();
	}

	@Override
	public String getJugadorMasPuntos() {
		return jugadorMasPuntos;
	}

	@Override
	public int getNumHabilitados() {
		return jugadoresInscritos.length;
	}

	@Override
	public int getNumInscritos() {
		return jugadoresInscritos.length;
	}
}
