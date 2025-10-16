package monopoly;

import partida.Avatar;
import partida.Jugador;

import java.util.ArrayList;


public class Casilla {

    //Atributos:
    //Primero pongo los privados (da igual)
    private String nombre; //Nombre de la casilla
    private String tipo; //Tipo de casilla (Solar, Especial, Transporte, Servicios, Comunidad, Suerte y Impuesto).
    private float valor; //Valor de esa casilla (en la mayoría será valor de compra, en la casilla parking se usará como el bote).
    private int posicion; //Posición que ocupa la casilla en el tablero (entero entre 1 y 40).
    private Jugador duenho; //Dueño de la casilla (por defecto sería la banca).
    private Grupo grupo; //Grupo al que pertenece la casilla (si es solar).
    private float alquiler; //Cantidad a pagar por caer en la casilla: el alquiler en solares/servicios/transportes o impuestos.
    private float hipoteca; //Valor otorgado por hipotecar una casilla
    private ArrayList<Avatar> avatares; //Avatares que están situados en la casilla.

    //Diferentes tipos de casilla, podría utilizar un tipo enumerado, pero como más adelante se modificará la práctica, trabajo con string
    public static final String TSOLAR = "Solar";
    public static final String TESPECIAL = "Especial";
    public static final String TTRANSPORTE = "Transporte";
    public static final String TSERVICIOS = "Servicios";
    public static final String TCOMUNIDAD = "Comunidad";
    public static final String TSUERTE = "Suerte";
    public static final String TIMPUESTO = "Impuesto";


    //Constructores:
    public Casilla() {
    }//Parámetros vacíos

    /*Constructor para casillas tipo Solar, Servicios o Transporte:
     * Parámetros: nombre casilla, tipo (debe ser solar, serv. o transporte), posición en el tablero, valor y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador duenho) {
        if (!(TSOLAR.equals(tipo) || TSERVICIOS.equals(tipo) || TTRANSPORTE.equals(tipo))) {//Si no es ninguno de los tipos mencionados, da error
            System.out.println("Tipo erróneo, debe ser 'Solar', 'Servicios' o 'Transporte'");
            //Comprobar si está bien creado el jugador, y sino no lo inserto en el arrayList
        }
        if (posicion < 1 || posicion > 40) {
            System.out.println("La posición debe estar entre 1 y 40");//No hay más de 40 casillas, trato el caso en el que se introduzca un valor no válido
        }


        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.valor = Math.max(0f, valor);//En caso en que se de un valor negativo, se toma el 0 para evitar errores
        this.duenho = duenho;
        //Inicializo los demás valores para que no dé error después
        this.alquiler = 0;
        this.hipoteca = 0;
        this.grupo = null;
        this.avatares = new ArrayList<>();
    }

    /*Constructor utilizado para inicializar las casillas de tipo IMPUESTOS.
     * Parámetros: nombre, posición en el tablero, impuesto establecido y dueño.
     */
    public Casilla(String nombre, int posicion, float alquiler, Jugador duenho) {
        if (posicion < 1 || posicion > 40) {
            System.out.println("La posición debe estar entre 1 y 40");//No hay más de 40 casillas, trato el caso en el que se introduzca un valor no válido
        }
        this.nombre = nombre;
        this.posicion = posicion;
        this.alquiler = Math.max(0f, alquiler);//En caso de valores negativos, se toma el 0
        this.duenho = duenho;
        this.tipo = TIMPUESTO;
        //Inicializo el resto de valores
        this.valor = 0;
        this.hipoteca = 0;
        this.grupo = null;
        this.avatares = new ArrayList<>();
    }

    /*Constructor utilizado para crear las otras casillas (Suerte, Caja de comunidad y Especiales):
     * Parámetros: nombre, tipo de la casilla (será uno de los que queda), posición en el tablero y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, Jugador duenho) {

        if (!(TSUERTE.equals(tipo) || TCOMUNIDAD.equals(tipo) || TESPECIAL.equals(tipo))) {
            System.out.println("Tipo erróneo, debe ser 'Suerte', 'Comunidad' o 'Especial'");
        }
        if (posicion < 1 || posicion > 40) {
            System.out.println("La posición debe estar entre 1 y 40");
        }
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.duenho = duenho;

        this.valor = 0;
        this.alquiler = 0;
        this.hipoteca = 0;
        this.grupo = null;
        this.avatares = new ArrayList<>();

    }

    //Método utilizado para añadir un avatar al array de avatares en casilla.
    public void anhadirAvatar(Avatar av) {
        this.avatares.add(av);
    }

    //Método utilizado para eliminar un avatar del array de avatares en casilla.
    public void eliminarAvatar(Avatar av) {
        this.avatares.remove(av);
    }

    /*Método para evaluar qué hacer en una casilla concreta. Parámetros:
     * - Jugador cuyo avatar está en esa casilla.
     * - La banca (para ciertas comprobaciones).
     * - El valor de la tirada: para determinar impuesto a pagar en casillas de servicios.
     * Valor devuelto: true en caso de ser solvente (es decir, de cumplir las deudas), y false
     * en caso de no cumplirlas.*/
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        if(actual==null) return false;

        if(TSUERTE.equals(tipo)||TCOMUNIDAD.equals(tipo)||(TESPECIAL.equals(tipo)&&("Cárcel".equalsIgnoreCase(nombre)||"Salida".equalsIgnoreCase(nombre)))){
            return true;
        }

        if(TESPECIAL.equals(tipo)&&"IrACarcel".equalsIgnoreCase(nombre)){
            actual.encarcelar();
            return true;
        }

