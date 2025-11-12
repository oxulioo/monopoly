package monopoly;

public class MonopolyETSE {

    private static Juego juego = new Juego();
    private static Menu menu = new Menu(juego);
    static void main() {
        juego.iniciarPartida(); //iniciamos la partida
        menu.run();
    }

}
