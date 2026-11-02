package excepciones;

public class JugadorNoConvocadoException extends EntrenamientoException {

	private static final long serialVersionUID = 1L;

	public JugadorNoConvocadoException(String mensaje) {
		super(mensaje);
	}
}
