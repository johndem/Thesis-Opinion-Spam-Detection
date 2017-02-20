import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Results {
	
	public Results() {
		
	}
	
	public void saveReviewInstances(List<String> reviews, boolean spam) throws IOException {
		
		int counter = 1;
		
		String prefix = "";
		if (spam) {
			prefix = "d_";
		}
		else {
			prefix = "t_";
		}
		
		for (String review : reviews) {
			List<String> lines = Arrays.asList(review);
			String filename = "output/" + prefix + String.valueOf(counter) + ".txt";
			Path file = Paths.get(filename);
			Files.write(file, lines, Charset.forName("UTF-8"));
			counter++;
		}
		
	}

}
