import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Estacionamiento {
    private final int capacidadMaxima = 5;
    private final Semaphore semaforo = new Semaphore(5);
    private final ArrayList<Coche> coches = new ArrayList<>();
    private AtomicInteger cochesAparcaron=new AtomicInteger(0);
    private AtomicInteger cochesFuera=new AtomicInteger(0);

    public synchronized boolean entrar(Coche coche) {
        try {
            if (semaforo.tryAcquire(5, TimeUnit.SECONDS)) {
                coches.add(coche);
                System.out.println(coche + " ha entrado ");
                cochesAparcaron.incrementAndGet();
                return true;
            } else {
                if (coche.esVip()) {
                    desalojarCocheNormal(coche);
                    System.out.println(coche + "ha entrado desalojando a un coche normal.");
                    return true;

                } else {
                    System.out.println(coche + " no pudo entrar (parking lleno).");
                    cochesFuera.incrementAndGet();
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
            System.out.println(coche + " ha salido");
        }
    }

    public synchronized void desalojarCocheNormal(Coche cocheVip) {
        for (Coche coche : new ArrayList<>(coches)) {
            if (!coche.esVip()) {
                coches.remove(coche);
                System.out.println(coche + " fue desalojado por " + cocheVip);
                coches.add(cocheVip);
                break;

            }
        }

    }
    public void mostrarEstadisticas(){
        System.out.println("Plazas libres: "+semaforo.availablePermits());
    }
    public void mostrarEstadisticasFinales(){
        System.out.println("Coches que lograron entrar: " + cochesAparcaron.get());
        System.out.println("Coches que no lograron entrar: " + cochesFuera.get());
    }
}