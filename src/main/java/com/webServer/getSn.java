package com.webServer;

import com.common.config;
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
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet({"/api/webServer/getSn"})
public class getSn extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        HttpSession session = request.getSession();
        if (session.getAttribute("loginPassword") == null || !session.getAttribute("loginPassword").equals(config.get("loginPassword"))) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "请先登录");
            pw.println(resJo);
            return;
        }
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from sn where sn like ? limit ?, ?");
            stmt.setString(1, "%" + request.getParameter("sn") + "%");
            stmt.setInt(2, (Integer.valueOf(request.getParameter("page"))-1)*10);
            stmt.setInt(3, Integer.valueOf(request.getParameter("pageSize")));
            ResultSet res = stmt.executeQuery();
            JSONArray ja = new JSONArray();
            while (res.next()) {
                JSONObject jo = new JSONObject();
                jo.put("idnpm start", res.getString("id"));
                jo.put("sn", res.getString("sn"));
                jo.put("remark", res.getString("remark"));
                jo.put("addTime", res.getString("addTime"));
                jo.put("jobName", res.getString("jobName"));
                jo.put("jobContent", res.getString("jobContent"));
                jo.put("stopContent", res.getString("stopContent"));
                jo.put("currentState", res.getString("currentState"));
                jo.put("lastHttpTime", res.getString("lastHttpTime"));
                jo.put("currentWx", res.getString("currentWx"));
                jo.put("goodWxNum", res.getString("goodWxNum"));
                jo.put("badWxNum", res.getString("badWxNum"));
                jo.put("appVer", res.getString("appVer"));
                ja.put(jo);
            }
            resJo.put("res", "success");
            resJo.put("data", ja);

            //查询总数
            stmt = conn.prepareStatement("select count(*) as total from sn where sn like ?");
            stmt.setString(1, "%" + request.getParameter("sn") + "%");
            res = stmt.executeQuery();
            if(res.next()) {
                resJo.put("total", res.getInt("total"));
            }else{
                resJo.put("total", 0);
            }
            res.close();
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
            e2.printStackTrace();
            resJo.put("errInfo", e2.getMessage());
            getServletContext().log("web getSn err: " + e2.getMessage());
            resJo.put("res", "fail");
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
        doPost(request, response);
    }
}