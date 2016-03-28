package ble;
/**
 * 
 * 
 * @author Anylife.zlb@gmail.com
 *
 */
public enum BleDeviceState {
	
//	|-1.未知的状态      |0.不在搜索范围     |1.已经丢失    |2.即将丢失    |3.安全范围中 

	UNKNOW("unknow", -1), UNCARE("uncare", 0), LOSTED("losted", 1), LOSTING("losting", 2), SAFE("safe", 4);
	// 成员变量
	private String stateName;
	private int stateCode;
	// 构造方法
	private BleDeviceState(String stateName, int stateCode) {
		this.stateName = stateName;
		this.stateCode = stateCode;
	}
	// 普通方法
	public static String getName(int stateCode) {
		for (BleDeviceState c : BleDeviceState.values()) {
			if (c.getStateCode() == stateCode) {
				return c.stateName;
			}
		}
		return null;	     
	}
	
	
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	public int getStateCode() {
		return stateCode;
	}
	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}

}