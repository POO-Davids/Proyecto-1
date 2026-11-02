package excepciones;

public class JugadorSuspendidoException extends PartidoException {

	private static final long serialVersionUID = 1L;

	public JugadorSuspendidoException(String mensaje) {
		super(mensaje);
	}
}
