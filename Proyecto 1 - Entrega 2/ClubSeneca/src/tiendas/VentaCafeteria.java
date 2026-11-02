package tiendas;

import java.time.LocalDate;
import java.util.List;

import excepciones.ValorInvalidoException;
import usuarios.Usuario;

public class VentaCafeteria extends Venta {

	private float propina;
	private List<ArticuloCafeteria> articulo;
	private int[] cantidades;

	public VentaCafeteria(String idVenta, Usuario comprador, List<ArticuloCafeteria> articulo, int[] cantidades, LocalDate fecha,
			float propina, float puntosRedimidos, float iva) {
		super(idVenta, comprador, fecha, 0, 0, puntosRedimidos, iva);
		this.articulo = articulo;
		this.cantidades = cantidades;
		this.propina = propina;
		for (int i = 0; i < articulo.size(); i++) {
			valor += articulo.get(i).getPrecio() * cantidades[i];
		}
	}

	public float getPropina() {
		return propina;
	}

	public void setPropina(float propina) throws ValorInvalidoException {
		if (propina < 0) {
			throw new ValorInvalidoException("La propina no puede ser negativa");
		}
		this.propina = propina;
	}

	public List<ArticuloCafeteria> getArticulos() {
		return articulo;
	}

	public int[] getCantidades() {
		return cantidades;
	}

	@Override
	public float calcularTotal() {
		return calcularSubtotal() - puntosRedimidos + iva + propina;
	}

	@Override
	public String getRubro() {
		return "cafeteria";
	}

	@Override
	public String toString() {
		return super.toString() + String.format(" | propina: %.0f", propina);
	}
}
