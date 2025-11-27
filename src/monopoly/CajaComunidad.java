package monopoly;

import partida.Jugador;

public class CajaComunidad extends Accion {

    public CajaComunidad(int posicion) {
        super("Caja", posicion); // Mantenemos el nombre "Caja" para coincidir con tu lógica de Tablero/Carta
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        // LÓGICA MOVIDA DE CASILLA.JAVA (Caso TCOMUNIDAD)
        System.out.println(actual.getNombre() + " cae en Caja de Comunidad.");
        // Delegamos en Juego la gestión de la carta
        juego.procesarCasillaEspecial(actual, "Comunidad");
    }

    @Override
    public String toString() {
        return "{\n" +
                "tipo: comunidad\n" +
                "}";
    }
}
