package keti.soc.cnd.kdlna;

import org.teleal.cling.model.meta.Device;

public class DeviceDisplay {
    Device device;

    public DeviceDisplay(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDisplay that = (DeviceDisplay) o;
        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public String toString() {
        // Display a little star while the device is being loaded
        //rustamchange//original code for toString//return device.isFullyHydrated() ? device.getDisplayString() : device.getDisplayString() + " *";
    	
    	//rustamchange/// code from wireme
    	String display;

		if (device.getDetails().getFriendlyName() != null)
			display = device.getDetails().getFriendlyName();
		else
			display = device.getDisplayString();

		// Display a little star while the device is being loaded (see
		// performance optimization earlier)
		return device.isFullyHydrated() ? display : display + " *";
    }
}