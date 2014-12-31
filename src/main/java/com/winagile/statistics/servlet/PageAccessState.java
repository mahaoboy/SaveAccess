package com.winagile.statistics.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.user.User;
import com.ibm.icu.text.SimpleDateFormat;
import com.winagile.activeObject.AccessSaveService;
import com.winagile.activeObject.SaveAccess;

public class PageAccessState extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final AccessSaveService as;
	private final PageManager pm;
	private UserAccessor ua;
	private List<Map<String, String>> realList = new ArrayList<Map<String, String>>();
	private Map<String, String> pageInfoAccessDate = new HashMap<String, String>();
	private WebSudoManager webSudoManager;

	public PageAccessState(AccessSaveService as, PageManager pm,
			UserAccessor ua, WebSudoManager webSudoManager) {
		this.as = as;
		this.pm = pm;
		this.ua = ua;
		this.webSudoManager = webSudoManager;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			webSudoManager.willExecuteWebSudoRequest(req);
			List<SaveAccess> asResult = as.all();
			getRealContext(asResult);

			Map<String, Object> context = MacroUtils.defaultVelocityContext();
			context.put("asResult", realList);
			context.put("accessDate", pageInfoAccessDate);
			resp.setContentType("text/html");
			resp.getWriter().write(
					VelocityUtils.getRenderedTemplate(
							"/templates/pageAccessState.vm", context));
		} catch (WebSudoSessionException wes) {
			webSudoManager.enforceWebSudoProtection(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			webSudoManager.willExecuteWebSudoRequest(req);
			Map<?, ?> context = MacroUtils.defaultVelocityContext();
			resp.setContentType("text/html");
			resp.getWriter().write(
					VelocityUtils.getRenderedTemplate(
							"/templates/pageAccessState.vm", context));

		} catch (WebSudoSessionException wes) {
			webSudoManager.enforceWebSudoProtection(req, resp);
		}
	}

	private void getRealContext(List<SaveAccess> saR) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		realList = new ArrayList<Map<String, String>>();
		pageInfoAccessDate = new HashMap<String, String>();

		for (SaveAccess saRI : saR) {
			if (pageInfoAccessDate.containsKey(saRI.getPageId().toString()
					+ saRI.getUserKey())) {
				StringBuffer extraAccessTimeList = new StringBuffer();
				extraAccessTimeList.append(pageInfoAccessDate.get(saRI
						.getPageId().toString() + saRI.getUserKey()));
				extraAccessTimeList.append("<br />");
				extraAccessTimeList.append(sdf.format(new Date(saRI
						.getAccessEntity())));
				pageInfoAccessDate.put(
						saRI.getPageId().toString() + saRI.getUserKey(),
						extraAccessTimeList.toString());

			} else {
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

					pageInfoAccessDate.put(
							saRI.getPageId().toString() + saRI.getUserKey(),
							sdf.format(new Date(saRI.getAccessEntity())));
					realList.add(pageInfo);
				} else {
					continue;
				}
			}
			System.out.println(realList.toString());
			System.out.println(pageInfoAccessDate.toString());
		}
	}
}