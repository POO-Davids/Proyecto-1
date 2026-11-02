package interfaz;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controladores.ControladorSocio;
import estadisticas.Estadistica;
import estadisticas.EstadisticaTenisMesa;
import estadisticas.EstadisticaTenisPadel;
import excepciones.ClubSenecaException;

public class InterfazSocio extends InterfazUsuario {

	public InterfazSocio(ControladorSocio controlador, Scanner entrada) {
		super(controlador, entrada);
	}

	private ControladorSocio socio() {
		return (ControladorSocio) controlador;
	}

	@Override
	protected String[] getOpciones() {
		return unir(super.getOpciones(), "Consultar disponibilidad de instalaciones", "Reservar instalación (partido de práctica)",
				"Cancelar reserva", "Registrar resultado de partido de práctica");
	}

	public void consultarDisponibilidadInstalaciones() {
		mostrarMensaje(socio().consultarDisponibilidadInstalaciones(leerTexto("Tipo (tenis, padel, mesa, grupal)"), leerFecha("Fecha")));
	}

	public void reservarInstalacion() throws ClubSenecaException {
		socio().reservarInstalacion(leerTexto("Id de la instalación"), leerFecha("Fecha"), leerEnteros("Horas"),
				leerTexto("Modalidad (sencillos o dobles)"), leerLista("Ids de los jugadores del equipo 1"),
				leerLista("Ids de los jugadores del equipo 2"));
		mostrarMensaje("Reserva creada");
	}

	public void cancelarReserva() throws ClubSenecaException {
		socio().cancelarReserva(leerTexto("Id de la reserva"));
		mostrarMensaje("Reserva cancelada");
	}

	public void registrarResultadoPartidoPractica() throws ClubSenecaException {
		String idPartido = leerTexto("Id del partido");
		boolean mesa = leerTexto("Disciplina (tenis, padel o mesa)").equalsIgnoreCase("mesa");
		int jugadores = leerEntero("Número de jugadores");
		List<Estadistica> estadisticas = new ArrayList<>();
		for (int i = 0; i < jugadores; i++) {
			String idJugador = leerTexto("Id del jugador " + (i + 1));
			if (mesa) {
				estadisticas.add(new EstadisticaTenisMesa(idJugador, leerEntero("Sets ganados"), leerEntero("Puntos totales")));
			} else {
				estadisticas.add(new EstadisticaTenisPadel(idJugador, leerEntero("Sets ganados"), leerEntero("Games ganados"),
						leerEntero("Aces"), leerEntero("Errores no forzados")));
			}
		}
		socio().registrarResultadoPartidoPractica(idPartido, estadisticas);
		mostrarMensaje("Resultado registrado");
	}

	@Override
	public void ejecutarOpcion(int opcion) {
		try {
			switch (opcion) {
			case 1 -> comprar();
			case 2 -> cotizarCompraCafeteria();
			case 3 -> consultarAlergenos();
			case 4 -> consumirBebida();
			case 5 -> consultarPuntosFidelidad();
			case 6 -> consultarDisponibilidadInstalaciones();
			case 7 -> reservarInstalacion();
			case 8 -> cancelarReserva();
			case 9 -> registrarResultadoPartidoPractica();
			default -> super.ejecutarOpcion(opcion);
			}
		} catch (ClubSenecaException e) {
			mostrarError(e);
		}
	}
}
