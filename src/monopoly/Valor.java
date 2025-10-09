package monopoly;


/*
public class Valor {
    //Se incluyen una serie de constantes útiles para no repetir valores.
    public static final float FORTUNA_BANCA = 500000; // Cantidad que tiene inicialmente la Banca
    public static final float FORTUNA_INICIAL = 9543076.28f; // Cantidad que recibe cada jugador al comenzar la partida
    public static final float SUMA_VUELTA = 1301328.584f; // Cantidad que recibe un jugador al pasar pos la Salida
    */
    public final class Valor{
        private Valor() {}
        //Para evitar errores de desbordamiento pongo L (long)
        public static final long FORTUNA_INICIAL = 15000000L;
        public static final long SUMA_VUELTA = 2000000L;
        public static final long PRECIO_SALIR_CARCEL = 500000L;
        public static final long IMPUESTO_FIJO = 2000000L;

        public static final long FACTOR_SERVICIO = 50000L;
        public static final long ALQUILER_TRANSPORTE = 250000L;
        public static final long FORTUNA_BANCA = Long.MAX_VALUE; //Dado que la fortuna de la banca es ilimitada, le doy como valor el mayor número que cabe en un long con signo en Java.
    }
    
    //Colores del texto:
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

}
