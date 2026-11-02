package equipos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import amonestaciones.Amonestacion;
import jugadores.Jugador;
import jugadores.JugadorConjunto;

public class Equipo {

	private String nombre;
	private String deporte;
	private String categoria;
	private int cupo;
	private String[] jugadores;
	private String idEquipo;
	private HashMap<String, Integer> camisetas;
	private HashMap<String, List<Amonestacion>> amonestacionesLocalesJugadores;

	public Equipo(String idEquipo, String nombre, String deporte, String categoria, int cupo) {
		this.idEquipo = idEquipo;
		this.nombre = nombre;
		this.deporte = deporte;
		this.categoria = categoria;
		this.cupo = cupo;
		this.jugadores = new String[0];
		this.camisetas = new HashMap<>();
		this.amonestacionesLocalesJugadores = new HashMap<>();
	}

	public String getIdEquipo() {
		return idEquipo;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDeporte() {
		return deporte;
	}

	public String getCategoria() {
		return categoria;
	}

	public int getCupo() {
		return cupo;
	}

	public String[] getJugadoresIds() {
		return jugadores;
	}

	public Boolean tieneCupo() {
		return jugadores.length < cupo;
	}

	public Boolean camisetaDisponible(int camiseta) {
		return !camisetas.containsValue(camiseta);
	}

	public void inscribirJugador(Jugador jugador) {
		jugadores = Arrays.copyOf(jugadores, jugadores.length + 1);
		jugadores[jugadores.length - 1] = jugador.getIdJugador();
		if (jugador instanceof JugadorConjunto conjunto) {
			camisetas.put(jugador.getIdJugador(), conjunto.getCamiseta());
		}
	}

	public void quitarJugador(Jugador jugador) {
		jugadores = Arrays.stream(jugadores).filter(id -> !id.equals(jugador.getIdJugador())).toArray(String[]::new);
		camisetas.remove(jugador.getIdJugador());
	}

	public void agregarAmonestacion(String idJugador, Amonestacion amonestacion) {
		amonestacionesLocalesJugadores.computeIfAbsent(idJugador, id -> new ArrayList<>()).add(amonestacion);
	}

	public List<Amonestacion> getAmonestaciones(String idJugador) {
		return amonestacionesLocalesJugadores.getOrDefault(idJugador, new ArrayList<>());
	}

	@Override
	public String toString() {
		return idEquipo + " | " + nombre + " | " + deporte + " | " + categoria + " | jugadores: " + jugadores.length + "/" + cupo;
	}
}
