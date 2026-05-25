package org.cboard.services;

import javax.servlet.http.HttpServletRequest;

import org.cboard.dao.UserDao;
import org.cboard.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public class CboardUserService implements AuthenticationService {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UserDao userDao;

	@Override
	public User getCurrentUser() {
		User curUser = (User) request.getSession().getAttribute("user");
		curUser.setUserPassword(null);
		return curUser;
	}

	@Override
	public User getUser(User param) {
		return loadUserByUsername(param);
	}

	@Override
	public void updatePwd(User user) {
		userDao.updatePwd(user);
	}

	public void updateLoginCnt(User user) {
		userDao.updateLoginCnt(user);
	}

	public User loadUserByUsername(User param) throws UsernameNotFoundException {
		try {
			User user = userDao.getUser(param);
			if (user != null) {
				user.setResTypeList(userDao.getResTypeList(user.getRoleId()));
			}
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
