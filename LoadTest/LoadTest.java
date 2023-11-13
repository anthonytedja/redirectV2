import java.net.http.*;
import java.io.FileWriter;
import java.net.*;
import java.util.Date;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
// https://openjdk.org/groups/net/httpclient/intro.html
// https://openjdk.org/groups/net/httpclient/recipes.html
// https://www.appsdeveloperblog.com/execute-an-http-put-request-in-java/

public class LoadTest {
	static char[] ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
	static Random rand;

	public static String randomString(int length) {
		StringBuilder r = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int index = rand.nextInt(ALPHANUMERIC.length);
			r.append(ALPHANUMERIC[index]);
		}
		return r.toString();
	}

	public static void main(String[] args) {
		if (args.length != 6) {
			System.out.println("usage: java LoadTest HOST PORT SEED [PUT|GET] NUM_REQUESTS OUTPUT_FILE");
			System.exit(1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		int seed = Integer.parseInt(args[2]);
		String requestType = args[3];
		int numRequests = Integer.parseInt(args[4]);
		String outputFileName = args[5];

		rand = new Random(seed);

		List<Double> results = new ArrayList();

		try {
			FileWriter writer = new FileWriter(outputFileName);

			for (int i = 0; i < numRequests; i++) {
				String longURL = "http://" + randomString(100);
				String shortURL = randomString(20);
				if (requestType.equals("PUT")) {
					double executionTime = timedPut(
							"http://" + host + ":" + port + "/?short=" + shortURL + "&long=" + longURL);
					results.add(executionTime);
				}
				if (requestType.equals("GET")) {
					double executionTime = timedGet("http://" + host + ":" + port + "/" + shortURL);
					results.add(executionTime);
				}
			}

			StringBuilder outputToFile = new StringBuilder();
			for (Double result : results) {
				outputToFile.append(result.toString());
				outputToFile.append("\n");
			}

			writer.write(outputToFile.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static double timedPut(String uri) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))
				.PUT(HttpRequest.BodyPublishers.noBody())
				.build();

		Date start = new Date();
		client.send(request, HttpResponse.BodyHandlers.ofString());
		Date end = new Date();
		//return Math.random() * 100;
		return end.getTime() - start.getTime();
	}

	public static double timedGet(String uri) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))
				.GET()
				.build();

		Date start = new Date();
		client.send(request, HttpResponse.BodyHandlers.ofString());
		Date end = new Date();
		//return Math.random() * 100;
		return end.getTime() - start.getTime();
	}
}
