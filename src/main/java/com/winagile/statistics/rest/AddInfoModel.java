package com.winagile.statistics.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddInfoModel {

	@XmlElement(name = "value")
	private String message;

	public AddInfoModel() {
	}

	public AddInfoModel(String key) {
			this.message = key;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}