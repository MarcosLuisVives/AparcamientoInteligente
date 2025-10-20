import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Estacionamiento {
    private final AtomicInteger capacidadMaxima = new AtomicInteger(5);
    private final Semaphore semaforo = new Semaphore(5);
    private final ArrayList<Coche> coches = new ArrayList<>();

    public synchronized boolean entrar(Coche coche) {
        try {
            if (semaforo.tryAcquire(5, TimeUnit.SECONDS)) {
                coches.add(coche);
                System.out.println(coche + " ha entrado. Plazas libres: " + semaforo.availablePermits());
                return true;
            } else {
                if (coche.esVip()) {
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

    public synchronized void salir(Coche coche) {
        if (coches.remove(coche)) {
            semaforo.release();
            System.out.println(coche + " ha salido. Plazas libres: " + semaforo.availablePermits());
        }
    }

    public synchronized void desalojarCocheNormal(Coche cocheVip) {
        for (Coche coche : new ArrayList<>(coches)) {
            if (!coche.esVip()) {
                coches.remove(coche);
                System.out.println(coche + " fue desalojado por " + cocheVip);
                coches.add(cocheVip);

            }
        }

    }
}