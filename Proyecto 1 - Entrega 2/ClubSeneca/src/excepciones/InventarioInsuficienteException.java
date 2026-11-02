package excepciones;

public class InventarioInsuficienteException extends TiendaException {

	private static final long serialVersionUID = 1L;

	public InventarioInsuficienteException(String mensaje) {
		super(mensaje);
	}
}
