package excepciones;

public class MinimoJugadoresException extends PartidoException {

	private static final long serialVersionUID = 1L;

	public MinimoJugadoresException(String mensaje) {
		super(mensaje);
	}
}
