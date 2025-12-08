package monopoly;
import monopoly.carta.*;
import monopoly.casilla.*;
import monopoly.consola.*;
import monopoly.edificios.*;
import monopoly.jugador.*;
import monopoly.partida.*;
import java.util.ArrayList;
import monopoly.exceptions.*;

public class Juego implements Comando {

    // Atributo estático requerido por el PDF para input/output
    public static final Consola consola = new ConsolaNormal();
    private final java.util.Map<String, Trato> tratosRecibidos = new java.util.HashMap<>();

    private ArrayList<Jugador> jugadores;
    private ArrayList<Avatar> avatares;
    private int turno;
    private int lanzamientos;
    private Tablero tablero;
    private Dado dado1;
    private Dado dado2;
    private boolean tirado;
    private int doblesConsecutivos = 0;
    private final Jugador banca;
    private final java.util.EnumMap<Edificio.Tipo, Integer> secTipo = new java.util.EnumMap<>(Edificio.Tipo.class);
    private final java.util.Map<Long, Integer> numeroPorEdificio = new java.util.HashMap<>();
    private Baraja baraja;

    public Juego() {
        this.banca = new Jugador();
        this.tablero = new Tablero(banca);
        this.jugadores = new ArrayList<>();
    }

    public Jugador getBanca() {
        return banca;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public int getJugadoresNum() {
        return jugadores.size();
    }

    private long secEdificio = 0;

    private long nextEdificioId() {
        return ++secEdificio;
    }

    private int nextNumTipo(Edificio.Tipo t) {
        int n = secTipo.getOrDefault(t, 0) + 1;
        secTipo.put(t, n);
        return n;
    }
    public void recibirTrato(Trato t){
        tratosRecibidos.put(t.getId(), t);
    }


    public Trato getTrato(String idTrato){
        return tratosRecibidos.get(idTrato);
    }
    public java.util.Collection<Trato> getListaTratos(){
        return tratosRecibidos.values();
    }
    public void verTablero() {
        if (tablero == null) {
            Juego.consola.imprimir("No hay tablero cargado.");
            return;
        }
        Juego.consola.imprimir(tablero.toString());
    }

    public void iniciarPartida() {
        jugadores = new ArrayList<>();
        avatares = new ArrayList<>();
        turno = 0;
        lanzamientos = 0;
        tirado = false;
        dado1 = new Dado();
        dado2 = new Dado();
        tablero = new Tablero(this.banca);
        this.baraja = new Baraja(); // <-- Reiniciar baraja al iniciar nueva monopoly.partida
        this.baraja.barajar();
    }

    public void mostrarJugadorActual() throws MonopolyEtseException{
        if (jugadores == null || jugadores.isEmpty()) {
            throw new AccionInvalidaException("No hay jugadores. Crea uno con: crear jugador <Nombre> <tipoAvatar>");
        }
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);
        Juego.consola.imprimir("Tiene el turno: " + actual.getNombre());
    }

    public void crearJugador(String nombre, String tipoAvatar) throws MonopolyEtseException {
        Casilla salida;
        salida = tablero.encontrar_casilla("Salida");
        if (salida == null) {
            throw new AccionInvalidaException("Error crítico: No se encontró la casilla 'Salida' en el tablero.");
        }

        Jugador j;
        j = new Jugador(nombre, tipoAvatar, salida, avatares);
        if (j.getNombre() == null) {
            throw new AccionInvalidaException("Error creando jugador");
        }

        jugadores.add(j);
        Juego.consola.imprimir("Creado jugador '" + nombre + "' con avatar '" + tipoAvatar + "' en Salida.");
        try {
            Juego.consola.imprimir(tablero.toString());
        } catch (Throwable ignored) {
        }
    }

    public void listarJugadores() throws MonopolyEtseException {
        if (!hayJugadores()) {
            return;
        }
        for (Jugador j : jugadores) {
           try{ descJugador(j.getNombre());
           } catch(AccionInvalidaException e){
               throw new AccionInvalidaException("No hay jugadores");
           }
        }
    }

    public void descJugador(String nombreBuscado) throws MonopolyEtseException{
        if (nombreBuscado == null || nombreBuscado.isEmpty()) {
            throw new AccionInvalidaException("Uso: describir jugador <Nombre>");
        }
        if (!hayJugadores()) {
            return;
        }

        Jugador j = null;
        for (Jugador x : jugadores) {
            if (x.getNombre().equals(nombreBuscado)) {
                j = x;
                break;
            }
        }
        if (j == null) {
            throw new AccionInvalidaException("Este jugador no existe");

        }

        String nombre = j.getNombre();
        String avatar = "-";
        Avatar a = j.getAvatar();
        if (a != null) {
            String tipo = a.getTipo();
            char id = a.getID();
            avatar = tipo + " (" + id + ")";
        }
        String fortuna = String.format("%d", j.getFortuna());
        String posicion = "-";
        Casilla pos = null;
        if (a != null) pos = a.getPosicion();
        if (pos != null) posicion = pos.getNombre();

        String propiedades = "-";
        java.util.List<Casilla> auxProps = j.getPropiedades();
        if (auxProps != null && !auxProps.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int added = 0;
            for (Casilla c : auxProps) {
                // CORRECCIÓN: Check propiedad
                if (c instanceof Propiedad && ((Propiedad) c).gethipotecada() == 0) {
                    if (added++ > 0) sb.append(", ");
                    sb.append(c.getNombre());
                }
            }
            if (added > 0) propiedades = sb.toString();
        }

        String hipotecadas = "-";
        java.util.List<Casilla> auxHip = j.getPropiedades();
        if (auxHip != null && !auxHip.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int added = 0;
            for (Casilla c : auxHip) {
                // CORRECCIÓN: Check propiedad
                if (c instanceof Propiedad && ((Propiedad) c).gethipotecada() == 1) {
                    if (added++ > 0) sb.append(", ");
                    sb.append(c.getNombre());
                }
            }
            if (added > 0) hipotecadas = sb.toString();
        }

        String edificiosLista = "-";
        java.util.List<Edificio> eds = j.getMisEdificios();
        if (eds != null && !eds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < eds.size(); i++) {
                Edificio e = eds.get(i);
                int num = numeroPorEdificio.getOrDefault(e.getId(), 0);
                String tipo = e.getTipo().name().toLowerCase();
                sb.append(tipo).append(" ").append(num);
                if (i < eds.size() - 1) sb.append(", ");
            }
            edificiosLista = sb.toString();
        }

        String enCarcel = j.isEnCarcel() ? "Sí" : "No";

