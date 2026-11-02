package partidos;

import java.time.LocalDateTime;
import java.util.List;

import estadisticas.Estadistica;
import reservas.Reserva;

public class PartidoPractica extends Partido {

	private String reservador;
	private String disciplina;
	private String modalidad;
	private String[] equipo1;
	private String[] equipo2;
	private Reserva reserva;

	public PartidoPractica(String idPartido, String reservador, Reserva reserva, String[] equipo1, String[] equipo2,
			String disciplina) {
		this(idPartido, reserva.getFecha().atTime(reserva.getHoras()[0], 0), reservador, reserva, equipo1, equipo2, disciplina);
	}

	public PartidoPractica(String idPartido, LocalDateTime fecha, String reservador, Reserva reserva, String[] equipo1,
			String[] equipo2, String disciplina) {
		super(idPartido, fecha, disciplina, "Práctica");
		this.reservador = reservador;
		this.reserva = reserva;
		this.equipo1 = equipo1;
		this.equipo2 = equipo2;
		this.disciplina = disciplina;
		this.modalidad = equipo1.length + equipo2.length == 4 ? "dobles" : "sencillos";
	}

	public String getReservador() {
		return reservador;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public String getModalidad() {
		return modalidad;
	}

	public String[] getEquipo1() {
		return equipo1;
	}

	public String[] getEquipo2() {
		return equipo2;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public void registrarResultado(List<Estadistica> estadisticas) {
		for (Estadistica estadistica : estadisticas) {
			agregarEstadistica(estadistica);
		}
		setCerrado();
	}

	public void cancelar() {
		reserva = null;
		setCerrado();
	}

	@Override
	public void reemplazarJugador(String idJugador, String idAnonimo) {
		super.reemplazarJugador(idJugador, idAnonimo);
		equipo1 = reemplazar(equipo1, idJugador, idAnonimo);
		equipo2 = reemplazar(equipo2, idJugador, idAnonimo);
	}

	@Override
	public String getResumen() {
		return getEncabezado() + " | " + modalidad + " | reservó: " + reservador + " | "
				+ String.join(", ", equipo1) + " vs " + String.join(", ", equipo2) + getDetalle();
	}
}
