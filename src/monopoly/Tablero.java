package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
* OJO, HAY UNA COSA QUE PREGUNTAR, EL GUION PONE LAS CASILLAS DE SUERTE Y CAJA CON EL MISMO NOMBRE
* (NO CON NUMEROS) POR LO QUE A LA HORA DE BUSCAR LA CASILLA POR EL NOMBRE, PUEDEN HABER PROBLEMAS
* HAY DOS OPCIONES, LLAMARLAS CAJA1,CAJA2,...,SUERTE1,SUERTE2,... O EN EL MÉTODO DE BUSCAR,
* AÑADIR UN CAMPO QUE INDIQUE LA POSICION EXACTA (AUNQUE ENTONCES CREO QUE NO NECESITARÍA EL NOMBRE)
*/

public class Tablero {

    // region ==== ATRIBUTOS ====

    private ArrayList<ArrayList<Casilla>> posiciones; //Posiciones del tablero: se define como un arraylist de arraylists de casillas (uno por cada lado del tablero).
    private HashMap<String, Grupo> grupos; //Grupos del tablero, almacenados como un HashMap con clave String (será el color del grupo).
    //La clave es el color del grupo, y el valor es el grupo, que contiene todas las casillas de dicho color (recordar, la tabla HashMap guarda pares clave-valor)
    private Jugador banca; //Un jugador que será la banca.FIXME:PUBLICO

    // endregion

    // region ==== CONSTRUCTORES ====
    //Constructor: únicamente le pasamos el jugador banca (que se creará desde el menú).
    public Tablero(Jugador banca) {
        this.banca=banca;
        //Voy a crear las 4 listas, una para cada lado
        this.posiciones=new ArrayList<>(4);
        for (int i=0;i<4;i++){
            this.posiciones.add(new ArrayList<>());
        }
        //Inicializo el HashMap
        this.grupos=new HashMap<>();

        //Llamo a 3 metodos (implementados más adelante) para crear el tablero correctamente)
        generarCasillas();
        precargarAlquileres();
        generarGrupos();
    }

    // endregion

    // region ==== MÉTODOS ====

