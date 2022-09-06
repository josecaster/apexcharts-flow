package sr.we;

import com.vaadin.flow.component.PushConfiguration;
import com.vaadin.flow.component.ReconnectDialogConfiguration;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.communication.PushMode;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sr.we.CustomErrorHandler;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;

@Component
public class ServiceListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {

        event.getSource().addSessionInitListener(
                initEvent -> {

                    Cookie[] cookies = VaadinRequest.getCurrent().getCookies();
                    if (cookies != null && cookies.length >= 1) {
                        Optional<Cookie> any = Arrays.stream(cookies).filter(f -> f.getName().equalsIgnoreCase("my-lang")).findAny();
                        if (any.isPresent()) {
                            Cookie cookie = any.get();
                            if (TranslationProvider.LOCALE_EN.getLanguage().equalsIgnoreCase(cookie.getValue())) {
                                initEvent.getSession().setLocale(TranslationProvider.LOCALE_EN);
                            } else if (TranslationProvider.LOCALE_NL.getLanguage().equalsIgnoreCase(cookie.getValue())) {
                                initEvent.getSession().setLocale(TranslationProvider.LOCALE_NL);
                            }
                        }
                    }

                    LoggerFactory.getLogger(getClass())
                            .info("A new Session has been initialized!");
                    initEvent.getSession().setErrorHandler(new CustomErrorHandler());
                });


        event.getSource().addUIInitListener(uiInitEvent -> {
            LoggerFactory.getLogger(getClass())
                    .info("A new UI has been initialized!");
//            LoadingIndicatorConfiguration indicator = uiInitEvent.getUI().getLoadingIndicatorConfiguration();
//            indicator.setApplyDefaultTheme(false);
//            indicator.setSecondDelay(1000);
//
//            PushConfiguration push = uiInitEvent.getUI().getPushConfiguration();
//            push.setPushMode(PushMode.AUTOMATIC);
//
//            ReconnectDialogConfiguration dialog = uiInitEvent.getUI().getReconnectDialogConfiguration();
//            dialog.setDialogText("reconnecting...");
        });
    }
}
