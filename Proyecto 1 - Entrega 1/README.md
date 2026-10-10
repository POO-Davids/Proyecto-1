| Integrante               | Código    |
|--------------------------|-----------|
| David Triana             | 202410344 |
| David Herrera            | 202314421 |
| David Martinez Jaramillo | 202513297 |

# Proyecto #1 Entrega 1 - Análisis

## Modelo Domino Completo

![Modelo de dominio completo](diagramas/diagrama.png)

Debido a la relativa dificultad para leer este diagrama de dominio, hemos decidido hacer particiones del mismo, las cuales se muestran en las próximas 11 páginas.

### Club Seneca

![Club Seneca](diagramas/01_Club_Seneca_vista_general_.png)

### Usuarios

![Usuarios](diagramas/02_Usuarios.png)

### Empleados

![Empleados](diagramas/03_Empleados_y_Turnos.png)

### Equipos y Jugadores

![Equipos y Jugadores](diagramas/04_Equipos_y_Jugadores.png)

### Partidos

![Partidos](diagramas/05_Partidos.png)

### Competencias

![Competencias](diagramas/06_Competencias.png)

### Estadísticas

![Estadísticas](diagramas/07_Estadisticas.png)

### Amonestaciones

![Amonestaciones](diagramas/08_Amonestaciones.png)

### Entrenamientos

![Entrenamientos](diagramas/09_Entrenamientos.png)

### Instalaciones

![Instalaciones](diagramas/10_Instalaciones_y_Reservas.png)

### Tienda y Cafetería

![Tienda y Cafetería](diagramas/11_Tienda_y_Cafeteria.png)

---

## Requerimientos Funcionales

### Administrador

#### RF-A01 Crear Equipos

El sistema debe permitir al administrador crear equipos.

**Programa de prueba:** (1) El Administrador inicia sesión con su login y password. (2) Crea un Equipo con nombre="Fútbol Masculino", deporte="Fútbol", categoria="Sub-17” y cupo=20. (3) El equipo existe con la lista jugadores (JugadorConjunto[*]) vacía. (4) El equipo se agrega a ClubSeneca.Equipos (5) Se intenta crear otro equipo con el mismo nombre, deporte y categoría, y el sistema lo rechaza.

#### RF-A02 Dar de Baja Equipos

El sistema debe permitir al administrador dar de baja equipos.

**Programa de prueba:** (1) Existe un Equipo "Voleibol Femenino" con 2 JugadorConjunto asociados. (2) El administrador lo elimina de la lista de equipos. (3) El equipo ya no aparece en la lista de equipos del ClubSeneca (4) Los partidos internos que este equipo jugó contra otro que ya existe no se eliminan, sino que el equipo se reemplaza por un equipo anónimo (único en equipos).

#### RF-A03 Crear Jugadores

El sistema debe permitir al administrador crear jugadores.

**Programa de prueba:** (1) Existe un Socio “Ana” (edad="20") con 0 jugadores en jugandoComo. (2) El administrador crea un JugadorConjunto (posicion="Delantera", camiseta=9) en el Equipo “Fútbol Femenino” (categoria="Mayores") y un JugadorIndividual (nivel="Intermedio", disciplina="tenis", elo=1000), los dos con usuario=Ana. Se verifica que Ana tenga 2 jugadores en jugandoComo y que el JugadorConjunto aparezca en los jugadores del equipo. (3) Se intenta lo siguiente, y el sistema lo rechaza en todos los casos:

- Crearle a Ana otro jugador del mismo deporte
- Crear un jugador en “Fútbol Femenino” con camiseta=9 (número repetido en el equipo)
- Crear un jugador en un equipo que ya llegó a su cupo
- Crear un jugador en un equipo cuya categoría no corresponde a la edad del socio (por ejemplo, Ana en un equipo Sub-17).

#### RF-A04 Dar de Baja Jugadores

El sistema debe permitir al administrador dar de baja jugadores.

**Programa de prueba:** (1) Existe un JugadorConjunto en el equipo “Baloncesto A” con EstadisticaBasket registradas. (2) El administrador lo da de baja. (3) Se verifica que el jugador salga de los jugadores del equipo y del jugandoComo del socio asociado. (4) Se verifica que, en los partidos que jugó, ese jugador quede reemplazado por un jugador anónimo, que es único en el sistema.

#### RF-A05 Consultar inventario

El sistema debe permitir al administrador consultar el estado del inventario de las instalaciones y artículos.

