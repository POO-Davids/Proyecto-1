package excepciones;

public class InstalacionNoDisponibleException extends InstalacionException {

	private static final long serialVersionUID = 1L;

	public InstalacionNoDisponibleException(String mensaje) {
		super(mensaje);
	}
}