    //Método para crear todas las casillas del tablero. Formado a su vez por cuatro métodos (1/lado).
    private void generarCasillas() {
        //Llamo a 4 metodos (uno para cada lado del tablero) que generen las casillas
        this.insertarLadoSur();
        this.insertarLadoOeste();
        this.insertarLadoNorte();
        this.insertarLadoEste();
    }
    //Método para cargar inicialmente los valores de los alquileres según el Apéndice
    private void precargarAlquileres() {
        // Recorremos todas las casillas de los 4 lados
        for (ArrayList<Casilla> lado:posiciones) {
            for (Casilla c:lado) {
                if (c==null) continue;

                String tipo=c.getTipo();
                String nombre=c.getNombre();

                //Valores exactos del alquiler +hipoteca(50% del precio)
                if (Casilla.TSOLAR.equals(tipo)) {
                    int alquiler = 0;
                    switch (nombre) {
                        // Marrón
                        case "Solar1":  alquiler =  20000;  break;
                        case "Solar2":  alquiler =  40000;  break;
                        // Cián
                        case "Solar3":  alquiler =  60000;  break;
                        case "Solar4":  alquiler =  60000;  break;
                        case "Solar5":  alquiler =  80000;  break;
                        // Rosa
                        case "Solar6":  alquiler = 100000;  break;
                        case "Solar7":  alquiler = 100000;  break;
                        case "Solar8":  alquiler = 120000;  break;
                        // Naranja
                        case "Solar9":  alquiler = 140000;  break;
                        case "Solar10": alquiler = 140000;  break;
                        case "Solar11": alquiler = 160000;  break;
                        // Rojo
                        case "Solar12": alquiler = 180000;  break;
                        case "Solar13": alquiler = 180000;  break;
                        case "Solar14": alquiler = 200000;  break;
                        // Amarillo
                        case "Solar15": alquiler = 220000;  break;
                        case "Solar16": alquiler = 220000;  break;
                        case "Solar17": alquiler = 240000;  break;
                        // Verde
                        case "Solar18": alquiler = 260000;  break;
                        case "Solar19": alquiler = 260000;  break;
                        case "Solar20": alquiler = 280000;  break;
                        // Azul
                        case "Solar21": alquiler = 350000;  break;
                        case "Solar22": alquiler = 500000;  break;
                    }
                    c.setAlquiler(alquiler);//Introduzco el alquiler base (sin edificios), con el setter
                    c.setHipoteca((int) Math.max(0, c.getValor() / 2));
                }

                //Si es TRANSPORTE, el precio de compra es 500.000 y el alquiler fijo es250.000
                else if (Casilla.TTRANSPORTE.equals(tipo)) {
                    c.setAlquiler((int) Valor.ALQUILER_TRANSPORTE);// 250.000
                    c.setHipoteca((int) 0);// No hipotecable en esta parte, hay que cambiarlo (entiendo) en siguientes entregas
                }

                //Si es SERVICIO, el precio de compra es 500.000 y el alquiler se calcula con la tirada
                else if (Casilla.TSERVICIOS.equals(tipo)) {
                    c.setAlquiler(Valor.FACTOR_SERVICIO);// Se calcula: 4 * tirada * FACTOR_SERVICIO
                    c.setHipoteca(0);// No hipotecable en esta parte
                }

                //En el caso de que sean IMPUESTOS/ESPECIALES/SUERTE/COMUNIDAD, se tiene que (no hace falta hacer nada):
                //Los impuestos ya traen la "cantidad a pagar" en el campo 'alquiler' desde el constructor.
                //El parking acumula bote en 'valor' cuando alguien paga impuestos.
            }

        }

    }
//fixme2 asignarABanca()
    /*

// Asigna a la banca toda casilla comprable
    private void asignarABanca(Casilla c) {
        if (c == null) return;
        String t = c.getTipo();
        boolean comprable = Casilla.TSOLAR.equals(t) || Casilla.TSERVICIOS.equals(t) || Casilla.TTRANSPORTE.equals(t);
        if (!comprable) return;

        c.setDueno(banca);
        if (!banca.getPropiedades().contains(c)) {
            banca.getPropiedades().add(c);
        }
    }

     */
    //Esta la escribo yo, no estaba en el esqueleto, MIRAR BIEN
    private void generarGrupos(){
        //Busco la casilla, compruebo que no sean null y la añado al grupo

        //Marrón
        Casilla s1=encontrar_casilla("Solar1");
        Casilla s2=encontrar_casilla("Solar2");
        if(s1!=null && s2!=null){
            Grupo marron=new Grupo(s1,s2, "Marron");
            grupos.put("Marron", marron);
        }
        //Cián
        Casilla s3=encontrar_casilla("Solar3");
        Casilla s4=encontrar_casilla("Solar4");
        Casilla s5=encontrar_casilla("Solar5");
        if(s3!=null && s4!=null && s5!=null){
            Grupo cian=new Grupo(s3,s4,s5,"Cian");
            grupos.put("Cian",cian);
        }
        //Rosa
        Casilla s6=encontrar_casilla("Solar6");
        Casilla s7=encontrar_casilla("Solar7");
        Casilla s8=encontrar_casilla("Solar8");
        if(s6!=null && s7!=null && s8!=null){
            Grupo rosa=new Grupo(s6,s7,s8, "Rosa");
            grupos.put("Rosa", rosa);
        }
        //Naranja
        Casilla s9=encontrar_casilla("Solar9");
        Casilla s10=encontrar_casilla("Solar10");
        Casilla s11=encontrar_casilla("Solar11");
        if(s9!=null && s10!=null && s11!=null){
            Grupo naranja=new Grupo(s9,s10,s11,"Naranja");
            grupos.put("Naranja",naranja);
        }
        //Rojo
        Casilla s12=encontrar_casilla("Solar12");
        Casilla s13=encontrar_casilla("Solar13");
        Casilla s14=encontrar_casilla("Solar14");
        if(s12!=null && s13!=null && s14!=null){
            Grupo rojo=new Grupo(s12,s13,s14,"Rojo");
            grupos.put("Rojo",rojo);
        }
        //Amarillo
        Casilla s15=encontrar_casilla("Solar15");
        Casilla s16=encontrar_casilla("Solar16");
        Casilla s17=encontrar_casilla("Solar17");
        if(s15!=null && s16!=null && s17!=null){
            Grupo amarillo=new Grupo(s15,s16,s17,"Amarillo");
            grupos.put("Amarillo",amarillo);
        }
        //Verde
        Casilla s18=encontrar_casilla("Solar18");
        Casilla s19=encontrar_casilla("Solar19");
        Casilla s20=encontrar_casilla("Solar20");
        if(s18!=null && s19!=null && s20!=null){
            Grupo verde=new Grupo(s18,s19,s20,"Verde");
            grupos.put("Verde",verde);
        }
        //Azul
        Casilla s21=encontrar_casilla("Solar21");
        Casilla s22=encontrar_casilla("Solar22");
        if(s21!=null && s22!=null){
            Grupo azul=new Grupo(s21,s22,"Azul");
            grupos.put("Azul",azul);
        }
    }

