package competencia;

public class EquipoAfueraCompetencia extends EquipoCompetencia {

	private String nombre;
	private String jugadorMasPuntos;
	private int puntosJugadorMasPuntos;
	private int numHabilitados;
	private int numInscritos;

	public EquipoAfueraCompetencia(String nombre) {
		super(nombre);
		this.nombre = nombre;
	}

	@Override
	public String getNombre() {
		return nombre;
	}

	@Override
	public String getJugadorMasPuntos() {
		return jugadorMasPuntos;
	}

	@Override
	public int getNumHabilitados() {
		return numHabilitados;
	}

	@Override
	public int getNumInscritos() {
		return numInscritos;
	}

	public int getPuntosJugadorMasPuntos() {
		return puntosJugadorMasPuntos;
	}

	public void setNumHabilitados(int num) {
		this.numHabilitados = num;
	}

	public void setNumInscritos(int num) {
		this.numInscritos = num;
	}

	public void setJugadorMasPuntos(String nombre) {
		this.jugadorMasPuntos = nombre;
	}

	public void setPuntosJugadorMasPuntos(int puntos) {
		this.puntosJugadorMasPuntos = puntos;
	}
}
