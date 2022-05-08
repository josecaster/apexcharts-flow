package sr.we;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ContextProvider implements ApplicationContextAware {
	private static ApplicationContext ctx = null;

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		ContextProvider.ctx = ctx;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<?> cl) {
		return (T) ctx.getBean(cl);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) ctx.getBean(name);
	}

}
