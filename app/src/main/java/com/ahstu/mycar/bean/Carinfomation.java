package com.ahstu.mycar.bean;

/**
 * Created by xuning on 2016/5/7.
 */

import cn.bmob.v3.BmobObject;

public class Carinfomation extends BmobObject {
    private String car_brand;
    private String car_sign;
    private String car_number;
    private String car_model;
    private String car_enginerno;
    private String car_level;
    private Integer car_mile;
    private Integer car_gas;
    private String car_enginerstate;
    private String car_shiftstate;
    private String car_light;
    private Boolean car_start;
    private Boolean car_door;
    private Boolean car_air;
    private Boolean car__lock;
    private User user;

    public String getCar_brand() {
        return car_brand;
    }

    public String getCar_number() {
        return car_number;
    }

    public String getCar_model() {
        return car_model;
    }

    public String getCar_enginerno() {
        return car_enginerno;
    }

    public String getCar_light() {
        return car_light;
    }

    public String getCar_shiftstate() {
        return car_shiftstate;
    }

    public String getCar_enginerstate() {
        return car_enginerstate;
    }


    public String getCar_level() {
        return car_level;
    }

    public void setCar_brand(String car_brand) {
        this.car_brand = car_brand;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public void setCar_light(String car_light) {
        this.car_light = car_light;
    }

    public void setCar_shiftstate(String car_shiftstate) {
        this.car_shiftstate = car_shiftstate;
    }

    public void setCar_enginerstate(String car_enginerstate) {
        this.car_enginerstate = car_enginerstate;
    }


    public void setCar_level(String car_level) {
        this.car_level = car_level;
    }

    public void setCar_enginerno(String car_enginerno) {
        this.car_enginerno = car_enginerno;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getCar_mile() {
        return car_mile;
    }

    public void setCar_mile(Integer car_mile) {
        this.car_mile = car_mile;
    }

    public Integer getCar_gas() {
        return car_gas;
    }

    public void setCar_gas(Integer car_gas) {
        this.car_gas = car_gas;
    }

    public Boolean getCar_start() {
        return car_start;
    }

    public void setCar_start(Boolean car_start) {
        this.car_start = car_start;
    }

    public Boolean getCar_door() {
        return car_door;
    }

    public void setCar_door(Boolean car_door) {
        this.car_door = car_door;
    }

    public Boolean getCar_air() {
        return car_air;
    }

    public void setCar_air(Boolean car_air) {
        this.car_air = car_air;
    }

    public Boolean getCar__lock() {
        return car__lock;
    }

    public void setCar__lock(Boolean car__lock) {
        this.car__lock = car__lock;
    }

    public String getCar_sign() {
        return car_sign;
    }

    public void setCar_sign(String car_sign) {
        this.car_sign = car_sign;
    }
}
