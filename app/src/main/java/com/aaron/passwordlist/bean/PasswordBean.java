package com.aaron.passwordlist.bean;

import java.io.Serializable;

/**
 * Created by stars on 2015/7/27.
 */
public class PasswordBean implements Serializable {
    public int pwdId;
    public String pwdkeyword;
    public String pwdAccount;
    public String pwdTip;
    public int userKey;// 外键
}
