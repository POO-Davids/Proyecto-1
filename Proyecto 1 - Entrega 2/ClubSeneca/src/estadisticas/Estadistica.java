package estadisticas;

public abstract class Estadistica {

	protected String usuarioUsername;

	public Estadistica(String usuarioUsername) {
		this.usuarioUsername = usuarioUsername;
	}

	public static Estadistica crear(String deporte, String usuarioUsername) {
		return switch (deporte.toLowerCase()) {
		case "baloncesto" -> new EstadisticaBasket(usuarioUsername, 0, 0);
		case "voleibol" -> new EstadisticaVolley(usuarioUsername, 0, 0);
		case "tenis", "padel", "pádel" -> new EstadisticaTenisPadel(usuarioUsername, 0, 0, 0, 0);
		case "tenis de mesa", "mesa" -> new EstadisticaTenisMesa(usuarioUsername, 0, 0);
		default -> new EstadisticaFutbol(usuarioUsername, 0, 0);
		};
	}

	public String getUsuarioUsername() {
		return usuarioUsername;
	}

	public void setUsuarioUsername(String usuarioUsername) {
		this.usuarioUsername = usuarioUsername;
	}

	public abstract void sumar(Estadistica otra);

	public abstract int getValorPrincipal();
}
