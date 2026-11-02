package estadisticas;

public class EstadisticaVolley extends Estadistica {

	private int aces;
	private int bloqueos;

	public EstadisticaVolley(String usuarioUsername, int aces, int bloqueos) {
		super(usuarioUsername);
		this.aces = aces;
		this.bloqueos = bloqueos;
	}

	public int getAces() {
		return aces;
	}

	public int getBloqueos() {
		return bloqueos;
	}

	public void setAces(int aces) {
		this.aces = aces;
	}

	public void setBloqueos(int bloqueos) {
		this.bloqueos = bloqueos;
	}

	@Override
	public void sumar(Estadistica otra) {
		if (otra instanceof EstadisticaVolley o) {
			aces += o.aces;
			bloqueos += o.bloqueos;
		}
	}

	@Override
	public int getValorPrincipal() {
		return aces;
	}

	@Override
	public String toString() {
		return "aces=" + aces + ", bloqueos=" + bloqueos;
	}
}