        Juego.consola.imprimir("Jugador: " + nombre);
        Juego.consola.imprimir("  Avatar: " + avatar);
        Juego.consola.imprimir("  Fortuna: " + fortuna);
        Juego.consola.imprimir("  Posición: " + posicion);
        Juego.consola.imprimir("  Propiedades: " + propiedades);
        Juego.consola.imprimir("  Hipotecas: " + hipotecadas);
        Juego.consola.imprimir("  Edificios:" + edificiosLista);
        Juego.consola.imprimir("  En cárcel: " + enCarcel);
        Juego.consola.imprimir("");
    }

    public void descAvatar(String ID) throws MonopolyEtseException {
        if (ID == null || ID.isEmpty()) {
            throw new AccionInvalidaException("Debes especificar un ID de avatar");

        }
        if (avatares == null || avatares.isEmpty()) {
            throw new AccionInvalidaException("No hay avatares creados");

        }

        char idBuscado = ID.charAt(0);
        Avatar encontrado = null;
        for (Avatar a : avatares) {
            if (a.getID() == idBuscado) {
                encontrado = a;
                break;
            }
        }
        if (encontrado == null) {
            throw new AccionInvalidaException ("No existe el avatar con ese ID");


        }

        String tipo = encontrado.getTipo();
        String pos = "-";
        Casilla c = encontrado.getPosicion();
        if (c != null) pos = c.getNombre();

        String jugadorNombre = "-";
        Jugador j = encontrado.getJugador();
        if (j != null) jugadorNombre = j.getNombre();

        Juego.consola.imprimir("Avatar: " + idBuscado);
        Juego.consola.imprimir("  Tipo: " + tipo);
        Juego.consola.imprimir("  Jugador: " + jugadorNombre);
        Juego.consola.imprimir("  Posición: " + pos);
        Juego.consola.imprimir("");
    }

    public void descCasilla(String nombre) throws MonopolyEtseException{
        if (nombre == null || nombre.isEmpty()){
            throw new AccionInvalidaException("Debes especificar un nombre de casilla");

        }
        Casilla c = null;
        c = tablero.encontrar_casilla(nombre);
        if (c == null) {
            throw new AccionInvalidaException ("No existe la casilla con ese nombre");

        }
        // Casilla.toString() ya hace lo correcto en las hijas
        Juego.consola.imprimir(c.toString());
    }

    public void declararBancarrota(Jugador deudor) {
        Juego.consola.imprimir("\n!!! BANCARROTA !!!");
        Juego.consola.imprimir("El jugador " + deudor.getNombre() + " no puede hacer frente a sus pagos obligatorios.");
        Juego.consola.imprimir("Todas sus propiedades pasan a la Banca (o al acreedor, simplificado a Banca/Eliminación).");
        Juego.consola.imprimir("El jugador es eliminado del juego.");

        // Lógica simple de eliminación
        deudor.getAvatar().setPosicion(null); // Quitar del tablero
        jugadores.remove(deudor);

        // Devolver propiedades a la banca para que se puedan comprar de nuevo
        for (Casilla prop : deudor.getPropiedades()) {
            if (prop instanceof Propiedad) {
                ((Propiedad)prop).setDueno(banca);
                // Opcional: eliminar edificios, resetear hipotecas...
            }
        }

        // Ajustar turno si es necesario para no saltar al siguiente erróneamente
        if (turno >= jugadores.size()) turno = 0;
    }

    public void lanzarDados() throws MonopolyEtseException{
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");

        }
        if (lanzamientos == 1) {
            throw new AccionInvalidaException("Ya has tirado una vez, no puedes volver a tirar.");

        }
        Jugador actual = jugadores.get(turno);

        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int suma = d1 + d2;
        if (d1 != d2) lanzamientos = 1;

        if (actual.isEnCarcel()) {
            if ((actual.getTiradasCarcel() == 3 && d1 != d2 && lanzamientos != 1) || (d1 == d2 && lanzamientos != 1)) {
                Juego.consola.imprimir("Sales de la carcel");
                salirCarcel();
                actual.setTiradasCarcel(0);
            }
            if (d1 != d2 && actual.getTiradasCarcel() < 3 && lanzamientos != 1) {
                actual.setTiradasCarcel(actual.getTiradasCarcel() + 1);
                Juego.consola.imprimir("Has tirado " + actual.getTiradasCarcel() + " veces en la carcel.");
                return;
            }
        }
        boolean esDoble = (d1 == d2);

        Juego.consola.imprimir("Dados: " + d1 + " + " + d2 + " = " + suma + (esDoble ? " (dobles)" : ""));

        if (esDoble) {
            doblesConsecutivos++;
            if (doblesConsecutivos >= 3) {
                Juego.consola.imprimir("¡Tres dobles seguidos! " + actual.getNombre() + " va a la cárcel.");
                lanzamientos = 1;
                enviarACarcel(actual);
                doblesConsecutivos = 0;
                tirado = true;
                return;
            }
        } else {
            doblesConsecutivos = 0;
            tirado = true;
        }

            Avatar a = actual.getAvatar();
            a.moverAvatar(tablero.getPosiciones(), suma);
            Casilla c = a.getPosicion();


            if (c != null && c instanceof IrCarcel) {
               actual.encarcelar();
            }
            if (c != null) {
               try{ c.evaluarCasilla(actual, this, suma);
               } catch (SaldoInsuficienteException e){
                   declararBancarrota(actual);
                   throw new SaldoInsuficienteException(actual.getNombre(), suma-actual.getFortuna(), 1);
               }
            }

        Juego.consola.imprimir(tablero.toString());

        if (esDoble) {
            Juego.consola.imprimir(actual.getNombre() + " ha sacado dobles y puede volver a lanzar.");
            tirado = false;
        }
    }

    public void lanzarDadosForzado(int d1, int d2) throws MonopolyEtseException {
        // ... (misma lógica de cast en movimiento, resumida)
        if (!hayJugadores()) {
            return;
        }
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        // ... lógica de cárcel igual ...
        int suma = d1 + d2;
        boolean esDoble = (d1 == d2);

        if (esDoble) {
            doblesConsecutivos++;
            if (doblesConsecutivos >= 3) {
                enviarACarcel(actual);
                doblesConsecutivos = 0;
                tirado = true;
                return;
            }
        } else {
            doblesConsecutivos = 0;
            tirado = true;
        }


            Avatar a = actual.getAvatar();
            a.moverAvatar(tablero.getPosiciones(), suma);
            Casilla c = a.getPosicion();
            if (c != null) c.evaluarCasilla(actual, this, suma);
            else {
                throw new AccionInvalidaException("No hay casilla para mover el avatar");

            }


        try {
            Juego.consola.imprimir(tablero.toString());
        } catch (Throwable ignore) {
        }
        if (esDoble) tirado = false;
    }

    public void comprar(String nombre) throws MonopolyEtseException{
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");

        }
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        Casilla cas = tablero.encontrar_casilla(nombre);
        if (cas == null) {
           throw new AccionInvalidaException ("No existe la casilla con ese nombre");

        }

        // CORRECCIÓN: Check Propiedad
        if (!(cas instanceof Propiedad)) {
            throw new AccionInvalidaException("Solo se puede comprar propiedades.");
        }
        Propiedad prop = (Propiedad) cas;

        Casilla pos = actual.getAvatar().getPosicion();
        if (pos == null || pos != cas) {
            throw new AccionInvalidaException("No puedes comprar propiedades fuera de tu casilla actual.");
        }

        Jugador propietario = prop.getDueno();
        if (propietario != null && propietario != this.getBanca()) {
            throw new AccionInvalidaException("Esta propiedad no esta en venta."+((Propiedad) cas).getDueno());
        }

        int precio = prop.getValor();
        double saldo = actual.getFortuna();
        if (saldo < precio) {
            long faltante = (long) (precio - saldo);
            throw new SaldoInsuficienteException(actual.getNombre(),faltante,0);
        }
        prop.comprar(actual); // Llama al comprar de Propiedad

        if (prop.getDueno() == actual) {
            actual.getEstadisticas().sumarDineroInvertido((long) precio);
        }
        Juego.consola.imprimir(actual.getNombre() + " compra '" + prop.getNombre() + "' por " + precio + ".");
        Juego.consola.imprimir("La fortuna actual de " + actual.getNombre() + " es: " + actual.getFortuna() + ".");
        Juego.consola.imprimir(tablero.toString());
    }

    public void salirCarcel() throws MonopolyEtseException{
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");

        }
        Jugador actual = jugadores.get(turno);
        if (!actual.isEnCarcel()) {
            throw new AccionInvalidaException(actual.getNombre() + " no está en la cárcel.");
        }
        if (actual.getFortuna() < Valor.PRECIO_SALIR_CARCEL) {
            declararBancarrota(actual);
            throw new SaldoInsuficienteException(actual.getNombre(), Valor.PRECIO_SALIR_CARCEL - actual.getFortuna(),1);
        }
        boolean ok = actual.sumarGastos(Valor.PRECIO_SALIR_CARCEL);
        if (ok) actual.getEstadisticas().sumarPagoTasasImpuestos(Valor.PRECIO_SALIR_CARCEL);
        actual.salirCarcel();
        Juego.consola.imprimir(actual.getNombre() + " paga y sale de la cárcel.");
        try {
            Juego.consola.imprimir(tablero.toString());
        } catch (Throwable ignored) {
        }
    }

    public void listarVenta() throws MonopolyEtseException {
        ArrayList<Casilla> enVenta = new ArrayList<>();
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                // CORRECCIÓN: Check Propiedad
                if (c instanceof Propiedad) {
                    Propiedad p = (Propiedad) c;
                    if (p.getDueno() == this.getBanca()) {
                        enVenta.add(c);
                    }
                }
            }
        }
        for (Casilla c : enVenta) {
            this.descCasilla(c.getNombre());
            Juego.consola.imprimir("},\n{");
        }
    }

    public void listarAvatares() throws MonopolyEtseException {
        if (avatares == null || avatares.isEmpty()) return;
        for (Avatar a : avatares) {
            char id = '\0';
            String tipo = "-";
            String pos = "-";
            String jugadorNombre = "-";

            id= a.getID();
            if (id == '\0') {
                throw new AccionInvalidaException("Avatar sin ID");
            }
            tipo = a.getTipo();
            if (tipo == null) {
                throw new AccionInvalidaException("Avatar sin tipo");
            }
            Casilla c = a.getPosicion();
            if (c != null) pos = c.getNombre();
            else {
                throw new AccionInvalidaException("Avatar sin casilla");
            }

            try {
                Jugador j = a.getJugador();
                if (j != null) jugadorNombre = j.getNombre();
            } catch (Throwable ignored) {
                for (Jugador j : jugadores) {
                    try {
                        if (j.getAvatar() == a) {
                            jugadorNombre = j.getNombre();
                            break;
                        }
                    } catch (Throwable ignored2) {
                        throw new AccionInvalidaException("Avatar sin jugador");
                    }
                }
            }

            Juego.consola.imprimir("{ id: " + a.getID() + ", tipo: " + a.getTipo() + " ... }");
        }
    }

    public void acabarTurno() {
        if (!hayJugadores()) {
            return;
        }
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        lanzamientos = 0;
        turno = (turno + 1) % jugadores.size();
        doblesConsecutivos = 0;
        tirado = false;
        Jugador actual = jugadores.get(turno);
        Juego.consola.imprimir("Nuevo turno para: " + actual.getNombre());
    }

    public void enviarACarcel(Jugador j) throws MonopolyEtseException{
        if (j == null || tablero == null) return;
        Casilla carcel = tablero.encontrar_casilla("Cárcel");
        j.encarcelar();
        Avatar a = j.getAvatar();
        if (a != null) a.setPosicion(carcel);
        else {
            throw new AccionInvalidaException("N");
        }
    }

    // --- EDIFICACIÓN (Requiere casteo a Solar) ---

    public void edificarCasa() throws MonopolyEtseException {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);
        Casilla pos = actual.getAvatar().getPosicion();

        // CORRECCIÓN: Check Solar
        if (!(pos instanceof Solar)) {
            throw new EdificacionNoPermitidaException("Solo se puede edificar propiedades.");
        }


        Solar s = (Solar) pos;
        // VALIDACIÓN: No puedes construir casas si ya hay edificios superiores
        if (s.getNumHoteles() > 0 || s.getNumPiscinas() > 0 || s.getNumPistas() > 0) {
            throw new EdificacionNoPermitidaException("No puedes edificar casas porque ya has mejorado esta propiedad (Hotel/Piscina/Pista).");
        }
        // VALIDACIÓN: Límite de 4 casas
        if (s.getNumCasas() >= 4) {
            throw new EdificacionNoPermitidaException("Límite de casas alcanzado (4). Debes evolucionar a Hotel.");
        }

        // CORRECCIÓN: Grupo es Propiedad, pero Solar lo tiene.
        if (s.getGrupo() == null || !s.getGrupo().esDuenoGrupo(actual)) {
            Juego.consola.imprimir("No se puede edificar: no tienes el grupo completo.\n");
            return;
        }

        s.edificar("casa"); // Delegamos en Solar (nuevo método)
        // NOTA: Tu Solar.edificar ya hace la lógica. Si quieres mantener la lógica AQUÍ,
        // deberías usar los setters de Solar (setNumCasas, etc).
        // Para respetar "mover código", asumo que la lógica compleja se movió a Solar.java
        // Si no, copia-pega tu bloque original aquí casteando 'pos' a 'Solar'.
    }

    public void edificarHotel() throws MonopolyEtseException {
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");
        }
        Jugador actual = jugadores.get(turno);
        Casilla pos = actual.getAvatar().getPosicion();
        if (!(pos instanceof Solar)){
            throw new EdificacionNoPermitidaException("Solo se puede edificar propiedades.");
        }
        Solar s = (Solar) pos;
        // VALIDACIÓN: No puedes construir hotel si ya lo tienes o tienes mejoras superiores
        if (s.getNumHoteles() > 0 || s.getNumPiscinas() > 0 || s.getNumPistas() > 0) {
            throw new EdificacionNoPermitidaException("Ya tienes un hotel (o una mejora superior) en esta propiedad.");
        }
        // VALIDACIÓN: Requisito previo (4 casas)
        if (s.getNumCasas() != 4) {
            throw new EdificacionNoPermitidaException("Necesitas tener exactamente 4 casas para poder edificar un hotel.");
        }
        ((Solar) pos).edificar("hotel");
    }

    public void edificarPiscina() throws MonopolyEtseException{
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");
        }
        Jugador actual = jugadores.get(turno);
        Casilla pos = actual.getAvatar().getPosicion();

        if (!(pos instanceof Solar)) {
            throw new EdificacionNoPermitidaException("Solo se puede edificar propiedades.");
        }
        // VALIDACIÓN: Requisito previo (Tener Hotel)
        Solar s = (Solar) pos;
        if (s.getNumHoteles() == 0) {
            throw new EdificacionNoPermitidaException("No puedes edificar una piscina sin tener previamente un hotel.");
        }
        // VALIDACIÓN: No puedes construir piscina si ya tienes una o algo superior
        if (s.getNumPiscinas() > 0 || s.getNumPistas() > 0) {
            throw new EdificacionNoPermitidaException("Ya tienes una piscina (o una mejora superior) en esta propiedad.");
        }
        ((Solar) pos).edificar("piscina");
    }

    public void edificarPista() throws MonopolyEtseException {
        if (!hayJugadores()){
            throw new AccionInvalidaException("No hay jugadores");
        }
        Jugador actual = jugadores.get(turno);
        Casilla pos = actual.getAvatar().getPosicion();
        if (!(pos instanceof Solar)){
            throw new EdificacionNoPermitidaException("Solo se puede edificar propiedades.");
        }
        Solar s = (Solar) pos;
        // VALIDACIÓN: Requisitos previos (Hotel y Piscina)
        if (s.getNumHoteles() == 0 || s.getNumPiscinas() == 0) {
            throw new EdificacionNoPermitidaException("Para edificar una pista necesitas tener un hotel y una piscina.");
        }
        // VALIDACIÓN: Límite (Máximo 1 pista)
        if (s.getNumPistas() > 0) {
            throw new EdificacionNoPermitidaException("Ya hay una pista de deporte construida en esta propiedad.");
        }
        ((Solar) pos).edificar("pista");
    }

    // --- LISTAR EDIFICIOS ---
    // Método para listar edificios (todos o por color)
    public void listarEdificios(String color) throws MonopolyEtseException {
        // --- CASO 1: LISTAR TODOS LOS EDIFICIOS (Sin color) ---
        if (color == null) {
            java.util.List<Edificio> todos = new java.util.ArrayList<>();
            if (jugadores != null) {
                for (Jugador j : jugadores) {
                    if (j != null && j.getMisEdificios() != null) todos.addAll(j.getMisEdificios());
                }
            }

            if (todos.isEmpty()) {
                Juego.consola.imprimir("{}");
                return;
            }

            for (int i = 0; i < todos.size(); i++) {
                Edificio e = todos.get(i);
                String tipo = e.getTipo().name().toLowerCase();
                int numTipo = numeroPorEdificio.getOrDefault(e.getId(), 0);
                String idStr = tipo + "-" + numTipo;

                String propietario = (e.getPropietario() != null) ? e.getPropietario().getNombre() : "-";
                // En Solar es donde está el nombre, aunque Propiedad también lo tiene
                String casilla = (e.getSolar() != null) ? e.getSolar().getNombre() : "-";
                String grupoStr = "-";

                // Obtenemos el grupo desde el Solar
                if (e.getSolar() != null && e.getSolar().getGrupo() != null) {
                    grupoStr = e.getSolar().getGrupo().getColorGrupo();
                    if (grupoStr == null) {
                        throw new AccionInvalidaException("Grupo sin color");
                    }

                }

                // Usamos el método casteado para el coste
                long coste = costeConstruccion(e.getSolar(), e.getTipo());

                Juego.consola.imprimir("{");
                Juego.consola.imprimir("id: " + idStr + ",");
                Juego.consola.imprimir("propietario: " + propietario + ",");
                Juego.consola.imprimir("casilla: " + casilla + ",");
                Juego.consola.imprimir("grupo: " + grupoStr + ",");
                Juego.consola.imprimir("coste: " + coste);
                Juego.consola.imprimir(i < todos.size() - 1 ? "}," : "}");
            }
            return;
        }

        // --- CASO 2: LISTAR POR GRUPO DE COLOR ---
        Grupo grupo = tablero.getGrupos().get(color);
        if (grupo == null) {
            String colorCapitalizado = Character.toUpperCase(color.charAt(0)) + color.substring(1).toLowerCase();
            grupo = tablero.getGrupos().get(colorCapitalizado);
            if (grupo == null) {
                throw new AccionInvalidaException("Grupo de color '" + color + "' no encontrado.");
            }
        }

        // Banderas de construcción
        boolean puedeCasa = true;
        boolean puedeHotel = true;
        boolean puedePiscina = true;
        boolean puedePista = true;

        int maxCasas = 0;
        int maxHoteles = 0;
        int maxPiscinas = 0;
        int maxPistas = 0;

        // Iteramos sobre los miembros, filtrando solo los Solares
        for (Casilla c : grupo.getMiembros()) {
            if (c == null) continue;

            // IMPORTANTE: Solo procesamos si es Solar (Propiedad edificable)
            if (c instanceof Solar) {
                Solar s = (Solar) c; // Casteo a Solar para acceder a sus métodos

                Juego.consola.imprimir("{");
                Juego.consola.imprimir("propiedad: " + s.getNombre() + ",");

                // Listar edificios por tipo (delegando al método auxiliar)
                Juego.consola.imprimirSinSalto("  casas: ");
                listarEdificiosPorTipo(s, Edificio.Tipo.CASA);

                Juego.consola.imprimirSinSalto("  hoteles: ");
                listarEdificiosPorTipo(s, Edificio.Tipo.HOTEL);

                Juego.consola.imprimirSinSalto("  piscinas: ");
                listarEdificiosPorTipo(s, Edificio.Tipo.PISCINA);

                Juego.consola.imprimirSinSalto("  pistasDeDeporte: ");
                listarEdificiosPorTipo(s, Edificio.Tipo.PISTA);

                // Calcular alquiler actual manualmente para mostrarlo
                // Usamos getAlquilerBase() porque getAlquiler() ya no existe en Casilla
                long alquilerActual = s.getAlquilerBase();

                if (s.getNumCasas() > 0) alquilerActual = (long) s.getNumCasas() * s.getAlquilerCasa();
                if (s.getNumHoteles() > 0) alquilerActual += (long) s.getNumHoteles() * s.getAlquilerHotel();
                if (s.getNumPiscinas() > 0) alquilerActual += (long) s.getNumPiscinas() * s.getAlquilerPiscina();
                if (s.getNumPistas() > 0) alquilerActual += (long) s.getNumPistas() * s.getAlquilerPistaDeporte();

                Juego.consola.imprimir("  alquiler: " + alquilerActual);

                // Contadores para verificar uniformidad en el grupo
                if (s.getNumCasas() == 4) maxCasas++;
                if (s.getNumHoteles() == 1) maxHoteles++;
                if (s.getNumPiscinas() == 1) maxPiscinas++;
                if (s.getNumPistas() == 1) maxPistas++;
            }
        }

        // Lógica de validación de construcción (idéntica a tu original)
        int numPropiedadesGrupo = grupo.getMiembros().size();

        if (maxCasas == numPropiedadesGrupo) puedeCasa = false;
        if (maxHoteles == numPropiedadesGrupo) {
            puedeHotel = false;
            if (maxCasas == 0) puedeCasa = false;
        }
        if (maxPiscinas == numPropiedadesGrupo) puedePiscina = false;
        if (maxPistas == numPropiedadesGrupo) puedePista = false;

        // Imprimir resultados de qué se puede construir
        if (!puedeCasa || !puedeHotel || !puedePiscina || !puedePista) {
            Juego.consola.imprimir("Ya no puedes construír:\n");
        }
        if (!puedeCasa)
            Juego.consola.imprimir("   -casas: no se pueden construir casas (límite alcanzado o hay hoteles).\n");
        if (!puedeHotel) Juego.consola.imprimir("   -hoteles: no se pueden construir hoteles.\n");
        if (!puedePiscina) Juego.consola.imprimir("   -piscinas: no se pueden construir piscinas.\n");
        if (!puedePista) Juego.consola.imprimir("   -pistas de deporte: no se pueden construir pistas.\n");

        if (puedeCasa || puedeHotel || puedePiscina || puedePista) {
            Juego.consola.imprimir("Aun puedes construír:\n");
        }
        if (puedeCasa) Juego.consola.imprimir("   -casas\n");
        if (puedeHotel) Juego.consola.imprimir("   -hoteles\n");
        if (puedePiscina) Juego.consola.imprimir("   -piscinas\n");
        if (puedePista) Juego.consola.imprimir("   -pistas de deporte\n");

        Juego.consola.imprimir("}");
    }

    // Método auxiliar corregido con casteo
    private void listarEdificiosPorTipo(Casilla c, Edificio.Tipo tipo) throws MonopolyEtseException {
        if (!(c instanceof Solar)) {
            throw new AccionInvalidaException("Solo se puede listar por grupo de color.");
        }

        Solar s = (Solar) c; // Casteo
        java.util.List<String> nombresEdificios = new java.util.ArrayList<>();

        // s.getEdificios() ahora es accesible porque 's' es Solar
        for (Edificio e : s.getEdificios()) {
            if (e.getTipo() == tipo) {
                int numTipo = numeroPorEdificio.getOrDefault(e.getId(), 0);
                nombresEdificios.add(tipo.name().toLowerCase() + "-" + numTipo);
            }
        }

        if (nombresEdificios.isEmpty()) {
            Juego.consola.imprimir("-");
        } else {
            Juego.consola.imprimir("[" + String.join(", ", nombresEdificios) + "]");
        }
    }

    public long costeConstruccion(Casilla c, Edificio.Tipo t) throws MonopolyEtseException {
        if (!(c instanceof Solar)){
            throw new AccionInvalidaException("Solo se puede listar por grupo de color y solar.");
        };
        Solar s = (Solar) c;
        return switch (t) {
            case CASA -> s.getPrecioCasa();
            case HOTEL -> s.getPrecioHotel();
            case PISCINA -> s.getPrecioPiscina();
            case PISTA -> s.getPrecioPistaDeporte();
        };
    }

    // --- HIPOTECAS ---
    public void hipotecar(String nombreProp) throws MonopolyEtseException{
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(nombreProp);
        if (!(c instanceof Propiedad)) {
            throw new HipotecaNoPermitidaException("Solo se puede hipotecar propiedades.");
        }
        Propiedad p = (Propiedad) c;

        if (!(p.getDueno().equals(actual))) return;

        if (c instanceof Solar) {
            ((Solar) c).hipotecar(); // Delegamos en Solar (que chequea edificios)
        } else {
            // Lógica genérica
            if (p.gethipotecada() == 1) return;
            p.sethipotecada(1);
            actual.sumarFortuna(p.getHipoteca());
            Juego.consola.imprimir("Hipotecada.");
        }
    }

    public void deshipotecar(String nombreSolar) throws MonopolyEtseException{
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(nombreSolar);

        // 1. Verificar si existe
        if (c == null) {
            throw new AccionInvalidaException("La casilla " + nombreSolar + " no existe.");
        }

        // 2. Verificar si es una Propiedad (Solar, Transporte, Servicio)
        if (!(c instanceof Propiedad)) {
            throw new HipotecaNoPermitidaException("La casilla " + c.getNombre() + " no es una propiedad y no se puede deshipotecar.");
        }

        Propiedad p = (Propiedad) c; // Casteo seguro

        // 3. Verificar dueño
        if (!p.perteneceAJugador(actual)) {
            throw new HipotecaNoPermitidaException(actual.getNombre() + " no puede deshipotecar " + p.getNombre() + ". No es su propiedad.");
        }

        // 4. Verificar si está hipotecada
        if (p.gethipotecada() == 0) {
            throw new HipotecaNoPermitidaException(actual.getNombre() + " no puede deshipotecar " + p.getNombre() + ". No está hipotecada.");
        }

        // 5. Calcular coste (Hipoteca + 10% de interés)
        int coste = (int) (p.getHipoteca() * 1.10);

        // 6. Verificar solvencia y ejecutar
        if (actual.getFortuna() < coste) {
            Juego.consola.imprimir("No tienes dinero suficiente para deshipotecar " + p.getNombre() + ". Coste: " + coste + "€");
            throw new SaldoInsuficienteException(actual.getNombre(), coste- actual.getFortuna(),0);
        }

        // Pagar y cambiar estado
        actual.sumarGastos(coste);
        p.sethipotecada(0); // Marcar como deshipotecada

        // Mensaje de éxito
        String color = (p.getGrupo() != null ? p.getGrupo().getColorGrupo() : "-");
        Juego.consola.imprimir(actual.getNombre() + " paga " + coste + "€ por deshipotecar " + p.getNombre() +
                ". Ahora puede recibir alquileres y edificar en el grupo " + color + ".");
    }

    // --- VENDER ---
    public void venderPropiedad(String tipo, String solarName, int cantidad) throws MonopolyEtseException{
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(solarName);

        // 1. Validaciones básicas
        if (c == null) {
            throw new AccionInvalidaException("La casilla " + solarName + " no existe.");

        }

        // 2. Validación de tipo: Solo los Solares se pueden vender por partes (edificios)
        if (!(c instanceof Solar)) {
            throw new AccionInvalidaException("La casilla " + solarName + " no es un Solar, no se puede vender " + tipo + ".");

        }
        Solar s = (Solar) c; // Casteo a Solar para acceder a sus métodos

        // 3. Validación de dueño
        if (!s.getDueno().equals(actual)) {
            throw new AccionInvalidaException("No se pueden vender " + tipo + " en " + s.getNombre() + ". Esta propiedad no pertenece a " + actual.getNombre() + ".");

        }

        String t = tipo.toLowerCase();

        // 4. Lógica de venta (Copiada de tu original, usando 's' en vez de 'c')
        switch (t) {
            case "casas":
                if (s.getNumHoteles() > 0 || s.getNumPiscinas() > 0 || s.getNumPistas() > 0) {
                    throw new AccionInvalidaException("No puedes vender casas mientras tengas hoteles o mejoras superiores.");
                }
                int vendidas = Math.min(cantidad, s.getNumCasas());
                if (s.getNumCasas() == 0) {
                    throw new AccionInvalidaException("No hay casas en " + s.getNombre() + ".");

                }
                if (s.getNumCasas() < cantidad) {
                    Juego.consola.imprimir("No hay suficientes casas en esta propiedad. Se venderán " + s.getNumCasas() + " casas. Recibiendo " + s.getNumCasas() * s.getPrecioCasa());
                } else if (s.getNumCasas() == cantidad) { // Corrección menor: else if para evitar doble mensaje si son iguales
                    Juego.consola.imprimir("Se venden todas las casas de esta propiedad. Recibiendo " + cantidad * s.getPrecioCasa());
                }

                actual.sumarFortuna(vendidas * s.getPrecioCasa());

                // Nota: Mantenemos tu lógica original, aunque restas 'cantidad' en vez de 'vendidas'
                // Si cantidad > vendidas, esto podría dar negativo, pero respetamos tu código.
                s.setNumCasas(s.getNumCasas() - cantidad);

                eliminarEdificiosDe(s, actual, Edificio.Tipo.CASA, vendidas);
                break;

            case "pista":
                if (s.getNumPistas() == 0) {
                   throw new AccionInvalidaException("No hay pistas en " + s.getNombre() + ".");

                }
                if (cantidad > 1 && s.getNumPistas() == 1) {
                    Juego.consola.imprimir("Solamente se puede vender 1 pista, recibiendo " + s.getPrecioPistaDeporte());
                }
                if (s.getNumPistas() == 1) {
                    Juego.consola.imprimir("Vendiendo 1 pista, recibiendo " + s.getPrecioPistaDeporte());
                }

                actual.sumarFortuna(s.getPrecioPistaDeporte());
                s.setNumPistas(s.getNumPistas() - cantidad);
                eliminarEdificiosDe(s, actual, Edificio.Tipo.PISTA, 1);
                break;

            case "piscina":
                if (s.getNumPistas() > 0) {
                    throw new AccionInvalidaException("No puedes vender la piscina mientras tengas una pista de deporte.");
                }
                if (s.getNumPiscinas() == 0) {
                    throw new AccionInvalidaException("No hay piscinas en " + s.getNombre() + ".");

                }
                if (cantidad > 1 && s.getNumPiscinas() == 1) {
                    Juego.consola.imprimir("Solamente se puede vender 1 piscina, recibiendo " + s.getPrecioPiscina());
                }
                if (s.getNumPiscinas() == 1) {
                    Juego.consola.imprimir("Vendiendo 1 piscina, recibiendo " + s.getPrecioPiscina());
                }

                actual.sumarFortuna(s.getPrecioPiscina());
                s.setNumPiscinas(s.getNumPiscinas() - cantidad);
                eliminarEdificiosDe(s, actual, Edificio.Tipo.PISCINA, 1);
                break;

            case "hoteles":
                if (s.getNumPiscinas() > 0 || s.getNumPistas() > 0) {
                    throw new AccionInvalidaException("No puedes vender hoteles mientras tengas piscinas o pistas.");
                }
                if (s.getNumHoteles() == 0) {
                   throw new AccionInvalidaException("No hay hoteles en " + s.getNombre() + ".");

                }
                if (cantidad > 1 && s.getNumHoteles() == 1) {
                    Juego.consola.imprimir("Solamente se puede vender 1 hotel, recibiendo " + s.getPrecioHotel());
                    // s.setNumHoteles(s.getNumHoteles() - 1); // Estaba duplicado en tu código
                }
                if (s.getNumHoteles() == 1) {
                    Juego.consola.imprimir("Vendiendo 1 hotel, recibiendo " + s.getPrecioHotel());
                    // s.setNumHoteles(s.getNumHoteles() - 1);
                }
                // En tu original restabas dos veces arriba, aquí unificamos la resta final:
                s.setNumHoteles(s.getNumHoteles() - 1);

                actual.sumarFortuna(s.getPrecioHotel());
                eliminarEdificiosDe(s, actual, Edificio.Tipo.HOTEL, 1);
                break;

            default:
                throw new AccionInvalidaException("Tipo de edificio no válido: " + tipo);
        }
    }

    @Override
    public void estadisticasJugador(String nombreJugador) {
        if(!hayJugadores()){return;}
        Jugador jugador=null;
        for(Jugador j:jugadores){
            if(j.getNombre().equals(nombreJugador)){
                jugador=j;
                break;
            }
        }
        if(jugador==null){
            System.out.println("No existe el jugador: "+nombreJugador);
            return;
        }

        //imprimimos por pantalla todas las banderas que pusimos por pantalla
        EstadisticasJugador estadisticas=jugador.getEstadisticas();
        System.out.println("estadísticas " + nombreJugador);
        System.out.println("{");
        System.out.println("    dineroInvertido: " + estadisticas.getDineroInvertido() + ",");
        System.out.println("    pagoTasasImpuestos: " + estadisticas.getPagoTasasImpuestos() + ",");
        System.out.println("    pagoDeAlquileres: " + estadisticas.getPagoDeAlquileres() + ",");
        System.out.println("    cobroDeAlquileres: " + estadisticas.getCobroDeAlquileres() + ",");
        System.out.println("    pasarPorCasillaDeSalida: " + estadisticas.getPasarPorCasillaDeSalida() + ",");
        System.out.println("    premiosInversionesOBote: " + estadisticas.getPremiosInversionesOBote() + ",");
        System.out.println("    vecesEnLaCarcel: " + estadisticas.getVecesEnLaCarcel());
        System.out.println("}");
    }

    // Método auxiliar actualizado para aceptar 'Solar' en vez de 'Casilla'
    private void eliminarEdificiosDe(Solar s, Jugador propietario, Edificio.Tipo tipo, int n) {
        if (n <= 0) return;

        int quitados = 0;
        // Ahora s.getEdificios() funciona porque 's' es Solar
        java.util.List<Edificio> copiaEdificios = new java.util.ArrayList<>(s.getEdificios());

        for (Edificio e : copiaEdificios) {
            if (quitados >= n) break;

            if (e.getTipo() == tipo && e.getPropietario() == propietario) {
                // e.eliminar() llama a s.eliminarEdificio(e) y actualiza contadores
                s.eliminarEdificio(e);


                // Eliminamos el ID del mapa global
                numeroPorEdificio.remove(e.getId());

                quitados++;
            }
        }
    }

    // Antes llamaba a Carta.sacarCarta (estático)
    // Ahora llama a this.baraja.sacarCarta (instancia)
    public void procesarCasillaEspecial(Jugador jugador, String tipoCasilla) throws MonopolyEtseException {
        Carta c = this.baraja.sacarCarta(tipoCasilla); // Usamos nuestra instancia
        if (c != null) {
            c.accion(jugador, this);
        } else {
            throw new AccionInvalidaException("No hay cartas de " + tipoCasilla + " disponibles.");
        }
    }

    public void moverJugadorACasilla(Jugador jugador, String nombreCasilla, boolean cobrarSalida) throws MonopolyEtseException {
        Casilla destino = tablero.encontrar_casilla(nombreCasilla);
        if (destino != null) {
            if (cobrarSalida && pasaPorSalida(jugador.getAvatar().getPosicion(), destino)) {
                jugador.sumarFortuna(2000000);
                jugador.getEstadisticas().sumarPasarPorSalida();
            }
            jugador.getAvatar().setPosicion(destino);
            try {
                destino.evaluarCasilla(jugador, this, 0);
            } catch (MonopolyEtseException e) {
                Juego.consola.imprimir("Error al evaluar la casilla destino: " + e.getMessage());
                throw e;
            }
        }
    }

    // funcion analoga a la anterior pero mueve hacia atras
    public void moverJugadorAPosicion(Jugador jugador, int posicion) throws MonopolyEtseException {
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if (c.getPosicion() == posicion) {
                    jugador.getAvatar().setPosicion(c);
                    try {
                        c.evaluarCasilla(jugador, this, 0);
                    } catch (MonopolyEtseException e) {
                        Juego.consola.imprimir("Error al evaluar la casilla tras mover: " + e.getMessage());
                        throw e;
                    }
                    return;
                }
            }
        }
    }

    private boolean pasaPorSalida(Casilla actual, Casilla destino) {
        return destino.getPosicion() < actual.getPosicion();
    }

    private boolean hayJugadores() {
        return jugadores != null && !jugadores.isEmpty();
    }

    public void estadisticasJuego() {
        if (!hayJugadores()) {
            return;
        }

        // Variables para almacenar los récords
        Casilla masRentable = null;
        Casilla masFrecuentada = null;
        Grupo masRentableGrupo = null;
        Jugador masVueltas = null;
        Jugador enCabeza = null;

        long maxRentabilidadCasilla = -1;
        int maxFrecuencia = -1;
        long maxRentabilidadGrupo = -1;
        int maxVueltas = -1;
        long maxFortunaTotal = -1;

        // 1. Recorrer todas las casillas del tablero
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if (c == null) continue;

                // A. Casilla más rentable
                // En la nueva estructura, solo los Solares rastrean dinámicamente el dinero generado
                // (Si quieres que Transporte/Servicio cuenten, deberías añadir getDineroGenerado a Propiedad)
                if (c instanceof Solar) {
                    Solar s = (Solar) c;
                    if (s.getDineroGenerado() > maxRentabilidadCasilla) {
                        maxRentabilidadCasilla = s.getDineroGenerado();
                        masRentable = s;
                    }
                }

                // B. Casilla más frecuentada
                // Este método está en la clase base Casilla, así que funciona para todas
                if (c.frecuenciaVisita() > maxFrecuencia) {
                    maxFrecuencia = c.frecuenciaVisita();
                    masFrecuentada = c;
                }
            }
        }

        // 2. Recorrer todos los grupos
        java.util.HashMap<String, Grupo> grupos = tablero.getGrupos();
        if (grupos != null) {
            for (Grupo g : grupos.values()) {
                // C. Grupo más rentable
                if (g.getRentabilidad() > maxRentabilidadGrupo) {
                    maxRentabilidadGrupo = g.getRentabilidad();
                    masRentableGrupo = g;
                }
            }
        }

        // 3. Recorrer todos los jugadores
        for (Jugador j : jugadores) {
            if (j == null || j.getNombre().equals("Banca")) continue;

            // D. Jugador con más vueltas
            if (j.getVueltas() > maxVueltas) {
                maxVueltas = j.getVueltas();
                masVueltas = j;
            }

            // E. Jugador en cabeza
            long fortunaTotal = valorTotalJugador(j);
            if (fortunaTotal > maxFortunaTotal) {
                maxFortunaTotal = fortunaTotal;
                enCabeza = j;
            }
        }

        // Imprimir resultados
        Juego.consola.imprimir("estadisticas");
        Juego.consola.imprimir("{");
        Juego.consola.imprimir("  casillaMasRentable: " + (masRentable != null ? masRentable.getNombre() : "-") + ",");
        Juego.consola.imprimir("  grupoMasRentable: " + (masRentableGrupo != null ? masRentableGrupo.getColorGrupo() : "-") + ",");
        Juego.consola.imprimir("  casillaMasFrecuentada: " + (masFrecuentada != null ? masFrecuentada.getNombre() : "-") + ",");
        Juego.consola.imprimir("  jugadorMasVueltas: " + (masVueltas != null ? masVueltas.getNombre() : "-") + ",");
        Juego.consola.imprimir("  jugadorEnCabeza: " + (enCabeza != null ? enCabeza.getNombre() : "-"));
        Juego.consola.imprimir("}");
    }

    private long valorTotalJugador(Jugador j) {
        long total = j.getFortuna();

        for (Casilla c : j.getPropiedades()) {
            // Sumar el valor base de la propiedad
            if (c instanceof Propiedad) {
                total += ((Propiedad) c).getValor();
            }

            // Sumar el valor de los edificios (solo si es Solar)
            if (c instanceof Solar) {
                Solar s = (Solar) c;
                total += (long) s.getNumCasas() * s.getPrecioCasa();
                total += (long) s.getNumHoteles() * s.getPrecioHotel();
                total += (long) s.getNumPiscinas() * s.getPrecioPiscina();
                total += (long) s.getNumPistas() * s.getPrecioPistaDeporte();
            }
        }
        return total;
    }
    private Jugador buscarJugador(String nombre){
        if(nombre==null) return null;
        for(Jugador j:jugadores){
            if(j.getNombre().equalsIgnoreCase(nombre)) return j;
        }
        return null;
    }

    // Método auxiliar para interpretar el texto de los tratos (ej: "Solar1 y 5000")
    // Devuelve un array con [Propiedad, Dinero (int)]
    private Object[] parsearElementosTrato(String texto) {
        Propiedad prop = null;
        int dinero = 0;

        // Separamos por " y " (con espacios)
        String[] items = texto.split(" y ");

        for (String item : items) {
            item = item.trim();
            if (item.isEmpty() || item.equalsIgnoreCase("nada")) continue;

            // Intentamos ver si es número (dinero)
            try {
                // El dinero se debe convertir de forma segura
                dinero = Integer.parseInt(item);
            } catch (NumberFormatException e) {
                // Si no es número, buscamos la casilla
                Casilla c = tablero.encontrar_casilla(item);
                if (c instanceof Propiedad) {
                    prop = (Propiedad) c;
                }
            }
        }
        return new Object[]{prop, dinero};
    }
    /*
    @Override
    // --- REQUISITO 32: PROPONER TRATO ---
    public void proponerTrato(String comando) throws MonopolyEtseException{
        try {
            if (!hayJugadores()) {
                throw new AccionInvalidaException("No hay jugadores en el juego.");
            };
            Jugador proponente = jugadores.get(turno);

            // --- 1. PARSEO BÁSICO DEL COMANDO ---
            int posDosPuntos = comando.indexOf(':');
            if (posDosPuntos == -1) throw new AccionInvalidaException("Formato incorrecto. Falta ':'.");

            String nombreDestino = comando.substring(6, posDosPuntos).trim();
            String resto = comando.substring(posDosPuntos + 1).trim();

            if (!resto.startsWith("cambiar (") || !resto.endsWith(")")) {
                throw new AccionInvalidaException("Formato incorrecto. Debe ser: cambiar (lo_que_das, lo_que_pides)");
            }

            // Quitamos "cambiar (" y ")" y separamos por la coma
            String contenido = resto.substring(9, resto.length() - 1);
            String[] lados = contenido.split(",");

            if (lados.length != 2) throw new AccionInvalidaException("Debes separar oferta y demanda con una coma.");

            // --- 2. JUGADORES ---
            Jugador propuesto = buscarJugador(nombreDestino);
            if (propuesto == null) throw new AccionInvalidaException("El jugador " + nombreDestino + " no existe.");
            if (propuesto == proponente) throw new AccionInvalidaException("No puedes proponerte tratos a ti mismo.");

            // --- 3. USAMOS LOS MÉTODOS SIMPLES ---
            // Lado 0: Lo que OFRECE el proponente
            Propiedad propOfrece = sacarPropiedad(lados[0]);
            int dineroOfrece = sacarDinero(lados[0]);

            // Lado 1: Lo que PIDE (demanda)
            Propiedad propPide = sacarPropiedad(lados[1]);
            int dineroPide = sacarDinero(lados[1]);

            // --- 4. VALIDACIONES DE REGLAS ---
            if (propOfrece != null && !propOfrece.perteneceAJugador(proponente)) {
                throw new AccionInvalidaException("No puedes ofrecer " + propOfrece.getNombre() + " porque no es tuya.");
            }
            if (dineroOfrece > 0 && proponente.getFortuna() < dineroOfrece) {
                throw new SaldoInsuficienteException(proponente.getNombre(), dineroOfrece - proponente.getFortuna());
            }
            if (propPide != null && !propPide.perteneceAJugador(propuesto)) {
                throw new AccionInvalidaException("No puedes pedir " + propPide.getNombre() + " porque no pertenece a " + propuesto.getNombre());
            }

            // --- 5. CREAR Y GUARDAR ---
            Trato nuevoTrato = new Trato(proponente, propuesto, propOfrece, dineroOfrece, propPide, dineroPide);

            propuesto.recibirTrato(nuevoTrato);
            proponente.recibirTrato(nuevoTrato);

            Juego.consola.imprimir("Trato propuesto con éxito: " + nuevoTrato.getId());

        } catch (MonopolyEtseException e) {
            Juego.consola.imprimir("Error: " + e.getMessage());
        } catch (Exception e) {
            Juego.consola.imprimir("Error de formato. Ejemplo: trato Ana: cambiar (Solar1, 5000)");
        }
    }

     */
    // --- REEMPLAZAR EN JUEGO.JAVA ---

    @Override
    public void proponerTrato(String comando) throws MonopolyEtseException {
        if (!hayJugadores()) throw new AccionInvalidaException("No hay jugadores.");
        Jugador proponente = jugadores.get(turno);

        // 1. Parseo inicial
        int posDosPuntos = comando.indexOf(':');
        if (posDosPuntos == -1) throw new AccionInvalidaException("Formato incorrecto. Uso: trato Jugador: cambiar (A, B)");

        String nombreDestino = comando.substring(6, posDosPuntos).trim();
        String resto = comando.substring(posDosPuntos + 1).trim();

        if (!resto.startsWith("cambiar (") || !resto.endsWith(")")) {
            throw new AccionInvalidaException("Formato incorrecto. Debe ser: cambiar (lo_que_das, lo_que_pides)");
        }

        String contenido = resto.substring(9, resto.length() - 1);
        String[] lados = contenido.split(",");
        if (lados.length != 2) throw new AccionInvalidaException("Debes separar oferta y demanda con una coma.");

        Jugador propuesto = buscarJugador(nombreDestino);
        if (propuesto == null) throw new AccionInvalidaException("El jugador " + nombreDestino + " no existe.");
        if (propuesto == proponente) throw new AccionInvalidaException("No puedes hacer tratos contigo mismo.");

        // 2. Extracción y VALIDACIÓN ESTRICTA de elementos
        // Usamos un método helper que lanza excepción si algo no cuadra
        Object[] oferta = analizarParteTrato(lados[0].trim());
        Propiedad propOfrece = (Propiedad) oferta[0];
        int dineroOfrece = (int) oferta[1];

        Object[] demanda = analizarParteTrato(lados[1].trim());
        Propiedad propPide = (Propiedad) demanda[0];
        int dineroPide = (int) demanda[1];

        // 3. Validaciones de propiedad (Punto 3: Estado actual)
        if (propOfrece != null && !propOfrece.perteneceAJugador(proponente)) {
            throw new AccionInvalidaException("No puedes ofrecer " + propOfrece.getNombre() + " porque no es tuya.");
        }
        if (propPide != null && !propPide.perteneceAJugador(propuesto)) {
            throw new AccionInvalidaException("No puedes pedir " + propPide.getNombre() + " porque no pertenece a " + propuesto.getNombre());
        }
        if (dineroOfrece > proponente.getFortuna()) {
            throw new SaldoInsuficienteException(proponente.getNombre(), dineroOfrece - proponente.getFortuna(),0);
        }

        // 4. Crear trato
        Trato nuevoTrato = new Trato(proponente, propuesto, propOfrece, dineroOfrece, propPide, dineroPide);
        propuesto.recibirTrato(nuevoTrato);
        proponente.recibirTrato(nuevoTrato); // Opcional: para que el proponente también lo vea si quieres

        Juego.consola.imprimir("Trato propuesto correctamente: " + nuevoTrato.getId());
    }

    // --- NUEVO MÉTODO DE PARSEO ESTRICTO (Punto 1) ---
    private Object[] analizarParteTrato(String texto) throws AccionInvalidaException {
        Propiedad p = null;
        int d = 0;

        if (texto.isEmpty() || texto.equalsIgnoreCase("nada")) {
            return new Object[]{null, 0};
        }

        String[] elementos = texto.split(" y ");
        for (String item : elementos) {
            item = item.trim();
            if (item.isEmpty()) continue;

            // Intentamos ver si es número
            try {
                int val = Integer.parseInt(item);
                if (val < 0) throw new AccionInvalidaException("No puedes usar cantidades negativas: " + val);
                d += val;
            } catch (NumberFormatException e) {
                // Si no es número, TIENE que ser una propiedad válida
                Casilla c = tablero.encontrar_casilla(item);
                if (c == null) {
                    throw new AccionInvalidaException("La propiedad '" + item + "' no existe en el tablero.");
                }
                if (!(c instanceof Propiedad)) {
                    throw new AccionInvalidaException("'" + item + "' no es una propiedad intercambiable.");
                }
                if (p != null) {
                    throw new AccionInvalidaException("Solo puedes incluir una propiedad por lado del trato.");
                }
                p = (Propiedad) c;
            }
        }
        return new Object[]{p, d};
    }

    // --- REQUISITO 34: LISTAR TRATOS ---
    public void listarTratos() {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);

        // Asumo que Jugador tiene un método getListaTratos() que devuelve la colección de tratos recibidos
        java.util.Collection<Trato> tratos = actual.getListaTratos();
        if (tratos.isEmpty()) {
            Juego.consola.imprimir("No tienes tratos pendientes.");
            return;
        }

        Juego.consola.imprimir("Tratos vinculados a " + actual.getNombre() + ":");
        for (Trato t : tratos) {
            String rol = (t.getProponente() == actual) ? "[Propuesto por ti]" : "[TE PROPONEN]";
            Juego.consola.imprimir(t.toString() + " " + rol);
        }
    }
    /*
    // --- REQUISITO 33: ACEPTAR TRATO ---
    @Override
    public void aceptarTrato(String idTrato) throws MonopolyEtseException{
        if (!hayJugadores()) throw new AccionInvalidaException("No hay jugadores en el juego.");
        Jugador aceptante = jugadores.get(turno);

        // Asumo que Jugador tiene getTrato(id)
        Trato t = aceptante.getTrato(idTrato);

        if (t == null) {
            throw new AccionInvalidaException("No se encontró el trato " + idTrato + " en tu lista.");

        }

        if (t.getProponente() == aceptante) {
            throw new AccionInvalidaException("No puedes aceptar tu propio trato.");

        }

        Jugador proponente = t.getProponente();

        try {
            // 1. RE-VALIDACIÓN DE ESTADO (Fondos y Propiedades)

            if (t.getPropiedadOfrecida() != null && !t.getPropiedadOfrecida().perteneceAJugador(proponente))
                throw new AccionInvalidaException("El trato no puede ser aceptado: la propiedad ofrecida ya no pertenece al proponente.");

            if (t.getPropiedadDeseada() != null && !t.getPropiedadDeseada().perteneceAJugador(aceptante))
                throw new AccionInvalidaException("El trato no puede ser aceptado: la propiedad solicitada ya no te pertenece.");

            if (proponente.getFortuna() < t.getDineroOfrecido())
                throw new SaldoInsuficienteException(proponente.getNombre(), t.getDineroOfrecido() - proponente.getFortuna());

            if (aceptante.getFortuna() < t.getDineroDeseado())
                throw new SaldoInsuficienteException(aceptante.getNombre(), t.getDineroDeseado() - aceptante.getFortuna());

            // 2. EJECUCIÓN DEL INTERCAMBIO

            // Dinero
            if (t.getDineroOfrecido() > 0) {
                proponente.sumarGastos(t.getDineroOfrecido());
                aceptante.sumarFortuna(t.getDineroOfrecido());
            }
            if (t.getDineroDeseado() > 0) {
                aceptante.sumarGastos(t.getDineroDeseado());
                proponente.sumarFortuna(t.getDineroDeseado());
            }

            // Propiedades (Usamos los métodos de Jugador y Propiedad)
            if (t.getPropiedadOfrecida() != null) {
                t.getPropiedadOfrecida().getDueno().eliminarPropiedad(t.getPropiedadOfrecida());
                aceptante.anadirPropiedad(t.getPropiedadOfrecida());
                t.getPropiedadOfrecida().setDueno(aceptante);
            }
            if (t.getPropiedadDeseada() != null) {
                t.getPropiedadDeseada().getDueno().eliminarPropiedad(t.getPropiedadDeseada());
                proponente.anadirPropiedad(t.getPropiedadDeseada());
                t.getPropiedadDeseada().setDueno(proponente);
            }

            // 3. LIMPIEZA
            proponente.eliminarTrato(idTrato);
            aceptante.eliminarTrato(idTrato);

            Juego.consola.imprimir("¡Trato " + idTrato + " aceptado y ejecutado con éxito!");

        } catch (MonopolyEtseException e) {
            Juego.consola.imprimir("No se pudo realizar el trato: " + e.getMessage());
        }
    }

     */


    @Override
    public void aceptarTrato(String idTrato) throws MonopolyEtseException {
        if (!hayJugadores()) return;
        Jugador aceptante = jugadores.get(turno);
        Trato t = aceptante.getTrato(idTrato);

        if (t == null) throw new AccionInvalidaException("Trato no encontrado: " + idTrato);
        if (t.getProponente() == aceptante) throw new AccionInvalidaException("No puedes aceptar tu propio trato.");

        Jugador proponente = t.getProponente();

        // --- RE-VALIDACIÓN (Punto 3: El estado puede haber cambiado) ---
        if (t.getPropiedadOfrecida() != null && !t.getPropiedadOfrecida().perteneceAJugador(proponente)) {
            throw new AccionInvalidaException("El trato ya no es válido: " + proponente.getNombre() + " ya no tiene " + t.getPropiedadOfrecida().getNombre());
        }
        if (t.getPropiedadDeseada() != null && !t.getPropiedadDeseada().perteneceAJugador(aceptante)) {
            throw new AccionInvalidaException("El trato ya no es válido: Tú ya no tienes " + t.getPropiedadDeseada().getNombre());
        }
        if (proponente.getFortuna() < t.getDineroOfrecido()) {
            throw new SaldoInsuficienteException(proponente.getNombre(), t.getDineroOfrecido() - proponente.getFortuna(),0);
        }
        if (aceptante.getFortuna() < t.getDineroDeseado()) {
            throw new SaldoInsuficienteException(aceptante.getNombre(), t.getDineroDeseado() - aceptante.getFortuna(),0);
        }

        // --- EJECUCIÓN (Punto 2: Transferencia correcta) ---

        // 1. Dinero
        if (t.getDineroOfrecido() > 0) {
            proponente.sumarGastos(t.getDineroOfrecido());
            aceptante.sumarFortuna(t.getDineroOfrecido());
        }
        if (t.getDineroDeseado() > 0) {
            aceptante.sumarGastos(t.getDineroDeseado());
            proponente.sumarFortuna(t.getDineroDeseado());
        }

        // 2. Propiedades (Usamos un método helper para evitar errores)
        if (t.getPropiedadOfrecida() != null) {
            transferirPropiedad(t.getPropiedadOfrecida(), proponente, aceptante);
        }
        if (t.getPropiedadDeseada() != null) {
            transferirPropiedad(t.getPropiedadDeseada(), aceptante, proponente);
        }

        // 3. Limpieza
        aceptante.eliminarTrato(idTrato);
        proponente.eliminarTrato(idTrato); // Borrarlo también del proponente

        Juego.consola.imprimir("¡Trato cerrado! Se han intercambiado los bienes.");
    }

    private void transferirPropiedad(Propiedad p, Jugador origen, Jugador destino) {
        origen.eliminarPropiedad(p);
        destino.anadirPropiedad(p);
        p.setDueno(destino);
    }

    // --- REQUISITO 35: ELIMINAR TRATO ---
    @Override
    public void eliminarTrato(String idTrato) throws MonopolyEtseException{
        if (!hayJugadores()) throw new AccionInvalidaException("No hay jugadores en el juego.");
        Jugador actual = jugadores.get(turno);

        Trato t = actual.getTrato(idTrato);
        if (t == null) {
            throw new AccionInvalidaException("No se encontró el trato " + idTrato);

        }

        // Regla: Solo el que propone puede eliminar
        if (t.getProponente() != actual) {
            throw new AccionInvalidaException("No puedes eliminar este trato porque no lo propones.Solo puedes aceptarlo o ignorarlo.");
        }

        // Eliminar de ambos jugadores involucrados
        Jugador otro = t.getPropuesto();

        actual.eliminarTrato(idTrato);
        otro.eliminarTrato(idTrato);

        Juego.consola.imprimir("Se ha eliminado el trato " + idTrato);
    }
    // Método simple 1: Busca si hay dinero en el texto (ej: "Solar1 y 5000")
    private int sacarDinero(String texto) {
        // Separamos por " y "
        String[] partes = texto.split(" y ");
        for (String parte : partes) {
            try {
                // Si se puede convertir a número, es el dinero. Lo devolvemos.
                return Integer.parseInt(parte.trim());
            } catch (NumberFormatException e) {
                // Si falla, no era número, seguimos buscando
            }
        }
        return 0; // Si no encontramos dinero, devolvemos 0
    }

    // Método simple 2: Busca si hay una propiedad en el texto
    private Propiedad sacarPropiedad(String texto) {
        String[] partes = texto.split(" y ");
        for (String parte : partes) {
            // Buscamos si el texto coincide con una casilla
            Casilla c = tablero.encontrar_casilla(parte.trim());
            // Si existe y es una Propiedad, la devolvemos
            if (c instanceof Propiedad) {
                return (Propiedad) c;
            }
        }
        return null; // Si no encontramos propiedad, devolvemos null
    }

}




