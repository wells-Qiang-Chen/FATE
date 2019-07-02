package com.webank.ai.fate.board.global;


public enum ErrorCode {

    //COMMON CODE
    SUCCESS(0, "OK"),
    AUTH_ERROR(10000, "AUTH_ERROR"),
    PARAM_ERROR(10001, "PARAM_ERROR"),
    TIME_OUT(10012, "TIME_OUT"),
    SYSTEM_ERROR(100003, "SYSTEM_ERROR");


    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;

    private ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}