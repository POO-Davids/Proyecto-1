package competencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import equipos.Equipo;
import estadisticas.Estadistica;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import excepciones.RequisitosInscripcionException;
import excepciones.ValorInvalidoException;
import partidos.PartidoCompetencia;

public class CentralCompetencias {

	private LinkedHashMap<String, Competencia> competencias;
	private int contador;

	public CentralCompetencias() {
		competencias = new LinkedHashMap<>();
	}

	public void crearCompetencia(String tipo, String nombre, String entidad, String temporada, String deporte, String categoria,
			String formato, int nominaMinima, int equivalenciaSuspension) throws ValorInvalidoException {
		String id = generarId();
		if (tipo.equalsIgnoreCase("ASCUN")) {
			agregarCompetencia(new CompetenciaASCUN(id, nombre, entidad, temporada, deporte, categoria, formato, nominaMinima,
					equivalenciaSuspension));
		} else if (tipo.equalsIgnoreCase("Cerros")) {
			agregarCompetencia(new CompetenciaCerros(id, nombre, entidad, temporada, deporte, categoria, formato, nominaMinima,
					equivalenciaSuspension));
		} else {
			throw new ValorInvalidoException("El tipo de competencia debe ser ASCUN o Cerros");
		}
	}

	public void agregarCompetencia(Competencia competencia) {
		competencias.put(competencia.getIdCompetencia(), competencia);
	}

	public void inscribirEquipoACompetencia(Equipo equipo, String idCompetencia, String[] nomina)
			throws ElementoNoEncontradoException, RequisitosInscripcionException, ElementoDuplicadoException {
		buscarCompetencia(idCompetencia).inscribirEquipo(equipo, nomina);
	}

	public Competencia getCompetencia(String idCompetencia) {
		return competencias.get(idCompetencia);
	}

	public List<Competencia> getCompetencias() {
		return new ArrayList<>(competencias.values());
	}

	public void registrarPartido(String idCompetencia, PartidoCompetencia partido) throws ElementoNoEncontradoException {
		buscarCompetencia(idCompetencia).agregarPartido(partido);
	}

	public List<EquipoCompetencia> getTablaPosiciones(String idCompetencia, String nombreGrupo) throws ElementoNoEncontradoException {
		Competencia competencia = buscarCompetencia(idCompetencia);
		if (competencia instanceof CompetenciaASCUN ascun) {
			return ascun.getTablaPosiciones(nombreGrupo);
		}
		return competencia.getTablaPosiciones();
	}

	public List<Estadistica> getGoleadores(String idCompetencia) throws ElementoNoEncontradoException {
		return buscarCompetencia(idCompetencia).getGoleadores();
	}

	public String getJugadorDestacado(String idCompetencia) throws ElementoNoEncontradoException {
		return buscarCompetencia(idCompetencia).getJugadorDestacado();
	}

	public void cerrarCompetencia(String idCompetencia) throws ElementoNoEncontradoException {
		buscarCompetencia(idCompetencia).setCerrado();
	}

	private Competencia buscarCompetencia(String idCompetencia) throws ElementoNoEncontradoException {
		Competencia competencia = getCompetencia(idCompetencia);
		if (competencia == null) {
			throw new ElementoNoEncontradoException("No existe la competencia " + idCompetencia);
		}
		return competencia;
	}

	private String generarId() {
		String id;
		do {
			id = "COMP-" + (++contador);
		} while (competencias.containsKey(id));
		return id;
	}
}
