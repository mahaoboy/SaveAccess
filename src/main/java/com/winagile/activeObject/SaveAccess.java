package com.winagile.activeObject;

import java.util.Date;
import java.util.List;

import net.java.ao.Entity;
import net.java.ao.Preload;

import com.atlassian.sal.api.user.UserKey;

@Preload
public interface SaveAccess extends Entity {
	Long getPageId();

	void setPageId(Long pageId);

	UserKey getUserName();

	void setUserName(UserKey userName);

	List<Date> getAccessTime();

	void setAccessTime(List<Date> accessTime);

	Long getAccessCount();

	void setAccessCount(Long accessCcount);
	
	Date getLastAccessDate();

	void setLastAccessDate(Date lastAccessDate);
}