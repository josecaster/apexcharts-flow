package sr.we.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import sr.we.TranslationProvider;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class LanguageSelect extends Select<Locale> {

    public LanguageSelect() {
        setItems(TranslationProvider.LOCALE_NL, TranslationProvider.LOCALE_EN);

//        setItemLabelGenerator(f -> {
//            if(f.equals(TranslationProvider.LOCALE_NL)){
//                return getTranslation("sr.we.dutch");
//            }
//            if(f.equals(TranslationProvider.LOCALE_EN)){
//                return getTranslation("sr.we.english");
//            }
//          return "";
//        });

        setRenderer(new ComponentRenderer<>(f-> {
            if(f.equals(TranslationProvider.LOCALE_NL)){
                return new Span(getTranslation("sr.we.dutch"));
            }
            if(f.equals(TranslationProvider.LOCALE_EN)){
                return new Span(getTranslation("sr.we.english"));
            }
            return null;
        }));

        Cookie[] cookies = VaadinRequest.getCurrent().getCookies();
        if (cookies != null && cookies.length >= 1) {
            Optional<Cookie> any = Arrays.stream(cookies).filter(f -> f.getName().equalsIgnoreCase("my-lang")).findAny();
            if (any.isPresent()) {
                Cookie cookie = any.get();
                if (TranslationProvider.LOCALE_EN.toString().equalsIgnoreCase(cookie.getValue())) {
                    setValue(TranslationProvider.LOCALE_EN);
                } else if (TranslationProvider.LOCALE_NL.toString().equalsIgnoreCase(cookie.getValue())) {
                    setValue(TranslationProvider.LOCALE_NL);
                }
            }
        }

        if(getValue() == null){
            Locale locale = VaadinService.getCurrentRequest().getLocale();
            if (TranslationProvider.LOCALE_EN.getLanguage().equalsIgnoreCase(locale.getLanguage())) {
                setValue(TranslationProvider.LOCALE_EN);
            } else if (TranslationProvider.LOCALE_NL.getLanguage().equalsIgnoreCase(locale.getLanguage())) {
                setValue(TranslationProvider.LOCALE_NL);
            } else {
                setValue(TranslationProvider.LOCALE_EN);
            }
        }

        addValueChangeListener(f -> {
            Cookie myCookie = new Cookie("my-lang", f.getValue().getLanguage());
            myCookie.setMaxAge(60 * 60 * 24 * 7 * 52); // define after how many *seconds* the cookie should expire
            myCookie.setPath("/"); // single slash means the cookie is set for your whole application.
            VaadinService.getCurrentResponse().addCookie(myCookie);

            VaadinSession.getCurrent().setLocale(f.getValue());
            UI.getCurrent().getPage().reload();
        });


    }
}
