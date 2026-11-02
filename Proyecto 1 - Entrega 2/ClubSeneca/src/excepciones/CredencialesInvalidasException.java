package excepciones;

public class CredencialesInvalidasException extends AutenticacionException {

	private static final long serialVersionUID = 1L;

	public CredencialesInvalidasException(String mensaje) {
		super(mensaje);
	}
}
