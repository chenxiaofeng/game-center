package com.reportforms.model;

import java.sql.Timestamp;

import com.reportforms.util.DateUtil;

/**
 * 铃声,音乐下载
 * @author lisong.lan
 *
 */
public class MusicDownLoad extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1863276718348611178L;
	
	private Integer musicId;
	
	private String musicName;
	
	private Integer appType;
	
	private String appTypeString;
	
	private String theDate;
	
	private Integer categoryId;
	
	private String categoryName;
	
	private String countryCn;
	
	private String country;
	
	private Long downloadNum;
	
	private Timestamp createTime;
	
	private String createTimeString;
	
	public Integer getMusicId() {
		return musicId;
	}

	public void setMusicId(Integer musicId) {
		this.musicId = musicId;
	}

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}
	
	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public String getAppTypeString() {
		if(null != this.musicId){
			this.appTypeString = "Ringtones";
		}else {
			return "";
		}
		return appTypeString;
	}
	
	public String getTheDate() {
		return theDate;
	}

	public void setTheDate(String theDate) {
		this.theDate = theDate;
	}

	public String getCountryCn() {
		return countryCn;
	}

	public void setCountryCn(String countryCn) {
		this.countryCn = countryCn;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getDownloadNum() {
		return downloadNum;
	}

	public void setDownloadNum(Long downloadNum) {
		this.downloadNum = downloadNum;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
		this.createTimeString = DateUtil.getTimestampToString(createTime);
	}
	
	public String getCreateTimeString() {
		return createTimeString;
	}
	
	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

}
