package d.andrade.tarefas.server;

import java.util.concurrent.BlockingQueue;

public class TarefaConsumir implements Runnable {

  private final BlockingQueue<String> fila;

  public TarefaConsumir(BlockingQueue<String> fila) {
    this.fila = fila;
  }

  @Override
  public void run() {

    try {
      String comando = null;

      while (true) {
        comando = fila.take();
        System.out.println("Consumindo comando " + comando + ", "
            + Thread.currentThread().getName());
        Thread.sleep(10000);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
