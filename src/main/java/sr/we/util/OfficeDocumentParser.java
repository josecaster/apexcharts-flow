package sr.we.util;
/*
import fj.F;
import fj.F2;
import fj.data.Option;
import fj.data.Stream;
import org.apache.log4j.Logger;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fj.Function.curry;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.Stream.stream;*/

public class OfficeDocumentParser {

//	private static final Logger logger = Logger.getLogger(OfficeDocDocxParser.class);
	/*private Locale locale;
	private final Variables dateFormatsToRemove = new Variables();
	private Map<String, String> placeholders;
	private String outputFileName;
	private Docx docx;

	public OfficeDocumentParser(Locale locale){
		this.locale = locale;
		Locale.setDefault(locale);
	}

	public OfficeDocumentParser(Locale locale, InputStream stream, Map<String, String> placeholders, String outputFileName) {
		this(locale);
		docx = new Docx(stream);
		docx.setVariablePattern(new VariablePattern("${", "}"));
		this.outputFileName = outputFileName;
		this.placeholders = placeholders;
	}

	public OfficeDocumentParser(Locale locale, String docxPath, Map<String, String> placeholders, String outputFileName) {
		this(locale);
		docx = new Docx(docxPath);
		docx.setVariablePattern(new VariablePattern("${", "}"));
		this.outputFileName = outputFileName;
		this.placeholders = placeholders;
	}

	public void setPlaceholders(Map<String, String> placeholders) {
		this.placeholders = placeholders;
	}

	private void setVariables() {
		if (docx == null)
			return;
		Variables variables = new Variables();
		Map<String, String> refinedPlaceholders = refine(placeholders);
		refinedPlaceholders.forEach(new BiConsumer<String, String>() {
			@Override
			public void accept(String key, String value) {
				if (value == null)
					return;
				if (parseWithPatterns(value, stream("dd/MM/yyyy", "dd-MM-yyyy")).isNone()) {
					variables.addTextVariable(new TextVariable(key, value));
				} else {
					String formattedDate = getFormattedDate(docx.readTextContent(), key, value);
					variables.addTextVariable(new TextVariable(key, formattedDate));
				}
			}

			private String getFormattedDate(String fullText, String placeholderText, String value) {
				Locale.setDefault(locale);
				String placeholder = placeholderText.substring(2, placeholderText.length() - 1);

				String regex = "\\$\\{" + placeholder + "}%\\([\\w.,/\\s-]+\\)";
				Pattern pattern = Pattern.compile(regex);
				Matcher m = pattern.matcher(fullText);

				String formatComb = null;
				while (m.find()) {
					formatComb = fullText.substring(m.start(), m.end());
				}

				if (formatComb == null)
					return value;

				formatComb = formatComb.replace(placeholderText, "");

				// preparing format to be removed
				dateFormatsToRemove.addTextVariable(new TextVariable(formatComb, ""));

				formatComb = formatComb.substring(2, formatComb.length() - 1);

				SimpleDateFormat dnt = new SimpleDateFormat(formatComb);
				Date date = null;
				try {
					date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return dnt.format(date);
			}
		});

		docx.fillTemplate(variables);
	}

	private Map<String, String> refine(Map<String, String> placeholders) {
		String fullText = docx.readTextContent();

		Map<String, String> mappedKeys = new HashMap<>();

		placeholders.keySet().forEach(key -> {
			if (fullText.contains(key)) {
				mappedKeys.put(String.format("${%s}", key), placeholders.get(key));
			}
		});

		return mappedKeys;
	}

	private void performCleanup() {
		docx.setVariablePattern(new VariablePattern("%(", ")"));
		docx.fillTemplate(dateFormatsToRemove);
	}

	public void createOutputFile() {
		if (outputFileName == null)
			return;
		setVariables();
		performCleanup();
		docx.save(outputFileName);
	}

	private static F<String, F<String, fj.data.Option<Date>>> parseDate = curry(
			new F2<String, String, fj.data.Option<Date>>() {
				public fj.data.Option<Date> f(String pattern, String s) {
					try {
						return some(new SimpleDateFormat(pattern).parse(s));
					} catch (ParseException e) {
						return none();
					}
				}
			});

	private static Option<Option<Date>> parseWithPatterns(String s, Stream<String> patterns) {
		return (Option<Option<Date>>) stream(s).apply(patterns.map(parseDate)).find(Option.<Date>isSome_());
	}*/
}
