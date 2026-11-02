package excepciones;

public abstract class InstalacionException extends ClubSenecaException {

	private static final long serialVersionUID = 1L;

	public InstalacionException(String mensaje) {
		super(mensaje);
	}
}
