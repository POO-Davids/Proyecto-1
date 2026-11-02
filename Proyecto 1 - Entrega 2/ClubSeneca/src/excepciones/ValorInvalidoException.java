package excepciones;

public class ValorInvalidoException extends TiendaException {

	private static final long serialVersionUID = 1L;

	public ValorInvalidoException(String mensaje) {
		super(mensaje);
	}
}
