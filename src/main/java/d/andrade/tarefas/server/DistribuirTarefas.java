package d.andrade.tarefas.server;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DistribuirTarefas implements Runnable {

  private final ExecutorService threadPool;
  private final BlockingQueue<String> filaCommands;
  private Socket socket;
  private final ServidorTarefas servidor;

  public DistribuirTarefas(ExecutorService threadPool, BlockingQueue<String> filaCommands, Socket socket, ServidorTarefas servidor) {
    this.threadPool = threadPool;
    this.filaCommands = filaCommands;
    this.socket = socket;
    this.servidor = servidor;
  }

  @Override
  public void run() {
    System.out.println("Distribuindo as tarefas para o cliente " + socket);
    try (
        Scanner scn = new Scanner(socket.getInputStream());
        PrintStream saidaCliente = new PrintStream(socket.getOutputStream())
    ) {

      while (scn.hasNextLine()) {
        String comando = scn.nextLine();
        System.out.println("Comando recebido: " + comando);

        switch (comando) {
          case "c1": {
            // confirmação do o cliente
            saidaCliente.println("Confirmação do comando c1");
            CommandC1 c1 = new CommandC1(saidaCliente);
            threadPool.execute(c1);
            break;
          }
          case "c2": {
            saidaCliente.println("Confirmação do comando c2");
            CommandC2WS c2 = new CommandC2WS(saidaCliente);
            CommandC2AcessoDB c2DB = new CommandC2AcessoDB(saidaCliente);
            Future<String> fcmdC2ws = threadPool.submit(c2);
            Future<String> fcmdC2db = threadPool.submit(c2DB);
            threadPool.submit(new JuntaResultadosFutureWSFutureBanco(fcmdC2ws, fcmdC2db, saidaCliente));
            break;
          }
          case "c3": {
            this.filaCommands.put(comando); //lembrando, bloqueia se tiver cheia
            saidaCliente.println("Comando c3 adicionado na fila");
            return;
          }
          case "fim": {
            saidaCliente.println("Desligando servidor");
            servidor.parar();
            return;
          }
          default: {
            saidaCliente.println("Comando não encontrado");
          }
        }
      }

      Thread.sleep(20000);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
