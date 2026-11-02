package tiendas;

public class Bebida extends ArticuloCafeteria {

	private Boolean esCaliente;

	public Bebida(String nombre, float precio, Boolean esCaliente) {
		super(nombre, precio);
		this.esCaliente = esCaliente;
	}

	@Override
	public Boolean getEsCaliente() {
		return esCaliente;
	}

	@Override
	public String toString() {
		return super.toString() + " | bebida " + (esCaliente ? "caliente" : "fría");
	}
}
