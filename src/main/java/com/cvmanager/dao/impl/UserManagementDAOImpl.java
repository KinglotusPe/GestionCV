package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.*;
import java.sql.*;
import java.util.*;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.utils.ValidacionUtil;

public class UserManagementDAOImpl implements UserManagementDAO {
    public void update(int actor,int id,String role,String status,String first,String last,String company) throws Exception {
        try(Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int activeAdmins=0;String oldRole=null,oldStatus=null;
                // Serialize administrative changes so concurrent requests cannot remove the last administrator.
                try(PreparedStatement st=c.prepareStatement("SELECT user_id,role,status FROM usuarios ORDER BY user_id FOR UPDATE");ResultSet rs=st.executeQuery()) {
                    while(rs.next()) {
                        if("admin".equals(rs.getString("role"))&&"active".equals(rs.getString("status")))activeAdmins++;
                        if(rs.getInt("user_id")==id){oldRole=rs.getString("role");oldStatus=rs.getString("status");}
                    }
                }
                if(oldRole==null)throw new IllegalArgumentException("Usuario inexistente.");
                boolean removesAdmin="admin".equals(oldRole)&&"active".equals(oldStatus)&&(!role.equals("admin")||!status.equals("active"));
                if(removesAdmin&&(activeAdmins<=1||actor==id))throw new IllegalArgumentException("No puede retirar su propio acceso ni el último administrador activo.");
                if(role.equals("graduate")&&!exists(c,"egresados",id)) {
                    first=ValidacionUtil.text(first,"Nombres del egresado",80,true);last=ValidacionUtil.text(last,"Apellidos del egresado",100,true);
                    try(PreparedStatement st=c.prepareStatement("INSERT INTO egresados(user_id,first_name,last_name) VALUES(?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                        st.setInt(1,id);st.setString(2,first);st.setString(3,last);st.executeUpdate();
                        try(ResultSet rs=st.getGeneratedKeys()){rs.next();try(PreparedStatement cv=c.prepareStatement("INSERT INTO cv(graduate_id) VALUES(?)")){cv.setInt(1,rs.getInt(1));cv.executeUpdate();}}
                    }
                }
                if(role.equals("company")&&!exists(c,"empresas",id)) {
                    company=ValidacionUtil.text(company,"Nombre de empresa",150,true);
                    try(PreparedStatement st=c.prepareStatement("INSERT INTO empresas(user_id,company_name) VALUES(?,?)")){st.setInt(1,id);st.setString(2,company);st.executeUpdate();}
                }
                try(PreparedStatement st=c.prepareStatement("UPDATE usuarios SET role=?,status=? WHERE user_id=?")){st.setString(1,role);st.setString(2,status);st.setInt(3,id);st.executeUpdate();}
                c.commit();
            }catch(Exception e){c.rollback();throw e;}
        }
    }
    private boolean exists(Connection c,String table,int id)throws SQLException {
        try(PreparedStatement st=c.prepareStatement("SELECT 1 FROM "+table+" WHERE user_id=?")){st.setInt(1,id);try(ResultSet rs=st.executeQuery()){return rs.next();}}
    }
}
