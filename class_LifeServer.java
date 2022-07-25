import java.net.*;
import java.io.*;

class LifeServer {

	private Socket socket;
	private String header = "HTTP/1.1 200 OK\r\n\r\n";
	private String body = "<!DOCTYPE html><html>Hello</html>";
	private int count = 0;

	public void start(int maxStep) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			ServerSocket server = new ServerSocket(8080);
			while (count < maxStep) {
				socket = server.accept();
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				/*
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				*/
				//System.out.println(reader.readLine());
				writer.write(header + body);
				writer.newLine();
				writer.flush();
				count++;
			}
		} catch (Exception e) {
			System.out.println("Socket error");
			System.out.println(e);
		} finally {
			try {
				reader.close();
				writer.close();
				socket.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	public void setResponse(String text) {
		body = "<!DOCTYPE html><html>" + text + "</html>";
	}

}