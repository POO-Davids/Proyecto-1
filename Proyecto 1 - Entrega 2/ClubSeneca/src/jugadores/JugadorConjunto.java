package jugadores;

import equipos.Equipo;
import estadisticas.Estadistica;
import usuarios.Usuario;

public class JugadorConjunto extends Jugador {

	private String posicion;
	private int camiseta;
	private String equipoId;
	private String deporte;

	public JugadorConjunto(String idJugador, Usuario usuario, String posicion, int camiseta, Equipo equipo) {
		this(idJugador, usuario, posicion, camiseta, equipo.getIdEquipo(), equipo.getDeporte());
	}

	public JugadorConjunto(String idJugador, Usuario usuario, String posicion, int camiseta, String equipoId, String deporte) {
		super(idJugador, usuario);
		this.posicion = posicion;
		this.camiseta = camiseta;
		this.equipoId = equipoId;
		this.deporte = deporte;
		this.estadisticaAcumulada = Estadistica.crear(deporte, idJugador);
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public int getCamiseta() {
		return camiseta;
	}

	public void setCamiseta(int camiseta) {
		this.camiseta = camiseta;
	}

	public String getEquipoId() {
		return equipoId;
	}

	@Override
	public String getDeporte() {
		return deporte;
	}
}
