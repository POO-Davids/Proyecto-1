package estadisticas;

public class EstadisticaFutbol extends Estadistica {

	private int goles;
	private int asistencias;

	public EstadisticaFutbol(String usuarioUsername, int goles, int asistencias) {
		super(usuarioUsername);
		this.goles = goles;
		this.asistencias = asistencias;
	}

	public int getGoles() {
		return goles;
	}

	public int getAsistencias() {
		return asistencias;
	}

	public void setGoles(int goles) {
		this.goles = goles;
	}

	public void setAsistencias(int asistencias) {
		this.asistencias = asistencias;
	}

	@Override
	public void sumar(Estadistica otra) {
		if (otra instanceof EstadisticaFutbol o) {
			goles += o.goles;
			asistencias += o.asistencias;
		}
	}

	@Override
	public int getValorPrincipal() {
		return goles;
	}

	@Override
	public String toString() {
		return "goles=" + goles + ", asistencias=" + asistencias;
	}
}
