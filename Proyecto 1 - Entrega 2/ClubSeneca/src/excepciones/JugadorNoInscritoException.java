package excepciones;

public class JugadorNoInscritoException extends CompetenciaException {

	private static final long serialVersionUID = 1L;

	public JugadorNoInscritoException(String mensaje) {
		super(mensaje);
	}
}
