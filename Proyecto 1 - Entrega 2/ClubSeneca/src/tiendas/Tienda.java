package tiendas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import excepciones.CodigoDescuentoInvalidoException;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import excepciones.InventarioInsuficienteException;
import excepciones.ValorInvalidoException;
import usuarios.Empleado;
import usuarios.Socio;
import usuarios.Usuario;

public class Tienda {

	private String[] codigosDescuento;
	private float descuentoEmpleado = 0.15f;
	private float descuentoSocioConCodigo = 0.08f;
	private float IVA = 0.19f;
	private List<VentaTienda> ventas;
	private LinkedHashMap<String, ArticuloTienda> inventario;
	private LinkedHashMap<String, ArticuloTienda> sugerencias;
	private int contadorVentas;

	public Tienda() {
		codigosDescuento = new String[0];
		ventas = new ArrayList<>();
		inventario = new LinkedHashMap<>();
		sugerencias = new LinkedHashMap<>();
	}

	public void agregarArticulo(ArticuloTienda articulo) throws ElementoDuplicadoException {
		if (getArticulo(articulo.getNombre()) != null) {
			throw new ElementoDuplicadoException("Ya existe el artículo " + articulo.getNombre() + " en la tienda");
		}
		inventario.put(articulo.getNombre().toLowerCase(), articulo);
	}

	public ArticuloTienda getArticulo(String nombre) {
		return inventario.get(nombre.toLowerCase());
	}

	public void abastecer(String nombre, int cantidad) throws ElementoNoEncontradoException, ValorInvalidoException {
		if (cantidad <= 0) {
			throw new ValorInvalidoException("La cantidad debe ser mayor que cero");
		}
		ArticuloTienda articulo = getArticulo(nombre);
		if (articulo == null) {
			throw new ElementoNoEncontradoException("No existe el artículo " + nombre + " en la tienda");
		}
		articulo.abastecer(cantidad);
	}

	public void sugerirArticulo(ArticuloTienda articulo) {
		sugerencias.put(articulo.getNombre().toLowerCase(), articulo);
	}

	public void aceptarSugerencia(String nombre) throws ElementoNoEncontradoException, ElementoDuplicadoException {
		ArticuloTienda articulo = sugerencias.get(nombre.toLowerCase());
		if (articulo == null) {
			throw new ElementoNoEncontradoException("No hay una sugerencia de " + nombre);
		}
		agregarArticulo(articulo);
		sugerencias.remove(nombre.toLowerCase());
	}

	public void denegarSugerencia(String nombre) throws ElementoNoEncontradoException {
		if (sugerencias.remove(nombre.toLowerCase()) == null) {
			throw new ElementoNoEncontradoException("No hay una sugerencia de " + nombre);
		}
	}

	public void agregarCodigoDescuento(String codigo) {
		if (validarCodigoDescuento(codigo)) {
			return;
		}
		codigosDescuento = Arrays.copyOf(codigosDescuento, codigosDescuento.length + 1);
		codigosDescuento[codigosDescuento.length - 1] = codigo;
	}

	public Boolean validarCodigoDescuento(String codigo) {
		return Arrays.asList(codigosDescuento).contains(codigo);
	}

	public float calcularDescuentoCodigo(Usuario comprador, String codigo, float subtotal) throws CodigoDescuentoInvalidoException {
		if (comprador instanceof Empleado empleado && codigo.equals(empleado.getCodigoDescuentoTienda())) {
			return Math.round(subtotal * descuentoEmpleado);
		}
		if (comprador instanceof Socio socio && codigo.equals(socio.getCodigoDescuentoDado())) {
			return Math.round(subtotal * descuentoSocioConCodigo);
		}
		throw new CodigoDescuentoInvalidoException("El código " + codigo + " no es válido para " + comprador.getUsername());
	}