**Programa de prueba:** (1) El club tiene en ClubSeneca.inventarioInstalaciones una Instalacion de tipo="tenis" y otra de tipo="mesa", cada una con su disponibilidad (Horario). Tiene 3 ArticuloDeportivo en Tienda.inventario y 1 Bebida y 1 Snack en Cafeteria.inventario. (2) El administrador consulta el inventario. (3) Se verifica que el reporte muestre cada instalación con su tipo, su admite y su horario, y cada artículo con nombre, precio y disponibilidad. (4) Se verifica que un artículo con disponibilidad=0 aparezca marcado como agotado.

#### RF-A06 Mover Artículos

El sistema debe permitir al administrador mover artículos entre ubicaciones.

**Programa de prueba:** (1) Un ArticuloDeportivo “Balón Fútbol” tiene disponibilidad=10 en la ubicación de origen y 2 en la de destino. (2) El administrador mueve 5 unidades. (3) Se verifica que el origen quede con 5 y el destino con 7, y que el total no cambie. (4) Se intenta mover 8 unidades desde un origen que tiene 5, y el sistema lo rechaza sin modificar el inventario.

#### RF-A07 Abastecer La Tienda y Cafetería

El sistema debe permitir al administrador abastecer la tienda y cafetería.

**Programa de prueba:** (1) En Tienda.inventario está el ArticuloDeportivo “Raqueta Pádel” con disponibilidad=0, y en Cafeteria.inventario está el Snack “Barra de granola” con disponibilidad=2. (2) El administrador abastece la raqueta con 15 unidades y el snack con 20. (3) Se verifica que la raqueta quede con disponibilidad=15 y se pueda volver a comprar, y que el snack quede con disponibilidad=22. (4) El administrador abastece un artículo nuevo (“Grip”, categoria="Accesorios", precio=12000), y se verifica que se agregue a Tienda.inventario. (5) Se intenta abastecer con una cantidad <= 0, y el sistema lo rechaza.

#### RF-A08 Administrar Turnos

El sistema debe permitir al administrador administrar los turnos de los empleados.

**Programa de prueba:** (1) En ClubSeneca.usuarios hay 3 Entrenador y 2 Fisioterapeuta. ClubSeneca.horarioAtencion tiene h8 a h19 en true, y los turnos actuales cubren cada hora con al menos 2 entrenadores y 1 fisioterapeuta. (2) El administrador asigna al Entrenador “Mario” un turno (Horario) para el lunes 2026-10-12 con h8 a h11 en true. (3) Se verifica que Mario se pueda asignar a una SesionEntrenamiento ese día con horaInicio=9, y que se rechace con horaInicio=14. (4) El administrador le quita el turno (todas las horas en false), y se verifica que Mario ya no se pueda asignar a sesiones nuevas. (5) Un fisioterapeuta crea una solicitudCambioTurno que dejaría 0 fisioterapeutas en algún bloque horario. El administrador intenta aprobarla, el sistema lo rechaza, la solicitud queda con estado="rechazada" y el turno no cambia.

#### RF-A09 Generar Reportes Deportivos

El sistema debe permitir al administrador generar reportes deportivo sobre jugadores y partidos.

**Programa de prueba:** (1) En una CompetenciaASCUN de fútbol hay 2 PartidoCompetencia con marcador, EstadisticaFutbol (goles, asistencias) de 3 jugadores, una AmonestacionFutbol, asistencia y evolucion ranking y EquipoCompetencia.puntos=3. (2) El administrador genera el reporte de esa competencia. (3) Se verifica que el reporte muestre los resultados, la tabla de puntos por equipo, los goleadores ordenados.

#### RF-A10 Generar Reportes De Ventas

El sistema debe permitir al administrador generar reportes de ventas discriminados por rubro (tienda, cafetería y mensualidades) y por periodo (diario, semanal y mensual).

**Programa de prueba:** Programa de prueba: (0) Al inicio no hay ventas. (1) Se registran cuatro movimientos:

- El 2026-10-05, un Socio compra 2 “Balón Fútbol” (precio=80000) en la tienda: subtotal 160000, IVA 30400, total 190400;
- El 2026-10-05, un Entrenador compra 1 “Raqueta” (precio=200000) con su código de Tienda.codigosDescuento (15%): descuento 30000, subtotal 170000, IVA 32300, total 202300;
- El 2026-10-07, un Socio compra un café en la cafetería (precio=5000): impuesto al consumo 400, propina 500, total 5900;
- El 2026-10-20 se paga una mensualidad de 150000.

