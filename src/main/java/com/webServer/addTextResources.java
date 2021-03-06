package com.webServer;

import com.common.config;
import com.common.utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.json.JSONObject;

@WebServlet({"/api/webServer/addTextResources"})
public class addTextResources extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        HttpSession session = request.getSession();
        if (session.getAttribute("loginPassword") == null || !session.getAttribute("loginPassword").equals(config.get("loginPassword"))) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "请先登录");
            pw.println(resJo);
            return;
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Map map = utils.getFormData(request);
            String resourceType = ((FileItem) map.get("resourceType")).getString("utf-8");
            String[] sArr = utils.txt2array((FileItem) map.get("txtFile"));
            conn = utils.getConnection();
            stmt = conn.prepareStatement("delete from resource where type = ?");
            stmt.setString(1, resourceType);
            stmt.executeUpdate();
            int saveCount = 0;
            for (String resourceStr : sArr) {
                try {
                    if (!resourceStr.equals("")) {
                        stmt = conn.prepareStatement("insert into resource (type, val, addTime) value(?, ?, ?)");
                        stmt.setString(1, resourceType);
                        stmt.setString(2, resourceStr);
                        stmt.setString(3, utils.getCurrentTimeStr());
                        if(stmt.executeUpdate() == 1) {
                            saveCount++;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            resJo.put("res", "success");
            resJo.put("saveCount", saveCount);
            stmt.close();
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e2) {
                }
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e3) {
            resJo.put("errInfo", utils.getExceptionMsg(e3));
            resJo.put("res", "fail");
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e4) {
                }
            }
        } catch (Throwable th) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e5) {
                }
            }
        }
        pw.println(resJo);
        pw.close();
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}