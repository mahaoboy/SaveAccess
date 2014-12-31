package com.winagile.activeObject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Date;
import java.util.List;

import net.java.ao.Query;

import com.atlassian.activeobjects.external.ActiveObjects;

public class AccessSaveServiceImpl implements AccessSaveService {

	private final ActiveObjects ao;
	private static String TYPE = "type";
	private static String LASTTAG = "last";
	private static String OTHERTAG = "other";

	public AccessSaveServiceImpl(ActiveObjects ao) {
		this.ao = checkNotNull(ao);
	}

	@Override
	public SaveAccess add(Long pageId, String userkey) {
		final SaveAccess saSCount = ao.create(SaveAccess.class);
		Long now = new Date().getTime();
		setLastTagToOther(pageId, userkey);

		saSCount.setPageId(pageId);
		saSCount.setUserKey(userkey);
		saSCount.setAccessEntity(now);
		saSCount.setType(LASTTAG);
		saSCount.save();
		return saSCount;
	}

	@Override
	public List<SaveAccess> all() {
		return newArrayList(ao.find(SaveAccess.class,
				Query.select().order("id DESC")));
	}

	@Override
	public int getAccessCount(Long pageId, String userKey) {
		return ao.count(SaveAccess.class, "page_id = ? AND user_key = ?",
				pageId, userKey);
	}

	@Override
	public int getCurrentItemsNum() {
		return ao.count(SaveAccess.class,
				Query.select().where(TYPE + " = ?", LASTTAG));
	}

	@Override
	public List<SaveAccess> filterWithLimit(int limitSize, int offset) {
		return newArrayList(ao.find(SaveAccess.class,
				Query.select().where(TYPE + " = ?", LASTTAG).order("id DESC")
						.limit(limitSize).offset(offset)));
	}

	@Override
	public List<SaveAccess> getAccessByFilter(Long pageId, String userkey) {
		return newArrayList(ao.find(SaveAccess.class,
				"page_id = ? AND user_key = ?", pageId, userkey));
	}

	private void setLastTagToOther(Long pageId, String userkey) {
		final SaveAccess[] saSLast = ao.find(SaveAccess.class,
				"page_id = ? AND user_key = ? AND type = ?", pageId, userkey,
				LASTTAG);
		if (saSLast.length > 0) {
			for (SaveAccess saI : saSLast) {
				saI.setType(OTHERTAG);
				saI.save();
			}
		}
	}

}