(2) El administrador genera el reporte de ventas. (3) Se verifica que aparezca cada venta con fecha, comprador, subtotal, impuesto, descuento aplicado y total. (4) Se verifica el total por rubro (tienda 392700, cafetería 5900, mensualidades 150000) y el total general (548600). (5) Se verifica cada periodo: el reporte diario del 2026-10-05 muestra solo las 2 ventas de tienda, el semanal del 5 al 11 de octubre muestra 3 ventas y el mensual de octubre muestra las 4.

#### RF-A11 Aceptar sugerencias de las tiendas

El sistema debe permitir al administrador aceptar recomendaciones de nuevos artículos o snacks realizadas por los empleados.

**Programa de prueba:** (1) Tienda.sugerencias contiene el ArticuloDeportivo “Tobillera”, y Cafeteria.sugerencias contiene la Bebida “Té frío” (esCaliente=false). (2) El administrador acepta las dos. (3) Se verifica que cada artículo pase al inventario de su tienda con disponibilidad=0 y salga de sugerencias. (4) Se intenta aceptar la sugerencia de un artículo que ya existe en el inventario, y el sistema lo rechaza.

#### RF-A12 Denegar sugerencias de las tiendas

El sistema debe permitir al administrador denegar recomendaciones de nuevos artículos o snacks realizadas por los empleados.

**Programa de prueba:** (1) Tienda.sugerencias contiene "Tobillera". (2) El administrador la deniega. (3) Sale de sugerencias y no se agrega a Tienda.inventario.

#### RF-A13 Cambiar horario de instalaciones

El sistema debe permitir al administrador cambiar el horario de las instalaciones en una fecha específica

**Programa de prueba:** (1) Una Instalacion de tipo="padel" tiene en su disponibilidad un Horario con fecha=2026-10-17 y h6 a h21 en true (de 06:00 a 22:00). Ese día hay una Reserva con hora=[20] de un PartidoPracticaRaqueta. (2) El administrador cambia el horario de esa fecha a 07:00–20:00, de modo que solo h7 a h19 quedan en true. (3) Se verifica que el Horario del 2026-10-17 se actualice y que un socio no pueda reservar ese día a las 21:00. (4) Se verifica que la reserva de las 20:00 se cancele (sale de ClubSeneca.reservas) y que la cancelación se propague: el PartidoPracticaRaqueta asociado también se cancela. Lo mismo ocurre con una SesionEntrenamiento cuya reserva quede fuera del nuevo horario.

#### RF-A14 Eliminar instalación

El sistema debe permitir al administrador eliminar instalaciones

**Programa de prueba:** (1) Existe una Instalacion de tipo="mesa" sin reservas futuras y otra de tipo="tenis" con una Reserva futura de un PartidoPracticaRaqueta. (2) El administrador elimina la mesa, y se verifica que salga de ClubSeneca.inventarioInstalaciones. (3) El administrador intenta eliminar la cancha de tenis, y el sistema avisa o rechaza la eliminación porque tiene reservas futuras asociadas (de partidos o de SesionEntrenamiento).

#### RF-A15 Agregar instalación

El sistema debe permitir al administrador agregar instalaciones

**Programa de prueba:** (1) El administrador agrega una Instalacion de tipo="padel" con admite=2. (2) Se verifica que aparezca en ClubSeneca.inventarioInstalaciones y que se pueda reservar para 4 jugadores. (3) El administrador agrega una Instalacion de tipo="tenis" con admite=2, y se verifica que acepte las dos modalidades (sencillos con 2 jugadores y dobles con 4). (4) Se intenta agregar una instalación con admite <= 0, y el sistema la rechaza.

#### RF-A16 Consultar historial

El sistema debe permitir al administrador consultar el historial completo de reservas, partidos y sesiones de entrenamiento.

**Programa de prueba:** (1) ClubSeneca tiene:

- en reservas: 2 Reserva, una del 2026-09-20 a las 10:00 en una Instalacion de tipo="tenis", reservadoPor el Socio “Ana”, y otra del 2026-09-22 a las 16:00 en una Instalacion de tipo="grupal"
- en historialPartidos: un PartidoInterno (2026-09-21, Fútbol, Sub-17, “Fútbol Sub-17 A” 3 - 1 “Fútbol Sub-17 B”), un PartidoCompetencia (2026-09-27, institucionRival="Universidad Nacional", escenario="Estadio Alfonso López", 2 - 0) y un PartidoPracticaRaqueta ligado a la primera reserva
- en sesionesEntrenamiento: una SesionEntrenamientoEquipo (fecha=2026-09-22, horaInicio=16, duracion=90) ligada a la segunda reserva, con 3 AsistenciaJugador registradas.

