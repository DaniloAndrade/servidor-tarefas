package d.andrade.tarefas.client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteTarefa {

  public static void main(String[] args) throws IOException, InterruptedException {
    PrintStream saida = null;
    Scanner scanner = null;
    try (Socket socket = new Socket("localhost", 12345)) {

      System.out.println("Conexão Estabelecida");
      Thread threadSendCommand = new Thread(() -> sendCommand(socket));

      Thread threadResponseCommand = new Thread(() -> responseCommand(socket));

      threadSendCommand.start();
      threadResponseCommand.start();

      threadSendCommand.join();
      System.out.println("Fechando o socket do cliente");
    }
  }

  private static void responseCommand(Socket socket) {
    try (Scanner respServidor = new Scanner(socket.getInputStream());) {
      while (respServidor.hasNextLine()) {
        System.out.println(respServidor.nextLine());
      }
    }catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private static void sendCommand(Socket socket) {
    try (
        PrintStream saida = new PrintStream(socket.getOutputStream());
        Scanner scanner = new Scanner(System.in);
    ) {

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if (line.trim().equals("")) {
          break;
        }

        saida.println(line);

      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

}
