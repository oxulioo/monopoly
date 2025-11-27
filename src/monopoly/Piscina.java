/*package monopoly;
import partida.Jugador;

public class Piscina extends Edificio {

    public Piscina(long id, Casilla solar, Jugador propietario) {
        super(id, solar, propietario);
    }

    @Override
    public long getPrecioCompra() {
        // Obtenemos el precio de la Piscina del Solar asociado
        return getSolar().getPrecioPiscina();
    }

    @Override
    protected void decrementarContadorSolar(Casilla solar) {
        // Decrementa el contador de piscinas del solar
        if (solar.getNumPiscinas() > 0) solar.setNumPiscinas(solar.getNumPiscinas() - 1);
    }

    @Override
    public String getTipoNombre() {
        return "Piscina";
    }
    // toString() se hereda de Edificio.java
}*/