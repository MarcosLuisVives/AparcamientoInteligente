import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        Estacionamiento estacionamiento = new Estacionamiento();
        ArrayList<Coche>coches=new ArrayList<>();
        for(int i = 0; i < 15; i++){
            Coche coche = new Coche("Coche " + i, estacionamiento, i%3==0);
            coche.start();
            coches.add(coche);
        }
        Thread hilo = new Thread(() -> {
            // Código en segundo plano
            System.out.println("======ESTADO ESTACIONAMIENTO=====");
            while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                estacionamiento.mostrarEstadisticas();
            }
        });
        hilo.start();
        for (Coche c:coches){
            try {
                c.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
       estacionamiento.mostrarEstadisticasFinales();
    }
}
