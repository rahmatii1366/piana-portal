package ir.piana.boot.utils.errorprocessor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@ConfigurationProperties(prefix = "piana.tools.log")
class LogPropertiesAndFunctionalityProvider implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private String tracePrefix;

    public static String getTracePrefix() {
        return applicationContext.getBean(LogPropertiesAndFunctionalityProvider.class).tracePrefix;
    }

    public void setTracePrefix(String tracePrefix) {
        this.tracePrefix = tracePrefix;
    }

    private static boolean hasSpecifiedPackagePrefixes(StackTraceElement stackTraceElement) {
        String tp = applicationContext.getBean(LogPropertiesAndFunctionalityProvider.class).tracePrefix;

        return tp == null ||
                tp.isEmpty() ||
                stackTraceElement.getClassName().startsWith(tp) &&
                        !stackTraceElement.getClassName().equals(
                                ApiExceptionService.class.getName());
    }

    static StackTraceElement[] getStackTraceArray(StackTraceElement[] stackTraceElements) {
        return Arrays.stream(stackTraceElements)
                .filter(LogPropertiesAndFunctionalityProvider::hasSpecifiedPackagePrefixes)
                .toArray(size -> new StackTraceElement[size]);
    }

    public static <T> T getEnvironmentProperty(
            String key, Class<T> targetClass, T defaultValue) {
        if (key == null || targetClass == null) {
            throw new NullPointerException();
        }

        T value = null;
        if (applicationContext != null) {
            System.out.println(applicationContext.getEnvironment().getProperty(key));
            value = applicationContext.getEnvironment().getProperty(key, targetClass, defaultValue);
        }
        return value;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
