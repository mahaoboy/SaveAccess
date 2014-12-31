package com.winagile.activeObject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Date;
import java.util.List;

import net.java.ao.Query;

import com.atlassian.activeobjects.external.ActiveObjects;

public class AccessSaveServiceImpl implements AccessSaveService {

	private final ActiveObjects ao;

	public AccessSaveServiceImpl(ActiveObjects ao) {
		this.ao = checkNotNull(ao);
	}

	@Override
	public SaveAccess add(Long pageId, String userkey) {
		final SaveAccess saSCount = ao.create(SaveAccess.class);
		Long now = new Date().getTime();

		saSCount.setPageId(pageId);
		saSCount.setUserKey(userkey);
		saSCount.setAccessEntity(now);
		saSCount.save();
		return saSCount;
	}

	@Override
	public List<SaveAccess> all() {
		return newArrayList(ao.find(SaveAccess.class, Query.select().order("id DESC")));
	}

	@Override
	public int getAccessCount(Long pageId, String userKey) {
		// TODO Auto-generated method stub

		return ao.count(SaveAccess.class, "page_id = ? AND user_key = ?",
				pageId, userKey);
	}

}
