package com.myhexin.DB.mybatis.mode;

public class QueryClass {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column QueryClass.id
     *
     * @mbggenerated Fri Jul 3 10:05:05 CST 2015
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column QueryClass.tagName
     *
     * @mbggenerated Fri Jul 3 10:05:05 CST 2015
     */
    private String tagName;


    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column QueryClass.id
     *
     * @return the value of QueryClass.id
     *
     * @mbggenerated Fri Apr 10 09:34:39 CST 2015
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column QueryClass.id
     *
     * @param id the value for QueryClass.id
     *
     * @mbggenerated Fri Apr 10 09:34:39 CST 2015
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column QueryClass.tagName
     *
     * @return the value of QueryClass.tagName
     *
     * @mbggenerated Fri Apr 10 09:34:39 CST 2015
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column QueryClass.tagName
     *
     * @param id the value for QueryClass.tagName
     *
     * @mbggenerated Fri Apr 10 09:34:39 CST 2015
     */
    public void setId(String tagName) {
        this.tagName = tagName;
    }

	@Override
	public String toString() {
		return "QueryClass [id=" + id + ", tagName=" + tagName + "]";
	}
    
}