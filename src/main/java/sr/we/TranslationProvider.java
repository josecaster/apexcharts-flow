package sr.we;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vaadin.flow.i18n.I18NProvider;

@Component
public class TranslationProvider implements I18NProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6977664443080163524L;

	public static final String BUNDLE_PREFIX = "messages";

	public final Locale LOCALE_NL = new Locale("nl");
	public final Locale LOCALE_EN = new Locale("en");

	private List<Locale> locales = Collections.unmodifiableList(Arrays.asList(LOCALE_NL, LOCALE_EN));

	public static List<String> customs;
	static {
		customs = new ArrayList<>();
	}

	@Override
	public List<Locale> getProvidedLocales() {
		return locales;
	}

	@Override
	public String getTranslation(String key, Locale locale, Object... params) {
		if (key == null) {
			LoggerFactory.getLogger(TranslationProvider.class.getName())
					.warn("Got lang request for key with null value!");
			return "";
		}

		final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);

		String value;
		try {
			value = bundle.getString(key);
		} catch (final MissingResourceException e) {
			if(!customs.isEmpty()) {
				for(String prefix : customs) {
					final ResourceBundle custom = ResourceBundle.getBundle(prefix, locale);
					try {
						value = custom.getString(key);
						return value;
					} catch (final MissingResourceException f) {
						continue;
					}
				}
			}
			LoggerFactory.getLogger(TranslationProvider.class.getName()).warn("Missing resource", e);
			return "!" + locale.getLanguage() + ": " + key;
		}
		if (params.length > 0) {
			value = MessageFormat.format(value, params);
		}
		return value;
	}
	
	public void addCustom(String prefix) {
		customs.add(prefix);
	}
}