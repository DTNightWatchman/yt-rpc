package com.yt.example.common.model;

import java.io.Serializable;

/**
 * @author by yt
 * @Classname User
 * @Description TODO
 * @Date 2024/3/12 16:30
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
