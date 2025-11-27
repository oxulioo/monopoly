package monopoly;

import partida.Jugador;
// Importamos las excepciones necesarias, asumiendo que están en el paquete 'monopoly'
// import monopoly.DineroInsuficienteException;

public class Servicio extends Propiedad {

    // El grupo de Servicios normalmente se crea para agrupar las casillas.
    public Servicio(String nombre, int posicion, Grupo grupo) {
        // Usamos la constante PRECIO_SERVICIO_TRANSPORTE (500.000€) para el valor de compra.
        // La hipoteca se asume como la mitad (250.000€).
        super(nombre, posicion, Valor.PRECIO_SERVICIO_TRANSPORTE, Valor.PRECIO_SERVICIO_TRANSPORTE / 2, null);
        this.grupo = grupo; // El constructor de Propiedad no recibe Grupo en el código del usuario, lo seteo aquí.
        if (grupo != null) {
            grupo.anhadirCasilla(this);
        }
    }

    // --- Implementación de métodos abstractos de Propiedad ---

    /**
     * Implementación de valor(). Devuelve el precio de compra del servicio.
     */
    @Override
    public float valor() {
        return (float) this.valor;
    }

    /**
     * Implementación del método abstracto alquiler(Jugador j).
     * En Servicios, la lógica real de cobro (que necesita la tirada) se gestiona
     * en evaluarCasilla. Este método se deja como indicador de fallo si se llama
     * sin contexto.
     */
    @Override
    public boolean alquiler(Jugador j) {
        System.out.println("ADVERTENCIA: Se intentó calcular alquiler de Servicio sin la tirada de dados.");
        return false;
    }

    /**
     * Lógica clave: calcula y gestiona el pago de alquiler en base a la tirada.
     * @param actual El jugador que cae en la casilla.
     * @param juego La instancia de Juego.
     * @param tirada El valor total de los dados.
     */
    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        // 1. Si no tiene dueño (Banca)
        if (dueno == null || dueno.getNombre().equals("Banca")) {
            System.out.println("Estás en " + nombre + ". Pertenece a la Banca. Valor de compra: " + valor);
            return;
        }

        // 2. Si el dueño soy yo
        if (dueno.equals(actual)) {
            System.out.println("Estás en tu propiedad, " + nombre + ".");
            return;
        }

        // 3. Si tiene dueño (y no soy yo) -> Pagar alquiler
        if (hipotecada) {
            System.out.println(nombre + " está hipotecada. No pagas alquiler.");
            return;
        }

        // Lógica de cálculo y pago
        try {
            long importeAlquiler = calcularAlquilerServicio(tirada);

            System.out.println(actual.getNombre() + " paga un alquiler de " + importeAlquiler + "€ a " + dueno.getNombre());

            // Intento de pago. Asumo que Jugador.sumarGastos gestiona la excepción/bancarrota
            if (!actual.sumarGastos((int)importeAlquiler)) {
                // Si falla el pago, se maneja la bancarrota (o venta de propiedades)
                System.out.println("ERROR: Dinero insuficiente. " + actual.getNombre() + " debe gestionar su fortuna.");
            } else {
                dueno.sumarFortuna((int)importeAlquiler); // El dueño recibe el dinero
                // Aquí se actualizarían las estadísticas de cobro/pago
            }

        } catch (Exception e) {
            System.out.println("Error al procesar el pago de alquiler en " + nombre + ": " + e.getMessage());
        }
    }

    // --- Lógica de Alquiler Específica de Servicio ---

    /**
     * Calcula el alquiler basándose en el valor de la tirada de dados y el número de
     * Servicios que posee el dueño.
     * FÓRMULA: (Tirada * FACTOR_SERVICIO) * (1 o 2, según servicios poseídos)
     * @param tirada El valor total de los dados.
     * @return El importe del alquiler.
     */
    public long calcularAlquilerServicio(int tirada) {
        if (this.dueno == null) return 0;

        int serviciosPoseidos = 0;
        // Cuento cuántos servicios tiene el dueño
        for (Casilla c : this.dueno.getPropiedades()) {
            if (c instanceof Servicio) {
                serviciosPoseidos++;
            }
        }

        // Factor multiplicador: 1 si 1 servicio, 2 si 2 servicios
        int factorMultiplicador = (serviciosPoseidos == 2) ? 2 : 1;

        // Valor.FACTOR_SERVICIO = 50000
        long alquilerBase = (long) tirada * Valor.FACTOR_SERVICIO;

        return alquilerBase * factorMultiplicador;
    }

    // --- Método toString ---
    @Override
    public String toString() {
        // Reutilizamos la info base de Propiedad
        String infoPropiedad = super.toString();
        String tipoStr = "Servicio";

        // Alquiler con 1 servicio: Tirada * 50.000€
        // Alquiler con 2 servicios: Tirada * 100.000€
        String alquilerInfo = String.format("Alquiler: { 1 Servicio: Tirada * %d€, 2 Servicios: Tirada * %d€ }",
                Valor.FACTOR_SERVICIO,
                Valor.FACTOR_SERVICIO * 2);

        // Ajustamos la salida del grupo/tipo para que se muestre como 'Servicio'
        String res = infoPropiedad.replace("grupo:", "tipo:");
        res = res.replace("No aplica", tipoStr);

        return String.format("%s, %s",
                res.substring(0, res.lastIndexOf('}')),
                alquilerInfo
        ) + "}";
    }
}