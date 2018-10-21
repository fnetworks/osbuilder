import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TestMain {

	public static void main(String[] args) throws IOException {
		Properties props = new Properties();
		props.setProperty("https://regexr.com/", "182380");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		props.store(out, null);
		System.out.println(out.toString());
	}

}
