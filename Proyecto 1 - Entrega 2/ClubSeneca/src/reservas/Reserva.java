package reservas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import instalaciones.Instalacion;

public class Reserva {

	private String idReserva;
	private int[] horas;
	private LocalDate fecha;
	private String reservadoPor;
	private Instalacion instalacion;

	public Reserva(String idReserva, Instalacion instalacion, LocalDate fecha, int[] horas, String reservadoPor) {
		this.idReserva = idReserva;
		this.instalacion = instalacion;
		this.fecha = fecha;
		this.horas = horas;
		this.reservadoPor = reservadoPor;
	}

	public String getIdReserva() {
		return idReserva;
	}

	public int[] getHoras() {
		return horas;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public Instalacion getInstalacion() {
		return instalacion;
	}

	public String getReservadoPor() {
		return reservadoPor;
	}

	public Boolean cruzaCon(LocalDate fecha, int[] horas) {
		if (!this.fecha.equals(fecha)) {
			return false;
		}
		for (int hora : horas) {
			for (int propia : this.horas) {
				if (hora == propia) {
					return true;
				}
			}
		}
		return false;
	}

	public Boolean estaEnCurso(LocalDateTime momento) {
		return cruzaCon(momento.toLocalDate(), new int[] { momento.getHour() });
	}

	public Boolean esFutura() {
		LocalDateTime ahora = LocalDateTime.now();
		int ultima = Arrays.stream(horas).max().orElse(0);
		return fecha.atTime(ultima, 0).isAfter(ahora) || estaEnCurso(ahora);
	}

	@Override
	public String toString() {
		return idReserva + " | " + fecha + " | horas: " + Arrays.toString(horas) + " | instalación: " + instalacion.getInstalacionId()
				+ " (" + instalacion.getTipo() + ") | reservada por: " + reservadoPor;
	}
}
