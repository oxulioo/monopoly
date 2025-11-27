/*package monopoly;
import partida.Jugador;

public class Casa extends Edificio {

    public Casa(long id, Casilla solar, Jugador propietario) {
        super(id, solar, propietario);
    }

    @Override
    public long getPrecioCompra() {
        // Obtenemos el precio de la Casa del Solar asociado
        return getSolar().getPrecioCasa();
    }

    @Override
    protected void decrementarContadorSolar(Casilla solar) {
        // Decrementa el contador de casas del solar
        if (solar.getNumCasas() > 0) solar.setNumCasas(solar.getNumCasas() - 1);
    }

    @Override
    public String getTipoNombre() {
        return "Casa";
    }
    // toString() se hereda de Edificio.java
}*/