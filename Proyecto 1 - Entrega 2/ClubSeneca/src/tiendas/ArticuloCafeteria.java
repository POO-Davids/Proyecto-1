package tiendas;

public abstract class ArticuloCafeteria extends Articulo {

	public ArticuloCafeteria(String nombre, float precio) {
		super(nombre, precio);
	}

	public Boolean getEsCaliente() {
		return false;
	}

	public String[] getAlergenos() {
		return new String[0];
	}
}
