package lk.ishara.buildsecure.core;

import java.util.Properties;
import java.io.File;

import lk.ishara.buildsecure.utils.Util;

/**
 * Created by Ishara on 12/31/2017.
 */

public class Configuration {
    private boolean userConfig = false;

    public boolean isEnableBuildsec() {
        return enableBuildsec;
    }

    public boolean isManifestCheck() {
        return manifestCheck;
    }

    public boolean isUsesPermission() {
        return usesPermission;
    }

    private boolean enableBuildsec = true;
    private boolean manifestCheck = true;
    private boolean usesPermission = true;
    private boolean logEntry = true;
    private boolean allowDataBackup = false;
    public boolean isAllowDataBackup() {
        return allowDataBackup;
    }

    private String reportPath = "";
    private eReportFormat reportFormat;
    public enum eReportFormat {
        eTxt {
            @Override
            public String toString(){
                return "txt";
            }
        },
        eXml {
            @Override
            public String toString() {
                return "xml";
            }
        },
        eHtml{
            @Override
            public String toString(){
                return "html";
            }
        },
        eJson{
            @Override
            public String toString(){
                return "json";
            }
        };
        public static eReportFormat getReportFormat(String strRF){
            eReportFormat rf = null;
            for (eReportFormat rfItem : eReportFormat.values()){
                if (rfItem.toString().equalsIgnoreCase(strRF)) {
                    rf = rfItem;
                    break;
                }
            }
            return rf;
        }
    };
    private static Configuration thisInstance = null;

    public eReportFormat getReportFormat(){
        return this.reportFormat;
    }

    public static Configuration getConfiguration(){
        return getConfiguration(null);
    }
    public static Configuration getConfiguration(Properties prop){
        if (thisInstance == null) {
            if (prop != null) {
                thisInstance = new Configuration(prop);
            }
        }
        return thisInstance;
    }

     public String getBuildSecReportPath() {
         return getReportPath();
     }
    private Configuration(Properties prop){
        super();
        String enableBuildsec = prop.getProperty("enable_buildsec");
        if ("false".equalsIgnoreCase(enableBuildsec)) {
            this.enableBuildsec = false;
        }

        String manifestCheck = prop.getProperty("manifest_check");
        if ("false".equalsIgnoreCase(manifestCheck)) {
            this.manifestCheck = false;
        }

        String usesPermission = prop.getProperty("uses_permission");
        if ("false".equalsIgnoreCase(usesPermission)) {
            this.usesPermission = false;
        }

        String logEntry = prop.getProperty("log_entry");

        if ("false".equalsIgnoreCase(logEntry)) {
            this.logEntry = false;
        }

        String allowDataBackup = prop.getProperty("allow_data_backup");
        if ("true".equalsIgnoreCase(allowDataBackup)){
            this.allowDataBackup = true;
        }

        String reportFormat = prop.getProperty("report_format");
        this.reportFormat = eReportFormat.getReportFormat(reportFormat);
        String reportPath = prop.getProperty("report_path");
        if (reportPath != null && new File(reportPath).exists()) {
            this.reportPath = reportPath;
        } else {
            this.reportPath = Util.getBuildSecDefaultWorkDir();
        }
        System.out.println(this.toString());
    }
    public String getReportPath(){
        return this.reportPath;
    }

    @Override
    public String toString(){
        return  "enableBuildsec=" + enableBuildsec + ", manifestCheck= "
                + manifestCheck + ", usesPermission=" + usesPermission + ",logEntry=" + logEntry
                + ", allowDataBackup=" + allowDataBackup + ", reportFormat=" + reportFormat + ", reportPath=" + reportPath;
    }

    public String toJSON(){
        StringBuilder sb = new StringBuilder("{\"userConfig\":\"" + Boolean.toString(userConfig) + "\",\"");
        sb.append(this.toString().replace("\\", "/").replace(" ", "").replace("=", "\":\"").replace(",", "\",\""));
        sb.append("\"}");
        return sb.toString();
    }
}