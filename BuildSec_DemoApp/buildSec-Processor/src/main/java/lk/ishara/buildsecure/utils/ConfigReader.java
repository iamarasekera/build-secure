package lk.ishara.buildsecure.utils;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.Date;
import java.util.Properties;

import lk.ishara.buildsecure.core.Configuration;
import lk.ishara.buildsecure.core.Constants;

/**
 * @author Ishara
 */

public class ConfigReader {
    Util util = new Util();
    InputStream inputStream;
    private Configuration configuration;

    public Configuration getPropValues() throws IOException {
        try {
            Properties prop = new Properties();

            // load from user home
            String fiePath = Util.getBuildSecDefaultWorkDir();

            try {
                File directory = new File(fiePath);
                if (!directory.exists()) {
                    directory.mkdir();
                }

                inputStream = new FileInputStream(fiePath + Constants.PROP_FILE_NAME);
            } catch (FileNotFoundException fNe) {
                inputStream = util.getInputStreamByName(Constants.PROP_FILE_NAME);
                Util.writeToFile(fiePath + Constants.PROP_FILE_NAME, inputStream);
            }

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(
                        "property file '" + Constants.PROP_FILE_NAME + "' not found in the classpath");
            }


            if (prop != null) {
                configuration = Configuration.getConfiguration(prop);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return configuration;
    }
}