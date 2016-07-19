package com.ahstu.mycar.bean;

/**
 * @author 吴天洛
 *         功能：离线地图数据
 */
public class OfflineMapCityBean {
    private String cityName;
    private int cityCode;
    private int size;
    /**
     * 下载的进度
     */
    private int progress;

    private Flag flag = Flag.NO_STATUS;

    public OfflineMapCityBean() {
    }

    public OfflineMapCityBean(String cityName, int cityCode, int progress, int size) {
        this.cityName = cityName;
        this.cityCode = cityCode;
        this.progress = progress;
        this.size = size;
    }
    /**
     * 下载的状态：无状态，暂停，正在下载
     *
     * @author zhy
     */
    public enum Flag {
        NO_STATUS, PAUSE, DOWNLOADING

    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
