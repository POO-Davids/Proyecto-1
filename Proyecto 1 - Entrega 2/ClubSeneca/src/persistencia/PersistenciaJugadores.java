package persistencia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controladores.CentralClubSeneca;
import estadisticas.Estadistica;
import estadisticas.EstadisticaBasket;
import estadisticas.EstadisticaFutbol;
import estadisticas.EstadisticaTenisMesa;
import estadisticas.EstadisticaTenisPadel;
import estadisticas.EstadisticaVolley;
import excepciones.PersistenciaException;
import jugadores.Jugador;
import jugadores.JugadorConjunto;
import jugadores.JugadorIndividual;
import usuarios.Usuario;

public class PersistenciaJugadores extends Persistencia {

	private static final String ARCHIVO = "jugadores.json";

	public PersistenciaJugadores() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONArray lista = new JSONArray();
		for (Jugador jugador : club.getJugadores().getJugadores()) {
			lista.put(jugadorAJSON(jugador));
		}
		escribirJSON(ruta, new JSONObject().put("jugadores", lista));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			JSONArray lista = json.getJSONArray("jugadores");
			for (int i = 0; i < lista.length(); i++) {
				club.getJugadores().agregarJugador(jugadorDesdeJSON(lista.getJSONObject(i), club));
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONObject jugadorAJSON(Jugador jugador) {
		JSONObject json = new JSONObject().put("tipo", jugador.getClass().getSimpleName()).put("idJugador", jugador.getIdJugador())
				.put("usuario", jugador.getUsuario().getUsername()).put("partidosSuspendido", jugador.getPartidosSuspendido())
				.put("amonestaciones", jugador.getAmonestaciones())
				.put("estadisticaAcumulada", estadisticaAJSON(jugador.getEstadisticaAcumulada()));
		if (jugador instanceof JugadorIndividual individual) {
			json.put("disciplina", individual.getDisciplina()).put("nivel", individual.getNivel()).put("elo", individual.getElo());
		} else if (jugador instanceof JugadorConjunto conjunto) {
			json.put("posicion", conjunto.getPosicion()).put("camiseta", conjunto.getCamiseta()).put("equipoId", conjunto.getEquipoId())
					.put("deporte", conjunto.getDeporte());
		}
		return json;
	}

	private Jugador jugadorDesdeJSON(JSONObject json, CentralClubSeneca club) throws PersistenciaException {
		Usuario usuario = club.getUsuarios().getUsuario(json.getString("usuario"));
		if (usuario == null) {
			throw new PersistenciaException("El jugador " + json.getString("idJugador") + " tiene un usuario inexistente");
		}
		String id = json.getString("idJugador");
		Jugador jugador = json.getString("tipo").equals("JugadorIndividual")
				? new JugadorIndividual(id, usuario, json.getString("disciplina"), json.getString("nivel"), json.getFloat("elo"))
				: new JugadorConjunto(id, usuario, json.getString("posicion"), json.getInt("camiseta"), json.getString("equipoId"),
						json.getString("deporte"));
		int partidos = json.getInt("partidosSuspendido");

		if (partidos < 0) {
			throw new PersistenciaException(
					"Los partidos de suspension no pueden ser negativos");
		}

		if (partidos > 0) {
			jugador.suspender(partidos);
		}
		for (int i = 0; i < json.getInt("amonestaciones"); i++) {
			jugador.aumentarAmonestaciones();
		}
		jugador.acumularEstadistica(estadisticaDesdeJSON(json.getJSONObject("estadisticaAcumulada")));
		return jugador;
	}

	static JSONObject estadisticaAJSON(Estadistica estadistica) {
		JSONObject json = new JSONObject().put("tipo", estadistica.getClass().getSimpleName()).put("usuarioUsername",
				estadistica.getUsuarioUsername() == null ? "" : estadistica.getUsuarioUsername());
		if (estadistica instanceof EstadisticaFutbol e) {
			json.put("goles", e.getGoles()).put("asistencias", e.getAsistencias());
		} else if (estadistica instanceof EstadisticaBasket e) {
			json.put("puntos", e.getPuntos()).put("rebotes", e.getRebotes());
		} else if (estadistica instanceof EstadisticaVolley e) {
			json.put("aces", e.getAces()).put("bloqueos", e.getBloqueos());
		} else if (estadistica instanceof EstadisticaTenisPadel e) {
			json.put("setGanados", e.getSetGanados()).put("gamesGanados", e.getGamesGanados()).put("aces", e.getAces())
					.put("erroresNoForzados", e.getErroresNoForzados());
		} else if (estadistica instanceof EstadisticaTenisMesa e) {
			json.put("setGanados", e.getSetGanados()).put("puntosTotales", e.getPuntosTotales());
		}
		return json;
	}

	static Estadistica estadisticaDesdeJSON(JSONObject json) {
		String usuario = json.getString("usuarioUsername");
		return switch (json.getString("tipo")) {
		case "EstadisticaBasket" -> new EstadisticaBasket(usuario, json.getInt("puntos"), json.getInt("rebotes"));
		case "EstadisticaVolley" -> new EstadisticaVolley(usuario, json.getInt("aces"), json.getInt("bloqueos"));
		case "EstadisticaTenisPadel" -> new EstadisticaTenisPadel(usuario, json.getInt("setGanados"), json.getInt("gamesGanados"),
				json.getInt("aces"), json.getInt("erroresNoForzados"));
		case "EstadisticaTenisMesa" -> new EstadisticaTenisMesa(usuario, json.getInt("setGanados"), json.getInt("puntosTotales"));
		default -> new EstadisticaFutbol(usuario, json.getInt("goles"), json.getInt("asistencias"));
		};
	}
}
