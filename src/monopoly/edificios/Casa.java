package monopoly.edificios;
import monopoly.casilla.Solar;
import monopoly.jugador.Jugador;
public class Casa extends Edificio {
    public Casa(String id, Solar solar, Jugador propietario) {
        super(id, Tipo.CASA, solar, propietario);
    }

    @Override
    public String toString() {
        return "Casa{id='" + id + "', solar=" + solar.getNombre() + "}";
    }
}