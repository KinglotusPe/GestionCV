package pe.edu.hierropozo.cvmanager.service;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.dao.*;
import pe.edu.hierropozo.cvmanager.util.PasswordUtil;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkflowIntegrationTest {
    int admin,graduate,other,company,graduateId,cvId;
    String prefix="test-"+UUID.randomUUID();
    final UserDAOImpl users=new UserDAOImpl();
    @BeforeAll void seed()throws Exception {
        try(Connection c=DBConnection.getConnection()) {assertTrue(c.getCatalog().endsWith("_test"),"Integration writes require a database ending in _test");}
        RegistrationService registration=new RegistrationService();
        var testAdmin=new pe.edu.hierropozo.cvmanager.model.User();testAdmin.setEmail(prefix+"-admin@example.com");testAdmin.setPasswordHash(PasswordUtil.hash("TestPassword!123"));testAdmin.setRole("admin");testAdmin.setStatus("active");
        try(Connection c=DBConnection.getConnection()){users.create(c,testAdmin);}
        registration.register(prefix+"-graduate@example.com","TestPassword!123","graduate","María","Prueba",null);
        registration.register(prefix+"-other@example.com","TestPassword!123","graduate","Otro","Prueba",null);
        registration.register(prefix+"-company@example.com","TestPassword!123","company",null,null,"Empresa Prueba");
        admin=users.findByEmail(prefix+"-admin@example.com").getUserId();graduate=users.findByEmail(prefix+"-graduate@example.com").getUserId();other=users.findByEmail(prefix+"-other@example.com").getUserId();company=users.findByEmail(prefix+"-company@example.com").getUserId();
        users.updateStatus(graduate,"active");users.updateStatus(other,"active");users.updateStatus(company,"active");
        graduateId=new GraduateDAOImpl().findByUserId(graduate).getGraduateId();cvId=new CurriculumDAOImpl().findByGraduate(graduateId).getCvId();
    }
    @AfterAll void cleanup()throws Exception {
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement("DELETE FROM usuarios WHERE email LIKE ?")){st.setString(1,prefix+"%");st.executeUpdate();}
    }
    @Test void bootstrapCannotCreateSecondAdmin()throws Exception {
        assertThrows(IllegalArgumentException.class,()->new RegistrationService().createAdmin(prefix+"-second@example.com","TestPassword!123"));
    }
    @Test void administratorCannotDisableOwnAccount() {
        assertThrows(IllegalArgumentException.class,()->new UserManagementService().update(admin,admin,"admin","suspended",null,null,null));
    }
    @Test void changingRoleCreatesRequiredProfileAndPreservesPreviousOne()throws Exception {
        new UserManagementService().update(admin,other,"company","active",null,null,"Nueva Empresa");
        assertEquals("company",users.findById(other).getRole());
        assertNotNull(new GraduateDAOImpl().findByUserId(other));
        new UserManagementService().update(admin,other,"graduate","active",null,null,null);
        assertEquals("graduate",users.findById(other).getRole());
    }
    @Test void sectionUpdatesCannotModifyAnotherCv()throws Exception {
        CVSectionService service=new CVSectionService();
        service.save(cvId,"skill",null,Map.of("skillName","Java","category","technical","proficiencyLevel","3"));
        var row=new CurriculumDAOImpl().section("habilidades","cv_id",cvId).get(0);int item=((Number)row.get("skill_id")).intValue();
        int otherCv=new CurriculumDAOImpl().findByGraduate(new GraduateDAOImpl().findByUserId(other).getGraduateId()).getCvId();
        assertThrows(IllegalArgumentException.class,()->service.save(otherCv,"skill",item,Map.of("skillName","Changed","category","technical","proficiencyLevel","4")));
        service.save(cvId,"skill",item,Map.of("skillName","Java avanzado","category","technical","proficiencyLevel","5"));
        assertEquals("Java avanzado",new CurriculumDAOImpl().section("habilidades","cv_id",cvId).get(0).get("skill_name"));
    }
    @Test void contactDetailsRequireAcceptanceAndPrivateProfilesCannotBeContacted()throws Exception {
        CurriculumDAOImpl cvs=new CurriculumDAOImpl();SearchService search=new SearchService();cvs.setPublished(cvId,true);
        search.requestContact(company,graduateId,"Propuesta de entrevista");
        assertNull(search.getContactRequests(company).get(0).get("graduate_email"));
        execute("UPDATE solicitudes_contacto SET status='accepted' WHERE graduate_id=?",graduateId);
        assertEquals(prefix+"-graduate@example.com",search.getContactRequests(company).get(0).get("graduate_email"));
        execute("UPDATE egresados SET is_public=0 WHERE graduate_id=?",graduateId);
        assertNull(cvs.findPublished(cvId));
        assertThrows(IllegalArgumentException.class,()->search.requestContact(company,graduateId,"No autorizado"));
        execute("UPDATE egresados SET is_public=1 WHERE graduate_id=?",graduateId);
        assertFalse(search.search(null,null,null,null,null,1,10).isEmpty());
        cvs.setPublished(cvId,false);
    }
    @Test void resetTokenIsSingleUseAndChangesPassword()throws Exception {
        PasswordService service=new PasswordService();String token=service.issue(graduate);
        service.reset(token,"NewPassword!456");assertTrue(PasswordUtil.verify("NewPassword!456",users.findById(graduate).getPasswordHash()));
        assertThrows(IllegalArgumentException.class,()->service.reset(token,"AnotherPassword!123"));
    }
    @Test void applicationGrantsAndWithdrawRevokesAccess()throws Exception {
        JobService jobs=new JobService();jobs.save(company,null,Map.of("title","Técnico de pruebas","description","Funciones del puesto","city","Ayacucho","modality","onsite","status","open"));
        int job=((Number)jobs.list(company,true,null).get(0).get("job_id")).intValue();
        assertFalse(jobs.canReadCv(company,cvId));jobs.apply(graduate,job,"Mi presentación");assertTrue(jobs.canReadCv(company,cvId));
        assertThrows(SQLException.class,()->jobs.apply(graduate,job,"Duplicado"));
        int application=((Number)jobs.applications(graduate,false).get(0).get("application_id")).intValue();
        assertThrows(IllegalArgumentException.class,()->jobs.respond(other,false,application,"withdrawn"));
        jobs.respond(company,true,application,"reviewing");jobs.respond(graduate,false,application,"withdrawn");assertFalse(jobs.canReadCv(company,cvId));
        assertThrows(IllegalArgumentException.class,()->jobs.respond(company,true,application,"accepted"));
        execute("UPDATE vacantes SET status='closed' WHERE job_id=?",job);
        assertThrows(IllegalArgumentException.class,()->jobs.apply(other,job,"Postular cerrado"));
    }
    private void execute(String sql,int id)throws SQLException {
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql)){st.setInt(1,id);st.executeUpdate();}
    }
}
