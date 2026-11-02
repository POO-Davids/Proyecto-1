package competencia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import excepciones.MarcadorInvalidoException;
import excepciones.RequisitosInscripcionException;
import partidos.PartidoCompetencia;

public class CompetenciaASCUN extends Competencia {

	private int numeroClasificadoresGrupos;
	private FaseGrupos faseGrupos;
	private FaseEliminacionDirecta faseEliminacion;

	public CompetenciaASCUN(String idCompetencia, String nombre, String entidad, String temporada, String deporte,
			String categoria, String formato, int nominaMinima, int equivalenciaSuspension) {
		super(idCompetencia, nombre, entidad, temporada, deporte, categoria, formato, nominaMinima, equivalenciaSuspension);
		this.numeroClasificadoresGrupos = 2;
		this.faseGrupos = new FaseGrupos(4);
		this.faseEliminacion = new FaseEliminacionDirecta();
	}

	public void crearGrupo(String nombreGrupo) throws ElementoDuplicadoException {
		faseGrupos.crearGrupo(nombreGrupo);
	}

	public void inscribirEquipoAGrupo(String nombreGrupo, String equipoId)
			throws ElementoNoEncontradoException, RequisitosInscripcionException, ElementoDuplicadoException {
		if (getEquipoParticipante(equipoId) == null) {
			throw new ElementoNoEncontradoException("El equipo " + equipoId + " no participa en " + nombre);
		}
		faseGrupos.agregarEquipoAGrupo(nombreGrupo, equipoId);
	}

	public void terminarFaseGrupos() {
		clasificarEquipos();
	}

	private void clasificarEquipos() {
		for (Grupo grupo : faseGrupos.getGrupos()) {
			List<String> ids = new ArrayList<>();
			for (EquipoCompetencia equipo : getTablaPosiciones(grupo.getNombre())) {
				ids.add(equipo.getEquipoCompetenciaId());
			}
			faseEliminacion.agregarEquipos(ids.subList(0, Math.min(numeroClasificadoresGrupos, ids.size())).toArray(new String[0]));
		}
	}

	public void eliminarEquipo(String equipoId) {
		faseEliminacion.eliminarEquipo(equipoId);
	}

	@Override
	public void registrarResultado(String idPartido, int puntuacionPropio, int puntuacionAfuera)
			throws ElementoNoEncontradoException, MarcadorInvalidoException {
		super.registrarResultado(idPartido, puntuacionPropio, puntuacionAfuera);
		PartidoCompetencia partido = buscarPartido(idPartido);
		List<String> restantes = Arrays.asList(faseEliminacion.getEquiposRestantes());
		String propio = partido.getEquipoPropio().getEquipoCompetenciaId();
		String afuera = partido.getEquipoAfuera().getEquipoCompetenciaId();
		if (restantes.contains(propio) && restantes.contains(afuera) && puntuacionPropio != puntuacionAfuera) {
			eliminarEquipo(puntuacionPropio > puntuacionAfuera ? afuera : propio);
		}
	}

	public List<EquipoCompetencia> getTablaPosiciones(String nombreGrupo) {
		Grupo grupo = nombreGrupo == null ? null : faseGrupos.getGrupo(nombreGrupo);
		if (grupo == null) {
			return getTablaPosiciones();
		}
		List<EquipoCompetencia> tabla = new ArrayList<>();
		for (EquipoCompetencia equipo : getTablaPosiciones()) {
			if (Arrays.asList(grupo.getEquipos()).contains(equipo.getEquipoCompetenciaId())) {
				tabla.add(equipo);
			}
		}
		return tabla;
	}

	public int getNumeroClasificadoresGrupos() {
		return numeroClasificadoresGrupos;
	}

	public FaseGrupos getFaseGrupos() {
		return faseGrupos;
	}

	public FaseEliminacionDirecta getFaseEliminacion() {
		return faseEliminacion;
	}
}
