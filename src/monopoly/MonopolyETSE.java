package monopoly;

import monopoly.exceptions.MonopolyEtseException;

public class MonopolyETSE {

    private static final Juego juego = new Juego();
    private static final Menu menu = new Menu(juego);
    static void main() throws MonopolyEtseException {
        juego.iniciarPartida(); //iniciamos la partida
       try {
           menu.run();
       } catch (MonopolyEtseException e) {
          return;
       }
    }

}
