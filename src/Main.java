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
        for (Coche c:coches){
            try {
                c.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
       estacionamiento.mostrarEstadisticas();
    }
}
