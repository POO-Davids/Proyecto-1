package controladores;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import amonestaciones.AmonestacionFutbol;
import competencia.CentralCompetencias;
import competencia.Competencia;
import competencia.CompetenciaASCUN;
import competencia.EquipoAfueraCompetencia;
import competencia.EquipoCompetencia;
import entrenamientos.AsistenciaJugador;
import entrenamientos.SesionEntrenamiento;
import equipos.CentralEquipos;
import equipos.Equipo;
import estadisticas.Estadistica;
import excepciones.AccionNoPermitidaException;
import excepciones.CoberturaTurnosException;
import excepciones.CompetenciaException;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import excepciones.EquipoException;
import excepciones.InstalacionConReservasException;
import excepciones.InventarioInsuficienteException;
import excepciones.MarcadorInvalidoException;
import excepciones.PartidoException;
import excepciones.ValorInvalidoException;
import instalaciones.CentralInstalaciones;
import jugadores.CentralJugadores;
import jugadores.Jugador;
import jugadores.JugadorConjunto;
import jugadores.JugadorIndividual;
import partidos.Partido;
import partidos.PartidoCompetencia;
import partidos.Partidos;
import reservas.Reserva;
import tiendas.Articulo;
import tiendas.ArticuloCafeteria;
import tiendas.ArticuloTienda;
import tiendas.PagoMensualidad;
import tiendas.Venta;
import usuarios.CentralUsuarios;
import usuarios.Empleado;
import usuarios.SolicitudCambioTurno;
import usuarios.Socio;
import usuarios.Usuario;

public class ControladorAdministrador extends ControladorRol {

	public ControladorAdministrador(CentralClubSeneca central) {
		super(central);
	}

	public void registrarUsuario(Usuario usuario) throws ElementoDuplicadoException {
		CentralUsuarios usuarios = central.getUsuarios();
		if (usuarios.existeUsuario(usuario.getUsername())) {
			throw new ElementoDuplicadoException("Ya existe el usuario " + usuario.getUsername());
		}
		usuarios.agregarUsuario(usuario);
		if (usuario instanceof Empleado empleado && empleado.getCodigoDescuentoTienda() != null) {
			central.getTienda().agregarCodigoDescuento(empleado.getCodigoDescuentoTienda());
		}
	}

	public void crearEquipo(String nombre, String deporte, String categoria, int cupo)
			throws ElementoDuplicadoException, ValorInvalidoException {
		if (cupo <= 0) {
			throw new ValorInvalidoException("El cupo debe ser mayor que cero");
		}
		CentralEquipos equipos = central.getEquipos();
		if (equipos.existeEquipo(nombre, deporte, categoria)) {
			throw new ElementoDuplicadoException("Ya existe el equipo " + nombre + " (" + deporte + ", " + categoria + ")");
		}
		equipos.crearEquipo(nombre, deporte, categoria, cupo);
	}

	public void darDeBajaEquipo(String idEquipo) throws ElementoNoEncontradoException {
		CentralEquipos equipos = central.getEquipos();
		Equipo equipo = equipos.getEquipo(idEquipo);
		if (equipo == null || equipo == equipos.getEquipoAnonimo()) {
			throw new ElementoNoEncontradoException("No existe el equipo " + idEquipo);
		}
		central.getPartidos().reemplazarEquipoEnHistorial(idEquipo, equipos.getEquipoAnonimo());
		equipos.quitarEquipo(idEquipo);
	}

