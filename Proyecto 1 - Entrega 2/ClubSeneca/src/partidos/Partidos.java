package partidos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import amonestaciones.Amonestacion;
import amonestaciones.AmonestacionFutbol;
import competencia.Competencia;
import competencia.EquipoAfueraCompetencia;
import competencia.EquipoCompetencia;
import competencia.EquipoPropioCompetencia;
import equipos.Equipo;
import excepciones.ElementoNoEncontradoException;
import excepciones.EquiposIncompatiblesException;
import excepciones.JugadorNoInscritoException;
import excepciones.JugadorSuspendidoException;
import excepciones.MarcadorInvalidoException;
import excepciones.MinimoJugadoresException;
import jugadores.CentralJugadores;
import jugadores.Jugador;
import reservas.Reserva;

public class Partidos {

	private static final int MAXIMO_AMARILLAS_INTERNO = 2;

	private HashMap<String, Partido> partidos;
	private CentralJugadores jugadores;
	private int contador;

	public Partidos(CentralJugadores jugadores) {
		this.partidos = new HashMap<>();
		this.jugadores = jugadores;
	}

	public PartidoInterno programarPartidoInterno(Equipo equipo1, Equipo equipo2, LocalDateTime fecha, String[] habilitados1,
			String[] habilitados2) throws EquiposIncompatiblesException, MinimoJugadoresException, JugadorNoInscritoException,
			JugadorSuspendidoException {
		if (equipo1 == equipo2 || !equipo1.getDeporte().equalsIgnoreCase(equipo2.getDeporte())
				|| !equipo1.getCategoria().equalsIgnoreCase(equipo2.getCategoria())) {
			throw new EquiposIncompatiblesException("Los equipos deben ser distintos y del mismo deporte y categoría");
		}
		verificarJugadoresHabilitados(equipo1, habilitados1);
		verificarJugadoresHabilitados(equipo2, habilitados2);
		if (!verificarMinimoJugadores(equipo1.getDeporte(), habilitados1)
				|| !verificarMinimoJugadores(equipo2.getDeporte(), habilitados2)) {
			throw new MinimoJugadoresException("No se alcanza el mínimo de jugadores habilitados (7 fútbol, 5 baloncesto, 6 voleibol)");
		}
		PartidoInterno partido = new PartidoInterno(generarId(), equipo1, equipo2, fecha, habilitados1, habilitados2);
		agregarPartido(partido);
		return partido;
	}

	public PartidoCompetencia crearPartidoCompetencia(Competencia competencia, LocalDateTime fecha,
			EquipoAfueraCompetencia equipoAfuera, String escenario, String[] habilitados)
			throws MinimoJugadoresException, JugadorNoInscritoException, JugadorSuspendidoException {
		EquipoPropioCompetencia equipoPropio = null;
		for (EquipoCompetencia equipo : competencia.getEquiposParticipantes()) {
			if (equipo instanceof EquipoPropioCompetencia propio
					&& Arrays.stream(habilitados).allMatch(id -> propio.estaInscrito(id))) {
				equipoPropio = propio;
			}
		}
		for (String idJugador : habilitados) {
			if (equipoPropio == null || !competencia.jugadorPuedeJugar(idJugador)) {
				throw new JugadorNoInscritoException("El jugador " + idJugador + " no está en la nómina de la competencia");
			}
			verificarNoSuspendido(idJugador);
		}
		if (!verificarMinimoJugadores(competencia.getDeporte(), habilitados)) {
			throw new MinimoJugadoresException("No se alcanza el mínimo de jugadores habilitados");
		}
		PartidoCompetencia partido = new PartidoCompetencia(generarId(), competencia.getIdCompetencia(), fecha,
				competencia.getDeporte(), competencia.getCategoria(), equipoPropio, equipoAfuera, escenario, habilitados,
				competencia.getEquivalenciaSuspension());
		agregarPartido(partido);
		return partido;
	}

