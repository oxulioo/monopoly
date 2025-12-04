package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Tablero {

    private final ArrayList<ArrayList<Casilla>> posiciones;
    private final HashMap<String, Grupo> grupos;
    private final Jugador banca;

    public ArrayList<ArrayList<Casilla>> getPosiciones() {
        return posiciones;
    }

    public Tablero(Jugador banca) {
        this.banca = banca;
        this.posiciones = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            this.posiciones.add(new ArrayList<>());
        }
        this.grupos = new HashMap<>();

        generarCasillas();
        // precargarDatosCasillas(); -> YA NO ES NECESARIO, los datos van en el constructor de Solar
        generarGrupos();
    }

    // --- MÉTODOS AUXILIARES ACTUALIZADOS CON LOS NUEVOS CONSTRUCTORES ---

    private Casilla crearSolar(String nombre, int pos, int valor) {
        // Datos específicos extraídos de tu antiguo precargarDatosCasillas
        // Formato: new Solar(nombre, pos, valor, hipoteca, dueno, precioCasa, precioHotel, precioPiscina, precioPista, ALQUILER_BASE)
        // Nota: He calculado la hipoteca como valor/2 y puesto los alquileres base que tenías en el switch.

        int hipoteca = valor / 2;

        return switch (nombre) {
            case "Solar1" -> new Solar(nombre, pos, valor, hipoteca, banca, 500000, 2500000, 500000, 500000, 20000);
            case "Solar2" -> new Solar(nombre, pos, valor, hipoteca, banca, 500000, 2500000, 500000, 500000, 40000);
            case "Solar3", "Solar4" -> new Solar(nombre, pos, valor, hipoteca, banca, 500000, 2500000, 500000, 500000, 60000);
            case "Solar5" -> new Solar(nombre, pos, valor, hipoteca, banca, 500000, 2500000, 500000, 500000, 80000);
            case "Solar6", "Solar7" -> new Solar(nombre, pos, valor, hipoteca, banca, 1000000, 5000000, 1000000, 1000000, 100000);
            case "Solar8" -> new Solar(nombre, pos, valor, hipoteca, banca, 1000000, 5000000, 1000000, 1000000, 120000);
            case "Solar9", "Solar10" -> new Solar(nombre, pos, valor, hipoteca, banca, 1000000, 5000000, 1000000, 1000000, 140000);
            case "Solar11" -> new Solar(nombre, pos, valor, hipoteca, banca, 1000000, 5000000, 1000000, 1000000, 160000);
            case "Solar12", "Solar13" -> new Solar(nombre, pos, valor, hipoteca, banca, 1500000, 7500000, 1500000, 1500000, 180000);
            case "Solar14" -> new Solar(nombre, pos, valor, hipoteca, banca, 1500000, 7500000, 1500000, 1500000, 200000);
            case "Solar15", "Solar16" -> new Solar(nombre, pos, valor, hipoteca, banca, 1500000, 7500000, 1500000, 1500000, 220000);
            case "Solar17" -> new Solar(nombre, pos, valor, hipoteca, banca, 1500000, 7500000, 1500000, 1500000, 240000);
            case "Solar18", "Solar19" -> new Solar(nombre, pos, valor, hipoteca, banca, 2000000, 10000000, 2000000, 2000000, 260000);
            case "Solar20" -> new Solar(nombre, pos, valor, hipoteca, banca, 2000000, 10000000, 2000000, 2000000, 280000);
            case "Solar21" -> new Solar(nombre, pos, valor, hipoteca, banca, 2000000, 10000000, 2000000, 2000000, 350000);
            case "Solar22" -> new Solar(nombre, pos, valor, hipoteca, banca, 2000000, 10000000, 2000000, 2000000, 500000);
            default -> null;
        };
    }

    private Casilla crearServicio(String nombre, int pos) {
        // nombre, pos, valor, hipoteca, dueño
        return new Servicio(nombre, pos, Valor.PRECIO_SERVICIO_TRANSPORTE, 0, banca);
    }

    private Casilla crearTransporte(String nombre, int pos) {
        // nombre, pos, valor, hipoteca, dueño
        return new Transporte(nombre, pos, Valor.PRECIO_SERVICIO_TRANSPORTE, 0, banca);
    }

    // --- GENERACIÓN DE CASILLAS (Insertando las nuevas clases) ---

    private void generarCasillas() {
        this.insertarLadoSur();
        this.insertarLadoOeste();
        this.insertarLadoNorte();
        this.insertarLadoEste();
    }

    private void insertarLadoSur() {
        ArrayList<Casilla> sur = posiciones.getFirst();
        sur.add(new Salida(1)); // Antes: new Casilla("Salida", 1, TESPECIAL)
        sur.add(crearSolar("Solar1", 2, 600000));
        sur.add(new CajaComunidad(3)); // Antes: new Casilla("Caja", 3, TCOMUNIDAD)
        sur.add(crearSolar("Solar2", 4, 600000));
        sur.add(new Impuesto("Imp1", 5, 2000000)); // Antes: new Casilla("Imp1", 5, 2000000)
        sur.add(crearTransporte("Trans1", 6));
        sur.add(crearSolar("Solar3", 7, 1000000));
        sur.add(new Suerte(8)); // Antes: new Casilla("Suerte", 8, TSUERTE)
        sur.add(crearSolar("Solar4", 9, 1000000));
        sur.add(crearSolar("Solar5", 10, 1200000));
        sur.add(new Carcel(11)); // Antes: new Casilla("Cárcel", 11, TESPECIAL)
    }

    private void insertarLadoOeste() {
        ArrayList<Casilla> oeste = posiciones.get(1);
        oeste.add(crearSolar("Solar6", 12, 1400000));
        oeste.add(crearServicio("Serv1", 13));
        oeste.add(crearSolar("Solar7", 14, 1400000));
        oeste.add(crearSolar("Solar8", 15, 1600000));
        oeste.add(crearTransporte("Trans2", 16));
        oeste.add(crearSolar("Solar9", 17, 1800000));
        oeste.add(new CajaComunidad(18));
        oeste.add(crearSolar("Solar10", 19, 1800000));
        oeste.add(crearSolar("Solar11", 20, 2200000));
    }

    private void insertarLadoNorte() {
        ArrayList<Casilla> norte = posiciones.get(2);

        Parking parking = new Parking(21, 0); // Iniciamos parking con bote 0
        norte.add(parking);
        Casilla.setParkingReferencia(parking);

        norte.add(crearSolar("Solar12", 22, 2200000));
        norte.add(new Suerte(23));
        norte.add(crearSolar("Solar13", 24, 2200000));
        norte.add(crearSolar("Solar14", 25, 2400000));
        norte.add(crearTransporte("Trans3", 26));
        norte.add(crearSolar("Solar15", 27, 2600000));
        norte.add(crearSolar("Solar16", 28, 2600000));
        norte.add(crearServicio("Serv2", 29));
        norte.add(crearSolar("Solar17", 30, 2800000));
        norte.add(new IrCarcel(31)); // Antes: new Casilla("IrCarcel", ...)
    }

    private void insertarLadoEste() {
        ArrayList<Casilla> este = posiciones.get(3);
        este.add(crearSolar("Solar18", 32, 3000000));
        este.add(crearSolar("Solar19", 33, 3000000));
        este.add(new CajaComunidad(34));
        este.add(crearSolar("Solar20", 35, 3200000));
        este.add(crearTransporte("Trans4", 36));
        este.add(new Suerte(37));
        este.add(crearSolar("Solar21", 38, 3500000));
        este.add(new Impuesto("Imp2", 39, 2000000));
        este.add(crearSolar("Solar22", 40, 4000000));
    }

    // --- GRUPOS (Se mantiene igual) ---
    private void generarGrupos() {
        // Marrón
        Casilla s1 = encontrar_casilla("Solar1");
        Casilla s2 = encontrar_casilla("Solar2");
        if (s1 != null && s2 != null) grupos.put("Marron", new Grupo(s1, s2, "Marron"));

        // Cián
        Casilla s3 = encontrar_casilla("Solar3");
        Casilla s4 = encontrar_casilla("Solar4");
        Casilla s5 = encontrar_casilla("Solar5");
        if (s3 != null && s4 != null && s5 != null) grupos.put("Cian", new Grupo(s3, s4, s5, "Cian"));

        // Rosa
        Casilla s6 = encontrar_casilla("Solar6");
        Casilla s7 = encontrar_casilla("Solar7");
        Casilla s8 = encontrar_casilla("Solar8");
        if (s6 != null && s7 != null && s8 != null) grupos.put("Rosa", new Grupo(s6, s7, s8, "Rosa"));

        // Naranja
        Casilla s9 = encontrar_casilla("Solar9");
        Casilla s10 = encontrar_casilla("Solar10");
        Casilla s11 = encontrar_casilla("Solar11");
        if (s9 != null && s10 != null && s11 != null) grupos.put("Naranja", new Grupo(s9, s10, s11, "Naranja"));

        // Rojo
        Casilla s12 = encontrar_casilla("Solar12");
        Casilla s13 = encontrar_casilla("Solar13");
        Casilla s14 = encontrar_casilla("Solar14");
        if (s12 != null && s13 != null && s14 != null) grupos.put("Rojo", new Grupo(s12, s13, s14, "Rojo"));

        // Amarillo
        Casilla s15 = encontrar_casilla("Solar15");
        Casilla s16 = encontrar_casilla("Solar16");
        Casilla s17 = encontrar_casilla("Solar17");
        if (s15 != null && s16 != null && s17 != null) grupos.put("Amarillo", new Grupo(s15, s16, s17, "Amarillo"));

        // Verde
        Casilla s18 = encontrar_casilla("Solar18");
        Casilla s19 = encontrar_casilla("Solar19");
        Casilla s20 = encontrar_casilla("Solar20");
        if (s18 != null && s19 != null && s20 != null) grupos.put("Verde", new Grupo(s18, s19, s20, "Verde"));

        // Azul
        Casilla s21 = encontrar_casilla("Solar21");
        Casilla s22 = encontrar_casilla("Solar22");
        if (s21 != null && s22 != null) grupos.put("Azul", new Grupo(s21, s22, "Azul"));
    }

    // --- MÉTODOS DE BÚSQUEDA Y PINTADO (Sin cambios importantes, solo mantenemos la lógica) ---

    public Casilla encontrar_casilla(String nombre) {
        if (nombre == null) return null;
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                if (c != null && nombre.equals(c.getNombre())) {
                    return c;
                }
            }
        }
        return null;
    }

    public HashMap<String, Grupo> getGrupos() {
        return this.grupos;
    }

    @Override
    public String toString() {
        final int CELL = 15;
        final int CELDAS_FILA = 11;
        final int ANCHO_LINEA = (CELL + 1) * CELDAS_FILA + 1;

        StringBuilder sb = new StringBuilder();
        sb.append(lineaHorizontal()).append('\n');
        final int anchoInterior = ANCHO_LINEA - 3 * (CELL) + 3;

        for (int i = 0; i < 9; i++) {
            int izq = 20 - i;
            int der = 32 + i;
            sb.append(celda(izq))
                    .append(" ".repeat(anchoInterior))
                    .append(celda(der))
                    .append("\n");
        }
        sb.append(lineasur()).append('\n');
        return sb.toString();
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

    private Casilla porPos(int pos) {
        for (ArrayList<Casilla> lado : posiciones)
            for (Casilla c : lado)
                if (c != null && c.getPosicion() == pos) return c;
        return null;
    }

    private String celda(int pos) {
        Casilla c = porPos(pos);
        String nom = (c == null || c.getNombre() == null) ? ("?" + pos) : c.getNombre();
        String av = "";
        try {
            if (c != null && c.getAvatares() != null && !c.getAvatares().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Avatar a : c.getAvatares()) sb.append('&').append(a.getID());
                av = " " + sb;
            }
        } catch (Exception _){}

        String texto = nom + av;
        if (texto.length() > 15) texto = texto.substring(0, 15);

        String color = getString(c);
        return color + String.format("%-" + 15 + "s", texto) + Valor.RESET;
    }

    private static String getString(Casilla c) {
        if (c == null) return Valor.TEXTO_BLANCO;

        // Usamos los Strings estáticos de Casilla para comparar tipos,
        // ya que la propiedad 'tipo' la mantuvimos en la clase base.
        if (Casilla.TSOLAR.equals(c.getTipo())) {
            // Ojo: ahora 'c' es Casilla, pero si es Solar podemos acceder a su grupo desde Propiedad
            if (c instanceof Solar) {
                Grupo g = ((Solar)c).getGrupo();
                if (g != null) {
                    return switch (g.getColorGrupo()) {
                        case "Marron" -> Valor.RGB_MARRON;
                        case "Cian" -> Valor.RGB_CIAN;
                        case "Rosa" -> Valor.RGB_ROSA;
                        case "Naranja" -> Valor.RGB_NARANJA;
                        case "Rojo" -> Valor.RGB_ROJO;
                        case "Amarillo" -> Valor.RGB_AMARILLO;
                        case "Verde" -> Valor.RGB_VERDE;
                        case "Azul" -> Valor.RGB_AZUL;
                        default -> Valor.TEXTO_BLANCO;
                    };
                }
            }
        } else if (Casilla.TSERVICIOS.equals(c.getTipo())) return Valor.RGB_SERVICIOS;
        else if (Casilla.TTRANSPORTE.equals(c.getTipo())) return Valor.RGB_TRANSPORTE;
        else if (Casilla.TIMPUESTO.equals(c.getTipo())) return Valor.RGB_IMPUESTO;
        else if (Casilla.TESPECIAL.equals(c.getTipo())) return Valor.RGB_ESPECIAL;
        else if (Casilla.TSUERTE.equals(c.getTipo())) return Valor.RGB_SUERTE;
        else if (Casilla.TCOMUNIDAD.equals(c.getTipo())) return Valor.RGB_COMUNIDAD;

        return Valor.TEXTO_BLANCO;
    }
}