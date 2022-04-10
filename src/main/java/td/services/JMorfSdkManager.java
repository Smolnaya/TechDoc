package td.services;

import ru.textanalysis.tawt.jmorfsdk.JMorfSdk;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdkFactory;

public class JMorfSdkManager {
    private static JMorfSdk jMorfSdk;

    public JMorfSdkManager() {
        this.jMorfSdk = JMorfSdkFactory.loadFullLibrary();
    }

    public static JMorfSdk getjMorfSdk() {
        return jMorfSdk;
    }
}
