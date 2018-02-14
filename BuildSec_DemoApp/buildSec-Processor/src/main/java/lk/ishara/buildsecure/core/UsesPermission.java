package lk.ishara.buildsecure.core;


/**
 * Created by Ishara on 12/25/2017.
 */

public class UsesPermission {
    public enum eUpStatus{
        upS_existed{
            @Override
            public String toString(){
                return "existed";
            }
        },
        upS_added{
            @Override
            public String toString(){
                return "added";
            }
        },
        upS_removed{
            @Override
            public String toString(){
                return "removed";
            }
        }
    }
    private String permissionName;
    private eUpStatus upStatus;

    @Override
    public boolean equals(Object obj) {
		return (this.permissionName.equals(((UsesPermission) obj).permissionName));
    }

    public void setPermissionName(String permission) {
        this.permissionName = permission;
    }

    public String getPermissionName(){
        return this.permissionName;
    }

    public eUpStatus getUpStatus() {
        return upStatus;
    }

    public void setUpStatus(eUpStatus upStatus) {
        this.upStatus = upStatus;
    }
}
