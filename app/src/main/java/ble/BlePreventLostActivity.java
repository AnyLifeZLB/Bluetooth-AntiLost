package ble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.ble.antilost.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import main.BaseActivity;


/**
 * 
 * 
 * @author anylife.zlb@gmail.com
 */
@SuppressLint("NewApi")
public class BlePreventLostActivity extends BaseActivity implements OnClickListener{
	private String TAG=BlePreventLostActivity.class.getSimpleName();
	private TextView instruction_tips; 
	private ListView bleListView;
	
	//已经扫描出来的设备列表集Set,自定义obj是否重复
	private List<BleDevice> findedBleDevicesList = new ArrayList<BleDevice>();
	
	private List<BleDevice> sacnedBleDevicesList = new ArrayList<BleDevice>();

	//已经扫描出来的设备（MAC作为Key）数据集合（List<?>   ?需要是BleDevice？不如integer  ）
	private Map<String,List<Integer>> sacnedBleDevicesData = new HashMap<String,List<Integer>>();

    private BleDeviceListAdapter mDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mFindDevice=true;  //true : 刚刚进入蓝牙防丢的设备扫描阶段；    false：对扫描出来的设备进行蓝牙防丢
    private boolean mLiveDevice=false;  //true : 刚刚进入蓝牙防丢的设备扫描阶段；    false：对扫描出来的设备进行蓝牙防丢

    private Handler mHandler;
    private DeviceLiveThread deviceLiveThread;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000*10;       //点击开始扫描后的10秒停止扫描
    private static final long LIVE_PERIOD = 1000*7;       //点击开始扫描后的10秒停止扫描

