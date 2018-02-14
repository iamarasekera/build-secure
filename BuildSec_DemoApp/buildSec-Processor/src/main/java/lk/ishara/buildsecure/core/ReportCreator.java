package lk.ishara.buildsecure.core;

import java.util.ArrayList;
import java.util.List;

import lk.ishara.buildsecure.utils.Util;

/**
 * Created by Ishara on 12/30/2017.
 */

public class ReportCreator {
    Configuration config;
    private AndroidManifest androidManifest;

    public ReportCreator(AndroidManifest androidManifest, Configuration config, String message) {
		super();
		this.androidManifest = androidManifest;
		this.config = config;
		this.complianceStatus = new ComplianceStatus(androidManifest, config);
		this.complianceStatus.setNoLogEntries(message == null ||  message.isEmpty());
    }

    private ComplianceStatus complianceStatus;

    public AndroidManifest getAndroidManifest() {
        return this.androidManifest;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public List<String> getReport(boolean js, String message) {
        switch (config.getReportFormat()) {
            case eHtml: {
                return getJSONReport(js, message);
            }

            case eTxt: {

            }
            case eXml: {

            }
            case eJson: {
                return getJSONReport(js, message);
            }
        }
        return new ArrayList<String>();
    }

    private StringBuilder getSubProperty(String propertyName, String propValue, boolean js){
        StringBuilder sb = new StringBuilder("");
        if (js) {
            sb.append("var "+ propertyName +"=");
        } else {
            sb.append("\"" +propertyName + "\": ");
        }
        sb.append(propValue);
        if (js) {
            sb.append(";\n");
        } else {
            sb.append("\n");
        }
        return sb;
    }

    public List<String> getJSONReport(boolean js, String message) {
        List<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        sb.append(getSubProperty("config", config.toJSON(), js));
        sb.append(getSubProperty("jdkversion", "\"" + System.getProperty("java.version") +"\"", js));
        sb.append(getSubProperty("datetime", "\"" + Util.getFormattedCurrentDateTime() +"\"", js));
        sb.append(getSubProperty("user", "\"" + System.getProperty("user.name") +"\"", js));
        sb.append(getSubProperty("androidManifest", androidManifest.toJSON(), js));
        sb.append(getSubProperty("complianceStatus", complianceStatus.toJSON(), js));
        if (message==null || message.isEmpty()) {
            message = " ";
        }
        sb.append(getSubProperty("message", "'" + message + "'", js));

        ret.add(sb.toString());
        return ret;
    }
}