package monopoly;

import partida.Avatar;
import partida.Jugador;

import java.util.ArrayList;

public class Casilla {


    //Primero pongo los privados (da igual)
    private final String nombre; //Nombre de la casilla
    private final String tipo; //Tipo de casilla (Solar, Especial, Transporte, Servicios, Comunidad, Suerte e Impuesto).
    private int valor; //Valor de esa casilla (en la mayoría será valor de compra, en la casilla parking se usará como el bote).
    private final int posicion; //Posición que ocupa la casilla en el tablero (entero entre 1 y 40).
    private Jugador dueno; //Dueño de la casilla (por defecto sería la banca).
    private Grupo grupo; //Grupo al que pertenece la casilla (si es solar).
    private int alquiler; //Cantidad a pagar por caer en la casilla: el alquiler en solares/servicios/transportes o impuestos.
    private int hipoteca; //Valor otorgado por hipotecar una casilla
    private final ArrayList<Avatar> avatares; //Avatares que están situados en la casilla.

    private int hipotecada; //bandera para saber si hay

    private static Casilla parkingReferencia;//Dado que el método evaluar casilla no tiene como parámetro el tablero, no puedo modificar la casilla parking cuando se pagan impuestos, por lo que creo esta variable

    //Diferentes tipos de casilla, podría utilizar un tipo enumerado, pero como más adelante se modificará la práctica, trabajo con string
    public static final String TSOLAR = "Solar";
    public static final String TESPECIAL = "Especial";
    public static final String TTRANSPORTE = "Transporte";
    public static final String TSERVICIOS = "Servicios";
    public static final String TCOMUNIDAD = "Comunidad";
    public static final String TSUERTE = "Suerte";
    public static final String TIMPUESTO = "Impuesto";

    private int numCasas=0;
    private int numHoteles=0;
    private int numPiscinas=0;
    private int numPistas=0;


    private int precioCasa;
    private int precioHotel;
    private int precioPiscina;
    private int precioPistaDeporte;

    // Alquileres de edificios
    private int alquilerCasa;
    private int alquilerHotel;
    private int alquilerPiscina;
    private int alquilerPistaDeporte;

    private final java.util.List<Edificio> edificios = new java.util.ArrayList<>();
    public java.util.List<Edificio> getEdificios() { return java.util.Collections.unmodifiableList(edificios); }
    public void anadirEdificio(Edificio e) { edificios.add(e); }
    public void eliminarEdificio(Edificio e){ edificios.remove(e); }

    private long dineroGenerado = 0;   // suma de alquileres cobrados
    private int vecesVisitada = 0;     // veces que cae un jugador


