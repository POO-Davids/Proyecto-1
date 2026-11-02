package excepciones;

public abstract class EquipoException extends ClubSenecaException {

	private static final long serialVersionUID = 1L;

	public EquipoException(String mensaje) {
		super(mensaje);
	}
}