	public void crearJugador(String usernameSocio, Jugador jugador)
			throws ElementoNoEncontradoException, EquipoException, ValorInvalidoException {
		Usuario usuario = central.getUsuarios().getUsuario(usernameSocio);
		if (usuario == null) {
			throw new ElementoNoEncontradoException("No existe el usuario " + usernameSocio);
		}
		CentralJugadores jugadores = central.getJugadores();
		Jugador nuevo;
		if (jugador instanceof JugadorIndividual individual) {
			nuevo = jugadores.inscribirJugadorIndividual(usuario, individual.getDisciplina(), individual.getNivel(), individual.getElo());
		} else if (jugador instanceof JugadorConjunto conjunto) {
			Equipo equipo = central.getEquipos().getEquipo(conjunto.getEquipoId());
			if (equipo == null) {
				throw new ElementoNoEncontradoException("No existe el equipo " + conjunto.getEquipoId());
			}
			String deporte = conjunto.getDeporte() == null || conjunto.getDeporte().isBlank() ? equipo.getDeporte()
					: conjunto.getDeporte();
			nuevo = jugadores.inscribirJugadorConjunto(usuario, deporte, conjunto.getPosicion(), conjunto.getCamiseta(), equipo);
			central.getEquipos().inscribirJugador(nuevo, equipo);
		} else {
			throw new ValorInvalidoException("Tipo de jugador no válido");
		}
		if (usuario instanceof Socio socio) {
			socio.agregarJugador(nuevo.getIdJugador());
		}
	}

	public void darDeBajaJugador(String idJugador) throws ElementoNoEncontradoException {
		CentralJugadores jugadores = central.getJugadores();
		Jugador jugador = jugadores.darDeBajaJugador(idJugador);
		central.getPartidos().reemplazarJugadorEnHistorial(idJugador, jugadores.getJugadorAnonimo());
		if (jugador instanceof JugadorConjunto conjunto) {
			Equipo equipo = central.getEquipos().getEquipo(conjunto.getEquipoId());
			if (equipo != null) {
				central.getEquipos().quitarJugador(jugador, equipo);
			}
		}
		if (jugador.getUsuario() instanceof Socio socio) {
			socio.quitarJugador(idJugador);
		}
	}

	public String consultarInventario() {
		return central.getInstalaciones().consultarInventario() + central.getTienda().consultarInventario()
				+ central.getCafeteria().consultarInventario();
	}

	public void moverArticulo(String nombreArticulo, String origen, String destino, int cantidad)
			throws ElementoNoEncontradoException, InventarioInsuficienteException {
		Articulo articulo = central.getTienda().getArticulo(nombreArticulo);
		if (articulo == null) {
			articulo = central.getCafeteria().getArticulo(nombreArticulo);
		}
		if (articulo == null) {
			throw new ElementoNoEncontradoException("No existe el artículo " + nombreArticulo);
		}
		if (articulo.getUnidadesEn(origen) < cantidad) {
			throw new InventarioInsuficienteException("Solo hay " + articulo.getUnidadesEn(origen) + " unidades en " + origen);
		}
		articulo.moverUnidades(origen, destino, cantidad);
	}

	public void abastecer(String tienda, String nombreArticulo, int cantidad) throws ValorInvalidoException, ElementoNoEncontradoException {
		if (cantidad <= 0) {
			throw new ValorInvalidoException("La cantidad a abastecer debe ser mayor que cero");
		}
		if (esTienda(tienda)) {
			central.getTienda().abastecer(nombreArticulo, cantidad);
		} else {
			central.getCafeteria().abastecer(nombreArticulo, cantidad);
		}
	}

	public void agregarArticulo(String tienda, Articulo articulo) throws ElementoDuplicadoException, ValorInvalidoException {
		if (esTienda(tienda) && articulo instanceof ArticuloTienda articuloTienda) {
			central.getTienda().agregarArticulo(articuloTienda);
		} else if (!esTienda(tienda) && articulo instanceof ArticuloCafeteria articuloCafeteria) {
			central.getCafeteria().agregarArticulo(articuloCafeteria);
		} else {
			throw new ValorInvalidoException("El artículo no corresponde a " + tienda);
		}
	}

