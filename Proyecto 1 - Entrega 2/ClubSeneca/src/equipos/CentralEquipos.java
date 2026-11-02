package equipos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import jugadores.Jugador;

public class CentralEquipos {

	public static final String ID_ANONIMO = "EQ-ANONIMO";

	private String[] idEquipos;
	private LinkedHashMap<String, Equipo> equipos;
	private Equipo anonimo;
	private int contador;

	public CentralEquipos() {
		idEquipos = new String[0];
		equipos = new LinkedHashMap<>();
	}

	public void inscribirJugador(Jugador jugador, Equipo equipo) {
		equipo.inscribirJugador(jugador);
	}

	public void quitarJugador(Jugador jugador, Equipo equipo) {
		equipo.quitarJugador(jugador);
	}

	public void crearEquipo(String nombre, String deporte, String categoria, int cupo) {
		agregarEquipo(new Equipo(generarId(), nombre, deporte, categoria, cupo));
	}

	public void agregarEquipo(Equipo equipo) {
		equipos.put(equipo.getIdEquipo(), equipo);
		idEquipos = Arrays.copyOf(idEquipos, idEquipos.length + 1);
		idEquipos[idEquipos.length - 1] = equipo.getIdEquipo();
	}

	public void quitarEquipo(String idEquipo) {
		equipos.remove(idEquipo);
		idEquipos = Arrays.stream(idEquipos).filter(id -> !id.equals(idEquipo)).toArray(String[]::new);
	}

	public Equipo getEquipo(String idEquipo) {
		if (ID_ANONIMO.equals(idEquipo)) {
			return getEquipoAnonimo();
		}
		return equipos.get(idEquipo);
	}

	public Equipo buscarPorNombre(String nombre) {
		for (Equipo equipo : equipos.values()) {
			if (equipo.getNombre().equalsIgnoreCase(nombre)) {
				return equipo;
			}
		}
		return null;
	}

	public List<Equipo> getEquipos() {
		return new ArrayList<>(equipos.values());
	}

	public String[] getIdEquipos() {
		return idEquipos;
	}

	public Boolean existeEquipo(String nombre, String deporte, String categoria) {
		for (Equipo equipo : equipos.values()) {
			if (equipo.getNombre().equalsIgnoreCase(nombre) && equipo.getDeporte().equalsIgnoreCase(deporte)
					&& equipo.getCategoria().equalsIgnoreCase(categoria)) {
				return true;
			}
		}
		return false;
	}

	public List<Equipo> getEquiposPorDeporte(String deporte) {
		List<Equipo> lista = new ArrayList<>();
		for (Equipo equipo : equipos.values()) {
			if (equipo.getDeporte().equalsIgnoreCase(deporte)) {
				lista.add(equipo);
			}
		}
		return lista;
	}

	public Equipo getEquipoAnonimo() {
		if (anonimo == null) {
			anonimo = new Equipo(ID_ANONIMO, "Equipo anónimo", "", "", 0);
		}
		return anonimo;
	}

	private String generarId() {
		String id;
		do {
			id = "EQ-" + (++contador);
		} while (equipos.containsKey(id));
		return id;
	}
}
