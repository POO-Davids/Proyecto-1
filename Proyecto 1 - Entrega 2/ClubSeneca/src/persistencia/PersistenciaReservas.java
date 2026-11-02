package persistencia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controladores.CentralClubSeneca;
import excepciones.PersistenciaException;
import reservas.Reserva;

public class PersistenciaReservas extends Persistencia {

	private static final String ARCHIVO = "reservas.json";

	public PersistenciaReservas() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONArray lista = new JSONArray();
		for (Reserva reserva : club.getReservas().getReservas()) {
			lista.put(reservaAJSON(reserva));
		}
		escribirJSON(ruta, new JSONObject().put("reservas", lista));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			JSONArray lista = json.getJSONArray("reservas");
			for (int i = 0; i < lista.length(); i++) {
				Reserva reserva = reservaDesdeJSON(lista.getJSONObject(i), club);
				if (reserva.getInstalacion() != null) {
					club.getReservas().agregarReserva(reserva);
				}
			}
		} catch (JSONException | NullPointerException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONObject reservaAJSON(Reserva reserva) {
		return new JSONObject().put("idReserva", reserva.getIdReserva()).put("instalacion", reserva.getInstalacion().getInstalacionId())
				.put("fecha", fechaAJSON(reserva.getFecha())).put("horas", enterosAJSON(reserva.getHoras()))
				.put("reservadoPor", reserva.getReservadoPor());
	}

	private Reserva reservaDesdeJSON(JSONObject json, CentralClubSeneca club) {
		return new Reserva(json.getString("idReserva"), club.getInstalaciones().getInstalacion(json.getString("instalacion")),
				fechaDesdeJSON(json.getString("fecha")), enterosDesdeJSON(json.getJSONArray("horas")), json.getString("reservadoPor"));
	}
}
