package excepciones;

public class PersistenciaException extends ClubSenecaException {

	private static final long serialVersionUID = 1L;

	public PersistenciaException(String mensaje) {
		super(mensaje);
	}
}
