package catserver.server.natives;

import catserver.server.CatServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.craftbukkit.CraftServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CatServerNative {
    public static void loadNative(CraftServer server) {
        if (CatServer.getConfig().enableNative) {
            String arch = System.getProperty("sun.arch.data.model");
            String fNativeName = "CatAsyncNative" + arch + ".dll";
            File nativeFile = new File(fNativeName);
            try {
                InputStream is = server.getClass().getClassLoader().getResourceAsStream(fNativeName);
                if (is == null) {
                    throw new ClassNotFoundException("CatAsyncNative ResNotFound");
                }
                FileUtils.copyInputStreamToFile(is, nativeFile);
            } catch (IOException | ClassNotFoundException e) {
                if (e instanceof ClassNotFoundException) {
                    e.printStackTrace();
                }
            }
            try {
                System.load(nativeFile.getCanonicalPath());
            } catch (IOException | UnsatisfiedLinkError e) {
                e.printStackTrace();
                System.out.println("CatAsyncNative DLL Load Failed. enableNative will be set false");
                CatServer.getConfig().enableNative = false;
            }
        }
    }
}
