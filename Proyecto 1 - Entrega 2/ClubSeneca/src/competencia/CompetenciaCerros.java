package competencia;

import java.util.List;

public class CompetenciaCerros extends Competencia {

	public CompetenciaCerros(String idCompetencia, String nombre, String entidad, String temporada, String deporte,
			String categoria, String formato, int nominaMinima, int equivalenciaSuspension) {
		super(idCompetencia, nombre, entidad, temporada, deporte, categoria, formato, nominaMinima, equivalenciaSuspension);
	}

	@Override
	public List<EquipoCompetencia> getTablaPosiciones() {
		return super.getTablaPosiciones();
	}
}
