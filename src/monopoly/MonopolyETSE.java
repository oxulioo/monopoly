package monopoly;

import partida.Jugador;

public class MonopolyETSE {


    // FIXME: crear una banca universal para ahorrar constructores y mejorar gestion y eso se hace metiendola dentro del menu y por lo tanto con universales que sean banca menu y tablero
    public static Menu menu = new Menu(){
    };

    public static void main(String[] args) {
        menu.iniciarPartida(); //iniciamos la partida
        menu.run();
    }

}
