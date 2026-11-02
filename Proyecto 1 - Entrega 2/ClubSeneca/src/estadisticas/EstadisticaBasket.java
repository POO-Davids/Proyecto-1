package estadisticas;

public class EstadisticaBasket extends Estadistica {

	private int puntos;
	private int rebotes;

	public EstadisticaBasket(String usuarioUsername, int puntos, int rebotes) {
		super(usuarioUsername);
		this.puntos = puntos;
		this.rebotes = rebotes;
	}

	public int getPuntos() {
		return puntos;
	}

	public int getRebotes() {
		return rebotes;
	}

	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}

	public void setRebotes(int rebotes) {
		this.rebotes = rebotes;
	}

	@Override
	public void sumar(Estadistica otra) {
		if (otra instanceof EstadisticaBasket o) {
			puntos += o.puntos;
			rebotes += o.rebotes;
		}
	}

	@Override
	public int getValorPrincipal() {
		return puntos;
	}

	@Override
	public String toString() {
		return "puntos=" + puntos + ", rebotes=" + rebotes;
	}
}