    @Override
    public void onCreate(Bundle savedInstanceState) {    //
        super.onCreate(savedInstanceState);
		setContentView(R.layout.ble_prevent_lost);
		instruction_tips=(TextView) findViewById(R.id.instruction_tips);
		instruction_tips.setOnClickListener(this);
		bleListView=(ListView) findViewById(R.id.ble_device_list);
   
        // Initializes list view adapter.
        mDeviceListAdapter =new BleDeviceListAdapter(this,findedBleDevicesList);// new mDeviceListAdapter(this);
        bleListView.setAdapter(mDeviceListAdapter);

        mHandler = new Handler();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        
        //如果手机不支持蓝牙4.0，api>18. 那么直接退出
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
   
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        Log.e(TAG, " test" + BleDeviceState.UNKNOW.getStateName());
        
        findDevice(true);
            
    }
     
    
    /**
     * 扫描设备蓝牙设备
     * 
     * 
     * @param enable  
     *        true:扫描设备，并在SCAN_PERIOD后停止         
     *        false：不扫描，直接就停止了（在onPause中停止了）
     */
    private void findDevice(final boolean enable) {
        if (enable) {
        	
        	setInstructStytle(true);
            mFindDevice = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);   //开始扫描设备	
            mDeviceListAdapter.notifyDataSetChanged();

        	mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"\n\n设备个数："+findedBleDevicesList.size()+"\n设备信息 ：\n"+findedBleDevicesList.toString());
                	if(mFindDevice){
                		if(findedBleDevicesList!=null&&findedBleDevicesList.size()>0){
                			instruction_tips.setVisibility(View.VISIBLE);
//                			findedBleDevicesList -->  sacnedBleDevicesData
                			
                			for(int i=0;i<findedBleDevicesList.size();i++){
                				List<Integer> tempList=new ArrayList<Integer>();
                				tempList.add(findedBleDevicesList.get(i).getSsid());
                				sacnedBleDevicesData.put(findedBleDevicesList.get(i).getMacAddr(), tempList);
                			}
                			
                		}      		
                	}
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                	mFindDevice=false;
                    invalidateOptionsMenu();
                    
                }
            }, SCAN_PERIOD);

        } else {
        	setInstructStytle(false);
        	mFindDevice = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
          
      invalidateOptionsMenu();

    }
    
    /** 
     * Device scan callback.
     * 
     * if you call  [mBluetoothAdapter.startLeScan(mLeScanCallback);]this will call back.
     * 
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
					Log.e(TAG,"设备"+device.getName()+"    Rssi强度:"+rssi+" "+device.getAddress());
					BleDevice bleDevice=new BleDevice(0, device.getName(), device.getAddress(), "uuid", rssi, device.getBondState(), device.getType());
                    
					if(mFindDevice){ //处于第一次的发现设备阶段
						if(findedBleDevicesList!=null&&!findedBleDevicesList.contains(bleDevice)){
							findedBleDevicesList.add(bleDevice);
	                        mDeviceListAdapter.notifyDataSetChanged();
	                    }
					}else{
						   
						//
						if(sacnedBleDevicesList!=null&&!sacnedBleDevicesList.contains(bleDevice)){
	                    	sacnedBleDevicesList.add(bleDevice);
//	                        mDeviceListAdapter.notifyDataSetChanged();
	                    }
						
						//2. 向MAC 地址对应的List<Integer> 添加一个值。
	                    List<Integer> bleDevices=sacnedBleDevicesData.get(bleDevice.getMacAddr());
	                    if(null==bleDevices){
	                    	bleDevices=new ArrayList<Integer>();
	                    }
	                    bleDevices.add(rssi);
	                    sacnedBleDevicesData.put(bleDevice.getMacAddr(), bleDevices);	
					}
                  }
            });
        }
    };

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 设置一些样式
     * 
     * @param needReScan
     */
    private void setInstructStytle(boolean needReScan){
    	
    	if(needReScan){
        	instruction_tips.setVisibility(View.INVISIBLE);
        	mDeviceListAdapter.clear(); 
    	}else{//要那种慢慢显示出来的效果
        	instruction_tips.setVisibility(View.VISIBLE);
    	}
    	    
    }
    
    
    
    public class DeviceLiveThread extends Thread {
    	private boolean isRunning=true;
    	
    	private void stopThread(){
    		isRunning=false;
    	}
    	
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		while(isRunning){
    			if(mLiveDevice){
    				Log.d(TAG,"统计结果,采集数据为==================\n");
        			Set<String> keyset=sacnedBleDevicesData.keySet();
        			for(String key:keyset){
        				Log.e(TAG,"size="+sacnedBleDevicesData.get(key).size()+" "+sacnedBleDevicesData.get(key));
        			}
        			Log.d(TAG,"统计结束,清除本次统计*****************************************************\n");
        			
        			if(null!=sacnedBleDevicesData&&sacnedBleDevicesData.size()>0){
        				Map<String,Double> rssiValueList= BlePreventLostCore.getDeviceState(sacnedBleDevicesData); //根据换回的结果统计
        				displayLiveResult(rssiValueList);
        				rssiValueList.clear();
        		        sacnedBleDevicesData.clear();//一次分析完后，清除
        			} else{  //上一次分析完后再也没有采集到数据，全部丢失
        				displayLiveResult(null);
        			}

        			try {
        				Thread.sleep(LIVE_PERIOD);
        			} catch (InterruptedException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
    			}else{
        			try {
        				Thread.sleep(LIVE_PERIOD);
        			} catch (InterruptedException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
    			}

    		}	
    	}
    }
    
    
    
    /**
     * 开始进入
     * 
     */
    private void startDeviceLiving(){
    	if(deviceLiveThread==null){
        	deviceLiveThread=new DeviceLiveThread();
        	deviceLiveThread.start();
    	}
    }
    
    /**
     * 更新数据显示的结果
     * 
     * @param rssiValueList
     */
    private void displayLiveResult(Map<String,Double> rssiValueList){
    	
    	//0.处理异常的情况
    	if(null==rssiValueList||rssiValueList.size()==0){
        	int size=findedBleDevicesList.size();
        	for(int i=0;i<size;i++){
        		findedBleDevicesList.get(i).setSsid(0);
        	}
    	}else{
    		
        	//1.处理返回来的适配数据
        	Set<String> keySet=rssiValueList.keySet();
        	int size=findedBleDevicesList.size();
        	for(int i=0;i<size;i++){
        		String findedDeviceKey=findedBleDevicesList.get(i).getMacAddr(); //
        		if(keySet.contains(findedDeviceKey)){ //假如以前扫描到的设备在监听阶段还是存在，那么重新赋值RSSI,否则丢失了
        			findedBleDevicesList.get(i).setSsid(rssiValueList.get(findedDeviceKey).intValue());
        		}else{  //置设备状态为丢失,震动报警
        			findedBleDevicesList.get(i).setSsid(0);
        		}
        	}
    	}
    	
    	//2.更新显示数据。
        runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		        mDeviceListAdapter.notifyDataSetChanged();
			}
		});
    }
    
    
    
    /**
     * 停止
     * 
     */
    private void stopDeviceLiving(){
    	
    }
    
    
    
    
    //======================================================================================
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.instruction_tips:
			mLiveDevice=!mLiveDevice;
	        invalidateOptionsMenu();
	                                    
			if(mLiveDevice){
				instruction_tips.setText(R.string.device_live_stop);
	            mBluetoothAdapter.startLeScan(mLeScanCallback);   //开始扫描设备	
				startDeviceLiving();
			}else{
				instruction_tips.setText(R.string.device_live_start);
	            mBluetoothAdapter.stopLeScan(mLeScanCallback);   //开始扫描设备	
				stopDeviceLiving();
			}

			break;
		default:
			break;
		}
	}

      
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blemenu, menu);
        if(mLiveDevice){
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }else if (!mFindDevice) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }
             
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                findDevice(true);
                break;
            case R.id.menu_stop:
                findDevice(false);
                break;
        }
        return true;
    }

    
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if((System.currentTimeMillis()-exitTime) > 2000){
				Toast.makeText(getApplicationContext(), R.string.double_return_exit, Toast.LENGTH_SHORT).show();   
				exitTime = System.currentTimeMillis();
			}else{
				BlePreventLostActivity.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	   
	
	
    //========================Activity life cycle=======================================================
    
  	@Override
  	protected void onStart(){
  		super.onStart();		
  		Log.e(TAG,"*************onStart");
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }  
        }
  	}

  	@Override
  	protected void onRestart(){
  		super.onRestart();
  		Log.e(TAG,"*************onRestart");
  	}
	
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");

    }

  	@Override
  	protected void onStop() {
  		super.onStop();
  		Log.e(TAG,"*************onStop");
  	}

  	@Override
  	protected void onDestroy(){
  		super.onDestroy();
  		Log.e(TAG,"*************onDestroy");
    	if(deviceLiveThread!=null){
        	deviceLiveThread.stopThread();
    	}
  		mLiveDevice=false;
  		findDevice(false);
        sacnedBleDevicesList.clear();
        findedBleDevicesList.clear();
        sacnedBleDevicesData.clear();
        mDeviceListAdapter.notifyDataSetChanged();
        
  	}

  	
}
