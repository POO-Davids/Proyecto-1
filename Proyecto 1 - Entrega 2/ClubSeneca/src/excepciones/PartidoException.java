package excepciones;

public abstract class PartidoException extends ClubSenecaException {

	private static final long serialVersionUID = 1L;

	public PartidoException(String mensaje) {
		super(mensaje);
	}
}