(2) El Administrador inicia sesión y consulta el historial. (3) Se verifica que aparezcan las 2 reservas con fecha, hora, tipo de instalación y quién reservó; los 3 partidos con fecha, deporte y categoría, más los datos propios de cada tipo (equipos y marcador en el interno; rival, escenario y marcador en el de competencia; jugadores en el de práctica); y la sesión con su entrenador, instalación, duración y la asistencia de cada jugador. Todo debe salir ordenado por fecha. (4) El administrador filtra solo partidos entre el 2026-09-25 y el 2026-09-30, y se verifica que aparezca únicamente Partidos entre esas fechas. Con un rango sin registros, el sistema muestra un historial vacío sin error.

#### RF-A17 Gestionar partidos internos

El sistema debe permitir al administrador programar partidos internos entre equipos, registrar el marcador y las estadísticas individuales de los jugadores participantes, registrar las tarjetas correspondientes y calcular las suspensiones cuando aplique.

**Programa de prueba:** (1) Existen los Equipo “Fútbol Sub-17 A” y “Fútbol Sub-17 B” (deporte="Fútbol", categoria="Sub-17"), cada uno con 8 JugadorConjunto, además de un equipo “Baloncesto Sub-17” y otro “Fútbol Sub-15”. (2) El administrador programa un PartidoInterno (fecha=2026-10-15 15:00) con equipo1="Fútbol Sub-17 A" y equipo2="Fútbol Sub-17 B", y 7 jugadoresHabilitados por equipo. Se verifica que quede en ClubSeneca.historialPartidos con deporte="Fútbol" y categoria="Sub-17". (3) Registra el marcador puntuacionEquipo1=2, puntuacionEquipo2=1, y una EstadisticaFutbol por cada participante: por ejemplo, Pedro (camiseta 9, equipo A) con goles=2 y asistencias=0, y Luis (equipo B) con goles=1. Se verifica que el partido tenga 14 Estadisticas en estadisticasJugadores y que la estadisticaAcumulada de Pedro aumente en 2 goles. (4) Registra una AmonestacionLocal con amarillas=2 para Carlos (B), con roja=1 para Mario (B) y con amarillas=1 para Andrés (B). Se verifica que Carlos y Mario queden suspendidos y Andrés no. (5) Se programa el siguiente partido del equipo B. El sistema rechaza a Carlos y a Mario como jugadoresHabilitados y acepta a Andrés. Una vez registrado ese partido, Carlos y Mario quedan habilitados para el siguiente. (6) Se intenta lo siguiente, y el sistema lo rechaza en todos los casos:

- Programar un partido entre “Fútbol Sub-17 A” y “Baloncesto Sub-17” (deporte distinto) o “Fútbol Sub-15” (categoría distinta);
- Programar un partido de fútbol en el que un equipo tiene solo 6 jugadores habilitados.
- Registrar un marcador negativo.

#### RF-A18 Gestionar competencias

El sistema debe permitir al administrador inscribir equipos en competencias verificando los requisitos de inscripción, registrar partidos oficiales con su rival y escenario, y gestionar la información correspondiente a las fases, posiciones y estadísticas de la competencia, incluyendo goleadores y jugadores destacados.

