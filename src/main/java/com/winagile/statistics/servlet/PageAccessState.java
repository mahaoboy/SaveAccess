package com.winagile.statistics.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bucket.core.actions.PaginationSupport;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
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

	public PageAccessState(WebSudoManager webSudoManager,
			pageAccessUtil pageUtil) {
		this.webSudoManager = webSudoManager;
		this.pageUtil = pageUtil;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			webSudoManager.willExecuteWebSudoRequest(req);
			pageUtil.getAllRealContext();

			Map<String, Object> context = MacroUtils.defaultVelocityContext();
			context.put("asResult", pageUtil.getRealList());
			context.put("accessDate", pageUtil.getPageInfoAccessDate());

			context.put("pageNumber", 1);
			context.put("totalPage", pageUtil.getTotalPage());
			context.put("paginationSupport", paginationSupport);

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

}