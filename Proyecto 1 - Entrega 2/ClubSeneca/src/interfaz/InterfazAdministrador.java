package interfaz;

import java.time.LocalDate;
import java.util.Scanner;

import controladores.ControladorAdministrador;
import estadisticas.Estadistica;
import estadisticas.EstadisticaBasket;
import estadisticas.EstadisticaFutbol;
import estadisticas.EstadisticaTenisMesa;
import estadisticas.EstadisticaTenisPadel;
import estadisticas.EstadisticaVolley;
import excepciones.ClubSenecaException;
import instalaciones.Instalacion;
import jugadores.Jugador;
import jugadores.JugadorConjunto;
import jugadores.JugadorIndividual;
import tiendas.Articulo;
import tiendas.ArticuloTienda;
import tiendas.Bebida;
import tiendas.Snack;
import usuarios.Administrador;
import usuarios.Entrenador;
import usuarios.Fisioterapeuta;
import usuarios.SolicitudCambioTurno;
import usuarios.Socio;
import usuarios.Usuario;

public class InterfazAdministrador extends InterfazRol {

	public InterfazAdministrador(ControladorAdministrador controlador, Scanner entrada) {
		super(controlador, entrada);
	}

	private ControladorAdministrador admin() {
		return (ControladorAdministrador) controlador;
	}

	@Override
	protected String[] getOpciones() {
		return new String[] { "Registrar usuario", "Crear equipo", "Dar de baja equipo", "Crear jugador", "Dar de baja jugador",
				"Consultar inventario", "Mover artículo", "Abastecer", "Agregar artículo", "Resolver sugerencia", "Asignar turno",
				"Consultar solicitudes de cambio de turno", "Resolver cambio de turno", "Generar reporte",
				"Registrar pago de mensualidad", "Agregar instalación", "Fijar máximo de instalaciones simultáneas",
				"Eliminar instalación", "Cambiar horario de instalación", "Consultar historial", "Programar partido interno",
				"Registrar partido de competencia", "Registrar marcador", "Registrar estadística", "Registrar amonestación",
				"Crear competencia", "Inscribir equipo a competencia", "Agregar equipo de afuera", "Crear grupo",
				"Agregar equipo a grupo", "Terminar fase de grupos", "Consultar tabla de posiciones", "Cerrar competencia" };
	}

	public void registrarUsuario() throws ClubSenecaException {
		String tipo = leerTexto("Tipo (Socio, Entrenador, Fisioterapeuta, Administrador)");
		String username = leerTexto("Username");
		String password = leerTexto("Password");
		Usuario usuario = switch (tipo.toLowerCase()) {
		case "socio" -> new Socio(username, password, leerTexto("Nombre"), String.valueOf(leerEntero("Edad")));
		case "entrenador" -> new Entrenador(username, password, leerTexto("Código de descuento de tienda"));
		case "fisioterapeuta" -> new Fisioterapeuta(username, password, leerTexto("Código de descuento de tienda"));
		default -> new Administrador(username, password);
		};
		admin().registrarUsuario(usuario);
		mostrarMensaje("Usuario registrado");
	}

	public void crearEquipo() throws ClubSenecaException {
		admin().crearEquipo(leerTexto("Nombre"), leerTexto("Deporte (Fútbol, Baloncesto, Voleibol)"),
				leerTexto("Categoría (Sub-13, Sub-15, Sub-17, Mayores)"), leerEntero("Cupo"));
		mostrarMensaje("Equipo creado");
	}

	public void darDeBajaEquipo() throws ClubSenecaException {
		admin().darDeBajaEquipo(leerTexto("Id del equipo"));
		mostrarMensaje("Equipo dado de baja");
	}

	public void crearJugador() throws ClubSenecaException {
		String usernameSocio = leerTexto("Username del socio");
		Jugador jugador;
		if (leerTexto("Tipo (individual o conjunto)").equalsIgnoreCase("individual")) {
			jugador = new JugadorIndividual(null, null, leerTexto("Disciplina (tenis, padel, tenis de mesa)"),
					leerTexto("Nivel (Principiante, Intermedio, Avanzado)"), leerDecimal("ELO inicial"));
		} else {
			String idEquipo = leerTexto("Id del equipo");
			jugador = new JugadorConjunto(null, null, leerTexto("Posición"), leerEntero("Camiseta"), idEquipo, "");
		}
		admin().crearJugador(usernameSocio, jugador);
		mostrarMensaje("Jugador creado");
	}