**Programa de prueba:** (1) Existen dos competencias en ClubSeneca.competencias: una CompetenciaASCUN “ASCUN Fútbol 2026” (entidad="ASCUN", temporada="2026-2", deporte="Fútbol", categoria="Mayores", formato="Grupos y eliminación directa", nominaMinima=12, maximoAmarillas=2); un TorneoCerros “Torneo de los Cerros 2026” de baloncesto, categoria="Mayores", formato="Liga". Además existe el Equipo “Fútbol Mayores”, con 15 JugadorConjunto. (2) El administrador inscribe “Fútbol Mayores” en la competencia ASCUN con una nómina de 14 jugadores. Se verifica que se cree un EquipoPropio con equipo="Fútbol Mayores", inscritos=14 jugadores y puntos=0, y que aparezca en equiposParticipantes. (3) Arma la FaseGrupos con un Grupo “A” que contiene el EquipoPropio y 3 EquipoAfuera (“Universidad Nacional”, “Javeriana” y “Rosario”). (4) Registra un PartidoCompetencia: fecha=2026-10-20, institucionRival="Universidad Nacional", escenario="Estadio Alfonso López", 11 jugadoresHabilitados de la nómina, puntuacionEquipoPropio=3 y puntuacionEquipoRival=1, con una EstadisticaFutbol de goles=2 para Pedro y de goles=1 y asistencias=1 para Luis, y una AmonestacionComp con amarillas=1 para Carlos. Se verifica que: el partido quede en competencia.partidos y en ClubSeneca.historialPartidos; el EquipoPropio tenga puntos=3 (victoria en fútbol); las estadísticas se acumulen en estadisticasAcumuladasJugadores. (5) Registra un segundo partido contra “Javeriana” que termina 1-1, con un gol de Pedro y otra amarilla para Carlos. Se verifica que el EquipoPropio tenga puntos=4 y que Carlos llegue a 2 amarillas (igual a maximoAmarillas), con lo cual queda suspendido para el siguiente partido oficial. (6) Se consulta la tabla de posiciones del Grupo “A” y se verifica que esté ordenada por puntos. Los dos primeros pasan a FaseEliminacionDirecta.equiposRestantes. (7) Se consultan las clasificaciones: el goleador es Pedro con 3 goles, seguido de Luis con 1, y el jugador más destacado se calcula a partir de las estadísticas acumuladas de la competencia. (8) Se intenta lo siguiente, y el sistema lo rechaza en todos los casos:

- Inscribir a “Fútbol Sub-17” en la competencia ASCUN (categoría no permitida)
- Inscribir una nómina de 10 jugadores
- Alinear en un partido oficial a un jugador que no está en inscritos
- Alinear a Carlos en el partido siguiente a su suspensión

### Entrenador

#### RF-E01 Crear Sesiones De Entrenamiento

El sistema debe permitir al entrenador crear sesiones de entrenamiento.

**Programa de prueba:** (1) Un Entrenador cuyo turno del 2026-10-12 tiene h16 y h17 en true inicia sesión. (2) Crea una SesionEntrenamientoEquipo con fecha=2026-10-12, horaInicio=16, duracion=90 y equipoConvocado="Fútbol Sub-17 A", en una Instalacion de tipo="grupal". (3) Se verifica que la sesión quede en ClubSeneca.sesionesEntrenamiento con el entrenador como responsable y con una Reserva (fecha=2026-10-12, hora=[16,17]) sobre esa instalación. (4) Se intenta crear otra sesión en la misma instalación y hora, o fuera del turno del entrenador, y el sistema la rechaza.

#### RF-E02 Registrar asistencia

El sistema debe permitir al entrenador registrar la asistencia de cada jugador.

**Programa de prueba:** (1) Existe una SesionEntrenamientoEquipo del entrenador cuyo equipoConvocado tiene 3 jugadores. (2) El entrenador registra asistio="asistió" para Pedro, asistio="llegó tarde" para Luis y asistio="faltó" para Carlos. (3) Se verifica que cada AsistenciaJugador guarde ese valor con su sesion y su jugador, y que aparezca en las asistencias del jugador. (4) Se intenta registrar la asistencia de un jugador que no fue convocado, o en una sesión de otro entrenador, y el sistema lo rechaza.

#### RF-E03 Registrar Evaluación De Desempeño

El sistema debe permitir al entrenador registrar una evaluación de desempeño.

**Programa de prueba:** (1) Pedro tiene asistio="asistió" en una SesionEntrenamientoEquipo del entrenador. (2) El entrenador registra evaluacion=4 y comentario="Buena definición" en la AsistenciaJugador de Pedro. (3) Se verifica que la AsistenciaJugador guarde los dos valores. (4) Se intenta evaluar a un jugador con asistio="faltó", o registrar una evaluación mayor o menor que 1 a 5, y el sistema lo rechaza.

#### RF-E04 Compartir Codigo De Descuento A Un Socio

El sistema debe permitir al entrenador compartir un código de descuento a un socio.

**Programa de prueba:** 1) El Entrenador tiene codigoDescuentoTienda="ENT-0042", que está en Tienda.codigosDescuento. (2) Lo comparte con el Socio “Ana”, y se verifica que Ana quede en las solicitudesCodigo del entrenador con codigoDescuentoDado="ENT-0042". (3) Ana compra una “Raqueta Tenis” con el código, y se verifica que se aplique el 8% de descuento.

