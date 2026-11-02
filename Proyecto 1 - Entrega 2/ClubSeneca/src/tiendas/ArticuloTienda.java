package tiendas;

public class ArticuloTienda extends Articulo {

	private String categoria;

	public ArticuloTienda(String nombre, float precio, String categoria) {
		super(nombre, precio);
		this.categoria = categoria;
	}

	public String getCategoria() {
		return categoria;
	}

	@Override
	public String toString() {
		return super.toString() + " | categoria: " + categoria;
	}
}