	public void resolverSugerencia(String tienda, String nombreArticulo, Boolean aceptada)
			throws ElementoNoEncontradoException, ElementoDuplicadoException, ValorInvalidoException {
		if (esTienda(tienda)) {
			if (aceptada) {
				central.getTienda().aceptarSugerencia(nombreArticulo);
			} else {
				central.getTienda().denegarSugerencia(nombreArticulo);
			}
		} else if (aceptada) {
			central.getCafeteria().aceptarSugerencia(nombreArticulo);
		} else {
			central.getCafeteria().denegarSugerencia(nombreArticulo);
		}
	}

	private boolean esTienda(String tienda) throws ValorInvalidoException {
		if (tienda.equalsIgnoreCase("Tienda")) {
			return true;
		}
		if (tienda.toLowerCase().startsWith("cafeter")) {
			return false;
		}
		throw new ValorInvalidoException("La tienda debe ser Tienda o Cafeteria");
	}

	public void asignarTurno(String empleadoUsername, LocalDate fecha, Boolean[] turno)
			throws ElementoNoEncontradoException, ValorInvalidoException {
		validarHorario(turno);
		central.getUsuarios().asignarTurno(empleadoUsername, fecha, turno);
	}

	public List<SolicitudCambioTurno> consultarSolicitudesCambioTurno() {
		return central.getUsuarios().getSolicitudesPendientes();
	}

	public Boolean resolverCambioTurno(String idSolicitud, Boolean aprobada)
			throws ElementoNoEncontradoException, CoberturaTurnosException {
		if (aprobada) {
			return central.getUsuarios().aprobarSolicitudCambioTurno(idSolicitud, central.getHorarioAtencion());
		}
		central.getUsuarios().rechazarSolicitudCambioTurno(idSolicitud);
		return false;
	}

	public String generarReporte(String tipo, String filtro, LocalDate inicio, LocalDate fin)
			throws ElementoNoEncontradoException, ValorInvalidoException {
		return switch (tipo.toLowerCase()) {
		case "ventas" -> reporteVentas(filtro == null || filtro.isBlank() ? "todos" : filtro.toLowerCase(), inicio, fin);
		case "deportivo" -> reporteDeportivo(filtro == null ? "" : filtro, inicio, fin);
		case "asistencia" -> reporteAsistencia(inicio, fin);
		default -> throw new ValorInvalidoException("El tipo de reporte debe ser ventas, deportivo o asistencia");
		};
	}

	private String reporteVentas(String rubro, LocalDate inicio, LocalDate fin) {
		boolean todos = rubro.equals("todos");
		List<Venta> ventas = new ArrayList<>();
		if (todos || rubro.equals("tienda")) {
			ventas.addAll(central.getTienda().getVentasEntre(inicio, fin));
		}
		if (todos || rubro.startsWith("cafeter")) {
			ventas.addAll(central.getCafeteria().getVentasEntre(inicio, fin));
		}
		if (todos || rubro.equals("mensualidades")) {
			for (PagoMensualidad pago : central.getMensualidades()) {
				if (!pago.getFecha().isBefore(inicio) && !pago.getFecha().isAfter(fin)) {
					ventas.add(pago);
				}
			}
		}
		ventas.sort(Comparator.comparing(Venta::getFecha));
		StringBuilder texto = new StringBuilder("== Reporte de ventas " + inicio + " a " + fin + " (" + rubro + ") ==\n");
		Map<String, Float> totales = new LinkedHashMap<>();
		float general = 0;
		for (Venta venta : ventas) {
			texto.append(venta).append("\n");
			totales.merge(venta.getRubro(), venta.calcularTotal(), Float::sum);
			general += venta.calcularTotal();
		}
		texto.append("Ventas: ").append(ventas.size()).append("\n");
		for (Map.Entry<String, Float> total : totales.entrySet()) {
			texto.append(String.format("Total %s: %.0f%n", total.getKey(), total.getValue()));
		}
		texto.append(String.format("Total general: %.0f%n", general));
		return texto.toString();
	}

