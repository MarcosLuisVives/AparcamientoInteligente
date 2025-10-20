import java.util.Random;
public class Coche extends Thread {
    private String nombre;
    private boolean vip;
    private Estacionamiento aparcamiento;
    private final Random random = new Random();

  public Coche(String nombre, Estacionamiento aparcamiento, boolean vip) {
    this.nombre = nombre;
    this.aparcamiento = aparcamiento;
    this.vip =vip;
  }
   public boolean esVip(){
      return vip;
   }

    @Override
    public void run() {
        // Intentar aparcar
        if (!aparcamiento.entrar(nombre)) {
            System.out.println( nombre + " se marcha, no pudo entrar.");
            return;
        }

        // Simular el tiempo estacionado (2–6 segundos)
        int tiempo = random.nextInt(5) + 2;
        System.out.println(nombre + " está estacionado (" + tiempo + "s).");

        try {
            Thread.sleep(tiempo * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Salir del aparcamiento
        aparcamiento.salirCoche(nombre);
    }
    @Override
    public String toString(){
        if(vip){
            return "Coche VIP: " + nombre;
        }else{
            return "Coche Normal: " + nombre;
        }
    }
}