    //Método para insertar las casillas del lado norte.
    private void insertarLadoNorte() {
        //Le asigno la posición al array (hay 4)
        ArrayList<Casilla> norte = posiciones.get(2);

        //Dado que el parking tiene que actualizar el bote y no tengo un metodo que acepte el tablero como parámetro (no está en el esqueleto); uso una variable estática y actualizo la casilla
        Casilla parking = new Casilla("Parking",21, Casilla.TESPECIAL);
        norte.add(parking);
        Casilla.setParkingReferencia(parking);
        //Añado cada casilla, le asigno el nombre, el tipo, la posición, el valor y el dueño según corresponda (algunas casillas no tienen dueño o valor por ejemplo)
        norte.add(new Casilla("Solar12", Casilla.TSOLAR, 22, 2200000, banca));
        norte.add(new Casilla("Suerte", 23, Casilla.TSUERTE));
        norte.add(new Casilla("Solar13", Casilla.TSOLAR, 24, 2200000, banca));
        norte.add(new Casilla("Solar14", Casilla.TSOLAR, 25, 2400000, banca));
        norte.add(new Casilla("Trans3", Casilla.TTRANSPORTE, 26, 500000, banca));
        norte.add(new Casilla("Solar15", Casilla.TSOLAR, 27, 2600000, banca));
        norte.add(new Casilla("Solar16", Casilla.TSOLAR, 28, 2600000, banca));
        norte.add(new Casilla("Serv2", Casilla.TSERVICIOS, 29, 500000, banca));
        norte.add(new Casilla("Solar17", Casilla.TSOLAR, 30, 2800000, banca));
        norte.add(new Casilla("IrCarcel", 31, Casilla.TESPECIAL));
    } // FIXME: TODO MAL. EL DUEÑO TIENE QUE SER LA BANCA

    //Método para insertar las casillas del lado sur.
    private void insertarLadoSur() {
        //Le asigno la posición al array (hay 4)
        ArrayList<Casilla>sur=posiciones.get(0);
        //Añado cada casilla, le asigno el nombre, el tipo, la posición, el valor y el dueño según corresponda (algunas casillas no tienen dueño o valor por ejemplo)
        sur.add(new Casilla("Cárcel", 11, Casilla.TESPECIAL));
        sur.add(new Casilla("Solar5", Casilla.TSOLAR, 10, 1200000, banca));
        sur.add(new Casilla("Solar4", Casilla.TSOLAR, 9, 1000000, banca));
        sur.add(new Casilla("Suerte", 8, Casilla.TSUERTE));
        sur.add(new Casilla("Solar3", Casilla.TSOLAR, 7, 1000000, banca));
        sur.add(new Casilla("Trans1", Casilla.TTRANSPORTE, 6, 500000, banca));
        sur.add(new Casilla("Imp1", 5, 2000000));
        sur.add(new Casilla("Solar2", Casilla.TSOLAR, 4, 600000, banca));
        sur.add(new Casilla("Caja", 3, Casilla.TCOMUNIDAD));
        sur.add(new Casilla("Solar1", Casilla.TSOLAR, 2, 600000, banca));
        sur.add(new Casilla("Salida", 1, Casilla.TESPECIAL));
    }

