package monopoly;

import partida.Jugador;

public class MonopolyETSE {

    public static Menu menu = new Menu();

    public static void main(String[] args) {
        menu.iniciarPartida();
        menu.run();
    }
    /*ESTO ES LO QUE TENIA ANTES
    public static void main(String[] args) {
        new Menu();
    } //arranca el juego, crea banca, tablero, dados y estado.
    */
}
