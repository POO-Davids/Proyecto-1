package persistencia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controladores.CentralClubSeneca;
import entrenamientos.AsistenciaJugador;
import entrenamientos.SesionEntrenamiento;
import entrenamientos.SesionEntrenamientoConjunto;
import entrenamientos.SesionEntrenamientoIndividual;
import excepciones.PersistenciaException;
import jugadores.Jugador;
import reservas.Reserva;

public class PersistenciaEntrenamientos extends Persistencia {

	private static final String ARCHIVO = "entrenamientos.json";

	public PersistenciaEntrenamientos() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONArray lista = new JSONArray();
		for (SesionEntrenamiento sesion : club.getSesionesEntrenamiento().getSesiones()) {
			lista.put(sesionAJSON(sesion));
		}
		escribirJSON(ruta, new JSONObject().put("sesiones", lista));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			JSONArray lista = json.getJSONArray("sesiones");
			for (int i = 0; i < lista.length(); i++) {
				SesionEntrenamiento sesion = sesionDesdeJSON(lista.getJSONObject(i), club);
				if (sesion != null) {
					club.getSesionesEntrenamiento().agregarSesion(sesion);
				}
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONObject sesionAJSON(SesionEntrenamiento sesion) {
		JSONObject json = new JSONObject().put("tipo", sesion.getClass().getSimpleName()).put("idSesion", sesion.getIdSesion())
				.put("fecha", fechaAJSON(sesion.getFecha())).put("horaInicio", sesion.getHoraInicio()).put("duracion", sesion.getDuracion())
				.put("entrenador", sesion.getEntrenadorUsername()).put("reserva", sesion.getReserva().getIdReserva());
		if (sesion instanceof SesionEntrenamientoConjunto conjunto) {
			json.put("equipoConvocadoId", conjunto.getEquipoConvocadoId());
		} else if (sesion instanceof SesionEntrenamientoIndividual individual) {
			json.put("jugadoresConvocados", textosAJSON(individual.getJugadoresConvocados()));
		}
		JSONArray asistencias = new JSONArray();
		for (AsistenciaJugador asistencia : sesion.getAsistencias().values()) {
			asistencias.put(asistenciaAJSON(asistencia));
		}
		return json.put("asistencias", asistencias);
	}

	private SesionEntrenamiento sesionDesdeJSON(JSONObject json, CentralClubSeneca club) {
		Reserva reserva = club.getReservas().getReserva(json.getString("reserva"));
		if (reserva == null) {
			return null;
		}
		SesionEntrenamiento sesion = json.getString("tipo").equals("SesionEntrenamientoConjunto")
				? new SesionEntrenamientoConjunto(json.getString("idSesion"), fechaDesdeJSON(json.getString("fecha")), json.getInt("horaInicio"),
						json.getInt("duracion"), json.getString("entrenador"), reserva, json.getString("equipoConvocadoId"))
				: new SesionEntrenamientoIndividual(json.getString("idSesion"), fechaDesdeJSON(json.getString("fecha")),
						json.getInt("horaInicio"), json.getInt("duracion"), json.getString("entrenador"), reserva,
						textosDesdeJSON(json.getJSONArray("jugadoresConvocados")));
		JSONArray asistencias = json.getJSONArray("asistencias");
		for (int i = 0; i < asistencias.length(); i++) {
			AsistenciaJugador asistencia = asistenciaDesdeJSON(asistencias.getJSONObject(i), sesion);
			sesion.agregarAsistencia(asistencia);
			Jugador jugador = club.getJugadores().getJugadorPorId(asistencia.getJugadorId());
			if (jugador != null) {
				jugador.agregarAsistencia(asistencia);
			}
		}
		return sesion;
	}

	private JSONObject asistenciaAJSON(AsistenciaJugador asistencia) {
		return new JSONObject().put("jugadorId", asistencia.getJugadorId()).put("asistio", asistencia.getAsistio())
				.put("evaluacion", asistencia.getEvaluacion()).put("comentario", asistencia.getComentario());
	}

	private AsistenciaJugador asistenciaDesdeJSON(JSONObject json, SesionEntrenamiento sesion) {
		AsistenciaJugador asistencia = new AsistenciaJugador(json.getString("jugadorId"), json.getString("asistio"), sesion);
		asistencia.setEvaluacion(json.getInt("evaluacion"));
		asistencia.setComentario(json.getString("comentario"));
		return asistencia;
	}
}
