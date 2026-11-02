package competencia;

import java.util.Arrays;

public class Grupo {

	private String nombre;
	private String[] equipos;

	public Grupo(String nombre) {
		this.nombre = nombre;
		this.equipos = new String[0];
	}

	public String getNombre() {
		return nombre;
	}

	public void agregarEquipo(String equipoId) {
		equipos = Arrays.copyOf(equipos, equipos.length + 1);
		equipos[equipos.length - 1] = equipoId;
	}

	public String[] getEquipos() {
		return equipos;
	}
}
