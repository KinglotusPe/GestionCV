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
    @Test void futureAndArchivedVacanciesRejectApplications()throws Exception {
        var portal=new LaborPortalService();var dao=new JobDAO();
        var p=new HashMap<>(Map.of("title","Programada","description","Funciones","city","Ayacucho","modality","onsite","status","open","openingDate",java.time.LocalDate.now().plusDays(2).toString()));
        portal.save(company,"company",null,p,null);
        int job=((Number)dao.list(company,"company","Programada",null,null).get(0).get("job_id")).intValue();
        assertEquals("upcoming",dao.find(company,"company",job).get("stage"));
        assertThrows(IllegalArgumentException.class,()->new JobService().apply(graduate,job,"Postulación adelantada"));
        p.remove("openingDate");portal.save(company,"company",job,p,null);
        portal.archive(company,"company",job,true);
        assertThrows(IllegalArgumentException.class,()->new JobService().apply(graduate,job,"Postulación archivada"));
        assertThrows(IllegalArgumentException.class,()->dao.find(graduate,"graduate",job));
        assertEquals(dao.list(graduate,"graduate",null,"ongoing",null).size(),new AdminService().stats().get("jobs"));
        portal.archive(company,"company",job,false);assertEquals("ongoing",dao.find(graduate,"graduate",job).get("stage"));
    }
    @Test void multipleCareersSaveAtomicallyAndOwnershipIsEnforced()throws Exception {
        var portal=new LaborPortalService();var dao=new JobDAO();
        var p=Map.of("title","Multicarrera","description","Funciones","city","Ayacucho","modality","onsite","status","open");
        portal.save(company,"company",null,p,new String[]{"1","2"});
        int job=((Number)dao.list(company,"company","Multicarrera",null,null).get(0).get("job_id")).intValue();
        assertEquals(2,((List<?>)dao.find(company,"company",job).get("careerIds")).size());
        assertThrows(IllegalArgumentException.class,()->portal.save(company,"company",job,p,new String[]{"2147483647"}));
        assertEquals(2,((List<?>)dao.find(company,"company",job).get("careerIds")).size());
        assertThrows(IllegalArgumentException.class,()->portal.save(other,"company",job,p,null));
        assertThrows(IllegalArgumentException.class,()->portal.save(admin,"admin",job,p,null));
        portal.archive(admin,"admin",job,true);
    }
    @Test void documentDownloadChecksVacancyAccess()throws Exception {
        var portal=new LaborPortalService();var dao=new JobDAO();
        portal.save(company,"company",null,Map.of("title","Documentada","description","Funciones","city","Ayacucho","modality","onsite","status","open"),null);
        int job=((Number)dao.list(company,"company","Documentada",null,null).get(0).get("job_id")).intValue();
        byte[] pdf="%PDF-1.4\n%%EOF".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        try {
            assertThrows(IllegalArgumentException.class,()->portal.addDocument(company,"company",job,"Falso","<html>".getBytes()));
            portal.addDocument(company,"company",job,"Bases",pdf);
            int doc=((Number)portal.documents(company,"company",job).get(0).get("document_id")).intValue();
            assertNotNull(portal.download(graduate,"graduate",doc));
            assertThrows(IllegalArgumentException.class,()->portal.download(other,"company",doc));
            portal.archive(company,"company",job,true);
            assertThrows(IllegalArgumentException.class,()->portal.download(graduate,"graduate",doc));
            portal.removeDocument(company,"company",job,doc);
            assertTrue(portal.documents(company,"company",job).isEmpty());
        }finally {for(var doc:portal.documents(company,"company",job))portal.removeDocument(company,"company",job,((Number)doc.get("document_id")).intValue());}
    }
    @Test void companyDecisionRequiresReasonAndRecordsHistory()throws Exception {
        var portal=new LaborPortalService();int id=((Number)JobDAO.rows("SELECT company_id FROM empresas WHERE user_id=?",company).get(0).get("company_id")).intValue();
        assertThrows(IllegalArgumentException.class,()->portal.review(admin,id,"active",""));
        portal.review(admin,id,"suspended","Revisión pendiente");assertEquals("suspended",users.findById(company).getStatus());
        portal.review(admin,id,"active","Documentación validada");assertEquals("active",users.findById(company).getStatus());
        assertEquals(2,JobDAO.rows("SELECT review_id FROM empresa_revisiones WHERE company_id=?",id).size());
    }
    @Test void institutionalVacancyHasIndependentEditorAndExpires()throws Exception {
        var portal=new LaborPortalService();var dao=new JobDAO();
        int companyId=((Number)JobDAO.rows("SELECT company_id FROM empresas WHERE user_id=?",company).get(0).get("company_id")).intValue();
        var p=new HashMap<>(Map.of("title","Institucional","description","Funciones","city","Ayacucho","modality","onsite","status","open","companyId",String.valueOf(companyId),"closingDate",java.time.LocalDate.now().minusDays(1).toString()));
        portal.save(admin,"admin",null,p,null);
        int job=((Number)dao.list(admin,"admin","Institucional",null,null).get(0).get("job_id")).intValue();
        assertEquals("finished",dao.find(admin,"admin",job).get("stage"));
        assertThrows(IllegalArgumentException.class,()->portal.save(company,"company",job,p,null));
        assertThrows(IllegalArgumentException.class,()->new JobService().apply(graduate,job,"Fuera de plazo"));
        p.put("closingDate",java.time.LocalDate.now().plusDays(1).toString());portal.save(admin,"admin",job,p,null);
        assertEquals("ongoing",dao.find(admin,"admin",job).get("stage"));
    }
    @Test void reviewRollsBackStateWhenHistoryInsertFails()throws Exception {
        var portal=new LaborPortalService();int id=((Number)JobDAO.rows("SELECT company_id FROM empresas WHERE user_id=?",company).get(0).get("company_id")).intValue();
        String before=users.findById(company).getStatus();
        int count=JobDAO.rows("SELECT review_id FROM empresa_revisiones WHERE company_id=?",id).size();
        // Actor inexistente provoca un fallo FK en el segundo INSERT, después del UPDATE.
        assertThrows(SQLException.class,()->portal.review(Integer.MAX_VALUE,id,"suspended","Prueba controlada de rollback"));
        assertEquals(before,users.findById(company).getStatus());
        assertEquals(count,JobDAO.rows("SELECT review_id FROM empresa_revisiones WHERE company_id=?",id).size());
    }
    @Test void reportFiltersDatesAndRejectsUnknownTypes()throws Exception {
        var portal=new LaborPortalService();
        for(String kind:List.of("jobs","companies","careers","applicants"))assertNotNull(portal.report(kind,"2000-01-01","2099-12-31"));
        assertTrue(portal.report("jobs","1900-01-01","1900-01-02").isEmpty());
        assertThrows(IllegalArgumentException.class,()->portal.report("sql","2000-01-01","2099-12-31"));
    }
    private void execute(String sql,int id)throws SQLException {
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql)){st.setInt(1,id);st.executeUpdate();}
    }
}
