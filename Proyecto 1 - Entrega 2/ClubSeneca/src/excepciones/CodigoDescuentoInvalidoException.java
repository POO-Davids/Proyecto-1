package excepciones;

public class CodigoDescuentoInvalidoException extends TiendaException {

	private static final long serialVersionUID = 1L;

	public CodigoDescuentoInvalidoException(String mensaje) {
		super(mensaje);
	}
}
