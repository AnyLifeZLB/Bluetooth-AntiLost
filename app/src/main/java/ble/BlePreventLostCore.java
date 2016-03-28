package ble;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @author anylife.zlb@gmail.com
 */
public class BlePreventLostCore {
	private static String TAG=BlePreventLostCore.class.getSimpleName();
	private final  static int leastNum=3; // 设备采集到的SSID[] size>3才是有意义的值
	private final  static double deviateBaseFlag=0.1; //
	private final  static double WCF=0.5;         //Weight Compensation Flag,权重补偿因子

	private static Map<String,List<Integer>> sacnedBleDevicesData;
	/**
	 * 去除采集到数据中的脏值（各位客官，自己根据自己产品的业务逻辑和实际情况，定义本算法）
	 * deviateBaseFlag 为基准的脏值偏移标志，如果长度比较大的话，可以适当的加大             
	 * 先简单的以算术平均数作为参照，大于average 的deviateBaseFlag*average 就判断是脏值。
	 * ---------------------------------------------------------------------------
	 * 
	 * 
	 * 然而 [-58, -31, -29, -30, -28]来看是行不通的，第一个值明显是不好的脏值。
	 * 
	 */
	public static void clearDigest() {
//		double deviateFlag=0.2**……&&N; //
		Map<String,Double> sAverageSSID=new HashMap<>();   //简单算术平均数
		Set<String> ks =sacnedBleDevicesData.keySet();
		
		//求对应手表的算术平均数。
		for(String key : ks ){
			double average=0.0;			
			List<Integer> ssidList=sacnedBleDevicesData.get(key);
			int size=sacnedBleDevicesData.get(key).size();
			for(int i=0;i<size;i++){
				average=average+ssidList.get(i);
			}   
			average=(double)average/size;
			sAverageSSID.put(key, average);  
		}
		
		//去除脏值。
		for(String key : ks ){
			List<Integer> ssidList=sacnedBleDevicesData.get(key);
			int size=sacnedBleDevicesData.get(key).size();
			//自己定义策略，尽量保留后半部分的值,只去除size/2+1的值
			if(size>leastNum){
				size=size/2+1;
				double averageTemp=sAverageSSID.get(key);
				while(--size!=-1){
					Log.e(TAG,key+"i: "+size);
					if(Math.abs(ssidList.get(size)-averageTemp) >  Math.abs(deviateBaseFlag*averageTemp)){
						Log.d(TAG,key+"移除 脏值索引 i: "+size);
						ssidList.remove(size);
					}
				} 
				sacnedBleDevicesData.put(key, ssidList);
			}
		}	
	}
	/**
	 * ssid 对用的权重分布[... , ...]
	 * 
	 * @return
	 */
	public static Map<String,List<Double>> getWeghtCompensationMap(){
		Map<String,List<Double>> weghtCompensationDatas =  new HashMap<String,List<Double>>(); //权数分布
//		Map<String,Double>  weghtCompensationDecreasing; //权重递减因子
		//1.初始化权数分布
		Set<String> ks =sacnedBleDevicesData.keySet();
		for(String key : ks ){
			final int size=sacnedBleDevicesData.get(key).size();
			List<Double> weghtCompDataList=new ArrayList<Double>(size-1);
			if(size>leastNum){
				//1.权重分布的前半部分初始化
				for(int i=0;i<size;i++){
	                double decreasing=WCF-WCF*2*(i+1)/size;
					if(decreasing>0&&i<size/2){
						weghtCompDataList.add(i, decreasing);
					}else{
						weghtCompDataList.add(i, (double) 0);
					}
				}
				
				//2.权重分布的后半部分初始化
				for(int j=size-1;j>size/2;j--){
					double temp=weghtCompDataList.get(size-j-1);
					weghtCompDataList.set(j,temp);
				}
	
				//3.权重分布的 实际处理
				for(int i=0;i<size/2;i++){
					weghtCompDataList.set(i, (1-weghtCompDataList.get(i))/size);
				}
				for(int j=size-1;j>size/2;j--){
					weghtCompDataList.set(j, (1+weghtCompDataList.get(j))/size);
				}
				
				//4.权重分布的 中位数实际处理
				if(size%2==0){
					weghtCompDataList.set(size/2,1.0/size);
				}else{
					weghtCompDataList.set(size/2, 1.0/size);
					weghtCompDataList.set(size/2-1, 1.0/size);
				}
				
				//5.权重分布测试，相加应该无限接近    100/100=1
				double test=0;
				for(int s=0;s<size;s++){
					test=test+weghtCompDataList.get(s);
				}
				Log.e(TAG,key+" test 和： "+test);

			}else{
				for(int k=0;k<size;k++){
					weghtCompDataList.add(k, 1.0/size);
				}
			}
			
			weghtCompensationDatas.put(key, weghtCompDataList);
		}//权重分布完成
		return weghtCompensationDatas;
	}
	
	
	/**
	 * 时间越后，权重越大
	 * 
	 * @param sacnedBleDD
	 * @return
	 */ 
	public static Map<String,Double> getDeviceState(final Map<String,List<Integer>> sacnedBleDD){
		sacnedBleDevicesData=sacnedBleDD;
		clearDigest(); //去除脏值
		
		Map<String,List<Double>> weightCompensation=getWeghtCompensationMap();      //权数分布因子 
		Log.e(TAG,"  "+weightCompensation);
		
		Map<String,Double> averageSSID=new HashMap<>();
		Set<String> ks =sacnedBleDevicesData.keySet();
		for(String key : ks ){
			double average=0.0;
			List<Double> weghtCompDataList=weightCompensation.get(key);
			List<Integer> ssidList=sacnedBleDevicesData.get(key);

			for(int i=0;i<weghtCompDataList.size();i++){
				average=average+weghtCompDataList.get(i)*ssidList.get(i);
			}   
			Log.d(TAG,key+" 加权算术平均："+average);
			averageSSID.put(key, average);  
			//bingo.
		}
		return averageSSID;
	}

}
