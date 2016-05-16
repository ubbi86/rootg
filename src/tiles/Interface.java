package tiles;

import java.io.Serializable;

public class Interface implements Serializable {
	private IO io;
	private Device device;

	// CONSTRUCTORS
	public Interface(IO io, Device device) {
		this.io = io;
		this.device = device;
	}

	// GETTERS&SETTERS
	public IO getIo() {
		return io;
	}

	public Device getDevice() {
		return device;
	}

	public void setIo(IO io) {
		this.io = io;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	// METHODS
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interface other = (Interface) obj;
		if (io != IO.BOTH && other.io != IO.BOTH && io != other.io)
			return false;
		if (device != other.device)
			return false;
		return true;
	}

	public boolean isOutGate() {
		return ((io == IO.OUTPUT || this.io == IO.BOTH) && device != Device.CORE && device != Device.WIREA
				&& device != Device.WIREB && device != Device.WIREC && device != Device.WIREAOUTB
				&& device != Device.WIREBOUTC && device != Device.WIRECOUTA && device != Device.CONST0
				&& device != Device.CONST1);
	}

}
