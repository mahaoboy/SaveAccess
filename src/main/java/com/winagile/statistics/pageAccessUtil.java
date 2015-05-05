package com.winagile.statistics;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.ibm.icu.text.SimpleDateFormat;
import com.winagile.activeObject.AccessSaveService;
import com.winagile.activeObject.SaveAccess;

public class pageAccessUtil {
	private List<Map<String, String>> realList = new ArrayList<Map<String, String>>();
	private Map<String, String> pageInfoAccessDate = new HashMap<String, String>();
	public static int START_INDEX = 0;
	public static int COUNT_ON_EACH_PAGE = 20;
	private int totalPage;
	private int totalItem = 0;
	private String groupName = null;
	private String spaceName = null;

	private Long fromDate = null;
	private Long toDate = null;
	private List<String> groupList = new ArrayList<String>();
	private List<String> spaceList = new ArrayList<String>();

	private String pageTitle = null;
	private UserKey userKey = null;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	private final AccessSaveService as;
	private final PageManager pm;
	private UserAccessor ua;
	private GroupManager gm;
	private UserManager um;
	private SpaceManager sm;

	public pageAccessUtil(AccessSaveService as, PageManager pm,
			UserAccessor ua, GroupManager gm, UserManager um, SpaceManager sm) {
		this.as = as;
		this.pm = pm;
		this.ua = ua;
		this.gm = gm;
		this.um = um;
		this.sm = sm;
	}

	public void getAllRealContext(int pageNum) {
		List<SaveAccess> saR = as.filterWithLimit(COUNT_ON_EACH_PAGE,
				calculateStartIndex(pageNum, COUNT_ON_EACH_PAGE));
		System.out.println("saR size:" + saR.size());

		realList = new ArrayList<Map<String, String>>();
		pageInfoAccessDate = new HashMap<String, String>();

		getRealList(saR);

		if (totalItem == 0) {
			totalItem = as.getCurrentItemsNum();
		}

		totalPage = calculateTotalPage(totalItem, COUNT_ON_EACH_PAGE);
	}

	public void getFilteredRealContext(int pageNum) {
		if (groupName == null && fromDate == null && toDate == null
				&& userKey == null && pageTitle == null && spaceName == null) {
			getAllRealContext(pageNum);
		} else {
			List<SaveAccess> saR;
			if (fromDate != null && toDate != null) {
				saR = as.filterWithDate(fromDate, toDate,
						userKey == null ? null : userKey.getStringValue());
			} else if (fromDate != null && toDate == null) {
				saR = as.filterWithStartDate(fromDate, userKey == null ? null
						: userKey.getStringValue());
			} else if (fromDate == null && toDate != null) {
				saR = as.filterWithEndDate(toDate, userKey == null ? null
						: userKey.getStringValue());
			} else {
				saR = as.all(userKey == null ? null : userKey.getStringValue());
			}

			if (pageTitle != null) {
				saR = filterPageTitle(saR);
			}

			if (groupName != null) {
				saR = filterGroup(saR);
			}

			if (spaceName != null) {
				saR = filterSpace(saR);
			}

			if (totalItem == 0) {
				totalItem = saR.size();
			}

			realList = new ArrayList<Map<String, String>>();
			pageInfoAccessDate = new HashMap<String, String>();

			totalPage = calculateTotalPage(totalItem, COUNT_ON_EACH_PAGE);
			int startIndex = calculateStartIndex(pageNum, COUNT_ON_EACH_PAGE);
			int endindex = startIndex + COUNT_ON_EACH_PAGE > saR.size() ? saR
					.size() : startIndex + COUNT_ON_EACH_PAGE;
			getRealList(saR.subList(startIndex, endindex));
		}
	}

	private List<SaveAccess> filterPageTitle(List<SaveAccess> saR) {
		List<SaveAccess> saRnew = new ArrayList<SaveAccess>();
		if (!saR.isEmpty()) {
			for (SaveAccess saRI : saR) {
				if (pm.getPage(saRI.getPageId()) != null && pm.getPage(saRI.getPageId()).getTitle().equals(pageTitle)) {
					saRnew.add(saRI);
				}
			}
		}
		return saRnew;
	}

