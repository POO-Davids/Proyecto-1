package persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controladores.CentralClubSeneca;
import excepciones.PersistenciaException;

public abstract class Persistencia {

	protected String nombreArchivo;

	public Persistencia(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public abstract void guardar(String ruta, CentralClubSeneca club) throws PersistenciaException;

	public abstract void cargar(String ruta, CentralClubSeneca club) throws PersistenciaException;

	protected JSONObject leerJSON(String ruta) throws PersistenciaException {
		try {
			return new JSONObject(Files.readString(Path.of(ruta), StandardCharsets.UTF_8));
		} catch (IOException | JSONException e) {
			throw new PersistenciaException("No se pudo leer " + ruta + ": " + e.getMessage());
		}
	}

	protected void escribirJSON(String ruta, JSONObject json) throws PersistenciaException {
		try {
			Files.writeString(Path.of(ruta), json.toString(2), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new PersistenciaException("No se pudo escribir " + ruta + ": " + e.getMessage());
		}
	}

	protected String fechaAJSON(LocalDate fecha) {
		return fecha.toString();
	}

	protected LocalDate fechaDesdeJSON(String texto) {
		return LocalDate.parse(texto);
	}

	protected JSONArray textosAJSON(String[] textos) {
		return new JSONArray(textos);
	}

	protected String[] textosDesdeJSON(JSONArray json) {
		String[] textos = new String[json.length()];
		for (int i = 0; i < textos.length; i++) {
			textos[i] = json.getString(i);
		}
		return textos;
	}

	protected JSONArray enterosAJSON(int[] enteros) {
		return new JSONArray(enteros);
	}

	protected int[] enterosDesdeJSON(JSONArray json) {
		int[] enteros = new int[json.length()];
		for (int i = 0; i < enteros.length; i++) {
			enteros[i] = json.getInt(i);
		}
		return enteros;
	}

	protected JSONArray horasAJSON(Boolean[] horas) {
		return new JSONArray(horas);
	}

	protected Boolean[] horasDesdeJSON(JSONArray json) {
		Boolean[] horas = new Boolean[json.length()];
		for (int i = 0; i < horas.length; i++) {
			horas[i] = json.getBoolean(i);
		}
		return horas;
	}
}
