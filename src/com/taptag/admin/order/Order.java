package com.taptag.admin.order;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.taptag.admin.date.TimestampDateDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Comparable<Order> {

	private Integer id;
	@JsonProperty(value = "company_id")
	private Integer companyId;
	@JsonProperty(value = "vendor_id")
	private Integer vendorId;
	private Boolean paid;
	private Boolean completed;
	@JsonProperty(value = "created_at")
	@JsonDeserialize(using = TimestampDateDeserializer.class)
	private Date createdAt;
		
	public Order() {
		
	}

	//============================================================
	//====================GETTERS AND SETTERS=====================
	//============================================================
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public int compareTo(Order another) {
		return this.createdAt.compareTo(another.getCreatedAt());
	}

	
	
}
