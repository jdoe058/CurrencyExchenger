package util;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Properties;

@UtilityClass
public class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
//        try (InputStream inputStream = PropertiesUtil.class.getClassLoader()
//                .getResourceAsStream("application.properties")) {
//            PROPERTIES.load(inputStream);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        // Получение переменных окружения и добавление тех, что начинаются с "db."
        Map<String, String> env = System.getenv();
        env.forEach((key, value) -> {
            if (key.startsWith("db.")) {
                PROPERTIES.setProperty(key, value);
            }
        });
    }
}
