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
    //lleva la cuenta de cuántas casas, cuántos hoteles, etc., se han construido en total en el juego para poder darles un número secuencial
    private final java.util.EnumMap<Edificio.Tipo,Integer> secTipo =
            new java.util.EnumMap<>(Edificio.Tipo.class); //mapa de tipo enumerado que guarda los tipos
    private final java.util.Map<Long,Integer> numeroPorEdificio = new java.util.HashMap<>(); //guarda parejas de datos, clave->valor

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
        int n = secTipo.getOrDefault(t, 0) + 1; //suma 1 al que hay en el mapa, si no hay toma el 0
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

        // 4) Mensaje y “repintar” tablero
        System.out.println("Creado jugador '" + nombre + "' con avatar '" + tipoAvatar + "' en Salida.");
        try { System.out.println(tablero); } catch (Throwable ignored) {}
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    public void listarJugadores() {
        if(!hayJugadores()){return;}
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
        if(!hayJugadores()){return;}

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
        if(!hayJugadores()){return;}
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

        // Si sacó dobles, puede volver a lanzar en este turno
        if (esDoble) {
            System.out.println(actual.getNombre() + " ha sacado dobles y puede volver a lanzar.");
            tirado = false; // permitir otra tirada
        }

    }



    //  lanzar dados X+Y (forzado)
    public void lanzarDadosForzado(int d1, int d2) {
        if(!hayJugadores()){return;}
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
                c.evaluarCasilla(actual, this, suma);
            }
        } catch (Throwable e) {
            System.out.println("(Aviso) Falta implementar correctamente el movimiento del Avatar.");
            return;
        }


        // Repintar tablero
        try { System.out.println(tablero); } catch (Throwable ignore) {}

        // Si sacó dobles, puede volver a lanzar
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
        if(!hayJugadores()){return;}
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

        // Actualizar estadísticas de inversión
        if (cas.getDueno() == actual) {
            actual.getEstadisticas().sumarDineroInvertido((long)precio);
        }

        // Mensajes y repintado
        System.out.println(actual.getNombre() + " compra '" + cas.getNombre() + "' por " + (long)precio + ".");
        System.out.println("La fortuna actual de " + actual.getNombre() + " es: " + actual.getFortuna()+".");
        System.out.println(tablero);
    }


    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'.
    public void salirCarcel() {
        if(!hayJugadores()){return;}
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
        if (fortuna < Valor.PRECIO_SALIR_CARCEL) {
            System.out.println("No tienes suficiente dinero para salir de la cárcel.");
            return;
        }

        // 3) Pagar a la banca y marcar como libre
        boolean ok= actual.sumarGastos(Valor.PRECIO_SALIR_CARCEL);
        if(ok) actual.getEstadisticas().sumarPagoTasasImpuestos(Valor.PRECIO_SALIR_CARCEL);

        actual.salirCarcel();

        // 4) Mensaje + repintado del tablero (si toString() está implementado)
        System.out.println(actual.getNombre() + " paga " + Valor.PRECIO_SALIR_CARCEL + " y sale de la cárcel.");
        try { System.out.println(tablero); } catch (Throwable ignored) {}
    }


    public void listarVenta() {
        ArrayList<Casilla> enVenta = new ArrayList<>();

        for (ArrayList<Casilla> lado : tablero.getPosiciones()) { // recorres cada lado del tablero
            for (Casilla c : lado) {                              // recorres cada casilla del lado
                if (c.getDueno() == this.getBanca() && c.getTipo().equalsIgnoreCase("Solar") || c.getTipo().equalsIgnoreCase("Transporte") || c.getTipo().equalsIgnoreCase("Servicios")) {
                    //Lo añado a la lista de casillas en venta
                    enVenta.add(c);
                }
            }
        }

        // ahora imprimimos las casillas en venta, llamando a describir Casilla
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
        if(!hayJugadores()){return;}
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


    public void enviarACarcel(Jugador j) {
        if (j == null || tablero == null) return;
        //Busco la casilla de la cárcel
        Casilla carcel = tablero.encontrar_casilla("Cárcel");
        if (carcel == null) carcel = tablero.encontrar_casilla("Carcel"); // por si no hay tilde
        //Encarcelo
        j.encarcelar();

        try {
            Avatar a = j.getAvatar();
            if (a != null) a.setPosicion(carcel);
        } catch (Throwable ignored) {}
    }


    public void edificarCasa(){
        if(!hayJugadores()){return;}//Si no hay jugadores no se puede edificar
        if (turno < 0) turno = 0;//Si le turno está mal ajustado se reinicia o se ajusta
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);
        //Sacamos la casilla a la que pertenece el avatar
        Casilla pos = actual.getAvatar().getPosicion();
        //Si no es solar no se puede edificar
        if(!Casilla.TSOLAR.equals(pos.getTipo())) {
            System.out.println("Sólo se puede edificar en casillas de tipo solar.\n");
            return;
        }
        //Si el que quiere edificar no es dueño del grupo completo de la casilla no puede edificar
        if(!pos.getGrupo().esDuenoGrupo(actual)){
            System.out.println("No se puede edificar una casilla de un grupo que no es del jugador\n");
            return;
        }
        //Si pasa el máximo de casas no puede
        if (pos.getNumCasas()>=4){
            System.out.println("Ya tienes el máximo de casas en este solar. Prueba a construir un hotel!\n");
            return;

        }
        //Cojo el precio, compruebo que tiene suficiente dinero
        int precio = pos.getPrecioCasa();
        if (actual.getFortuna() < precio) {
            System.out.println("No tienes suficiente dinero para construir una casa");
            return;
        }


        // Restar dinero de la fortuna
        actual.sumarGastos(precio);
        // Actualizar estadísticas de inversión
        actual.getEstadisticas().sumarDineroInvertido(precio);
        //Actualizar numero de casas
        pos.setNumCasas(pos.getNumCasas() + 1);

        // Crear y registrar el edificio
        Edificio.Tipo tipo = Edificio.Tipo.CASA; //Establece el tipo (enum) a edificar, casa
        Edificio e = new Edificio(nextEdificioId(), tipo, pos, actual); //Crea una nueva instancia de Edificio con su constructor
        pos.anadirEdificio(e);//Añade el edificio a la lista de edificios de la propiedad
        actual.anadirEdificio(e);//Añade el edificio a la lista de edificios del jugador

        // Asigna número de casa
        numeroPorEdificio.put(e.getId(), nextNumTipo(tipo));//Mapa que asigna el Id con el numero secuencial unico para cada edificio

        System.out.println("Se ha edificado una casa en " + pos.getNombre() + ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
    }


    public void edificarHotel(){
        if(!hayJugadores()){return;}
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
        int precio = pos.getPrecioHotel();
        if (actual.getFortuna() < precio) {
            System.out.println("No tienes suficiente dinero para construir un hotel");
            return;
        }
        actual.sumarGastos(precio);
        // elimina 4 casas de ese solar y jugador
        java.util.List<Edificio> borrar = new java.util.ArrayList<>();//Lista temporal borrar que almacena los edificios a borrar
        for (Edificio ed : pos.getEdificios()) {//Recorro todos los edificios de la propiedad actual
            if (ed.getTipo() == Edificio.Tipo.CASA && ed.getSolar() == pos && ed.getPropietario() == actual) { //comprobamos que estamos iterando sobre el solar y con dicho propietario correctamente
                borrar.add(ed); //borramos la casa si construimos un hotel (la añadimos a la lista de eliminacion)
                if (borrar.size() == 4) break; //borra hasta 4 casas que es el maximo
            }
        }
        //Recorro la lista de edificios a borrar y los borro
        for (Edificio ed : borrar) { // borramos las casas
            pos.eliminarEdificio(ed);
            actual.eliminarEdificio(ed);
            numeroPorEdificio.remove(ed.getId()); // borramos el numero asociado al edificio
        }
        //Reestablezco el numero de casas
        pos.setNumCasas(0);

        pos.setNumHoteles(pos.getNumHoteles() + 1);

        //Asigno tipo hotel del enum
        Edificio.Tipo tipo = Edificio.Tipo.HOTEL;
        Edificio e = new Edificio(nextEdificioId(), tipo, pos, actual);//Creo la nueva instancia
        pos.anadirEdificio(e);//Añado a lista de edificios de la propiedad
        actual.anadirEdificio(e);//Añado a lista de edificios del jugador



        // Actualizar estadísticas de inversión
        actual.getEstadisticas().sumarDineroInvertido(precio);
        numeroPorEdificio.put(e.getId(), nextNumTipo(tipo)); // Asigna "hotel 1", "hotel 2", ...
        System.out.println("Se ha edificado un hotel en " + pos.getNombre() + ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");


    }

    public void edificarPiscina(){
        if(!hayJugadores()){return;}
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

        if (pos.getNumHoteles()==0){
            System.out.println("Aún no tienes un hotel. Prueba a construír uno!");
            return;
        }
        int precio = pos.getPrecioPiscina();
        if (actual.getFortuna() < precio) {
            System.out.println("No tienes suficiente dinero para construir una piscina");
            return;
        }
        actual.sumarGastos(precio);

        pos.setNumPiscinas(pos.getNumPiscinas() + 1);

        Edificio.Tipo tipo = Edificio.Tipo.PISCINA;
        Edificio e = new Edificio(nextEdificioId(), tipo, pos, actual);
        pos.anadirEdificio(e);
        actual.anadirEdificio(e);


        // Actualizar estadísticas de inversión
        actual.getEstadisticas().sumarDineroInvertido(precio);
        numeroPorEdificio.put(e.getId(), nextNumTipo(tipo)); // asigna "piscina 1", "piscina 2", ...
        System.out.println("Se ha edificado una piscina en " + pos.getNombre() + ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");


    }

    public void edificarPista(){
        if(!hayJugadores()){return;}
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

        if (pos.getNumHoteles()==0){
            System.out.println("Aún no tienes un hotel. Prueba a construír uno!");
            return;
        }
        if(pos.getNumPiscinas()==0){
            System.out.println("Aún no tienes una piscina, prueba a construir una!");
            return;
        }
        int precio = pos.getPrecioPistaDeporte();
        if (actual.getFortuna() < precio) {
            System.out.println("No tienes suficiente dinero para construir una pista");
            return;
        }
        actual.sumarGastos(precio);

        pos.setNumPistas(pos.getNumPistas() + 1);

        Edificio.Tipo tipo = Edificio.Tipo.PISTA;
        Edificio e = new Edificio(nextEdificioId(), tipo, pos, actual);
        pos.anadirEdificio(e);
        actual.anadirEdificio(e);



        // Actualizar estadísticas de inversión
        actual.getEstadisticas().sumarDineroInvertido(precio);
        numeroPorEdificio.put(e.getId(), nextNumTipo(tipo)); // asigna "pista 1", "pista 2", ...
        System.out.println("Se ha edificado una pista en " + pos.getNombre() + ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");


    }


    public void listarEdificios(String color) {
        //listar edificios sin color
        if (color == null) {
            //Si no se especifica color, se recolectan todos los edificios del juego en el array
            java.util.List<Edificio> todos = new java.util.ArrayList<>();
            if (jugadores != null) {
                for (Jugador j : jugadores) {
                    if (j != null && j.getMisEdificios() != null) todos.addAll(j.getMisEdificios());
                }
            }
            //Si no hay edificios
            if (todos.isEmpty()) {
                System.out.println("{}");
                return;
            }

            for (int i = 0; i < todos.size(); i++) {
                //Recorro todos los edificios cogiendo sus propiedades a imprimir y las imprimo
                Edificio e = todos.get(i);
                String tipo = e.getTipo().name().toLowerCase(); // casa, hotel, piscina, pista
                int numTipo = numeroPorEdificio.getOrDefault(e.getId(), 0); // coge o el numero o si no puede un numero por defecto que se asigno como 0
                String idStr = tipo + "-" + numTipo;

                String propietario = (e.getPropietario() != null) ? e.getPropietario().getNombre() : "-";
                String casilla = (e.getSolar() != null) ? e.getSolar().getNombre() : "-";
                String grupo = "-";
                if (e.getSolar() != null && e.getSolar().getGrupo() != null) {
                    try { grupo = e.getSolar().getGrupo().getColorGrupo(); }
                    catch (Throwable _) { }
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
            return;
        }
        //empezamos con listado por color
        Grupo grupo = tablero.getGrupos().get(color);
        if (grupo == null) {
            // Intenta buscar por color mayúsculas
            String colorCapitalizado = Character.toUpperCase(color.charAt(0)) + color.substring(1).toLowerCase();
            grupo = tablero.getGrupos().get(colorCapitalizado);
            if (grupo == null) {
                System.out.println("No existe el grupo de color: " + color);
                return;
            }
        }

    // banderas (para después imprimir lo que se podría y lo que no se podría construir)
        boolean puedeCasa = true;
        boolean puedeHotel = true;
        boolean puedePiscina = true;
        boolean puedePista = true;
        int maxCasas = 0;
        int maxHoteles = 0;
        int maxPiscinas = 0;
        int maxPistas = 0;

        // Iteramos sobre las propiedades del grupo
        for (Casilla c : grupo.getMiembros()) {
            if (c == null) continue;
            System.out.println("{");

            System.out.println("propiedad: " + c.getNombre() + ",");

            // Listar casas
            System.out.print("  casas: ");
            listarEdificiosPorTipo(c, Edificio.Tipo.CASA);

            // Listar hoteles
            System.out.print("  hoteles: ");
            listarEdificiosPorTipo(c, Edificio.Tipo.HOTEL);

            // Listar piscinas
            System.out.print("  piscinas: ");
            listarEdificiosPorTipo(c, Edificio.Tipo.PISCINA);

            // Listar pistas
            System.out.print("  pistasDeDeporte: ");
            listarEdificiosPorTipo(c, Edificio.Tipo.PISTA);

            // Calcular alquiler actual
            long alquilerActual = c.getAlquiler(); // Alquiler base
            //Vamos sumando a la base dependiendo de los edificios
            if (c.getNumCasas() > 0) alquilerActual = (long) c.getNumCasas() * c.getAlquilerCasa();
            if (c.getNumHoteles() > 0) alquilerActual += (long) c.getNumHoteles() * c.getAlquilerHotel();
            if (c.getNumPiscinas() > 0) alquilerActual += (long) c.getNumPiscinas() * c.getAlquilerPiscina();
            if (c.getNumPistas() > 0) alquilerActual += (long) c.getNumPistas() * c.getAlquilerPistaDeporte();

            System.out.println("  alquiler: " + alquilerActual);

            //contamos edificios
            if (c.getNumCasas() == 4) maxCasas++;
            if (c.getNumHoteles() == 1) maxHoteles++;
            if (c.getNumPiscinas()==1) maxPiscinas++;
            if (c.getNumPistas()==1) maxPistas++;

        }

        int numPropiedadesGrupo = grupo.getMiembros().size();
        if (maxCasas == numPropiedadesGrupo) { //comparamos con el numero de propiedades de ese grupo de color
            puedeCasa = false; // No más casas si todas tienen 4
        }
        if (maxHoteles == numPropiedadesGrupo) {
            puedeHotel = false; // No más hoteles si todas tienen 1
            if (maxCasas == 0) puedeCasa = false; // Tampoco casas (si ya hay hoteles)
        }
        if (maxPiscinas == numPropiedadesGrupo) {
            puedePiscina=false; //No mas piscinas si en cada una hay una
        }
        if (maxPistas==numPropiedadesGrupo) {
            puedePista = false; //No mas pistas si en cada una hay una
        }


        //Teniendo en cuenta las variables anteriores, imprimimos por pantalla que se puede construir en cada grupo
        if (!puedeCasa||!puedeHotel||!puedePiscina||!puedePista) {
            System.out.println("Ya no puedes construír:\n");
        }
        if (!puedeCasa) {
            System.out.println("   -casas: no se pueden construir casas, ya que todos tienen 4 casas o 1 hotel.\n");
        }
        if (!puedeHotel) {
            System.out.println("   -hoteles: no se pueden construir hoteles, ya que todos tienen 1 hotel.\n");
        }
        if (!puedePiscina) {
            System.out.println("   -piscinas: no se pueden construir piscinas, ya que todos tienen 1 piscina.\n");
        }
        if (!puedePista) {
            System.out.println("   -pistas de deporte: no se pueden construir pistas de deporte, ya que todos tienen 1 pista de deporte.\n");
        }
        if (puedeCasa||puedeHotel||puedePiscina||puedePista) {
            System.out.println("Aun puedes construír:\n");
        }
        if(puedeCasa){
            System.out.println("   -casas\n");
        }
        if(puedeHotel){
            System.out.println("   -hoteles\n");
        }
        if(puedePiscina){
            System.out.println("   -piscinas\n");
        }
        if(puedePista){
            System.out.println("   -pistas de deporte\n");
        }
        System.out.println("}");


    }

    /**
     * Método para 'listarEdificios(String color)'
     * Imprime la lista de edificios de un tipo para una casilla.
     */
    private void listarEdificiosPorTipo(Casilla c, Edificio.Tipo tipo) {
        ArrayList<String> nombresEdificios = new ArrayList<>();
        for (Edificio e : c.getEdificios()) {
            if (e.getTipo() == tipo) {
                int numTipo = numeroPorEdificio.getOrDefault(e.getId(), 0); //si no encuentra el numero o no esta toma el 0
                nombresEdificios.add(tipo.name().toLowerCase() + "-" + numTipo);
            }
        }

        if (nombresEdificios.isEmpty()) {
            System.out.println("-"); //si esta vacio imprime -
        } else {
            System.out.println("[" + String.join(", ", nombresEdificios) + "]");
        }
    }

    public long costeConstruccion(Casilla c, Edificio.Tipo t) { //funcion que me recupera el precio de cada uno de los edificios
        if (c == null) return 0;
        return switch (t) {
            case CASA -> c.getPrecioCasa();
            case HOTEL -> c.getPrecioHotel();
            case PISCINA -> c.getPrecioPiscina();
            case PISTA -> c.getPrecioPistaDeporte();
        };
    }


    private boolean tieneEdificios(Casilla c) { //función que me dice si hay algo contruído en dicha casilla
        return c.getNumCasas() > 0 || c.getNumHoteles() > 0 || c.getNumPiscinas() > 0 || c.getNumPistas() > 0;
    }



    public void hipotecar(String nombreProp){
        Jugador actual = jugadores.get(turno); //quien tiene el turno
        Casilla c = this.tablero.encontrar_casilla(nombreProp); //buscamos la casilla
        //miramos que exista, le pertenezca y aun no este hipotecada
        if (c == null) { System.out.println("No existe la casilla: " + nombreProp); return; }
        if (!(c.getDueno().equals(actual))) { System.out.println(actual.getNombre() + " no puede hipotecar " + nombreProp + ". No es una propiedad que le pertenece."); return; }
        if (c.gethipotecada()==1) { System.out.println(actual.getNombre() + " no puede hipotecar " + nombreProp + ". Ya está hipotecada."); return; }

        // Debes vender todos los edificios antes de hipotecar
        if (tieneEdificios(c)) {
            System.out.println("Antes de hipotecar la propiedad se deberán vender todos los edificios.");
            return;
        }

        int importe = c.getHipoteca();
        c.sethipotecada(1); //bandera a 1
        actual.sumarFortuna(importe); //recibimos dinero

        String color = (c.getGrupo()!=null ? c.getGrupo().getColorGrupo() : "-");
        System.out.println(actual.getNombre() + " recibe " + importe + "€ por la hipoteca de " + c.getNombre() + ". No puede recibir alquileres ni edificar en el grupo " + color + ".");
    }

    public void deshipotecar(String nombreSolar) {
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(nombreSolar);
        if (c == null) { System.out.println("No existe la casilla: " + nombreSolar); return; }
        if (!(c.getDueno().equals(actual))) { System.out.println(actual.getNombre() + " no puede deshipotecar " + nombreSolar + ". No es una propiedad que le pertenece."); return; }
        if (c.gethipotecada()==0) { System.out.println(actual.getNombre() + " no puede deshipotecar " + nombreSolar + ". No está hipotecada."); return; }

        int importe = c.getHipoteca();
        if (actual.getFortuna() < importe) {
            System.out.println("No tienes dinero suficiente para deshipotecar " + nombreSolar + ".");
            return;
        }
        actual.sumarGastos(importe); //pagamos
        c.sethipotecada(0); //ponemos la bandera a 0

        String color = (c.getGrupo()!=null ? c.getGrupo().getColorGrupo() : "-");
        System.out.println(actual.getNombre() + " paga " + importe + "€ por deshipotecar " + c.getNombre() + ". Ahora puede recibir alquileres y edificar en el grupo " + color + ".");
    }


    //función que elimina edificios
    private void eliminarEdificiosDe(Casilla c, Jugador propietario, Edificio.Tipo tipo, int n) {
        if (n <= 0) return;

        int quitados = 0;
        // copia de la lista
        java.util.List<Edificio> copiaEdificios = new java.util.ArrayList<>(c.getEdificios());

        // buscamos el edificio a eliminar
        for (Edificio e : copiaEdificios) {
            if (quitados >= n) break;

            if (e.getTipo() == tipo && e.getPropietario() == propietario) {
                //eliminamos usando el propio metofo
                e.eliminar();

                // eliminamos el edificio de la lista y su número
                numeroPorEdificio.remove(e.getId());

                quitados++;
            }
        }
    }



    public void venderPropiedad(String tipo, String solar,  int cantidad){
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(solar);
        if (c == null) {
            System.out.println("No existe la casilla: " + solar);
            return;
        }
        if (!(c.getDueno().equals(actual))) {
            System.out.println("No se pueden vender " + tipo + " en " + c.getNombre() + ". Esta propiedad no pertenece a " + actual.getNombre() + "."); return; }

        String t = tipo.toLowerCase();
        switch (t) { //filtramos por tipo
            case "casas": //miramos el numero de las casas y comparamos con el que queremos vender
                int vendidas = Math.min(cantidad, c.getNumCasas());
                if(c.getNumCasas()==0){
                    System.out.println("No hay casas en esta propiedad.");
                    return;
                }
                if (c.getNumCasas() < cantidad && c.getNumCasas()!=0) {
                    System.out.println("No hay suficientes casas en esta propiedad.Se venderán " + c.getNumCasas() + " casas. Recibiendo " + c.getNumCasas()*c.getPrecioCasa());
                }
                if (c.getNumCasas() == cantidad && c.getNumCasas()!=0) {
                    System.out.println("Se venden todas las casas de esta propiedad.Recibiendo " + cantidad*c.getPrecioCasa());
                }
                actual.sumarFortuna(vendidas * c.getPrecioCasa());
                c.setNumCasas(c.getNumCasas()-cantidad);
                eliminarEdificiosDe(c, actual, Edificio.Tipo.CASA, vendidas);
                break;

            case "pista": //miramos el numero de las pistas y comparamos con el que queremos vender
                if(c.getNumPistas()==0){
                    System.out.println("No hay Pistas en esta propiedad");
                    return;
                }
                if (cantidad>1 && c.getNumPistas()==1){
                    System.out.println("Solamente se puede vender 1 pista, recibiendo"+c.getPrecioPistaDeporte());
                }
                if (c.getNumPistas()==1){
                    System.out.println("Vendiendo 1 pista, recibiendo "+c.getPrecioPistaDeporte());
                }
                actual.sumarFortuna(c.getPrecioPistaDeporte());
                c.setNumPistas(c.getNumPistas()- cantidad);
                eliminarEdificiosDe(c, actual, Edificio.Tipo.PISTA, 1);
                break;

            case "piscina"://miramos el numero de las piscinas y comparamos con el que queremos vender
                if(c.getNumPiscinas()==0){
                    System.out.println("No hay Piscinas en esta propiedad");
                    return;
                }
                if (cantidad>1 && c.getNumPiscinas()==1){
                    System.out.println("Solamente se puede vender 1 piscina, recibiendo "+c.getPrecioPiscina());
                }
                if (c.getNumPiscinas()==1){
                    System.out.println("Vendiendo 1 piscinas, recibiendo "+c.getPrecioPiscina());
                }
                actual.sumarFortuna(c.getPrecioPiscina());
                c.setNumPiscinas(c.getNumPiscinas() - cantidad);
                eliminarEdificiosDe(c, actual, Edificio.Tipo.PISCINA, 1);
                break;

            case "hoteles": //miramos el numero de las piscinas y comparamos con el que queremos vender
                if(c.getNumHoteles()==0){
                    System.out.println("No hay Hoteles en esta propiedad");
                    return;
                }
                if (cantidad>1 && c.getNumHoteles()==1){
                    System.out.println("Solamente se puede vender 1 hotel, recibiendo "+c.getPrecioHotel());
                    c.setNumHoteles(c.getNumHoteles()-1);
                }
                if (c.getNumHoteles()==1){
                    System.out.println("Vendiendo 1 hoteles, recibiendo "+c.getPrecioHotel());
                    c.setNumHoteles(c.getNumHoteles() - 1);
                }
                actual.sumarFortuna(c.getPrecioHotel());
                eliminarEdificiosDe(c, actual, Edificio.Tipo.HOTEL, 1);
                break;
            default:
                System.out.println("Tipo de edificio no reconocido: " + tipo);
        }

    }


    public void procesarCasillaEspecial(Jugador jugador, String tipoCasilla) {
        if ("Suerte".equals(tipoCasilla) || "Comunidad".equals(tipoCasilla)) {
            Carta carta = Carta.sacarCarta(tipoCasilla); //sacamos una carta aleatoria dependiendo de la baraja, de suerte o de comunidad
            if (carta != null) {
                carta.ejecutar(jugador, this); //ejecutamos la carta
            }
        }
    }

    //funcion interna que me mueve el avatar hacia adelante
    public void moverJugadorACasilla(Jugador jugador, String nombreCasilla, boolean cobrarSalida) {
        //Busco la casilla de destino
        Casilla destino = tablero.encontrar_casilla(nombreCasilla);
        if (destino != null) {
            //Si puedes cobrar por la salida y pasas, cobras
            if (cobrarSalida && pasaPorSalida(jugador.getAvatar().getPosicion(), destino)) {
                jugador.sumarFortuna(2000000); //si pasas por la salida cobras
                jugador.getEstadisticas().sumarPasarPorSalida(2000000);
                System.out.println("¡Pasas por Salida! Cobras 2.000.000€");
            }
            //Muevo al jugador a la casilla de destino
            jugador.getAvatar().setPosicion(destino); //muevo el avatar y miro que acciones tengo que hacer en dicha
            // Evaluar efectos de la nueva casilla
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

    public Jugador[] getJugadores() {
        return jugadores.toArray(new Jugador[0]);
    }

    //Método para mostrar las estadísticas de un jugador
    public void estadisticasJugador(String nombreJugador){
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

    public void estadisticasJuego() {
        if(!hayJugadores()){return;}

        //variables
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
                if (c.getDineroGenerado() > maxRentabilidadCasilla) {
                    maxRentabilidadCasilla = c.getDineroGenerado();
                    masRentable = c;
                }

                // B. Casilla más frecuentada
                if (c.getVecesVisitada() > maxFrecuencia) {
                    maxFrecuencia = c.getVecesVisitada();
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

            // E. Jugador en cabeza (usa tu método valorTotalJugador)
            long fortunaTotal = valorTotalJugador(j);
            if (fortunaTotal > maxFortunaTotal) {
                maxFortunaTotal = fortunaTotal;
                enCabeza = j;
            }
        }

        System.out.println("estadisticas");
        System.out.println("{");
        System.out.println("  casillaMasRentable: " + (masRentable != null ? masRentable.getNombre() : "-") + ",");
        System.out.println("  grupoMasRentable: " + (masRentableGrupo != null ? masRentableGrupo.getColorGrupo() : "-") + ",");
        System.out.println("  casillaMasFrecuentada: " + (masFrecuentada != null ? masFrecuentada.getNombre() : "-") + ",");
        System.out.println("  jugadorMasVueltas: " + (masVueltas != null ? masVueltas.getNombre() : "-") + ",");
        System.out.println("  jugadorEnCabeza: " + (enCabeza != null ? enCabeza.getNombre() : "-"));
        System.out.println("}");
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




