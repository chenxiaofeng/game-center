package com.mas.ws.pojo;

import java.io.Serializable;
import java.util.Date;

public class ClientUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Integer clientId;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 机器序列号
     */
    private String imei;

    /**
     * 网卡(wifi)地址
     */
    private String mac;

    /**
     * sim卡号
     */
    private String imsi;

    /**
     * 短信中心号
     */
    private String smsc;

    /**
     * 手机机型
     */
    private String deviceModel;

    /**
     * 手机厂商
     */
    private String deviceVendor;

    /**
     * 1手机，2平板
     */
    private Integer deviceType;

    /**
     * 网络类型
     */
    private String netType;

    /**
     * 系统版本号
     */
    private String osVersion;

    /**
     * 系统版本名字
     */
    private String osVersionName;

    /**
     * 系统附加信息
     */
    private String osAddtional;

    /**
     * 运营商
     */
    private String serviceSupplier;

    /**
     * 平台分辨率
     */
    private String resolution;

    /**
     * 屏幕宽
     */
    private Integer screenWidth;

    /**
     * 屏幕高
     */
    private Integer screenHeight;

    /**
     * 屏幕密度
     */
    private Float screenDensity;

    /**
     * 有效值
     */
    private Boolean state;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 最后一次登陆ip
     */
    private String IP;

    /**
     * 最后一次登陆所在国家
     */
    private String country;

    /**
     * 最后一次登陆所在省份
     */
    private String province;

    /**
     * 最后一次登陆所在城市
     */
    private String city;

    /**
     * 启动次数
     */
    private Integer activeNum;

    /**
     * 初始版本
     */
    private String appPackageNameFirst;

    private String appVersionNameFirst;

    private Integer appVersionCodeFirst;

    /**
     * 最后一次启动版本
     */
    private String appPackageNameLast;

    private String appVersionNameLast;

    private Integer appVersionCodeLast;

    /**
     * @return 编号
     */
    public Integer getClientId() {
        return clientId;
    }

    /**
     * @param clientid 
	 *            编号
     */
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    /**
     * @return 手机号码
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone 
	 *            手机号码
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return 机器序列号
     */
    public String getImei() {
        return imei;
    }

    /**
     * @param imei 
	 *            机器序列号
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * @return 网卡(wifi)地址
     */
    public String getMac() {
        return mac;
    }

    /**
     * @param mac 
	 *            网卡(wifi)地址
     */
    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * @return sim卡号
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * @param imsi 
	 *            sim卡号
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * @return 短信中心号
     */
    public String getSmsc() {
        return smsc;
    }

    /**
     * @param smsc 
	 *            短信中心号
     */
    public void setSmsc(String smsc) {
        this.smsc = smsc;
    }

    /**
     * @return 手机机型
     */
    public String getDeviceModel() {
        return deviceModel;
    }

    /**
     * @param devicemodel 
	 *            手机机型
     */
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    /**
     * @return 手机厂商
     */
    public String getDeviceVendor() {
        return deviceVendor;
    }

    /**
     * @param devicevendor 
	 *            手机厂商
     */
    public void setDeviceVendor(String deviceVendor) {
        this.deviceVendor = deviceVendor;
    }

    /**
     * @return 1手机，2平板
     */
    public Integer getDeviceType() {
        return deviceType;
    }

    /**
     * @param devicetype 
	 *            1手机，2平板
     */
    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * @return 网络类型
     */
    public String getNetType() {
        return netType;
    }

    /**
     * @param nettype 
	 *            网络类型
     */
    public void setNetType(String netType) {
        this.netType = netType;
    }

    /**
     * @return 系统版本号
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * @param osversion 
	 *            系统版本号
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * @return 系统版本名字
     */
    public String getOsVersionName() {
        return osVersionName;
    }

    /**
     * @param osversionname 
	 *            系统版本名字
     */
    public void setOsVersionName(String osVersionName) {
        this.osVersionName = osVersionName;
    }

    /**
     * @return 系统附加信息
     */
    public String getOsAddtional() {
        return osAddtional;
    }

    /**
     * @param osaddtional 
	 *            系统附加信息
     */
    public void setOsAddtional(String osAddtional) {
        this.osAddtional = osAddtional;
    }

    /**
     * @return 运营商
     */
    public String getServiceSupplier() {
        return serviceSupplier;
    }

    /**
     * @param servicesupplier 
	 *            运营商
     */
    public void setServiceSupplier(String serviceSupplier) {
        this.serviceSupplier = serviceSupplier;
    }

    /**
     * @return 平台分辨率
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * @param resolution 
	 *            平台分辨率
     */
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    /**
     * @return 屏幕宽
     */
    public Integer getScreenWidth() {
        return screenWidth;
    }

    /**
     * @param screenwidth 
	 *            屏幕宽
     */
    public void setScreenWidth(Integer screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * @return 屏幕高
     */
    public Integer getScreenHeight() {
        return screenHeight;
    }

    /**
     * @param screenheight 
	 *            屏幕高
     */
    public void setScreenHeight(Integer screenHeight) {
        this.screenHeight = screenHeight;
    }

    /**
     * @return 屏幕密度
     */
    public Float getScreenDensity() {
        return screenDensity;
    }

    /**
     * @param screendensity 
	 *            屏幕密度
     */
    public void setScreenDensity(Float screenDensity) {
        this.screenDensity = screenDensity;
    }

    /**
     * @return 有效值
     */
    public Boolean getState() {
        return state;
    }

    /**
     * @param state 
	 *            有效值
     */
    public void setState(Boolean state) {
        this.state = state;
    }

    /**
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createtime 
	 *            创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updatetime 
	 *            修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 
	 *            备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return 最后一次登陆ip
     */
    public String getIP() {
        return IP;
    }

    /**
     * @param ip 
	 *            最后一次登陆ip
     */
    public void setIP(String IP) {
        this.IP = IP;
    }

    /**
     * @return 最后一次登陆所在国家
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country 
	 *            最后一次登陆所在国家
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return 最后一次登陆所在省份
     */
    public String getProvince() {
        return province;
    }

    /**
     * @param province 
	 *            最后一次登陆所在省份
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * @return 最后一次登陆所在城市
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city 
	 *            最后一次登陆所在城市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return 启动次数
     */
    public Integer getActiveNum() {
        return activeNum;
    }

    /**
     * @param activenum 
	 *            启动次数
     */
    public void setActiveNum(Integer activeNum) {
        this.activeNum = activeNum;
    }

    /**
     * @return 初始版本
     */
    public String getAppPackageNameFirst() {
        return appPackageNameFirst;
    }

    /**
     * @param apppackagenamefirst 
	 *            初始版本
     */
    public void setAppPackageNameFirst(String appPackageNameFirst) {
        this.appPackageNameFirst = appPackageNameFirst;
    }

    public String getAppVersionNameFirst() {
        return appVersionNameFirst;
    }

    public void setAppVersionNameFirst(String appVersionNameFirst) {
        this.appVersionNameFirst = appVersionNameFirst;
    }

    public Integer getAppVersionCodeFirst() {
        return appVersionCodeFirst;
    }

    public void setAppVersionCodeFirst(Integer appVersionCodeFirst) {
        this.appVersionCodeFirst = appVersionCodeFirst;
    }

    /**
     * @return 最后一次启动版本
     */
    public String getAppPackageNameLast() {
        return appPackageNameLast;
    }

    /**
     * @param apppackagenamelast 
	 *            最后一次启动版本
     */
    public void setAppPackageNameLast(String appPackageNameLast) {
        this.appPackageNameLast = appPackageNameLast;
    }

    public String getAppVersionNameLast() {
        return appVersionNameLast;
    }

    public void setAppVersionNameLast(String appVersionNameLast) {
        this.appVersionNameLast = appVersionNameLast;
    }

    public Integer getAppVersionCodeLast() {
        return appVersionCodeLast;
    }

    public void setAppVersionCodeLast(Integer appVersionCodeLast) {
        this.appVersionCodeLast = appVersionCodeLast;
    }
}