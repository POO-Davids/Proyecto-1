package persistencia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDate;
import controladores.CentralClubSeneca;
import excepciones.PersistenciaException;
import tiendas.Bebida;
import usuarios.Administrador;
import usuarios.CentralUsuarios;
import usuarios.Empleado;
import usuarios.Entrenador;
import usuarios.Fisioterapeuta;
import usuarios.SolicitudCambioTurno;
import usuarios.Socio;
import usuarios.Usuario;

public class PersistenciaUsuarios extends Persistencia {

	private static final String ARCHIVO = "usuarios.json";

	public PersistenciaUsuarios() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		CentralUsuarios usuarios = club.getUsuarios();
		JSONArray listaUsuarios = new JSONArray();
		for (Usuario usuario : usuarios.getUsuarios()) {
			listaUsuarios.put(usuarioAJSON(usuario));
		}
		JSONArray solicitudes = new JSONArray();
		for (SolicitudCambioTurno solicitud : usuarios.getSolicitudes()) {
			solicitudes.put(solicitudAJSON(solicitud));
		}
		escribirJSON(ruta, new JSONObject().put("usuarios", listaUsuarios).put("solicitudes", solicitudes));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			CentralUsuarios usuarios = club.getUsuarios();
			JSONArray listaUsuarios = json.getJSONArray("usuarios");
			for (int i = 0; i < listaUsuarios.length(); i++) {
				usuarios.agregarUsuario(usuarioDesdeJSON(listaUsuarios.getJSONObject(i), club));
			}
			JSONArray solicitudes = json.getJSONArray("solicitudes");
			for (int i = 0; i < solicitudes.length(); i++) {
				usuarios.agregarSolicitud(solicitudDesdeJSON(solicitudes.getJSONObject(i), club));
			}
		} catch (JSONException | ClassCastException | NullPointerException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONObject usuarioAJSON(Usuario usuario) {
		JSONObject json = new JSONObject().put("tipo", usuario.getTipo()).put("username", usuario.getUsername())
				.put("password", usuario.getPassword());
		JSONArray bebidas = new JSONArray();
		for (Bebida bebida : usuario.getBebidasSinConsumir()) {
			bebidas.put(new JSONObject().put("nombre", bebida.getNombre()).put("precio", bebida.getPrecio())
					.put("esCaliente", bebida.getEsCaliente()));
		}
		json.put("bebidasSinConsumir", bebidas);
		if (usuario instanceof Socio socio) {
			json.put("nombre", socio.getNombre()).put("edad", socio.getEdad())
					.put("codigoDescuentoDado", socio.getCodigoDescuentoDado() == null ? "" : socio.getCodigoDescuentoDado())
					.put("puntosFidelidad", socio.getPuntosFidelidad()).put("jugandoComo", textosAJSON(socio.getJugandoComo()));
		} else if (usuario instanceof Empleado empleado) {

			json.put("codigoDescuentoTienda",
					empleado.getCodigoDescuentoTienda())
				.put("turno", horasAJSON(empleado.getTurno()))
				.put("puntosFidelidad", empleado.getPuntosFidelidad())
				.put("sociosConCodigo",
					textosAJSON(empleado.getSociosConCodigo()));

			JSONArray turnosPorFecha = new JSONArray();

			for (LocalDate fecha
					: empleado.getTurnosPorFecha().keySet()) {

				turnosPorFecha.put(
					new JSONObject()
						.put("fecha", fecha.toString())
						.put("turno",
							horasAJSON(empleado.getTurno(fecha))));
			}

			json.put("turnosPorFecha", turnosPorFecha);
		}
		return json;
	}

	private Usuario usuarioDesdeJSON(JSONObject json, CentralClubSeneca club) {
		String username = json.getString("username");
		String password = json.getString("password");
		Usuario usuario;
		switch (json.getString("tipo")) {
		case "Socio" -> {
			Socio socio = new Socio(username, password, json.getString("nombre"), json.getString("edad"));
			String codigo = json.optString("codigoDescuentoDado", "");
			socio.setCodigoDescuento(codigo.isEmpty() ? null : codigo);
			socio.setPuntosFidelidad(json.getFloat("puntosFidelidad"));
			for (String idJugador : textosDesdeJSON(json.getJSONArray("jugandoComo"))) {
				socio.agregarJugador(idJugador);
			}
			usuario = socio;
		}
		case "Entrenador", "Fisioterapeuta" -> {
			String codigo = json.getString("codigoDescuentoTienda");
			Empleado empleado = json.getString("tipo").equals("Entrenador") ? new Entrenador(username, password, codigo)
					: new Fisioterapeuta(username, password, codigo);
			empleado.setTurno(null, horasDesdeJSON(json.getJSONArray("turno")));
			JSONArray turnosPorFecha =
			json.optJSONArray("turnosPorFecha");

			if (turnosPorFecha != null) {

				for (int i = 0; i < turnosPorFecha.length(); i++) {

					JSONObject turnoGuardado =
							turnosPorFecha.getJSONObject(i);

					LocalDate fecha = LocalDate.parse(
							turnoGuardado.getString("fecha"));

					Boolean[] horas = horasDesdeJSON(
							turnoGuardado.getJSONArray("turno"));

					empleado.setTurno(fecha, horas);
				}
			}
			empleado.setPuntosFidelidad(json.getFloat("puntosFidelidad"));
			for (String socio : textosDesdeJSON(json.getJSONArray("sociosConCodigo"))) {
				empleado.agregarSocioConCodigo(socio);
			}
			usuario = empleado;
		}
		default -> usuario = new Administrador(username, password);
		}
		JSONArray bebidas = json.getJSONArray("bebidasSinConsumir");
		for (int i = 0; i < bebidas.length(); i++) {
			JSONObject bebida = bebidas.getJSONObject(i);
			usuario.agregarBebidaSinConsumir(new Bebida(bebida.getString("nombre"), bebida.getFloat("precio"), bebida.getBoolean("esCaliente")));
		}
		return usuario;
	}

	private JSONObject solicitudAJSON(
			SolicitudCambioTurno solicitud) {

		return new JSONObject()
				.put("idSolicitud", solicitud.getIdSolicitud())
				.put("empleado",
					solicitud.getEmpleado().getUsername())
				.put("fecha",
					solicitud.getFecha() == null
						? JSONObject.NULL
						: solicitud.getFecha().toString())
				.put("turno", horasAJSON(solicitud.getTurno()))
				.put("estado", solicitud.getEstado());
	}

	private SolicitudCambioTurno solicitudDesdeJSON(
			JSONObject json,
			CentralClubSeneca club) {

		String textoFecha = json.optString("fecha", "");

		LocalDate fecha = textoFecha.isBlank()
				? null
				: LocalDate.parse(textoFecha);

		return new SolicitudCambioTurno(
				json.getString("idSolicitud"),
				(Empleado) club.getUsuarios().getUsuario(
						json.getString("empleado")),
				fecha,
				horasDesdeJSON(json.getJSONArray("turno")),
				json.getString("estado"));
	}
}