	public void darDeBajaJugador() throws ClubSenecaException {
		admin().darDeBajaJugador(leerTexto("Id del jugador"));
		mostrarMensaje("Jugador dado de baja");
	}

	public void consultarInventario() {
		mostrarMensaje(admin().consultarInventario());
	}

	public void moverArticulo() throws ClubSenecaException {
		admin().moverArticulo(leerTexto("Artículo"), leerTexto("Ubicación de origen"), leerTexto("Ubicación de destino"),
				leerEntero("Cantidad"));
		mostrarMensaje("Artículo movido");
	}

	public void abastecer() throws ClubSenecaException {
		admin().abastecer(leerTexto("Tienda o Cafeteria"), leerTexto("Artículo"), leerEntero("Cantidad"));
		mostrarMensaje("Artículo abastecido");
	}

	public void agregarArticulo() throws ClubSenecaException {
		String tienda = leerTexto("Tienda o Cafeteria");
		String nombre = leerTexto("Nombre");
		float precio = leerDecimal("Precio");
		Articulo articulo;
		if (tienda.equalsIgnoreCase("Tienda")) {
			articulo = new ArticuloTienda(nombre, precio, leerTexto("Categoría"));
		} else if (leerTexto("Bebida o Snack").equalsIgnoreCase("Bebida")) {
			articulo = new Bebida(nombre, precio, leerSiNo("¿Es caliente?"));
		} else {
			articulo = new Snack(nombre, precio, leerLista("Alérgenos"));
		}
		admin().agregarArticulo(tienda, articulo);
		mostrarMensaje("Artículo agregado con disponibilidad 0; use Abastecer para agregar unidades");
	}

	public void resolverSugerencia() throws ClubSenecaException {
		String tienda = leerTexto("Tienda o Cafeteria");
		String nombre = leerTexto("Artículo sugerido");
		boolean aceptada = leerSiNo("¿Aceptar la sugerencia?");
		admin().resolverSugerencia(tienda, nombre, aceptada);
		mostrarMensaje(aceptada ? "Sugerencia aceptada" : "Sugerencia denegada");
	}

	public void asignarTurno() throws ClubSenecaException {
		admin().asignarTurno(leerTexto("Username del empleado"), leerFecha("Fecha"), leerHorario("Horas del turno"));
		mostrarMensaje("Turno asignado");
	}

	public void consultarSolicitudesCambioTurno() {
		StringBuilder texto = new StringBuilder("Solicitudes pendientes:\n");
		for (SolicitudCambioTurno solicitud : admin().consultarSolicitudesCambioTurno()) {
			texto.append(solicitud.getIdSolicitud()).append(" | ").append(solicitud.getEmpleado().getUsername()).append(" (")
					.append(solicitud.getEmpleado().getTipo()).append(") | horas: ")
					.append(Instalacion.horasComoTexto(solicitud.getTurno())).append("\n");
		}
		mostrarMensaje(texto.toString());
	}

	public void resolverCambioTurno() throws ClubSenecaException {
		boolean aprobada = admin().resolverCambioTurno(leerTexto("Id de la solicitud"), leerSiNo("¿Aprobar la solicitud?"));
		mostrarMensaje(aprobada ? "Solicitud aprobada" : "Solicitud rechazada");
	}

	public void generarReporte() throws ClubSenecaException {
		String tipo = leerTexto("Tipo de reporte (ventas, deportivo, asistencia)");
		if (tipo.equalsIgnoreCase("ventas")) {
			String rubro = leerTexto("Rubro (tienda, cafeteria, mensualidades, todos)");
			String periodo = leerTexto("Periodo (diario, semanal, mensual)").toLowerCase();
			LocalDate inicio = leerFecha("Fecha de inicio");
			LocalDate fin = switch (periodo) {
			case "semanal" -> inicio.plusDays(6);
			case "mensual" -> inicio.withDayOfMonth(inicio.lengthOfMonth());
			default -> inicio;
			};
			if (periodo.equals("mensual")) {
				inicio = inicio.withDayOfMonth(1);
			}
			mostrarMensaje(admin().generarReporte(tipo, rubro, inicio, fin));
		} else {
			String filtro = tipo.equalsIgnoreCase("deportivo") ? leerTexto("Filtro (id de competencia, deporte, equipo o vacío)") : "";
			mostrarMensaje(admin().generarReporte(tipo, filtro, leerFecha("Fecha de inicio"), leerFecha("Fecha de fin")));
		}
	}

