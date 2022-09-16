package sr.we.util;

/*import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.html.AbstractHtmlExporter;
import org.docx4j.convert.out.html.AbstractHtmlExporter.HtmlSettings;
import org.docx4j.convert.out.html.HtmlExporterNG2;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;*/

public class OfficeDocDocxParser {

	/*private static final Logger logger = Logger.getLogger(OfficeDocDocxParser.class);
	private Locale locale;

	public OfficeDocDocxParser(Locale locale) {
		this.locale = locale;
	}

	public static void main(String[] args) throws Docx4JException, IOException, JAXBException {
		String filePath = "C:\\Users\\yusuf\\Downloads";
		String file = "werkgeversverklaring.doc";

		HashMap<String, String> map = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;

			{
				this.put("employee.name", "Company Name here...");
				this.put("EM_NAAM", "Test Name");
				this.put("EM_VOORNAAM", "Test name 2");
			}

			@Override
			public String get(Object key) {

				return super.get(key);
			}
		};

		Locale locale = new Locale("en", "US");

		OfficeDocDocxParser docx4j = new OfficeDocDocxParser(locale);
		WordprocessingMLPackage template = docx4j.getTemplate(filePath + file);

		MainDocumentPart documentPart = template.getMainDocumentPart();

		// System.out.println("----------------------------------------------------");

		// System.out.println(XmlUtils.marshaltoString(documentPart.getJaxbElement(),
		// true, true));

		org.docx4j.wml.Document wmlDocumentEl = documentPart.getJaxbElement();
		String xml = XmlUtils.marshaltoString(wmlDocumentEl, true);
		// System.out.println(xml);
		// StrSubstitutor strSubstitutor = new StrSubstitutor(map);
		// String replace = strSubstitutor.replace(xml);
		Object obj = XmlUtils.unmarshallFromTemplate(xml, map);
		template.getMainDocumentPart().setJaxbElement((org.docx4j.wml.Document) obj);

		// System.out.println("----------------------------------------------------");

		docx4j.writeDocxToStream(template, filePath + "Hello2.docx");

		template = docx4j.getTemplate(new File(filePath + "Hello2.docx"));
		HtmlSettings htmlSettings = new HtmlSettings();
		AbstractHtmlExporter exporter = new HtmlExporterNG2();
//		exporter.setHtmlSettings(htmlSettings);TODO
		OutputStream os = new FileOutputStream(filePath + "hello.html");
		// os = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(os);
		try {
			exporter.html(template, result, htmlSettings);
			// //System.out.println( ((ByteArrayOutputStream)os).toString() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	public String getStringFromFile(byte[] byteArray, String outputFilename)
			throws IOException, ParserConfigurationException, TransformerException {
		String extension = FilenameUtils.getExtension(outputFilename);
		if (extension != null) {
			if (extension.equalsIgnoreCase(FileExtension.DOC)) {
				HWPFDocumentCore wordDocument = AbstractWordUtils.loadDoc(new ByteArrayInputStream(byteArray));
				WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
						DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
				wordToHtmlConverter.processDocument(wordDocument);
				Document htmlDocument = wordToHtmlConverter.getDocument();
				DOMSource domSource = new DOMSource(htmlDocument);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer serializer = tf.newTransformer();
				serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				serializer.setOutputProperty(OutputKeys.INDENT, "yes");
				serializer.setOutputProperty(OutputKeys.METHOD, "html");
				StringWriter writer = new StringWriter();
				serializer.transform(domSource, new StreamResult(writer));
				String output = writer.toString();
				return output;
			} else if (extension.equalsIgnoreCase(FileExtension.DOCX)) {
				XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(byteArray));
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				XHTMLConverter.getInstance().convert(document, os, null);
				String out = new String(os.toByteArray(), "UTF-8");
				return out;
			}
		}
		return null;
	}

	public void getFileFromTemplate(byte[] byteArray, String outputFilename, Map<String, String> placeholders)
			throws IOException, Docx4JException, JAXBException {
		String extension = FilenameUtils.getExtension(outputFilename);
		if (extension.equalsIgnoreCase(FileExtension.DOC)) {
			POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(byteArray));
			HWPFDocument doc = new HWPFDocument(fs);
			doc = replaceText(doc, placeholders);
			saveWord(outputFilename, doc);
		} else if (extension.equalsIgnoreCase(FileExtension.DOCX)) {
			OfficeDocumentParser parser = new OfficeDocumentParser(locale,new ByteArrayInputStream(byteArray), placeholders,
					outputFilename);
			parser.createOutputFile();
		}
	}

	public void getFileFromTemplate(File file, String outputFilename, Map<String, String> placeholders)
			throws FileNotFoundException, IOException, Docx4JException, JAXBException {
		String extension = FilenameUtils.getExtension(file.getName());
		if (extension.equalsIgnoreCase(FileExtension.DOC)) {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
			HWPFDocument doc = new HWPFDocument(fs);
			doc = replaceText(doc, placeholders);
			saveWord(outputFilename, doc);
		} else if (extension.equalsIgnoreCase(FileExtension.DOCX)) {
			OfficeDocumentParser parser = new OfficeDocumentParser(locale,file.getPath(), placeholders, outputFilename);
			parser.createOutputFile();
		}
	}

	public void getFileFromTemplate(String filename, String outputFilename, Map<String, String> placeholders)
			throws FileNotFoundException, IOException, Docx4JException, JAXBException {
		getFileFromTemplate(new File(filename), outputFilename, placeholders);
	}

	private void createOutputFile(WordprocessingMLPackage template, String outputFilename,
			Map<String, String> placeholders) throws IOException, Docx4JException, JAXBException {
		// List<Object> texts =
		// getAllElementFromObject(template.getMainDocumentPart(), Text.class);
		// searchAndReplace(texts, placeholders);
		Map<String, String> capitalizedPlaceholders = new HashMap<String, String>();
		if (placeholders != null) {
			Iterator<String> iterator = placeholders.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = placeholders.get(key);
				if (value != null) {
					value = value.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&apos;")
							.replace("<", "&lt;").replace(">", "&gt;");
					placeholders.put(key, value);
					capitalizedPlaceholders.put(key.toUpperCase(), value);
				}
			}
		}
		placeholders.putAll(capitalizedPlaceholders);
		org.docx4j.wml.Document wmlDocumentEl = template.getMainDocumentPart().getJaxbElement();
		String xml = XmlUtils.marshaltoString(wmlDocumentEl, true);

		xml = getRegExprPlaceHolders(xml);

		// //System.out.println(xml);
		Object obj = XmlUtils.unmarshallFromTemplate(xml, (HashMap<String, String>) placeholders);
		template.getMainDocumentPart().setJaxbElement((org.docx4j.wml.Document) obj);

		writeDocxToStream(template, outputFilename);
	}

	private String getRegExprPlaceHolders(String value) {
		Pattern r = Pattern.compile("\\$(\\{.*?})");
		Matcher m = r.matcher(value);
		while (m.find()) {
			String group = m.group();
			String fix = fixTags(value, group);
			value = value.replace(group, fix);
		}
		return value;
	}

	private String fixTags(String value, String placeHolder) {
		Pattern r = Pattern.compile("(<.[^(><.)]+>)");
		Matcher m = r.matcher(placeHolder);
		String holder = placeHolder;
		while (m.find()) {
			String group = m.group();
			holder = holder.replace(group, "");
		}
		return holder;
	}

	private WordprocessingMLPackage getTemplate(byte[] byteArray) throws Docx4JException, FileNotFoundException {
		WordprocessingMLPackage template = WordprocessingMLPackage.load(new ByteArrayInputStream(byteArray));
		return template;
	}

	private WordprocessingMLPackage getTemplate(String filename) throws Docx4JException, FileNotFoundException {
		WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(new File(filename)));
		return template;
	}

	private WordprocessingMLPackage getTemplate(File file) throws Docx4JException, FileNotFoundException {
		WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(file));
		return template;
	}

	private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement)
			obj = ((JAXBElement<?>) obj).getValue();

		if (obj.getClass().equals(toSearch))
			result.add(obj);
		else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}
		}
		return result;
	}

	private void writeDocxToStream(WordprocessingMLPackage template, String target)
			throws IOException, Docx4JException {
		File f = new File(target);
		template.save(f);
	}

	private HWPFDocument replaceText(HWPFDocument doc, Map<String, String> values) {
		Range r1 = doc.getRange();

		for (int i = 0; i < r1.numSections(); ++i) {
			Section s = r1.getSection(i);
			for (int x = 0; x < s.numParagraphs(); x++) {
				Paragraph p = s.getParagraph(x);
				for (int z = 0; z < p.numCharacterRuns(); z++) {
					CharacterRun run = p.getCharacterRun(z);
					String text = run.text();
					Iterator<String> iterator = values.keySet().iterator();
					while (iterator.hasNext()) {
						String key = iterator.next();
//						System.out.println("KEY [" + key + "] VALUE [" + values.get(key) + "]");
						if (text.contains(key)) {
							run.replaceText("${" + key + "}", values.get(key));
						}
					}
				}
			}
		}
		return doc;
	}

	private void saveWord(String filePath, HWPFDocument doc) throws FileNotFoundException, IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filePath);
			doc.write(out);
		} finally {
			out.close();
		}
	}*/
}