#### RF-E05 Comprar en la tienda y cafetería

El sistema debe permitir al entrenador comprar en la tienda y cafetería del club

**Programa de prueba:** (1) El entrenador inicia sesión y elige 1 "Balón Básquet" (precio=90000, disponibilidad=4). (2) Confirma la compra con su codigoDescuentoTienda. (3) Se verifica que disponibilidad=3, que se aplique el descuento y el IVA, y que la venta quede registrada. (4) Se intenta comprar 5 unidades de un artículo con disponibilidad=3, y el sistema lo rechaza.

### Fisioterapeuta

#### RF-F01 Comprar en la tienda y cafetería

El sistema debe permitir al fisioterapeuta comprar en la tienda y cafetería del club

**Programa de prueba:** (1) El Fisioterapeuta inicia sesión y elige 2 "Cuerda de fuerza" (precio=25000, disponibilidad=10). (2) Confirma la compra. (3) Se verifica que disponibilidad=8 y que la venta quede registrada con IVA. (4) Se intenta comprar un artículo con disponibilidad=0, y el sistema lo rechaza.

#### RF-F02 Compartir Codigo De Descuento A Un Socio

El sistema debe permitir al fisioterapeuta compartir un código de descuento a un socio

**Programa de prueba:** (1) El Fisioterapeuta tiene codigoDescuentoTienda="FIS-0007", que está en Tienda.codigosDescuento. (2) Lo comparte con el Socio “Luis”, y se verifica que Luis quede en sus solicitudesCodigo con codigoDescuentoDado="FIS-0007". (3) Luis compra una “Tobillera” (precio=40000) con el código, y se verifica que se aplique el 8% de descuento. (4) Se intenta usar un código que no está en Tienda.codigosDescuento, y el sistema lo rechaza.

### Socio

#### RF-S01 Reservar instalaciones

El sistema debe permitir al socio reservar una instalación deportiva para un bloque horario, indicando la cantidad de jugadores y la modalidad cuando corresponda. El sistema debe verificar que exista disponibilidad y que la cantidad de jugadores corresponda a la capacidad de la instalación. Si no hay cupo disponible, debe rechazar la reserva.

**Programa de prueba:** (1) El Socio “Ana”, que tiene un JugadorIndividual de tenis, reserva una Instalacion de tipo="tenis" (admite=2) el sábado 2026-10-17 de 10:00 a 11:00, en modalidad dobles y con 4 jugadores. (2) Se verifica que se cree una Reserva (fecha=2026-10-17, hora=[10]) con esa instalación y que quede en ClubSeneca.reservas. También se verifica que se cree un PartidoPracticaRaqueta con reservadoPor=el jugador de Ana, con esa reserva y con 2 JugadorIndividual en equipo1 y 2 en equipo2. (3) Se verifica que, si la instalación no tenía un Horario para esa fecha, se cree uno nuevo con fecha=2026-10-17 en Instalacion.disponibilidad. (4) Otro socio intenta reservar la misma cancha en el mismo bloque, y el sistema lo rechaza. (5) Se intenta reservar con 3 jugadores, una cantidad que no corresponde a la instalación, y el sistema lo rechaza. (6) El Administrador intenta reservar, y el sistema lo rechaza.

#### RF-S02 Programar partidos de práctica

El sistema debe permitir al socio programar partidos de práctica con otros jugadores de niveles iguales o adyacentes. El sistema debe rechazar la programación cuando los niveles no sean compatibles, excepto cuando uno de los jugadores también sea entrenador.

**Programa de prueba:** (1) El socio programa un PartidoPracticaRaqueta entre su JugadorIndividual (nivel="Intermedio") y un rival de nivel “Avanzado” (niveles adyacentes), y el sistema lo acepta. (2) Programa un partido entre su jugador “Avanzado” y un rival “Principiante” (niveles no adyacentes), y el sistema lo rechaza. (3) Repite el caso 2 con un rival cuyo Jugador tiene esEntrenador=true, y el sistema lo acepta por la excepción. (4) Se verifica que el partido aceptado tenga una Reserva con instalación asignada y que no se cruce con otra reserva.

#### RF-S03 Comprar artículos deportivos

El sistema debe permitir al socio comprar artículos deportivos disponibles en la tienda. Al realizar una compra, el sistema debe verificar la cantidad disponible, registrar la venta, aplicar el IVA correspondiente y actualizar el inventario.