	public void registrarPagoMensualidad() throws ClubSenecaException {
		admin().registrarPagoMensualidad(leerTexto("Username del socio"), leerDecimal("Valor"), leerFecha("Fecha"));
		mostrarMensaje("Pago registrado");
	}

	public void agregarInstalacion() throws ClubSenecaException {
		admin().agregarInstalacion(leerTexto("Tipo (tenis, padel, mesa, grupal)"), leerEntero("Jugadores que admite"),
				leerSiNo("¿Es techada?"));
		mostrarMensaje("Instalación agregada");
	}

	public void setMaximoInstalacionesSimultaneas() throws ClubSenecaException {
		admin().setMaximoInstalacionesSimultaneas(leerTexto("Tipo"), leerEntero("Máximo"));
		mostrarMensaje("Máximo actualizado");
	}

	public void eliminarInstalacion() throws ClubSenecaException {
		admin().eliminarInstalacion(leerTexto("Id de la instalación"));
		mostrarMensaje("Instalación eliminada");
	}

	public void cambiarHorarioInstalacion() throws ClubSenecaException {
		admin().cambiarHorarioInstalacion(leerTexto("Id de la instalación"), leerFecha("Fecha"), leerHorario("Horas abiertas"));
		mostrarMensaje("Horario actualizado");
	}

	public void consultarHistorial() throws ClubSenecaException {
		mostrarMensaje(admin().consultarHistorial(leerTexto("Tipo (partidos, entrenamientos, reservas, todo)"), leerFecha("Inicio"),
				leerFecha("Fin")));
	}

	public void programarPartidoInterno() throws ClubSenecaException {
		admin().programarPartidoInterno(leerTexto("Id del equipo 1"), leerTexto("Id del equipo 2"), leerFechaHora("Fecha"),
				leerLista("Jugadores habilitados del equipo 1"), leerLista("Jugadores habilitados del equipo 2"));
		mostrarMensaje("Partido programado");
	}

	public void registrarPartidoCompetencia() throws ClubSenecaException {
		admin().registrarPartidoCompetencia(leerTexto("Id de la competencia"), leerFechaHora("Fecha"), leerTexto("Nombre del rival"),
				leerTexto("Escenario"), leerLista("Jugadores habilitados de la nómina"));
		mostrarMensaje("Partido registrado");
	}

	public void registrarMarcador() throws ClubSenecaException {
		admin().registrarMarcador(leerTexto("Id del partido"), leerEntero("Puntuación equipo 1 / propio"),
				leerEntero("Puntuación equipo 2 / rival"));
		mostrarMensaje("Marcador registrado");
	}

	public void registrarEstadistica() throws ClubSenecaException {
		String idPartido = leerTexto("Id del partido");
		String idJugador = leerTexto("Id del jugador");
		Estadistica estadistica = switch (leerTexto("Tipo (futbol, basket, volley, tenis, mesa)").toLowerCase()) {
		case "basket" -> new EstadisticaBasket(idJugador, leerEntero("Puntos"), leerEntero("Rebotes"));
		case "volley" -> new EstadisticaVolley(idJugador, leerEntero("Aces"), leerEntero("Bloqueos"));
		case "tenis" -> new EstadisticaTenisPadel(idJugador, leerEntero("Sets ganados"), leerEntero("Games ganados"),
				leerEntero("Aces"), leerEntero("Errores no forzados"));
		case "mesa" -> new EstadisticaTenisMesa(idJugador, leerEntero("Sets ganados"), leerEntero("Puntos totales"));
		default -> new EstadisticaFutbol(idJugador, leerEntero("Goles"), leerEntero("Asistencias"));
		};
		admin().registrarEstadistica(idPartido, idJugador, estadistica);
		mostrarMensaje("Estadística registrada");
	}