    //Método que inserta casillas del lado oeste.
    private void insertarLadoOeste() {
        //Le asigno la posición al array (hay 4)
        ArrayList<Casilla>oeste=posiciones.get(1);
        //Añado cada casilla, le asigno el nombre, el tipo, la posición, el valor y el dueño según corresponda (algunas casillas no tienen dueño o valor por ejemplo)
        oeste.add(new Casilla("Solar6", Casilla.TSOLAR, 12, 1400000, banca));
        oeste.add(new Casilla("Serv1", Casilla.TSERVICIOS, 13, 500000,banca));
        oeste.add(new Casilla("Solar7", Casilla.TSOLAR, 14, 1400000, banca));
        oeste.add(new Casilla("Solar8", Casilla.TSOLAR, 15, 1600000, banca));
        oeste.add(new Casilla("Trans2", Casilla.TTRANSPORTE, 16, 500000, banca));
        oeste.add(new Casilla("Solar9", Casilla.TSOLAR, 17, 1800000, banca));
        oeste.add(new Casilla("Caja", 18, Casilla.TCOMUNIDAD));
        oeste.add(new Casilla("Solar10", Casilla.TSOLAR, 19, 1800000, banca));
        oeste.add(new Casilla("Solar11", Casilla.TSOLAR, 20, 2200000, banca));
    }

    //Método que inserta las casillas del lado este.
    private void insertarLadoEste() {
        //Le asigno la posición al array (hay 4)
        ArrayList<Casilla>este=posiciones.get(3);
        //Añado cada casilla, le asigno el nombre, el tipo, la posición, el valor y el dueño según corresponda (algunas casillas no tienen dueño o valor por ejemplo)
        este.add(new Casilla("Solar18", Casilla.TSOLAR, 32, 3000000, banca));
        este.add(new Casilla("Solar19", Casilla.TSOLAR, 33, 3000000, banca));
        este.add(new Casilla("Caja", 34, Casilla.TCOMUNIDAD));
        este.add(new Casilla("Solar20", Casilla.TSOLAR, 35, 3200000, banca));
        este.add(new Casilla("Trans4", Casilla.TTRANSPORTE, 36, 500000, banca));
        este.add(new Casilla("Suerte", 37, Casilla.TSUERTE));
        este.add(new Casilla("Solar21", Casilla.TSOLAR, 38, 3500000, banca));
        este.add(new Casilla("Imp2", 39, 2000000));
        este.add(new Casilla("Solar22", Casilla.TSOLAR, 40, 4000000, banca));
    }

    //Para imprimir el tablero, modificamos el método toString().
    public String toString() {
        final int CELL = 15;// ancho fijo de cada casilla
        final int CELDAS_FILA = 11;
        final int ANCHO_LINEA = (CELL + 1) * CELDAS_FILA + 1;

        StringBuilder sb = new StringBuilder();

        // Fila superior: pos 21..31
        sb.append(lineaHorizontal(21, 31, CELL)).append('\n');
        final int anchoInterior = ANCHO_LINEA - 3 * (CELL) + 3; // interior entre barras laterales
        for (int i = 0; i < 9; i++) {
            int izq = 20 - i;    // 20→12
            int der = 32 + i;    // 31→39
            sb.append(celda(izq, CELL))
                    .append(" ".repeat(anchoInterior))
                    .append(celda(der, CELL))
                    .append("\n");
        }

        // Fila inferior: pos 1..10
        sb.append(lineasur(11, 1, CELL)).append('\n');



        return sb.toString();
    }

    /** Devuelve la casilla por posición absoluta (1..40). */
    private Casilla porPos(int pos) {
        for (ArrayList<Casilla> lado : posiciones)
            for (Casilla c : lado)
                if (c != null && c.getPosicion() == pos) return c;
        return null;
    }

