package com.winagile.statistics.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.spring.container.ContainerManager;
import com.winagile.activeObject.AccessSaveService;

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddInfoModel {
	private static String SUCC = "success";
	private static String FAIL = "failed";

	@XmlElement(name = "value")
	private String message;

	public AddInfoModel() {
	}

	public AddInfoModel(String key) {
		AccessSaveService accessSaveService = (AccessSaveService) ContainerManager
				.getComponent("AccessSaveService");
		if (addAccessInfo(key, accessSaveService)) {
			this.message = SUCC;
		} else {
			this.message = FAIL;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private boolean addAccessInfo(String pageId,
			AccessSaveService accessSaveService) {
		
		accessSaveService.add(Long.parseLong(pageId),
				AuthenticatedUserThreadLocal.get().getKey());

		return true;

	}
}