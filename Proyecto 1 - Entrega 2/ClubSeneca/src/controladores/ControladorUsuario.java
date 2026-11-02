package controladores;

import java.time.LocalDateTime;

import excepciones.BebidaCalienteException;
import excepciones.ClubSenecaException;
import excepciones.ElementoNoEncontradoException;
import excepciones.PromocionNoAcumulableException;
import excepciones.TiendaException;
import excepciones.ValorInvalidoException;
import instalaciones.Instalacion;
import reservas.Reserva;
import tiendas.ArticuloCafeteria;
import tiendas.Bebida;
import tiendas.Cafeteria;
import tiendas.Venta;
import usuarios.CentralUsuarios;
import usuarios.Empleado;
import usuarios.Socio;
import usuarios.Usuario;

public abstract class ControladorUsuario extends ControladorRol {

	protected Usuario usuario;

	public ControladorUsuario(CentralClubSeneca central, Usuario usuario) {
		super(central);
		this.usuario = usuario;
	}

	public String comprar(String tienda, String[] articulos, int[] cantidades, String codigoDescuento, float propina,
			float puntosARedimir) throws ElementoNoEncontradoException, TiendaException {
		if (articulos.length == 0 || articulos.length != cantidades.length) {
			throw new ValorInvalidoException("Debe indicar una cantidad por cada artículo");
		}
		for (int cantidad : cantidades) {
			if (cantidad <= 0) {
				throw new ValorInvalidoException("Las cantidades deben ser mayores que cero");
			}
		}
		if (puntosARedimir < 0) {
			throw new ValorInvalidoException("Los puntos a redimir no pueden ser negativos");
		}
		boolean esTienda = tienda.equalsIgnoreCase("Tienda");
		if (!esTienda && !tienda.equalsIgnoreCase("Cafeteria") && !tienda.equalsIgnoreCase("Cafetería")) {
			throw new ValorInvalidoException("Debe comprar en Tienda o Cafeteria");
		}
		String codigo = codigoDescuento == null ? "" : codigoDescuento.trim();
		if (esTienda) {
			if (!codigo.isEmpty() && puntosARedimir > 0) {
				throw new PromocionNoAcumulableException("El código de descuento no es acumulable con la redención de puntos");
			}
			if (codigo.isEmpty() && puntosARedimir == 0 && usuario instanceof Socio socio && socio.getCodigoDescuentoDado() != null) {
				codigo = socio.getCodigoDescuentoDado();
			}
		} else {
			if (propina < 0) {
				throw new ValorInvalidoException("La propina no puede ser negativa");
			}
			if (incluyeBebidaCaliente(articulos)) {
				Reserva reserva = central.getReservas().getReservaEnCurso(usuario.getUsername(), LocalDateTime.now());
				if (reserva != null && verificarBebidaCaliente(reserva.getInstalacion())) {
					throw new BebidaCalienteException("No se despachan bebidas calientes en una cancha de pádel o mesa techada");
				}
			}
		}
		CentralUsuarios usuarios = central.getUsuarios();
		if (puntosARedimir > 0) {
			usuarios.redimirPuntos(usuario.getUsername(), puntosARedimir);
		}
		Venta venta;
		try {
			venta = esTienda ? central.getTienda().registrarVenta(usuario, articulos, cantidades, codigo, puntosARedimir)
					: central.getCafeteria().registrarVenta(usuario, articulos, cantidades, propina, puntosARedimir);
		} catch (ClubSenecaException e) {
			if (puntosARedimir > 0) {
				usuarios.acumularPuntos(usuario.getUsername(), puntosARedimir);
			}
			throw e;
		}
		if (!esTienda) {
			for (int i = 0; i < articulos.length; i++) {
				if (central.getCafeteria().getArticulo(articulos[i]) instanceof Bebida bebida) {
					for (int k = 0; k < cantidades[i]; k++) {
						usuario.agregarBebidaSinConsumir(bebida);
					}
				}
			}
		}
		float puntos = venta.calcularPuntosGenerados();
		usuarios.acumularPuntos(usuario.getUsername(), puntos);
		return venta + "\nPuntos generados: " + puntos + " | saldo de puntos: " + consultarPuntosFidelidad();
	}

	private boolean incluyeBebidaCaliente(String[] articulos) {
		for (String nombre : articulos) {
			ArticuloCafeteria articulo = central.getCafeteria().getArticulo(nombre);
			if (articulo != null && articulo.getEsCaliente()) {
				return true;
			}
		}
		return false;
	}

	public String cotizarCompraCafeteria(String[] articulos, int[] cantidades) throws ElementoNoEncontradoException {
		Cafeteria cafeteria = central.getCafeteria();
		float subtotal = 0;
		for (int i = 0; i < articulos.length; i++) {
			ArticuloCafeteria articulo = cafeteria.getArticulo(articulos[i]);
			if (articulo == null) {
				throw new ElementoNoEncontradoException("No existe el artículo " + articulos[i] + " en la cafetería");
			}
			subtotal += articulo.getPrecio() * cantidades[i];
		}
		float impuesto = cafeteria.calcularImpuesto(subtotal);
		float propina = cafeteria.calcularPropinaSugerida(subtotal);
		return String.format("Subtotal: %.0f | Impuesto al consumo: %.0f | Propina sugerida: %.0f | Total: %.0f", subtotal, impuesto,
				propina, subtotal + impuesto + propina);
	}

	public String[] consultarAlergenos(String[] articulos) throws ElementoNoEncontradoException {
		return central.getCafeteria().consultarAlergenos(articulos);
	}

	public void consumirBebida(String nombreBebida) throws ElementoNoEncontradoException {
		usuario.consumirBebida(nombreBebida);
	}

	public float consultarPuntosFidelidad() {
		if (usuario instanceof Socio socio) {
			return socio.getPuntosFidelidad();
		}
		if (usuario instanceof Empleado empleado) {
			return empleado.getPuntosFidelidad();
		}
		return 0;
	}

	protected Boolean verificarBebidaCaliente(Instalacion instalacion) {
		return instalacion.getTipo().equalsIgnoreCase("padel") || (instalacion.getTipo().equalsIgnoreCase("mesa") && instalacion.getTechada());
	}
}
