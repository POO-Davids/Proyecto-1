package usuarios;

public class Entrenador extends Empleado {

	public Entrenador(String username, String password, String codigoDescuentoTienda) {
		super(username, password, codigoDescuentoTienda);
	}

	@Override
	public String getTipo() {
		return "Entrenador";
	}
}
