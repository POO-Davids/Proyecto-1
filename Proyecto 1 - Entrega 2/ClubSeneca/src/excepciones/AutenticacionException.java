package excepciones;

public abstract class AutenticacionException extends ClubSenecaException {

	private static final long serialVersionUID = 1L;

	public AutenticacionException(String mensaje) {
		super(mensaje);
	}
}
