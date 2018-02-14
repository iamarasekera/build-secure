package lk.ishara.buildsecure.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lk.ishara.buildsecure.core.AndroidManifest;
import lk.ishara.buildsecure.core.ComplianceStatus;
import lk.ishara.buildsecure.core.Configuration;
import lk.ishara.buildsecure.core.Configuration.eReportFormat;
import lk.ishara.buildsecure.core.Constants;
import lk.ishara.buildsecure.core.ReportCreator;

/**
 * Created by Ishara on 12/30/2017.
 */

public class Util {
    public InputStream getInputStreamByName(String file) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);
        return inputStream;
    }

    public static void writeTextToFile(String filePath, List<String> text) {
        try {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            for (String line : text) {
                writer.println(line);
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error in creating configuration file.");
        }
    }

    public static void writeToFile(String filePath, InputStream inputStream) {
        try {
            FileOutputStream fOutputStream = new FileOutputStream(filePath);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                fOutputStream.write(buf, 0, len);
            }
            fOutputStream.close();
        } catch (Exception e) {
            System.out.println("Error in creating configuration file.");
        }
    }

    public static String getBuildSecDefaultWorkDir() {
        // Get the system property environment from jvm for the userhome
        // directory
        String userHomeDir = System.getProperty("user.home");
        // Configuration file placed in .buildSec sub directory
        String fiePath = userHomeDir + "\\" + Constants.APP_DIRECTORY + "\\";

        try {
            File directory = new File(fiePath);
            if (!directory.exists()) {
                directory.mkdir();
            }
        } catch (Exception fNe) {
        }
        return fiePath;
    }

    public static String getProjectName(){
        String prjName ="";
        File dir = new File(".");
        File [] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".iml");
            }
        });

        if (files.length > 0) {
            prjName = files[0].getName();
            prjName = prjName.substring(0, prjName.length() - 4);
        }
        return prjName;
    }

    public void publishReport(AndroidManifest androidManifest, Configuration config, String message) {
        ReportCreator rc = new ReportCreator(androidManifest, config, message);
        eReportFormat rf = config.getReportFormat();
        List<String> reportText = rc.getReport(rf.equals(eReportFormat.eHtml), message);

        switch (rf) {
            case eHtml: {
                String rptJS = config.getBuildSecReportPath() + "/" + Constants.RPRT_JS_NAME;
                if (reportText != null) {
                    writeTextToFile(rptJS, reportText);
                }
                String reportHTML = config.getBuildSecReportPath() + "/" + Constants.RPRT_HTML_NAME;
                InputStream isHTML = getInputStreamByName(Constants.RPRT_HTML_NAME);
                if (isHTML != null) {
                    writeToFile(reportHTML, isHTML);
                    try {
                        Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start chrome file://" + reportHTML });
                    } catch (IOException ioe) {
                    }
                }
                break;
            }
            case eJson : {
                String rptJSN = config.getBuildSecReportPath() + "/" + Constants.RPRT_JSN_NAME;
                if (reportText != null) {
                    writeTextToFile(rptJSN, reportText);
                    try {
                        Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start chrome file://" + rptJSN });
                    } catch (IOException ioe) {

                    }
                }
            }
            default:
                break;
        }
    }

    public static String getFormattedCurrentDateTime() {
        String strDate = "";
        try {
            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            strDate = simpleDateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }
}