package excepciones;

public class AccionNoPermitidaException extends AutenticacionException {

	private static final long serialVersionUID = 1L;

	public AccionNoPermitidaException(String mensaje) {
		super(mensaje);
	}
}
