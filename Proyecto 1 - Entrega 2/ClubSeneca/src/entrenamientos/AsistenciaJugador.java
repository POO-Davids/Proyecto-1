package entrenamientos;

public class AsistenciaJugador {

	private String asistio;
	private int evaluacion;
	private String comentario;
	private String jugadorId;
	private SesionEntrenamiento sesion;

	public AsistenciaJugador(String jugadorId, String asistio, SesionEntrenamiento sesion) {
		this.jugadorId = jugadorId;
		this.asistio = asistio;
		this.sesion = sesion;
		this.comentario = "";
	}

	public String getJugadorId() {
		return jugadorId;
	}

	public String getAsistio() {
		return asistio;
	}

	public void setAsistio(String asistio) {
		this.asistio = asistio;
	}

	public int getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(int evaluacion) {
		this.evaluacion = evaluacion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public SesionEntrenamiento getSesion() {
		return sesion;
	}

	@Override
	public String toString() {
		return jugadorId + ": " + asistio + (evaluacion > 0 ? " | evaluación " + evaluacion + " \"" + comentario + "\"" : "");
	}
}
