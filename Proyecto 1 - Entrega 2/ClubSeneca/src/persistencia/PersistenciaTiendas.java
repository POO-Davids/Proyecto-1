package persistencia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controladores.CentralClubSeneca;
import excepciones.ElementoDuplicadoException;
import excepciones.PersistenciaException;
import tiendas.Articulo;
import tiendas.ArticuloCafeteria;
import tiendas.ArticuloTienda;
import tiendas.Bebida;
import tiendas.Cafeteria;
import tiendas.Snack;
import tiendas.Tienda;
import tiendas.Venta;
import tiendas.VentaCafeteria;
import tiendas.VentaTienda;
import usuarios.Usuario;

public class PersistenciaTiendas extends Persistencia {

	private static final String ARCHIVO = "tiendas.json";

	public PersistenciaTiendas() {
		super(ARCHIVO);
	}

	@Override
	public void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		Tienda tienda = club.getTienda();
		Cafeteria cafeteria = club.getCafeteria();
		JSONObject jsonTienda = new JSONObject().put("codigosDescuento", textosAJSON(tienda.getCodigosDescuento()))
				.put("inventario", articulosAJSON(tienda.getInventario())).put("sugerencias", articulosAJSON(tienda.getSugerencias()))
				.put("ventas", ventasAJSON(tienda.getVentas()));
		JSONObject jsonCafeteria = new JSONObject().put("inventario", articulosAJSON(cafeteria.getInventario()))
				.put("sugerencias", articulosAJSON(cafeteria.getSugerencias())).put("ventas", ventasAJSON(cafeteria.getVentas()));
		escribirJSON(ruta, new JSONObject().put("tienda", jsonTienda).put("cafeteria", jsonCafeteria));
	}

	@Override
	public void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException {
		JSONObject json = leerJSON(ruta);
		try {
			Tienda tienda = club.getTienda();
			Cafeteria cafeteria = club.getCafeteria();
			JSONObject jsonTienda = json.getJSONObject("tienda");
			for (String codigo : textosDesdeJSON(jsonTienda.getJSONArray("codigosDescuento"))) {
				tienda.agregarCodigoDescuento(codigo);
			}
			for (Articulo articulo : articulosDesdeJSON(jsonTienda.getJSONArray("inventario"))) {
				tienda.agregarArticulo((ArticuloTienda) articulo);
			}
			for (Articulo articulo : articulosDesdeJSON(jsonTienda.getJSONArray("sugerencias"))) {
				tienda.sugerirArticulo((ArticuloTienda) articulo);
			}
			JSONObject jsonCafeteria = json.getJSONObject("cafeteria");
			for (Articulo articulo : articulosDesdeJSON(jsonCafeteria.getJSONArray("inventario"))) {
				cafeteria.agregarArticulo((ArticuloCafeteria) articulo);
			}
			for (Articulo articulo : articulosDesdeJSON(jsonCafeteria.getJSONArray("sugerencias"))) {
				cafeteria.sugerirArticulo((ArticuloCafeteria) articulo);
			}
			JSONArray ventas = jsonTienda.getJSONArray("ventas");
			for (int i = 0; i < ventas.length(); i++) {
				tienda.agregarVenta((VentaTienda) ventaDesdeJSON(ventas.getJSONObject(i), club));
			}
			ventas = jsonCafeteria.getJSONArray("ventas");
			for (int i = 0; i < ventas.length(); i++) {
				cafeteria.agregarVenta((VentaCafeteria) ventaDesdeJSON(ventas.getJSONObject(i), club));
			}
		} catch (JSONException | ClassCastException | NullPointerException | ElementoDuplicadoException e) {
			throw new PersistenciaException("Archivo " + ARCHIVO + " inválido: " + e.getMessage());
		}
	}

	private JSONArray articulosAJSON(List<? extends Articulo> articulos) {
		JSONArray lista = new JSONArray();
		for (Articulo articulo : articulos) {
			lista.put(articuloAJSON(articulo));
		}
		return lista;
	}

	private List<Articulo> articulosDesdeJSON(JSONArray json) {
		List<Articulo> articulos = new ArrayList<>();
		for (int i = 0; i < json.length(); i++) {
			articulos.add(articuloDesdeJSON(json.getJSONObject(i)));
		}
		return articulos;
	}

	private JSONObject articuloAJSON(Articulo articulo) {
		JSONObject json = new JSONObject().put("tipo", articulo.getClass().getSimpleName()).put("nombre", articulo.getNombre())
				.put("precio", articulo.getPrecio()).put("ubicaciones", new JSONObject(articulo.getUbicaciones()));
		if (articulo instanceof ArticuloTienda articuloTienda) {
			json.put("categoria", articuloTienda.getCategoria());
		} else if (articulo instanceof Bebida bebida) {
			json.put("esCaliente", bebida.getEsCaliente());
		} else if (articulo instanceof Snack snack) {
			json.put("alergenos", textosAJSON(snack.getAlergenos()));
		}
		return json;
	}

	private Articulo articuloDesdeJSON(JSONObject json) {
		String nombre = json.getString("nombre");
		float precio = json.getFloat("precio");
		Articulo articulo = switch (json.getString("tipo")) {
		case "Bebida" -> new Bebida(nombre, precio, json.getBoolean("esCaliente"));
		case "Snack" -> new Snack(nombre, precio, textosDesdeJSON(json.getJSONArray("alergenos")));
		default -> new ArticuloTienda(nombre, precio, json.getString("categoria"));
		};
		HashMap<String, Integer> ubicaciones = new HashMap<>();
		JSONObject jsonUbicaciones = json.getJSONObject("ubicaciones");
		for (String ubicacion : jsonUbicaciones.keySet()) {
			ubicaciones.put(ubicacion, jsonUbicaciones.getInt(ubicacion));
		}
		articulo.setUbicaciones(ubicaciones);
		return articulo;
	}

	private JSONArray ventasAJSON(List<? extends Venta> ventas) {
		JSONArray lista = new JSONArray();
		for (Venta venta : ventas) {
			lista.put(ventaAJSON(venta));
		}
		return lista;
	}

	private JSONObject ventaAJSON(Venta venta) {
		JSONObject json = new JSONObject().put("tipo", venta.getClass().getSimpleName()).put("idVenta", venta.getIdVenta())
				.put("comprador", venta.getComprador().getUsername()).put("fecha", fechaAJSON(venta.getFecha()))
				.put("puntosRedimidos", venta.getPuntosRedimidos()).put("iva", venta.getIva());
		JSONArray articulos = new JSONArray();
		if (venta instanceof VentaTienda ventaTienda) {
			ventaTienda.getArticulos().forEach(articulo -> articulos.put(articulo.getNombre()));
			json.put("cantidades", enterosAJSON(ventaTienda.getCantidades())).put("descuentoPorCodigo", ventaTienda.getDescuentoPorCodigo());
		} else if (venta instanceof VentaCafeteria ventaCafeteria) {
			ventaCafeteria.getArticulos().forEach(articulo -> articulos.put(articulo.getNombre()));
			json.put("cantidades", enterosAJSON(ventaCafeteria.getCantidades())).put("propina", ventaCafeteria.getPropina());
		}
		return json.put("articulos", articulos);
	}

	private Venta ventaDesdeJSON(JSONObject json, CentralClubSeneca club) {
		Usuario comprador = club.getUsuarios().getUsuario(json.getString("comprador"));
		String[] nombres = textosDesdeJSON(json.getJSONArray("articulos"));
		int[] cantidades = enterosDesdeJSON(json.getJSONArray("cantidades"));
		if (json.getString("tipo").equals("VentaTienda")) {
			List<ArticuloTienda> articulos = new ArrayList<>();
			for (String nombre : nombres) {
				articulos.add(club.getTienda().getArticulo(nombre));
			}
			return new VentaTienda(json.getString("idVenta"), comprador, articulos, cantidades, fechaDesdeJSON(json.getString("fecha")),
					json.getFloat("descuentoPorCodigo"), json.getFloat("puntosRedimidos"), json.getFloat("iva"));
		}
		List<ArticuloCafeteria> articulos = new ArrayList<>();
		for (String nombre : nombres) {
			articulos.add(club.getCafeteria().getArticulo(nombre));
		}
		return new VentaCafeteria(json.getString("idVenta"), comprador, articulos, cantidades, fechaDesdeJSON(json.getString("fecha")),
				json.getFloat("propina"), json.getFloat("puntosRedimidos"), json.getFloat("iva"));
	}
}
