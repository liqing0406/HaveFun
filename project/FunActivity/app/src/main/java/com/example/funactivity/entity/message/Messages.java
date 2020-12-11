package com.example.funactivity.entity.message;

import com.example.funactivity.entity.User.User;

import java.util.Date;

public class Messages {
    private Integer id;
    private User sendUser;
    private User receiveUser;
    private String message;
    private Boolean read;
    private Date time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getSendUser() {
        return sendUser;
    }

    public void setSendUser(User sendUser) {
        this.sendUser = sendUser;
    }

    public User getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(User receiveUser) {
        this.receiveUser = receiveUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        read = read;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "id=" + id +
                ", sendUser=" + sendUser +
                ", receiveUser=" + receiveUser +
                ", message='" + message + '\'' +
                ", read=" + read +
                ", time=" + time +
                '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
