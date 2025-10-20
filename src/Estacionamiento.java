import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Estacionamiento {
    private final AtomicInteger capacidadMaxima=new AtomicInteger(5);
    private final Semaphore semaforo=new Semaphore(5);
    private final ArrayList<Coche> coches=new ArrayList<>();

    public synchronized boolean entrar(Coche coche) {
        try {
            // Intentar entrar normalmente
            if (semaforo.tryAcquire(5, TimeUnit.SECONDS)) {
                coches.add(coche);
                System.out.println(coche + " ha entrado. Plazas libres: " + semaforo.availablePermits());
                return true;
            } else {
                // No hay hueco
                if (coche.esVip()) {
                    // Forzar entrada VIP desalojando un coche normal
                    desalojarCocheNormal(coche);
                    System.out.println(coche + " (VIP) ha entrado desalojando a un coche normal.");
                    return true;
                } else {
                    System.out.println(coche + " no pudo entrar (parking lleno).");
                    return false;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void salir(Coche coche) {
        coches.remove(coche);
        semaforo.release();
        System.out.println(coche + " ha salido. Plazas libres: " + semaforo.availablePermits());
    }
    public void desalojarCocheNormal(Coche cocheVip) {
        for(Coche coche:coches){
            if(!coche.esVip()){
                salir(coche);
                coches.add(cocheVip);

            }
        }
    }




}