package usuarios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import excepciones.CoberturaTurnosException;
import excepciones.CredencialesInvalidasException;
import excepciones.ElementoNoEncontradoException;
import excepciones.PuntosInsuficientesException;
import excepciones.ValorInvalidoException;

public class CentralUsuarios {

	private LinkedHashMap<String, Usuario> usuarios;
	private List<SolicitudCambioTurno> solicitudesCambioTurno;
	private CentralCambioTurno centralCambioTurno;
	private int contadorSolicitudes;

	public CentralUsuarios() {
		usuarios = new LinkedHashMap<>();
		solicitudesCambioTurno = new ArrayList<>();
		centralCambioTurno = new CentralCambioTurno();
	}

	public void agregarUsuario(Usuario usuario) {

		if (usuario == null || usuario.getUsername() == null
				|| usuario.getUsername().isBlank()) {
			throw new IllegalArgumentException("Usuario invalido");
		}

		if (usuarios.containsKey(usuario.getUsername())) {
			throw new IllegalArgumentException(
					"El username ya esta registrado");
		}

		usuarios.put(usuario.getUsername(), usuario);
	}

	public Usuario autenticar(String username, String password) throws CredencialesInvalidasException {
		Usuario usuario = getUsuario(username);
		if (usuario == null) {
			throw new CredencialesInvalidasException("El usuario " + username + " no existe");
		}
		if (!usuario.verificarLogin(username, password)) {
			throw new CredencialesInvalidasException("Contraseña incorrecta");
		}
		return usuario;
	}

	public Usuario getUsuario(String username) {
		return usuarios.get(username);
	}

	public List<Usuario> getUsuarios() {
		return new ArrayList<>(usuarios.values());
	}

	public List<Empleado> getEmpleados(String tipo) {
		List<Empleado> empleados = new ArrayList<>();
		for (Usuario usuario : usuarios.values()) {
			if (usuario instanceof Empleado empleado && (tipo.equals("Todos") || empleado.getTipo().equals(tipo))) {
				empleados.add(empleado);
			}
		}
		return empleados;
	}

	public void asignarTurno(String empleadoUsername, LocalDate fecha, Boolean[] turno) throws ElementoNoEncontradoException {
		getEmpleado(empleadoUsername).setTurno(fecha, turno);
	}

	public void solicitarCambioTurno(
			String empleadoUsername,
			LocalDate fecha,
			Boolean[] horario)
			throws ElementoNoEncontradoException,
				ValorInvalidoException {

		Empleado empleado = getEmpleado(empleadoUsername);

		if (fecha == null) {
			throw new ValorInvalidoException(
					"Debe indicar la fecha del cambio");
		}

		for (SolicitudCambioTurno solicitud : solicitudesCambioTurno) {

			if (solicitud.getEmpleado().getUsername()
					.equals(empleadoUsername)
					&& "pendiente".equals(solicitud.getEstado())) {

				throw new ValorInvalidoException(
						"Ya existe una solicitud pendiente");
			}
		}

		if (!centralCambioTurno.interfazCambioTurno(horario)) {
			throw new ValorInvalidoException(
					"El turno debe tener 24 horas");
		}

		solicitudesCambioTurno.add(
				new SolicitudCambioTurno(
						generarId(), empleado, fecha, horario));
	}

	public List<SolicitudCambioTurno> getSolicitudesPendientes() {
		List<SolicitudCambioTurno> pendientes = new ArrayList<>();
		for (SolicitudCambioTurno solicitud : solicitudesCambioTurno) {
			if (solicitud.getEstado().equals("pendiente")) {
				pendientes.add(solicitud);
			}
		}
		return pendientes;
	}

	public List<SolicitudCambioTurno> getSolicitudes() {
		return solicitudesCambioTurno;
	}

	public void agregarSolicitud(SolicitudCambioTurno solicitud) {
		solicitudesCambioTurno.add(solicitud);
	}

