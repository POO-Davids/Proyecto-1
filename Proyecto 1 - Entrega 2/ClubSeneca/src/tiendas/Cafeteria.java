package tiendas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import excepciones.InventarioInsuficienteException;
import excepciones.ValorInvalidoException;
import usuarios.Usuario;

public class Cafeteria {

	private float impuestoConsumo = 0.08f;
	private float propinaSugerida = 0.10f;
	private LinkedHashMap<String, ArticuloCafeteria> inventario;
	private LinkedHashMap<String, ArticuloCafeteria> sugerencias;
	private List<VentaCafeteria> ventas;
	private int contadorVentas;

	public Cafeteria() {
		inventario = new LinkedHashMap<>();
		sugerencias = new LinkedHashMap<>();
		ventas = new ArrayList<>();
	}

	public void agregarArticulo(ArticuloCafeteria articulo) throws ElementoDuplicadoException {
		if (getArticulo(articulo.getNombre()) != null) {
			throw new ElementoDuplicadoException("Ya existe el artículo " + articulo.getNombre() + " en la cafetería");
		}
		inventario.put(articulo.getNombre().toLowerCase(), articulo);
	}

	public ArticuloCafeteria getArticulo(String nombre) {
		return inventario.get(nombre.toLowerCase());
	}

	public void abastecer(String nombre, int cantidad) throws ElementoNoEncontradoException, ValorInvalidoException {
		if (cantidad <= 0) {
			throw new ValorInvalidoException("La cantidad debe ser mayor que cero");
		}
		ArticuloCafeteria articulo = getArticulo(nombre);
		if (articulo == null) {
			throw new ElementoNoEncontradoException("No existe el artículo " + nombre + " en la cafetería");
		}
		articulo.abastecer(cantidad);
	}

	public void sugerirArticulo(ArticuloCafeteria articulo) {
		sugerencias.put(articulo.getNombre().toLowerCase(), articulo);
	}

	public void aceptarSugerencia(String nombre) throws ElementoNoEncontradoException, ElementoDuplicadoException {
		ArticuloCafeteria articulo = sugerencias.get(nombre.toLowerCase());
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

	public String[] consultarAlergenos(String[] nombres) throws ElementoNoEncontradoException {
		LinkedHashSet<String> alergenos = new LinkedHashSet<>();
		for (String nombre : nombres) {
			ArticuloCafeteria articulo = getArticulo(nombre);
			if (articulo == null) {
				throw new ElementoNoEncontradoException("No existe el artículo " + nombre + " en la cafetería");
			}
			if (articulo instanceof Snack snack) {
				alergenos.addAll(List.of(snack.getAlergenos()));
			}
		}
		return alergenos.toArray(new String[0]);
	}

	public float calcularPropinaSugerida(float subtotal) {
		return Math.round(subtotal * propinaSugerida);
	}

	public float calcularImpuesto(float base) {
		return Math.round(base * impuestoConsumo);
	}

	public VentaCafeteria registrarVenta(Usuario comprador, String[] nombres, int[] cantidades, float propina, float puntosRedimidos)
			throws ElementoNoEncontradoException, InventarioInsuficienteException, ValorInvalidoException {
		if (propina < 0) {
			throw new ValorInvalidoException("La propina no puede ser negativa");
		}
		List<ArticuloCafeteria> articulos = new ArrayList<>();
		float subtotal = 0;
		for (int i = 0; i < nombres.length; i++) {
			ArticuloCafeteria articulo = getArticulo(nombres[i]);
			if (articulo == null) {
				throw new ElementoNoEncontradoException("No existe el artículo " + nombres[i] + " en la cafetería");
			}
			articulos.add(articulo);
			subtotal += articulo.getPrecio() * cantidades[i];
		}
		if (!verificarDisponibilidad(nombres, cantidades)) {
			throw new InventarioInsuficienteException("No hay unidades suficientes para la compra");
		}
		float base = subtotal - puntosRedimidos;
		if (puntosRedimidos < 0 || base < 0) {
			throw new ValorInvalidoException("Los puntos a redimir no pueden superar el valor de la compra");
		}
		VentaCafeteria venta = new VentaCafeteria(generarId(), comprador, articulos, cantidades, LocalDate.now(), propina,
				puntosRedimidos, calcularImpuesto(base));
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

	public List<VentaCafeteria> getVentasEntre(LocalDate inicio, LocalDate fin) {
		List<VentaCafeteria> resultado = new ArrayList<>();
		for (VentaCafeteria venta : ventas) {
			if (!venta.getFecha().isBefore(inicio) && !venta.getFecha().isAfter(fin)) {
				resultado.add(venta);
			}
		}
		return resultado;
	}

	public String consultarInventario() {
		StringBuilder texto = new StringBuilder("== Inventario cafetería ==\n");
		for (ArticuloCafeteria articulo : inventario.values()) {
			texto.append(articulo).append("\n");
		}
		return texto.toString();
	}

	private String generarId() {
		String id;
		do {
			id = "VC-" + (++contadorVentas);
		} while (existeVenta(id));
		return id;
	}

	private boolean existeVenta(String id) {
		for (VentaCafeteria venta : ventas) {
			if (venta.getIdVenta().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public List<ArticuloCafeteria> getInventario() {
		return new ArrayList<>(inventario.values());
	}

	public List<ArticuloCafeteria> getSugerencias() {
		return new ArrayList<>(sugerencias.values());
	}

	public List<VentaCafeteria> getVentas() {
		return ventas;
	}

	public void agregarVenta(VentaCafeteria venta) {
		ventas.add(venta);
	}
}
