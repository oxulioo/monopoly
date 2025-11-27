/*package monopoly;
import partida.Jugador;

public class Hotel extends Edificio {

    public Hotel(long id, Casilla solar, Jugador propietario) {
        super(id, solar, propietario);
    }

    @Override
    public long getPrecioCompra() {
        // Obtenemos el precio del Hotel del Solar asociado
        return getSolar().getPrecioHotel();
    }

    @Override
    protected void decrementarContadorSolar(Casilla solar) {
        // Decrementa el contador de hoteles del solar
        if (solar.getNumHoteles() > 0) solar.setNumHoteles(solar.getNumHoteles() - 1);
    }

    @Override
    public String getTipoNombre() {
        return "Hotel";
    }
    // toString() se hereda de Edificio.java
}*/