package usuarios;

import java.time.LocalDate;
import java.util.List;

public class CentralCambioTurno {

	private int minimoEntrenadores = 2;
	private int minimoFisioterapeutas = 1;

	public Boolean interfazCambioTurno(Boolean[] horario) {
		return verificarValidezCambioTurno(horario);
	}

	private Boolean verificarValidezCambioTurno(Boolean[] horario) {
		if (horario == null || horario.length != 24) {
			return false;
		}
		for (Boolean hora : horario) {
			if (hora == null) {
				return false;
			}
		}
		return true;
	}

	public Boolean verificarCobertura(
        LocalDate fecha,
        List<Empleado> empleados,
        Boolean[] horarioAtencion) {

		if (empleados == null
				|| !verificarValidezCambioTurno(horarioAtencion)) {
			return false;
		}

		for (int hora = 0; hora < horarioAtencion.length; hora++) {

			if (horarioAtencion[hora]) {

				if (contarEmpleadosEnHora(
						"Entrenador", fecha, hora, empleados)
						< minimoEntrenadores
						||
						contarEmpleadosEnHora(
						"Fisioterapeuta", fecha, hora, empleados)
						< minimoFisioterapeutas) {

					return false;
				}
			}
		}

		return true;
	}

	private int contarEmpleadosEnHora(
			String tipo,
			LocalDate fecha,
			int hora,
			List<Empleado> empleados) {

		int contador = 0;

		for (Empleado empleado : empleados) {

			if (empleado.getTipo().equals(tipo)
					&& Boolean.TRUE.equals(
						empleado.getTurno(fecha)[hora])) {

				contador++;
			}
		}

		return contador;
	}
}
