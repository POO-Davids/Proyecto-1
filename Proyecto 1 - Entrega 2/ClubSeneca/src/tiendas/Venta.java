package tiendas;

import java.time.LocalDate;

import usuarios.Usuario;

public abstract class Venta {

	protected String idVenta;
	protected float valor;
	protected float puntosRedimidos;
	protected LocalDate fecha;
	protected float iva;
	protected float descuento;
	protected Usuario comprador;

	public Venta(String idVenta, Usuario comprador, LocalDate fecha, float valor, float descuento, float puntosRedimidos, float iva) {
		this.idVenta = idVenta;
		this.comprador = comprador;
		this.fecha = fecha;
		this.valor = valor;
		this.descuento = descuento;
		this.puntosRedimidos = puntosRedimidos;
		this.iva = iva;
	}

	public String getIdVenta() {
		return idVenta;
	}

	public float getValor() {
		return valor;
	}

	public float getPuntosRedimidos() {
		return puntosRedimidos;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public float getIva() {
		return iva;
	}

	public float getDescuento() {
		return descuento;
	}

	public Usuario getComprador() {
		return comprador;
	}

	public float calcularSubtotal() {
		return valor;
	}

	public float calcularTotal() {
		return calcularSubtotal() - descuento - puntosRedimidos + iva;
	}

	public float calcularPuntosGenerados() {
		return (calcularSubtotal() - descuento - puntosRedimidos) * 0.02f;
	}

	public abstract String getRubro();

	@Override
	public String toString() {
		float base = calcularSubtotal() - descuento - puntosRedimidos;
		return String.format(
				"%s | %s | %s | comprador: %s | valor bruto: %.0f | descuento: %.0f | puntos redimidos: %.0f | subtotal: %.0f | impuesto: %.0f | total: %.0f",
				fecha, getRubro(), idVenta, comprador.getUsername(), calcularSubtotal(), descuento, puntosRedimidos, base, iva,
				calcularTotal());
	}
}
