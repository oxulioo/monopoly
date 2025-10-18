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
    private Jugador banca; //Un jugador que será la banca.

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
        this.grupos=new HashMap<>();

        generarCasillas();
        precargarAlquileres();
        generarGrupos();
    }

    // endregion

    // region ==== MÉTODOS ====

    //Método para crear todas las casillas del tablero. Formado a su vez por cuatro métodos (1/lado).
    private void generarCasillas() {
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
                    float alquiler = 0f;
                    switch (nombre) {
                        // Marrón
                        case "Solar1":  alquiler =  20000f;  break;
                        case "Solar2":  alquiler =  40000f;  break;
                        // Cián
                        case "Solar3":  alquiler =  60000f;  break;
                        case "Solar4":  alquiler =  60000f;  break;
                        case "Solar5":  alquiler =  80000f;  break;
                        // Rosa
                        case "Solar6":  alquiler = 100000f;  break;
                        case "Solar7":  alquiler = 100000f;  break;
                        case "Solar8":  alquiler = 120000f;  break;
                        // Naranja
                        case "Solar9":  alquiler = 140000f;  break;
                        case "Solar10": alquiler = 140000f;  break;
                        case "Solar11": alquiler = 160000f;  break;
                        // Rojo
                        case "Solar12": alquiler = 180000f;  break;
                        case "Solar13": alquiler = 180000f;  break;
                        case "Solar14": alquiler = 200000f;  break;
                        // Amarillo
                        case "Solar15": alquiler = 220000f;  break;
                        case "Solar16": alquiler = 220000f;  break;
                        case "Solar17": alquiler = 240000f;  break;
                        // Verde
                        case "Solar18": alquiler = 260000f;  break;
                        case "Solar19": alquiler = 260000f;  break;
                        case "Solar20": alquiler = 280000f;  break;
                        // Azul
                        case "Solar21": alquiler = 350000f;  break;
                        case "Solar22": alquiler = 500000f;  break;
                        default:        alquiler = 0f;       break;// por si añadís más adelante
                    }
                    c.setAlquiler(alquiler);                       // Alquiler base exacto (sin edificios)
                    c.setHipoteca(Math.max(0f, c.getValor() / 2f));// Hipoteca = 50% del precio (Apéndice I)
                }

                // === TRANSPORTE: precio de compra 500.000; alquiler fijo 250.000 en Parte 1 ===
                else if (Casilla.TTRANSPORTE.equals(tipo)) {
                    // El constructor ya puso 500.000, por si acaso aseguramos:
                    if (c.getValor() <= 0) c.setValor(500000f);
                    c.setAlquiler(Valor.ALQUILER_TRANSPORTE);      // 250.000
                    c.setHipoteca(0f);                              // No hipotecable en esta parte
                }

                // === SERVICIOS: precio de compra 500.000; alquiler se calcula con la tirada ===
                else if (Casilla.TSERVICIOS.equals(tipo)) {
                    if (c.getValor() <= 0) c.setValor(500000f);
                    c.setAlquiler(0f);                              // Se calcula: 4 * tirada * FACTOR_SERVICIO
                    c.setHipoteca(0f);                              // No hipotecable en esta parte
                }

                // === IMPUESTOS / ESPECIALES / SUERTE / COMUNIDAD ===
                // Impuestos ya traen la "cantidad a pagar" en el campo 'alquiler' desde el constructor.
                // Parking acumula bote en 'valor' cuando alguien paga impuestos/carta; aquí no tocamos nada.
            }
        }
    }

    //Esta la escribo yo, no estaba en el esqueleto, MIRAR BIEN
    private void generarGrupos(){
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
        ArrayList<Casilla>norte=posiciones.get(2);
        norte.add(new Casilla("Parking", Casilla.TESPECIAL, 21, null));
        norte.add(new Casilla("Solar12", Casilla.TSOLAR, 22, 2200000, null));
        norte.add(new Casilla("Suerte", Casilla.TSUERTE, 23, null));
        norte.add(new Casilla("Solar13", Casilla.TSOLAR, 24, 2200000, null));
        norte.add(new Casilla("Solar14", Casilla.TSOLAR, 25, 2400000, null));
        norte.add(new Casilla("Trans3", Casilla.TTRANSPORTE, 26, 500000, null));
        norte.add(new Casilla("Solar15", Casilla.TSOLAR, 27, 2600000, null));
        norte.add(new Casilla("Solar16", Casilla.TSOLAR, 28, 2600000, null));
        norte.add(new Casilla("Serv2", Casilla.TSERVICIOS, 29, 500000, null));
        norte.add(new Casilla("Solar17", Casilla.TSOLAR, 30, 2800000, null));
    }

    //Método para insertar las casillas del lado sur.
    private void insertarLadoSur() {
        ArrayList<Casilla>sur=posiciones.get(0);
        sur.add(new  Casilla("Salida", Casilla.TESPECIAL, 1, null));
        sur.add(new Casilla("Solar1", Casilla.TSOLAR, 2, 600000, null));
        sur.add(new Casilla("Caja", Casilla.TCOMUNIDAD, 3, null));
        sur.add(new Casilla("Solar2", Casilla.TSOLAR, 4, 600000, null));
        sur.add(new Casilla("Imp1", 5, 2000000, null));
        sur.add(new Casilla("Trans1", Casilla.TTRANSPORTE, 6, 500000, null));
        sur.add(new Casilla("Solar3", Casilla.TSOLAR, 7, 1000000, null));
        sur.add(new Casilla("Suerte", Casilla.TSUERTE, 8, null));
        sur.add(new Casilla("Solar4", Casilla.TSOLAR, 9, 1000000, null));
        sur.add(new Casilla("Solar5", Casilla.TSOLAR, 10, 1200000, null));
    }

    //Método que inserta casillas del lado oeste.
    private void insertarLadoOeste() {
        ArrayList<Casilla>oeste=posiciones.get(1);
        oeste.add(new Casilla("Cárcel", Casilla.TESPECIAL, 11, null));
        oeste.add(new Casilla("Solar6", Casilla.TSOLAR, 12, 1400000, null));
        oeste.add(new Casilla("Serv1", Casilla.TSERVICIOS, 13, 500000,null));
        oeste.add(new Casilla("Solar7", Casilla.TSOLAR, 14, 1400000, null));
        oeste.add(new Casilla("Solar8", Casilla.TSOLAR, 15, 1600000, null));
        oeste.add(new Casilla("Trans2", Casilla.TTRANSPORTE, 16, 500000, null));
        oeste.add(new Casilla("Solar9", Casilla.TSOLAR, 17, 1800000, null));
        oeste.add(new Casilla("Caja", Casilla.TCOMUNIDAD, 18, null));
        oeste.add(new Casilla("Solar10", Casilla.TSOLAR, 19, 1800000, null));
        oeste.add(new Casilla("Solar11", Casilla.TSOLAR, 20, 2200000, null));
    }

    //Método que inserta las casillas del lado este.
    private void insertarLadoEste() {
        ArrayList<Casilla>este=posiciones.get(3);
        este.add(new Casilla("IrCarcel", Casilla.TESPECIAL, 31, null));
        este.add(new Casilla("Solar18", Casilla.TSOLAR, 32, 3000000, null));
        este.add(new Casilla("Solar19", Casilla.TSOLAR, 33, 3000000, null));
        este.add(new Casilla("Caja", Casilla.TCOMUNIDAD, 34, null));
        este.add(new Casilla("Solar20", Casilla.TSOLAR, 35, 3200000, null));
        este.add(new Casilla("Trans4", Casilla.TTRANSPORTE, 36, 500000, null));
        este.add(new Casilla("Suerte", Casilla.TSUERTE, 37, null));
        este.add(new Casilla("Solar21", Casilla.TSOLAR, 38, 3500000, null));
        este.add(new Casilla("Imp2", 39, 2000000, null));
        este.add(new Casilla("Solar22", Casilla.TSOLAR, 40, 4000000, null));
    }

    //Para imprimir el tablero, modificamos el método toString().
    @Override
    public String toString() {
        String[] lados = {"Sur", "Oeste", "Norte", "Este"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < posiciones.size(); i++) {
            sb.append("== Lado ").append(lados[i]).append(" ==\n");
            for (Casilla c : posiciones.get(i)) {
                // color por tipo
                String color = Valor.WHITE;
                if (Casilla.TSOLAR.equals(c.getTipo()))          color = Valor.YELLOW;
                else if (Casilla.TESPECIAL.equals(c.getTipo()))  color = Valor.CYAN;
                else if (Casilla.TTRANSPORTE.equals(c.getTipo()))color = Valor.BLUE;
                else if (Casilla.TSERVICIOS.equals(c.getTipo())) color = Valor.PURPLE;
                else if (Casilla.TIMPUESTO.equals(c.getTipo()))  color = Valor.RED;
                else if (Casilla.TSUERTE.equals(c.getTipo()))    color = Valor.GREEN;
                else if (Casilla.TCOMUNIDAD.equals(c.getTipo())) color = Valor.WHITE;

                // avatares en la casilla: &A&B...
                String avStr = "";
                try {
                    java.util.ArrayList<partida.Avatar> avs = c.getAvatares();
                    if (avs != null && !avs.isEmpty()) {
                        StringBuilder sa = new StringBuilder();
                        for (partida.Avatar a : avs) {
                            sa.append('&').append(a.getID());
                        }
                        avStr = " " + sa;
                    }
                } catch (Throwable ignored) {}

                sb.append(color)
                        .append(String.format("%02d %s%s [%s]%n",
                                c.getPosicion(), c.getNombre(), avStr, c.getTipo()))
                        .append(Valor.RESET);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    
    //Método usado para buscar la casilla con el nombre pasado como argumento:
    public Casilla encontrar_casilla(String nombre){
        if(nombre==null){
            return null;
        }
        //Recorro el ArrayList completo para encontrar la casilla
        for(ArrayList<Casilla>lado:posiciones){
            for(Casilla c:lado){
                if(c!=null && nombre.equals(c.getNombre())){
                    return c;
                }
            }
        }
        return null;
    }

    //A partir de aquí lo añado para ayudar al menu (se puede quitar si no sirve)
    // Devuelve las 4 listas (Sur, Oeste, Norte, Este)
    public ArrayList<ArrayList<Casilla>> getPosiciones() {
        return posiciones;
    }

    // Devuelve las 40 casillas en una sola lista (útil para "listar en venta")
    public ArrayList<Casilla> getTodasLasCasillas() {
        ArrayList<Casilla> todas = new ArrayList<>(40);
        for (ArrayList<Casilla> lado : posiciones) {
            todas.addAll(lado);
        }
        return todas;
    }

    // endregion

}
