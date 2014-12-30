package com.winagile.activeObject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.user.UserKey;

public class AccessSaveServiceImpl implements AccessSaveService {

	private final ActiveObjects ao;

	public AccessSaveServiceImpl(ActiveObjects ao) {
		this.ao = checkNotNull(ao);
	}

	@Override
	public SaveAccess add(Long pageId, UserKey userName) {
		final SaveAccess[] sa;
		sa = ao.find(SaveAccess.class, "userName = ? AND pageId = ?", userName,
				pageId);
		if (sa.length > 0) {
			sa[0].setAccessCount(sa[0].getAccessCount() + 1);
			List<Date> updateTime = sa[0].getAccessTime();
			Date now = new Date();
			updateTime.add(now);
			sa[0].setAccessTime(updateTime);
			sa[0].setLastAccessDate(now);
			sa[0].save();
			return sa[0];
		} else {
			final SaveAccess saS = ao.create(SaveAccess.class);
			saS.setPageId(pageId);
			saS.setUserName(userName);
			saS.setAccessCount(1L);
			List<Date> updateTime = new ArrayList<Date>();
			Date now = new Date();
			updateTime.add(now);
			saS.setAccessTime(updateTime);
			saS.setLastAccessDate(now);
			saS.save();
			return saS;
		}

	}

	@Override
	public List<SaveAccess> all() {
		return newArrayList(ao.find(SaveAccess.class));
	}

}
