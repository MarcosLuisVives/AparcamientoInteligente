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
    private AtomicInteger cochesDesalojados = new AtomicInteger(0);

    private volatile boolean activo = true;

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
                    cochesDesalojados.incrementAndGet();
                    semaforo.tryAcquire(); // el VIP ocupa la plaza liberada
                    System.out.println(coche + " ha entrado desalojando a un coche normal.");
                    cochesAparcaron.incrementAndGet();
                    return true;
                }else {
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
                semaforo.release(); // liberar plaza del coche desalojado
                break;
            }
        }
    }
    public void mostrarEstadisticas() {
        System.out.println("======ESTADO ESTACIONAMIENTO=====");
        System.out.println("Plazas libres: " + semaforo.availablePermits());
        System.out.println("===========");
    }


    public void mostrarEstadisticasFinales() {
        System.out.println("Coches que lograron entrar: " + cochesAparcaron.get());
        System.out.println("Coches que no lograron entrar: " + cochesFuera.get());
        System.out.println("Coches desalojados: " + cochesDesalojados.get());
    }
    // Métodos para controlar el flag
    public boolean isActivo() {
        return activo;
    }

    public void detener() {
        activo = false;
    }

}