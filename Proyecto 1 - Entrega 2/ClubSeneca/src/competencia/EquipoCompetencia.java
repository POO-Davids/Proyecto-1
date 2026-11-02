package competencia;

public abstract class EquipoCompetencia {

	protected int puntos;
	protected String equipoCompetenciaId;

	public EquipoCompetencia(String equipoCompetenciaId) {
		this.equipoCompetenciaId = equipoCompetenciaId;
		this.puntos = 0;
	}

	public String getEquipoCompetenciaId() {
		return equipoCompetenciaId;
	}

	public int getPuntos() {
		return puntos;
	}

	public void sumarPuntos(int puntos) {
		this.puntos += puntos;
	}

	public abstract String getNombre();

	public abstract String getJugadorMasPuntos();

	public abstract int getNumHabilitados();

	public abstract int getNumInscritos();
}