	private String reporteDeportivo(String filtro, LocalDate inicio, LocalDate fin) throws ElementoNoEncontradoException {
		CentralCompetencias competencias = central.getCompetencias();
		Competencia competencia = competencias.getCompetencia(filtro);
		StringBuilder texto = new StringBuilder("== Reporte deportivo " + inicio + " a " + fin + " ==\n");
		if (competencia != null) {
			texto.append(competencia).append("\nResultados:\n");
			for (PartidoCompetencia partido : competencia.getPartidos()) {
				texto.append(partido.getResumen()).append("\n");
			}
			texto.append(consultarTablaPosiciones(competencia.getIdCompetencia(), null));
			texto.append("Goleadores:\n");
			for (Estadistica estadistica : competencias.getGoleadores(competencia.getIdCompetencia())) {
				texto.append("  ").append(nombreJugador(estadistica.getUsuarioUsername())).append(": ")
						.append(estadistica.getValorPrincipal()).append(" (").append(estadistica).append(")\n");
			}
			String destacado = competencias.getJugadorDestacado(competencia.getIdCompetencia());
			texto.append("Jugador destacado: ").append(destacado == null ? "ninguno" : nombreJugador(destacado)).append("\n");
			return texto.toString();
		}
		for (Partido partido : central.getPartidos().getPartidosEntre(inicio, fin)) {
			boolean cumple = filtro.isBlank() || partido.getDeporte().equalsIgnoreCase(filtro)
					|| partido.getResumen().contains(filtro);
			if (cumple) {
				texto.append(partido.getResumen()).append("\n");
			}
		}
		return texto.toString();
	}

	private String reporteAsistencia(LocalDate inicio, LocalDate fin) {
		StringBuilder texto = new StringBuilder("== Reporte de asistencia " + inicio + " a " + fin + " ==\n");
		Map<String, int[]> conteo = new LinkedHashMap<>();
		for (SesionEntrenamiento sesion : central.getSesionesEntrenamiento().getSesionesEntre(inicio, fin)) {
			texto.append(sesion.getResumen()).append("\n");
			for (AsistenciaJugador asistencia : sesion.getAsistencias().values()) {
				int[] valores = conteo.computeIfAbsent(asistencia.getJugadorId(), id -> new int[2]);
				valores[1]++;
				if (!asistencia.getAsistio().equals("faltó")) {
					valores[0]++;
				}
			}
		}
		texto.append("Asistencia por jugador:\n");
		for (Map.Entry<String, int[]> entrada : conteo.entrySet()) {
			texto.append("  ").append(nombreJugador(entrada.getKey())).append(": ").append(entrada.getValue()[0]).append("/")
					.append(entrada.getValue()[1]).append(" sesiones\n");
		}
		texto.append("Ranking ELO actual:\n");
		for (Jugador jugador : central.getJugadores().getJugadores()) {
			if (jugador instanceof JugadorIndividual individual) {
				texto.append("  ").append(nombreJugador(individual.getIdJugador())).append(" (").append(individual.getDisciplina())
						.append(", ").append(individual.getNivel()).append("): ").append(Math.round(individual.getElo())).append("\n");
			}
		}
		return texto.toString();
	}

	private String nombreJugador(String idJugador) {
		Jugador jugador = central.getJugadores().getJugadorPorId(idJugador);
		if (jugador == null) {
			return idJugador;
		}
		String nombre = jugador.getUsuario() instanceof Socio socio ? socio.getNombre() : jugador.getUsuario().getUsername();
		return nombre + " [" + idJugador + "]";
	}

	public void registrarPagoMensualidad(String usernameSocio, float valor, LocalDate fecha)
			throws ElementoNoEncontradoException, ValorInvalidoException {
		if (valor <= 0) {
			throw new ValorInvalidoException("El valor de la mensualidad debe ser mayor que cero");
		}
		if (!(central.getUsuarios().getUsuario(usernameSocio) instanceof Socio socio)) {
			throw new ElementoNoEncontradoException("No existe el socio " + usernameSocio);
		}
		central.registrarMensualidad(new PagoMensualidad(socio, valor, fecha, YearMonth.from(fecha).toString()));
	}

