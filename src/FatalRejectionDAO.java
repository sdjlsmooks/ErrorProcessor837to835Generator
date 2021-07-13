import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;

public class FatalRejectionDAO {

	private String fatalRejectionFilename; // Output of the FindRejectionsUtility
	
	public FatalRejectionDAO(String filename) {
		fatalRejectionFilename = filename;
	}
	
	public HashMap<String,FatalRejection> getFatalRejections() throws Exception {
		
		HashMap<String,FatalRejection> retVal = new HashMap<>();
		
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(fatalRejectionFilename))){
			retVal = new HashMap<String,FatalRejection>();
			
			String line = null;
			while ((line = lnr.readLine()) != null) {
				String[] fields = line.split("\t");
				if (fields.length == 2) {
					FatalRejection fr = new FatalRejection(fields[0], fields[1]);
					retVal.put(fields[0],fr);
				}
				else {
					throw new Exception("Incorrect number of fields in FatalRejection");
				}
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		
		return retVal;
	}

}
