package com.xya.ValueObject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by ubuntu on 14-12-20.
 */
public class AccountVO extends BmobObject {
    //用户基本信息
    private String username;
    private String email;
    private String password;
    private BmobFile accountBkg;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BmobFile getAccountBkg() {
        return accountBkg;
    }

    public void setAccountBkg(BmobFile accountBkg) {
        this.accountBkg = accountBkg;
    }
}
