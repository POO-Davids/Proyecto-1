package excepciones;

public class PromocionNoAcumulableException extends TiendaException {

	private static final long serialVersionUID = 1L;

	public PromocionNoAcumulableException(String mensaje) {
		super(mensaje);
	}
}
