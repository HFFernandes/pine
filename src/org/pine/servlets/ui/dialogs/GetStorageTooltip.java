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
package org.pine.servlets.ui.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.pine.dao.Dao;
import org.pine.model.Key;
import org.pine.model.Row;
import org.pine.model.Value;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetStorageTooltip")
public class GetStorageTooltip extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetStorageTooltip() {
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

			String content = request.getParameter("content");

			String[] indexes = content.split(";");
			boolean correctFormat = true;
			for (int i = 0; i < indexes.length; i++) {
				if (!StringUtils.isNumeric(indexes[i])) {
					correctFormat = false;
					break;
				}
				if (("0").equals(indexes[i])) {
					correctFormat = false;
					break;
				}
			}

			if (correctFormat) {
				Value value = Dao.getValue(Integer.parseInt(request.getParameter("id")));
				Integer[] storageIds = value.getStorageIds();
				out.print(content + getStorageTooltip(storageIds));
			} else {
				out.print(content);
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getStorageTooltip(Integer[] integers) throws SQLException {
		if (integers != null) {
			StringBuilder result = new StringBuilder(
					"<div class=\"tooltip\"><div style=\"width: auto;\" class=\"table\">");
			int tableId = Dao.getRow(integers[0]).getTableId();

			List<Key> keys = Dao.getKeys(tableId);
			result.append("<div class=\"table-row key-row\">");
			result.append("<div class=\"table-cell ui-cell-mini index-header-cell\">Index</div>");
			for (Key key : keys) {
				result.append("<div class=\"table-cell ui-cell-mini key-cell\">");
				result.append(key.getName());
				result.append("</div>");
			}
			result.append("</div>");

			for (int i = 0; i < integers.length; i++) {
				Row row = Dao.getRow(integers[i]);
				List<Value> values = Dao.getValues(row);
				result.append("<div class=\"table-row value-row\">");
				result.append("<div id=\"").append(row.getId());
				result.append("\" class=\"table-cell ui-cell-mini index-cell\">");
				result.append(row.getOrder());
				result.append("</div>");
				for (Value value : values) {
					String storageCell = (value.isStorage()) ? " storage-cell" : "";
					result.append("<div class=\"table-cell ui-cell-mini value-cell ");
					result.append(storageCell).append("\">");
					result.append(value.getValue());
					result.append("</div>");
				}
				result.append("</div>");
			}
			result.append("</div>");
			result.append("<br><a href=\"/pine/storages/?id=").append(tableId)
					.append("\" target=\"_blank\">Open storage in the new tab</a></div>");
			return result.toString();
		}
		return "";
	}

}
