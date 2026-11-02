package persistencia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import competencia.Competencia;
import competencia.CompetenciaASCUN;
import competencia.CompetenciaCerros;
import competencia.EquipoAfueraCompetencia;
import competencia.EquipoCompetencia;
import competencia.EquipoPropioCompetencia;
import competencia.FaseEliminacionDirecta;
import competencia.Grupo;
import controladores.CentralClubSeneca;
import equipos.Equipo;
import excepciones.ClubSenecaException;
import excepciones.PersistenciaException;

public class PersistenciaCompetencias extends Persistencia {

	private static final String ARCHIVO = "competencias.json";

	public PersistenciaCompetencias() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONArray lista = new JSONArray();
		for (Competencia competencia : club.getCompetencias().getCompetencias()) {
			lista.put(competenciaAJSON(competencia));
		}
		escribirJSON(ruta, new JSONObject().put("competencias", lista));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			JSONArray lista = json.getJSONArray("competencias");
			for (int i = 0; i < lista.length(); i++) {
				club.getCompetencias().agregarCompetencia(competenciaDesdeJSON(lista.getJSONObject(i), club));
			}
		} catch (JSONException | ClubSenecaException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONObject competenciaAJSON(Competencia competencia) {
		JSONObject json = new JSONObject().put("tipo", competencia.getClass().getSimpleName())
				.put("idCompetencia", competencia.getIdCompetencia()).put("nombre", competencia.getNombre())
				.put("entidad", competencia.getEntidad()).put("temporada", competencia.getTemporada())
				.put("deporte", competencia.getDeporte()).put("categoria", competencia.getCategoria()).put("formato", competencia.getFormato())
				.put("cerrado", competencia.getCerrado()).put("nominaMinima", competencia.getNominaMinima())
				.put("equivalenciaSuspension", competencia.getEquivalenciaSuspension());
		JSONArray equipos = new JSONArray();
		for (EquipoCompetencia equipo : competencia.getEquiposParticipantes()) {
			equipos.put(equipoCompetenciaAJSON(equipo));
		}
		json.put("equipos", equipos);
		if (competencia instanceof CompetenciaASCUN ascun) {
			JSONArray grupos = new JSONArray();
			for (Grupo grupo : ascun.getFaseGrupos().getGrupos()) {
				grupos.put(new JSONObject().put("nombre", grupo.getNombre()).put("equipos", textosAJSON(grupo.getEquipos())));
			}
			json.put("grupos", grupos).put("restantes", textosAJSON(ascun.getFaseEliminacion().getEquiposRestantes()))
					.put("eliminados", textosAJSON(ascun.getFaseEliminacion().getEquiposEliminados()));
		}
		return json;
	}

	private Competencia competenciaDesdeJSON(JSONObject json, CentralClubSeneca club) throws ClubSenecaException {
		String id = json.getString("idCompetencia");
		Competencia competencia = json.getString("tipo").equals("CompetenciaASCUN")
				? new CompetenciaASCUN(id, json.getString("nombre"), json.getString("entidad"), json.getString("temporada"),
						json.getString("deporte"), json.getString("categoria"), json.getString("formato"), json.getInt("nominaMinima"),
						json.getInt("equivalenciaSuspension"))
				: new CompetenciaCerros(id, json.getString("nombre"), json.getString("entidad"), json.getString("temporada"),
						json.getString("deporte"), json.getString("categoria"), json.getString("formato"), json.getInt("nominaMinima"),
						json.getInt("equivalenciaSuspension"));
		JSONArray equipos = json.getJSONArray("equipos");
		for (int i = 0; i < equipos.length(); i++) {
			competencia.agregarEquipoParticipante(equipoCompetenciaDesdeJSON(equipos.getJSONObject(i), club));
		}
		if (competencia instanceof CompetenciaASCUN ascun) {
			JSONArray grupos = json.getJSONArray("grupos");
			for (int i = 0; i < grupos.length(); i++) {
				JSONObject grupo = grupos.getJSONObject(i);
				ascun.crearGrupo(grupo.getString("nombre"));
				for (String equipoId : textosDesdeJSON(grupo.getJSONArray("equipos"))) {
					ascun.inscribirEquipoAGrupo(grupo.getString("nombre"), equipoId);
				}
			}
			FaseEliminacionDirecta eliminacion = ascun.getFaseEliminacion();
			String[] eliminados = textosDesdeJSON(json.getJSONArray("eliminados"));
			eliminacion.agregarEquipos(eliminados);
			eliminacion.agregarEquipos(textosDesdeJSON(json.getJSONArray("restantes")));
			for (String eliminado : eliminados) {
				eliminacion.eliminarEquipo(eliminado);
			}
		}
		if (json.getBoolean("cerrado")) {
			competencia.setCerrado();
		}
		return competencia;
	}

	private JSONObject equipoCompetenciaAJSON(EquipoCompetencia equipoCompetencia) {
		JSONObject json = new JSONObject().put("puntos", equipoCompetencia.getPuntos()).put("nombre", equipoCompetencia.getNombre());
		if (equipoCompetencia instanceof EquipoPropioCompetencia propio) {
			Equipo equipo = propio.getEquipo();
			json.put("tipo", "EquipoPropioCompetencia").put("equipoId", propio.getEquipoId()).put("deporte", equipo.getDeporte())
					.put("categoria", equipo.getCategoria()).put("inscritos", textosAJSON(propio.getJugadoresInscritos()));
		} else if (equipoCompetencia instanceof EquipoAfueraCompetencia afuera) {
			json.put("tipo", "EquipoAfueraCompetencia")
					.put("jugadorMasPuntos", afuera.getJugadorMasPuntos() == null ? "" : afuera.getJugadorMasPuntos())
					.put("puntosJugadorMasPuntos", afuera.getPuntosJugadorMasPuntos()).put("numHabilitados", afuera.getNumHabilitados())
					.put("numInscritos", afuera.getNumInscritos());
		}
		return json;
	}

	private EquipoCompetencia equipoCompetenciaDesdeJSON(JSONObject json, CentralClubSeneca club) {
		EquipoCompetencia equipoCompetencia;
		if (json.getString("tipo").equals("EquipoPropioCompetencia")) {
			Equipo equipo = club.getEquipos().getEquipo(json.getString("equipoId"));
			if (equipo == null) {
				equipo = new Equipo(json.getString("equipoId"), json.getString("nombre"), json.getString("deporte"),
						json.getString("categoria"), 0);
			}
			equipoCompetencia = new EquipoPropioCompetencia(equipo, textosDesdeJSON(json.getJSONArray("inscritos")));
		} else {
			EquipoAfueraCompetencia afuera = new EquipoAfueraCompetencia(json.getString("nombre"));
			String jugador = json.getString("jugadorMasPuntos");
			afuera.setJugadorMasPuntos(jugador.isEmpty() ? null : jugador);
			afuera.setPuntosJugadorMasPuntos(json.getInt("puntosJugadorMasPuntos"));
			afuera.setNumHabilitados(json.getInt("numHabilitados"));
			afuera.setNumInscritos(json.getInt("numInscritos"));
			equipoCompetencia = afuera;
		}
		equipoCompetencia.sumarPuntos(json.getInt("puntos"));
		return equipoCompetencia;
	}
}
