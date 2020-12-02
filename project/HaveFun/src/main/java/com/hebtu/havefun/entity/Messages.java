package com.hebtu.havefun.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hebtu.havefun.entity.User.User;
import org.hibernate.annotations.Proxy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/2 9:17
 * @projectName HaveFun
 * @className Messages.java
 * @description TODO
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Entity
@Table(name = "messages")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "send_user")
    private User sendUser;

    @ManyToOne
    @JoinColumn(name = "receive_user")
    private User receiveUser;

    @Column(name = "message")
    private String message;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "time")
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
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
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
                ", isRead=" + isRead +
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
