package javatsts;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.time.Instant;
import java.time.LocalDateTime;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * Hello world!
 *
 */
public class App {

	// private static HttpURLConnection con;

	public static void main(String[] args)
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException {
		String AppKey = "XXXXXX";

		String AppSecret = "XXXXXX";
		HashMap<String, String> headers = new HashMap<>();
		String url = "{Valid url here}";
		String payload = "{String java payload}";

		String year, month, day, hrs, mins, secs;

		LocalDateTime date = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

		year = DateTimeFormatter.ofPattern("yyyy").format(date);
		month = DateTimeFormatter.ofPattern("MM").format(date);
		day = DateTimeFormatter.ofPattern("dd").format(date);
		hrs = DateTimeFormatter.ofPattern("HH").format(date);
		mins = DateTimeFormatter.ofPattern("mm").format(date);
		secs = DateTimeFormatter.ofPattern("ss").format(date);

		String noncetime = year + month + day + hrs + mins + secs;

		String timespan = year + "-" + month + "-" + day + "T" + hrs + ":" + mins + ":" + secs + "Z";

		byte[] nonveArray = noncetime.getBytes(StandardCharsets.UTF_8);

		String nonce = Base64.getEncoder().encodeToString(nonveArray);
		System.out.println(nonce);

		String rawStr = nonce + timespan + AppSecret;

		System.out.println(rawStr);

		byte[] wordArray = rawStr.getBytes(StandardCharsets.UTF_8);

		final MessageDigest digest = MessageDigest.getInstance("SHA-256");

		final byte[] SHA256 = digest.digest(wordArray);

		String base64 = Base64.getEncoder().encodeToString(SHA256);
		System.out.println(base64);

		String Authorization = "UsernameToken Username=\"" + AppKey + "\", PasswordDigest=\"" + base64 + "\", Nonce=\""
				+ nonce + "\", Created=\"" + timespan + "\"";
		String X_SSE = "WSSE realm=\"DOP\", profile=\"UsernameToken\"";

		headers.put("Authorization", X_SSE);
		headers.put("X-WSSE", Authorization);
		headers.put("Content-Type", "application/json;charset=UTF-8");
		headers.put("X-RequestHeader", "request TransId=\"2009032412304510001\"");
		System.out.println(sendPost(url, payload, headers));

	}

	public static HashMap<String, String> sendPost(String url, String payload,
			HashMap<String, String> headers)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		// log.info(Utils.prelogString(Utils.getCodelineNumber(), "Sending Http Post
		// Request to >> " + url));

		HashMap<String, String> response = new HashMap<String, String>();
		CloseableHttpClient httpclient = HttpClientBuilder.create()
				.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.build();
		HttpPost httppost = new HttpPost(url);
		CloseableHttpResponse responseBody = null;
		int status = 0;

		try {
			StringEntity entity = new StringEntity(payload, StandardCharsets.UTF_8);

			for (String header : headers.keySet()) {
				httppost.addHeader(header, headers.get(header));
			}

			httppost.setEntity(entity);

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httppost);

			// Compare the 2 calls for body and response below
			String body = responseHandler.handleResponse(responseBody);
			// String body = EntityUtils.toString(responseBody.getEntity());
			status = responseBody.getStatusLine().getStatusCode();
			response.put("Status", "" + status);
			response.put("Body", body);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// log.error(Utils.prelogString("", Utils.getCodelineNumber(), (long) 0.0,
			// "Error Fetching", e.getMessage()));
			response.put("Status", "" + status);
			response.put("Body", e.getMessage());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// log.error(Utils.prelogString("", Utils.getCodelineNumber(), (long) 0.0,
			// "Error Fetching", e.getMessage()));
			response.put("Status", "" + status);
			response.put("Body", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			// (Utils.prelogString("", Utils.getCodelineNumber(), (long) 0.0, "Error
			// Fetching", e.getMessage()));
			response.put("Status", "" + status);
			response.put("Body", e.getMessage());
		} finally {
			// TODO: fix finally
		}

		return response;

	}

}