	public PartidoPractica crearPartidoPractica(String reservador, Reserva reserva, String[] equipo1, String[] equipo2,
			String disciplina) {
		PartidoPractica partido = new PartidoPractica(generarId(), reservador, reserva, equipo1, equipo2, disciplina);
		agregarPartido(partido);
		return partido;
	}

	public void agregarPartido(Partido partido) {
		partidos.put(partido.getIdPartido(), partido);
	}

	public Partido getPartido(String idPartido) {
		return partidos.get(idPartido);
	}

	public List<Partido> getPartidos() {
		return new ArrayList<>(partidos.values());
	}

	public List<Partido> getPartidosEntre(LocalDate inicio, LocalDate fin) {
		List<Partido> lista = new ArrayList<>();
		for (Partido partido : partidos.values()) {
			LocalDate dia = partido.getFecha().toLocalDate();
			if (!dia.isBefore(inicio) && !dia.isAfter(fin)) {
				lista.add(partido);
			}
		}
		lista.sort(Comparator.comparing(Partido::getFecha));
		return lista;
	}

	public void cancelarPartidoPorReserva(String idReserva) {
		for (Partido partido : getPartidos()) {
			if (partido instanceof PartidoPractica practica && practica.getReserva() != null
					&& practica.getReserva().getIdReserva().equals(idReserva)) {
				practica.cancelar();
				partidos.remove(practica.getIdPartido());
			}
		}
	}

	public void reemplazarEquipoEnHistorial(String idEquipo, Equipo anonimo) {
		for (Partido partido : partidos.values()) {
			if (partido instanceof PartidoInterno interno) {
				interno.reemplazarEquipo(idEquipo, anonimo);
			}
		}
	}

	public void reemplazarJugadorEnHistorial(String idJugador, Jugador anonimo) {
		for (Partido partido : partidos.values()) {
			partido.reemplazarJugador(idJugador, anonimo.getIdJugador());
		}
	}

	private Boolean verificarMinimoJugadores(String deporte, String[] habilitados) {
		String nombre = deporte.toLowerCase();
		int minimo = nombre.matches("futbol") ? 7 : nombre.equals("baloncesto") ? 5 : nombre.equals("voleibol") ? 6 : 1;
		return habilitados.length >= minimo;
	}

	public void registrarMarcador(String idPartido, int puntuacion1, int puntuacion2)
			throws MarcadorInvalidoException, ElementoNoEncontradoException {
		if (puntuacion1 < 0 || puntuacion2 < 0) {
			throw new MarcadorInvalidoException("El marcador no puede ser negativo");
		}
		Partido partido = buscarPartido(idPartido);
		if (partido.getCerrado()) {
			throw new MarcadorInvalidoException("El partido " + idPartido + " ya tiene marcador");
		}
		if (puntuacion1 == puntuacion2 && !partido.getDeporte().toLowerCase().matches("futbol")) {
			throw new MarcadorInvalidoException("En " + partido.getDeporte() + " no hay empates");
		}
		if (partido instanceof PartidoInterno interno) {
			interno.registrarMarcador(puntuacion1, puntuacion2);
			interno.setCerrado();
			cumplirSuspensiones(interno.getEquipo1().getJugadoresIds(), interno);
			cumplirSuspensiones(interno.getEquipo2().getJugadoresIds(), interno);
		} else if (partido instanceof PartidoCompetencia competencia) {
			competencia.registrarMarcador(puntuacion1, puntuacion2);
			competencia.setCerrado();
			cumplirSuspensiones(competencia.getEquipoPropio().getEquipo().getJugadoresIds(), competencia);
		} else {
			throw new MarcadorInvalidoException("El resultado de un partido de práctica se registra con estadísticas");
		}
	}

