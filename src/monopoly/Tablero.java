package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
* OJO, HAY UNA COSA QUE PREGUNTAR, EL GUION PONE LAS CASILLAS DE SUERTE Y CAJA CON EL MISMO NOMBRE
* (NO CON NÚMEROS) POR LO QUE A LA HORA DE BUSCAR LA CASILLA POR EL NOMBRE, PUEDEN HABER PROBLEMAS
* HAY DOS OPCIONES, LLAMARLAS CAJA1,CAJA2,...,SUERTE1,SUERTE2,... O EN EL MÉTODO DE BUSCAR,
* AÑADIR UN CAMPO QUE INDIQUE LA POSICIÓN EXACTA (AUNQUE ENTONCES CREO QUE NO NECESITARÍA EL NOMBRE)
*/

public class Tablero {


    private final ArrayList<ArrayList<Casilla>> posiciones; //Posiciones del tablero: se define como un arraylist de arraylists de casillas (uno por cada lado del tablero).
    private final HashMap<String, Grupo> grupos; //Grupos del tablero, almacenados como un HashMap con clave String (será el color del grupo).
    //La clave es el color del grupo, y el valor es el grupo, que contiene todas las casillas de dicho color (recordar, la tabla HashMap guarda par clave-valor)
    private final Jugador banca; //Un jugador que será la banca.

    public ArrayList<ArrayList<Casilla>> getPosiciones() {
        return posiciones;
    }
    //Ojo, no me sirve el getter getPosicion() porque este solo toma una posición, y necesito el array (más posiciones, para una fila/columna)


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

