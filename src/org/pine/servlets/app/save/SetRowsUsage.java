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
package org.pine.servlets.app.save;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Table;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/SetRowsUsage")
public class SetRowsUsage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SetRowsUsage() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();

			int id = Integer.parseInt(request.getParameter("id"));
			Table table = Dao.getTable(id);
			boolean usage = false;
			if (request.getParameter("usage") != null) {
				usage = Boolean.parseBoolean(request.getParameter("usage"));
			}

			table.setShowUsage(usage);
			Dao.updateTable(table);
			out.print("success");

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
