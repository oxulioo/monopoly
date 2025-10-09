package monopoly;

import java.util.ArrayList;
import partida.*;

public class Menu {

    //Atributos
    private ArrayList<Jugador> jugadores; //Jugadores de la partida.
    private ArrayList<Avatar> avatares; //Avatares en la partida.
    private int turno = 0; //Índice correspondiente a la posición en el arrayList del jugador (y el avatar) que tienen el turno
    private int lanzamientos; //Variable para contar el número de lanzamientos de un jugador en un turno.
    private Tablero tablero; //Tablero en el que se juega.
    private Dado dado1; //Dos dados para lanzar y avanzar casillas.
    private Dado dado2;
    private Jugador banca; //El jugador banca.
    private boolean tirado; //Booleano para comprobar si el jugador que tiene el turno ha tirado o no.
    private boolean solvente; //Booleano para comprobar si el jugador que tiene el turno es solvente, es decir, si ha pagado sus deudas.

    // === Arranque ===
    public Menu() {
        iniciarPartida();
    }



    // Método para inciar una partida: crea los jugadores y avatares.
    private void iniciarPartida() {
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

        // Banca (según el guion: Jugador() vacío actúa como banca, sin avatar y con fortuna muy alta)
        banca = new Jugador();

        // Tablero: la banca empieza como propietaria de todo
        // (El propio constructor de Tablero debe asignar la propiedad inicial a banca)
        tablero = new Tablero(banca);
    }
    
    /*Método que interpreta el comando introducido y toma la accion correspondiente.
    * Parámetro: cadena de caracteres (el comando).
    */
    private void analizarComando(String comando) {
        if (comando == null) return;
        //comando = comando.trim();
        if (comando.isEmpty()) return;

        // Exige exactamente "comandos " (en minúsculas) al principio
        if (comando.startsWith("comandos ")) {
            String ruta = comando.substring(9); // NO trim: se usa tal cual
            ejecutarFichero(ruta);
            return;
        }

        // Aquí irán el resto de comandos
        System.out.println("[NO IMPLEMENTADO] " + comando);
    }

    private void ejecutarFichero(String ruta) {
        java.io.File f = new java.io.File(ruta);
        if (!f.exists()) {                         // chequeo simple (opcional)
            System.out.println("Error leyendo: " + ruta);
            return;
        }
        try (java.util.Scanner sc = new java.util.Scanner(f)) {  // <- try-with-resources
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();      // sin trim, como pediste
                analizarComando(linea);
            }
        } catch (java.io.IOException e) {          // errores de E/S
            System.out.println("Error leyendo: " + ruta);
        }
    }


    /*Método que realiza las acciones asociadas al comando 'describir jugador'.
    * Parámetro: comando introducido
     */
    private void descJugador(String[] partes) {
    }

    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
    * Parámetro: id del avatar a describir.
    */
    private void descAvatar(String ID) {
    }

    /* Método que realiza las acciones asociadas al comando 'describir nombre_casilla'.
    * Parámetros: nombre de la casilla a describir.
    */
    private void descCasilla(String nombre) {
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'lanzar dados'.
    private void lanzarDados() {
    }

    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
    * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    private void comprar(String nombre) {
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'. 
    private void salirCarcel() {
    }

    // Método que realiza las acciones asociadas al comando 'listar enventa'.
    private void listarVenta() {
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    private void listarJugadores() {
    }

    // Método que realiza las acciones asociadas al comando 'listar avatares'.
    private void listarAvatares() {
    }

    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    private void acabarTurno() {
    }

}
