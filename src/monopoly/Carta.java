package monopoly;

import partida.Jugador;
import java.util.ArrayList;
import java.util.List;

public class Carta {
    private final int id;
    private final String descripcion;
    private final String tipo; // "Suerte" o "Comunidad"

    // Mazos estáticos para no tener que crearlos cada vez, con todas las cartas de cada tipo
    private static final List<Carta> mazoSuerte = new ArrayList<>();
    private static final List<Carta> mazoComunidad = new ArrayList<>();
    //Valores que ayudarán a seguir el orden pedido de las cartas
    private static int indiceSuerte = 0;
    private static int indiceComunidad = 0;

    static {
        // Inicializar mazo de Suerte (una sola vez)
        mazoSuerte.add(new Carta(1, "Suerte", "Decides hacer un viaje de placer. Avanza hasta Solar19. Si pasas por la casilla de Salida, cobra 2.000.000€."));
        mazoSuerte.add(new Carta(2, "Suerte", "Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€."));
        mazoSuerte.add(new Carta(3, "Suerte", "¡Has ganado el bote de la lotería! Recibe 1.000.000€."));
        mazoSuerte.add(new Carta(4, "Suerte", "Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€."));
        mazoSuerte.add(new Carta(5, "Suerte", "¡Hora punta de tráfico! Retrocede tres casillas."));
        mazoSuerte.add(new Carta(6, "Suerte", "Te multan por usar el móvil mientras conduces. Paga 150.000€."));
        mazoSuerte.add(new Carta(7, "Suerte", "Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprarla. Si tiene dueño, paga al dueño el doble de la operación indicada."));

        // Inicializar mazo de Comunidad
        mazoComunidad.add(new Carta(1, "Comunidad", "Paga 500.000€ por un fin de semana en un balneario de 5 estrellas."));
        mazoComunidad.add(new Carta(2, "Comunidad", "Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€."));
        mazoComunidad.add(new Carta(3, "Comunidad", "Colócate en la casilla de Salida. Cobra 2.000.000€."));
        mazoComunidad.add(new Carta(4, "Comunidad", "Devolución de Hacienda. Cobra 500.000€."));
        mazoComunidad.add(new Carta(5, "Comunidad", "Retrocede hasta Solar1 para comprar antigüedades exóticas."));
        mazoComunidad.add(new Carta(6, "Comunidad", "Ve a Solar20 para disfrutar del San Fermín. Si pasas por la casilla de Salida, cobra 2.000.000€."));
    }

    public Carta(int id, String tipo, String descripcion) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // Método estático para sacar carta (más simple)
    public static Carta sacarCarta(String tipo) {
        if ("Suerte".equals(tipo)) {
            //Selecciono la carta del mazo y actualizo el indice
            Carta carta = mazoSuerte.get(indiceSuerte);
            indiceSuerte = (indiceSuerte + 1) % mazoSuerte.size();
            return carta;
        } else if ("Comunidad".equals(tipo)) {
            //Selecciono la carta del mazo y actualizo el indice
            Carta carta = mazoComunidad.get(indiceComunidad);
            indiceComunidad = (indiceComunidad + 1) % mazoComunidad.size();
            return carta;
        }
        return null;
    }

    // Ejecutar la acción de la carta
    public void ejecutar(Jugador jugador, Juego juego) {
        //Tras seleccionar la carta, la imprimo por pantalla y la ejecuto con el método correspondiente
        System.out.println(jugador.getNombre() + ", elige una carta: " + id);
        System.out.println("Acción: " + descripcion);

        if ("Suerte".equals(tipo)) {
            ejecutarSuerte(jugador, juego);
        } else {
            ejecutarComunidad(jugador, juego);
        }
    }
    //Método para ejecutar una carta de suerte
    private void ejecutarSuerte(Jugador jugador, Juego juego) {
        switch(id) {
            //En el primer caso, muevo al jugador hasta la casilla y cobro la salida en caso de pasar por ella
            case 1: avanzarACasilla(jugador, juego, "Solar19", true);break;
            //En el segundo caso encarcelo, sin cobrar si pasa por la salida
            case 2: juego.enviarACarcel(jugador); break;
            //En el tercer caso gana la loteria, aumentando su fortuna y actualizando su atributo de PremiosInversionesOBote
            case 3: jugador.sumarFortuna(1000000);
                jugador.getEstadisticas().sumarPremiosInversionesOBote(1000000);
                break;
            //En el cuarto caso, paga a todos los jugadores y actualiza sus atributos de PagoTasasImpuestos
            case 4: pagarATodos(jugador, juego, 250000);
                jugador.getEstadisticas().sumarPagoTasasImpuestos(250000L *juego.getJugadoresNum());
                break;
            //En el quinto caso, retrocede tres casillas
            case 5: retrocederCasillas(jugador, juego, 3); break;
            //En el sexto caso, paga una multa y se actualiza su atributo de PagoTasasImpuestos
            case 6: pagarSiPuede(jugador, 150000);
                break;
            //En el séptimo caso, avanza hasta la casilla de transporte más cercana y tiene la posibilidad de comprar o no, pero si tiene dueño, paga el doble
            case 7: avanzarTransporteMasCercano(jugador, juego); break;
        }
    }

