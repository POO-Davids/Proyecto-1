package controladores;

import java.time.LocalDate;

import excepciones.ElementoNoEncontradoException;
import excepciones.ValorInvalidoException;
import tiendas.Articulo;
import tiendas.ArticuloCafeteria;
import tiendas.ArticuloTienda;
import usuarios.Usuario;

public class ControladorEmpleado extends ControladorUsuario {

	public ControladorEmpleado(CentralClubSeneca central, Usuario usuario) {
		super(central, usuario);
	}

	public void compartirCodigoDescuento(String usernameSocio) throws ElementoNoEncontradoException {
		central.getUsuarios().compartirCodigoDescuento(usernameSocio, usuario.getUsername());
	}

	public void solicitarCambioTurno(LocalDate fecha, Boolean[] turnoNuevo) throws ElementoNoEncontradoException, ValorInvalidoException {
		central.getUsuarios().solicitarCambioTurno(usuario.getUsername(), fecha, turnoNuevo);
	}

	public void sugerirArticulo(String tienda, Articulo articulo) throws ValorInvalidoException {
		if (tienda.equalsIgnoreCase("Tienda") && articulo instanceof ArticuloTienda articuloTienda) {
			central.getTienda().sugerirArticulo(articuloTienda);
		} else if (tienda.toLowerCase().startsWith("cafeter") && articulo instanceof ArticuloCafeteria articuloCafeteria) {
			central.getCafeteria().sugerirArticulo(articuloCafeteria);
		} else {
			throw new ValorInvalidoException("El artículo no corresponde a la tienda " + tienda);
		}
	}

	@Override
	public String getRol() {
		return usuario.getTipo();
	}
}
