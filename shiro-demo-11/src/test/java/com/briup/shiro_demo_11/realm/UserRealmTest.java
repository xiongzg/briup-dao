package com.briup.shiro_demo_11.realm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.junit.Test;

import com.briup.shiro_demo_11.BaseTest;
import com.github.zhangkaitao.shiro.chapter11.realm.UserRealm;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */
public class UserRealmTest extends BaseTest {

    @Override
    public void tearDown() throws Exception {
        userService.changePassword(u1.getId(), password);
        RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        UserRealm userRealm = (UserRealm) securityManager.getRealms().iterator().next();
        userRealm.clearCachedAuthenticationInfo(subject().getPrincipals());

        super.tearDown();
    }
 
    @Test
      public void testClearCachedAuthenticationInfo() {
    	try {
			String username = "zhang";
			
			login(username, password);
			System.out.println("登入成功");
			userService.changePassword(u1.getId(), password + "1");
			System.out.println("修改密码成功");
			//获得安全管理器
			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
			//获得当前项目中的realm 按照顺序， 所以获取了第一个
			UserRealm userRealm = (UserRealm) securityManager.getRealms().iterator().next();
			System.out.println("用户的realm : "+userRealm);
			userRealm.clearCachedAuthenticationInfo(subject().getPrincipals());//清除缓存中当前用户名对应的信息
			
			login(username, password + "1");
			System.out.println("修改密码以后登入成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Test
    public void testClearCachedAuthorizationInfo() {
        login(u1.getUsername(), password);
        subject().checkRole(r1.getRole());
        userService.correlationRoles(u1.getId(), r2.getId());

        RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        UserRealm userRealm = (UserRealm) securityManager.getRealms().iterator().next();
        userRealm.clearCachedAuthorizationInfo(subject().getPrincipals());

        subject().checkRole(r2.getRole());
    }



    @Test
    public void testClearCache() {
        login(u1.getUsername(), password);
        subject().checkRole(r1.getRole());

        userService.changePassword(u1.getId(), password + "1");
        userService.correlationRoles(u1.getId(), r2.getId());

        RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        UserRealm userRealm = (UserRealm) securityManager.getRealms().iterator().next();
        userRealm.clearCache(subject().getPrincipals());

        login(u1.getUsername(), password + "1");
        subject().checkRole(r2.getRole());
    }

}
