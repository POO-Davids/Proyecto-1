package tiendas;

import java.time.LocalDate;

import usuarios.Usuario;

public class PagoMensualidad extends Venta {

	private String periodo;

	public PagoMensualidad(Usuario socio, float valor, LocalDate fecha, String periodo) {
		super("MEN-" + socio.getUsername() + "-" + periodo, socio, fecha, valor, 0, 0, 0);
		this.periodo = periodo;
	}

	public String getPeriodo() {
		return periodo;
	}

	@Override
	public float calcularTotal() {
		return valor;
	}

	@Override
	public String getRubro() {
		return "mensualidades";
	}
}
