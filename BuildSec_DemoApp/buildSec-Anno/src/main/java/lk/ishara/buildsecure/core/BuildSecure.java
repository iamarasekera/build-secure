package lk.ishara.buildsecure.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD) // can use in method only.
public @interface BuildSecure {
    // should ignore this test?
    public boolean enabled() default true;
    public enum Severity {
        LOW, MEDIUM, HIGH
    }

    public enum LogLevel {
        strict, ignore, error, warning, info, debug, verbose;
    }

    public enum Permission{
        ACCESS_COARSE_LOCATION,
        ACCESS_FINE_LOCATION,
        ADD_VOICEMAIL,
        ANSWER_PHONE_CALLS,
        BODY_SENSORS,
        CALL_PHONE,
        CAMERA,
        GET_ACCOUNTS,
        PROCESS_OUTGOING_CALLS,
        READ_CALENDAR,
        READ_CALL_LOG,
        READ_CONTACTS,
        READ_EXTERNAL_STORAGE,
        READ_PHONE_NUMBERS,
        READ_PHONE_STATE,
        READ_SMS,
        RECEIVE_MMS,
        RECEIVE_SMS,
        RECEIVE_WAP_PUSH,
        RECORD_AUDIO,
        SEND_SMS,
        USE_SIP,
        WRITE_CALENDAR,
        WRITE_CALL_LOG,
        WRITE_CONTACTS,
        WRITE_EXTERNAL_STORAGE
    }

    Severity severity() default Severity.HIGH;
    Permission[] permission() default Permission.READ_CONTACTS;

    String [] accessLevel() default "";
    boolean enableDataBackup() default false;

    LogLevel logLevel() default  LogLevel.strict;
    String verifiedBy() default "Ishara";

    String lastModified() default "02/12/2017";
}
