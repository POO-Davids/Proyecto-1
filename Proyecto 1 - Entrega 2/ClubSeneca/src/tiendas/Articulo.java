package tiendas;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import excepciones.InventarioInsuficienteException;

public abstract class Articulo {

	public static final String UBICACION_BODEGA = "Bodega";

	protected String nombre;
	protected float precio;
	protected int disponibilidad;
	protected HashMap<String, Integer> ubicaciones;

	public Articulo(String nombre, float precio) {
		this.nombre = nombre;
		this.precio = precio;
		this.disponibilidad = 0;
		this.ubicaciones = new LinkedHashMap<>();
	}

	public String getNombre() {
		return nombre;
	}

	public float getPrecio() {
		return precio;
	}

	public int getDisponibilidad() {
		return disponibilidad;
	}

	public Boolean estaAgotado() {
		return disponibilidad == 0;
	}

	public void abastecer(int cantidad) {
		ubicaciones.merge(UBICACION_BODEGA, cantidad, Integer::sum);
		disponibilidad += cantidad;
	}

	public void descontar(int cantidad) throws InventarioInsuficienteException {
		if (cantidad > disponibilidad) {
			throw new InventarioInsuficienteException("Solo hay " + disponibilidad + " unidades de " + nombre);
		}
		disponibilidad -= cantidad;
		int pendiente = cantidad;
		for (Map.Entry<String, Integer> ubicacion : ubicaciones.entrySet()) {
			int sacar = Math.min(pendiente, ubicacion.getValue());
			ubicacion.setValue(ubicacion.getValue() - sacar);
			pendiente -= sacar;
		}
	}

	public int getUnidadesEn(String ubicacion) {
		return ubicaciones.getOrDefault(ubicacion, 0);
	}

	public void moverUnidades(String origen, String destino, int cantidad) throws InventarioInsuficienteException {
		if (cantidad <= 0 || getUnidadesEn(origen) < cantidad) {
			throw new InventarioInsuficienteException("Solo hay " + getUnidadesEn(origen) + " unidades de " + nombre + " en " + origen);
		}
		ubicaciones.put(origen, getUnidadesEn(origen) - cantidad);
		ubicaciones.put(destino, getUnidadesEn(destino) + cantidad);
	}

	public HashMap<String, Integer> getUbicaciones() {
		return ubicaciones;
	}

	public void setUbicaciones(HashMap<String, Integer> ubicaciones) {
		this.ubicaciones = new LinkedHashMap<>(ubicaciones);
		this.disponibilidad = ubicaciones.values().stream().mapToInt(Integer::intValue).sum();
	}

	@Override
	public String toString() {
		return nombre + " | precio: " + precio + " | disponibilidad: " + disponibilidad + (estaAgotado() ? " (AGOTADO)" : "")
				+ " | ubicaciones: " + ubicaciones;
	}
}