	private List<SaveAccess> filterGroup(List<SaveAccess> saR) {
		List<SaveAccess> saRnew = new ArrayList<SaveAccess>();
		if (!saR.isEmpty()) {
			for (SaveAccess saRI : saR) {
				if (um.isUserInGroup(new UserKey(saRI.getUserKey()), groupName)) {
					saRnew.add(saRI);
				}
			}
		}
		return saRnew;
	}

	private List<SaveAccess> filterSpace(List<SaveAccess> saR) {
		List<SaveAccess> saRnew = new ArrayList<SaveAccess>();
		if (!saR.isEmpty()) {
			for (SaveAccess saRI : saR) {
				if (sm.getSpaceFromPageId(saRI.getPageId()) != null &&spaceName.equals(sm.getSpace(
						sm.getSpaceFromPageId(saRI.getPageId())).getName())) {
					// System.out.println("target spaceName : " + spaceName);
					// System.out.println("current spaceName : " + sm.getSpace(
					// sm.getSpaceFromPageId(saRI.getPageId())).getName());
					saRnew.add(saRI);
				}
			}
		}
		return saRnew;
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
					pageInfo.put(
							"pageSpace",
							sm.getSpace(sm.getSpaceFromPageId(saRI.getPageId()))
									.getDisplayTitle());

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
				// System.out.println(realList.toString());
				// System.out.println(pageInfoAccessDate.toString());
			}
		}
	}

	private void getAccessTimeList(Long pageId, String userkey) {
		List<SaveAccess> saRT = new ArrayList<SaveAccess>();
		if (fromDate != null && toDate != null) {
			saRT = as.getAccessByFilterWithDate(pageId, userkey, fromDate,
					toDate);
		} else if (fromDate != null && toDate == null) {
			saRT = as.getAccessByFilterWithStartDate(pageId, userkey, fromDate);
		} else if (fromDate == null && toDate != null) {
			saRT = as.getAccessByFilterWithEndDate(pageId, userkey, toDate);
		} else {
			saRT = as.getAccessByFilter(pageId, userkey);
		}

		if (!saRT.isEmpty()) {
			StringBuffer extraAccessTimeList = new StringBuffer();
			for (SaveAccess saRTI : saRT) {
				extraAccessTimeList.append(sdf.format(new Date(saRTI
						.getAccessEntity())));
				extraAccessTimeList.append(" / ");
				extraAccessTimeList.append(saRTI.getRespTime());
				extraAccessTimeList.append("ms");
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

	public int getTotalItem() {
		return totalItem;
	}

	public void setTotalItem(int totalItem) {
		this.totalItem = totalItem;
	}

	public List<String> getGroupList() {
		try {
			groupList = new ArrayList<String>();
			for (Group pg : gm.getGroups()) {
				groupList.add(pg.getName());
			}
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getFromDate() {
		return fromDate == null ? null : sdfDate.format(new Date(fromDate));
	}

	public void setFromDate(String fromDate) {
		try {
			this.fromDate = fromDate == null ? null : sdfDate.parse(fromDate)
					.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getToDate() {
		return toDate == null ? null : sdfDate.format(new Date(toDate));
	}

	public void setToDate(String toDate) {
		try {
			this.toDate = toDate == null ? null : sdfDate.parse(toDate)
					.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getUserKey() {
		return userKey == null ? null : ua.getUserByKey(userKey).getName();
	}

	public void setUserKey(String userName) {
		this.userKey = userName == null ? null : ua.getUserByName(userName)
				.getKey();
	}

	public List<String> getSpaceList() {
		spaceList = new ArrayList<String>();
		for (Space sitem : sm.getAllSpaces()) {
			spaceList.add(sitem.getName());
		}
		return spaceList;
	}

	public void setSpaceList(List<String> spaceList) {
		this.spaceList = spaceList;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}

}
