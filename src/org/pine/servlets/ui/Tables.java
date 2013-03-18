/*******************************************************************************
 * Copyright (c) 2013 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.pine.servlets.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Product;
import org.pine.model.Table;
import org.pine.model.User;
import org.pine.servlets.ServletHelper;
import org.pine.settings.GlobalSettings;
import org.pine.uimodel.Sections;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/tables/")
public class Tables extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Tables() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			if (!GlobalSettings.getInstance().init(getServletContext().getRealPath(""))) {
				response.sendRedirect("/pine/firstlaunch");
				return;
			}
			Dao dao = new Dao();

			if (request.getSession(false) == null) {
				response.sendRedirect("/pine/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			} else if (request.getSession(false).getAttribute("userName") == null) {
				response.sendRedirect("/pine/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			} else if ((request.getParameter("product") == null) && (request.getParameter("id") == null)) {
				response.sendRedirect("/pine");
			} else {

				out.print("<!DOCTYPE html>");
				out.print("<html>");
				out.print("<head>");
				out.print("<title>Data Tables - Pine</title>");
				out.print("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
				out.print("<link href=\"../css/style.css\" rel=\"stylesheet\" type=\"text/css\" />");
				out.print("<link href=\"../css/jquery.contextMenu.css\" rel=\"stylesheet\" type=\"text/css\" />");
				out.print("<script type=\"text/javascript\" src=\"../js/jquery-1.9.1.min.js\"></script>");
				out.print("<script type=\"text/javascript\" src=\"../js/jquery-ui-1.10.2.custom.min.js\"></script>");
				out.print("<script type=\"text/javascript\" src=\"../js/jquery.contextMenu.js\"></script>");

				String userName = (String) request.getSession(false).getAttribute("userName");
				User user = dao.getUserByName(userName);

				int productId = 0;
				int tableId = 0;
				String tableType = "table";
				if (request.getParameter("id") != null) {
					tableId = Integer.parseInt(request.getParameter("id"));
					Table table = dao.getTable(tableId);
					switch (table.getType()) {
					case TABLE:
						productId = dao.getProductIdByPrimaryTableId(tableId);
						break;

					case PRECONDITION:
						productId = dao.getProductIdBySecondaryTableId(tableId);
						break;

					case POSTCONDITION:
						productId = dao.getProductIdBySecondaryTableId(tableId);
						break;

					default:
						break;
					}
					tableType = table.getType().toString().toLowerCase();
				} else {
					productId = Integer.parseInt(request.getParameter("product"));
				}

				if (!user.hasAccessToProduct(productId)) {
					out.println("<a href=\".\"><img id=\"logo-mini\" src=\"../img/pine_logo_mini.png\"></a>");
					out.println("<span id=\"extends-symbol\" style=\"color: rgba(255,255,255,0);\">&nbsp;&gt;&nbsp;</span>");
					out.println("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
				} else {

					out.print("<script type=\"text/javascript\">");
					out.print("var productId = \"" + productId + "\";");
					out.print("var tableId = \"" + tableId + "\";");
					out.print("var tableType = \"" + tableType + "\";");
					out.print("</script>");
					out.print("<script type=\"text/javascript\" src=\"../js/dataCenter.js\"></script>");
					out.print("<script type=\"text/javascript\" src=\"../js/footer.js\"></script>");

					out.print("</head>");
					out.print("<body>");
					out.print(ServletHelper.getUserPanel(user));
					includeHeader(out, "tables", dao.getProduct(productId));

					out.print("<div id=\"main\" class=\"table\">");
					out.print("<div class=\"table-row\">");
					out.print("<div class=\"table-cell entities-list\">");
					out.print("<div id=\"category-container\"></div>");
					out.print("</div>");
					out.print("<div id=\"waiting\" class=\"table-cell\">");
					out.print("<img src=\"../img/ajax-loader.gif\" class=\"waiting-gif\" />");
					out.print("<div class=\"table top-panel\"></div>");
					out.print("<div class=\"table entities-values\" style=\"width: auto;\"></div>");
					out.print("</div>");
					out.print("</div>");
					out.print("</div>");
					out.print(ServletHelper.getContextMenus("table"));
					out.print(ServletHelper.getLoadingGif());
				}
				out.print(ServletHelper.getFooter(getServletContext().getRealPath("")));
				out.print("</body>");
				out.print("</html>");

				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private void includeHeader(PrintWriter out, String sectionKey, Product product) {

		String productName = product.getName();
		String sectionName = Sections.getNameByKey(sectionKey);

		out.print("<a href=\"/pine\"><img id=\"logo-mini\" src=\"../img/pine_logo_mini.png\"></a>");
		out.print("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		out.print("<a href=\"/pine/?product=" + product.getId() + "\">");
		out.print("<span id=\"product-name\" class=\"header-text\">" + productName + "</span></a>");
		out.print("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		out.print("<a href=\"/pine/" + sectionKey + "/?product=" + product.getId() + "\">");
		out.print("<span id=\"section-name\" class=\"header-text\">" + sectionName + "</span></a>");
		out.print("<br /><br />");

	}
}