**Programa de prueba:** (1) El Socio compra 2 “Pelotas Tenis” con precio de 20000 cada una y disponibilidad inicial de 6 unidades. (2) Se verifica que el total sea 47600 (40000 más IVA del 19%), que la disponibilidad quede en 4 unidades y que la VentaTienda quede en Tienda.ventas con el socio como comprador. (3) Se verifica que los puntos de fidelidad generados sean 800, es decir, el 2% del valor de la compra antes del IVA (40000). (4) Se intenta comprar 7 unidades del mismo artículo, cuando solo hay 4 disponibles, y el sistema rechaza la compra sin modificar el inventario.

#### RF-S04 Redimir puntos de fidelidad

El sistema debe permitir al socio utilizar sus puntos de fidelidad como descuento en compras futuras. El sistema debe actualizar la cantidad de puntos después de utilizarlos.

**Programa de prueba:** (1) El Socio tiene inicialmente puntosFidelidad=500. (2) Compra un artículo con valor de 30000 y decide redimir 300 puntos como descuento. (3) Se verifica que la venta quede con puntosRedimidos=300 y que se aplique antes del IVA: el valor queda en 29700, el IVA es 5643 y el total es 35343. (4) Se verifica que la compra genere 594 puntos nuevos (el 2% de 29700). (5) Se verifica que el saldo final sea 794 puntos (500 − 300 + 594).

#### RF-S05 Registrar estadísticas y actualizar ranking de partidos de raqueta

El sistema debe registrar las estadísticas de los partidos de práctica de tenis, pádel y tenis de mesa y actualizar el puntaje de ranking tipo ELO de cada jugador y disciplina después de cada partido.

**Programa de prueba:** (1) Existe un PartidoPracticaRaqueta de tenis en modalidad sencillos entre “Ana” (JugadorIndividual, disciplina="tenis", elo=1000) y “Sofía” (disciplina="tenis", elo=1000). Ana también tiene un JugadorIndividual de pádel con elo=1100. (2) Al terminar el partido se registra una EstadisticaTenisPadel para Ana (setsGanados=2, gamesGanados=12, aces=3, erroresNoForzados=5) y otra para Sofía (setsGanados=0, gamesGanados=7, aces=1, erroresNoForzados=9). (3) Se verifica que las 2 estadísticas queden en estadisticasJugadores del partido y se sumen a la estadisticaAcumulada de cada jugadora. (4) Se verifica que el elo de tenis se actualice con la fórmula simplificada (K=32, resultado esperado 0.5): Ana queda con 1016 y Sofía con 984. El elo de pádel de Ana sigue en 1100. (5) En un partido de tenis de mesa se registra una EstadisticaTenisMesa (setsGanados, puntosTotales) para cada jugador, y se verifica que el elo de esa disciplina también se actualice.

#### RF-S06 Comprar en la cafetería

El sistema debe permitir al socio comprar bebidas y snacks en la cafetería, informar los posibles alérgenos de los snacks antes de la compra, aplicar el impuesto al consumo del 8 % y permitir modificar la propina sugerida del 10 %. Además, el sistema debe impedir el despacho de bebidas calientes cuando el socio se encuentre dentro de una cancha de pádel o una mesa de tenis de mesa techada.

**Programa de prueba:** (1) Cafeteria.inventario tiene el Snack “Barra de granola” (precio=6000, disponibilidad=10, alergenos=[“maní”, “gluten”]), la Bebida “Café” (precio=5000, esCaliente=true) y la Bebida “Limonada” (precio=4000, esCaliente=false). (2) El socio elige la barra de granola, y se verifica que el sistema le muestre “maní, gluten” antes de confirmar. (3) El socio compra la barra y un café. Se verifica que el sistema calcule un subtotal de 11000, un impuesto al consumo de 880, una propina sugerida de 1100 y un total de 12980. (4) El socio cambia la propina a 500, y se verifica:

- Que el total quede en 12380
- Que la VentaCafeteria quede en Cafeteria.ventas con propina=500, iva=880 y El socio como comprador
- Que la disponibilidad de cada artículo baje en 1
- Que el café quede en las bebidasSinConsumir del socio
- Que el socio gane 220 puntos de fidelidad (el 2% de 11000)

(5) Un socio con una Reserva en curso en una Instalacion de tipo="padel" pide un café, y el sistema lo rechaza. Si pide una limonada, el sistema la acepta. (6) El socio que tiene el café en bebidasSinConsumir intenta usar una cancha de pádel o una mesa de tenis de mesa techada, y el sistema lo rechaza. (7) Se intenta registrar una propina negativa, y el sistema lo rechaza

