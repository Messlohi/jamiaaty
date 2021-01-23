package com.example.jamiaaty;

public class chatMessageModel {

    String time,idReceivevr,idSender,message;
    Boolean vu = false;

    public chatMessageModel(String time, String idReceivevr, String idSender, String message) {
        this.time = time;
        this.idReceivevr = idReceivevr;
        this.idSender = idSender;
        this.message = message;
    }

    public chatMessageModel() {
    }

    public Boolean getVu() {
        return vu;
    }

    public void setVu(Boolean vu) {
        this.vu = vu;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIdReceivevr() {
        return idReceivevr;
    }

    public void setIdReceivevr(String idReceivevr) {
        this.idReceivevr = idReceivevr;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
