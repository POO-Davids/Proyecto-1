package usuarios;

public class Fisioterapeuta extends Empleado {

	public Fisioterapeuta(String username, String password, String codigoDescuentoTienda) {
		super(username, password, codigoDescuentoTienda);
	}

	@Override
	public String getTipo() {
		return "Fisioterapeuta";
	}
}