### Todos los usuarios

#### RF-T

El sistema debe permitir a todos los usuarios iniciar sesión mediante un login y una contraseña.

**Programa de prueba:** (1) Se crean un Administrador, un Entrenador, un Fisioterapeuta y un Socio, cada uno con su login y password. (2) Cada uno inicia sesión con sus credenciales correctas, y se verifica que el sistema lo identifique con su tipo y le muestre solo sus opciones (por ejemplo, el socio no ve "Crear Equipos"). (3) Se intenta entrar con una contraseña incorrecta o un login que no existe, y el sistema lo rechaza.

---

## Restricciones del proyecto

1. **Información persistente:**
   Toda la información registrada en la aplicación debe ser persistente y mantenerse así la aplicación cierre o se reinicie de esta manera no se perderá información

2. **Almacenamiento en archivos JSON:**
   La información suministrada será guardada en archivos JSON, los cuales son fáciles de manipular y muy útiles con respecto a el almacenamiento de información. Se almacenará todo en una carpeta.

3. **Manipulación de la carpeta de archivos JSON:**
   Únicamente la aplicación podrá escribir y leer la carpeta de archivos JSON

4. **Estructura:**
   La carpeta de archivos JSON no puede ser la misma carpeta donde se encuentre el código fuente de la aplicación.

5. **Autenticación:**
   Todos los usuarios del sistema deberán ingresar con un usuario y contraseña para verificar su identidad y su rol.

6. **Lenguaje:**
   Toda la aplicación será desarrollada en el lenguaje de programación JAVA.

## Restricciones de las Historias de Usuario (Reglas de dominio)

7. **Equipos por deporte:**
   Un jugador solo puede pertenecer a un equipo por cada deporte, aunque puede estar inscrito simultáneamente en más de un deporte si cumple con la edad correspondiente a la categoría.

8. **Capacidad de instalaciones:**
   Las reservas de instalaciones deben respetar la capacidad definida para cada instalación y modalidad. Además, cada disciplina tiene un número máximo de instalaciones que pueden utilizarse simultáneamente.

9. **Niveles para partidos de práctica:**
   Los partidos de práctica solo pueden realizarse entre jugadores de niveles iguales o adyacentes. La excepción es cuando uno de los jugadores también es entrenador.

10. **Mínimo de jugadores:**
    Para realizar un partido interno, el equipo debe contar con el número mínimo de jugadores habilitados que corresponda al deporte. Por ejemplo: 7 en fútbol, 5 en baloncesto y 6 en voleibol.

11. **Cambio de Turnos:**
    Para poder realizar un cambio de turnos deben de haber 2 entrenadores y 1 fisioterapeuta.

12. **Suspensiones:**
    En fútbol, un jugador que acumule dos tarjetas amarillas o reciba una tarjeta roja queda suspendido para el siguiente partido de su equipo.

13. **Requisitos de competencias:**
    Solo los jugadores registrados en la nómina de una competencia pueden disputar sus partidos oficiales. Además, el equipo debe cumplir los requisitos de inscripción de la competencia.

14. **IVA:**
    Las compras realizadas en la tienda deben pagar un IVA del 19 %.

15. **Puntos de fidelidad:**
    Cada venta genera puntos equivalentes al 2 % de su valor y un punto equivale a un peso. Los puntos pueden utilizarse como descuento en compras futuras.

16. **Bebidas calientes:**
    No se puede despachar una bebida caliente a un socio que se encuentre dentro de una cancha de pádel o una mesa de tenis de mesa techada. Tampoco se permite utilizar esas instalaciones llevando una bebida caliente.

17. **Crear sesiones:**
    El entrenador para poder crear una sesión de entrenamiento necesita una fecha/hora/duración/instalacion/jugadores

18. **Registrar asistencia:**
    El entrenador para poder registrar la asistencia debe poner si llego tarde, si asistió o falto.

19. **Alérgenos:**
    Los snacks de la cafetería deben tener registrados sus posibles alérgenos y estos deben informar al socio antes de realizar la compra.

20. **Registrar Desempeño:**
    El entrenador debe registrar el desempeño de un jugador con un valor entre [1,5]

21. **Mover Objetos:**
    El administrador puede mover objetos entre ubicaciones si el objeto existe.
