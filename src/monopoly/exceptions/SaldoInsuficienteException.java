package monopoly.exceptions;

public class SaldoInsuficienteException extends FinancialException {
    public SaldoInsuficienteException(String nombreJugador, long cantidadFaltante) {
        super("El jugador " + nombreJugador + " no tiene saldo suficiente. Le faltan " + cantidadFaltante + "€.");
    }

}