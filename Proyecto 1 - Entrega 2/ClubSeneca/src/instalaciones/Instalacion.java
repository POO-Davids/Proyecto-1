package instalaciones;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;

import excepciones.InstalacionNoDisponibleException;

public class Instalacion {

	private static final int APERTURA = 6;
	private static final int CIERRE = 22;

	private String instalacionId;
	private String tipo;
	private int admite;
	private Boolean techada;
	private HashMap<LocalDate, Boolean[]> horario;
	private HashMap<LocalDate, Boolean[]> disponibilidad;

	public Instalacion(String instalacionId, String tipo, int admite, Boolean techada) {
		this(instalacionId, tipo, admite, techada, new HashMap<>(), new HashMap<>());
	}

	public Instalacion(String instalacionId, String tipo, int admite, Boolean techada, HashMap<LocalDate, Boolean[]> horario,
			HashMap<LocalDate, Boolean[]> disponibilidad) {
		this.instalacionId = instalacionId;
		this.tipo = tipo;
		this.admite = admite;
		this.techada = techada;
		this.horario = horario;
		this.disponibilidad = disponibilidad;
	}

	public String getInstalacionId() {
		return instalacionId;
	}

	public String getTipo() {
		return tipo;
	}

	public int getAdmite() {
		return admite;
	}

	public Boolean getTechada() {
		return techada;
	}

	public HashMap<LocalDate, Boolean[]> getHorario() {
		return horario;
	}

	public void setHorario(LocalDate fecha, Boolean[] nuevoHorario) {
		Boolean[] anterior = horarioDe(fecha);
		Boolean[] libres = disponibilidad.get(fecha);
		Boolean[] nuevaDisponibilidad = new Boolean[24];
		for (int h = 0; h < 24; h++) {
			boolean reservada = anterior[h] && !libres[h];
			nuevaDisponibilidad[h] = nuevoHorario[h] && !reservada;
		}
		horario.put(fecha, Arrays.copyOf(nuevoHorario, 24));
		disponibilidad.put(fecha, nuevaDisponibilidad);
	}

	public HashMap<LocalDate, Boolean[]> getDisponibilidad() {
		return disponibilidad;
	}

	public Boolean estaDisponible(LocalDate fecha, int[] horas) {
		if (fecha == null || horas == null || horas.length == 0) {
			return false;
		}
		Boolean[] libres = disponibilidadDe(fecha);
		for (int hora : horas) {
			if (hora < 0 || hora > 23 || !libres[hora]) {
				return false;
			}
		}
		return true;
	}

	public Boolean estaReservada(LocalDate fecha, int[] horas) {
		Boolean[] abierta = horarioDe(fecha);
		Boolean[] libres = disponibilidadDe(fecha);
		for (int hora : horas) {
			if (hora >= 0 && hora <= 23 && abierta[hora] && !libres[hora]) {
				return true;
			}
		}
		return false;
	}

	public Boolean admiteJugadores(int cantidad, String modalidad) {
		String modo = modalidad == null ? "" : modalidad.toLowerCase();
		switch (tipo.toLowerCase()) {
		case "tenis":
		case "mesa":
			if (modo.equals("sencillos")) {
				return cantidad == 2;
			}
			if (modo.equals("dobles")) {
				return cantidad == 4;
			}
			return cantidad == 2 || cantidad == 4;
		case "padel":
			return cantidad == 4;
		default:
			return cantidad > 0 && cantidad <= admite;
		}
	}

	public void agregarReserva(LocalDate fecha, int[] horas) throws InstalacionNoDisponibleException {
		if (!estaDisponible(fecha, horas)) {
			throw new InstalacionNoDisponibleException("La instalación " + instalacionId + " no está disponible en esas horas");
		}
		Boolean[] libres = disponibilidadDe(fecha);
		for (int hora : horas) {
			libres[hora] = false;
		}
	}

	public void liberarHoras(LocalDate fecha, int[] horas) {
		Boolean[] abierta = horarioDe(fecha);
		Boolean[] libres = disponibilidadDe(fecha);
		for (int hora : horas) {
			libres[hora] = abierta[hora];
		}
	}

	private Boolean[] horarioDe(LocalDate fecha) {
		if (!horario.containsKey(fecha)) {
			Boolean[] base = new Boolean[24];
			for (int h = 0; h < 24; h++) {
				base[h] = h >= APERTURA && h < CIERRE;
			}
			horario.put(fecha, base);
			disponibilidad.put(fecha, Arrays.copyOf(base, 24));
		}
		return horario.get(fecha);
	}

	private Boolean[] disponibilidadDe(LocalDate fecha) {
		horarioDe(fecha);
		return disponibilidad.get(fecha);
	}

	public static String horasComoTexto(Boolean[] horas) {
		StringBuilder texto = new StringBuilder();
		for (int h = 0; h < horas.length; h++) {
			if (horas[h]) {
				texto.append(texto.length() == 0 ? "" : ",").append(h);
			}
		}
		return texto.length() == 0 ? "ninguna" : texto.toString();
	}

	@Override
	public String toString() {
		StringBuilder texto = new StringBuilder(instalacionId + " | tipo: " + tipo + " | admite: " + admite + " | techada: "
				+ (techada ? "sí" : "no") + " | horario:");
		if (horario.isEmpty()) {
			texto.append(" " + APERTURA + ":00-" + CIERRE + ":00 (por defecto)");
		}
		horario.keySet().stream().sorted().forEach(fecha -> texto.append(" [").append(fecha).append(": ")
				.append(horasComoTexto(horario.get(fecha))).append("]"));
		return texto.toString();
	}
}
