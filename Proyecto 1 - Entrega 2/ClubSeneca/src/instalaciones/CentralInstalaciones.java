package instalaciones;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import excepciones.ElementoNoEncontradoException;
import excepciones.ValorInvalidoException;

public class CentralInstalaciones {

	private HashMap<String, Integer> maximoSimultaneas;
	private LinkedHashMap<String, Instalacion> instalaciones;
	private int contador;

	public CentralInstalaciones() {
		maximoSimultaneas = new HashMap<>();
		instalaciones = new LinkedHashMap<>();
	}

	public void agregarInstalacion(String tipo, int admite, Boolean techada) throws ValorInvalidoException {
		if (admite <= 0) {
			throw new ValorInvalidoException("La instalación debe admitir al menos un jugador");
		}
		String id = generarId();
		instalaciones.put(id, new Instalacion(id, tipo.toLowerCase(), admite, techada));
	}

	public void agregarInstalacion(Instalacion instalacion) {
		instalaciones.put(instalacion.getInstalacionId(), instalacion);
	}

	public void setMaximoSimultaneas(String tipo, int maximo) {
		maximoSimultaneas.put(tipo.toLowerCase(), maximo);
	}

	public HashMap<String, Integer> getMaximoSimultaneas() {
		return maximoSimultaneas;
	}

	public Boolean hayCupoSimultaneo(String tipo, LocalDate fecha, int[] horas) {
		Integer maximo = maximoSimultaneas.get(tipo.toLowerCase());
		if (maximo == null) {
			return true;
		}
		int ocupadas = 0;
		for (Instalacion instalacion : getInstalacionesPorTipo(tipo)) {
			if (instalacion.estaReservada(fecha, horas)) {
				ocupadas++;
			}
		}
		return ocupadas < maximo;
	}

	public void eliminarInstalacion(String idInstalacion) throws ElementoNoEncontradoException {
		if (instalaciones.remove(idInstalacion) == null) {
			throw new ElementoNoEncontradoException("No existe la instalación " + idInstalacion);
		}
	}

	public Instalacion getInstalacion(String idInstalacion) {
		return instalaciones.get(idInstalacion);
	}

	public List<Instalacion> getInstalacionesPorTipo(String tipo) {
		List<Instalacion> lista = new ArrayList<>();
		for (Instalacion instalacion : instalaciones.values()) {
			if (instalacion.getTipo().equalsIgnoreCase(tipo)) {
				lista.add(instalacion);
			}
		}
		return lista;
	}

	public List<Instalacion> getInstalaciones() {
		return new ArrayList<>(instalaciones.values());
	}

	public void cambiarHorario(String idInstalacion, LocalDate fecha, Boolean[] horario) throws ElementoNoEncontradoException {
		Instalacion instalacion = getInstalacion(idInstalacion);
		if (instalacion == null) {
			throw new ElementoNoEncontradoException("No existe la instalación " + idInstalacion);
		}
		instalacion.setHorario(fecha, horario);
	}

	public String consultarDisponibilidad(String tipo, LocalDate fecha) {
		StringBuilder texto = new StringBuilder("== Disponibilidad " + tipo + " el " + fecha + " ==\n");
		for (Instalacion instalacion : getInstalacionesPorTipo(tipo)) {
			instalacion.estaDisponible(fecha, new int[0]);
			texto.append(instalacion.getInstalacionId()).append(" (admite ").append(instalacion.getAdmite())
					.append(instalacion.getTechada() ? ", techada" : "").append("): horas libres ")
					.append(Instalacion.horasComoTexto(instalacion.getDisponibilidad().get(fecha))).append("\n");
		}
		return texto.toString();
	}

	public String consultarInventario() {
		StringBuilder texto = new StringBuilder("== Instalaciones ==\n");
		for (Instalacion instalacion : instalaciones.values()) {
			texto.append(instalacion).append("\n");
		}
		return texto.toString();
	}

	private String generarId() {
		String id;
		do {
			id = "INS-" + (++contador);
		} while (instalaciones.containsKey(id));
		return id;
	}
}