	private void cumplirSuspensiones(String[] idsJugadores, Partido partido) {
		for (String id : idsJugadores) {
			Jugador jugador = jugadores.getJugadorPorId(id);
			boolean amonestadoAqui = partido.getAmonestaciones().stream().anyMatch(a -> a.getIdJugador().equals(id));
			if (jugador != null && jugador.estaSuspendido() && !amonestadoAqui) {
				jugador.cumplirFechaSuspension();
			}
		}
	}

	public void registrarAmonestacion(String idPartido, Jugador jugador, Amonestacion amonestacion)
			throws ElementoNoEncontradoException {
		Partido partido = buscarPartido(idPartido);
		String idJugador = jugador.getIdJugador();
		if (partido instanceof PartidoCompetencia competencia && competencia.estaHabilitado(idJugador)) {
			competencia.agregarAmonestacion(idJugador, amonestacion);
		} else if (partido instanceof PartidoInterno interno && interno.estaHabilitado(idJugador)) {
			Equipo equipo = Arrays.asList(interno.getHabilitadosEquipo1()).contains(idJugador) ? interno.getEquipo1()
					: interno.getEquipo2();
			interno.agregarAmonestacion(idJugador, amonestacion);
			equipo.agregarAmonestacion(idJugador, amonestacion);
		} else {
			throw new ElementoNoEncontradoException("El jugador " + idJugador + " no estuvo habilitado en el partido " + idPartido);
		}
		jugador.aumentarAmonestaciones();
		aplicarSuspensiones(partido);
	}

	private Boolean verificarJugadoresHabilitados(Equipo equipo, String[] habilitados)
			throws JugadorNoInscritoException, JugadorSuspendidoException {
		for (String idJugador : habilitados) {
			if (!Arrays.asList(equipo.getJugadoresIds()).contains(idJugador)) {
				throw new JugadorNoInscritoException("El jugador " + idJugador + " no pertenece a " + equipo.getNombre());
			}
			verificarNoSuspendido(idJugador);
		}
		return true;
	}

	private void verificarNoSuspendido(String idJugador) throws JugadorSuspendidoException {
		Jugador jugador = jugadores.getJugadorPorId(idJugador);
		if (jugador != null && jugador.estaSuspendido()) {
			throw new JugadorSuspendidoException("El jugador " + idJugador + " está suspendido");
		}
	}

	private void aplicarSuspensiones(Partido partido) {
		List<Amonestacion> lista = partido.getAmonestaciones();
		Amonestacion ultima = lista.get(lista.size() - 1);
		String idJugador = ultima.getIdJugador();
		int maximo = MAXIMO_AMARILLAS_INTERNO;
		List<Amonestacion> acumuladas = lista.stream().filter(a -> a.getIdJugador().equals(idJugador)).toList();
		if (partido instanceof PartidoCompetencia competencia) {
			maximo = competencia.getMaximoAmarillas();
			acumuladas = competencia.getEquipoPropio().getAmonestaciones(idJugador);
		}
		boolean suspende = ultima.generaSuspension(maximo);
		if (ultima instanceof AmonestacionFutbol futbol && maximo > 0) {
			int total = acumuladas.stream().mapToInt(a -> a instanceof AmonestacionFutbol f ? f.getAmarillas() : 0).sum();
			int previas = total - futbol.getAmarillas();
			suspende = new AmonestacionFutbol(idJugador, total - (previas / maximo) * maximo, futbol.getRojas())
					.generaSuspension(maximo);
		}
		Jugador jugador = jugadores.getJugadorPorId(idJugador);
		if (suspende && jugador != null) {
			jugador.suspender(1);
		}
	}

	private Partido buscarPartido(String idPartido) throws ElementoNoEncontradoException {
		Partido partido = getPartido(idPartido);
		if (partido == null) {
			throw new ElementoNoEncontradoException("No existe el partido " + idPartido);
		}
		return partido;
	}

	private String generarId() {
		String id;
		do {
			id = "PAR-" + (++contador);
		} while (partidos.containsKey(id));
		return id;
	}
}
