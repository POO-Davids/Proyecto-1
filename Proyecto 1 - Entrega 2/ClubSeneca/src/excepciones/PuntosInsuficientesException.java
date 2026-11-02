package excepciones;

public class PuntosInsuficientesException extends TiendaException {

	private static final long serialVersionUID = 1L;

	public PuntosInsuficientesException(String mensaje) {
		super(mensaje);
	}
}
