package persistencia;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import amonestaciones.Amonestacion;
import amonestaciones.AmonestacionFutbol;
import competencia.Competencia;
import competencia.EquipoAfueraCompetencia;
import competencia.EquipoPropioCompetencia;
import controladores.CentralClubSeneca;
import equipos.CentralEquipos;
import equipos.Equipo;
import estadisticas.Estadistica;
import excepciones.PersistenciaException;
import partidos.Partido;
import partidos.PartidoCompetencia;
import partidos.PartidoInterno;
import partidos.PartidoPractica;

public class PersistenciaPartidos extends Persistencia {

	private static final String ARCHIVO = "partidos.json";

	public PersistenciaPartidos() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONArray lista = new JSONArray();
		for (Partido partido : club.getPartidos().getPartidos()) {
			lista.put(partidoAJSON(partido));
		}
		escribirJSON(ruta, new JSONObject().put("partidos", lista));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			JSONArray lista = json.getJSONArray("partidos");
			for (int i = 0; i < lista.length(); i++) {
				club.getPartidos().agregarPartido(partidoDesdeJSON(lista.getJSONObject(i), club));
			}
		} catch (JSONException | ClassCastException | NullPointerException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONObject partidoAJSON(Partido partido) {
		JSONObject json = new JSONObject().put("tipo", partido.getClass().getSimpleName()).put("idPartido", partido.getIdPartido())
				.put("fecha", partido.getFecha().toString()).put("deporte", partido.getDeporte()).put("categoria", partido.getCategoria())
				.put("cerrado", partido.getCerrado());
		JSONArray estadisticas = new JSONArray();
		for (Estadistica estadistica : partido.getEstadisticas()) {
			estadisticas.put(PersistenciaJugadores.estadisticaAJSON(estadistica));
		}
		JSONArray amonestaciones = new JSONArray();
		for (Amonestacion amonestacion : partido.getAmonestaciones()) {
			amonestaciones.put(amonestacionAJSON(amonestacion));
		}
		json.put("estadisticas", estadisticas).put("amonestaciones", amonestaciones);
		if (partido instanceof PartidoInterno interno) {
			json.put("equipo1", interno.getEquipo1().getIdEquipo()).put("equipo2", interno.getEquipo2().getIdEquipo())
					.put("puntuacion1", interno.getPuntuacionEquipo1()).put("puntuacion2", interno.getPuntuacionEquipo2())
					.put("habilitados1", textosAJSON(interno.getHabilitadosEquipo1()))
					.put("habilitados2", textosAJSON(interno.getHabilitadosEquipo2()));
		} else if (partido instanceof PartidoCompetencia competencia) {
			json.put("competenciaId", competencia.getCompetenciaId())
					.put("equipoPropio", competencia.getEquipoPropio().getEquipoCompetenciaId())
					.put("equipoAfuera", competencia.getEquipoAfuera().getEquipoCompetenciaId()).put("escenario", competencia.getEscenario())
					.put("habilitados", textosAJSON(competencia.getJugadoresHabilitados()))
					.put("puntuacionPropio", competencia.getPuntuacionEquipoPropio())
					.put("puntuacionAfuera", competencia.getPuntuacionEquipoAfuera());
		} else if (partido instanceof PartidoPractica practica) {
			json.put("reservador", practica.getReservador()).put("reserva", practica.getReserva() == null ? "" : practica.getReserva().getIdReserva())
					.put("disciplina", practica.getDisciplina()).put("equipo1", textosAJSON(practica.getEquipo1()))
					.put("equipo2", textosAJSON(practica.getEquipo2()));
		}
		return json;
	}

	private Partido partidoDesdeJSON(JSONObject json, CentralClubSeneca club) {
		String id = json.getString("idPartido");
		LocalDateTime fecha = LocalDateTime.parse(json.getString("fecha"));
		boolean cerrado = json.getBoolean("cerrado");
		Partido partido;
		Competencia competencia = null;
		switch (json.getString("tipo")) {
		case "PartidoInterno" -> {
			CentralEquipos equipos = club.getEquipos();
			Equipo equipo1 = equipos.getEquipo(json.getString("equipo1"));
			Equipo equipo2 = equipos.getEquipo(json.getString("equipo2"));
			PartidoInterno interno = new PartidoInterno(id, equipo1 == null ? equipos.getEquipoAnonimo() : equipo1,
					equipo2 == null ? equipos.getEquipoAnonimo() : equipo2, fecha, json.getString("deporte"), json.getString("categoria"),
					textosDesdeJSON(json.getJSONArray("habilitados1")), textosDesdeJSON(json.getJSONArray("habilitados2")));
			if (cerrado) {
				interno.registrarMarcador(json.getInt("puntuacion1"), json.getInt("puntuacion2"));
			}
			partido = interno;
		}
		case "PartidoCompetencia" -> {
			competencia = club.getCompetencias().getCompetencia(json.getString("competenciaId"));
			PartidoCompetencia partidoCompetencia = new PartidoCompetencia(id, competencia.getIdCompetencia(), fecha,
					json.getString("deporte"), json.getString("categoria"),
					(EquipoPropioCompetencia) competencia.getEquipoParticipante(json.getString("equipoPropio")),
					(EquipoAfueraCompetencia) competencia.getEquipoParticipante(json.getString("equipoAfuera")),
					json.getString("escenario"), textosDesdeJSON(json.getJSONArray("habilitados")), competencia.getEquivalenciaSuspension());
			if (cerrado) {
				partidoCompetencia.registrarMarcador(json.getInt("puntuacionPropio"), json.getInt("puntuacionAfuera"));
			}
			competencia.agregarPartido(partidoCompetencia);
			partido = partidoCompetencia;
		}
		default -> partido = new PartidoPractica(id, fecha, json.getString("reservador"),
				club.getReservas().getReserva(json.getString("reserva")), textosDesdeJSON(json.getJSONArray("equipo1")),
				textosDesdeJSON(json.getJSONArray("equipo2")), json.getString("disciplina"));
		}
		if (cerrado) {
			partido.setCerrado();
		}
		JSONArray estadisticas = json.getJSONArray("estadisticas");
		for (int i = 0; i < estadisticas.length(); i++) {
			Estadistica estadistica = PersistenciaJugadores.estadisticaDesdeJSON(estadisticas.getJSONObject(i));
			partido.agregarEstadistica(estadistica);
			if (competencia != null) {
				competencia.acumularEstadistica(estadistica);
			}
		}
		JSONArray amonestaciones = json.getJSONArray("amonestaciones");
		for (int i = 0; i < amonestaciones.length(); i++) {
			Amonestacion amonestacion = amonestacionDesdeJSON(amonestaciones.getJSONObject(i));
			partido.agregarAmonestacion(amonestacion.getIdJugador(), amonestacion);
			if (partido instanceof PartidoInterno interno) {
				Equipo equipo = Arrays.asList(interno.getHabilitadosEquipo1()).contains(amonestacion.getIdJugador()) ? interno.getEquipo1()
						: interno.getEquipo2();
				equipo.agregarAmonestacion(amonestacion.getIdJugador(), amonestacion);
			}
		}
		return partido;
	}

	private JSONObject amonestacionAJSON(Amonestacion amonestacion) {
		JSONObject json = new JSONObject().put("tipo", amonestacion.getClass().getSimpleName()).put("idJugador", amonestacion.getIdJugador());
		if (amonestacion instanceof AmonestacionFutbol futbol) {
			json.put("amarillas", futbol.getAmarillas()).put("rojas", futbol.getRojas());
		}
		return json;
	}

	private Amonestacion amonestacionDesdeJSON(JSONObject json) {
		return new AmonestacionFutbol(json.getString("idJugador"), json.optInt("amarillas"), json.optInt("rojas"));
	}
}
