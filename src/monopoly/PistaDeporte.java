/*package monopoly;
import partida.Jugador;

public class PistaDeporte extends Edificio {

    public PistaDeporte(long id, Casilla solar, Jugador propietario) {
        super(id, solar, propietario);
    }

    @Override
    public long getPrecioCompra() {
        // Obtenemos el precio de la Pista del Solar asociado
        return getSolar().getPrecioPistaDeporte();
    }

    @Override
    protected void decrementarContadorSolar(Casilla solar) {
        // Decrementa el contador de pistas del solar
        if (solar.getNumPistas() > 0) solar.setNumPistas(solar.getNumPistas() - 1);
    }

    @Override
    public String getTipoNombre() {
        return "PistaDeporte";
    }
    // toString() se hereda de Edificio.java
}*/