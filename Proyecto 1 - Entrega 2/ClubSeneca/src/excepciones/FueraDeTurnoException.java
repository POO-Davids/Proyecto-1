package excepciones;

public class FueraDeTurnoException extends TurnoException {

	private static final long serialVersionUID = 1L;

	public FueraDeTurnoException(String mensaje) {
		super(mensaje);
	}
}
