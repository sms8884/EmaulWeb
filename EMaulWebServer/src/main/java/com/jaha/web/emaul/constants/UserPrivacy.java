package com.jaha.web.emaul.constants;

public enum UserPrivacy {

    ALIAS("1", "닉네임"), NAME("2", "실명");

    private String code;
    private String desc;

    UserPrivacy(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserPrivacy value(String code) {
        switch (code){
            case "1" :
                return ALIAS;
            case "2" :
                return NAME;
            default :
                return ALIAS;
        }
    }
}
