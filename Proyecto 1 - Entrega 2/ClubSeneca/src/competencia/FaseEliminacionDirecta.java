package competencia;

import java.util.Arrays;

public class FaseEliminacionDirecta {

	private String[] equiposEliminados;
	private String[] equiposRestantes;

	public FaseEliminacionDirecta() {
		equiposEliminados = new String[0];
		equiposRestantes = new String[0];
	}

	public void agregarEquipos(String[] equiposIds) {
		for (String id : equiposIds) {
			if (!Arrays.asList(equiposRestantes).contains(id)) {
				equiposRestantes = Arrays.copyOf(equiposRestantes, equiposRestantes.length + 1);
				equiposRestantes[equiposRestantes.length - 1] = id;
			}
		}
	}

	public void eliminarEquipo(String idEquipo) {
		if (!Arrays.asList(equiposRestantes).contains(idEquipo)) {
			return;
		}
		equiposRestantes = Arrays.stream(equiposRestantes).filter(id -> !id.equals(idEquipo)).toArray(String[]::new);
		equiposEliminados = Arrays.copyOf(equiposEliminados, equiposEliminados.length + 1);
		equiposEliminados[equiposEliminados.length - 1] = idEquipo;
	}

	public String[] getEquiposRestantes() {
		return equiposRestantes;
	}

	public String[] getEquiposEliminados() {
		return equiposEliminados;
	}
}
