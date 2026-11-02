package excepciones;

public abstract class ClubSenecaException extends Exception {

	private static final long serialVersionUID = 1L;

	public ClubSenecaException(String mensaje) {
		super(mensaje);
	}
}