	public void agregarInstalacion(String tipo, int admite, Boolean techada) throws ValorInvalidoException {
		if (admite <= 0) {
			throw new ValorInvalidoException("La instalación debe admitir al menos un jugador");
		}
		if (!List.of("tenis", "padel", "mesa", "grupal").contains(tipo.toLowerCase())) {
			throw new ValorInvalidoException("El tipo debe ser tenis, padel, mesa o grupal");
		}
		central.getInstalaciones().agregarInstalacion(tipo, admite, techada);
	}

	public void setMaximoInstalacionesSimultaneas(String tipo, int maximo) throws ValorInvalidoException {
		if (maximo <= 0) {
			throw new ValorInvalidoException("El máximo debe ser mayor que cero");
		}
		central.getInstalaciones().setMaximoSimultaneas(tipo, maximo);
	}

	public void eliminarInstalacion(String idInstalacion) throws InstalacionConReservasException, ElementoNoEncontradoException {
		if (central.getReservas().tieneReservasFuturas(idInstalacion)) {
			throw new InstalacionConReservasException("La instalación " + idInstalacion + " tiene reservas futuras");
		}
		central.getInstalaciones().eliminarInstalacion(idInstalacion);
	}

	public void cambiarHorarioInstalacion(String idInstalacion, LocalDate fecha, Boolean[] horario)
			throws ElementoNoEncontradoException, ValorInvalidoException {
		validarHorario(horario);
		CentralInstalaciones instalaciones = central.getInstalaciones();
		instalaciones.cambiarHorario(idInstalacion, fecha, horario);
		for (String idReserva : central.getReservas().cancelarReservasFueraDeHorario(idInstalacion, fecha, horario)) {
			central.getPartidos().cancelarPartidoPorReserva(idReserva);
			central.getSesionesEntrenamiento().cancelarSesionPorReserva(idReserva);
		}
	}

	private void validarHorario(Boolean[] horario) throws ValorInvalidoException {
		if (horario == null || horario.length != 24) {
			throw new ValorInvalidoException("El horario debe tener 24 posiciones (h0..h23)");
		}
	}

	public String consultarHistorial(String tipo, LocalDate inicio, LocalDate fin) throws ValorInvalidoException {
		String clase = tipo.toLowerCase();
		boolean todo = clase.equals("todo");
		if (!todo && !List.of("partidos", "entrenamientos", "reservas").contains(clase)) {
			throw new ValorInvalidoException("El tipo debe ser partidos, entrenamientos, reservas o todo");
		}
		List<SimpleEntry<LocalDateTime, String>> registros = new ArrayList<>();
		if (todo || clase.equals("partidos")) {
			for (Partido partido : central.getPartidos().getPartidosEntre(inicio, fin)) {
				registros.add(new SimpleEntry<>(partido.getFecha(), "[Partido] " + partido.getResumen()));
			}
		}
		if (todo || clase.equals("entrenamientos")) {
			for (SesionEntrenamiento sesion : central.getSesionesEntrenamiento().getSesionesEntre(inicio, fin)) {
				registros.add(new SimpleEntry<>(sesion.getFecha().atTime(sesion.getHoraInicio(), 0), "[Entrenamiento] " + sesion.getResumen()));
			}
		}
		if (todo || clase.equals("reservas")) {
			for (Reserva reserva : central.getReservas().getReservasEntre(inicio, fin)) {
				registros.add(new SimpleEntry<>(reserva.getFecha().atTime(reserva.getHoras()[0], 0), "[Reserva] " + reserva));
			}
		}
		registros.sort(Map.Entry.comparingByKey());
		StringBuilder texto = new StringBuilder("== Historial de " + clase + " " + inicio + " a " + fin + " ==\n");
		for (SimpleEntry<LocalDateTime, String> registro : registros) {
			texto.append(registro.getValue()).append("\n");
		}
		if (registros.isEmpty()) {
			texto.append("(sin registros)\n");
		}
		return texto.toString();
	}

