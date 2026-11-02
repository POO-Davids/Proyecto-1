package excepciones;

public class MarcadorInvalidoException extends PartidoException {

	private static final long serialVersionUID = 1L;

	public MarcadorInvalidoException(String mensaje) {
		super(mensaje);
	}
}
