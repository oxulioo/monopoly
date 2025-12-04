package monopoly;
import partida.*;
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
    public void eliminarTrato(String idTrato){
        tratosRecibidos.remove(idTrato);
    }

    public void listarTratos(){
        consola.imprimir("Tratos recibidos:");
    }

    public Trato getTrato(String idTrato){
        return tratosRecibidos.get(idTrato);
    }
    public java.util.Collection<Trato> getListaTratos(){
        return tratosRecibidos.values();
    }
    public void aceptarTrato(String idTrato){
        tratosRecibidos.remove(idTrato);
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
        this.baraja = new Baraja(); // <-- Reiniciar baraja al iniciar nueva partida
        this.baraja.barajar();
    }

    public void mostrarJugadorActual() {
        if (jugadores == null || jugadores.isEmpty()) {
            Juego.consola.imprimir("No hay jugadores. Crea uno con: crear jugador <Nombre> <tipoAvatar>");
            return;
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

    public void listarJugadores() {
        if (!hayJugadores()) {
            return;
        }
        for (Jugador j : jugadores) {
            descJugador(j.getNombre());
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
        if (ID == null || ID.isEmpty()) return;
        if (avatares == null || avatares.isEmpty()) return;

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
        if (nombre == null || nombre.isEmpty()) return;
        Casilla c = null;
        c = tablero.encontrar_casilla(nombre);
        if (c == null) {
            throw new AccionInvalidaException ("No existe la casilla con ese nombre");
        }
        // Casilla.toString() ya hace lo correcto en las hijas
        Juego.consola.imprimir(c.toString());
    }

    public void lanzarDados() throws MonopolyEtseException{
        if (!hayJugadores()) {
            return;
        }
        if (lanzamientos == 1) {
            // Ya no hacemos imprimir + return, sino que lanzamos la excepción
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
//FIX ME: ME QUEDE AQUI
        try {
            Avatar a = actual.getAvatar();
            a.moverAvatar(tablero.getPosiciones(), suma);
            Casilla c = a.getPosicion();

            // Check por nombre para IrCarcel ya que ahora es una clase, pero nombre sigue siendo IrCarcel
            if (c != null && c instanceof IrCarcel) {
                // IrCarcel.evaluarCasilla ya gestiona, pero moverAvatar lo llama al final.
                // Aquí solo imprimimos si es necesario
            }
            if (c != null) {
                c.evaluarCasilla(actual, this, suma);
            }
        } catch (Throwable e) {
            Juego.consola.imprimir("(Aviso) Error en movimiento: " + e.getMessage());
            return;
        }
        Juego.consola.imprimir(tablero.toString());

        if (esDoble) {
            Juego.consola.imprimir(actual.getNombre() + " ha sacado dobles y puede volver a lanzar.");
            tirado = false;
        }
    }

    public void lanzarDadosForzado(int d1, int d2) {
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

        try {
            Avatar a = actual.getAvatar();
            a.moverAvatar(tablero.getPosiciones(), suma);
            Casilla c = a.getPosicion();
            if (c != null) c.evaluarCasilla(actual, this, suma);
        } catch (Throwable e) {
        }

        try {
            Juego.consola.imprimir(tablero.toString());
        } catch (Throwable ignore) {
        }
        if (esDoble) tirado = false;
    }

    public void comprar(String nombre) {
        if (!hayJugadores()) {
            return;
        }
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        Casilla cas = tablero.encontrar_casilla(nombre);
        if (cas == null) {
            Juego.consola.imprimir("No existe la casilla: " + nombre);
            return;
        }

        // CORRECCIÓN: Check Propiedad
        if (!(cas instanceof Propiedad)) {
            Juego.consola.imprimir("La casilla " + nombre + " no se puede comprar.");
            return;
        }
        Propiedad prop = (Propiedad) cas;

        Casilla pos = actual.getAvatar().getPosicion();
        if (pos == null || pos != cas) {
            Juego.consola.imprimir("No puedes comprar '" + prop.getNombre() + "': tu avatar no está en esa casilla.");
            return;
        }

        Jugador propietario = prop.getDueno();
        if (propietario != null && propietario != this.getBanca()) {
            Juego.consola.imprimir("La propiedad '" + prop.getNombre() + "' no está en venta.");
            return;
        }

        int precio = prop.getValor();
        double saldo = actual.getFortuna();
        if (saldo < precio) {
            Juego.consola.imprimir("No tienes suficiente dinero para comprar '" + prop.getNombre() + "'. Precio: " + precio);
            return;
        }
        prop.comprar(actual); // Llama al comprar de Propiedad

        if (prop.getDueno() == actual) {
            actual.getEstadisticas().sumarDineroInvertido((long) precio);
        }
        Juego.consola.imprimir(actual.getNombre() + " compra '" + prop.getNombre() + "' por " + precio + ".");
        Juego.consola.imprimir("La fortuna actual de " + actual.getNombre() + " es: " + actual.getFortuna() + ".");
        Juego.consola.imprimir(tablero.toString());
    }

    public void salirCarcel() {
        if (!hayJugadores()) {
            return;
        }
        Jugador actual = jugadores.get(turno);
        if (!actual.isEnCarcel()) {
            Juego.consola.imprimir(actual.getNombre() + " no está en la cárcel.");
            return;
        }
        if (actual.getFortuna() < Valor.PRECIO_SALIR_CARCEL) {
            Juego.consola.imprimir("No tienes suficiente dinero.");
            return;
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

    public void listarVenta() {
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
            descCasilla(c.getNombre());
            Juego.consola.imprimir("},\n{");
        }
    }

    public void listarAvatares() {
        if (avatares == null || avatares.isEmpty()) return;
        for (Avatar a : avatares) {
            char id = '\0';
            String tipo = "-";
            String pos = "-";
            String jugadorNombre = "-";

            try {
                id = a.getID();
            } catch (Throwable ignored) {
            }
            try {
                tipo = a.getTipo();
            } catch (Throwable ignored) {
            }
            try {
                Casilla c = a.getPosicion();
                if (c != null) pos = c.getNombre();
            } catch (Throwable ignored) {
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

    public void enviarACarcel(Jugador j) {
        if (j == null || tablero == null) return;
        Casilla carcel = tablero.encontrar_casilla("Cárcel");
        j.encarcelar();
        try {
            Avatar a = j.getAvatar();
            if (a != null) a.setPosicion(carcel);
        } catch (Throwable ignored) {
        }
    }

    // --- EDIFICACIÓN (Requiere casteo a Solar) ---

    public void edificarCasa() {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);
        Casilla pos = actual.getAvatar().getPosicion();

        // CORRECCIÓN: Check Solar
        if (!(pos instanceof Solar)) {
            Juego.consola.imprimir("Sólo se puede edificar en casillas de tipo solar.\n");
            return;
        }
        Solar s = (Solar) pos;

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

    public void edificarHotel() {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);
        Casilla pos = actual.getAvatar().getPosicion();
        if (!(pos instanceof Solar)) return;
        ((Solar) pos).edificar("hotel");
    }

    public void edificarPiscina() {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);
        Casilla pos = actual.getAvatar().getPosicion();
        if (!(pos instanceof Solar)) return;
        ((Solar) pos).edificar("piscina");
    }

    public void edificarPista() {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);
        Casilla pos = actual.getAvatar().getPosicion();
        if (!(pos instanceof Solar)) return;
        ((Solar) pos).edificar("pista");
    }

    // --- LISTAR EDIFICIOS ---
    // Método para listar edificios (todos o por color)
    public void listarEdificios(String color) {
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
                    try {
                        grupoStr = e.getSolar().getGrupo().getColorGrupo();
                    } catch (Throwable ignored) {
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
                Juego.consola.imprimir("No existe el grupo de color: " + color);
                return;
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
    private void listarEdificiosPorTipo(Casilla c, Edificio.Tipo tipo) {
        if (!(c instanceof Solar)) {
            Juego.consola.imprimir("-");
            return;
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

    public long costeConstruccion(Casilla c, Edificio.Tipo t) {
        if (!(c instanceof Solar)) return 0;
        Solar s = (Solar) c;
        return switch (t) {
            case CASA -> s.getPrecioCasa();
            case HOTEL -> s.getPrecioHotel();
            case PISCINA -> s.getPrecioPiscina();
            case PISTA -> s.getPrecioPistaDeporte();
        };
    }

    // --- HIPOTECAS ---
    public void hipotecar(String nombreProp) {
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(nombreProp);
        if (!(c instanceof Propiedad)) return;
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

    public void deshipotecar(String nombreSolar) {
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(nombreSolar);

        // 1. Verificar si existe
        if (c == null) {
            Juego.consola.imprimir("No existe la casilla: " + nombreSolar);
            return;
        }

        // 2. Verificar si es una Propiedad (Solar, Transporte, Servicio)
        if (!(c instanceof Propiedad)) {
            Juego.consola.imprimir("La casilla " + c.getNombre() + " no es una propiedad y no se puede deshipotecar.");
            return;
        }

        Propiedad p = (Propiedad) c; // Casteo seguro

        // 3. Verificar dueño
        if (!p.perteneceAJugador(actual)) {
            Juego.consola.imprimir(actual.getNombre() + " no puede deshipotecar " + p.getNombre() + ". No es su propiedad.");
            return;
        }

        // 4. Verificar si está hipotecada
        if (p.gethipotecada() == 0) {
            Juego.consola.imprimir(actual.getNombre() + " no puede deshipotecar " + p.getNombre() + ". No está hipotecada.");
            return;
        }

        // 5. Calcular coste (Hipoteca + 10% de interés)
        int coste = (int) (p.getHipoteca() * 1.10);

        // 6. Verificar solvencia y ejecutar
        if (actual.getFortuna() < coste) {
            Juego.consola.imprimir("No tienes dinero suficiente para deshipotecar " + p.getNombre() + ". Coste: " + coste + "€");
            return;
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
    public void venderPropiedad(String tipo, String solarName, int cantidad) {
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(solarName);

        // 1. Validaciones básicas
        if (c == null) {
            Juego.consola.imprimir("No existe la casilla: " + solarName);
            return;
        }

        // 2. Validación de tipo: Solo los Solares se pueden vender por partes (edificios)
        if (!(c instanceof Solar)) {
            Juego.consola.imprimir("La casilla " + solarName + " no es un Solar, no se puede vender " + tipo + ".");
            return;
        }
        Solar s = (Solar) c; // Casteo a Solar para acceder a sus métodos

        // 3. Validación de dueño
        if (!s.getDueno().equals(actual)) {
            Juego.consola.imprimir("No se pueden vender " + tipo + " en " + s.getNombre() + ". Esta propiedad no pertenece a " + actual.getNombre() + ".");
            return;
        }

        String t = tipo.toLowerCase();

        // 4. Lógica de venta (Copiada de tu original, usando 's' en vez de 'c')
        switch (t) {
            case "casas":
                int vendidas = Math.min(cantidad, s.getNumCasas());
                if (s.getNumCasas() == 0) {
                    Juego.consola.imprimir("No hay casas en esta propiedad.");
                    return;
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
                    Juego.consola.imprimir("No hay Pistas en esta propiedad");
                    return;
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
                if (s.getNumPiscinas() == 0) {
                    Juego.consola.imprimir("No hay Piscinas en esta propiedad");
                    return;
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
                if (s.getNumHoteles() == 0) {
                    Juego.consola.imprimir("No hay Hoteles en esta propiedad");
                    return;
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
                Juego.consola.imprimir("Tipo de edificio no reconocido: " + tipo);
        }
    }

    @Override
    public void estadisticasJugador(String nombre) {
        consola.imprimir("holamundo");
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
                e.eliminar();

                // Eliminamos el ID del mapa global
                numeroPorEdificio.remove(e.getId());

                quitados++;
            }
        }
    }

    // Antes llamaba a Carta.sacarCarta (estático)
    // Ahora llama a this.baraja.sacarCarta (instancia)
    public void procesarCasillaEspecial(Jugador jugador, String tipoCasilla) {
        Carta c = this.baraja.sacarCarta(tipoCasilla); // Usamos nuestra instancia
        if (c != null) {
            c.accion(jugador, this);
        } else {
            Juego.consola.imprimir("Error: No hay cartas disponibles de tipo " + tipoCasilla);
        }
    }

    public void moverJugadorACasilla(Jugador jugador, String nombreCasilla, boolean cobrarSalida) {
        Casilla destino = tablero.encontrar_casilla(nombreCasilla);
        if (destino != null) {
            if (cobrarSalida && pasaPorSalida(jugador.getAvatar().getPosicion(), destino)) {
                jugador.sumarFortuna(2000000);
            }
            jugador.getAvatar().setPosicion(destino);
            destino.evaluarCasilla(jugador, this, 0);
        }
    }

    // funcion analoga a la anterior pero mueve hacia atras
    public void moverJugadorAPosicion(Jugador jugador, int posicion) {
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if (c.getPosicion() == posicion) {
                    jugador.getAvatar().setPosicion(c);
                    c.evaluarCasilla(jugador, this, 0);
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
    public void proponerTrato(String comando){
        try{
            if(!hayJugadores()) return;
            Jugador proponente = jugadores.get(turno);

        }
    }
}




