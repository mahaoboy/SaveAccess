package com.winagile.activeObject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Date;
import java.util.List;

import net.java.ao.Query;

import com.atlassian.activeobjects.external.ActiveObjects;

public class AccessSaveServiceImpl implements AccessSaveService {

	private final ActiveObjects ao;
	private static String TYPE = "TYPE";
	private static String USER_KEY = "USER_KEY";
	private static String PAGE_ID = "PAGE_ID";
	private static String ACCESS_ENTITY = "ACCESS_ENTITY";
	private static String ID = "ID";
	private static String LASTTAG = "last";
	private static String OTHERTAG = "other";
	private static String ORDERBY = ID + " DESC";

	public AccessSaveServiceImpl(ActiveObjects ao) {
		this.ao = checkNotNull(ao);
	}

	@Override
	public SaveAccess add(Long pageId, String userkey, String respTime) {
		final SaveAccess saSCount = ao.create(SaveAccess.class);
		Long now = new Date().getTime();
		setLastTagToOther(pageId, userkey);

		saSCount.setPageId(pageId);
		saSCount.setUserKey(userkey);
		saSCount.setAccessEntity(now);
		saSCount.setRespTime(respTime);
		saSCount.setType(LASTTAG);
		saSCount.save();
		return saSCount;
	}

	@Override
	public List<SaveAccess> all(String userKey) {
		if (userKey == null) {
			return newArrayList(ao
					.find(SaveAccess.class,
							Query.select().where(TYPE + " = ?", LASTTAG)
									.order(ORDERBY)));
		} else {
			return newArrayList(ao.find(
					SaveAccess.class,
					Query.select()
							.where(TYPE + " = ?  AND " + USER_KEY + " = ?",
									LASTTAG, userKey).order(ORDERBY)));
		}
	}

	@Override
	public int getAccessCount(Long pageId, String userKey) {
		return ao.count(SaveAccess.class, PAGE_ID + " = ? AND " + USER_KEY
				+ " = ?", pageId, userKey);
	}

	@Override
	public int getCurrentItemsNum() {
		return ao.count(SaveAccess.class,
				Query.select().where(TYPE + " = ?", LASTTAG));
	}

	@Override
	public List<SaveAccess> filterWithLimit(int limitSize, int offset) {
		return newArrayList(ao.find(SaveAccess.class,
				Query.select().where(TYPE + " = ?", LASTTAG).order(ORDERBY)
						.limit(limitSize).offset(offset)));
	}

	@Override
	public List<SaveAccess> getAccessByFilter(Long pageId, String userkey) {
		return newArrayList(ao.find(
				SaveAccess.class,
				Query.select()
						.where(PAGE_ID + " = ? AND " + USER_KEY + " = ?",
								pageId, userkey).order(ORDERBY)));
	}

	private void setLastTagToOther(Long pageId, String userkey) {
		final SaveAccess[] saSLast = ao.find(SaveAccess.class, PAGE_ID
				+ " = ? AND " + USER_KEY + " = ? AND " + TYPE + " = ?", pageId,
				userkey, LASTTAG);
		if (saSLast.length > 0) {
			for (SaveAccess saI : saSLast) {
				saI.setType(OTHERTAG);
				saI.save();
			}
		}
	}

	@Override
	public List<SaveAccess> filterWithDate(Long startDate, Long endDate,
			String userKey) {
		if (userKey == null) {
			return newArrayList(ao
					.find(SaveAccess.class,
							Query.select()
									.where(TYPE + " = ? AND " + ACCESS_ENTITY
											+ " > ? AND " + ACCESS_ENTITY
											+ " < ?", LASTTAG, startDate,
											endDate).order(ORDERBY)));
		} else {
			return newArrayList(ao.find(
					SaveAccess.class,
					Query.select()
							.where(TYPE + " = ? AND " + ACCESS_ENTITY
									+ " > ? AND " + ACCESS_ENTITY + " < ? AND "
									+ USER_KEY + " = ?", LASTTAG, startDate,
									endDate, userKey).order(ORDERBY)));
		}
	}

	@Override
	public List<SaveAccess> filterWithStartDate(Long startDate, String userKey) {
		if (userKey == null) {
			return newArrayList(ao.find(
					SaveAccess.class,
					Query.select()
							.where(TYPE + " = ? AND " + ACCESS_ENTITY + " > ?",
									LASTTAG, startDate).order(ORDERBY)));
		} else {
			return newArrayList(ao.find(
					SaveAccess.class,
					Query.select()
							.where(TYPE + " = ? AND " + ACCESS_ENTITY
									+ " > ? AND " + USER_KEY + " = ?", LASTTAG,
									startDate, userKey).order(ORDERBY)));
		}
	}

	@Override
	public List<SaveAccess> filterWithEndDate(Long endDate, String userKey) {
		if (userKey == null) {
			return newArrayList(ao.find(
					SaveAccess.class,
					Query.select()
							.where(TYPE + " = ? AND " + ACCESS_ENTITY + " < ?",
									LASTTAG, endDate).order(ORDERBY)));
		} else {
			return newArrayList(ao.find(
					SaveAccess.class,
					Query.select()
							.where(TYPE + " = ? AND " + ACCESS_ENTITY
									+ " < ? AND " + USER_KEY + " = ?", LASTTAG,
									endDate, userKey).order(ORDERBY)));
		}
	}

	@Override
	public List<SaveAccess> getAccessByFilterWithDate(Long pageId,
			String userkey, Long startDate, Long endDate) {
		return newArrayList(ao.find(
				SaveAccess.class,
				Query.select()
						.where(PAGE_ID + " = ? AND " + USER_KEY + " = ? AND "
								+ ACCESS_ENTITY + " > ? AND " + ACCESS_ENTITY
								+ " < ?", pageId, userkey, startDate, endDate)
						.order(ORDERBY)));
	}

	@Override
	public List<SaveAccess> getAccessByFilterWithStartDate(Long pageId,
			String userkey, Long startDate) {
		return newArrayList(ao.find(
				SaveAccess.class,
				Query.select()
						.where(PAGE_ID + " = ? AND " + USER_KEY + " = ? AND "
								+ ACCESS_ENTITY + " > ?", pageId, userkey,
								startDate).order(ORDERBY)));
	}

	@Override
	public List<SaveAccess> getAccessByFilterWithEndDate(Long pageId,
			String userkey, Long endDate) {
		return newArrayList(ao.find(
				SaveAccess.class,
				Query.select()
						.where(PAGE_ID + " = ? AND " + USER_KEY + " = ? AND "
								+ ACCESS_ENTITY + " < ?", pageId, userkey,
								endDate).order(ORDERBY)));
	}

}