        //Llamo a 3 métodos (implementados más adelante) para crear el tablero correctamente
        generarCasillas();
        precargarDatosCasillas();
        generarGrupos();
    }

    //Métodos auxiliares para simplificar el código

    private Casilla crearSolar(String nombre, int pos, int valor) {
        return switch (nombre) {
            case "Solar1", "Solar2" -> new Casilla(nombre, Casilla.TSOLAR, pos, valor, banca, 500000, 500000, 100000, 200000);
            case "Solar3", "Solar4", "Solar5" -> new Casilla(nombre, Casilla.TSOLAR, pos, valor, banca, 500000, 500000, 100000, 200000);
            case "Solar6", "Solar7", "Solar8" -> new Casilla(nombre, Casilla.TSOLAR, pos, valor, banca, 1000000, 1000000, 200000, 400000);
            case "Solar9", "Solar10", "Solar11" -> new Casilla(nombre, Casilla.TSOLAR, pos, valor, banca, 1000000, 1000000, 200000, 400000);
            case "Solar12", "Solar13", "Solar14" -> new Casilla(nombre, Casilla.TSOLAR, pos, valor, banca, 1500000, 1500000, 300000, 600000);
            case "Solar15", "Solar16", "Solar17" -> new Casilla(nombre, Casilla.TSOLAR, pos, valor, banca, 1500000, 1500000, 300000, 600000);
            case "Solar18", "Solar19", "Solar20" -> new Casilla(nombre, Casilla.TSOLAR, pos, valor, banca, 2000000, 2000000, 400000, 800000);
            case "Solar21", "Solar22" -> new Casilla(nombre, Casilla.TSOLAR, pos, valor, banca, 2000000, 2000000, 400000, 800000);
            default -> new Casilla(nombre, Casilla.TSOLAR, pos, valor, banca, 0, 0, 0, 0);
        };
    }
    private Casilla crearServicio(String nombre, int pos) {
        return new Casilla(nombre, Casilla.TSERVICIOS, pos, Valor.PRECIO_SERVICIO_TRANSPORTE, banca, 0, 0, 0, 0);
    }

    private Casilla crearTransporte(String nombre, int pos) {
        return new Casilla(nombre, Casilla.TTRANSPORTE, pos, Valor.PRECIO_SERVICIO_TRANSPORTE, banca, 0, 0, 0, 0);
    }



    //Método para crear todas las casillas del tablero. Formado a su vez por cuatro métodos (1/lado).
    private void generarCasillas() {
        //Llamo a 4 métodos (uno para cada lado del tablero) que generen las casillas
        this.insertarLadoSur();
        this.insertarLadoOeste();
        this.insertarLadoNorte();
        this.insertarLadoEste();
    }
    private void precargarDatosCasillas() {
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                if (c == null) continue;

                String tipo = c.getTipo();
                String nombre = c.getNombre();

                switch (tipo) {
                    case Casilla.TSOLAR:
                        // ALQUILER BASE según Apéndice I
                        int alquilerBase = switch (nombre) {
                            case "Solar1" -> 20000;
                            case "Solar2" -> 40000;
                            case "Solar3", "Solar4" -> 60000;
                            case "Solar5" -> 80000;
                            case "Solar6", "Solar7" -> 100000;
                            case "Solar8" -> 120000;
                            case "Solar9", "Solar10" -> 140000;
                            case "Solar11" -> 160000;
                            case "Solar12", "Solar13" -> 180000;
                            case "Solar14" -> 200000;
                            case "Solar15", "Solar16" -> 220000;
                            case "Solar17" -> 240000;
                            case "Solar18", "Solar19" -> 260000;
                            case "Solar20" -> 280000;
                            case "Solar21" -> 350000;
                            case "Solar22" -> 500000;
                            default -> 0;
                        };
                        c.setAlquiler(alquilerBase);
                        c.setHipoteca(c.getValor() / 2);

                        //ALQUILERES DE EDIFICIOS según Apéndice I
                        switch (nombre) {
                            // Marrón
                            case "Solar1" -> {
                                c.setAlquilerCasa(400000);
                                c.setAlquilerHotel(2500000);
                                c.setAlquilerPiscina(500000);
                                c.setAlquilerPistaDeporte(500000);
                            }
                            case "Solar2" -> {
                                c.setAlquilerCasa(800000);
                                c.setAlquilerHotel(4500000);
                                c.setAlquilerPiscina(900000);
                                c.setAlquilerPistaDeporte(900000);
                            }
                            // Cián
                            case "Solar3", "Solar4" -> {
                                c.setAlquilerCasa(1000000);
                                c.setAlquilerHotel(5500000);
                                c.setAlquilerPiscina(1100000);
                                c.setAlquilerPistaDeporte(1100000);
                            }
                            case "Solar5" -> {
                                c.setAlquilerCasa(1250000);
                                c.setAlquilerHotel(6000000);
                                c.setAlquilerPiscina(1200000);
                                c.setAlquilerPistaDeporte(1200000);
                            }
                            // Rosa
                            case "Solar6", "Solar7" -> {
                                c.setAlquilerCasa(1500000);
                                c.setAlquilerHotel(7500000);
                                c.setAlquilerPiscina(1500000);
                                c.setAlquilerPistaDeporte(1500000);
                            }
                            case "Solar8" -> {
                                c.setAlquilerCasa(1750000);
                                c.setAlquilerHotel(9000000);
                                c.setAlquilerPiscina(1800000);
                                c.setAlquilerPistaDeporte(1800000);
                            }
                            // Naranja
                            case "Solar9", "Solar10" -> {
                                c.setAlquilerCasa(1850000);
                                c.setAlquilerHotel(9500000);
                                c.setAlquilerPiscina(1900000);
                                c.setAlquilerPistaDeporte(1900000);
                            }
                            case "Solar11" -> {
                                c.setAlquilerCasa(2000000);
                                c.setAlquilerHotel(10000000);
                                c.setAlquilerPiscina(2000000);
                                c.setAlquilerPistaDeporte(2000000);
                            }
                            // Rojo
                            case "Solar12", "Solar13" -> {
                                c.setAlquilerCasa(2200000);
                                c.setAlquilerHotel(10500000);
                                c.setAlquilerPiscina(2100000);
                                c.setAlquilerPistaDeporte(2100000);
                            }
                            case "Solar14" -> {
                                c.setAlquilerCasa(2325000);
                                c.setAlquilerHotel(11000000);
                                c.setAlquilerPiscina(2200000);
                                c.setAlquilerPistaDeporte(2200000);
                            }
                            // Amarillo
                            case "Solar15", "Solar16" -> {
                                c.setAlquilerCasa(2450000);
                                c.setAlquilerHotel(11500000);
                                c.setAlquilerPiscina(2300000);
                                c.setAlquilerPistaDeporte(2300000);
                            }
                            case "Solar17" -> {
                                c.setAlquilerCasa(2600000);
                                c.setAlquilerHotel(12000000);
                                c.setAlquilerPiscina(2400000);
                                c.setAlquilerPistaDeporte(2400000);
                            }
                            // Verde
                            case "Solar18", "Solar19" -> {
                                c.setAlquilerCasa(2750000);
                                c.setAlquilerHotel(12750000);
                                c.setAlquilerPiscina(2550000);
                                c.setAlquilerPistaDeporte(2550000);
                            }
                            case "Solar20" -> {
                                c.setAlquilerCasa(3000000);
                                c.setAlquilerHotel(14000000);
                                c.setAlquilerPiscina(2800000);
                                c.setAlquilerPistaDeporte(2800000);
                            }
                            // Azul
                            case "Solar21" -> {
                                c.setAlquilerCasa(3250000);
                                c.setAlquilerHotel(17000000);
                                c.setAlquilerPiscina(3400000);
                                c.setAlquilerPistaDeporte(3400000);
                            }
                            case "Solar22" -> {
                                c.setAlquilerCasa(4250000);
                                c.setAlquilerHotel(20000000);
                                c.setAlquilerPiscina(4000000);
                                c.setAlquilerPistaDeporte(4000000);
                            }
                        }
                        break;

                    case Casilla.TTRANSPORTE:
                        c.setAlquiler(Valor.ALQUILER_TRANSPORTE);
                        c.setHipoteca(0);
                        break;

                    case Casilla.TSERVICIOS:
                        c.setAlquiler(Valor.FACTOR_SERVICIO);
                        c.setHipoteca(0);
                        break;

                    case Casilla.TIMPUESTO:
                        c.setAlquiler(c.getValor());
                        break;

                    default:
                        break;
                }
            }
        }
    }
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
    private void insertarLadoNorte() {
        ArrayList<Casilla> norte = posiciones.get(2);

        Casilla parking = new Casilla("Parking", 21, Casilla.TESPECIAL);
        norte.add(parking);
        Casilla.setParkingReferencia(parking);

        norte.add(crearSolar("Solar12", 22, 2200000));
        norte.add(new Casilla("Suerte", 23, Casilla.TSUERTE));
        norte.add(crearSolar("Solar13", 24, 2200000));
        norte.add(crearSolar("Solar14", 25, 2400000));
        norte.add(crearTransporte("Trans3", 26));
        norte.add(crearSolar("Solar15", 27, 2600000));
        norte.add(crearSolar("Solar16", 28, 2600000));
        norte.add(crearServicio("Serv2", 29));
        norte.add(crearSolar("Solar17", 30, 2800000));
        norte.add(new Casilla("IrCarcel", 31, Casilla.TESPECIAL));
    }

    private void insertarLadoSur() {
        ArrayList<Casilla> sur = posiciones.getFirst();
        sur.add(new Casilla("Cárcel", 11, Casilla.TESPECIAL));
        sur.add(crearSolar("Solar5", 10, 1200000));
        sur.add(crearSolar("Solar4", 9, 1000000));
        sur.add(new Casilla("Suerte", 8, Casilla.TSUERTE));
        sur.add(crearSolar("Solar3", 7, 1000000));
        sur.add(crearTransporte("Trans1", 6));
        sur.add(new Casilla("Imp1", 5, 2000000));
        sur.add(crearSolar("Solar2", 4, 600000));
        sur.add(new Casilla("Caja", 3, Casilla.TCOMUNIDAD));
        sur.add(crearSolar("Solar1", 2, 600000));
        sur.add(new Casilla("Salida", 1, Casilla.TESPECIAL));
    }
    private void insertarLadoOeste() {
        ArrayList<Casilla> oeste = posiciones.get(1);
        oeste.add(crearSolar("Solar6", 12, 1400000));
        oeste.add(crearServicio("Serv1", 13));
        oeste.add(crearSolar("Solar7", 14, 1400000));
        oeste.add(crearSolar("Solar8", 15, 1600000));
        oeste.add(crearTransporte("Trans2", 16));
        oeste.add(crearSolar("Solar9", 17, 1800000));
        oeste.add(new Casilla("Caja", 18, Casilla.TCOMUNIDAD));
        oeste.add(crearSolar("Solar10", 19, 1800000));
        oeste.add(crearSolar("Solar11", 20, 2200000));
    }

    private void insertarLadoEste() {
        ArrayList<Casilla> este = posiciones.get(3);
        este.add(crearSolar("Solar18", 32, 3000000));
        este.add(crearSolar("Solar19", 33, 3000000));
        este.add(new Casilla("Caja", 34, Casilla.TCOMUNIDAD));
        este.add(crearSolar("Solar20", 35, 3200000));
        este.add(crearTransporte("Trans4", 36));
        este.add(new Casilla("Suerte", 37, Casilla.TSUERTE));
        este.add(crearSolar("Solar21", 38, 3500000));
        este.add(new Casilla("Imp2", 39, 2000000));
        este.add(crearSolar("Solar22", 40, 4000000));
    }
    //Para imprimir el tablero, modificamos el método toString().
    //devolver una representación en texto legible de un objeto
    public String toString() {
        final int CELL = 15;// ancho fijo de cada casilla
        final int CELDAS_FILA = 11;  // número de casillas por fila (10 casillas + 1 esquina)
        final int ANCHO_LINEA = (CELL + 1) * CELDAS_FILA + 1; // ancho total de la fila

        StringBuilder sb = new StringBuilder();

        // Fila superior: pos 21..31
        sb.append(lineaHorizontal()).append('\n');
        final int anchoInterior = ANCHO_LINEA - 3 * (CELL) + 3; // interior entre barras laterales
        for (int i = 0; i < 9; i++) {
            int izq = 20 - i;    //casillas verticales del lado izquierdo 20→12
            int der = 32 + i;    // casillas verticales del lado izquierdo 31→39
            sb.append(celda(izq)) //casilla izquierda
                    .append(" ".repeat(anchoInterior)) //espacio interior
                    .append(celda(der)) //casilla derecha
                    .append("\n"); //salto de linea
        }

        // Fila inferior: pos 11 a 1
        sb.append(lineasur()).append('\n');

        return sb.toString(); // devuelve el tablero como un String
    }

    // Devuelve la casilla por posición absoluta (1..40). Recorre todas las casillas y devuelve la primera posición que coincide
    private Casilla porPos(int pos) {
        for (ArrayList<Casilla> lado : posiciones)
            for (Casilla c : lado)
                if (c != null && c.getPosicion() == pos) return c;
        return null;
    }


    private String celda(int pos) {
        Casilla c = porPos(pos);
        // Si la casilla no existe o no tiene nombre, muestra '?', y su posicion.
        //Si sí existe, usa su nombre.
        String nom = (c == null || c.getNombre() == null) ? ("?" + pos) : c.getNombre();

        // avatares: &A&B...
        String av = "";
        try {
            assert c != null;
            ArrayList<partida.Avatar> avs = c.getAvatares(); //vamos a crear los avatares del tablero
            if (avs != null && !avs.isEmpty()) { //muestra solo los avatares que tenga la casilla
                StringBuilder sb = new StringBuilder();
                for (partida.Avatar a : avs) sb.append('&').append(a.getID());
                av = " " + sb;
            }
        } catch (Throwable ignored) {}

        String texto = nom + av;
        //Si el texto es demasiado largo, lo recorta para que quepa en el ancho de la celda.
        if (texto.length() > 15) texto = texto.substring(0, 15);

        // color
        String color = getString(c);

        return color + String.format("%-" + 15 + "s", texto) + Valor.RESET;
    }

    private static String getString(Casilla c) {
        String color = Valor.TEXTO_BLANCO;
        if (c != null) {
            if (Casilla.TSOLAR.equals(c.getTipo())) {
                Grupo g = c.getGrupo();
                if (g != null) {
                    color = switch (g.getColorGrupo()) {
                        case "Marron" -> Valor.RGB_MARRON;
                        case "Cian" -> Valor.RGB_CIAN;
                        case "Rosa" -> Valor.RGB_ROSA;
                        case "Naranja" -> Valor.RGB_NARANJA;
                        case "Rojo" -> Valor.RGB_ROJO;
                        case "Amarillo" -> Valor.RGB_AMARILLO;
                        case "Verde" -> Valor.RGB_VERDE;
                        case "Azul" -> Valor.RGB_AZUL;
                        default -> color;
                    };
                }
            } else if (Casilla.TSERVICIOS.equals(c.getTipo()))  color = Valor.RGB_SERVICIOS;
            else if (Casilla.TTRANSPORTE.equals(c.getTipo())) color = Valor.RGB_TRANSPORTE;
            else if (Casilla.TIMPUESTO.equals(c.getTipo()))    color = Valor.RGB_IMPUESTO;
            else if (Casilla.TESPECIAL.equals(c.getTipo()))    color = Valor.RGB_ESPECIAL;
            else if (Casilla.TSUERTE.equals(c.getTipo()))      color = Valor.RGB_SUERTE;
            else if (Casilla.TCOMUNIDAD.equals(c.getTipo()))   color = Valor.RGB_COMUNIDAD;
        }
        return color;
    }

    private String lineaHorizontal() {
        StringBuilder sb = new StringBuilder();
        for (int p = 21; p <= 31; p++) sb.append(celda(p));
        return sb.toString();
    }

    private String lineasur() {
        StringBuilder sb = new StringBuilder();
        for (int p = 11; p >= 1; p--) sb.append(celda(p));
        return sb.toString();
    }



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



}
