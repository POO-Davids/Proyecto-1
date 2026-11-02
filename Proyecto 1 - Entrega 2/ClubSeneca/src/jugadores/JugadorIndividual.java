package jugadores;

import java.util.List;

import estadisticas.Estadistica;
import usuarios.Usuario;

public class JugadorIndividual extends Jugador {

	public static final List<String> NIVELES = List.of("Principiante", "Intermedio", "Avanzado");

	private String nivel;
	private String disciplina;
	private float elo;

	public JugadorIndividual(String idJugador, Usuario usuario, String disciplina, String nivel, float elo) {
		super(idJugador, usuario);
		this.disciplina = disciplina;
		this.nivel = nivel;
		this.elo = elo;
		this.estadisticaAcumulada = Estadistica.crear(disciplina, idJugador);
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(String disciplina) {
		this.disciplina = disciplina;
	}

	public float getElo() {
		return elo;
	}

	public void setElo(float elo) {
		this.elo = elo;
	}

	@Override
	public String getDeporte() {
		return disciplina;
	}

	public Boolean esNivelCompatible(JugadorIndividual otro) {
		if (esEntrenador || otro.getEsEntrenador()) {
			return true;
		}
		return Math.abs(NIVELES.indexOf(nivel) - NIVELES.indexOf(otro.getNivel())) <= 1;
	}
}
