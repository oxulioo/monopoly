package monopoly;


/*
public class Valor {
    //Se incluyen una serie de constantes útiles para no repetir valores.
    public static final int FORTUNA_BANCA = 500000; // Cantidad que tiene inicialmente la Banca
    public static final int FORTUNA_INICIAL = 9543076.28f; // Cantidad que recibe cada jugador al comenzar la partida
    public static final int SUMA_VUELTA = 1301328.584f; // Cantidad que recibe un jugador al pasar pos la Salida
    */
public final class Valor {

    private Valor(){}//Esto evita que se use new Valor()

    public static final int FORTUNA_INICIAL = 15000000;
    public static final int SUMA_VUELTA = 2000000;
    public static final int PRECIO_SALIR_CARCEL = 500000;
    public static final int IMPUESTO_FIJO = 2000000;

    public static final int FACTOR_SERVICIO = 50000;
    public static final int ALQUILER_TRANSPORTE = 250000;
    public static final int FORTUNA_BANCA = Integer.MAX_VALUE;
    //Dado que la fortuna de la banca es ilimitada, le doy como valor el mayor número que cabe en un int con signo en Java.
    public static final int PRECIO_SERVICIO_TRANSPORTE = 500000; //Precio de compra de dichas casillas

    //Colores del texto:
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[40m";
    public static final String RED = "\u001B[41m";
    public static final String GREEN = "\u001B[42m";
    public static final String YELLOW = "\u001B[43m";
    public static final String BLUE = "\u001B[44m";
    public static final String PURPLE = "\u001B[45m";
    public static final String CYAN = "\u001B[46m";
    public static final String WHITE = "\u001B[47m";

/*
    public static final String On_Black="\[\033[40m\]";
            On_Red="\[\033[41m\]"         # Red
            On_Green="\[\033[42m\]"       # Green
            On_Yellow="\[\033[43m\]"      # Yellow
            On_Blue="\[\033[44m\]"        # Blue
            On_Purple="\[\033[45m\]"      # Purple
            On_Cyan="\[\033[46m\]"        # Cyan
            On_White="\[\033[47m\]"       # White
*/
}
