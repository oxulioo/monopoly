package monopoly.consola;

import monopoly.Juego;

import java.util.Scanner;

public class ConsolaNormal implements Consola {
    private final Scanner sc = new Scanner(System.in); //leemos lo que se escribe por teclado

    //reescribimos los metodos de consola, les damos una implementacion que no tienen por ser consola una interfaz
    @Override
    public void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public void imprimirSinSalto(String mensaje){
        System.out.print(mensaje);
    }

    @Override
    public String leer(String descripcion) {
        if (descripcion != null) {
            Juego.consola.imprimirSinSalto(descripcion);
        }
        return sc.nextLine(); //lee toda la linea
    }
}