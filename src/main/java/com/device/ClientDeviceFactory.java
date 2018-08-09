package com.device;

import com.device.model.ClientDevice;
import com.device.model.ClientDeviceImpl;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Component;

@Component
public class ClientDeviceFactory {

    public ClientDevice createClientDeviceFor(Device device) {
        return new ClientDeviceImpl(device);
    }

}
