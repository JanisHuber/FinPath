package ch.finpath.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Path("/test")
public class RestResource {

    // 1) Bevorzugt: direkte Injection des globalen JNDI-Namens
    @Resource(lookup = "jdbc/FinPathDS")
    private DataSource ds;

    private DataSource ensureDs() throws NamingException {
        if (ds != null) return ds;
        // 2) Fallback: programmatischer JNDI-Lookup (erst env, dann global)
        InitialContext ctx = new InitialContext();
        try {
            return (DataSource) ctx.lookup("java:comp/env/jdbc/FinPathDS");
        } catch (NamingException ignore) {
            // wenn kein web.xml-Env-Mapping existiert, globalen Namen versuchen
            return (DataSource) ctx.lookup("jdbc/FinPathDS");
        }
    }

    @GET
    @Path("/db")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> testDb() {
        Map<String, String> result = new HashMap<>();
        try {
            DataSource useDs = ensureDs();
            try (Connection con = useDs.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT now()")) {
                if (rs.next()) result.put("dbTime", rs.getString(1));
            }
        } catch (Exception e) {
            result.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return result;
    }
}