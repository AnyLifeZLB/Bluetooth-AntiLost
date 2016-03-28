package activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ble.antilost.R;
import main.BaseActivity;

/**
 *   
 *  @author liubao.zeng
 */   
public class LoginActivity extends BaseActivity implements OnClickListener,OnLongClickListener {
	// 声明控件对象
	private EditText et_name, et_pass;
	private Button mLoginButton,mLoginError,mRegister,ONLYTEST;  
	int selectIndex=1;
	int tempSelect=selectIndex;
	boolean isReLogin=false;
	private int SERVER_FLAG=0;

	private RelativeLayout countryselect;
	private TextView   coutry_phone_sn, coutryName;           //
//	private String [] coutry_phone_sn_array,coutry_name_array;

	public final static int LOGIN_ENABLE=0x01;    //注册完毕了
	public final static int LOGIN_UNABLE=0x02;    //
	public final static int PASS_ERROR=0x03;      //
	public final static int NAME_ERROR=0x04;      //


	final Handler UiMangerHandler = new Handler(){   
		@Override    
		public void handleMessage(Message msg) {  
			// TODO Auto-generated method stub
			switch(msg.what){	 
			case LOGIN_ENABLE:  
				mLoginButton.setClickable(true);            
//				mLoginButton.setText(R.string.login);
				break;
			case LOGIN_UNABLE:
				mLoginButton.setClickable(false);
				break;
			case PASS_ERROR:
				
				break;
			case NAME_ERROR:

				break;
			}     
			super.handleMessage(msg);
		}	  
	};
	private Button bt_username_clear;
	private Button bt_pwd_clear;
	private Button bt_pwd_eye;
	private TextWatcher username_watcher;       
	private TextWatcher password_watcher;       

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);	
//		requestWindowFeature(Window.FEATURE_NO_TITLE);       
//		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN );

		setContentView(R.layout.activity_login);

		et_name = (EditText) findViewById(R.id.username);
		et_pass = (EditText) findViewById(R.id.password);
		
		bt_username_clear = (Button)findViewById(R.id.bt_username_clear);
		bt_pwd_clear = (Button)findViewById(R.id.bt_pwd_clear);
		bt_pwd_eye = (Button)findViewById(R.id.bt_pwd_eye);
		bt_username_clear.setOnClickListener(this);
		bt_pwd_clear.setOnClickListener(this);
		bt_pwd_eye.setOnClickListener(this);
		initWatcher();
		et_name.addTextChangedListener(username_watcher);
		et_pass.addTextChangedListener(password_watcher);
		

		mLoginButton = (Button) findViewById(R.id.login);
		mLoginError  = (Button) findViewById(R.id.login_error);
		mRegister    = (Button) findViewById(R.id.register);

		ONLYTEST     = (Button) findViewById(R.id.registfer);
		ONLYTEST.setOnClickListener(this);       

		ONLYTEST.setOnLongClickListener((OnLongClickListener) this);
		mLoginButton.setOnClickListener(this);       
		mLoginError.setOnClickListener(this);       
		mRegister.setOnClickListener(this);  
		
//		countryselect=(RelativeLayout) findViewById(R.id.countryselect_layout);
//		countryselect.setOnClickListener(this);
//		coutry_phone_sn=(TextView) findViewById(R.id.contry_sn);
//		coutryName=(TextView) findViewById(R.id.country_name);
 
//		coutryName.setText(coutry_name_array[selectIndex]);    //默认为1
//		coutry_phone_sn.setText("+"+coutry_phone_sn_array[selectIndex]);
	}

	/**
	 * 手机号，密码输入控件公用这一个watcher
	 */
	private void initWatcher() {
		username_watcher = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			public void afterTextChanged(Editable s) {
				et_pass.setText("");
				if(s.toString().length()>0){
					bt_username_clear.setVisibility(View.VISIBLE);
				}else{
					bt_username_clear.setVisibility(View.INVISIBLE);
				}
			}
		};
		
		password_watcher = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			public void afterTextChanged(Editable s) {
				if(s.toString().length()>0){
					bt_pwd_clear.setVisibility(View.VISIBLE);
				}else{
					bt_pwd_clear.setVisibility(View.INVISIBLE);
				}
			}
		};
	}
  
     

	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.login:  //登陆
			login(); 
//			startActivity(MainActivity.class);
			break;
		case R.id.login_error: //无法登陆(忘记密码了吧)
//			Intent login_error_intent=new Intent();
//			login_error_intent.setClass(LoginActivity.this, ForgetCodeActivity.class);
//			startActivity(login_error_intent);
			break;

		case R.id.register:    //注册新的用户
//			Intent intent=new Intent();
//			intent.setClass(LoginActivity.this, ValidatePhoneNumActivity.class);
//			startActivity(intent);
			break;


		case R.id.registfer:

			if(SERVER_FLAG>10){
				Toast.makeText(this, "[内部测试--谨慎操作]", Toast.LENGTH_SHORT).show();
			}
			SERVER_FLAG++;

			break;

		case R.id.bt_username_clear:
			et_name.setText("");
			et_pass.setText("");
			break;
		case R.id.bt_pwd_clear:
			et_pass.setText("");
			break;
		case R.id.bt_pwd_eye:
			if(et_pass.getInputType() == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
				bt_pwd_eye.setBackgroundResource(R.drawable.button_eye_s);
				et_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
			}else{
				bt_pwd_eye.setBackgroundResource(R.drawable.button_eye_n);
				et_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			et_pass.setSelection(et_pass.getText().toString().length());
			break;
		}
	}

	/**
	 * 登陆
	 */
	private void login() {

		this.finish();
		
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.registfer:
			if(SERVER_FLAG>9){
				
			}
			//			SERVER_FLAG++;
			break;
		}

		return true;
	}




	/**
	 * 监听Back键按下事件,方法2:
	 * 注意:
	 * 返回值表示:是否能完全处理该事件
	 * 在此处返回false,所以会继续传播该事件.
	 * 在具体项目中此处的返回值视情况而定.
	 */ 
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if ((keyCode == KeyEvent.KEYCODE_BACK)) { 

			if(isReLogin){
				Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
				mHomeIntent.addCategory(Intent.CATEGORY_HOME);
				mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				LoginActivity.this.startActivity(mHomeIntent);
			}else{
				LoginActivity.this.finish();
			}

			return false; 
		}else { 
			return super.onKeyDown(keyCode, event); 
		} 
	}


}