	public VentaTienda registrarVenta(Usuario comprador, String[] nombres, int[] cantidades, String codigo, float puntosRedimidos)
			throws ElementoNoEncontradoException, InventarioInsuficienteException, CodigoDescuentoInvalidoException,
			ValorInvalidoException {
		if (comprador == null || nombres == null
					|| cantidades == null || nombres.length == 0
					|| nombres.length != cantidades.length) {

				throw new ValorInvalidoException(
						"Los datos de la compra no son validos");
			}

		for (int i = 0; i < nombres.length; i++) {

				if (nombres[i] == null || nombres[i].isBlank()
						|| cantidades[i] <= 0) {

					throw new ValorInvalidoException(
							"Nombre o cantidad de articulo no valida");
				}
			}
		List<ArticuloTienda> articulos = new ArrayList<>();
		float subtotal = 0;
		for (int i = 0; i < nombres.length; i++) {
			ArticuloTienda articulo = getArticulo(nombres[i]);
			if (articulo == null) {
				throw new ElementoNoEncontradoException("No existe el artículo " + nombres[i] + " en la tienda");
			}
			articulos.add(articulo);
			subtotal += articulo.getPrecio() * cantidades[i];
		}
		if (!verificarDisponibilidad(nombres, cantidades)) {
			throw new InventarioInsuficienteException("No hay unidades suficientes para la compra");
		}
		float descuento = 0;
		if (codigo != null && !codigo.isBlank()) {
			if (!validarCodigoDescuento(codigo)) {
				throw new CodigoDescuentoInvalidoException("El código " + codigo + " no existe");
			}
			descuento = calcularDescuentoCodigo(comprador, codigo, subtotal);
		}
		float base = subtotal - descuento - puntosRedimidos;
		if (puntosRedimidos < 0 || base < 0) {
			throw new ValorInvalidoException("Los puntos a redimir no pueden superar el valor de la compra");
		}
		VentaTienda venta = new VentaTienda(generarId(), comprador, articulos, cantidades, LocalDate.now(), descuento,
				puntosRedimidos, Math.round(base * IVA));
		for (int i = 0; i < articulos.size(); i++) {
			articulos.get(i).descontar(cantidades[i]);
		}
		ventas.add(venta);
		return venta;
	}

	private Boolean verificarDisponibilidad(String[] nombres, int[] cantidades) {
		LinkedHashMap<String, Integer> pedido = new LinkedHashMap<>();
		for (int i = 0; i < nombres.length; i++) {
			pedido.merge(nombres[i].toLowerCase(), cantidades[i], Integer::sum);
		}
		for (String nombre : pedido.keySet()) {
			if (getArticulo(nombre).getDisponibilidad() < pedido.get(nombre)) {
				return false;
			}
		}
		return true;
	}

	public List<VentaTienda> getVentasEntre(LocalDate inicio, LocalDate fin) {
		List<VentaTienda> resultado = new ArrayList<>();
		for (VentaTienda venta : ventas) {
			if (!venta.getFecha().isBefore(inicio) && !venta.getFecha().isAfter(fin)) {
				resultado.add(venta);
			}
		}
		return resultado;
	}

	public String consultarInventario() {
		StringBuilder texto = new StringBuilder("== Inventario tienda ==\n");
		for (ArticuloTienda articulo : inventario.values()) {
			texto.append(articulo).append("\n");
		}
		return texto.toString();
	}

	private String generarId() {
		String id;
		do {
			id = "VT-" + (++contadorVentas);
		} while (existeVenta(id));
		return id;
	}

	private boolean existeVenta(String id) {
		for (VentaTienda venta : ventas) {
			if (venta.getIdVenta().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public List<ArticuloTienda> getInventario() {
		return new ArrayList<>(inventario.values());
	}

	public List<ArticuloTienda> getSugerencias() {
		return new ArrayList<>(sugerencias.values());
	}

	public List<VentaTienda> getVentas() {
		return ventas;
	}

	public String[] getCodigosDescuento() {
		return codigosDescuento;
	}

	public void agregarVenta(VentaTienda venta) {
		ventas.add(venta);
	}
}
