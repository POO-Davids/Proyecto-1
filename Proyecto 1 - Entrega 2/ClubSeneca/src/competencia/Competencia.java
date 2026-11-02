package competencia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import equipos.Equipo;
import estadisticas.Estadistica;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import excepciones.MarcadorInvalidoException;
import excepciones.RequisitosInscripcionException;
import partidos.PartidoCompetencia;

public abstract class Competencia {

	protected String idCompetencia;
	protected String nombre;
	protected String entidad;
	protected String temporada;
	protected String deporte;
	protected String categoria;
	protected String formato;
	protected Boolean cerrado;
	protected int nominaMinima;
	protected int equivalenciaSuspension;
	protected LinkedHashMap<String, EquipoCompetencia> equiposParticipantes;
	protected List<PartidoCompetencia> partidos;
	protected LinkedHashMap<String, Estadistica> estadisticasAcumuladas;

	public Competencia(String idCompetencia, String nombre, String entidad, String temporada, String deporte, String categoria,
			String formato, int nominaMinima, int equivalenciaSuspension) {
		this.idCompetencia = idCompetencia;
		this.nombre = nombre;
		this.entidad = entidad;
		this.temporada = temporada;
		this.deporte = deporte;
		this.categoria = categoria;
		this.formato = formato;
		this.nominaMinima = nominaMinima;
		this.equivalenciaSuspension = equivalenciaSuspension;
		this.cerrado = false;
		this.equiposParticipantes = new LinkedHashMap<>();
		this.partidos = new ArrayList<>();
		this.estadisticasAcumuladas = new LinkedHashMap<>();
	}

	public void inscribirEquipo(Equipo equipo, String[] nomina) throws RequisitosInscripcionException, ElementoDuplicadoException {
		if (!verificarRequisitosInscripcion(equipo, nomina)) {
			throw new RequisitosInscripcionException("El equipo " + equipo.getNombre() + " no cumple los requisitos: deporte "
					+ deporte + ", categoría " + categoria + ", nómina mínima " + nominaMinima + " jugadores del equipo");
		}
		if (equiposParticipantes.containsKey(equipo.getIdEquipo())) {
			throw new ElementoDuplicadoException("El equipo ya está inscrito en " + nombre);
		}
		agregarEquipoParticipante(new EquipoPropioCompetencia(equipo, nomina));
	}

	public Boolean verificarRequisitosInscripcion(Equipo equipo, String[] nomina) {
		return equipo.getCategoria().equalsIgnoreCase(categoria) && equipo.getDeporte().equalsIgnoreCase(deporte)
				&& nomina.length >= nominaMinima && Arrays.asList(equipo.getJugadoresIds()).containsAll(Arrays.asList(nomina));
	}

	public void agregarEquipoAfuera(String nombre) throws ElementoDuplicadoException {
		if (equiposParticipantes.containsKey(nombre)) {
			throw new ElementoDuplicadoException("Ya existe el equipo " + nombre + " en la competencia");
		}
		agregarEquipoParticipante(new EquipoAfueraCompetencia(nombre));
	}

	public void agregarEquipoParticipante(EquipoCompetencia equipo) {
		equiposParticipantes.put(equipo.getEquipoCompetenciaId(), equipo);
	}

	public EquipoCompetencia getEquipoParticipante(String idEquipoCompetencia) {
		return equiposParticipantes.get(idEquipoCompetencia);
	}

	public List<EquipoCompetencia> getEquiposParticipantes() {
		return new ArrayList<>(equiposParticipantes.values());
	}

	public void agregarPartido(PartidoCompetencia partido) {
		partidos.add(partido);
	}

	public List<PartidoCompetencia> getPartidos() {
		return partidos;
	}

	public void registrarResultado(String idPartido, int puntuacionPropio, int puntuacionAfuera)
			throws ElementoNoEncontradoException, MarcadorInvalidoException {
		PartidoCompetencia partido = buscarPartido(idPartido);
		if (puntuacionPropio == puntuacionAfuera && !deporte.matches("(?i)f[uú]tbol")) {
			throw new MarcadorInvalidoException("En " + deporte + " no hay empates");
		}
		partido.getEquipoPropio().sumarPuntos(calcularPuntos(puntuacionPropio, puntuacionAfuera));
		partido.getEquipoAfuera().sumarPuntos(calcularPuntos(puntuacionAfuera, puntuacionPropio));
	}

	protected PartidoCompetencia buscarPartido(String idPartido) throws ElementoNoEncontradoException {
		for (PartidoCompetencia partido : partidos) {
			if (partido.getIdPartido().equals(idPartido)) {
				return partido;
			}
		}
		throw new ElementoNoEncontradoException("El partido " + idPartido + " no es de la competencia " + nombre);
	}

	private int calcularPuntos(int favor, int contra) {
		if (favor > contra) {
			return 3;
		}
		return favor == contra ? 1 : 0;
	}

	public void acumularEstadistica(Estadistica estadistica) {
		String idJugador = estadistica.getUsuarioUsername();
		Estadistica acumulada = estadisticasAcumuladas.computeIfAbsent(idJugador, id -> Estadistica.crear(deporte, id));
		acumulada.sumar(estadistica);
		for (EquipoCompetencia equipo : equiposParticipantes.values()) {
			if (equipo instanceof EquipoPropioCompetencia propio && propio.estaInscrito(idJugador)) {
				propio.registrarPuntosJugador(idJugador, acumulada.getValorPrincipal());
			}
		}
	}

	public Boolean jugadorPuedeJugar(String idJugador) {
		for (EquipoCompetencia equipo : equiposParticipantes.values()) {
			if (equipo instanceof EquipoPropioCompetencia propio && propio.estaInscrito(idJugador)) {
				return true;
			}
		}
		return false;
	}

	public List<EquipoCompetencia> getTablaPosiciones() {
		List<EquipoCompetencia> tabla = new ArrayList<>(equiposParticipantes.values());
		tabla.sort(Comparator.comparingInt(EquipoCompetencia::getPuntos).reversed());
		return tabla;
	}

	public List<Estadistica> getGoleadores() {
		List<Estadistica> goleadores = new ArrayList<>(estadisticasAcumuladas.values());
		goleadores.sort(Comparator.comparingInt(Estadistica::getValorPrincipal).reversed());
		return goleadores;
	}

	public String getJugadorDestacado() {
		List<Estadistica> goleadores = getGoleadores();
		return goleadores.isEmpty() ? null : goleadores.get(0).getUsuarioUsername();
	}

	public void setCerrado() {
		cerrado = true;
	}

	public String getIdCompetencia() {
		return idCompetencia;
	}

	public String getNombre() {
		return nombre;
	}

	public String getEntidad() {
		return entidad;
	}

	public String getTemporada() {
		return temporada;
	}

	public String getDeporte() {
		return deporte;
	}

	public String getCategoria() {
		return categoria;
	}

	public String getFormato() {
		return formato;
	}

	public Boolean getCerrado() {
		return cerrado;
	}

	public int getNominaMinima() {
		return nominaMinima;
	}

	public int getEquivalenciaSuspension() {
		return equivalenciaSuspension;
	}

	@Override
	public String toString() {
		return idCompetencia + " | " + nombre + " | " + entidad + " | " + temporada + " | " + deporte + " " + categoria + " | "
				+ formato + (cerrado ? " | CERRADA" : "");
	}
}
