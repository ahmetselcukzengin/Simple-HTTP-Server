import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		server.createContext("/main", new MyHandler());
		server.setExecutor(null);
		server.start();
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			String cookieValue = t.getRequestHeaders().getFirst("Cookie");
			int visitCount = 0;
			try {

				visitCount = Arrays.asList(cookieValue.split(";"))
						.stream()
						.filter(s -> s.trim().startsWith("visitCount"))
						.mapToInt(s -> Integer.valueOf(s.split("=")[1]))
						.max()
						.orElse(0);
				System.out.printf("Visit Count: %d\n", visitCount);
			}
			catch (Exception e) {
				visitCount = 0;
			}
			String response = "<html><body><p>Your visit count:</p><h1>" + visitCount + "<h1></body></html>";
			t.getResponseHeaders().add("Set-Cookie", "visitCount=" + Integer.toString(visitCount+1));
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

}
