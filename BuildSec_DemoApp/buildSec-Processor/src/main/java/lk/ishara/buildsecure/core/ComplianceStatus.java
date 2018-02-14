package lk.ishara.buildsecure.core;

/**
 * Created by Ishara on 1/28/2018.
 */

public class ComplianceStatus {
    private Configuration config;
    private AndroidManifest androidManifest;
    private boolean noLogEntries = false;

    public boolean isDataBackup() {
        return !(config.isAllowDataBackup() ^ androidManifest.isAllowBackup());
    }

    public boolean isUsesPermission() {
        // manifest check config as true
        boolean invalid = false;
        System.out.println(config.isUsesPermission() );
        for (UsesPermission up : androidManifest.getUsesPermissions()) {
            System.out.println(up.getUpStatus());
          invalid = up.getUpStatus() == UsesPermission.eUpStatus.upS_added ||
                   up.getUpStatus() == UsesPermission.eUpStatus.upS_removed;
           if (invalid){
               break;
           }
        }
        System.out.println(invalid);
        return config.isUsesPermission() && !invalid;
    }

    public boolean isNoLogEntries() {
        return noLogEntries;
    }

    public void setNoLogEntries(boolean noLogEntries){
        this.noLogEntries = noLogEntries;
    }

    public ComplianceStatus(AndroidManifest androidManifest, Configuration config) {
        this.config = config;
        this.androidManifest = androidManifest;
    }

    public boolean isSecurityStatus() {
        return this.isDataBackup() && this.isNoLogEntries() && this.isUsesPermission();
    }

    @Override
    public String toString() {
        return "\"securityStatus=" + isSecurityStatus() +
                ", dataBackup=" + isDataBackup() +
                ", usesPermission=" + isUsesPermission() +
                ", noLogEntries=" + noLogEntries;
    }

    public String toJSON(){
        StringBuilder sb = new StringBuilder("{");
        sb.append(this.toString().replace("\\", "/").replace(" ", "").replace("=", "\":\"").replace(",", "\",\""));
        sb.append("\"}");
        return sb.toString();
    }
}
