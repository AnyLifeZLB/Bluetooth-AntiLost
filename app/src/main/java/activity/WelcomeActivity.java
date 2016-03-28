package activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.ble.antilost.R;

/**
 * 
 * 
 * @author liubao.zeng
 *
 */
@SuppressLint("HandlerLeak")
public class WelcomeActivity extends Activity  {               
	private ImageView welcomeImg; 
	private String TAG=WelcomeActivity.class.getSimpleName();

	private Handler UiHandler = new Handler(){
	    public void handleMessage(Message msg) {
	    	switch (msg.what) {
			case 0:
				FinishWelcome();
				break;
			default:
				break;
			}
	    }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);    
        
        //不显示系统的标题栏          
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                              WindowManager.LayoutParams.FLAG_FULLSCREEN );
        
        setContentView(R.layout.activity_welcome);
        welcomeImg=(ImageView) findViewById(R.id.welcome); 
        
        Animation animation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.alpha_anim_on);		
        welcomeImg.startAnimation(animation);  
        
        DataInit();
        

        double i=1/11;
        double j=1.0/11;
        double k=(double)1/11;
        
			
	}
     
	
	/**
	 * 数据的初始化
	 * 
	 */
	private void DataInit(){
		
		UiHandler.sendEmptyMessageDelayed(0, 1500);
		
	
//        Animation finishAnl = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.alpha_anim_off);		
//        welcomeImg.startAnimation(finishAnl); 
//        finishAnl.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationStart(Animation animation) {
//				// TODO Auto-generated method stub
//				for(int i=0;i<1000;i++){
//					Log.e(TAG,"lallalala"+i);
//				}
//				
//				for(int i=0;i<100;i++){
//					Log.e(TAG,"datainit"+i);
//				}
//				
//			}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				// TODO Auto-generated method stub
//				for(int i=99990;i<100000;i++){
//					Log.e(TAG,"onAnimationEnd"+i);
//				}
//				FinishWelcome();
//			}
//		});
        
	}
	
	
	private void FinishWelcome(){
		   Intent intent=new Intent();
		   intent.setClass(this, LoginActivity.class);
		   startActivity(intent);
		   WelcomeActivity.this.finish();
	}

	
	
}
