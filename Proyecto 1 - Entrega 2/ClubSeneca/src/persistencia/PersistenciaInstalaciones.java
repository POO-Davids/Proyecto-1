package persistencia;

import java.time.LocalDate;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controladores.CentralClubSeneca;
import excepciones.PersistenciaException;
import instalaciones.CentralInstalaciones;
import instalaciones.Instalacion;

public class PersistenciaInstalaciones extends Persistencia {

	private static final String ARCHIVO = "instalaciones.json";

	public PersistenciaInstalaciones() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		CentralInstalaciones instalaciones = club.getInstalaciones();
		JSONArray lista = new JSONArray();
		for (Instalacion instalacion : instalaciones.getInstalaciones()) {
			lista.put(instalacionAJSON(instalacion));
		}
		escribirJSON(ruta, new JSONObject().put("maximoSimultaneas", new JSONObject(instalaciones.getMaximoSimultaneas()))
				.put("instalaciones", lista));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			CentralInstalaciones instalaciones = club.getInstalaciones();
			JSONObject maximos = json.getJSONObject("maximoSimultaneas");
			for (String tipo : maximos.keySet()) {
				instalaciones.setMaximoSimultaneas(tipo, maximos.getInt(tipo));
			}
			JSONArray lista = json.getJSONArray("instalaciones");
			for (int i = 0; i < lista.length(); i++) {
				instalaciones.agregarInstalacion(instalacionDesdeJSON(lista.getJSONObject(i)));
			}
		} catch (JSONException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONObject instalacionAJSON(Instalacion instalacion) {
		return new JSONObject().put("instalacionId", instalacion.getInstalacionId()).put("tipo", instalacion.getTipo())
				.put("admite", instalacion.getAdmite()).put("techada", instalacion.getTechada())
				.put("horario", horarioAJSON(instalacion.getHorario())).put("disponibilidad", horarioAJSON(instalacion.getDisponibilidad()));
	}

	private Instalacion instalacionDesdeJSON(JSONObject json) {
		return new Instalacion(json.getString("instalacionId"), json.getString("tipo"), json.getInt("admite"), json.getBoolean("techada"),
				horarioDesdeJSON(json.getJSONObject("horario")), horarioDesdeJSON(json.getJSONObject("disponibilidad")));
	}

	private JSONObject horarioAJSON(HashMap<LocalDate, Boolean[]> horario) {
		JSONObject json = new JSONObject();
		for (LocalDate fecha : horario.keySet()) {
			json.put(fechaAJSON(fecha), horasAJSON(horario.get(fecha)));
		}
		return json;
	}

	private HashMap<LocalDate, Boolean[]> horarioDesdeJSON(JSONObject json) {
		HashMap<LocalDate, Boolean[]> horario = new HashMap<>();
		for (String fecha : json.keySet()) {
			horario.put(fechaDesdeJSON(fecha), horasDesdeJSON(json.getJSONArray(fecha)));
		}
		return horario;
	}
}
