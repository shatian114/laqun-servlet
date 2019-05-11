package com.phoneServer;

import com.common.utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/updateLoginWx"})
public class updateLoginWx extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from loginWx where wxid = ? limit 1");
            stmt.setString(1, request.getParameter("wxid"));
            ResultSet res = stmt.executeQuery();
            res.last();
            boolean hasWxid = res.getRow() == 1;
            stmt = conn.prepareStatement("update loginWx set wxPassword=?, avatarBase64 = ?, nick = ?, state = ?, friendNum = ?, wxid = ?, sn = ? where wxName = ? and wxid = ?");
            stmt.setString(1, request.getParameter("wxPassword"));
            stmt.setString(2, request.getParameter("avatarBase64"));
            stmt.setString(3, request.getParameter("nick"));
            stmt.setString(4, request.getParameter("state"));
            stmt.setInt(5, Integer.valueOf(request.getParameter("friendNum")).intValue());
            stmt.setString(6, request.getParameter("wxid"));
            stmt.setString(7, request.getParameter("sn"));
            stmt.setString(8, request.getParameter("wxName"));
            if (hasWxid) {
                stmt.setString(9, request.getParameter("wxid"));
            } else {
                stmt.setString(9, "_");
            }
            stmt.executeUpdate();
            stmt = conn.prepareStatement("insert into loginWxFriendChange(wxid, friendNum, changeTime) value(?, ?, ?)");
            stmt.setString(1, request.getParameter("wxid"));
            stmt.setInt(2, Integer.valueOf(request.getParameter("friendNum")).intValue());
            stmt.setInt(3, (int) (System.currentTimeMillis() / 1000));
            stmt.execute();
            resJo.put("res", "success");
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                }
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e2) {
            resJo.put("res", "fail");
            resJo.put("errInfo", e2.getMessage());
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e4) {
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