    // FIXME
    /** Una celda "|<texto10>" con color (grupo para solares; tipo para el resto). */
    private String celda(int pos, int CELL) {
        Casilla c = porPos(pos);
        String nom = (c == null || c.getNombre() == null) ? ("?" + pos) : c.getNombre();

        // avatares: &A&B...
        String av = "";
        try {
            ArrayList<partida.Avatar> avs = c.getAvatares();
            if (avs != null && !avs.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (partida.Avatar a : avs) sb.append('&').append(a.getID());
                av = " " + sb;
            }
        } catch (Throwable ignored) {}

        String texto = nom + av;
        if (texto.length() > CELL) texto = texto.substring(0, CELL);

        // color
        String color = Valor.TEXTO_BLANCO;
        if (c != null) {
            if (Casilla.TSOLAR.equals(c.getTipo())) {
                Grupo g = c.getGrupo();
                if (g != null) {
                    switch (g.getColorGrupo()) {
                        case "Marron":   color = Valor.RGB_MARRON;   break;
                        case "Cian":     color = Valor.RGB_CIAN;    break;
                        case "Rosa":     color = Valor.RGB_ROSA;  break;
                        case "Naranja":  color = Valor.RGB_NARANJA;  break;
                        case "Rojo":     color = Valor.RGB_ROJO;     break;
                        case "Amarillo": color = Valor.RGB_AMARILLO;  break;
                        case "Verde":    color = Valor.RGB_VERDE;   break;
                        case "Azul":     color = Valor.RGB_AZUL;    break;
                    }
                }
            } else if (Casilla.TSERVICIOS.equals(c.getTipo()))  color = Valor.RGB_SERVICIOS;
            else if (Casilla.TTRANSPORTE.equals(c.getTipo())) color = Valor.RGB_TRANSPORTE;
            else if (Casilla.TIMPUESTO.equals(c.getTipo()))    color = Valor.RGB_IMPUESTO;
            else if (Casilla.TESPECIAL.equals(c.getTipo()))    color = Valor.RGB_ESPECIAL;
            else if (Casilla.TSUERTE.equals(c.getTipo()))      color = Valor.RGB_SUERTE;
            else if (Casilla.TCOMUNIDAD.equals(c.getTipo()))   color = Valor.RGB_COMUNIDAD;
        }

        return color + String.format("%-" + CELL + "s", texto) + Valor.RESET;
    }

    private String lineaHorizontal(int from, int to, int CELL) {
        StringBuilder sb = new StringBuilder();
        for (int p = from; p <= to; p++) sb.append(celda(p, CELL));
        return sb.toString();
    }

    private String lineasur(int from, int to, int CELL) {
        StringBuilder sb = new StringBuilder();
        if(from<=to){
            for (int p = from; p <= to; p++) sb.append(celda(p, CELL));
        }else{
            for(int p=from; p>=to;p--) sb.append(celda(p,CELL));
        }
        return sb.toString();
    }

// === HASTA aquí (fin de reemplazo) ===



    //Método usado para buscar la casilla con el nombre pasado como argumento:
    public Casilla encontrar_casilla(String nombre){
        //Si el nombre es nulo, no se encuentra
        if(nombre==null){
            return null;
        }
        //Recorro el ArrayList completo para encontrar la casilla
        for(ArrayList<Casilla>lado:posiciones){
            for(Casilla c:lado){
                if(c!=null && nombre.equals(c.getNombre())){
                    //Si encuentro una casilla c no nula cuyo nombre coincide con el parámetro de entrada, devuelvo dicha casilla
                    return c;
                }
            }
        }
        //En otro caso, no se encuentra
        return null;
    }

    //A partir de aquí lo añado para ayudar al menu (se puede quitar si no sirve)
    // Devuelve las 4 listas (Sur, Oeste, Norte, Este)
    public ArrayList<ArrayList<Casilla>> getPosiciones() {
        return posiciones;
    }
    //Ojo, no me sirve el getter getPosicion() porque este solo toma una posición, y necesito el array (más posiciones, para una fila/columna)

    // Devuelve las 40 casillas en una sola lista (útil para "listar en venta")
    public ArrayList<Casilla> getTodasLasCasillas() {
        ArrayList<Casilla> todas = new ArrayList<>(40);
        for (ArrayList<Casilla> lado : posiciones) {
            todas.addAll(lado);
        }
        return todas;
    }
    //Aquí tampoco me sirve getPosicion() ni getPosiciones(), porque necesito TODAS las 40 posiciones

    // endregion

}
