package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;

public class Fakedateinfo  implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
     * This field was generated by MyBatis Generator. This field corresponds to the database column FakeDateInfo.id
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    private Integer id;
	/**
     * This field was generated by MyBatis Generator. This field corresponds to the database column FakeDateInfo.text
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    private String text;
	/**
     * This field was generated by MyBatis Generator. This field corresponds to the database column FakeDateInfo.reportType
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    private String reporttype;
	/**
     * This field was generated by MyBatis Generator. This field corresponds to the database column FakeDateInfo.value
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    private String value;

	/**
     * This method was generated by MyBatis Generator. This method returns the value of the database column FakeDateInfo.id
     * @return  the value of FakeDateInfo.id
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    public Integer getId() {
	    return id;
    }

	/**
     * This method was generated by MyBatis Generator. This method sets the value of the database column FakeDateInfo.id
     * @param id  the value for FakeDateInfo.id
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    public void setId(Integer id) {
	    this.id = id;
    }

	/**
     * This method was generated by MyBatis Generator. This method returns the value of the database column FakeDateInfo.text
     * @return  the value of FakeDateInfo.text
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    public String getText() {
	    return text;
    }

	/**
     * This method was generated by MyBatis Generator. This method sets the value of the database column FakeDateInfo.text
     * @param text  the value for FakeDateInfo.text
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    public void setText(String text) {
	    this.text = text;
    }

	/**
     * This method was generated by MyBatis Generator. This method returns the value of the database column FakeDateInfo.reportType
     * @return  the value of FakeDateInfo.reportType
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    public String getReporttype() {
	    return reporttype;
    }

	/**
     * This method was generated by MyBatis Generator. This method sets the value of the database column FakeDateInfo.reportType
     * @param reporttype  the value for FakeDateInfo.reportType
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    public void setReporttype(String reporttype) {
	    this.reporttype = reporttype;
    }

	/**
     * This method was generated by MyBatis Generator. This method returns the value of the database column FakeDateInfo.value
     * @return  the value of FakeDateInfo.value
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    public String getValue() {
	    return value;
    }

	/**
     * This method was generated by MyBatis Generator. This method sets the value of the database column FakeDateInfo.value
     * @param value  the value for FakeDateInfo.value
     * @mbggenerated  Thu Jul 31 14:17:40 CST 2014
     */
    public void setValue(String value) {
	    this.value = value;
    }
}