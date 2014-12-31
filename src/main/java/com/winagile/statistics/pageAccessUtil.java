package com.winagile.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.ibm.icu.text.SimpleDateFormat;
import com.winagile.activeObject.AccessSaveService;
import com.winagile.activeObject.SaveAccess;

public class pageAccessUtil {
	private List<Map<String, String>> realList = new ArrayList<Map<String, String>>();
	private Map<String, String> pageInfoAccessDate = new HashMap<String, String>();
	public static int START_INDEX = 0;
	public static int COUNT_ON_EACH_PAGE = 2;
	private int totalPage;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final AccessSaveService as;
	private final PageManager pm;
	private UserAccessor ua;

	public pageAccessUtil(AccessSaveService as, PageManager pm, UserAccessor ua) {
		this.as = as;
		this.pm = pm;
		this.ua = ua;
	}

	public void getAllRealContext() {
		List<SaveAccess> saR = as.filterWithLimit(COUNT_ON_EACH_PAGE,
				START_INDEX);
		System.out.println("saR size:" + saR.size());

		realList = new ArrayList<Map<String, String>>();
		pageInfoAccessDate = new HashMap<String, String>();

		getRealList(saR);
		totalPage = calculateTotalPage(as.getCurrentItemsNum(), COUNT_ON_EACH_PAGE);
	}

	private void getRealList(List<SaveAccess> saR) {
		if (!saR.isEmpty()) {
			for (SaveAccess saRI : saR) {
				Map<String, String> pageInfo = new HashMap<String, String>();
				Page accessPage = pm.getPage(saRI.getPageId());
				User accessUser = ua
						.getUserByKey(new UserKey(saRI.getUserKey()));
				if (accessUser != null && accessPage != null) {
					pageInfo.put("userName", accessUser.getFullName());

					StringBuffer groupNameList = new StringBuffer();
					if (ua.getGroupNames(accessUser) != null) {
						for (String groupName : ua.getGroupNames(accessUser)) {
							groupNameList.append(groupName);
							groupNameList.append("<br />");
						}
					}
					pageInfo.put("userGroups", groupNameList.append("")
							.toString());

					pageInfo.put("pageTitle", accessPage.getDisplayTitle());
					pageInfo.put("pageUrl", accessPage.getUrlPath());
					pageInfo.put("accessCount",
							String.valueOf(as.getAccessCount(saRI.getPageId(),
									saRI.getUserKey())));
					pageInfo.put("uniqueKey", saRI.getPageId().toString()
							+ saRI.getUserKey());

					getAccessTimeList(saRI.getPageId(), saRI.getUserKey());
					realList.add(pageInfo);
				} else {
					continue;
				}
				System.out.println(realList.toString());
				System.out.println(pageInfoAccessDate.toString());
			}
		}
	}

	private void getAccessTimeList(Long pageId, String userkey) {
		List<SaveAccess> saRT = as.getAccessByFilter(pageId, userkey);
		if (!saRT.isEmpty()) {
			StringBuffer extraAccessTimeList = new StringBuffer();
			for (SaveAccess saRTI : saRT) {
				extraAccessTimeList.append(sdf.format(new Date(saRTI
						.getAccessEntity())));
				extraAccessTimeList.append("<br />");
			}
			pageInfoAccessDate.put(pageId.toString() + userkey,
					extraAccessTimeList.append("").toString());
		}
	}

	protected int calculateTotalPage(int totalItem, int pageSize) {
		double dPageTotal = Math.ceil((double) totalItem / (double) pageSize);
		return (int) dPageTotal;
	}

	// method in protected scope for unit test
	protected int calculateStartIndex(int pageNumber, int pageSize) {
		return Math.max(0, (pageNumber - 1) * pageSize);
	}

	public List<Map<String, String>> getRealList() {
		return realList;
	}

	public void setRealList(List<Map<String, String>> realList) {
		this.realList = realList;
	}

	public Map<String, String> getPageInfoAccessDate() {
		return pageInfoAccessDate;
	}

	public void setPageInfoAccessDate(Map<String, String> pageInfoAccessDate) {
		this.pageInfoAccessDate = pageInfoAccessDate;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

}
