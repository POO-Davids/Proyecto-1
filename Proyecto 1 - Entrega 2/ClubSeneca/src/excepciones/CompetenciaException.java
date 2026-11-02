package excepciones;

public abstract class CompetenciaException extends ClubSenecaException {

	private static final long serialVersionUID = 1L;

	public CompetenciaException(String mensaje) {
		super(mensaje);
	}
}
