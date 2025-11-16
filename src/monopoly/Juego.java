package monopoly;
import partida.*;
import java.util.ArrayList;

public class Juego {

    private ArrayList<Jugador> jugadores; //Jugadores de la partida en orden de alta.
    private ArrayList<Avatar> avatares; //Avatares en la partida.
    private int turno; //Índice correspondiente a la posición en el arrayList del jugador (y el avatar) que tienen el turno
    private int lanzamientos; //Variable para contar el número de lanzamientos de un jugador en un turno.
    private Tablero tablero; //Tablero en el que se juega.
    private Dado dado1; //Dos dados para lanzar y avanzar casillas.
    private Dado dado2;
    private boolean tirado; //Booleano para comprobar si el jugador que tiene el turno ha tirado o no.
    private int doblesConsecutivos = 0; // para contar dobles en el mismo turno
    private final Jugador banca;
    private final java.util.EnumMap<Edificio.Tipo,Integer> secTipo =
            new java.util.EnumMap<>(Edificio.Tipo.class);
    private final java.util.Map<Long,Integer> numeroPorEdificio = new java.util.HashMap<>();

    public Juego() {
        this.banca = new Jugador(); // Constructor vacío = banca
        this.tablero = new Tablero(banca); // Se pasa al crear el tablero
        this.jugadores = new ArrayList<>();
    }

    public Jugador getBanca() {
        return banca;
    }
    public Tablero getTablero() {
        return tablero;
    }
    public int getJugadoresNum(){
        return jugadores.size();
    }
    private long secEdificio = 0;
    private long nextEdificioId() { return ++secEdificio; }


    private int nextNumTipo(Edificio.Tipo t) {
        int n = secTipo.getOrDefault(t, 0) + 1;
        secTipo.put(t, n);
        return n;
    }

    // Imprime el tablero (usa toString() del Tablero)
    public void verTablero() {
        if (tablero == null) {
            System.out.println("No hay tablero cargado.");
            return;
        }
        System.out.print(tablero);
    }


    // Método para iniciar una partida: crea los jugadores y avatares.

    public void iniciarPartida() {
        // Estructuras y estado base
        jugadores    = new ArrayList<>();
        avatares     = new ArrayList<>();
        turno        = 0;
        lanzamientos = 0;
        tirado       = false;
        //Booleano para comprobar si el jugador que tiene el turno es solvente, es decir, si ha pagado sus deudas.

        // Dados
        dado1 = new Dado();
        dado2 = new Dado();
        // Tablero: la banca empieza como propietaria de todo

        tablero = new Tablero(this.banca);
    }