	public Boolean aprobarSolicitudCambioTurno(
			String idSolicitud,
			Boolean[] horarioAtencion)
			throws ElementoNoEncontradoException,
				CoberturaTurnosException {

		SolicitudCambioTurno solicitud =
				getSolicitudPendiente(idSolicitud);

		Empleado empleado = solicitud.getEmpleado();
		LocalDate fecha = solicitud.getFecha();

		Boolean[] turnoAnterior = empleado.getTurno(fecha);

		boolean teniaTurnoPropio =
				fecha != null
				&& empleado.getTurnosPorFecha().containsKey(fecha);

		boolean cubre;

		try {

			empleado.setTurno(fecha, solicitud.getTurno());

			cubre = centralCambioTurno.verificarCobertura(
					fecha, getEmpleados("Todos"), horarioAtencion);

		} finally {

			if (fecha != null && !teniaTurnoPropio) {
				empleado.quitarTurnoDeFecha(fecha);

			} else {
				empleado.setTurno(fecha, turnoAnterior);
			}
		}

		if (!cubre) {
			solicitud.rechazar();

			throw new CoberturaTurnosException(
					"El cambio de turno deja horas sin cobertura");
		}

		solicitud.aprobar();
		solicitud.cambiarTurno();

		return true;
	}

	public void rechazarSolicitudCambioTurno(String idSolicitud) throws ElementoNoEncontradoException {
		getSolicitudPendiente(idSolicitud).rechazar();
	}

	public void compartirCodigoDescuento(String socioUsername, String empleadoUsername) throws ElementoNoEncontradoException {
		if (!(getUsuario(socioUsername) instanceof Socio socio)) {
			throw new ElementoNoEncontradoException("No existe el socio " + socioUsername);
		}
		Empleado empleado = getEmpleado(empleadoUsername);
		empleado.agregarSocioConCodigo(socioUsername);
		socio.setCodigoDescuento(empleado.getCodigoDescuentoTienda());
	}

	public void acumularPuntos(String username, float puntos) {

		if (!Float.isFinite(puntos) || puntos < 0) {
			throw new IllegalArgumentException(
					"Los puntos a acumular deben ser validos");
		}

		Usuario usuario = getUsuario(username);

		if (usuario instanceof Socio socio) {
			socio.setPuntosFidelidad(
					socio.getPuntosFidelidad() + puntos);

		} else if (usuario instanceof Empleado empleado) {
			empleado.setPuntosFidelidad(
					empleado.getPuntosFidelidad() + puntos);
		}
	}

	public void redimirPuntos(String username, float puntos)
			throws PuntosInsuficientesException {

		if (!Float.isFinite(puntos) || puntos <= 0) {
			throw new IllegalArgumentException(
					"Los puntos a redimir deben ser positivos");
		}

		Usuario usuario = getUsuario(username);

		float saldo = usuario instanceof Socio socio
				? socio.getPuntosFidelidad()
				: usuario instanceof Empleado empleado
						? empleado.getPuntosFidelidad()
						: 0;

		if (saldo < puntos) {
			throw new PuntosInsuficientesException(
					"Solo tiene " + saldo + " puntos");
		}

		if (usuario instanceof Socio socio) {
			socio.setPuntosFidelidad(saldo - puntos);

		} else if (usuario instanceof Empleado empleado) {
			empleado.setPuntosFidelidad(saldo - puntos);
		}
	}

	public Boolean existeUsuario(String username) {
		return usuarios.containsKey(username);
	}

	private String generarId() {
		String id;
		do {
			id = "SOL-" + (++contadorSolicitudes);
		} while (buscarSolicitud(id) != null);
		return id;
	}

	private Empleado getEmpleado(String username) throws ElementoNoEncontradoException {
		if (getUsuario(username) instanceof Empleado empleado) {
			return empleado;
		}
		throw new ElementoNoEncontradoException("No existe el empleado " + username);
	}

	private SolicitudCambioTurno buscarSolicitud(String idSolicitud) {
		for (SolicitudCambioTurno solicitud : solicitudesCambioTurno) {
			if (solicitud.getIdSolicitud().equals(idSolicitud)) {
				return solicitud;
			}
		}
		return null;
	}

	private SolicitudCambioTurno getSolicitudPendiente(String idSolicitud) throws ElementoNoEncontradoException {
		SolicitudCambioTurno solicitud = buscarSolicitud(idSolicitud);
		if (solicitud == null || !solicitud.getEstado().equals("pendiente")) {
			throw new ElementoNoEncontradoException("No existe la solicitud pendiente " + idSolicitud);
		}
		return solicitud;
	}
}
