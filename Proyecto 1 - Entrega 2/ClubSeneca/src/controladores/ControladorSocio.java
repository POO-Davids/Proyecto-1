package controladores;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import estadisticas.Estadistica;
import excepciones.AccionNoPermitidaException;
import excepciones.BebidaCalienteException;
import excepciones.CapacidadInstalacionException;
import excepciones.ElementoNoEncontradoException;
import excepciones.EquiposIncompatiblesException;
import excepciones.InstalacionException;
import excepciones.InstalacionNoDisponibleException;
import excepciones.MarcadorInvalidoException;
import excepciones.MaximoSimultaneasException;
import excepciones.NivelIncompatibleException;
import excepciones.PartidoException;
import instalaciones.CentralInstalaciones;
import instalaciones.Instalacion;
import jugadores.CentralJugadores;
import jugadores.Jugador;
import jugadores.JugadorIndividual;
import partidos.Partido;
import partidos.PartidoPractica;
import reservas.Reserva;
import usuarios.Usuario;

public class ControladorSocio extends ControladorUsuario {

	public ControladorSocio(CentralClubSeneca central, Usuario usuario) {
		super(central, usuario);
	}

	public String consultarDisponibilidadInstalaciones(String tipo, LocalDate fecha) {
		return central.getInstalaciones().consultarDisponibilidad(tipo, fecha);
	}

	public void reservarInstalacion(String idInstalacion, LocalDate fecha, int[] horas, String modalidad, String[] equipo1,
			String[] equipo2) throws ElementoNoEncontradoException, InstalacionException, BebidaCalienteException, PartidoException {
		CentralInstalaciones instalaciones = central.getInstalaciones();
		Instalacion instalacion = instalaciones.getInstalacion(idInstalacion);
		if (instalacion == null) {
			throw new ElementoNoEncontradoException("No existe la instalación " + idInstalacion);
		}
		if (horas.length == 0) {
			throw new InstalacionNoDisponibleException("Debe indicar al menos una hora");
		}
		if (!instalacion.admiteJugadores(equipo1.length + equipo2.length, modalidad)) {
			throw new CapacidadInstalacionException("La instalación de " + instalacion.getTipo() + " no admite "
					+ (equipo1.length + equipo2.length) + " jugadores en modalidad " + modalidad);
		}
		if (!instalaciones.hayCupoSimultaneo(instalacion.getTipo(), fecha, horas)) {
			throw new MaximoSimultaneasException("Se alcanzó el máximo de instalaciones simultáneas de " + instalacion.getTipo());
		}
		if (!instalacion.estaDisponible(fecha, horas)) {
			throw new InstalacionNoDisponibleException("La instalación no está disponible en ese bloque");
		}
		if (verificarBebidaCaliente(instalacion) && usuario.tieneBebidaCaliente()) {
			throw new BebidaCalienteException("No puede usar esta instalación con una bebida caliente sin consumir");
		}
		String disciplina = switch (instalacion.getTipo()) {
		case "tenis" -> "tenis";
		case "padel" -> "padel";
		case "mesa" -> "tenis de mesa";
		default -> null;
		};
		CentralJugadores jugadores = central.getJugadores();
		if (disciplina != null) {
			List<String> todos = new ArrayList<>(Arrays.asList(equipo1));
			todos.addAll(Arrays.asList(equipo2));
			for (String id : todos) {
				Jugador jugador = jugadores.getJugadorPorId(id);
				if (!(jugador instanceof JugadorIndividual individual) || !individual.getDisciplina().equalsIgnoreCase(disciplina)) {
					throw new EquiposIncompatiblesException("El jugador " + id + " no juega " + disciplina);
				}
			}
			if (!jugadores.verificarNivelesEquipos(equipo1, equipo2)) {
				throw new NivelIncompatibleException("Los niveles de los jugadores no son iguales ni adyacentes");
			}
		}
		Reserva reserva = central.getReservas().crearReserva(instalacion, fecha, horas, usuario.getUsername());
		if (disciplina != null) {
			central.getPartidos().crearPartidoPractica(usuario.getUsername(), reserva, equipo1, equipo2, disciplina);
		}
	}

	public void cancelarReserva(String idReserva) throws ElementoNoEncontradoException, AccionNoPermitidaException {
		Reserva reserva = central.getReservas().getReserva(idReserva);
		if (reserva == null) {
			throw new ElementoNoEncontradoException("No existe la reserva " + idReserva);
		}
		if (!reserva.getReservadoPor().equals(usuario.getUsername())) {
			throw new AccionNoPermitidaException("La reserva " + idReserva + " no es suya");
		}
		central.getReservas().cancelarReserva(idReserva);
		central.getPartidos().cancelarPartidoPorReserva(idReserva);
		central.getSesionesEntrenamiento().cancelarSesionPorReserva(idReserva);
	}

	public void registrarResultadoPartidoPractica(String idPartido, List<Estadistica> estadisticas)
			throws ElementoNoEncontradoException, MarcadorInvalidoException {
		Partido partido = central.getPartidos().getPartido(idPartido);
		if (!(partido instanceof PartidoPractica practica)) {
			throw new ElementoNoEncontradoException("No existe el partido de práctica " + idPartido);
		}
		if (practica.getCerrado()) {
			throw new MarcadorInvalidoException("El partido " + idPartido + " ya tiene resultado");
		}
		List<String> equipo1 = Arrays.asList(practica.getEquipo1());
		List<String> equipo2 = Arrays.asList(practica.getEquipo2());
		int puntos1 = 0;
		int puntos2 = 0;
		for (Estadistica estadistica : estadisticas) {
			if (equipo1.contains(estadistica.getUsuarioUsername())) {
				puntos1 += estadistica.getValorPrincipal();
			} else if (equipo2.contains(estadistica.getUsuarioUsername())) {
				puntos2 += estadistica.getValorPrincipal();
			} else {
				throw new ElementoNoEncontradoException("El jugador " + estadistica.getUsuarioUsername() + " no jugó el partido");
			}
		}
		if (puntos1 == puntos2) {
			throw new MarcadorInvalidoException("El resultado no puede quedar empatado");
		}
		practica.registrarResultado(estadisticas);
		CentralJugadores jugadores = central.getJugadores();
		for (Estadistica estadistica : estadisticas) {
			jugadores.getJugadorPorId(estadistica.getUsuarioUsername()).acumularEstadistica(estadistica);
		}
		if (puntos1 > puntos2) {
			jugadores.actualizarElo(practica.getEquipo1(), practica.getEquipo2());
		} else {
			jugadores.actualizarElo(practica.getEquipo2(), practica.getEquipo1());
		}
	}

	@Override
	public String getRol() {
		return "Socio";
	}
}
