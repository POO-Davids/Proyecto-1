package estadisticas;

public class EstadisticaTenisMesa extends Estadistica {

	private int setGanados;
	private int puntosTotales;

	public EstadisticaTenisMesa(String usuarioUsername, int setGanados, int puntosTotales) {
		super(usuarioUsername);
		this.setGanados = setGanados;
		this.puntosTotales = puntosTotales;
	}

	public int getSetGanados() {
		return setGanados;
	}

	public int getPuntosTotales() {
		return puntosTotales;
	}

	public void setSetGanados(int sets) {
		this.setGanados = sets;
	}

	public void setPuntosTotales(int puntos) {
		this.puntosTotales = puntos;
	}

	@Override
	public void sumar(Estadistica otra) {
		if (otra instanceof EstadisticaTenisMesa o) {
			setGanados += o.setGanados;
			puntosTotales += o.puntosTotales;
		}
	}

	@Override
	public int getValorPrincipal() {
		return setGanados;
	}

	@Override
	public String toString() {
		return "sets=" + setGanados + ", puntos=" + puntosTotales;
	}
}
