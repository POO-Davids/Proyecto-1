package amonestaciones;

public class AmonestacionFutbol extends Amonestacion {

	private int amarillas;
	private int rojas;

	public AmonestacionFutbol(String idJugador, int amarillas, int rojas) {
		super(idJugador, amarillas + " amarilla(s), " + rojas + " roja(s)");
		this.amarillas = amarillas;
		this.rojas = rojas;
	}

	public int getAmarillas() {
		return amarillas;
	}

	public int getRojas() {
		return rojas;
	}

	public void setAmarillas(int amarillas) {
		this.amarillas = amarillas;
		this.descripcion = amarillas + " amarilla(s), " + rojas + " roja(s)";
	}

	public void setRojas(int rojas) {
		this.rojas = rojas;
		this.descripcion = amarillas + " amarilla(s), " + rojas + " roja(s)";
	}

	@Override
	public Boolean generaSuspension(int maximoAmarillas) {
		return amarillas >= maximoAmarillas || rojas >= 1;
	}
}
