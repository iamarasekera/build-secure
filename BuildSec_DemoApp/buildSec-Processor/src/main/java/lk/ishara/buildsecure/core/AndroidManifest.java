package lk.ishara.buildsecure.core;

import java.util.ArrayList;
import java.util.List;

import lk.ishara.buildsecure.core.UsesPermission.eUpStatus;

/**
 * Created by Ishara on 12/25/2017.
 */

public class AndroidManifest {
    private String packageName;
    private String projectName;
    boolean allowBackup;
    public List<UsesPermission> usesPermissions = new ArrayList<UsesPermission>();

    /**
     * Updates the uses permission list of the manifest according to the 
     * permissions actually used by android source code.
     * @param upList - List of uses permission names used by android code
     */
    public void updateUsesPermission( List<String> upList) {
    	//if uses permission in manifest not used by code, mark as removed
		for (UsesPermission up : usesPermissions) {
			if (!upList.contains(up.getPermissionName())){
				up.setUpStatus(UsesPermission.eUpStatus.upS_removed);
			}
		}
		
		/**
		 * if manifest does not contain uses-permissions which are used by code,
		 * add it to the list of uses-permissions of android and mark as added
		 */
		for (String permName : upList) {
			UsesPermission up = new UsesPermission();
			up.setPermissionName(permName);
			
			if (!usesPermissions.contains(up)){
				up.setUpStatus(UsesPermission.eUpStatus.upS_added);
				usesPermissions.add(up);
			}
		}
	}
    
    public String getProjectName() {
        return this.projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isAllowBackup() {
        return this.allowBackup;
    }
    public void setAllowBackup(boolean allowBackup) {
        this.allowBackup = allowBackup;
    }

    public List<UsesPermission> getUsesPermissions() {
        return this.usesPermissions;
    }

    public String toJSON() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"packageName\"");
        sb.append(":");
        sb.append("\"" + this.packageName + "\"");
        sb.append(",");
        sb.append("\"projectName\"");
        sb.append(":");
        sb.append("\"" + this.projectName + "\"");
        sb.append(",");
        sb.append("\"allowBackup\"");
        sb.append(":");
        sb.append("\"" + this.allowBackup + "\"");
        boolean usesPerm = true;
        for (int i = 0; i < this.usesPermissions.size(); i++) {
            if (i == 0) {
                sb.append(",\"usesPermissions\":[");
            }
            UsesPermission up = this.usesPermissions.get(i);
            String upFullName = up.getPermissionName();
            eUpStatus status = up.getUpStatus();
            upFullName = upFullName.substring(upFullName.lastIndexOf(".")+1, upFullName.length());
            sb.append("\"" + upFullName + "[" + status.toString() + "]" + "\"");
            if (i < this.usesPermissions.size() - 1) {
                sb.append(",");
            } else {
                sb.append("]");
            }
            if (!status.equals(eUpStatus.upS_existed)) {
                usesPerm = false;
            }
            }
        if (usesPerm){
            sb.append(",\"usesPerm\":\"Pass\"");
        } else {
            sb.append(",\"usesPerm\":\"Fail\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
