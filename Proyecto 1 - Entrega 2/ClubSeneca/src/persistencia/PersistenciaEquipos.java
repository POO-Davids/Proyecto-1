package persistencia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controladores.CentralClubSeneca;
import equipos.Equipo;
import excepciones.PersistenciaException;
import jugadores.Jugador;

public class PersistenciaEquipos extends Persistencia {

	private static final String ARCHIVO = "equipos.json";

	public PersistenciaEquipos() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONArray lista = new JSONArray();
		for (Equipo equipo : club.getEquipos().getEquipos()) {
			lista.put(equipoAJSON(equipo));
		}
		escribirJSON(ruta, new JSONObject().put("equipos", lista));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			JSONArray lista = json.getJSONArray("equipos");
			for (int i = 0; i < lista.length(); i++) {
				club.getEquipos().agregarEquipo(equipoDesdeJSON(lista.getJSONObject(i), club));
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONObject equipoAJSON(Equipo equipo) {
		return new JSONObject().put("idEquipo", equipo.getIdEquipo()).put("nombre", equipo.getNombre()).put("deporte", equipo.getDeporte())
				.put("categoria", equipo.getCategoria()).put("cupo", equipo.getCupo()).put("jugadores", textosAJSON(equipo.getJugadoresIds()));
	}

	private Equipo equipoDesdeJSON(JSONObject json, CentralClubSeneca club) {
		Equipo equipo = new Equipo(json.getString("idEquipo"), json.getString("nombre"), json.getString("deporte"),
				json.getString("categoria"), json.getInt("cupo"));
		for (String idJugador : textosDesdeJSON(json.getJSONArray("jugadores"))) {
			Jugador jugador = club.getJugadores().getJugadorPorId(idJugador);
			if (jugador != null) {
				equipo.inscribirJugador(jugador);
			}
		}
		return equipo;
	}
}
