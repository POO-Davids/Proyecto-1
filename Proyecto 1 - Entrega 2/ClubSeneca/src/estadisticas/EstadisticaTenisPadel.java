package estadisticas;

public class EstadisticaTenisPadel extends Estadistica {

	private int setGanados;
	private int gamesGanados;
	private int aces;
	private int erroresNoForzados;

	public EstadisticaTenisPadel(String usuarioUsername, int setGanados, int gamesGanados, int aces, int erroresNoForzados) {
		super(usuarioUsername);
		this.setGanados = setGanados;
		this.gamesGanados = gamesGanados;
		this.aces = aces;
		this.erroresNoForzados = erroresNoForzados;
	}

	public int getSetGanados() {
		return setGanados;
	}

	public int getGamesGanados() {
		return gamesGanados;
	}

	public int getAces() {
		return aces;
	}

	public int getErroresNoForzados() {
		return erroresNoForzados;
	}

	public void setSetGanados(int sets) {
		this.setGanados = sets;
	}

	public void setGamesGanados(int games) {
		this.gamesGanados = games;
	}

	public void setAces(int aces) {
		this.aces = aces;
	}

	public void setErroresNoForzados(int errores) {
		this.erroresNoForzados = errores;
	}

	@Override
	public void sumar(Estadistica otra) {
		if (otra instanceof EstadisticaTenisPadel o) {
			setGanados += o.setGanados;
			gamesGanados += o.gamesGanados;
			aces += o.aces;
			erroresNoForzados += o.erroresNoForzados;
		}
	}

	@Override
	public int getValorPrincipal() {
		return setGanados;
	}

	@Override
	public String toString() {
		return "sets=" + setGanados + ", games=" + gamesGanados + ", aces=" + aces + ", errores=" + erroresNoForzados;
	}
}
