import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.List;

public class ParserProgram {

	/**
	 * @author Rahul Nair
	 * @param args
	 *            : please pass the path of the file as first argument to the
	 *            program
	 */
	public static void main(String[] args) {
		String pathToFile;
		if (args == null || args.length == 0) {
			pathToFile = "sample.log";
			//pathToFile = "C:\\Users\\157462\\Downloads\\sample.log";
			String workingDir = System.getProperty("user.dir");
			pathToFile=workingDir+"\\"+pathToFile;
			System.out.println("path="+pathToFile);
		} else {
			pathToFile = args[0];
		}

		PocketPlayLogParser parser = new PocketPlayLogParser();
		LogAnalyzer anayzer = new LogAnalyzer();
		try {
			List<LogEntry> logs = anayzer.LoadLogsFromFile(pathToFile, parser);
			if (logs != null) {
				anayzer.analyzeLogs(logs);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
