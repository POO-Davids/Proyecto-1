package excepciones;

public class SesionIncompletaException extends EntrenamientoException {

	private static final long serialVersionUID = 1L;

	public SesionIncompletaException(String mensaje) {
		super(mensaje);
	}
}
