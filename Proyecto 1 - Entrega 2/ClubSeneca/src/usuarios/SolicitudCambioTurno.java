package usuarios;

public class SolicitudCambioTurno {

	private String idSolicitud;
	private Boolean[] turno;
	private String estado;
	private Empleado empleado;

	public SolicitudCambioTurno(String idSolicitud, Empleado empleado, Boolean[] turno) {
		this(idSolicitud, empleado, turno, "pendiente");
	}

	public SolicitudCambioTurno(String idSolicitud, Empleado empleado, Boolean[] turno, String estado) {
		this.idSolicitud = idSolicitud;
		this.empleado = empleado;
		this.turno = turno;
		this.estado = estado;
	}

	public void cambiarTurno() {
		empleado.setTurno(null, turno);
	}

	public String getIdSolicitud() {
		return idSolicitud;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public Boolean[] getTurno() {
		return turno;
	}

	public String getEstado() {
		return estado;
	}

	public void aprobar() {
		estado = "aprobada";
	}

	public void rechazar() {
		estado = "rechazada";
	}
}
