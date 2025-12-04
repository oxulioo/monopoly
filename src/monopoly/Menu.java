package monopoly;
import monopoly.exceptions.*;
import monopoly.exceptions.MonopolyEtseException;


public class Menu {

    private final Juego juego;

    // Constructor that receives the Juego instance
    public Menu(Juego juego) {
        this.juego = juego;
    }


    /*Método que interpreta el comando introducido y toma la acción correspondiente.
     * Parámetro: cadena de caracteres (el comando).
     */
    public void analizarComando(String comando) throws MonopolyEtseException {
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
                throw new AccionInvalidaException("Formato incorrecto. Uso: crear jugador <Nombre> <tipoAvatar>");
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
                    throw new AccionInvalidaException("Los dados deben ser números enteros. Uso: lanzar dados X+Y");
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
        if (comando.startsWith("deshipotecar ")) {
            String nombreProp = comando.substring("deshipotecar ".length());
            juego.deshipotecar(nombreProp);
        }
        if (comando.startsWith("vender ")) {//La estructura es vender casas Solar1 3

            String[] partes = comando.substring(7).split(" ");//Elimino los 7 primeros caracteres, es decir, "vender ", quedando la información
            //El split(" "); Lo que hace es, con el resto que queda, separarlo por espacios
            if (partes.length < 3) {
                throw new AccionInvalidaException("Faltan datos. Uso: vender <tipo> <solar> <cantidad>");
            } else {//Si no hay tres partes en la entrada, no es válida
                String tipo = partes[0]; //"casas" por ejemplo
                String solar = partes[1]; //"Solar1" por ejemplo
                int cantidad = Integer.parseInt(partes[2]); // 3
                try {
                    cantidad = Integer.parseInt(partes[2]);
                } catch (NumberFormatException e) {
                    throw new AccionInvalidaException("La cantidad debe ser un número entero.");
                }
                juego.venderPropiedad(tipo, solar, cantidad);
            }
        }
        if (comando.equals("estadisticas")) {
            juego.estadisticasJuego();
        }
        if (comando.startsWith("estadisticas ")) {
            String nombreJugador = comando.substring("estadisticas ".length());
            juego.estadisticasJugador(nombreJugador);
        }


        if (comando.startsWith("trato ")) {
            juego.proponerTrato(comando);
            return;
        }

        if (comando.startsWith("aceptar trato")) { // ej: aceptar trato1
            String id = comando.substring("aceptar trato".length()).trim();
            juego.aceptarTrato(id);
            return;
        }

        if (comando.startsWith("eliminar trato")) {
            String idtrato = comando.substring("eliminar trato".length()).trim();
            juego.eliminarTrato(idtrato);
            return;
        }

        if (comando.equals("tratos")) {
            juego.listarTratos();
            return;
        }
    }

    // Lector de comandos por consola
    public void run() throws MonopolyEtseException {
        //java.util.Scanner sc = new java.util.Scanner(System.in);
        Juego.consola.imprimir("Monopoly listo. Escribe comandos. (\"salir\" para terminar)");
        Juego.consola.imprimir("Ejemplos: ver tablero | crear jugador Ana coche | lanzar dados | comprar Solar1");

        while (true) {
            String linea = Juego.consola.leer("> ");
            //Juego.consola.imprimir("> ");
            if (linea == null) break;
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            if (linea.equalsIgnoreCase("salir")) {
                Juego.consola.imprimir("¡Hasta luego!");
                break;
            }
            try {
                analizarComando(linea);
            } catch (SaldoInsuficienteException e) { //bancarrota
                // CASO 1: Problemas de dinero (Requisito 29: tratada diferente)
                // Podrías ponerlo en rojo, o sugerir hipotecar
                Juego.consola.imprimir("[!] PROBLEMA DE FONDOS: " + e.getMessage());

            } catch (CompraNoPermitidaException e) {
                // CASO 2: Error específico de compras
                Juego.consola.imprimir("[!] COMPRA RECHAZADA: " + e.getMessage());

            } catch (EdificacionNoPermitidaException e) {
                // CASO 3: Error al edificar
                Juego.consola.imprimir("[!] NO PUEDES CONSTRUIR: " + e.getMessage());

            } catch (AccionInvalidaException e) {
                // CASO 4: Otros errores de reglas (turno, moverse, etc)
                Juego.consola.imprimir("[!] Acción no válida: " + e.getMessage());

            } catch (MonopolyEtseException e) {
                // CASO 5: Cualquier otra excepción propia que se nos haya olvidado
                Juego.consola.imprimir("Error del juego: " + e.getMessage());

            } catch (Exception e) {
                // Error inesperado de Java (Bugs, NullPointer, etc)
                Juego.consola.imprimir("Ocurrió un error interno: " + e.toString());
            }
        }
    }


    private void ejecutarFichero(String ruta) {
        if (ruta == null) {
            Juego.consola.imprimir("Error leyendo: " + ruta);
            return;
        }
        try (java.util.Scanner sc = new java.util.Scanner(new java.io.File(ruta))) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                try {
                    analizarComando(linea);
                } catch (SaldoInsuficienteException e) { //bancarrota
                    // CASO 1: Problemas de dinero (Requisito 29: tratada diferente)
                    // Podrías ponerlo en rojo, o sugerir hipotecar
                    Juego.consola.imprimir("[!] PROBLEMA DE FONDOS: " + e.getMessage());

                } catch (CompraNoPermitidaException e) {
                    // CASO 2: Error específico de compras
                    Juego.consola.imprimir("[!] COMPRA RECHAZADA: " + e.getMessage());

                } catch (EdificacionNoPermitidaException e) {
                    // CASO 3: Error al edificar
                    Juego.consola.imprimir("[!] NO PUEDES CONSTRUIR: " + e.getMessage());

                } catch (AccionInvalidaException e) {
                    // CASO 4: Otros errores de reglas (turno, moverse, etc)
                    Juego.consola.imprimir("[!] Acción no válida: " + e.getMessage());

                } catch (MonopolyEtseException e) {
                    // CASO 5: Cualquier otra excepción propia que se nos haya olvidado
                    Juego.consola.imprimir("Error del juego: " + e.getMessage());

                } catch (Exception e) {
                    // Error inesperado de Java (Bugs, NullPointer, etc)
                    Juego.consola.imprimir("Ocurrió un error interno: " + e.toString());
                }
            }
        } catch (Exception _) {
        }
    }
}