    private void ejecutarComunidad(Jugador jugador, Juego juego) {
        switch(id) {
            //Primer caso, pagar impuesto y actualizar estadisticas
            case 1: pagarSiPuede(jugador, 500000); break;
            //Segundo caso, encarcelar sin cobrar salida
            case 2: juego.enviarACarcel(jugador); break;
            //Tercer caso, ir a la casilla de salida y cobrar, actualizando estadísticas
            case 3:
                avanzarACasilla(jugador, juego, "Salida", false);
                jugador.sumarFortuna(2000000);
                jugador.getEstadisticas().sumarPasarPorSalida(2000000);
                break;
            //Cuarto caso, cobras por devolucion de hacienda y actualizas estadisticas
            case 4: jugador.sumarFortuna(500000);
                jugador.getEstadisticas().sumarPremiosInversionesOBote(500000);
                break;
            //Quinto caso, retrocede hasta Solar1 (no puedes cobrar salida por ser la primera posición del tablero)
            case 5: avanzarACasilla(jugador, juego, "Solar1", false); break;
            //Sexto caso, avanzar hasta Solar20, cobrando si pasas por la salida
            case 6: avanzarACasilla(jugador, juego, "Solar20", true); break;
        }
    }

    // Métodos auxiliares simples
    private void avanzarACasilla(Jugador jugador, Juego juego, String casilla, boolean cobrarSalida) {
        juego.moverJugadorACasilla(jugador, casilla, cobrarSalida);
    }

    // metodo para pagarle a otros jugadores el valor (sirve para una carta en concreto)
    private void pagarATodos(Jugador jugador, Juego juego, int cantidad) {
        for (Jugador otro : juego.getJugadores()) {
            if (otro != jugador && !"Banca".equals(otro.getNombre())) {
                boolean ok = jugador.sumarGastos(cantidad);
                if (ok) {
                    otro.sumarFortuna(cantidad);
                    // El receptor sí debe sumar cobro
                    otro.getEstadisticas().sumarCobroDeAlquileres(cantidad);
                }
            }
        }
    }


    private void retrocederCasillas(Jugador jugador, Juego juego, int casillas) {
        // Implementación simple - retrocede en el tablero
        Casilla actual = jugador.getAvatar().getPosicion();
        int nuevaPos = (actual.getPosicion() - casillas - 1 + 40) % 40 + 1;
        juego.moverJugadorAPosicion(jugador, nuevaPos);
    }

    private void avanzarTransporteMasCercano(Jugador jugador, Juego juego) {
        // Posiciones fijas de las casillas de Transporte
        final int[] posTransportes = {6, 16, 26, 36};
        final String[] nomTransportes = {"Trans1", "Trans2", "Trans3", "Trans4"};
        //
        Casilla actual = jugador.getAvatar().getPosicion();
        int minDistancia = 1000; // iniciamos un número muy grande para que no se encuentre nunca
        String transporteMasCercano = nomTransportes[0];//String con el nombre del transporte mas cercano
        int posDestinoFinal = posTransportes[0];//Posicion del destino final

        // 1. Calcular la casilla más cercana
        //Recorremos las 4 posiciones del array de transportes, y almacenaremos la distancia recorrida
        for (int i = 0; i < posTransportes.length; i++) {
            int posDestino = posTransportes[i];
            int distancia;

            //Si esta resta es positiva, entonces nos movemos en el sentido del tablero
            if (posDestino > actual.getPosicion()) {
                distancia = posDestino - actual.getPosicion(); // Movimiento hacia adelante
            } else {
                //Si la recta es negativa, entonces nos movemos en el sentido contrario del tablero
                distancia = (40 - actual.getPosicion()) + posDestino; // Dando la vuelta
            }
            //Si la distacia de la posicion actual del vector de transportes es la menor, actualizamos al menor valor
            if (distancia < minDistancia) {
                minDistancia = distancia;
                transporteMasCercano = nomTransportes[i];
                posDestinoFinal = posDestino;
            }
            //Como esto se repite para los 4 transportes, lo que conseguimos es la posicion de transporte más cercana de todas
        }

        // 2. Mover al jugador y gestionar pago
        Casilla destino = juego.getTablero().encontrar_casilla(transporteMasCercano);//Busco la casilla de transporte mas cercana
        if (destino == null) return;

        System.out.println("Avanzando al transporte más cercano: " + destino.getNombre());

        // 3. Comprobar si pasa por Salida (antes de mover)
        boolean cobraSalida = posDestinoFinal < actual.getPosicion();
        if (cobraSalida) {
            //En caso afirmativo, actualizo las estadisticas y sumo la fortuna
            jugador.sumarFortuna(Valor.SUMA_VUELTA);
            jugador.getEstadisticas().sumarPasarPorSalida(Valor.SUMA_VUELTA);
            jugador.setVueltas(jugador.getVueltas() + 1);
            System.out.println(jugador.getNombre() + " pasa por salida y recibe " + Valor.SUMA_VUELTA + "€.");
        }

        // 4. Mover el avatar
        jugador.getAvatar().setPosicion(destino);
        destino.incrementarVisita(); //incrementamos visitas

        // 5. Lógica de pago de la carta
        Jugador dueno = destino.getDueno();
        if (dueno != null && dueno != juego.getBanca() && dueno != jugador) {
            // "paga al dueño el doble de la operación indicada"
            System.out.println("La casilla pertenece a " + dueno.getNombre() + ". ¡Pagas el doble de alquiler!");

            // Reutilizamos el método existente con factor 2
            jugador.pagarAlquiler(destino, 2);

        } else if (dueno == null || dueno == juego.getBanca()) {
            System.out.println("La casilla no tiene dueño. Puedes comprarla en tu turno.");
        }
    }

    private void pagarSiPuede(Jugador jugador, int cantidad) {
        boolean ok = jugador.sumarGastos(cantidad);
        if (ok) {
            // Estadística correcta
            jugador.getEstadisticas().sumarPagoTasasImpuestos(cantidad);

            // Añadir al parking
            Casilla parking = Casilla.getParkingReferencia();
            if (parking != null) parking.sumarValor(cantidad);
        } else {
            System.out.println(jugador.getNombre() + " no tiene suficiente dinero. Debe hipotecar propiedades.");
        }
    }

}