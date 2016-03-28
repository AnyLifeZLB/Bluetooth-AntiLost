package ble;

/**
 * 
 * 
 * @author Anylife.zlb@gmail.com
 */
public class BleDevice {
	
	private int    id;        
	private String name;     //蓝牙的名称
	private String macAddr;  //蓝牙的地址，Mac地址
	private String uuid;     //蓝牙的uuid。
	private int    ssid;     //信号强度值。   ！！！！  应该是RSSI ---  SSID 是真蓝牙服务的id
	
	private int    state;    //蓝牙的使用状态。 |-1.未知的状态      |0.不在搜索范围     |1.已经丢失    |2.正在远离   |3.安全范围中 
	private int    mode;     //蓝牙的型号，不同厂商的芯片和sdk不一样
	

	public BleDevice(int id, String name, String macAddr, String uuid,
			int ssid, int state, int mode) {
		super();
		this.id = id;
		this.name = name;
		this.macAddr = macAddr;
		this.uuid = uuid;
		this.ssid = ssid;
		this.state = state;
		this.mode = mode;
	}

	public int getSsid() {
		return ssid;
	}

	public void setSsid(int ssid) {
		this.ssid = ssid;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMacAddr() {
		return macAddr;
	}

	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
	
	//http://zhangjunhd.blog.51cto.com/113473/71571/
	public int hashCode(){
		 //hashCode主要是用来提高hash系统的查询效率。当hashCode中不进行任何操作时，可以直接让其返回 一常数，或者不进行重写。
		return 17+ 9 * getMacAddr().hashCode();
    }
	
	public boolean equals(Object other){
		if(!(other instanceof BleDevice)) {
		    return false; 
		}
		
		final BleDevice bleDevice = (BleDevice)other;
		if(!getMacAddr().equals(bleDevice.getMacAddr())){
			return false;
		}

		return true;	
	}

	@Override
	public String toString() {
		return "BleDevice [id=" + id + ", name=" + name + ", macAddr="
				+ macAddr + ", uuid=" + uuid + ", ssid=" + ssid + ", state="
				+ state + ", mode=" + mode + "]";
	}



	
}
