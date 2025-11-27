package monopoly;

import partida.Jugador;
// Importamos las excepciones necesarias
// import monopoly.DineroInsuficienteException;
// import monopoly.CasillaHipotecadaException;

public class Transporte extends Propiedad {

    // Array de multiplicadores de alquiler (para 1, 2, 3 o 4 transportes)
    private static final int[] MULTIPLIERS = {1, 2, 4, 8};

    public Transporte(String nombre, int posicion, Grupo grupo) {
        // Usamos la constante PRECIO_SERVICIO_TRANSPORTE (500.000€) para el valor de compra
        // La hipoteca se asume como la mitad (250.000€).
        super(nombre, posicion, Valor.PRECIO_SERVICIO_TRANSPORTE, Valor.PRECIO_SERVICIO_TRANSPORTE / 2, null);
        this.grupo = grupo; // El constructor de Propiedad no recibe Grupo en el código del usuario, lo seteamos aquí.
        if (grupo != null) {
            grupo.anhadirCasilla(this);
        }
    }

    // --- Implementación de métodos abstractos de Propiedad ---

    /**
     * Implementación de valor(). Devuelve el precio de compra del transporte.
     */
    @Override
    public float valor() {
        return (float) this.valor;
    }

    /**
     * Calcula y gestiona el pago de alquiler.
     * @param jugadorPagador El jugador que cae en la casilla y debe pagar.
     * @return true si el pago fue exitoso o no se debía pagar, false si falló por bancarrota.
     */
    @Override
    public boolean alquiler(Jugador jugadorPagador) {
        if (dueno == null || dueno.getNombre().equals("Banca") || dueno.equals(jugadorPagador)) {
            return true; // No hay alquiler que pagar
        }

        // 1. Calcular el importe
        long importeAlquiler = calcularAlquilerTransporte();

        System.out.println(jugadorPagador.getNombre() + " paga un alquiler de " + importeAlquiler + "€ a " + dueno.getNombre()
                + " por tener " + calcularNumeroTransportesDelDueno() + " transportes.");

        // 2. Realizar la transferencia (asumo que Jugador.sumarGastos gestiona la bancarrota)
        try {
            // Utilizamos el método de pago más detallado si existe para las estadísticas, sino usamos sumarGastos
            // if (!jugadorPagador.pagarAlquiler(importeAlquiler, dueno, this)) {
            if (!jugadorPagador.sumarGastos((int)importeAlquiler)) {
                // Si sumarGastos devuelve false, el jugador está en bancarrota
                System.out.println("ERROR: Dinero insuficiente. " + jugadorPagador.getNombre() + " debe gestionar su fortuna.");
                return false;
            } else {
                dueno.sumarFortuna((int)importeAlquiler);
                // Aquí se actualizarían las estadísticas de cobro/pago
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error al procesar el pago de alquiler en " + nombre + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Lógica principal al caer en la casilla.
     */
    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        // Lógica heredada:
        if (dueno != null && !dueno.equals(actual) && !dueno.getNombre().equals("Banca")) {
            if (hipotecada) {
                System.out.println(nombre + " está hipotecada. No pagas alquiler.");
            } else {
                alquiler(actual); // Llama al método de Transporte (con la lógica de múltiples transportes)
            }
        } else if (dueno == null || dueno.getNombre().equals("Banca")) {
            System.out.println("Estás en " + nombre + ". Pertenece a la Banca. Valor de compra: " + valor);
            // La compra se hará explícitamente con el comando "comprar".
        }
    }

    // --- Lógica de Alquiler Específica de Transporte ---

    /**
     * Cuenta el número de casillas de tipo Transporte que posee el dueño.
     */
    private int calcularNumeroTransportesDelDueno() {
        if (this.dueno == null) return 0;

        int transportesPoseidos = 0;
        // Recorremos las propiedades del dueño para contar los transportes
        for (Casilla c : this.dueno.getPropiedades()) {
            if (c instanceof Transporte) {
                transportesPoseidos++;
            }
        }
        return transportesPoseidos;
    }

    /**
     * Calcula el alquiler basándose en el número de transportes que posee el dueño.
     * @return El importe del alquiler.
     */
    public long calcularAlquilerTransporte() {
        int transportes = calcularNumeroTransportesDelDueno();

        // El índice es (transportes - 1)
        int index = Math.min(transportes, MULTIPLIERS.length) - 1;

        if (index < 0) return 0; // Si no tiene ningún transporte (nunca debería ocurrir si se llama desde la misma casilla)

        int multiplicador = MULTIPLIERS[index];

        // Valor.ALQUILER_TRANSPORTE es el alquiler base (ej: 250.000€)
        return (long) Valor.ALQUILER_TRANSPORTE * multiplicador;
    }

    // --- Método toString ---
    @Override
    public String toString() {
        String infoPropiedad = super.toString();
        String tipoStr = "Transporte";

        // Usamos la tabla de multiplicadores para mostrar la información del alquiler
        String alquilerInfo = String.format(
                "Alquiler: { 1: %d€, 2: %d€, 3: %d€, 4: %d€ }",
                Valor.ALQUILER_TRANSPORTE * MULTIPLIERS[0],
                Valor.ALQUILER_TRANSPORTE * MULTIPLIERS[1],
                Valor.ALQUILER_TRANSPORTE * MULTIPLIERS[2],
                Valor.ALQUILER_TRANSPORTE * MULTIPLIERS[3]
        );

        // Ajustamos la salida del grupo/tipo
        String res = infoPropiedad.replace("grupo:", "tipo:");
        res = res.replace("No aplica", tipoStr);

        return String.format("%s, %s",
                res.substring(0, res.lastIndexOf('}')),
                alquilerInfo
        ) + "}";
    }
}