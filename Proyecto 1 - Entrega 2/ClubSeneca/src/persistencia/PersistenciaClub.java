package persistencia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controladores.CentralClubSeneca;
import excepciones.PersistenciaException;
import tiendas.PagoMensualidad;

public class PersistenciaClub extends Persistencia {

	private static final String ARCHIVO = "club.json";

	public PersistenciaClub() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONArray mensualidades = new JSONArray();
		for (PagoMensualidad pago : club.getMensualidades()) {
			mensualidades.put(pagoAJSON(pago));
		}
		escribirJSON(ruta, new JSONObject().put("horarioAtencion", horasAJSON(club.getHorarioAtencion())).put("mensualidades", mensualidades));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			JSONArray mensualidades = json.getJSONArray("mensualidades");
			for (int i = 0; i < mensualidades.length(); i++) {
				club.registrarMensualidad(pagoDesdeJSON(mensualidades.getJSONObject(i), club));
			}
			club.setHorarioAtencion(horasDesdeJSON(json.getJSONArray("horarioAtencion")));
		} catch (JSONException | NullPointerException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONObject pagoAJSON(PagoMensualidad pago) {
		return new JSONObject().put("socio", pago.getComprador().getUsername()).put("valor", pago.getValor())
				.put("fecha", fechaAJSON(pago.getFecha())).put("periodo", pago.getPeriodo());
	}

	private PagoMensualidad pagoDesdeJSON(JSONObject json, CentralClubSeneca club) {
		return new PagoMensualidad(club.getUsuarios().getUsuario(json.getString("socio")), json.getFloat("valor"),
				fechaDesdeJSON(json.getString("fecha")), json.getString("periodo"));
	}
}
