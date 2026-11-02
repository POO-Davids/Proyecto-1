package competencia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import excepciones.RequisitosInscripcionException;

public class FaseGrupos {

	private int maximoEnGrupo;
	private HashMap<String, Grupo> grupos;

	public FaseGrupos(int maximoEnGrupo) {
		this.maximoEnGrupo = maximoEnGrupo;
		this.grupos = new HashMap<>();
	}

	public void crearGrupo(String nombre) throws ElementoDuplicadoException {
		if (grupos.containsKey(nombre)) {
			throw new ElementoDuplicadoException("Ya existe el grupo " + nombre);
		}
		grupos.put(nombre, new Grupo(nombre));
	}

	public void agregarEquipoAGrupo(String nombreGrupo, String equipoId)
			throws ElementoNoEncontradoException, RequisitosInscripcionException, ElementoDuplicadoException {
		Grupo grupo = getGrupo(nombreGrupo);
		if (grupo == null) {
			throw new ElementoNoEncontradoException("No existe el grupo " + nombreGrupo);
		}
		for (Grupo otro : grupos.values()) {
			if (Arrays.asList(otro.getEquipos()).contains(equipoId)) {
				throw new ElementoDuplicadoException("El equipo ya está en el grupo " + otro.getNombre());
			}
		}
		if (grupo.getEquipos().length >= maximoEnGrupo) {
			throw new RequisitosInscripcionException("El grupo " + nombreGrupo + " ya tiene " + maximoEnGrupo + " equipos");
		}
		grupo.agregarEquipo(equipoId);
	}

	public Grupo getGrupo(String nombre) {
		return grupos.get(nombre);
	}

	public List<Grupo> getGrupos() {
		return new ArrayList<>(grupos.values());
	}

	public int getMaximoEnGrupo() {
		return maximoEnGrupo;
	}

	public String[] getClasificados(int porGrupo) {
		List<String> clasificados = new ArrayList<>();
		for (Grupo grupo : grupos.values()) {
			String[] equipos = grupo.getEquipos();
			clasificados.addAll(Arrays.asList(equipos).subList(0, Math.min(porGrupo, equipos.length)));
		}
		return clasificados.toArray(new String[0]);
	}
}
