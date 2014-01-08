import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class IDCounter {

	private File IDcounterFile;
	private Document IDcounterDoc;
	private Transformer transformer;

	public IDCounter() {

		try {

			String filePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			IDcounterFile = new File(filePath + "idcounter.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			IDcounterDoc = dBuilder.parse(IDcounterFile);
			IDcounterDoc.getDocumentElement().normalize();

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized int getCounterAndIncreaseByOne() {
		int counter = 0;
		counter = Integer.parseInt(IDcounterDoc.getFirstChild().getTextContent());
		counter++;

		IDcounterDoc.getFirstChild().setTextContent(String.valueOf(counter));

		DOMSource source = new DOMSource(IDcounterDoc);
		StreamResult result = new StreamResult(IDcounterFile);

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return counter;
	}
}