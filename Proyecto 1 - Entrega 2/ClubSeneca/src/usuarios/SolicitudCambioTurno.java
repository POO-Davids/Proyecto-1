package usuarios;

import java.time.LocalDate;
import java.util.Arrays;

public class SolicitudCambioTurno {

    private String idSolicitud;
    private LocalDate fecha;
    private Boolean[] turno;
    private String estado;
    private Empleado empleado;

    public SolicitudCambioTurno(
            String idSolicitud,
            Empleado empleado,
            LocalDate fecha,
            Boolean[] turno) {

        this(idSolicitud, empleado, fecha, turno, "pendiente");
    }

    public SolicitudCambioTurno(
            String idSolicitud,
            Empleado empleado,
            LocalDate fecha,
            Boolean[] turno,
            String estado) {

        this.idSolicitud = idSolicitud;
        this.empleado = empleado;
        this.fecha = fecha;
        this.turno = Arrays.copyOf(turno, turno.length);
        this.estado = estado;
    }

    public void cambiarTurno() {
        empleado.setTurno(fecha, turno);
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Boolean[] getTurno() {
        return Arrays.copyOf(turno, turno.length);
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