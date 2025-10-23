package monopoly;

import partida.Jugador;

public class MonopolyETSE {


    public static Menu menu = new Menu(){
    };

    public static void main(String[] args) {
        menu.iniciarPartida(); //iniciamos la partida
        menu.run();
    }

}
