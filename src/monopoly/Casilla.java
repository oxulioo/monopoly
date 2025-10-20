package monopoly;

import partida.Avatar;
import partida.Jugador;

import java.util.ArrayList;


public class Casilla {

    // region ==== ATRIBUTOS ====

    //Primero pongo los privados (da igual)
    private String nombre; //Nombre de la casilla
    private String tipo; //Tipo de casilla (Solar, Especial, Transporte, Servicios, Comunidad, Suerte y Impuesto).
    private int valor; //Valor de esa casilla (en la mayoría será valor de compra, en la casilla parking se usará como el bote).
    private final int posicion; //Posición que ocupa la casilla en el tablero (entero entre 1 y 40).
    private Jugador dueno; //Dueño de la casilla (por defecto sería la banca).
    private Grupo grupo; //Grupo al que pertenece la casilla (si es solar).
    private int alquiler; //Cantidad a pagar por caer en la casilla: el alquiler en solares/servicios/transportes o impuestos.
    private int hipoteca; //Valor otorgado por hipotecar una casilla
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
    public int getValor() {
        return valor;
    }
    public void setValor(int v) {
        this.valor = v;
    }
    public int getAlquiler() {
        return alquiler;
    }
    public void setAlquiler(int imp) {
        this.alquiler = imp;
    }
    public int getHipoteca() {
        return hipoteca;
    }
    public void setHipoteca(int hip) {
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

    /*Constructor para casillas tipo Solar, Servicios o Transporte:
     * Parámetros: nombre casilla, tipo (debe ser solar, serv. o transporte), posición en el tablero, valor y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, int valor, Jugador dueno) {
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
        this.valor = Math.max(0, valor);//En caso en que se de un valor negativo, se toma el 0 para evitar errores
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
    public Casilla(String nombre, int posicion, int alquiler, Jugador dueno) {
        if (posicion < 1 || posicion > 40) {
            System.out.println("La posición debe estar entre 1 y 40");//No hay más de 40 casillas, trato el caso en el que se introduzca un valor no válido
        }
        this.nombre = nombre;
        this.posicion = posicion;
        this.alquiler = Math.max(0, alquiler);//En caso de valores negativos, se toma el 0
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
    public Casilla(String nombre, int posicion, String tipo) {

        if (!(TSUERTE.equals(tipo) || TCOMUNIDAD.equals(tipo) || TESPECIAL.equals(tipo))) {//Si no es una de las casillas mencionadas, da error
            System.out.println("Tipo erróneo, debe ser 'Suerte', 'Comunidad' o 'Especial'");
        }
        if (posicion < 1 || posicion > 40) {
            System.out.println("La posición debe estar entre 1 y 40");
        }
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;

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
            int cantidad=(this.alquiler>0)?this.alquiler:Valor.IMPUESTO_FIJO;
            actual.pagarImpuesto(cantidad);
            return true;
        }
        //Si caes en una casilla de tipo transporte, y tiene dueño y no es el jugador que está en la casilla,
        //como en la primera entrega no se tienen en cuenta el número de casillas de transportes, entonces
        //se calcula la cantidad a pagar (Valor.ALQUILER_TRANSPORTE), se comprueba si puede pagar y se da el dinero al dueño
        if(TTRANSPORTE.equals(tipo)){
            if(this.dueno!=null&&!this.dueno.equals(actual)){
                int aPagar=Valor.ALQUILER_TRANSPORTE;
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
                int aPagar=4*tirada*Valor.FACTOR_SERVICIO;
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
                int aPagar=(this.alquiler>0)?this.alquiler:0;
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
        int precio=Math.max(0,this.valor);
        if(!solicitante.sumarGastos(precio)) return;
        //Se añade la propiedad al solicitante y este se convierte en su propietario (dueño)
        this.dueno=solicitante;
        solicitante.anadirPropiedad(this);
    }

    /*Método para añadir valor a una casilla. Utilidad:
     * - Sumar valor a la casilla de parking.
     * - Sumar valor a las casillas de solar al no comprarlas tras cuatro vueltas de todos los jugadores.
     * Este método toma como argumento la cantidad a añadir del valor de la casilla.*/
    public void sumarValor(int suma) {
        this.valor += suma; //verificar mayor quw 0
    }

    /*Método para mostrar información sobre una casilla.
     * Devuelve una cadena con información específica de cada tipo de casilla.*/
    public String infoCasilla() {
        String nombre = (this.nombre == null) ? "" : this.nombre.trim();
        String tipoRaw = (this.tipo == null) ? "" : this.tipo.trim();
        String propietario = (this.dueno == null) ? "Banca" : this.dueno.getNombre();

        String tlc = tipoRaw.toLowerCase();
        String nlc = nombre.toLowerCase();

        if ("solar".equals(tlc)) {
            String grupoStr = (this.grupo != null) ? this.grupo.getColorGrupo() : "-";

            int vCasa=0, vHotel=0, vPiscina=0, vPista=0;
            int aCasa=0, aHotel=0, aPiscina=0, aPista=0;

            switch (nombre) {
                // Marrón (Solar1–2)
                case "Solar1":  vCasa=500000;  vHotel=500000;  vPiscina=100000;  vPista=200000;
                    aCasa=400000;  aHotel=2500000; aPiscina=500000;  aPista=500000;  break;
                case "Solar2":  vCasa=500000;  vHotel=500000;  vPiscina=100000;  vPista=200000;
                    aCasa=800000;  aHotel=4500000; aPiscina=900000;  aPista=900000;  break;

                // Cian (Solar3–5)
                case "Solar3":  vCasa=500000;  vHotel=500000;  vPiscina=100000;  vPista=200000;
                    aCasa=1000000; aHotel=5500000; aPiscina=1100000; aPista=1100000; break;
                case "Solar4":  vCasa=500000;  vHotel=500000;  vPiscina=100000;  vPista=200000;
                    aCasa=1000000; aHotel=5500000; aPiscina=1100000; aPista=1100000; break;
                case "Solar5":  vCasa=500000;  vHotel=500000;  vPiscina=100000;  vPista=200000;
                    aCasa=1250000; aHotel=6000000; aPiscina=1200000; aPista=1200000; break;

                // Rosa (Solar6–8)
                case "Solar6":  vCasa=1000000; vHotel=1000000; vPiscina=200000;  vPista=400000;
                    aCasa=1500000; aHotel=7500000; aPiscina=1500000; aPista=1500000; break;
                case "Solar7":  vCasa=1000000; vHotel=1000000; vPiscina=200000;  vPista=400000;
                    aCasa=1500000; aHotel=7500000; aPiscina=1500000; aPista=1500000; break;
                case "Solar8":  vCasa=1000000; vHotel=1000000; vPiscina=200000;  vPista=400000;
                    aCasa=1750000; aHotel=9000000; aPiscina=1800000; aPista=1800000; break;

                // Naranja (Solar9–11)
                case "Solar9":  vCasa=1000000; vHotel=1000000; vPiscina=200000;  vPista=400000;
                    aCasa=1850000; aHotel=9500000; aPiscina=1900000; aPista=1900000; break;
                case "Solar10": vCasa=1000000; vHotel=1000000; vPiscina=200000;  vPista=400000;
                    aCasa=1850000; aHotel=9500000; aPiscina=1900000; aPista=1900000; break;
                case "Solar11": vCasa=1000000; vHotel=1000000; vPiscina=200000;  vPista=400000;
                    aCasa=2000000; aHotel=10000000; aPiscina=2000000; aPista=2000000; break;

                // Rojo (Solar12–14)
                case "Solar12": vCasa=1500000; vHotel=1500000; vPiscina=300000;  vPista=600000;
                    aCasa=2200000; aHotel=10500000; aPiscina=2100000; aPista=2100000; break;
                case "Solar13": vCasa=1500000; vHotel=1500000; vPiscina=300000;  vPista=600000;
                    aCasa=2200000; aHotel=10500000; aPiscina=2100000; aPista=2100000; break;
                case "Solar14": vCasa=1500000; vHotel=1500000; vPiscina=300000;  vPista=600000;
                    aCasa=2325000; aHotel=11000000; aPiscina=2200000; aPista=2200000; break;

                // Amarillo (Solar15–17)
                case "Solar15": vCasa=1500000; vHotel=1500000; vPiscina=300000;  vPista=600000;
                    aCasa=2450000; aHotel=11500000; aPiscina=2300000; aPista=2300000; break;
                case "Solar16": vCasa=1500000; vHotel=1500000; vPiscina=300000;  vPista=600000;
                    aCasa=2450000; aHotel=11500000; aPiscina=2300000; aPista=2300000; break;
                case "Solar17": vCasa=1500000; vHotel=1500000; vPiscina=300000;  vPista=600000;
                    aCasa=2600000; aHotel=12000000; aPiscina=2400000; aPista=2400000; break;

                // Verde (Solar18–20)
                case "Solar18": vCasa=2000000; vHotel=2000000; vPiscina=400000;  vPista=800000;
                    aCasa=2750000; aHotel=12750000; aPiscina=2550000; aPista=2550000; break;
                case "Solar19": vCasa=2000000; vHotel=2000000; vPiscina=400000;  vPista=800000;
                    aCasa=2750000; aHotel=12750000; aPiscina=2550000; aPista=2550000; break;
                case "Solar20": vCasa=2000000; vHotel=2000000; vPiscina=400000;  vPista=800000;
                    aCasa=3000000; aHotel=14000000; aPiscina=2800000; aPista=2800000; break;

                // Azul (Solar21–22)
                case "Solar21": vCasa=2000000; vHotel=2000000; vPiscina=400000;  vPista=800000;
                    aCasa=3250000; aHotel=17000000; aPiscina=3400000; aPista=3400000; break;
                case "Solar22": vCasa=2000000; vHotel=2000000; vPiscina=400000;  vPista=800000;
                    aCasa=4250000; aHotel=20000000; aPiscina=4000000; aPista=4000000; break;
            }
            return "{\n"
                    + "tipo: solar,\n"
                    + "grupo: " + grupoStr + ",\n"
                    + "propietario: " + propietario + ",\n"
                    + "valor: " + this.valor + ",\n"
                    + "alquiler: " + this.alquiler + ",\n"
                    + "valor hotel: " + vHotel + ",\n"
                    + "valor casa: " + vCasa + ",\n"
                    + "valor piscina: " + vPiscina + ",\n"
                    + "valor pista de deporte: " + vPista + ",\n"
                    + "alquiler casa: " + aCasa + ",\n"
                    + "alquiler hotel: " + aHotel + ",\n"
                    + "alquiler piscina: " + aPiscina + ",\n"
                    + "alquiler pista de deporte: " + aPista + ",\n"
                    + "}";
        }

        if ("impuesto".equals(tlc)) {
            int apagar = (this.alquiler > 0) ? this.alquiler : this.valor;
            return "{\n"
                    + "tipo: impuesto,\n"
                    + "apagar: " + apagar + "\n"
                    + "}";
        }

        if ("especial".equals(tlc) || "parking".equals(nlc) || "salida".equals(nlc) || "cárcel".equals(nlc) || "carcel".equals(nlc) || "ircarcel".equals(nlc)) {
            if ("parking".equals(nlc)) {
                StringBuilder jugadores = new StringBuilder();
                if (this.avatares != null && !this.avatares.isEmpty()) {
                    for (int i = 0; i < this.avatares.size(); i++) {
                        Jugador j = this.avatares.get(i).getJugador();
                        String nom = (j == null) ? "-" : j.getNombre();
                        if (i > 0) jugadores.append(", ");
                        jugadores.append(nom);
                    }
                }
                return "{\n"
                        + "bote: " + this.valor + ",\n"
                        + "jugadores: [" + jugadores + "]\n"
                        + "}";
            }

            if ("cárcel".equals(nlc) || "carcel".equals(nlc)) {
                int salir;
                try { salir = Valor.PRECIO_SALIR_CARCEL; } catch (Throwable t) { salir = 500000; }

                StringBuilder jugadores = new StringBuilder();
                if (this.avatares != null && !this.avatares.isEmpty()) {
                    for (int i = 0; i < this.avatares.size(); i++) {
                        Jugador j = this.avatares.get(i).getJugador();
                        String nom = (j == null) ? "-" : j.getNombre();
                        if (i > 0) jugadores.append(" ");
                        jugadores.append("[").append(nom).append("]");
                    }
                } else {
                    jugadores.append("[]");
                }

                return "{\n"
                        + "salir: " + salir + ",\n"
                        + "jugadores: " + jugadores + "\n"
                        + "}";
            }

            if ("salida".equals(nlc)) {
                return "{\n"
                        + "tipo: salida\n"
                        + "}";
            }

            return "{\n"
                    + "tipo: especial,\n"
                    + "nombre: " + (nombre.isEmpty() ? "-" : nombre) + "\n"
                    + "}";
        }

        if ("suerte".equals(tlc)) {
            return "{\n"
                    + "tipo: suerte\n"
                    + "}";
        }

        if ("comunidad".equals(tlc)) {
            return "{\n"
                    + "tipo: comunidad\n"
                    + "}";
        }

        if ("servicios".equals(tlc) || "servicio".equals(tlc) || "transporte".equals(tlc)) {
            return "{\n"
                    + "tipo: " + tlc + ",\n"
                    + "valor: " + this.valor + "\n"
                    + "}";
        }

        return "{\n"
                + "tipo: " + (tlc.isEmpty() ? "-" : tlc) + "\n"
                + "}";
    }







    /* Método para mostrar información de una casilla en venta.
     * Valor devuelto: texto con esa información.
     */
    public String casEnVenta() {
        boolean comprable = TSOLAR.equals(tipo) || TSERVICIOS.equals(tipo) || TTRANSPORTE.equals(tipo);
        if (!comprable) {
            return String.format("La casilla %s no es comprable", nombre);
        }
        boolean enVenta = (this.dueno == null);
        if (!enVenta) {
            String grupoStr = (this.grupo == null) ? "-" : this.grupo.getColorGrupo();
            return String.format("La casilla %s de tipo %s y grupo %s no está en venta", nombre, tipo, grupoStr);
        }

        String grupoStr = (this.grupo == null) ? "-" : this.grupo.getColorGrupo();
        if (this.grupo == null) {
            return String.format("La casilla %s de tipo %s está en venta con precio: %d", nombre, tipo, valor);
        }

        return String.format("La casilla %s de tipo %s y grupo %s está en venta con precio: %d",
                nombre, tipo, grupoStr, valor);
    }


    // endregion

}
