package monopoly;

import monopoly.exceptions.MonopolyEtseException;

public class MonopolyETSE {

    private static final Juego juego = new Juego();
    private static final Menu menu = new Menu(juego);
    static void main(){
        juego.iniciarPartida(); //iniciamos la monopoly.partida
       try {
           menu.run();
       } catch (MonopolyEtseException _) {
       }
    }

}
