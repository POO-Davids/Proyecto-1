package reservas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import excepciones.ElementoNoEncontradoException;
import excepciones.InstalacionNoDisponibleException;
import instalaciones.Instalacion;

public class CentralReservas {

	private LinkedHashMap<String, Reserva> reservas;
	private int contador;

	public CentralReservas() {
		reservas = new LinkedHashMap<>();
	}

	public Reserva crearReserva(Instalacion instalacion, LocalDate fecha, int[] horas, String reservadoPor)
			throws InstalacionNoDisponibleException {
		for (Reserva reserva : reservas.values()) {
			if (reserva.getInstalacion() == instalacion && reserva.cruzaCon(fecha, horas)) {
				throw new InstalacionNoDisponibleException("La reserva se cruza con " + reserva.getIdReserva());
			}
		}
		instalacion.agregarReserva(fecha, horas);
		Reserva reserva = new Reserva(generarId(), instalacion, fecha, horas, reservadoPor);
		reservas.put(reserva.getIdReserva(), reserva);
		return reserva;
	}

	public void agregarReserva(Reserva reserva) {
		reservas.put(reserva.getIdReserva(), reserva);
	}

	public void cancelarReserva(String idReserva) throws ElementoNoEncontradoException {
		Reserva reserva = reservas.remove(idReserva);
		if (reserva == null) {
			throw new ElementoNoEncontradoException("No existe la reserva " + idReserva);
		}
		reserva.getInstalacion().liberarHoras(reserva.getFecha(), reserva.getHoras());
	}

	public Reserva getReserva(String idReserva) {
		return reservas.get(idReserva);
	}

	public List<Reserva> getReservas() {
		return new ArrayList<>(reservas.values());
	}

	public List<Reserva> getReservasEntre(LocalDate inicio, LocalDate fin) {
		List<Reserva> lista = new ArrayList<>();
		for (Reserva reserva : reservas.values()) {
			if (!reserva.getFecha().isBefore(inicio) && !reserva.getFecha().isAfter(fin)) {
				lista.add(reserva);
			}
		}
		lista.sort(Comparator.comparing(Reserva::getFecha).thenComparingInt(r -> r.getHoras()[0]));
		return lista;
	}

	public List<Reserva> getReservasFueraDeHorario(String idInstalacion, LocalDate fecha, Boolean[] horario) {
		List<Reserva> lista = new ArrayList<>();
		for (Reserva reserva : reservas.values()) {
			if (reserva.getInstalacion().getInstalacionId().equals(idInstalacion) && reserva.getFecha().equals(fecha)) {
				for (int hora : reserva.getHoras()) {
					if (!horario[hora]) {
						lista.add(reserva);
						break;
					}
				}
			}
		}
		return lista;
	}

	public Reserva getReservaEnCurso(String username, LocalDateTime momento) {
		for (Reserva reserva : reservas.values()) {
			if (reserva.getReservadoPor().equals(username) && reserva.estaEnCurso(momento)) {
				return reserva;
			}
		}
		return null;
	}

	public Boolean tieneReservasFuturas(String idInstalacion) {
		for (Reserva reserva : reservas.values()) {
			if (reserva.getInstalacion().getInstalacionId().equals(idInstalacion) && reserva.esFutura()) {
				return true;
			}
		}
		return false;
	}

	public List<String> cancelarReservasFueraDeHorario(String idInstalacion, LocalDate fecha, Boolean[] horario)
			throws ElementoNoEncontradoException {
		List<String> canceladas = new ArrayList<>();
		for (Reserva reserva : getReservasFueraDeHorario(idInstalacion, fecha, horario)) {
			cancelarReserva(reserva.getIdReserva());
			canceladas.add(reserva.getIdReserva());
		}
		return canceladas;
	}

	private String generarId() {
		String id;
		do {
			id = "RES-" + (++contador);
		} while (reservas.containsKey(id));
		return id;
	}
}
