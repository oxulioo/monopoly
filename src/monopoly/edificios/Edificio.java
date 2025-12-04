package monopoly.edificios;
import monopoly.casilla.Solar;
import monopoly.jugador.Jugador;


public abstract class Edificio {
    public enum Tipo {
        CASA, HOTEL, PISCINA, PISTA
    }

    protected String id;
    protected Tipo tipo;
    protected Solar solar;
    protected Jugador propietario;

    public Edificio(String id, Tipo tipo, Solar solar, Jugador propietario) {
        this.id = id;
        this.tipo = tipo;
        this.solar = solar;
        this.propietario = propietario;
    }

    public String getId() { return id; }
    public Tipo getTipo() { return tipo; }
    public Solar getSolar() { return solar; }
    public Jugador getPropietario() { return propietario; }

    public abstract long getCosteConstruccion();
    public abstract long getAlquiler();

    public void setSolar(Solar solar) { this.solar = solar; }
    public void setPropietario(Jugador propietario) { this.propietario = propietario; }
}