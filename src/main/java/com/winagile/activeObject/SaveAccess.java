package com.winagile.activeObject;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface SaveAccess extends Entity {
	Long getPageId();

	void setPageId(Long pageId);

	String getUserKey();

	void setUserKey(String userKey);

	Long getAccessEntity();

	void setAccessEntity(Long accessEntity);
}