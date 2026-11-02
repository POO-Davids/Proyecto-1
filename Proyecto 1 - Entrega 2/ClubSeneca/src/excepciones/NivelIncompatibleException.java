package excepciones;

public class NivelIncompatibleException extends PartidoException {

	private static final long serialVersionUID = 1L;

	public NivelIncompatibleException(String mensaje) {
		super(mensaje);
	}
}
