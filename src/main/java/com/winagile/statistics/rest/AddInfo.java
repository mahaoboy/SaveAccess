package com.winagile.statistics.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.winagile.activeObject.AccessSaveService;

/**
 * A resource of message.
 */
@Path("/message")
public class AddInfo {

	private static String SUCC = "success";
	private static String FAIL = "failed";
	private static String NOTLOGINPAGENOTEXIST = "notLoginOrPageNotExist";
	private AccessSaveService accessSaveService;
	private PageManager pm;

	public AddInfo(AccessSaveService accessSaveService, UserAccessor ua,
			PageManager pm) {
		this.accessSaveService = accessSaveService;
		this.pm = pm;
	}

	@GET
	@AnonymousAllowed
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/{key}")
	public Response getMessage(@PathParam("key") String key) {
		return Response.ok(new AddInfoModel(addAccessInfo(key))).build();
	}

	private String addAccessInfo(String pageId) {
		final String[] splittedStr = pageId.split("-");
		final String pageIds = splittedStr[0];
		final String respTime = splittedStr[1] != null ? splittedStr[1] : "";
		if (AuthenticatedUserThreadLocal.get() != null
				&& pm.getPage(Long.parseLong(pageIds)) != null) {
			if (accessSaveService.add(Long.parseLong(pageIds),
					AuthenticatedUserThreadLocal.get().getKey()
							.getStringValue(), respTime) != null) {
				return SUCC;
			} else {
				return FAIL;
			}
		} else {
			return NOTLOGINPAGENOTEXIST;
		}
	}
}