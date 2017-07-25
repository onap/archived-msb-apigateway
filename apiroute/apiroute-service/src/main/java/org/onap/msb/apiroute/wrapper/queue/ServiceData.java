package org.onap.msb.apiroute.wrapper.queue;

public class ServiceData<T> {
	public static enum Type {
		consul
	};

	public static enum Operate {
		update, delete
	};

	public static enum DataType {
		service_list, service
	}

	private Type type = Type.consul;
	private DataType dataType;
	private T data;
	private Operate operate = Operate.update;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Operate getOperate() {
		return operate;
	}

	public void setOperate(Operate operate) {
		this.operate = operate;
	}

}
