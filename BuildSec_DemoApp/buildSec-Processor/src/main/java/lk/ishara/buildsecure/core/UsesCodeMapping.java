package lk.ishara.buildsecure.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;


/**
 * Created by ishara on 12/25/2017.
 */

public class UsesCodeMapping {


    public static Map<String, List<String>> usesMapping = new HashMap<>();
    static boolean logUsed = false;
    static {
        List<String> log = new ArrayList<>();
        log.add("Log.e");
        log.add("Log.d");
        log.add("Log.w");
        log.add("Log.v");
        log.add("Log.i");
        usesMapping.put("_LOG", log);

        List<String> acl = new ArrayList<>();
        acl.add("getAllCellInfo");
        acl.add("getCellLocation");
        usesMapping.put("ACCESS_COARSE_LOCATION", acl);

        List<String> afl = new ArrayList<>();
        afl.add("getCellLocation");
        usesMapping.put("ACCESS_FINE_LOCATION", afl);


        List<String> cp = new ArrayList<>();
        cp.add("sendUssdRequest");
        usesMapping.put("CALL_PHONE", cp);

        List<String> camera = new ArrayList<>();
        camera.add("Camera.open");
        usesMapping.put("CAMERA", camera);

        List<String> rpn = new ArrayList<>();
        rpn.add("getLine1Number");
        usesMapping.put("READ_PHONE_NUMBERS", rpn);

        List<String> sndsms = new ArrayList<>();
        sndsms.add("sendDataMessage");
        sndsms.add("sendVisualVoicemailSms");
        usesMapping.put("SEND_SMS", sndsms);

        List<String> rsms = new ArrayList<>();
        rsms.add("SmsManager.getDefault");
        usesMapping.put("RECEIVE_SMS", rsms);

        List<String> rdsms = new ArrayList<>();
        rdsms.add("getLine1Number");
        usesMapping.put("READ_SMS", rdsms);

        List<String> rps = new ArrayList<>();
        rps.add("getImei");
        rps.add("getCarrierConfig");
        rps.add("getDataNetworkType");
        rps.add("getLine1Number");
        rps.add("getGroupIdLevel1");
        rps.add("getDeviceId");
        rps.add("getDeviceSoftwareVersion");
        rps.add("getForbiddenPlmns");
        rps.add("getSubscriberId");
        rps.add("getVisualVoicemailPackageName");
        rps.add("getVoiceMailAlphaTag");
        rps.add("getVoiceMailNumber");
        rps.add("getVoiceNetworkType");
        usesMapping.put("READ_PHONE_STATE", rps);
    }

    public static void checkLog (BlockTree blkTree){
        String codeBody = blkTree.toString();
        List<? extends StatementTree> stList = blkTree.getStatements();

        for (StatementTree st : stList){
            Tree.Kind knd = st.getKind();
            if (knd == Tree.Kind.EXPRESSION_STATEMENT) {
                if (st.toString().indexOf("Log") >= 0) {
                    System.out.println("" + st.getKind() + "\t" + st.toString());
                    logUsed = true;
                }
            }
        }
    }

    public static List<String> checkCode(BlockTree blkTree){
        checkLog(blkTree);
        String codeBody = blkTree.toString();
        List<String> ret = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : usesMapping.entrySet()) {
            String perm = entry.getKey();
            List<String> codeSet = entry.getValue();
            for (String str : codeSet) {
               boolean found = codeBody.indexOf(str)  > -1;
               if (found) {
                   ret.add(perm);
                   break;
               }
            }
        }
        return ret;
    }
}