package monopoly;

import java.sql.SQLOutput;
import java.util.ArrayList;
import partida.*;

public class Menu {

    // region ==== ATRIBUTOS ====

    //Atributos
    private ArrayList<Jugador> jugadores; //Jugadores de la partida en orden de alta.
    private ArrayList<Avatar> avatares; //Avatares en la partida.
    private int turno; //Índice correspondiente a la posición en el arrayList del jugador (y el avatar) que tienen el turno
    private int lanzamientos; //Variable para contar el número de lanzamientos de un jugador en un turno.
    private Tablero tablero; //Tablero en el que se juega.
    private Dado dado1; //Dos dados para lanzar y avanzar casillas.
    private Dado dado2;
    private Jugador banca; //El jugador banca.
    private boolean tirado; //Booleano para comprobar si el jugador que tiene el turno ha tirado o no.
    private boolean solvente; //Booleano para comprobar si el jugador que tiene el turno es solvente, es decir, si ha pagado sus deudas.
    private int doblesConsecutivos = 0; // para contar dobles en el mismo turno
    private int suma;
    // endregion

    public Jugador getBanca(){return banca;}

    // region ==== MÉTODOS ====

    public Menu() {
        // Banca (según el guion: Jugador() vacío actúa como banca, sin avatar y con fortuna muy alta)
        banca = new Jugador();
    }

    // Imprime el tablero (usa toString() del Tablero)
    private void verTablero() {
        if (tablero == null) {
            System.out.println("No hay tablero cargado.");
            return;
        }
        System.out.print(tablero);
    }


    // Método para inciar una partida: crea los jugadores y avatares.

    public void iniciarPartida() {
        // Estructuras y estado base
        jugadores    = new ArrayList<>();
        avatares     = new ArrayList<>();
        turno        = 0;
        lanzamientos = 0;
        tirado       = false;
        solvente     = true;

        // Dados
        dado1 = new Dado();
        dado2 = new Dado();



        // Tablero: la banca empieza como propietaria de todo

        tablero = new Tablero(banca);
    }

    /*Método que interpreta el comando introducido y toma la accion correspondiente.
     * Parámetro: cadena de caracteres (el comando).
     */
    private void analizarComando(String comando) {
        if (comando == null) return;
        if (comando.isEmpty()) return;

        // Exige exactamente "comandos " (en minúsculas) al principio
        if (comando.startsWith("comandos ")) {
            String ruta = comando.substring(9); // NO trim: se usa tal cual
            ejecutarFichero(ruta);
            return;
        }
        // crear jugador <Nombre> <tipoAvatar>  (sin trim ni validación)
        if (comando.startsWith("crear jugador ")) {
            String resto = comando.substring("crear jugador ".length());
            int idx = resto.lastIndexOf(' ');
            if (idx <= 0 || idx == resto.length() - 1) {
                System.out.println("Uso: crear jugador <Nombre> <tipoAvatar>");
                return;
            }
            String nombre = resto.substring(0, idx);
            String tipo   = resto.substring(idx + 1);
            crearJugador(nombre, tipo);
            return;
        }

        // jugador: muestra quién tiene el turno
        if (comando.equals("jugador")) {
            mostrarJugadorActual();
            return;
        }

        //listar jugadores
        if (comando.equals("listar jugadores")) {
            listarJugadores();
            return;
        }

        // lanzar dados
        if (comando.equals("lanzar dados")) {
            lanzarDados();
            return;
        }
        if (comando.startsWith("lanzar dados ")) {
            String resto = comando.substring("lanzar dados ".length()); // p.ej. "3+4"
            int mas = resto.indexOf('+');
            if (mas > 0 && mas < resto.length() - 1) {
                String s1 = resto.substring(0, mas);
                String s2 = resto.substring(mas + 1);
                try {
                    int d1 = Integer.parseInt(s1);
                    int d2 = Integer.parseInt(s2);
                    lanzarDadosForzado(d1, d2);
                    return;
                } catch (NumberFormatException nfe) {
                    System.out.println("Uso: lanzar dados X+Y  (X e Y enteros)");
                    return;
                }
            }
            // si no trae '+', dejará pasar al caso normal (lanzar dados)
        }


        // acabar turno: pasa el turno al siguiente jugador
        if (comando.equals("acabar turno")) {
            acabarTurno();
            return;
        }

        // salir cárcel: pagas 500.000 y quedas libre
        if (comando.equals("salir carcel") || comando.equals("salir cárcel")) {
            salirCarcel();
            return;
        }

        // describir jugador <Nombre>
        if (comando.startsWith("describir jugador ")) {
            String nombreArg = comando.substring("describir jugador ".length());
            descJugador(nombreArg);
            return;
        }

        // describir avatar <ID>
        if (comando.startsWith("describir avatar ")) {
            String id = comando.substring("describir avatar ".length()); // tal cual
            descAvatar(id);
            return;
        }

        // listar avatares
        if (comando.equals("listar avatares")) {
            listarAvatares();
            return;
        }


        // describir <Casilla>
        if (comando.startsWith("describir ")) {
            // si más adelante añades 'describir jugador ...', ese tendrá su propio if antes
            String nombreCasilla = comando.substring("describir ".length());
            descCasilla(nombreCasilla);
            return;
        }

        // comprar <Propiedad>
        if (comando.startsWith("comprar ")) {
            String nombreProp = comando.substring("comprar ".length());

            comprar(nombreProp);
            return;
        }

        // listar en venta
        if (comando.equals("listar enventa")) {
            listarVenta();
            return;
        }

        //  ver tablero
        if (comando.equals("ver tablero")) {
            verTablero();
            return;
        }

    }

