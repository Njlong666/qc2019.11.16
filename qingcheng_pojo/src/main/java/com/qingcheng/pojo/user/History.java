package com.qingcheng.pojo.user;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hujialei
 */

@Table(name = "tb_history")
public class History implements Serializable {


    @Id
    private String username;

    @Id
    private String id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public History() {
    }

    public History(String username, String id) {
        this.username = username;
        this.id = id;
    }
}
