package excepciones;

public abstract class TurnoException extends ClubSenecaException {

	private static final long serialVersionUID = 1L;

	public TurnoException(String mensaje) {
		super(mensaje);
	}
}
