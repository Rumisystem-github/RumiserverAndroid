package su.rumishistem.android.rumiserver.ForegroundService;

import android.os.Parcel;
import android.os.RemoteException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import su.rumishistem.android.rumiserver.IForegrondService;

public class IPCService extends IForegrondService.Stub {
	private static final List<String> AllowPackageName = new ArrayList<String>(){
		{
			add("su.rumishistem.android.rumiserver");
		}
	};

	private ForegroundService FS;

	public IPCService(ForegroundService Context) {
		this.FS = Context;
	}

	@Override
	public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
		String[] PackageList = FS.getPackageManager().getPackagesForUid(getCallingUid());
		if (PackageList != null) {
			for (String Package : PackageList) {
				if (AllowPackageName.contains(Package)) {
					return super.onTransact(code, data, reply, flags);
				}
			}
		}
		throw new SecurityException("Fuck");
	}

	@Override
	public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

	}

	@Override
	public String getSelf() {
		try {
			CountDownLatch CDL = new CountDownLatch(1);
			String[] Result = new String[]{""};

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Result[0] = new ObjectMapper().writeValueAsString(FS.getSession().get("ACCOUNT_DATA"));
					} catch (Exception EX) {
						EX.printStackTrace();
					} finally {
						CDL.countDown();
					}
				}
			}).start();

			CDL.await();

			return Result[0];
		} catch (Exception EX) {
			EX.printStackTrace();
			return "{}";
		}
	}

	@Override
	public String getToken() {
		return FS.getToken();
	}
}
