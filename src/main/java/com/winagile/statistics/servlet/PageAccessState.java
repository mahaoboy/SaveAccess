package com.winagile.statistics.servlet;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bucket.core.actions.PaginationSupport;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.winagile.statistics.pageAccessUtil;

public class PageAccessState extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private WebSudoManager webSudoManager;
	private final pageAccessUtil pageUtil;
	private PaginationSupport<Map<String, String>> paginationSupport = new PaginationSupport<Map<String, String>>(
			pageAccessUtil.COUNT_ON_EACH_PAGE);
	private final LoginUriProvider loginUriProvider;
	private final UserManager userManager;

	public PageAccessState(WebSudoManager webSudoManager,
			pageAccessUtil pageUtil, LoginUriProvider loginUriProvider, UserManager userManager) {
		this.webSudoManager = webSudoManager;
		this.pageUtil = pageUtil;
		this.loginUriProvider = loginUriProvider;
		this.userManager = userManager;
	}

	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
	}
	
	private URI getUri(HttpServletRequest request)
	{
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null)
		{
			builder.append("?");
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	} 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
			if (loginUser == null || !userManager.isSystemAdmin(loginUser.getKey())) {
			    redirectToLogin(req, resp);
			    return;
			}
			webSudoManager.willExecuteWebSudoRequest(req);
			int pageNum, totalItemNum;
			if (req.getParameterMap().containsKey("pageNumber") && req.getParameter("pageNumber").length() > 0) {
				pageNum = Integer.valueOf(req.getParameter("pageNumber"));
			} else {
				pageNum = 1;
			}
			if (req.getParameterMap().containsKey("totalItem") && req.getParameter("totalItem").length() > 0) {
				totalItemNum = Integer.valueOf(req.getParameter("totalItem"));
				pageUtil.setTotalItem(totalItemNum);
			}else{
				pageUtil.setTotalItem(0);
			}
			
			if (req.getParameterMap().containsKey("groupNameField") && req.getParameter("groupNameField").length() > 0) {
				pageUtil.setGroupName(req.getParameter("groupNameField"));
			}else{
				pageUtil.setGroupName(null);
			}
			if (req.getParameterMap().containsKey("fromDate") && req.getParameter("fromDate").length() > 0) {
				System.out.println("req.getParameter(fromDate).length() : " + req.getParameter("fromDate").length());
				pageUtil.setFromDate(req.getParameter("fromDate"));
			}else{
				pageUtil.setFromDate(null);
			}
			if (req.getParameterMap().containsKey("toDate") && req.getParameter("toDate").length() > 0) {
				pageUtil.setToDate(req.getParameter("toDate"));
			}else{
				pageUtil.setToDate(null);
			}
			pageUtil.getFilteredRealContext(pageNum);

			Map<String, Object> context = MacroUtils.defaultVelocityContext();
			context.put("asResult", pageUtil.getRealList());
			context.put("accessDate", pageUtil.getPageInfoAccessDate());

			context.put("pageNumber", pageNum);
			context.put("totalPage", pageUtil.getTotalPage());
			context.put("totalItem", pageUtil.getTotalItem());
			context.put("paginationSupport", paginationSupport);

			context.put("groupName", pageUtil.getGroupName());
			context.put("fromDate", pageUtil.getFromDate());
			context.put("toDate", pageUtil.getToDate());

			context.put("AllGroups", pageUtil.getGroupList());

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
		doGet(req, resp);
	}

}