    // Muestra quién tiene el turno actual (por índice 'turno')
    public void mostrarJugadorActual() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores. Crea uno con: crear jugador <Nombre> <tipoAvatar>");
            return;
        }
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);
        System.out.println("Tiene el turno: " + actual.getNombre());
    }

    // Crea Jugador+Avatar en "Salida" y repinta el tablero
    public void crearJugador(String nombre, String tipoAvatar) {
        // 1) Buscar casilla de inicio ("Salida")
        Casilla salida;
        try {
            salida = tablero.encontrar_casilla("Salida");
        } catch (Exception e) {
            System.out.println("No se pudo localizar la casilla 'Salida'.");
            return;
        }
        if (salida == null) {
            System.out.println("No se encontró 'Salida' en el tablero.");
            return;
        }

        // 2) Crear jugador (asumimos que el constructor genera el Avatar con ID aleatorio)

        Jugador j;
        try {
            j = new Jugador(nombre, tipoAvatar, salida, avatares);
        } catch (Exception e) {
            System.out.println("Error creando jugador: " + e.getMessage());
            return;
        }

        // 3) Registrar
        jugadores.add(j);
        // Si vuestro Jugador expone getAvatar(), podríais también: if (j.getAvatar()!=null) avatares.add(j.getAvatar());

        // 4) Mensaje + “repintar” tablero
        System.out.println("Creado jugador '" + nombre + "' con avatar '" + tipoAvatar + "' en Salida.");
        try { System.out.println(tablero); } catch (Throwable ignored) {}
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    public void listarJugadores() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }
        for (Jugador j : jugadores) {
            // nombre
            String nombre = j.getNombre();
            descJugador(nombre);
        }
    }

    /* Método que realiza las acciones asociadas al comando 'describir jugador'.
     * Parámetro: comando introducido */
    // Imprime toda la info del jugador pedido en Parte 1
    public void descJugador(String nombreBuscado) {
        if (nombreBuscado == null || nombreBuscado.isEmpty()) {
            System.out.println("Uso: describir jugador <Nombre>");
            return;
        }
        if(hayJugadores()){return;}

        // Buscar por nombre exacto
        Jugador j = null;
        for (Jugador x : jugadores) {
            if (x.getNombre().equals(nombreBuscado)) {
                j = x;
                break;
            }
        }
        if (j == null) {
            System.out.println("No existe el jugador: " + nombreBuscado);
            return;
        }

        // Datos básicos
        String nombre = j.getNombre();

        // Avatar
        String avatar = "-";
        Avatar a = j.getAvatar();
        if (a != null) {
            String tipo = a.getTipo();
            char id   = a.getID();
            avatar = tipo + " (" + id + ")";
        }

        // Fortuna
        String fortuna = String.format("%d", j.getFortuna());

        // Posicion actual
        String posicion = "-";
        Casilla pos = null;
        if (a != null) pos = a.getPosicion();
        if (pos == null) {
            try { assert a != null; pos = a.getPosicion(); } catch (Throwable ignored) {}
        }
        if (pos != null) posicion = pos.getNombre();

        String propiedades = "-";
        java.util.List<Casilla> auxProps = j.getPropiedades();
        if (auxProps != null && !auxProps.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int added = 0;
            for (Casilla c : auxProps) {
                if (c != null && c.gethipotecada() == 0) {
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
                if (c != null && c.gethipotecada() == 1) {
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





        // ¿En cárcel? ( 'Sí/No')
        String enCarcel = j.isEnCarcel() ? "Sí" : "No";

        // Salida formateada
        System.out.println("Jugador: " + nombre);
        System.out.println("  Avatar: " + avatar);
        System.out.println("  Fortuna: " + fortuna);
        System.out.println("  Posición: " + posicion);
        System.out.println("  Propiedades: " + propiedades);
        System.out.println("  Hipotecas: " + hipotecadas);
        System.out.println("  Edificios:" + edificiosLista);
        System.out.println("  En cárcel: " + enCarcel);
        System.out.println();
    }



    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
     * Parámetro: id del avatar a describir.
     */
    public void descAvatar(String ID) {
        if (ID == null || ID.isEmpty()) {
            System.out.println("Uso: describir avatar <ID>");
            return;
        }
        if (avatares == null || avatares.isEmpty()) {
            System.out.println("No hay avatares en la partida.");
            return;
        }

        char idBuscado = ID.charAt(0);   // convertir el String introducido a char

        Avatar encontrado = null;
        for (Avatar a : avatares) {
            if (a.getID() == idBuscado) {   // comparar char con char
                encontrado = a;
                break;
            }
        }
        if (encontrado == null) {
            System.out.println("No existe el avatar con ID: " + idBuscado);
            return;
        }

        String tipo = encontrado.getTipo();
        String pos = "-";
        Casilla c = encontrado.getPosicion();
        if (c != null) pos = c.getNombre();

        String jugadorNombre = "-";
        Jugador j = encontrado.getJugador();
        if (j != null) jugadorNombre = j.getNombre();

        System.out.println("Avatar: " + idBuscado);
        System.out.println("  Tipo: " + tipo);
        System.out.println("  Jugador: " + jugadorNombre);
        System.out.println("  Posición: " + pos);
        System.out.println();
    }




    /* Método que realiza las acciones asociadas al comando 'describir nombre_casilla'.
     * Parámetros: nombre de la casilla a describir.
     */
    public void descCasilla(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            System.out.println("Uso: describir <Casilla>");
            return;
        }

        // Buscar la casilla en el tablero
        Casilla c = null;
        try {
            c = tablero.encontrar_casilla(nombre);
        } catch (Throwable ignore) {}

        if (c == null) {
            System.out.println("No se encontró la casilla: " + nombre);
            return;
        }

        // Mostrar la descripción usando Casilla.infoCasilla()
        try {
            System.out.println(c.infoCasilla());
        } catch (Throwable t) {

            try { System.out.println("Casilla: " + c.getNombre()); }
            catch (Throwable ignore) { System.out.println(c); }
        }
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'lanzar dados'.
    public void lanzarDados() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores. Crea uno con: crear jugador <Nombre> <tipoAvatar>");
            return;
        }
        if (lanzamientos==1){
            System.out.println("Ya has tirado una vez, no puedes volver a tirar.");
            return;
        }
        Jugador actual = jugadores.get(turno);

        // Tiramos los dos dados usando hacerTirada() para conocer cada valor
        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int suma = d1 + d2;
        if (d1 !=d2 ){
            lanzamientos=1;
        }

        if (actual.isEnCarcel()){

            if (actual.getTiradasCarcel()==3 && d1!=d2 && lanzamientos!=1){
                System.out.println("Sales de la carcel");
                salirCarcel();
                actual.setTiradasCarcel(0);
            }

            if (d1==d2 && lanzamientos!=1){
                System.out.println("Sales de la carcel");
                salirCarcel();
                actual.setTiradasCarcel(0);
            }

            if (d1!=d2 && actual.getTiradasCarcel()<3 && lanzamientos!=1){
                actual.setTiradasCarcel(actual.getTiradasCarcel()+1);
                System.out.println("Has tirado " + actual.getTiradasCarcel() + " veces en la carcel.");
                return;
            }

        }
        boolean esDoble = (d1 == d2);

        System.out.println("Dados: " + d1 + " + " + d2 + " = " + suma + (esDoble ? " (dobles)" : ""));

        // Gestionar dobles y 3 dobles seguidos -> cárcel
        if (esDoble) {
            doblesConsecutivos++;
            if (doblesConsecutivos >= 3) {
                System.out.println("¡Tres dobles seguidos! " + actual.getNombre() + " va a la cárcel.");
                lanzamientos=1;
                enviarACarcel(actual);
                doblesConsecutivos = 0;
                tirado = true;                 // el turno termina al ir a la cárcel
                try { System.out.println(tablero); } catch (Throwable ignore) {}
                return;

            }
        } else {
            doblesConsecutivos = 0;
            tirado = true; // si no es doble, este turno ya no puede volver a tirar
        }

        // Mover al jugador 'suma' casillas usando su Avatar
        try {
            Avatar a = actual.getAvatar();
            a.moverAvatar(tablero.getPosiciones(), suma);

            // Casilla actual tras mover
            Casilla c = a.getPosicion();

            // Si cae en "IrCarcel", lo mandamos a la cárcel y terminamos
            if (c != null && "IrCarcel".equalsIgnoreCase(c.getNombre())) {
                System.out.println("¡Ir a Cárcel! " + actual.getNombre() + " va a la cárcel.");
                enviarACarcel(actual);
                lanzamientos=1;
                try { System.out.println(tablero); } catch (Throwable ignore) {}
                return;
            }

            if (c != null && ("Suerte".equals(c.getTipo()) || "Comunidad".equals(c.getTipo()))) {
                procesarCasillaEspecial(actual, c.getTipo());
            }
            // Evaluar efectos de la casilla (pagos, etc.)
            if (c != null) {
                c.evaluarCasilla(actual, this, suma);
            }
        } catch (Throwable e) {
            System.out.println("(Aviso) Falta implementar correctamente el movimiento del Avatar.");
            return;
        }

        // Repintar tablero
        System.out.println(tablero);

        // Si sacó dobles (y no eran 3), puede volver a lanzar en este turno
        if (esDoble) {
            System.out.println(actual.getNombre() + " ha sacado dobles y puede volver a lanzar.");
            tirado = false; // permitir otra tirada
        }

    }



    //  lanzar dados X+Y (forzado)
    public void lanzarDadosForzado(int d1, int d2) {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores. Crea uno con: crear jugador <Nombre> <tipoAvatar>");
            return;
        }
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        if (actual.isEnCarcel()){

            if (actual.getTiradasCarcel()==3 && d1!=d2 && lanzamientos!=1){
                System.out.println("Sales de la carcel");
                salirCarcel();
                actual.setTiradasCarcel(0);
            }

            if (d1==d2&& lanzamientos!=1){
                System.out.println("Sales de la carcel");
                salirCarcel();
                actual.setTiradasCarcel(0);
            }

            if (d1!=d2 && actual.getTiradasCarcel()<3 && lanzamientos!=1){
                actual.setTiradasCarcel(actual.getTiradasCarcel()+1);
                System.out.println("Has tirado " + actual.getTiradasCarcel() + " veces en la carcel.");
                return ;
            }

        }

        int suma = d1 + d2;
        if (d1>6||d2>6||d1<1||d2<1){
            System.out.println("Los dados no pueden ser mayores a 6 o menores que 1.");
            return;
        }


        if (lanzamientos==1){
            System.out.println("Ya has tirado una vez, no puedes volver a tirar.");
            return;
        }

        if (d1!=d2) {
            lanzamientos = 1;
        }
        boolean esDoble = (d1 == d2);

        System.out.println("Dados (forzado): " + d1 + " + " + d2 + " = " + suma + (esDoble ? " (dobles)" : ""));

        // Gestionar dobles y 3 dobles seguidos -> cárcel
        if (esDoble) {
            doblesConsecutivos++;
            if (doblesConsecutivos >= 3) {
                System.out.println("¡Tres dobles seguidos! " + actual.getNombre() + " va a la cárcel.");
                lanzamientos=1;
                enviarACarcel(actual);
                doblesConsecutivos = 0;
                tirado = true;  // el turno termina al ir a cárcel
                try { System.out.println(tablero); } catch (Throwable ignore) {}
                return;
            }
        } else {
            doblesConsecutivos = 0;
            tirado = true; // si no es doble, ya no puede volver a tirar este turno
        }

        // Mover al jugador 'suma' casillas usando su Avatar
        try {
            Avatar a = actual.getAvatar();
            a.moverAvatar(tablero.getPosiciones(), suma);

            // Casilla actual tras mover
            Casilla c = a.getPosicion();

            // Si cae en "IrCarcel", lo mandamos a la cárcel y terminamos
            if (c != null && "IrCarcel".equalsIgnoreCase(c.getNombre())) {
                System.out.println("¡Ir a Cárcel! " + actual.getNombre() + " va a la cárcel.");
                enviarACarcel(actual);
                try { System.out.println(tablero); } catch (Throwable ignore) {}
                return;
            }

            if (c != null && ("Suerte".equals(c.getTipo()) || "Comunidad".equals(c.getTipo()))) {
                procesarCasillaEspecial(actual, c.getTipo());
            }

            // Evaluar efectos de la casilla (pagos, etc.)
            if (c != null) {
                c.evaluarCasilla(actual, this, suma);
            }
        } catch (Throwable e) {
            System.out.println("(Aviso) Falta implementar correctamente el movimiento del Avatar.");
            return;
        }


        // Repintar tablero
        try { System.out.println(tablero); } catch (Throwable ignore) {}

        // Si sacó dobles (y no eran 3), puede volver a lanzar
        if (esDoble) {
            System.out.println(actual.getNombre() + " ha sacado dobles y puede volver a lanzar.");
            tirado = false; // permitir otra tirada en este turno
        }
    }


    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
     * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    // comprar <Propiedad>
    public void comprar(String nombre) {
        if(hayJugadores()){return;}
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        // 1) Buscar la casilla por nombre
        Casilla cas;
        try { cas = tablero.encontrar_casilla(nombre); } catch (Throwable t) { cas = null; }
        if (cas == null) {
            System.out.println("No existe la casilla: " + nombre);
            return;
        }

        // 2) Comprobar que el avatar del jugador está exactamente en esa casilla
        Casilla pos;
        try { pos = actual.getAvatar().getPosicion(); } catch (Throwable t) { pos = null; }
        if (pos == null || pos != cas) {
            System.out.println("No puedes comprar '" + cas.getNombre() + "': tu avatar no está en esa casilla.");
            return;
        }

        // 3) Comprobar propietario actual (debe ser la banca)
        Jugador propietario = null;
        try { propietario = cas.getDueno(); } catch (Throwable ignored) {}
        if (propietario != null && propietario != this.getBanca()) {
            System.out.println("La propiedad '" + cas.getNombre() + "' no está en venta.");
            return;
        }

        // 4) Precio y dinero disponible
        double precio;
        try { precio = cas.getValor(); } catch (Throwable t) {
            System.out.println("No se pudo determinar el precio de '" + cas.getNombre() + "'.");
            return;
        }
        double saldo = actual.getFortuna();
        if (saldo < precio) {
            System.out.println("No tienes suficiente dinero para comprar '" + cas.getNombre() + "'. Precio: " + (long)precio);
            return;
        }
        cas.comprarCasilla(actual, this.getBanca());

        // Mensajes y repintado
        System.out.println(actual.getNombre() + " compra '" + cas.getNombre() + "' por " + (long)precio + ".");
        System.out.println("La fortuna actual de " + actual.getNombre() + " es: " + actual.getFortuna()+".");
        try { System.out.println(tablero); } catch (Throwable ignored) {}
    }


    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'.
    public static final int COSTE_SALIR_CARCEL = 500000;

    public void salirCarcel() {
        if(hayJugadores()){return;}
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();

        Jugador actual = jugadores.get(turno);

        // 1) Comprobar si está en la cárcel
        if (!actual.isEnCarcel()) {
            System.out.println(actual.getNombre() + " no está en la cárcel.");
            return;
        }

        // 2) Comprobar dinero suficiente
        double fortuna = actual.getFortuna();
        if (fortuna < COSTE_SALIR_CARCEL) {
            System.out.println("No tienes suficiente dinero para salir de la cárcel.");
            return;
        }

        // 3) Pagar a la banca y marcar como libre
        boolean ok= actual.sumarGastos(COSTE_SALIR_CARCEL);
        if(ok) actual.getEstadisticas().sumarPagoTasasImpuestos(COSTE_SALIR_CARCEL);

        actual.salirCarcel();

        // 4) Mensaje + repintado del tablero (si toString() está implementado)
        System.out.println(actual.getNombre() + " paga " + COSTE_SALIR_CARCEL + " y sale de la cárcel.");
        try { System.out.println(tablero); } catch (Throwable ignored) {}
    }


    public void listarVenta() {
        ArrayList<Casilla> enVenta = new ArrayList<>();

        for (ArrayList<Casilla> lado : tablero.getPosiciones()) { // recorres cada lado del tablero
            for (Casilla c : lado) {                              // recorres cada casilla del lado
                if (c.getDueno() == this.getBanca() &&
                        c.getTipo().equalsIgnoreCase("Solar") ||
                        c.getTipo().equalsIgnoreCase("Transporte") ||
                        c.getTipo().equalsIgnoreCase("Servicios")) {

                    enVenta.add(c);
                }
            }
        }

        // ahora imprimimos las casillas en venta
        for (Casilla c : enVenta) {
            String nombre = c.getNombre();
            descCasilla(nombre);
            System.out.println("},\n{");
        }
    }

    // Método que realiza las acciones asociadas al comando 'listar avatares'.

    public void listarAvatares() {
        if (avatares == null || avatares.isEmpty()) {
            System.out.println("No hay avatares en la partida.");
            return;
        }
        for (Avatar a : avatares) {
            char id= '\0' ;
            String tipo = "-";
            String pos = "-";
            String jugadorNombre = "-";

            try { id = a.getID(); } catch (Throwable ignored) {}
            try { tipo = a.getTipo(); } catch (Throwable ignored) {}
            try { Casilla c = a.getPosicion(); if (c != null) pos = c.getNombre(); } catch (Throwable ignored) {}
            try {
                Jugador j = a.getJugador();
                if (j != null) jugadorNombre = j.getNombre();
            } catch (Throwable ignored) {
                for (Jugador j : jugadores) {
                    try { if (j.getAvatar() == a) { jugadorNombre = j.getNombre(); break; } } catch (Throwable ignored2) {}
                }
            }

            System.out.println("{ id: " + id + ", tipo: " + tipo + ", jugador: " + jugadorNombre + ", posición: " + pos + " }");
        }
    }


    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    public void acabarTurno() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }
        // normaliza por si acaso
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        lanzamientos=0;
        // pasar al siguiente
        turno = (turno + 1) % jugadores.size();

        // reset de estado del turno
        doblesConsecutivos = 0;
        tirado = false;                // listo para que el nuevo jugador pueda lanzar


        // mensaje
        Jugador actual = jugadores.get(turno);
        System.out.println("Nuevo turno para: " + actual.getNombre());
    }






    // === Helper para enviar un jugador a la cárcel ===
    public void enviarACarcel(Jugador j) {
        if (j == null || tablero == null) return;
        Casilla carcel = tablero.encontrar_casilla("Cárcel");
        if (carcel == null) carcel = tablero.encontrar_casilla("Carcel"); // por si no hay tilde
        j.encarcelar();
        j.getEstadisticas().incrementarVecesEnLaCarcel();
        try {
            Avatar a = j.getAvatar();
            if (a != null) a.setPosicion(carcel);
        } catch (Throwable ignored) {}
    }


    public void edificarCasa(){
        if(hayJugadores()){return;}
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);
        //Sacamos la casilla a la que pertenece el avatar
        Casilla pos = actual.getAvatar().getPosicion();
        if(!Casilla.TSOLAR.equals(pos.getTipo())) {
            System.out.println("Sólo se puede edificar en casillas de tipo solar.\n");
            return;
        }
        if(!pos.getGrupo().esDuenoGrupo(actual)){
            System.out.println("No se puede edificar una casilla de un grupo que no es del jugador\n");
            return;
        }

        if (pos.getNumCasas()>=4){
            System.out.println("Ya tienes el máximo de casas en este solar. Prueba a construir un hotel!\n");
            return;

        }
        int precio = pos.getPrecioCasa();
        if (actual.getFortuna() < precio) {
            System.out.println("No tienes suficiente dinero para construir una casa");
            return;
        }

        // Restar dinero
        actual.sumarGastos(precio);
        pos.setNumCasas(pos.getNumCasas() + 1);

        // Crear y registrar el edificio
        Edificio.Tipo tipo = Edificio.Tipo.CASA;
        Edificio e = new Edificio(nextEdificioId(), tipo, pos, actual);
        pos.anadirEdificio(e);
        actual.anadirEdificio(e);

        // Asignar número de casa (para listado)
        numeroPorEdificio.put(e.getId(), nextNumTipo(tipo));

        System.out.println("Se ha edificado una casa en " + pos.getNombre() + ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
    }


    public void edificarHotel(){
        if(hayJugadores()){return;}
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        Casilla pos = actual.getAvatar().getPosicion();
        if(!Casilla.TSOLAR.equals(pos.getTipo())) {
            System.out.println("Sólo se puede edificar en casillas de tipo solar.\n");
            return;
        }
        if(!pos.getGrupo().esDuenoGrupo(actual)){
            System.out.println("No se puede edificar una casilla de un grupo que no es del jugador\n");
            return;
        }
        if(pos.getNumCasas()!=4){
            System.out.println("Aún no tienes las suficientes casas para comprar un hotel. Prueba a construír una casa!\n");
            return;
        }
        // elimina 4 casas de ese solar y jugador
        java.util.List<Edificio> borrar = new java.util.ArrayList<>();
        for (Edificio ed : pos.getEdificios()) {
            if (ed.getTipo() == Edificio.Tipo.CASA && ed.getSolar() == pos && ed.getPropietario() == actual) {
                borrar.add(ed);
                if (borrar.size() == 4) break;
            }
        }
        for (Edificio ed : borrar) {
            pos.eliminarEdificio(ed);
            actual.eliminarEdificio(ed);
            numeroPorEdificio.remove(ed.getId()); // si usas el mapa “tipo→número”
        }

        pos.setNumCasas(0);
        pos.setNumHoteles(pos.getNumHoteles() + 1);


        Edificio.Tipo tipo = Edificio.Tipo.HOTEL;
        Edificio e = new Edificio(nextEdificioId(), tipo, pos, actual);
        pos.anadirEdificio(e);
        actual.anadirEdificio(e);


        // asigna "hotel 1", "hotel 2", ...
        int precio= pos.getPrecioHotel();
        numeroPorEdificio.put(e.getId(), nextNumTipo(tipo));
        System.out.println("Se ha edificado un hotel en " + pos.getNombre() + ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");


    }

    public void edificarPiscina(){
        if(hayJugadores()){return;}
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        Casilla pos = actual.getAvatar().getPosicion();
        if(!Casilla.TSOLAR.equals(pos.getTipo())) {
            System.out.println("Sólo se puede edificar en casillas de tipo solar.\n");
            return;
        }
        if(!pos.getGrupo().esDuenoGrupo(actual)){
            System.out.println("No se puede edificar una casilla de un grupo que no es del jugador\n");
            return;
        }
        if(pos.getNumCasas()!=4){
            System.out.println("Aún no tienes las suficientes casas para comprar un hotel. Prueba a construír una casa!");
            return;
        }
        if (pos.getNumHoteles()==0){
            System.out.println("Aún no tienes un hotel. Prueba a construír uno!");
            return;
        }
        pos.setNumPiscinas(pos.getNumPiscinas() + 1);

        Edificio.Tipo tipo = Edificio.Tipo.PISCINA;
        Edificio e = new Edificio(nextEdificioId(), tipo, pos, actual);
        pos.anadirEdificio(e);
        actual.anadirEdificio(e);

        // asigna "piscina 1", "piscina 2", ...
        int precio= pos.getPrecioPiscina();
        numeroPorEdificio.put(e.getId(), nextNumTipo(tipo));
        System.out.println("Se ha edificado una piscina en " + pos.getNombre() + ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");


    }

    public void edificarPista(){
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        Casilla pos = actual.getAvatar().getPosicion();
        if(!Casilla.TSOLAR.equals(pos.getTipo())) {
            System.out.println("Sólo se puede edificar en casillas de tipo solar.\n");
            return;
        }
        if(!pos.getGrupo().esDuenoGrupo(actual)){
            System.out.println("No se puede edificar una casilla de un grupo que no es del jugador\n");
            return;
        }
        if(pos.getNumCasas()!=4){
            System.out.println("Aún no tienes las suficientes casas para comprar un hotel. Prueba a construír una casa!");
            return;
        }
        if (pos.getNumHoteles()==0){
            System.out.println("Aún no tienes un hotel. Prueba a construír uno!");
            return;
        }
        if(pos.getNumPiscinas()==0){
            System.out.println("Aún no tienes una piscina, prueba a construir una!");
            return;
        }

        pos.setNumPistas(pos.getNumPistas() + 1);

        Edificio.Tipo tipo = Edificio.Tipo.PISTA;
        Edificio e = new Edificio(nextEdificioId(), tipo, pos, actual);
        pos.anadirEdificio(e);
        actual.anadirEdificio(e);

        // asigna "pista 1", "pista 2", ...
        int precio=pos.getPrecioPiscina();
        numeroPorEdificio.put(e.getId(), nextNumTipo(tipo));
        System.out.println("Se ha edificado una pista en " + pos.getNombre() + ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");


    }

    // Menu.java
    public void listarEdificios(String color) {
        java.util.List<Edificio> todos = new java.util.ArrayList<>();
        if (jugadores != null && color==null) {
            for (Jugador j : jugadores) {
                if (j != null && j.getMisEdificios() != null) todos.addAll(j.getMisEdificios());
            }
        }
        if (jugadores != null && color != null) {
            for (Jugador j : jugadores) {
                if (j != null && j.getMisEdificios() != null) {
                    for (Edificio e : j.getMisEdificios()) {
                        String col = null;
                        Casilla s = e.getSolar();
                        if (s != null && s.getGrupo() != null) {
                            try { col = s.getGrupo().getColorGrupo(); }
                            catch (Throwable ignored) {
                                try { col = s.getGrupo().getColorGrupo(); } catch (Throwable ignored2) {}
                            }
                        }
                        if (col != null && col.equalsIgnoreCase(color)) {
                            todos.add(e); // solo añade los edificios cuya casilla es del color pedido
                        }
                    }
                }
            }
        }

        if (todos.isEmpty()) {
            System.out.println("{}");
            return;
        }

        for (int i = 0; i < todos.size(); i++) {
            Edificio e = todos.get(i);
            String tipo = e.getTipo().name().toLowerCase(); // casa, hotel, piscina, pista
            int numTipo = numeroPorEdificio.getOrDefault(e.getId(), 0);
            String idStr = tipo + "-" + numTipo;

            String propietario = (e.getPropietario() != null) ? e.getPropietario().getNombre() : "-";
            String casilla = (e.getSolar() != null) ? e.getSolar().getNombre() : "-";
            String grupo = "-";
            if (e.getSolar() != null && e.getSolar().getGrupo() != null) {
                try { grupo = e.getSolar().getGrupo().getColorGrupo(); }
                catch (Throwable t) {
                    try { grupo = e.getSolar().getGrupo().getColorGrupo(); } catch (Throwable ignored) {}
                }
            }

            long coste = costeConstruccion(e.getSolar(), e.getTipo());

            System.out.println("{");
            System.out.println("id: " + idStr + ",");
            System.out.println("propietario: " + propietario + ",");
            System.out.println("casilla: " + casilla + ",");
            System.out.println("grupo: " + grupo + ",");
            System.out.println("coste: " + coste);
            System.out.println(i < todos.size() - 1 ? "}," : "}");
        }
    }

    public long costeConstruccion(Casilla c, Edificio.Tipo t) {
        if (c == null) return 0;
        return switch (t) {
            case CASA -> c.getPrecioCasa();
            case HOTEL -> c.getPrecioHotel();
            case PISCINA -> c.getPrecioPiscina();
            case PISTA -> c.getPrecioPistaDeporte();
        };
    }


    private boolean tieneEdificios(Casilla c) {
        return c.getNumCasas() > 0 || c.getNumHoteles() > 0 || c.getNumPiscinas() > 0 || c.getNumPistas() > 0;
    }



    public void hipotecar(String nombreProp){
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(nombreProp);
        if (c == null) { System.out.println("No existe la casilla: " + nombreProp); return; }
        if (!(c.getDueno().equals(actual))) { System.out.println(actual.getNombre() + " no puede hipotecar " + nombreProp + ". No es una propiedad que le pertenece."); return; }
        if (c.gethipotecada()==1) { System.out.println(actual.getNombre() + " no puede hipotecar " + nombreProp + ". Ya está hipotecada."); return; }

        // Debe vender todos los edificios antes de hipotecar
        if (tieneEdificios(c)) {
            System.out.println("Antes de hipotecar la propiedad se deberán vender todos los edificios.");
            return;
        }

        int importe = c.getHipoteca();
        c.sethipotecada(1);
        actual.sumarFortuna(importe);

        String color = (c.getGrupo()!=null ? c.getGrupo().getColorGrupo() : "-");
        System.out.println(actual.getNombre() + " recibe " + importe + "€ por la hipoteca de " + c.getNombre() + ". No puede recibir alquileres ni edificar en el grupo " + color + ".");
    }

    public void deshipotecar(String nombreSolar) {
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(nombreSolar);
        if (c == null) { System.out.println("No existe la casilla: " + nombreSolar); return; }
        if (!(c.getDueno().equals(actual))) { System.out.println(actual.getNombre() + " no puede deshipotecar " + nombreSolar + ". No es una propiedad que le pertenece."); return; }
        if (c.gethipotecada()==0) { System.out.println(actual.getNombre() + " no puede hipotecar " + nombreSolar + ". No está hipotecada."); return; }

        int importe = c.getHipoteca();
        if (actual.getFortuna() < importe) {
            System.out.println("No tienes dinero suficiente para deshipotecar " + nombreSolar + ".");
            return;
        }
        actual.sumarGastos(importe);
        c.sethipotecada(0);

        String color = (c.getGrupo()!=null ? c.getGrupo().getColorGrupo() : "-");
        System.out.println(actual.getNombre() + " paga " + importe + "€ por deshipotecar " + c.getNombre() + ". Ahora puede recibir alquileres y edificar en el grupo " + color + ".");
    }




    private void eliminarEdificiosDe(Casilla c, Jugador propietario, Edificio.Tipo tipo, int n) {
        if (n <= 0) return;

        int quitados = 0;
        java.util.Iterator<Edificio> it = c.getEdificios().iterator();
        while (it.hasNext() && quitados < n) {
            Edificio e = it.next();
            if (e.getTipo() == tipo && e.getPropietario() == propietario) {
                it.remove();// quita de la casilla
                e.eliminar(); //elimina el edificio
                propietario.eliminarEdificio(e);// quita del jugador
                numeroPorEdificio.remove(e.getId()); // limpia numeración
                quitados++;
            }
        }

        // ajusta contadores del solar
        switch (tipo) {
            case CASA:    c.setNumCasas(Math.max(0, c.getNumCasas() - quitados)); break;
            case HOTEL:   c.setNumHoteles(Math.max(0, c.getNumHoteles() - quitados)); break;
            case PISCINA: c.setNumPiscinas(Math.max(0, c.getNumPiscinas() - quitados)); break;
            case PISTA:   c.setNumPistas(Math.max(0, c.getNumPistas() - quitados)); break;
        }
    }



    public void venderPropiedad(String tipo, String solar,  int cantidad){
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(solar);
        if (c == null) { System.out.println("No existe la casilla: " + solar); return; }
        if (!(c.getDueno().equals(actual))) { System.out.println("No se pueden vender " + tipo + " en " + c.getNombre() + ". Esta propiedad no pertenece a " + actual.getNombre() + "."); return; }

        String t = tipo.toLowerCase();
        switch (t) {
            case "casas":
                int vendidas = Math.min(cantidad, c.getNumCasas());
                if(c.getNumCasas()==0){
                    System.out.println("No hay casas en esta propiedad.");
                    return;
                }
                if (c.getNumCasas() < cantidad) {
                    System.out.println("No hay suficientes casas en esta propiedad.Se venderán " + c.getNumCasas()+ " casas. Recibiendo"+cantidad*c.getPrecioCasa());
                }
                if (c.getNumCasas() == cantidad) {
                    System.out.println("Se venden todas las casas de esta propiedad.Recibiendo"+cantidad*c.getPrecioCasa());
                }
                actual.sumarFortuna(vendidas * c.getPrecioCasa());
                c.setNumCasas(c.getNumCasas()-cantidad);
                eliminarEdificiosDe(c, actual, Edificio.Tipo.CASA, vendidas);
                break;

            case "pista":
                if(c.getNumPistas()==0){
                    System.out.println("No hay Pistas en esta propiedad");
                    return;
                }
                if (cantidad>1 && c.getNumPistas()==1){
                    System.out.println("Solamente se puede vender 1 pista, recibiendo"+c.getPrecioPistaDeporte());
                }
                if (c.getNumPistas()==1){
                    System.out.println("Vendiendo 1 pista, recibiendo"+c.getPrecioPistaDeporte());
                }
                actual.sumarFortuna(c.getPrecioPistaDeporte());
                c.setNumPistas(c.getNumPistas()- cantidad);
                eliminarEdificiosDe(c, actual, Edificio.Tipo.PISTA, 1);
                break;

            case "piscina":
                if(c.getNumPiscinas()==0){
                    System.out.println("No hay Piscinas en esta propiedad");
                    return;
                }
                if (cantidad>1 && c.getNumPiscinas()==1){
                    System.out.println("Solamente se puede vender 1 piscina, recibiendo"+c.getPrecioPiscina());
                }
                if (c.getNumPiscinas()==1){
                    System.out.println("Vendiendo 1 piscinas, recibiendo"+c.getPrecioPiscina());
                }
                actual.sumarFortuna(c.getPrecioPiscina());
                c.setNumPiscinas(c.getNumPiscinas() - cantidad);
                eliminarEdificiosDe(c, actual, Edificio.Tipo.PISCINA, 1);
                break;

            case "hoteles":
                if(c.getNumHoteles()==0){
                    System.out.println("No hay Hoteles en esta propiedad");
                    return;
                }
                if (cantidad>1 && c.getNumHoteles()==1){
                    System.out.println("Solamente se puede vender 1 piscina, recibiendo"+c.getPrecioHotel());
                }
                if (c.getNumHoteles()==1){
                    System.out.println("Vendiendo 1 hoteles, recibiendo"+c.getPrecioHotel());
                }
                actual.sumarFortuna(c.getPrecioHotel());
                c.setNumHoteles(c.getNumHoteles() - cantidad);
                eliminarEdificiosDe(c, actual, Edificio.Tipo.HOTEL, 1);
                break;
            default:
                System.out.println("Tipo de edificio no reconocido: " + tipo);
        }

    }


    public void procesarCasillaEspecial(Jugador jugador, String tipoCasilla) {
        if ("Suerte".equals(tipoCasilla) || "Comunidad".equals(tipoCasilla)) {
            Carta carta = Carta.sacarCarta(tipoCasilla);
            if (carta != null) {
                carta.ejecutar(jugador, this);
            }
        }
    }

    public void moverJugadorACasilla(Jugador jugador, String nombreCasilla, boolean cobrarSalida) {
        Casilla destino = tablero.encontrar_casilla(nombreCasilla);
        if (destino != null) {
            if (cobrarSalida && pasaPorSalida(jugador.getAvatar().getPosicion(), destino)) {
                jugador.sumarFortuna(2000000);
                jugador.getEstadisticas().sumarPasarPorSalida(2000000);
                System.out.println("¡Pasas por Salida! Cobras 2.000.000€");
            }
            jugador.getAvatar().setPosicion(destino);
            // Evaluar efectos de la nueva casilla
            destino.evaluarCasilla(jugador, this, 0);
        }
    }

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

    public Jugador[] getJugadores() {
        return jugadores.toArray(new Jugador[0]);
    }

    //Método para mostrar las estadísticas de un jugador
    public void estadisticasJugador(String nombreJugador){
        if(jugadores==null||jugadores.isEmpty()){
            System.out.println("No hay jugadores en la partida.");
            return;
        }
        Jugador jugador=null;
        for(Jugador j:jugadores){
            if(j.getNombre().equals(nombreJugador)){
                jugador=j;
                break;
            }
        }
        if(jugador==null){
            System.out.println("No existe el jugador: "+nombreJugador);
        }
        assert jugador != null;
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

    // Método para mostrar estadísticas generales del juego (Req. 24)
    public void estadisticasJuego() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        long totalInvertido = 0;
        long totalTasas = 0;
        long totalAlquileresPagados = 0;
        long totalAlquileresCobrados = 0;
        long totalSalida = 0;
        long totalPremios = 0;
        int totalCarcel = 0;

        Jugador masPaga = null;
        Jugador masCobra = null;
        Jugador masSalida = null;

        for (Jugador j : jugadores) {

            EstadisticasJugador e = j.getEstadisticas();
            totalInvertido += e.getDineroInvertido();
            totalTasas += e.getPagoTasasImpuestos();
            totalAlquileresPagados += e.getPagoDeAlquileres();
            totalAlquileresCobrados += e.getCobroDeAlquileres();
            totalSalida += e.getPasarPorCasillaDeSalida();
            totalPremios += e.getPremiosInversionesOBote();
            totalCarcel += e.getVecesEnLaCarcel();

            if (masPaga == null ||
                    e.getPagoTasasImpuestos() + e.getPagoDeAlquileres() >
                            masPaga.getEstadisticas().getPagoTasasImpuestos() + masPaga.getEstadisticas().getPagoDeAlquileres()) {
                masPaga = j;
            }

            if (masCobra == null ||
                    e.getCobroDeAlquileres() + e.getPremiosInversionesOBote() >
                            masCobra.getEstadisticas().getCobroDeAlquileres() + masCobra.getEstadisticas().getPremiosInversionesOBote()) {
                masCobra = j;
            }

            if (masSalida == null ||
                    e.getPasarPorCasillaDeSalida() >
                            masSalida.getEstadisticas().getPasarPorCasillaDeSalida()) {
                masSalida = j;
            }
        }

        // Imprimir estadisticas globales del juego
        System.out.println("=== ESTADÍSTICAS DE LA PARTIDA ===");

        System.out.println("Dinero invertido total: " + totalInvertido);
        System.out.println("Tasas/Impuestos totales pagados: " + totalTasas);
        System.out.println("Alquileres pagados totales: " + totalAlquileresPagados);
        System.out.println("Alquileres cobrados totales: " + totalAlquileresCobrados);
        System.out.println("Premios/Bote cobrados totales: " + totalPremios);
        System.out.println("Cantidad recibida al pasar por salida (total): " + totalSalida);
        System.out.println("Veces totales en la cárcel: " + totalCarcel);

        System.out.println("\n--- Jugadores destacados ---");
        System.out.println("Jugador que MÁS pagó (tasas + alquileres): " +
                masPaga.getNombre());

        System.out.println("Jugador que MÁS cobró (alquileres + premios): " +
                masCobra.getNombre());

        System.out.println("Jugador que MÁS pasó por la salida: " +
                masSalida.getNombre());

        // Ranking final por fortuna
        System.out.println("\n--- Ranking por Fortuna ---");
        jugadores.stream()
                .sorted((a, b) -> Long.compare(b.getFortuna(), a.getFortuna()))
                .forEach(j -> System.out.println(j.getNombre() + ": " + j.getFortuna()));
    }
    private long valorTotalJugador(Jugador j) {
        long total = j.getFortuna();

        for (Casilla c : j.getPropiedades()) {
            total += c.getValor();

            // edificios
            total += (long) c.getNumCasas() * c.getPrecioCasa();
            total += (long) c.getNumHoteles() * c.getPrecioHotel();
            total += (long) c.getNumPiscinas() * c.getPrecioPiscina();
            total += (long) c.getNumPistas() * c.getPrecioPistaDeporte();
        }

        return total;
    }

    private boolean hayJugadores() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return false;
        }
        return true;
    }



}




