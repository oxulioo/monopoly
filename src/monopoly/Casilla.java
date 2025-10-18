package monopoly;

import partida.Avatar;
import partida.Jugador;

import java.util.ArrayList;


public class Casilla {

    // region ==== ATRIBUTOS ====
    //Primero pongo los privados (da igual)
    private String nombre; //Nombre de la casilla
    private String tipo; //Tipo de casilla (Solar, Especial, Transporte, Servicios, Comunidad, Suerte y Impuesto).
    private float valor; //Valor de esa casilla (en la mayoría será valor de compra, en la casilla parking se usará como el bote).
    private int posicion; //Posición que ocupa la casilla en el tablero (entero entre 1 y 40).
    private Jugador dueno; //Dueño de la casilla (por defecto sería la banca).
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

    // endregion

    // region ==== GETTERS Y SETTERS ====

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

    public Jugador getDueno() {
        return dueno;
    }

    public void setDueno(Jugador d) {
        this.dueno = d;
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

    // endregion

    // region ==== CONSTRUCTORES ====
    //Constructores:
    public Casilla() {
    }//Parámetros vacíos

    /*Constructor para casillas tipo Solar, Servicios o Transporte:
     * Parámetros: nombre casilla, tipo (debe ser solar, serv. o transporte), posición en el tablero, valor y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador dueno) {
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
        this.dueno = dueno;
        //Inicializo los demás valores para que no dé error después
        this.alquiler = 0;
        this.hipoteca = 0;
        this.grupo = null;
        this.avatares = new ArrayList<>();
    }

    /*Constructor utilizado para inicializar las casillas de tipo IMPUESTOS.
     * Parámetros: nombre, posición en el tablero, impuesto establecido y dueño.
     */
    public Casilla(String nombre, int posicion, float alquiler, Jugador dueno) {
        if (posicion < 1 || posicion > 40) {
            System.out.println("La posición debe estar entre 1 y 40");//No hay más de 40 casillas, trato el caso en el que se introduzca un valor no válido
        }
        this.nombre = nombre;
        this.posicion = posicion;
        this.alquiler = Math.max(0f, alquiler);//En caso de valores negativos, se toma el 0
        this.dueno = dueno;
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
    public Casilla(String nombre, String tipo, int posicion, Jugador dueno) {

        if (!(TSUERTE.equals(tipo) || TCOMUNIDAD.equals(tipo) || TESPECIAL.equals(tipo))) {//Si no es una de las casillas mencionadas, da error
            System.out.println("Tipo erróneo, debe ser 'Suerte', 'Comunidad' o 'Especial'");
        }
        if (posicion < 1 || posicion > 40) {
            System.out.println("La posición debe estar entre 1 y 40");
        }
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.dueno = dueno;

        this.valor = 0;
        this.alquiler = 0;
        this.hipoteca = 0;
        this.grupo = null;
        this.avatares = new ArrayList<>();

    }

    // endregion

    // region ==== MÉTODOS ====

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
        //En el caso de que el jugador esté en una casilla de las mencionadas (de momento no se aplica pagos en suerte y comunidad) el jugador de primeras no tiene que pagar nada
        if(TSUERTE.equals(tipo)||TCOMUNIDAD.equals(tipo)||(TESPECIAL.equals(tipo)&&("Cárcel".equalsIgnoreCase(nombre)||"Salida".equalsIgnoreCase(nombre)))){
            return true;
        }
        //Si caes en la casilla IrCarcel, vas directo a la carcel con el método encarcelar
        if(TESPECIAL.equals(tipo)&&"IrCarcel".equalsIgnoreCase(nombre)){
            actual.encarcelar(this);
            return true;
        }
        //Si la casilla a la que caes es el parking, te llevas la fortuna acumulada en la casilla
        //Si la fortuna es >0, llamas al método sumarFortuna, y se restaura a 0 el bote del parking
        if(TESPECIAL.equals(tipo)&&"Parking".equalsIgnoreCase(nombre)){
            if(this.valor>0){
                actual.sumarFortuna(this.valor);
                this.valor=0;
            }
            return true;
        }
        //Si caes en una casilla de impuesto, se cobra al jugador
        if(TIMPUESTO.equals(tipo)){
            float cantidad=(this.alquiler>0)?this.alquiler:Valor.IMPUESTO_FIJO;
            actual.pagarImpuesto(cantidad);
            return true;
        }
        //Si caes en una casilla de tipo transporte, y tiene dueño y no es el jugador que está en la casilla,
        //como en la primera entrega no se tienen en cuenta el número de casillas de transportes, entonces
        //se calcula la cantidad a pagar (Valor.ALQUILER_TRANSPORTE), se comprueba si puede pagar y se da el dinero al dueño
        if(TTRANSPORTE.equals(tipo)){
            if(this.dueno!=null&&!this.dueno.equals(actual)){
                float aPagar=Valor.ALQUILER_TRANSPORTE;
                boolean respuesta=actual.sumarGastos(aPagar);
                if(respuesta) this.dueno.sumarFortuna(aPagar);
                return respuesta;
            }
            return true;
        }
        //Si caes en una casilla de tipo servicio con dueño, que no eres tú, entonces calculas la cantidad a pagar,
        //compruebas que tiene suficiente dinero y pagas, dándole al dueño lo que le corresponde
        if(TSERVICIOS.equals(tipo)){
            if(this.dueno!=null&&!this.dueno.equals(actual)){
                float aPagar=4f*tirada*Valor.FACTOR_SERVICIO;
                boolean respuesta=actual.sumarGastos(aPagar);
                if(respuesta) this.dueno.sumarFortuna(aPagar);
                return respuesta;
            }
            return true;
        }
        //Si caes en un solar con dueño que no eres tú, (...) pagar, si al que tienes que pagar tiene todas
        //las casillas del grupo (color), entonces pagas el doble
        if(TSOLAR.equals(tipo)){
            if(this.dueno!=null&&!this.dueno.equals(actual)){
                float aPagar=(this.alquiler>0)?this.alquiler:0;
                if(this.grupo!=null){
                    try{
                        if(this.grupo.esDuenoGrupo(this.dueno)){
                            aPagar*=2;
                        }
                    }catch (Exception ignored){

                    }
                }
                boolean respuesta=actual.sumarGastos(aPagar);
                if(respuesta) this.dueno.sumarFortuna(aPagar);
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

        //Comprobamos si la casilla es comprable (pertenece a un tipo que se puede comprar)
        boolean comprable=TSOLAR.equals(tipo)||TSERVICIOS.equals(tipo)||TTRANSPORTE.equals(tipo);
        if(!comprable) return;
        //Si la casilla ya tiene dueño, no se puede comprar
        if(this.dueno!=null) return;
        //Se toma el valor y se comprueba que el que quiere comprar tiene dinero suficiente
        float precio=Math.max(0f,this.valor);
        if(!solicitante.sumarGastos(precio)) return;
        //Se añade la propiedad al solicitante y este se convierte en su propietario (dueño)
        this.dueno=solicitante;
        solicitante.anadirPropiedad(this);
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
        String dueno = (this.dueno == null) ? "Banca" : this.dueno.getNombre();

        String s="Casilla: " + nombre + " | Tipo: " + tipo + " | Posición: " + this.posicion + " | Dueño: " + dueno;

        if(TSOLAR.equals(tipo)){
            String color= (this.grupo!=null)?this.grupo.getColorGrupo(): "-";
            s+=" | Grupo: " + color+ " | Valor: " +this.valor;
        }else if(TSERVICIOS.equals(tipo)||TTRANSPORTE.equals(tipo)){
            s+=" | Valor: "+this.valor;
        }else if(TIMPUESTO.equals(tipo)){
            s+=" | Alquiler: "+this.alquiler;
        }else if(TESPECIAL.equals(tipo)&&"Parking".equalsIgnoreCase(nombre)){
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
        boolean enVenta = (this.dueno == null); //|| ("Banca"); lo quito porque BANCA=>dueno==null (la banca no tiene dueno)

        if (!enVenta) {
            return String.format("La casilla %s de tipo %s no está en venta", nombre, tipo);
        }

        return String.format("La casilla %s de tipo %s está en venta con precio: %f", nombre, tipo, valor);
    }

    // endregion

}
