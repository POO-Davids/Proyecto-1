package usuarios;

public class Administrador extends Usuario {

	public Administrador(String username, String password) {
		super(username, password);
	}

	@Override
	public String getTipo() {
		return "Administrador";
	}
}
