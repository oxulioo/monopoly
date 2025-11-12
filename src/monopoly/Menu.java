package monopoly;
import partida.*;


public class Menu {

    private final Juego juego;

    // Constructor that receives the Juego instance
    public Menu(Juego juego) {
        this.juego = juego;
    }


    /*Método que interpreta el comando introducido y toma la acción correspondiente.
     * Parámetro: cadena de caracteres (el comando).
     */
    public void analizarComando(String comando) {
        if (comando == null) return;
        if (comando.isEmpty()) return;

        // Exige exactamente "comandos" (en minúsculas) al principio
        if (comando.startsWith("comandos ")) {
            String ruta = comando.substring(9); // NO trim: se usa tal cual
            ejecutarFichero(ruta);
            return;
        }
        // crear jugador <Nombre> <tipoAvatar> (sin trim ni validación)
        if (comando.startsWith("crear jugador ")) {
            String resto = comando.substring("crear jugador ".length());
            int idx = resto.lastIndexOf(' ');
            if (idx <= 0 || idx == resto.length() - 1) {
                System.out.println("Uso: crear jugador <Nombre> <tipoAvatar>");
                return;
            }
            String nombre = resto.substring(0, idx);
            String tipo = resto.substring(idx + 1);
            juego.crearJugador(nombre, tipo);
            return;
        }

        // jugador: muestra quién tiene el turno
        switch (comando) {
            case "jugador" -> {
                juego.mostrarJugadorActual();
                return;
            }


            //listar jugadores
            case "listar jugadores" -> {
                juego.listarJugadores();
                return;
            }


            // lanzar dados
            case "lanzar dados" -> {
                juego.lanzarDados();
                return;
            }
        }

        if (comando.startsWith("lanzar dados ")) {
            String resto = comando.substring("lanzar dados ".length()); // p.ej. "3+4"
            int mas = resto.indexOf('+');
            if (mas > 0 && mas < resto.length() - 1) {
                String s1 = resto.substring(0, mas);
                String s2 = resto.substring(mas + 1);
                try {
                    int d1 = Integer.parseInt(s1);
                    int d2 = Integer.parseInt(s2);
                    juego.lanzarDadosForzado(d1, d2);
                    return;
                } catch (NumberFormatException nfe) {
                    System.out.println("Uso: lanzar dados X+Y  (X e Y enteros)");
                    return;
                }
            }
            // si no trae '+', dejará pasar al caso normal (lanzar dados)
        }


        // acabar turno: pasa el turno al siguiente jugador
        if (comando.equals("acabar turno")) {
            juego.acabarTurno();
            return;
        }

        // salir cárcel: pagas 500.000 y quedas libre
        if (comando.equals("salir carcel") || comando.equals("salir cárcel")) {
            juego.salirCarcel();
            return;
        }

        // describir jugador <Nombre>
        if (comando.startsWith("describir jugador ")) {
            String nombreArg = comando.substring("describir jugador ".length());
            juego.descJugador(nombreArg);
            return;
        }

        // describir avatar <ID>
        if (comando.startsWith("describir avatar ")) {
            String id = comando.substring("describir avatar ".length()); // tal cual
            juego.descAvatar(id);
            return;
        }

        // listar avatares
        if (comando.equals("listar avatares")) {
            juego.listarAvatares();
            return;
        }


        // describir <Casilla>
        if (comando.startsWith("describir ")) {
            // si más adelante añades 'describir jugador ...', ese tendrá su propio if antes
            String nombreCasilla = comando.substring("describir ".length());
            juego.descCasilla(nombreCasilla);
            return;
        }

        // comprar <Propiedad>
        if (comando.startsWith("comprar ")) {
            String nombreProp = comando.substring("comprar ".length());
            juego.comprar(nombreProp);
            return;
        }

        // listar en venta
        if (comando.equals("listar enventa")) {
            juego.listarVenta();
            return;
        }

        //  ver tablero
        if (comando.equals("ver tablero")) {
            juego.verTablero();

        }


        if (comando.equals("edificar casa")) {
            juego.edificarCasa();
        }
        if (comando.equals("edificar hotel")) {
            juego.edificarHotel();
        }
        if (comando.equals("edificar piscina")) {
            juego.edificarPiscina();
        }

        if (comando.equals("edificar pista deporte")) {
            juego.edificarPista();
        }

        if (comando.equals("listar edificios")) {
            juego.listarEdificios(null);
        }
        if (comando.startsWith("listar edificios ")) {
            String nombreColor = comando.substring("listar edificios ".length());
            juego.listarEdificios(nombreColor);
        }
        if (comando.startsWith("hipotecar ")) {
            String nombreProp = comando.substring("hipotecar ".length());
            juego.hipotecar(nombreProp);
        }
        if(comando.startsWith("deshipotecar ")){
            String nombreProp= comando.substring("deshipotecar ".length());
            juego.deshipotecar(nombreProp);
        }
        if(comando.startsWith("vender ")){//La estructura es vender casas Solar1 3

            String[] partes = comando.substring(7).split(" ");//Elimino los 7 primeros caracteres, es decir, "vender ", quedando la información
            //El split(" "); Lo que hace es, con el resto que queda, separarlo por espacios
            if(partes.length >= 3) {//Si no hay tres partes en la entrada, no es válida
                String tipo = partes[0]; //"casas" por ejemplo
                String solar = partes[1]; //"Solar1" por ejemplo
                int cantidad = Integer.parseInt(partes[2]); // 3
                juego.venderPropiedad(tipo, solar, cantidad);
            } else {
                System.out.println("Formato incorrecto. Uso: vender <tipo> <solar> <cantidad>");
            }
        }
       if(comando.equals("estadisticas")){
           // juego.describirEstadisticas();
        }
        if(comando.startsWith("estadisticas ")){
          //  juego.describirEstadisticasJugador();
        }

    }

    // Lector de comandos por consola
    public void run() {
        java.util.Scanner sc = new java.util.Scanner(System.in);
        System.out.println("Monopoly listo. Escribe comandos. (\"salir\" para terminar)");
        System.out.println("Ejemplos: ver tablero | crear jugador Ana coche | lanzar dados | comprar Solar1");

        while (true) {
            System.out.print("> ");
            if (!sc.hasNextLine()) break;
            String linea = sc.nextLine().trim();
            if (linea.isEmpty()) continue;
            if (linea.equalsIgnoreCase("salir")) {
                System.out.println("¡Hasta luego!");
                break;
            }
            try {
                analizarComando(linea);
            } catch (Exception e) {
                System.out.println("Error procesando comando: " + e.getMessage());
            }
        }
    }

    private void ejecutarFichero(String ruta) {
        try (java.util.Scanner sc = new java.util.Scanner(new java.io.File(ruta))) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                this.analizarComando(linea);
            }
        } catch (Exception e) {
            System.out.println("Error leyendo: " + ruta);
        }
    }
}







