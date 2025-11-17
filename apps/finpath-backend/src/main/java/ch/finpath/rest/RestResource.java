package ch.finpath.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Path("/db")
public class RestResource {

    @Resource(lookup = "jdbc/FinPathDS")
    private DataSource ds;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> testDb() {
        Map<String, String> result = new HashMap<>();

        if (ds == null) {
            result.put("error", "DataSource injection failed (ds is null)");
            return result;
        }

        try (Connection con = ds.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT now()")) {
            if (rs.next()) {
                result.put("dbTime", rs.getString(1));
            }
        } catch (Exception e) {
            result.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        return result;
    }
}