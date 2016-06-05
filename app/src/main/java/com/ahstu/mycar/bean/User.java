package com.ahstu.mycar.bean;

import cn.bmob.v3.BmobUser;

/**
 * @author redowu on 2016/4/25
 *         功能：用于Bmob注册、获取信息。
 */
public class User extends BmobUser {
    private String myInstallation;

    public String getMyInstallation() {
        return myInstallation;
    }

    public void setMyInstallation(String myInstallation) {
        this.myInstallation = myInstallation;
    }
}
