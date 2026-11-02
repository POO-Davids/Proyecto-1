package jugadores;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import equipos.Equipo;
import excepciones.CamisetaRepetidaException;
import excepciones.CategoriaEdadException;
import excepciones.CupoEquipoExcedidoException;
import excepciones.ElementoNoEncontradoException;
import excepciones.EquipoPorDeporteException;
import excepciones.ValorInvalidoException;
import usuarios.Socio;
import usuarios.Usuario;

public class CentralJugadores {

	public static final String ID_ANONIMO = "JUG-ANONIMO";

	private String[] idJugadores;
	private LinkedHashMap<String, Jugador> jugadores;
	private Jugador anonimo;
	private int contador;

	public CentralJugadores() {
		idJugadores = new String[0];
		jugadores = new LinkedHashMap<>();
	}

	public Jugador inscribirJugadorIndividual(Usuario usuario, String disciplina, String nivel, float elo)
			throws EquipoPorDeporteException, ValorInvalidoException {
		if (!JugadorIndividual.NIVELES.contains(nivel)) {
			throw new ValorInvalidoException("El nivel debe ser Principiante, Intermedio o Avanzado");
		}
		if (getJugadorIndividual(usuario.getUsername(), disciplina) != null) {
			throw new EquipoPorDeporteException(usuario.getUsername() + " ya juega " + disciplina);
		}
		JugadorIndividual jugador = new JugadorIndividual(generarId(), usuario, disciplina, nivel, elo);
		agregarJugador(jugador);
		return jugador;
	}

	public Jugador inscribirJugadorConjunto(Usuario usuario, String deporte, String posicion, int camiseta, Equipo equipo)
			throws EquipoPorDeporteException, CategoriaEdadException, CupoEquipoExcedidoException, CamisetaRepetidaException {
		verificarValidezInscribirJugador(equipo, deporte, usuario);
		if (!equipo.camisetaDisponible(camiseta)) {
			throw new CamisetaRepetidaException("La camiseta " + camiseta + " ya está ocupada en " + equipo.getNombre());
		}
		JugadorConjunto jugador = new JugadorConjunto(generarId(), usuario, posicion, camiseta, equipo);
		agregarJugador(jugador);
		return jugador;
	}

	public Jugador darDeBajaJugador(String idJugador) throws ElementoNoEncontradoException {
		Jugador jugador = jugadores.remove(idJugador);
		if (jugador == null) {
			throw new ElementoNoEncontradoException("No existe el jugador " + idJugador);
		}
		idJugadores = Arrays.stream(idJugadores).filter(id -> !id.equals(idJugador)).toArray(String[]::new);
		return jugador;
	}

	private Boolean verificarValidezInscribirJugador(Equipo equipo, String deporte, Usuario usuario)
			throws EquipoPorDeporteException, CategoriaEdadException, CupoEquipoExcedidoException {
		if (!equipo.getDeporte().equalsIgnoreCase(deporte)) {
			throw new EquipoPorDeporteException("El equipo " + equipo.getNombre() + " no es de " + deporte);
		}
		if (!(usuario instanceof Socio socio)) {
			throw new CategoriaEdadException("Solo los socios pueden jugar en equipos de conjunto");
		}
		for (String id : socio.getJugandoComo()) {
			Jugador otro = getJugadorPorId(id);
			if (otro != null && otro.getDeporte().equalsIgnoreCase(deporte)) {
				throw new EquipoPorDeporteException(socio.getNombre() + " ya juega " + deporte);
			}
		}
		if (!edadCorrespondeACategoria(Integer.parseInt(socio.getEdad()), equipo.getCategoria())) {
			throw new CategoriaEdadException("La edad " + socio.getEdad() + " no corresponde a la categoría " + equipo.getCategoria());
		}
		if (!equipo.tieneCupo()) {
			throw new CupoEquipoExcedidoException("El equipo " + equipo.getNombre() + " no tiene cupo");
		}
		return true;
	}

	private boolean edadCorrespondeACategoria(int edad, String categoria) {
		if (categoria.equalsIgnoreCase("Mayores")) {
			return edad >= 17;
		}
		return edad < Integer.parseInt(categoria.replaceAll("\\D", ""));
	}

	public Jugador getJugadorPorId(String idJugador) {
		if (ID_ANONIMO.equals(idJugador)) {
			return getJugadorAnonimo();
		}
		return jugadores.get(idJugador);
	}

	public List<Jugador> getJugadores() {
		return new ArrayList<>(jugadores.values());
	}

	public String[] getIdJugadores() {
		return idJugadores;
	}

	public void agregarJugador(Jugador jugador) {
		jugadores.put(jugador.getIdJugador(), jugador);
		idJugadores = Arrays.copyOf(idJugadores, idJugadores.length + 1);
		idJugadores[idJugadores.length - 1] = jugador.getIdJugador();
	}

	public List<Jugador> getJugadoresDeUsuario(String username) {
		List<Jugador> lista = new ArrayList<>();
		for (Jugador jugador : jugadores.values()) {
			if (jugador.getUsuario().getUsername().equals(username)) {
				lista.add(jugador);
			}
		}
		return lista;
	}

	public JugadorIndividual getJugadorIndividual(String username, String disciplina) {
		for (Jugador jugador : getJugadoresDeUsuario(username)) {
			if (jugador instanceof JugadorIndividual individual && individual.getDisciplina().equalsIgnoreCase(disciplina)) {
				return individual;
			}
		}
		return null;
	}

	public Jugador getJugadorAnonimo() {
		if (anonimo == null) {
			anonimo = new JugadorConjunto(ID_ANONIMO, new Socio("anonimo", "", "Anónimo", "0"), "", 0, "", "");
		}
		return anonimo;
	}

	public Boolean verificarNivelesCompatibles(String idJugador1, String idJugador2) throws ElementoNoEncontradoException {
		return getIndividual(idJugador1).esNivelCompatible(getIndividual(idJugador2));
	}

	public void actualizarElo(String[] ganadores, String[] perdedores) throws ElementoNoEncontradoException {
		for (int i = 0; i < Math.min(ganadores.length, perdedores.length); i++) {
			JugadorIndividual ganador = getIndividual(ganadores[i]);
			JugadorIndividual perdedor = getIndividual(perdedores[i]);
			float eloA = ganador.getElo();
			float eloB = perdedor.getElo();
			float esperado = calcularResultadoEsperado(eloA, eloB);
			ganador.setElo(eloA + 32 * (1 - esperado));
			perdedor.setElo(eloB + 32 * (0 - (1 - esperado)));
		}
	}

	private float calcularResultadoEsperado(float eloA, float eloB) {
		return (float) (1 / (1 + Math.pow(10, (eloB - eloA) / 400)));
	}

	public Boolean verificarNivelesEquipos(String[] equipo1, String[] equipo2) throws ElementoNoEncontradoException {
		for (String id1 : equipo1) {
			for (String id2 : equipo2) {
				if (!verificarNivelesCompatibles(id1, id2)) {
					return false;
				}
			}
		}
		return true;
	}

	private JugadorIndividual getIndividual(String idJugador) throws ElementoNoEncontradoException {
		if (getJugadorPorId(idJugador) instanceof JugadorIndividual individual) {
			return individual;
		}
		throw new ElementoNoEncontradoException("No existe el jugador individual " + idJugador);
	}

	private String generarId() {
		String id;
		do {
			id = "JUG-" + (++contador);
		} while (jugadores.containsKey(id));
		return id;
	}
}
