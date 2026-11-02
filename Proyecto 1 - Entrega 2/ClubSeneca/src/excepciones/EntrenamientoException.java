package excepciones;

public abstract class EntrenamientoException extends ClubSenecaException {

	private static final long serialVersionUID = 1L;

	public EntrenamientoException(String mensaje) {
		super(mensaje);
	}
}
