package ru.knowledgebase.usermodule;

import org.junit.Before;
import org.junit.Test;
import ru.knowledgebase.articlemodule.ArticleNotFoundException;
import ru.knowledgebase.dbmodule.DataCollector;
import ru.knowledgebase.ldapmodule.LdapController;
import ru.knowledgebase.modelsmodule.Article;
import ru.knowledgebase.modelsmodule.rolemodels.ArticleRole;
import ru.knowledgebase.modelsmodule.rolemodels.GlobalRole;
import ru.knowledgebase.modelsmodule.rolemodels.UserArticleRole;
import ru.knowledgebase.modelsmodule.rolemodels.UserGlobalRole;
import ru.knowledgebase.modelsmodule.usermodels.User;
import ru.knowledgebase.rolemodule.ArticleRoleController;
import ru.knowledgebase.rolemodule.GlobalRoleController;
import ru.knowledgebase.rolemodule.exceptions.RoleAlreadyExistsException;
import ru.knowledgebase.rolemodule.exceptions.RoleNotFoundException;
import ru.knowledgebase.usermodule.exceptions.UserNotFoundException;

import static org.junit.Assert.assertTrue;

/**
 * Created by vova on 25.08.16.
 */
public class GlobalRoleControllerTest {
    private final String login1 = "testlogin1";

    private final String password1 = "testpassword1";

    private final int articleId = 1;
    private final String articleName = "testarticle";

    private final String role1Name = "testrole1";
    private final String role2Name = "testrole2";

    private final int roleId = 1;
    private final String roleName = "User";

    private DataCollector collector = new DataCollector();
    private LdapController ldapController = LdapController.getInstance();

    @Before
    public void prepareUser() throws Exception{
        User user = collector.findUser(login1);
        if (user == null) {
            user = new User();
            user.setLogin(login1);
            user.setPassword(password1);
            collector.addUser(user);
        }
        if (!ldapController.isUserExists(login1))
            ldapController.createUser(login1, password1, "User");
    }

    @Before
    public void prepareGlobalRole() throws Exception{
        GlobalRole role = collector.findGlobalRole(roleId);
        if (role == null){
            role = new GlobalRole(roleId);
            role.setName(roleName);
            collector.addGlobalRole(role);
        }
    }

    @Before
    public void prepareArticleRole() throws Exception{
        ArticleRole role = collector.findArticleRole(roleId);
        if (role == null){
            role = new ArticleRole(roleId);
            role.setName(roleName);
            collector.addArticleRole(role);
        }
    }

    @Before
    public void prepareRoles() throws Exception{
        GlobalRole role = collector.findGlobalRole(role1Name);
        if (role != null){
            collector.deleteGlobalRole(role);
        }
        role = collector.findGlobalRole(role2Name);
        if (role != null){
            collector.deleteGlobalRole(role);
        }
    }

    @Before
    public void prepareArticle() throws Exception{
        Article article = collector.findArticle(articleId);
        if (article == null) {
            article = new Article(articleId);
            article.setTitle(articleName);
            collector.addArticle(article);
        }
    }

    @Test
    public void create1() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        assertTrue(collector.findGlobalRole(role1Name) != null);
    }

    @Test(expected = RoleAlreadyExistsException.class)
    public void create2() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        GlobalRoleController.create(role);
    }

    @Test
    public void update1() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        role = collector.findGlobalRole(role1Name);
        role.setName(role2Name);
        GlobalRoleController.update(role);
        assertTrue(collector.findGlobalRole(role1Name) == null);
        assertTrue(collector.findGlobalRole(role2Name) != null);
    }

    @Test
    public void delete1() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.delete(role);
        assertTrue(collector.findGlobalRole(role1Name) == null);
    }

    @Test
    public void delete2() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.delete(role.getId());
        assertTrue(collector.findGlobalRole(role1Name) == null);
    }

    @Test
    public void delete3() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        UserGlobalRole userGlobalRole = new UserGlobalRole(user, role);
        GlobalRoleController.assignUserRole(userGlobalRole);
        collector.deleteGlobalRole(role);
        assertTrue(collector.findUser(login1) != null);
        assertTrue(collector.findGlobalRole(role1Name) == null);
    }

    @Test
    public void findUserRole1() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.assignUserRole(user, role);
        assertTrue(GlobalRoleController.findUserRole(user).getName().equals(role1Name));
    }

    @Test
    public void findUserRole2() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.assignUserRole(user, role);
        assertTrue(GlobalRoleController.findUserRole(user.getId()).getName().equals(role1Name));
    }

    @Test(expected = UserNotFoundException.class)
    public void findUserRole3() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.assignUserRole(user, role);
        assertTrue(GlobalRoleController.findUserRole(10000).getName().equals(role1Name));
    }

    @Test
    public void assignUserRole1() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.assignUserRole(user, role);
        assertTrue(collector.findUserGlobalRole(user) != null);
    }

    @Test
    public void assignUserRole2() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.assignUserRole(user.getId(), role.getId());
        assertTrue(collector.findUserGlobalRole(user) != null);
    }

    @Test
    public void assignUserRole3() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.assignUserRole(new UserGlobalRole(user, role));
        assertTrue(collector.findUserGlobalRole(user) != null);
    }

    @Test(expected = UserNotFoundException.class)
    public void assignUserRole4() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.assignUserRole(10000, role.getId());
        assertTrue(collector.findUserGlobalRole(user) != null);
    }

    @Test(expected = RoleNotFoundException.class)
    public void assignUserRole6() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        GlobalRoleController.assignUserRole(user.getId(), 10000);
        assertTrue(collector.findUserGlobalRole(user) != null);
    }

    @Test
    public void deleteUserRole1() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        collector.addUserGlobalRole(new UserGlobalRole(user, role));
        GlobalRoleController.deleteUserRole(user, role);
        assertTrue(collector.findUser(login1) != null);
        assertTrue(collector.findGlobalRole(role1Name) != null);
    }

    @Test
    public void deleteUserRole2() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        collector.addUserGlobalRole(new UserGlobalRole(user, role));
        GlobalRoleController.deleteUserRole(user.getId(), role.getId());
        assertTrue(collector.findUser(login1) != null);
        assertTrue(collector.findGlobalRole(role1Name) != null);
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUserRole3() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        collector.addUserGlobalRole(new UserGlobalRole(user, role));
        GlobalRoleController.deleteUserRole(10000, role.getId());
    }

    @Test(expected = RoleNotFoundException.class)
    public void deleteUserRole5() throws Exception{
        GlobalRole role = new GlobalRole(role1Name);
        GlobalRoleController.create(role);
        User user = collector.findUser(login1);
        role = collector.findGlobalRole(role1Name);
        collector.addUserGlobalRole(new UserGlobalRole(user, role));
        GlobalRoleController.deleteUserRole(user.getId(), 10000);
    }


}
