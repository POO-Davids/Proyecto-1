package excepciones;

public class EquiposIncompatiblesException extends PartidoException {

	private static final long serialVersionUID = 1L;

	public EquiposIncompatiblesException(String mensaje) {
		super(mensaje);
	}
}
