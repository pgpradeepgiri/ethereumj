package org.ethereum.solidity.compiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * Created by Anton Nashatyrev on 03.03.2016.
 */
public class Solc {

    public static final Solc INSTANCE = new Solc();

    private File solc = null;

    private Solc() {
        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));

        InputStream is = getClass().getResourceAsStream("/bin/" + getOS() + "/file.list");
        Scanner scanner = new Scanner(is);
        while (scanner.hasNext()) {
            String s = scanner.next();
            File targetFile = new File(tmpDir, s);
            if (!targetFile.canRead()) {
                InputStream fis = getClass().getResourceAsStream("/bin/" + getOS() + "/" + s);
                Files.copy(fis, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            if (solc == null) {
                // first file in the list denotes executable
                solc = targetFile;
                solc.setExecutable(true);
            }
            targetFile.deleteOnExit();
        }

    }

    private static String getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return "win";
        } else if (osName.contains("linux")) {
            return "linux";
        } else if (osName.contains("mac")) {
            return "mac";
        } else {
            throw new RuntimeException("Can't find solc compiler for unrecognized OS: " + osName);
        }
    }

    public File getExecutable() {
        return solc;
    }
}