    // Muestra quién tiene el turno actual (por índice 'turno')
    private void mostrarJugadorActual() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores. Crea uno con: crear jugador <Nombre> <tipoAvatar>");
            return;
        }
        // por si acaso, normalizamos el índice de turno
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();

        Jugador actual = jugadores.get(turno);

        System.out.println("Tiene el turno: " + actual.getNombre());
    }

    private void ejecutarFichero(String ruta) {
        try (java.util.Scanner sc = new java.util.Scanner(new java.io.File(ruta))) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                analizarComando(linea);
            }
        } catch (Exception e) {
            System.out.println("Error leyendo: " + ruta);
        }
    }

    // Crea Jugador+Avatar en "Salida" y repinta el tablero
    private void crearJugador(String nombre, String tipoAvatar) {
        // 1) Buscar casilla de inicio ("Salida")
        Casilla salida = null;
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
    private void listarJugadores() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        for (Jugador j : jugadores) {
            // nombre
            String nombre = j.getNombre();

            // avatar: "tipo (ID)" si existe; "-" si no
            String avatarStr = "-";
            Avatar a = j.getAvatar();
            if (a != null) {
                String tipo = a.getTipo();
                char id   = a.getID();
                avatarStr = tipo + " (" + id + ")";
            }

            String fortunaStr = String.format("%d",j.getFortuna());
            // propiedades: lista de nombres o "-"
            String propiedadesStr = "-";
            java.util.List<Casilla> props = j.getPropiedades();
            if (props != null && !props.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < props.size(); i++) {
                    Casilla c = props.get(i);
                    sb.append(c.getNombre());
                    if (i < props.size() - 1) sb.append(", ");
                }
                propiedadesStr = sb.toString();
            }

            // salida pedida en enunciado (en Parte 1 hipotecas y edificios son "-")
            System.out.println("Jugador: " + nombre);
            System.out.println("  Avatar: " + avatarStr);
            System.out.println("  Fortuna: " + fortunaStr);
            System.out.println("  Propiedades: " + propiedadesStr);
            System.out.println("  Hipotecas: -");
            System.out.println("  Edificios: -");
            System.out.println();
        }
    }

    /*Método que realiza las acciones asociadas al comando 'describir jugador'.
     * Parámetro: comando introducido
     */
    // Imprime toda la info del jugador pedido en Parte 1
    private void descJugador(String nombreBuscado) {
        if (nombreBuscado == null || nombreBuscado.isEmpty()) {
            System.out.println("Uso: describir jugador <Nombre>");
            return;
        }
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        // Buscar por nombre exacto
        Jugador j = null;
        for (Jugador x : jugadores) {
            if (x.getNombre().equals(nombreBuscado)) {
                j = x; break;
            }
        }
        if (j == null) {
            System.out.println("No existe el jugador: " + nombreBuscado);
            return;
        }

        // Datos básicos
        String nombre = j.getNombre();

        // Avatar
        String avatarStr = "-";
        Avatar a = j.getAvatar();
        if (a != null) {
            String tipo = a.getTipo();
            char id   = a.getID();
            avatarStr = tipo + " (" + id + ")";
        }

        // Fortuna

        String fortunaStr = String.format("%d", j.getFortuna());
        //Posicion actual
        String posicionStr = "-";
        Casilla pos = null;
        if (a != null) pos = a.getPosicion();
        if (pos == null) {
            try { pos = a.getPosicion(); } catch (Throwable ignored) {}
        }
        if (pos != null) posicionStr = pos.getNombre();

        // Propiedades
        String propiedadesStr = "-";
        java.util.List<Casilla> props = j.getPropiedades();
        if (props != null && !props.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < props.size(); i++) {
                sb.append(props.get(i).getNombre());
                if (i < props.size() - 1) sb.append(", ");
            }
            propiedadesStr = sb.toString();
        }



        // ¿En cárcel? ( 'Sí/No')
        String carcelStr = j.isEnCarcel() ? "Sí" : "No";




        // Salida formateada (Hipotecas y Edificios se muestran como “-”)
        System.out.println("Jugador: " + nombre);
        System.out.println("  Avatar: " + avatarStr);
        System.out.println("  Fortuna: " + fortunaStr);
        System.out.println("  Posición: " + posicionStr);
        System.out.println("  Propiedades: " + propiedadesStr);
        System.out.println("  Hipotecas: -");
        System.out.println("  Edificios: -");
        System.out.println("  En cárcel: " + carcelStr);
        System.out.println();
    }


    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
     * Parámetro: id del avatar a describir.
     */
    private void descAvatar(String ID) {
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
    private void descCasilla(String nombre) {
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
            catch (Throwable ignore) { System.out.println(String.valueOf(c)); }
        }
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'lanzar dados'.
    public int lanzarDados() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores. Crea uno con: crear jugador <Nombre> <tipoAvatar>");
            return 0;
        }
        if (lanzamientos==1){
            System.out.println("Ya has tirado una vez, no puedes volver a tirar.");
            return 0;
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
                return 0;
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
                return 0;

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
                return 0;
            }

            // Evaluar efectos de la casilla (pagos, etc.)
            if (c != null) {
                c.evaluarCasilla(actual, banca, suma);
            }
        } catch (Throwable e) {
            System.out.println("(Aviso) Falta implementar correctamente el movimiento del Avatar.");
            return 0;
        }

        // Repintar tablero
        System.out.println(tablero);

        // Si sacó dobles (y no eran 3), puede volver a lanzar en este turno
        if (esDoble) {
            System.out.println(actual.getNombre() + " ha sacado dobles y puede volver a lanzar.");
            tirado = false; // permitir otra tirada
        }

    return suma;
    }



    //  lanzar dados X+Y (forzado)
    private void lanzarDadosForzado(int d1, int d2) {
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

            // Evaluar efectos de la casilla (pagos, etc.)
            if (c != null) {
                c.evaluarCasilla(actual, banca, suma);
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
    private void comprar(String nombre) {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        // 1) Buscar la casilla por nombre
        Casilla cas = null;
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
        if (propietario != null && propietario != banca) {
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
        cas.comprarCasilla(actual, banca);

        // Mensajes y repintado
        System.out.println(actual.getNombre() + " compra '" + cas.getNombre() + "' por " + (long)precio + ".");
        System.out.println("La fortuna actual de " + actual.getNombre() + " es: " + actual.getFortuna()+".");
        try { System.out.println(tablero); } catch (Throwable ignored) {}
    }


    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'.
    private static final int COSTE_SALIR_CARCEL = 500000;

    private void salirCarcel() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }
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
        actual.sumarGastos(COSTE_SALIR_CARCEL);
        actual.salirCarcel();

        // 4) Mensaje + repintado del tablero (si toString() está implementado)
        System.out.println(actual.getNombre() + " paga " + COSTE_SALIR_CARCEL + " y sale de la cárcel.");
        try { System.out.println(tablero); } catch (Throwable ignored) {}
    }


    private void listarVenta() {
        ArrayList<Casilla> enVenta = new ArrayList<>();

        for (ArrayList<Casilla> lado : tablero.getPosiciones()) { // recorres cada lado del tablero
            for (Casilla c : lado) {                              // recorres cada casilla del lado
                if (c.getDueno() == banca &&
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

    private void listarAvatares() {
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

            System.out.println("{ id: " + id + ", tipo: " + tipo + ", jugador: " + jugadorNombre + ", posicion: " + pos + " }");
        }
    }


    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    private void acabarTurno() {
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
    private void enviarACarcel(Jugador j) {
        if (j == null || tablero == null) return;
        Casilla carcel = tablero.encontrar_casilla("Cárcel");
        if (carcel == null) carcel = tablero.encontrar_casilla("Carcel"); // por si no hay tilde
        j.encarcelar(carcel);
        try {
            Avatar a = j.getAvatar();
            if (a != null) a.setPosicion(carcel);
        } catch (Throwable ignored) {}
    }


    // Lector de comandos por consola
    public void run() {
        java.util.Scanner sc = new java.util.Scanner(System.in);
        System.out.println("Monopoly listo. Escribe comandos. (\"salir\" para terminar)");
        System.out.println("Ejemplos: ver tablero | crear jugador Ana coche | lanzar dados | comprar Solar1");

        while (true) {
            System.out.print("> ");
            if (!sc.hasNextLine()) break;
            String linea = sc.nextLine().trim();
            if (linea.isEmpty()) continue;
            if (linea.equalsIgnoreCase("salir")) {
                System.out.println("¡Hasta luego!");
                break;
            }
            try {
                analizarComando(linea);
            } catch (Exception e) {
                System.out.println("Error procesando comando: " + e.getMessage());
            }
        }
    }

    // endregion

}



