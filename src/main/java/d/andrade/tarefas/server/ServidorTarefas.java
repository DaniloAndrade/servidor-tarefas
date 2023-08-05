package d.andrade.tarefas.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServidorTarefas {

  private final ServerSocket server;
  private final ExecutorService executor;
  private AtomicBoolean estaRodando = new AtomicBoolean(true);

  private final BlockingQueue<String> filaCommands ;

  public ServidorTarefas() throws IOException {
    System.out.println("---- Iniciando Servidor ----");
    server = new ServerSocket(12345);
    executor = Executors.newCachedThreadPool(new FabricaDeThread(Executors.defaultThreadFactory()));
    filaCommands = new ArrayBlockingQueue<>(2);
    estaRodando.set(true);
    iniciarConsumidores();
  }

  public void rodar() throws IOException, InterruptedException {

    while (estaRodando.get()) {
      try {
        Socket accept = server.accept();
        System.out.println("Aceitando novo cliente na porta " + accept.getPort());
        executor.execute(new DistribuirTarefas(executor, filaCommands ,accept, this));

      } catch (SocketException e) {
        System.out.println("SocketException, está rodando? " + this.estaRodando);
      }
    }
  }

  public void parar() throws IOException {
    estaRodando.set(false);
    executor.shutdown();
    server.close();
  }

  private void iniciarConsumidores() {
    int qtdConsumidores = 2;
    for (int i = 0; i < qtdConsumidores; i++) {
      TarefaConsumir tarefa = new TarefaConsumir(filaCommands);
      this.executor.execute(tarefa);
    }
  }

  public static void main(String[] args) throws Exception {
    ServidorTarefas servidor = new ServidorTarefas();
    servidor.rodar();
  }



}
