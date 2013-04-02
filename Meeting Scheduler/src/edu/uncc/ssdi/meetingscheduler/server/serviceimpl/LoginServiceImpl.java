package edu.uncc.ssdi.meetingscheduler.server.serviceimpl;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.uncc.ssdi.meetingscheduler.client.services.LoginService;
import edu.uncc.ssdi.meetingscheduler.server.db.QueryHelper;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	@Override
	public boolean verifyRegisteredUser(String username, String password) {
		return QueryHelper.doesRowExistAs("select * from test.test_tbl where id = 1 and name = 'Ankur'");
	}
}