	public void programarPartidoInterno(String idEquipo1, String idEquipo2, LocalDateTime fecha, String[] habilitados1,
			String[] habilitados2) throws ElementoNoEncontradoException, PartidoException, CompetenciaException {
		Equipo equipo1 = central.getEquipos().getEquipo(idEquipo1);
		Equipo equipo2 = central.getEquipos().getEquipo(idEquipo2);
		if (equipo1 == null || equipo2 == null) {
			throw new ElementoNoEncontradoException("No existe alguno de los equipos " + idEquipo1 + ", " + idEquipo2);
		}
		central.getPartidos().programarPartidoInterno(equipo1, equipo2, fecha, habilitados1, habilitados2);
	}

	public void registrarPartidoCompetencia(String idCompetencia, LocalDateTime fecha, String idEquipoAfuera, String escenario,
			String[] habilitados) throws ElementoNoEncontradoException, PartidoException, CompetenciaException,
			AccionNoPermitidaException {
		Competencia competencia = buscarCompetencia(idCompetencia);
		if (competencia.getCerrado()) {
			throw new AccionNoPermitidaException("La competencia " + idCompetencia + " está cerrada");
		}
		if (!(competencia.getEquipoParticipante(idEquipoAfuera) instanceof EquipoAfueraCompetencia equipoAfuera)) {
			throw new ElementoNoEncontradoException("No existe el equipo rival " + idEquipoAfuera + " en la competencia");
		}
		PartidoCompetencia partido = central.getPartidos().crearPartidoCompetencia(competencia, fecha, equipoAfuera, escenario,
				habilitados);
		central.getCompetencias().registrarPartido(idCompetencia, partido);
	}

	public void registrarMarcador(String idPartido, int puntuacion1, int puntuacion2)
			throws MarcadorInvalidoException, ElementoNoEncontradoException {
		Partidos partidos = central.getPartidos();
		partidos.registrarMarcador(idPartido, puntuacion1, puntuacion2);
		if (partidos.getPartido(idPartido) instanceof PartidoCompetencia partido) {
			buscarCompetencia(partido.getCompetenciaId()).registrarResultado(idPartido, puntuacion1, puntuacion2);
		}
	}

	public void registrarEstadistica(String idPartido, String idJugador, Estadistica estadistica) throws ElementoNoEncontradoException {
		Partido partido = central.getPartidos().getPartido(idPartido);
		if (partido == null) {
			throw new ElementoNoEncontradoException("No existe el partido " + idPartido);
		}
		Jugador jugador = central.getJugadores().getJugadorPorId(idJugador);
		if (jugador == null) {
			throw new ElementoNoEncontradoException("No existe el jugador " + idJugador);
		}
		estadistica.setUsuarioUsername(idJugador);
		partido.agregarEstadistica(estadistica);
		jugador.acumularEstadistica(estadistica);
		if (partido instanceof PartidoCompetencia partidoCompetencia) {
			buscarCompetencia(partidoCompetencia.getCompetenciaId()).acumularEstadistica(estadistica);
		}
	}

	public void registrarAmonestacion(String idPartido, String idJugador, int amarillas, int rojas)
			throws ElementoNoEncontradoException, ValorInvalidoException {
		if (amarillas < 0 || rojas < 0 || amarillas + rojas == 0) {
			throw new ValorInvalidoException("Las tarjetas deben ser no negativas y al menos una");
		}
		Jugador jugador = central.getJugadores().getJugadorPorId(idJugador);
		if (jugador == null) {
			throw new ElementoNoEncontradoException("No existe el jugador " + idJugador);
		}
		central.getPartidos().registrarAmonestacion(idPartido, jugador, new AmonestacionFutbol(idJugador, amarillas, rojas));
	}

