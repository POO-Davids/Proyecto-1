package persistencia;

import java.io.File;
import java.util.List;

import controladores.CentralClubSeneca;
import excepciones.PersistenciaException;

public class CentralPersistencia {

	private String directorio;
	private List<Persistencia> persistencias;

	public CentralPersistencia(String directorio) {
		this.directorio = directorio;
		this.persistencias = List.of(new PersistenciaUsuarios(), new PersistenciaTiendas(), new PersistenciaInstalaciones(),
				new PersistenciaReservas(), new PersistenciaJugadores(), new PersistenciaEquipos(), new PersistenciaCompetencias(),
				new PersistenciaPartidos(), new PersistenciaEntrenamientos(), new PersistenciaClub());
	}

	public void guardarSesion(CentralClubSeneca club) throws PersistenciaException {
		crearDirectorio();
		for (Persistencia persistencia : persistencias) {
			persistencia.guardar(rutaArchivo(persistencia), club);
		}
	}

	public void cargarSesion(CentralClubSeneca club) throws PersistenciaException {
		if (!new File(directorio).isDirectory()) {
			return;
		}
		for (Persistencia persistencia : persistencias) {
			String ruta = rutaArchivo(persistencia);
			if (new File(ruta).isFile()) {
				persistencia.cargar(ruta, club);
			}
		}
	}

	public String getDirectorio() {
		return directorio;
	}

	public void setDirectorio(String directorio) {
		this.directorio = directorio;
	}

	private String rutaArchivo(Persistencia persistencia) {
		return directorio + File.separator + persistencia.getNombreArchivo();
	}

	private void crearDirectorio() throws PersistenciaException {
		File carpeta = new File(directorio);
		if (!carpeta.isDirectory() && !carpeta.mkdirs()) {
			throw new PersistenciaException("No se pudo crear el directorio " + directorio);
		}
	}
}
