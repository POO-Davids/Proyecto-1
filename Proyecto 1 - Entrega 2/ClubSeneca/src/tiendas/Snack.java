package tiendas;

public class Snack extends ArticuloCafeteria {

	private String[] alergenos;

	public Snack(String nombre, float precio, String[] alergenos) {
		super(nombre, precio);
		this.alergenos = alergenos;
	}

	@Override
	public String[] getAlergenos() {
		return alergenos;
	}

	@Override
	public String toString() {
		return super.toString() + " | alérgenos: " + String.join(", ", alergenos);
	}
}