	public void crearCompetencia(String tipo, String nombre, String entidad, String temporada, String deporte, String categoria,
			String formato, int nominaMinima, int maximoAmarillas) throws ValorInvalidoException {
		if (nominaMinima <= 0 || maximoAmarillas <= 0) {
			throw new ValorInvalidoException("La nómina mínima y el máximo de amarillas deben ser mayores que cero");
		}
		central.getCompetencias().crearCompetencia(tipo, nombre, entidad, temporada, deporte, categoria, formato, nominaMinima,
				maximoAmarillas);
	}

	public void inscribirEquipoACompetencia(String idCompetencia, String idEquipo, String[] nomina)
			throws ElementoNoEncontradoException, CompetenciaException, ElementoDuplicadoException {
		Equipo equipo = central.getEquipos().getEquipo(idEquipo);
		if (equipo == null) {
			throw new ElementoNoEncontradoException("No existe el equipo " + idEquipo);
		}
		central.getCompetencias().inscribirEquipoACompetencia(equipo, idCompetencia, nomina);
	}

	public void agregarEquipoAfuera(String idCompetencia, String nombre) throws ElementoNoEncontradoException, ElementoDuplicadoException {
		buscarCompetencia(idCompetencia).agregarEquipoAfuera(nombre);
	}

	public void crearGrupo(String idCompetencia, String nombreGrupo)
			throws ElementoNoEncontradoException, ElementoDuplicadoException, AccionNoPermitidaException {
		buscarASCUN(idCompetencia).crearGrupo(nombreGrupo);
	}

	public void agregarEquipoAGrupo(String idCompetencia, String nombreGrupo, String idEquipoCompetencia)
			throws ElementoNoEncontradoException, CompetenciaException, ElementoDuplicadoException, AccionNoPermitidaException {
		buscarASCUN(idCompetencia).inscribirEquipoAGrupo(nombreGrupo, idEquipoCompetencia);
	}

	public void terminarFaseGrupos(String idCompetencia) throws ElementoNoEncontradoException, AccionNoPermitidaException {
		buscarASCUN(idCompetencia).terminarFaseGrupos();
	}

	public String consultarTablaPosiciones(String idCompetencia, String nombreGrupo) throws ElementoNoEncontradoException {
		StringBuilder texto = new StringBuilder("Tabla de posiciones" + (nombreGrupo == null || nombreGrupo.isBlank() ? "" : " grupo " + nombreGrupo) + ":\n");
		int posicion = 1;
		for (EquipoCompetencia equipo : central.getCompetencias().getTablaPosiciones(idCompetencia, nombreGrupo)) {
			texto.append("  ").append(posicion++).append(". ").append(equipo.getNombre()).append(" [")
					.append(equipo.getEquipoCompetenciaId()).append("]: ").append(equipo.getPuntos()).append(" puntos\n");
		}
		if (buscarCompetencia(idCompetencia) instanceof CompetenciaASCUN ascun && ascun.getFaseEliminacion().getEquiposRestantes().length > 0) {
			texto.append("Eliminación directa, siguen: ").append(String.join(", ", ascun.getFaseEliminacion().getEquiposRestantes()))
					.append("\n");
		}
		return texto.toString();
	}

	public void cerrarCompetencia(String idCompetencia) throws ElementoNoEncontradoException {
		central.getCompetencias().cerrarCompetencia(idCompetencia);
	}

	private Competencia buscarCompetencia(String idCompetencia) throws ElementoNoEncontradoException {
		Competencia competencia = central.getCompetencias().getCompetencia(idCompetencia);
		if (competencia == null) {
			throw new ElementoNoEncontradoException("No existe la competencia " + idCompetencia);
		}
		return competencia;
	}

	private CompetenciaASCUN buscarASCUN(String idCompetencia) throws ElementoNoEncontradoException, AccionNoPermitidaException {
		if (buscarCompetencia(idCompetencia) instanceof CompetenciaASCUN ascun) {
			return ascun;
		}
		throw new AccionNoPermitidaException("Solo las competencias ASCUN tienen fase de grupos");
	}

	@Override
	public String getRol() {
		return "Administrador";
	}
}
