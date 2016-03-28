package main;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author  anylife.zlb@gmail.com
 * @time    2012-11-20
 *     
 */  
public class BipbipApplication extends Application{
    private String TAG=BipbipApplication.class.getSimpleName();
	private static BipbipApplication instance;
	private boolean isDebug=true;                  //是否是调试模式
	
	public static BipbipApplication getInstance() 
	{    
		return instance;
    } 
	
	//========================Application ==============================
	public void onCreate(){
		super.onCreate();
		Log.e("Application","Application onCreate !");
		instance = this;	
	}  
	

	                
	@Override
	public void onTerminate() {
		Log.e(TAG,"Application onTerminate !");

		// TODO Auto-generated method stub
		super.onTerminate();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isDebug(){
		return isDebug;
	}
	

	
	public String getSomeThing(){
		return "HelloWorld!";
	}
	
	public void updateSomeThing(){
		//Something 
	}
	

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo("com.devond.watch", 0);
			versionName = packageInfo.versionName;
			if (TextUtils.isEmpty(versionName)) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	
//	你终于明白，当初离开故乡的那一刻起就注定了你无法回头的青春；
//	你终于明白，旅行的意义就是遇见一些人，然后再与他们告别！
//	===================================================  张爱玲
	
}
