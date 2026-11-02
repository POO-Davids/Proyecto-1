package tiendas;

import java.time.LocalDate;
import java.util.List;

import usuarios.Usuario;

public class VentaTienda extends Venta {

	private float descuentoPorCodigo;
	private List<ArticuloTienda> articulo;
	private int[] cantidades;

	public VentaTienda(String idVenta, Usuario comprador, List<ArticuloTienda> articulo, int[] cantidades, LocalDate fecha,
			float descuentoPorCodigo, float puntosRedimidos, float iva) {
		super(idVenta, comprador, fecha, 0, descuentoPorCodigo, puntosRedimidos, iva);
		this.articulo = articulo;
		this.cantidades = cantidades;
		this.descuentoPorCodigo = descuentoPorCodigo;
		for (int i = 0; i < articulo.size(); i++) {
			valor += articulo.get(i).getPrecio() * cantidades[i];
		}
	}

	public float getDescuentoPorCodigo() {
		return descuentoPorCodigo;
	}

	public List<ArticuloTienda> getArticulos() {
		return articulo;
	}

	public int[] getCantidades() {
		return cantidades;
	}

	@Override
	public float calcularTotal() {
		return calcularSubtotal() - descuentoPorCodigo - puntosRedimidos + iva;
	}

	@Override
	public String getRubro() {
		return "tienda";
	}
}
