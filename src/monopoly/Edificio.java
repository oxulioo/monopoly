package monopoly;
import partida.Jugador;

public final class Edificio {
    public enum Tipo { CASA, HOTEL, PISCINA, PISTA }

    private final long id;
    private final Tipo tipo;
    private final Casilla solar;
    private final Jugador propietario;
    private final int turnoConstruccion;

    public Edificio(long id, Tipo tipo, Casilla solar, Jugador propietario, int turno) {
        this.id = id; this.tipo = tipo; this.solar = solar; this.propietario = propietario; this.turnoConstruccion = turno;
    }
    public long getId() { return id; }
    public Tipo getTipo() { return tipo; }
    public Casilla getSolar() { return solar; }
    public Jugador getPropietario() { return propietario; }
    public int getTurnoConstruccion() { return turnoConstruccion; }
}