        if(TESPECIAL.equals(tipo)&&"Parking".equalsIgnoreCase(nombre)){
            if(this.valor>0){
                actual.sumarFortuna(this.valor);
                this.valor=0;
            }
            return true;
        }
        if(TIMPUESTO.equals(tipo)){
            float cantidad=(this.alquiler>0)?this.alquiler:Valor.IMPUESTO_FIJO;
            actual.pagarImpuesto(cantidad);
            return true;
        }
        if(TTRANSPORTE.equals(tipo)){
            if(this.duenho!=null&&!this.duenho.equals(actual)){
                float aPagar=Valor.ALQUILER_TRANSPORTE;
                boolean respuesta=actual.pagar(aPagar);
                if(respuesta) this.duenho.cobrar(aPagar);
                return respuesta;
            }
            return true;
        }
        if(TSERVICIOS.equals(tipo)){
            if(this.duenho!=null&&!this.duenho.equals(actual)){
                float aPagar=4f*tirada*Valor.FACTOR_SERVICIO;
                boolean respuesta=actual.pagar(aPagar);
                if(respuesta) this.duenho.cobrar(aPagar);
                return respuesta;
            }
            return true;
        }
        
        if(TSOLAR.equals(tipo)){
            if(this.duenho!=null&&!this.duenho.equals(actual)){
                float aPagar=(this.alquiler>0)?this.alquiler:0;
                if(this.grupo!=null){
                    try{
                        if(this.grupo.esDuenhoGrupo(this.duenho)){
                            aPagar*=2;
                        }
                    }catch (Exception ignored){

                    }
                }
                boolean respuesta=actual.pagar(aPagar);
                if(respuesta) this.duenho.cobrar(aPagar);
                return respuesta;
            }
            return true;
        }
        return true;
    }

    /*Método usado para comprar una casilla determinada. Parámetros:
     * - Jugador que solicita la compra de la casilla.
     * - Banca del monopoly (es el dueño de las casillas no compradas aún).*/
    public void comprarCasilla(Jugador solicitante, Jugador banca) {


        boolean comprable=TSOLAR.equals(tipo)||TSERVICIOS.equals(tipo)||TTRANSPORTE.equals(tipo);
        if(!comprable) return;

        if(this.duenho!=null) return;

        float precio=Math.max(0f,this.valor);
        if(!solicitante.pagar(precio)) return;

        this.duenho=solicitante;
        solicitante.anhadirPropiedad(this);
    }

    /*Método para añadir valor a una casilla. Utilidad:
     * - Sumar valor a la casilla de parking.
     * - Sumar valor a las casillas de solar al no comprarlas tras cuatro vueltas de todos los jugadores.
     * Este método toma como argumento la cantidad a añadir del valor de la casilla.*/
    public void sumarValor(float suma) {
        this.valor += suma; //verificar mayor quw 0
    }

    /*Método para mostrar información sobre una casilla.
     * Devuelve una cadena con información específica de cada tipo de casilla.*/
    public String infoCasilla() {
        String nombre = (this.nombre == null) ? "" : this.nombre;
        String tipo = (this.tipo == null) ? "" : this.tipo;
        String dueno = (this.duenho == null) ? "Banca" : this.duenho.getNombre();

        String s="Casilla: " + nombre + " | Tipo: " + tipo + " | Posición: " + this.posicion + " | Dueño: " + dueno;

        if(TSOLAR.equals(tipo)){
            String color= (this.grupo!=null)?this.grupo.getColorGrupo(): "-";
            s+=" | Grupo: " + color+ " | Valor: " +this.valor;
        }else if(TSERVICIOS.equals(tipo)||TTRANSPORTE.equals(tipo)){
            s+=" | Valor: "+this.valor;
        }else if(TIMPUESTO.equals(tipo)){
            s+=" | Alquiler: "+this.alquiler;
        }else if(TESPECIAL.equals(tipo)&&"Parking".equals(nombre)){
            s+= " | Bote: "+this.valor;
        }

        return s;
    }

    /* Método para mostrar información de una casilla en venta.
     * Valor devuelto: texto con esa información.
     */
    public String casEnVenta() {
        boolean comprable = TSOLAR.equals(tipo) || TSERVICIOS.equals(tipo) || TTRANSPORTE.equals(tipo);
        //En la Parte 1, solo son comprables estos 3 tipos de casillas
        if (!comprable) {
            return String.format("La casilla %s del tipo %s no es comprable", nombre, tipo);
        }
        boolean enVenta = (this.duenho == null); //|| ("Banca"); lo quito porque BANCA=>duenho==null (la banca no tiene duenho)

        if (!enVenta) {
            return String.format("La casilla %s de tipo %s no está en venta", nombre, tipo);
        }

        return String.format("La casilla %s de tipo %s está en venta con precio: %f", nombre, tipo, valor);
    }

    //Vamos a añadir getters y setters
    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public int getPosicion() {
        return posicion;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float v) {
        this.valor = v;
    }

    public float getAlquiler() {
        return alquiler;
    }

    public void setAlquiler(float imp) {
        this.alquiler = imp;
    }

    public float getHipoteca() {
        return hipoteca;
    }

    public void setHipoteca(float hip) {
        this.hipoteca = hip;
    }

    public Jugador getDuenho() {
        return duenho;
    }

    public void setDuenho(Jugador d) {
        this.duenho = d;
    }

    public Grupo getGrupo(){
        return grupo;
    }
    public void setGrupo(Grupo g) {
        this.grupo = g;
    }
    public ArrayList<Avatar>getAvatares(){
        return avatares;
    }

}