    //Juego juego = new Juego();


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
    public int getAlquiler() {
        return alquiler;
    }
    public void setAlquiler(int imp) {
        this.alquiler = imp;
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

    //Dado que el metodo evaluar casilla no tiene como parámetro el tablero, no puedo modificar la casilla parking cuando se pagan impuestos, por lo que necesito estos getters y setters
    public static Casilla getParkingReferencia() { return parkingReferencia;}
    public static void setParkingReferencia(Casilla c) { parkingReferencia = c; }


    public int gethipotecada(){return hipotecada;}
    public void sethipotecada(int h){hipotecada=h;}

    public int getNumCasas() { return numCasas; }
    public int getNumHoteles() { return numHoteles; }
    public int getNumPiscinas() { return numPiscinas; }
    public int getNumPistas() { return numPistas; }

    public void setNumCasas(int n) { this.numCasas = n; }
    public void setNumHoteles(int n) { this.numHoteles = n; }
    public void setNumPiscinas(int n) { this.numPiscinas = n; }
    public void setNumPistas(int n) { this.numPistas = n; }

    public int getPrecioCasa() { return precioCasa; }
    public int getPrecioHotel() { return precioHotel; }
    public int getPrecioPiscina() { return precioPiscina; }
    public int getPrecioPistaDeporte() { return precioPistaDeporte; }



    public void setAlquilerCasa(int alquiler) { this.alquilerCasa = alquiler; }
    public void setAlquilerHotel(int alquiler) { this.alquilerHotel = alquiler; }
    public void setAlquilerPiscina(int alquiler) { this.alquilerPiscina = alquiler; }
    public void setAlquilerPistaDeporte(int alquiler) { this.alquilerPistaDeporte = alquiler; }


    public int getAlquilerCasa() { return alquilerCasa; }
    public int getAlquilerHotel() { return alquilerHotel; }
    public int getAlquilerPiscina() { return alquilerPiscina; }
    public int getAlquilerPistaDeporte() { return alquilerPistaDeporte; }

    public void sumarDineroGenerado(long cant) { dineroGenerado += cant; }
    public long getDineroGenerado() { return dineroGenerado; }

    public void incrementarVisita() { vecesVisitada++; }
    public int getVecesVisitada() { return vecesVisitada; }


    public Casilla(String nombre, String tipo, int posicion, int valor, Jugador dueno,
                   int precioCasa, int precioHotel, int precioPiscina, int precioPistaDeporte) {

        // Validaciones básicas
        if (!(TSOLAR.equals(tipo) || TSERVICIOS.equals(tipo) || TTRANSPORTE.equals(tipo))) {
            System.out.println("Tipo erróneo, debe ser 'Solar', 'Servicios' o 'Transporte'");
        }
        if (posicion < 1 || posicion > 40) {
            System.out.println("La posición debe estar entre 1 y 40");
        }

        // Inicialización básica
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.valor = Math.abs(valor);
        this.dueno = dueno;
        this.alquiler = 0;
        this.hipoteca = 0;
        this.grupo = null;
        this.avatares = new ArrayList<>();

        // Edificaciones (siempre empiezan en 0)
        this.numCasas = 0;
        this.numHoteles = 0;
        this.numPiscinas = 0;
        this.numPistas = 0;


        this.precioCasa = precioCasa;
        this.precioHotel = precioHotel;
        this.precioPiscina = precioPiscina;
        this.precioPistaDeporte = precioPistaDeporte;

        // Alquileres de edificios (inicializar a 0 por ahora)
        this.alquilerCasa = 0;
        this.alquilerHotel = 0;
        this.alquilerPiscina = 0;
        this.alquilerPistaDeporte = 0;
    }


    /*Constructor utilizado para inicializar las casillas de tipo IMPUESTOS.
     * Parámetros: nombre, posición en el tablero, impuesto establecido y dueño.
     */
    public Casilla(String nombre, int posicion, int valor) {
        if (posicion < 1 || posicion > 40) {
            System.out.println("La posición debe estar entre 1 y 40");//No hay más de 40 casillas, trato el caso en el que se introduzca un valor no válido
        }
        //Inicialuzo las variables
        this.nombre = nombre;
        this.posicion = posicion;
        this.tipo = TIMPUESTO;
        //Inicializo el resto de valores (los que no son introducidos como parámetros)
        this.valor = Math.abs(valor);
        this.grupo = null;
        this.avatares = new ArrayList<>();
        this.dueno=null;

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
        //Inicializo los campos
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        //Inicializo el resto de valores que no han sido introducidos como parámetros
        this.grupo = null;
        this.avatares = new ArrayList<>();

        this.dueno=null;

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
    public void evaluarCasilla(Jugador actual, Juego juego, int suma) {
        if(actual==null) return;
        //En el caso de que el jugador esté en una casilla de las mencionadas (de momento no se aplica pagos en suerte y comunidad) el jugador de primeras no tiene que pagar nada
        this.incrementarVisita();

        if (TSUERTE.equals(tipo)) {
            System.out.println(actual.getNombre() + " cae en Suerte.");
            juego.procesarCasillaEspecial(actual, tipo);
            return; // La acción de la carta se encarga del resto
        }
        if (TCOMUNIDAD.equals(tipo)) {
            System.out.println(actual.getNombre() + " cae en Caja de Comunidad.");
            juego.procesarCasillaEspecial(actual, tipo);
            return; // La acción de la carta se encarga del resto
        }
        if (TESPECIAL.equals(tipo) && ("Cárcel".equalsIgnoreCase(nombre) || "Salida".equalsIgnoreCase(nombre))) {
            return; // Visita a la cárcel o paso por salida (gestionado en moverAvatar)
        }
        //Si caes en la casilla IrCarcel, vas directo a la carcel con el método encarcelar
        if(TESPECIAL.equals(tipo)&&"IrCarcel".equalsIgnoreCase(nombre)){
            actual.encarcelar();
            return;
        }
        //Si la casilla a la que caes es el parking, te llevas la fortuna acumulada en la casilla
        //Si la fortuna es >0, llamas al método sumarFortuna, y se restaura a 0 el bote del parking

        if(TESPECIAL.equals(tipo)&&"Parking".equalsIgnoreCase(nombre)){
            if(this.valor>0){
                actual.sumarFortuna(this.valor);
                this.valor=0;
            }
            return;
        }

        //Si caes en una casilla de impuesto, se cobra al jugador
        if(TIMPUESTO.equals(tipo)){
            int cantidad=(this.valor>0)?this.valor:Valor.IMPUESTO_FIJO;

            //Acumulamos ahora el bote del Parking, como el esqueleto no permite meter como parámetro Tablero, hay que usar una variable estática
            boolean ok = actual.sumarGastos(cantidad);
            if (ok) {
                actual.getEstadisticas().sumarPagoTasasImpuestos(cantidad);
                Casilla.getParkingReferencia().sumarValor(cantidad);
            }

        }
        //Si caes en una casilla de tipo transporte, y tiene dueño y no es el jugador que está en la casilla,
        //como en la primera entrega no se tienen en cuenta el número de casillas de transportes, entonces
        //se calcula la cantidad a pagar (Valor.ALQUILER_TRANSPORTE), se comprueba si puede pagar y se da el dinero al dueño


        if(TTRANSPORTE.equals(tipo)){
            if(this.dueno!=null&&!this.dueno.equals(actual)&&this.dueno!=juego.getBanca()){

                actual.pagarAlquiler(this, 1);
                return;
            }
            return;
        }
        //Si caes en una casilla de tipo servicio con dueño, que no eres tú, entonces calculas la cantidad a pagar,
        //compruebas que tiene suficiente dinero y pagas, dándole al dueño lo que le corresponde



        if(TSERVICIOS.equals(tipo)){
            if(this.dueno!=null&&!this.dueno.equals(actual)&&this.dueno!=juego.getBanca()){
                int factor_pago = 4*suma;
                actual.pagarAlquiler(this, factor_pago);
                return;
            }
            return;
        }
        //Si caes en un solar con dueño que no eres tú


        if(TSOLAR.equals(tipo)){
            if(this.dueno!=null&&!this.dueno.equals(actual)&&this.dueno!=juego.getBanca()&&this.grupo.esDuenoGrupo(this.dueno)){
                if(this.hipotecada==0) {
                    actual.pagarAlquiler(this, 2);
                }


                }else if(this.dueno!=null&&!this.dueno.equals(actual)&&this.dueno!=juego.getBanca()){
                if(this.hipotecada==0) {
                    actual.pagarAlquiler(this, 1);
                }
            }
        }
    }

    /*Método usado para comprar una casilla determinada. Parámetros:
     * - Jugador que solicita la compra de la casilla.
     * - Banca del monopoly (es el dueño de las casillas no compradas aún).*/

    public void comprarCasilla(Jugador solicitante, Jugador banca) {

        //Se toma el valor y se comprueba que el que quiere comprar tiene dinero suficiente
        int precio = Math.max(0, this.valor);

        if (!solicitante.sumarGastos(precio)) {
            // sumarGastos ya imprime el mensaje de bancarrota si ocurre
            System.out.println(solicitante.getNombre() + " no tiene suficiente dinero para comprar " + this.nombre);
            return;
        }

        //Se añade la propiedad al solicitante.
        //Esta función (anadirPropiedad) se encargará de poner al 'solicitante' como dueño.
        solicitante.anadirPropiedad(this);

        // El mensaje de éxito se mueve a Juego.comprar(),
        // pero también podría ir aquí si lo prefieres.
    }
    /*Método para añadir valor a una casilla. Utilidad:
     * - Sumar valor a la casilla de parking.
     * - Sumar valor a las casillas de solar al no comprarlas tras cuatro vueltas de todos los jugadores.
     * Este método toma como argumento la cantidad a añadir del valor de la casilla.*/
    public void sumarValor(int suma) {
        this.valor += suma; //verificar mayor que 0
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

            int vCasa = this.precioCasa;
            int vHotel = this.precioHotel;
            int vPiscina = this.precioPiscina;
            int vPista = this.precioPistaDeporte;

            // Usar los atributos de alquileres de edificios
            int aCasa = this.alquilerCasa;
            int aHotel = this.alquilerHotel;
            int aPiscina = this.alquilerPiscina;
            int aPista = this.alquilerPistaDeporte;

            return "{\n"
                    + "nombre: " + nombre + ",\n"
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
        //Si es casilla de impuesto, imprime lo siguiente

        if ("impuesto".equals(tlc)) {
            int aPagar = (this.alquiler > 0) ? this.alquiler : this.valor;
            return "{\n"
                    + "tipo: impuesto,\n"
                    + "Tasa: " + aPagar + "\n"
                    + "}";
        }

        if ("especial".equals(tlc) || "parking".equals(nlc) || "salida".equals(nlc) || "cárcel".equals(nlc) || "carcel".equals(nlc) || "ircarcel".equals(nlc)) {
            //Si es casilla especial de tipo parking, imprime lo siguiente
            switch (nlc) {
                case "parking" -> {
                    StringBuilder jugadores = getStringBuilder();
                    //Imprimo descripción de parking con el valor (bote)
                    return "{\n"
                            + "bote: " + this.valor + ",\n"
                            + "jugadores: [" + jugadores + "]\n"
                            + "}";
                }

                //Si es casilla especial de tipo parking, imprime lo siguiente
                case "cárcel", "carcel" -> {
                    int salir = Valor.PRECIO_SALIR_CARCEL;
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

                //Si la casilla es la salida, imprime el cobro al pasar por ella
                case "salida" -> {
                    return """
                            {
                            tipo: salida
                            }""";
                }
            }

            return "{\n"
                    + "tipo: especial,\n"
                    + "nombre: " + (nombre.isEmpty() ? "-" : nombre) + "\n"
                    + "}";
        }

        return switch (tlc) {
            case "suerte" -> """
                    {
                    tipo: suerte
                    }""";

            //Casilla caja
            case "comunidad" -> """
                    {
                    tipo: comunidad
                    }""";

            //Casilla servicios (transporte y servicios)
            case "servicios", "servicio", "transporte" -> "{\n"
                    + "tipo: " + tlc + ",\n"
                    + "valor: " + this.valor + "\n"
                    + "}";
            default -> "{\n"
                    + "tipo: " + (tlc.isEmpty() ? "-" : tlc) + "\n"
                    + "}";
        };

    }

    private StringBuilder getStringBuilder() {
        StringBuilder jugadores = new StringBuilder();
        //Si hay avatares en la casilla, los mete
        if (this.avatares != null && !this.avatares.isEmpty()) {
            for (int i = 0; i < this.avatares.size(); i++) {
                Jugador j = this.avatares.get(i).getJugador();
                String nom = (j == null) ? "-" : j.getNombre();
                if (i > 0) jugadores.append(", ");
                jugadores.append(nom);
            }
        }
        return jugadores;
    }




    /* Método para mostrar información de una casilla en venta.
     * Valor devuelto: texto con esa información.
     */

    public int getHipoteca() {
        return hipoteca;
    }


}
