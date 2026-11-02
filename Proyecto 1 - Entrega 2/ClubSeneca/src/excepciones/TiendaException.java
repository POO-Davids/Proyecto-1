package excepciones;

public abstract class TiendaException extends ClubSenecaException {

	private static final long serialVersionUID = 1L;

	public TiendaException(String mensaje) {
		super(mensaje);
	}
}