	public void registrarAmonestacion() throws ClubSenecaException {
		admin().registrarAmonestacion(leerTexto("Id del partido"), leerTexto("Id del jugador"), leerEntero("Amarillas"),
				leerEntero("Rojas"));
		mostrarMensaje("Amonestación registrada");
	}

	public void crearCompetencia() throws ClubSenecaException {
		admin().crearCompetencia(leerTexto("Tipo (ASCUN o Cerros)"), leerTexto("Nombre"), leerTexto("Entidad"), leerTexto("Temporada"),
				leerTexto("Deporte"), leerTexto("Categoría"), leerTexto("Formato"), leerEntero("Nómina mínima"),
				leerEntero("Máximo de amarillas"));
		mostrarMensaje("Competencia creada");
	}

	public void inscribirEquipoACompetencia() throws ClubSenecaException {
		admin().inscribirEquipoACompetencia(leerTexto("Id de la competencia"), leerTexto("Id del equipo"), leerLista("Nómina"));
		mostrarMensaje("Equipo inscrito");
	}

	public void agregarEquipoAfuera() throws ClubSenecaException {
		admin().agregarEquipoAfuera(leerTexto("Id de la competencia"), leerTexto("Nombre del equipo de afuera"));
		mostrarMensaje("Equipo agregado");
	}

	public void crearGrupo() throws ClubSenecaException {
		admin().crearGrupo(leerTexto("Id de la competencia"), leerTexto("Nombre del grupo"));
		mostrarMensaje("Grupo creado");
	}

	public void agregarEquipoAGrupo() throws ClubSenecaException {
		admin().agregarEquipoAGrupo(leerTexto("Id de la competencia"), leerTexto("Nombre del grupo"),
				leerTexto("Id del equipo en la competencia (id del equipo propio o nombre del rival)"));
		mostrarMensaje("Equipo agregado al grupo");
	}

	public void terminarFaseGrupos() throws ClubSenecaException {
		admin().terminarFaseGrupos(leerTexto("Id de la competencia"));
		mostrarMensaje("Fase de grupos terminada");
	}

	public void consultarTablaPosiciones() throws ClubSenecaException {
		mostrarMensaje(admin().consultarTablaPosiciones(leerTexto("Id de la competencia"), leerTexto("Grupo (vacío si es liga)")));
	}

	public void cerrarCompetencia() throws ClubSenecaException {
		admin().cerrarCompetencia(leerTexto("Id de la competencia"));
		mostrarMensaje("Competencia cerrada");
	}

	@Override
	public void ejecutarOpcion(int opcion) {
		try {
			switch (opcion) {
			case 1 -> registrarUsuario();
			case 2 -> crearEquipo();
			case 3 -> darDeBajaEquipo();
			case 4 -> crearJugador();
			case 5 -> darDeBajaJugador();
			case 6 -> consultarInventario();
			case 7 -> moverArticulo();
			case 8 -> abastecer();
			case 9 -> agregarArticulo();
			case 10 -> resolverSugerencia();
			case 11 -> asignarTurno();
			case 12 -> consultarSolicitudesCambioTurno();
			case 13 -> resolverCambioTurno();
			case 14 -> generarReporte();
			case 15 -> registrarPagoMensualidad();
			case 16 -> agregarInstalacion();
			case 17 -> setMaximoInstalacionesSimultaneas();
			case 18 -> eliminarInstalacion();
			case 19 -> cambiarHorarioInstalacion();
			case 20 -> consultarHistorial();
			case 21 -> programarPartidoInterno();
			case 22 -> registrarPartidoCompetencia();
			case 23 -> registrarMarcador();
			case 24 -> registrarEstadistica();
			case 25 -> registrarAmonestacion();
			case 26 -> crearCompetencia();
			case 27 -> inscribirEquipoACompetencia();
			case 28 -> agregarEquipoAfuera();
			case 29 -> crearGrupo();
			case 30 -> agregarEquipoAGrupo();
			case 31 -> terminarFaseGrupos();
			case 32 -> consultarTablaPosiciones();
			case 33 -> cerrarCompetencia();
			default -> super.ejecutarOpcion(opcion);
			}
		} catch (ClubSenecaException e) {
			mostrarError(e);
		}
	}
}
