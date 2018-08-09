package com.device.model;

import org.springframework.mobile.device.Device;

public class ClientDeviceImpl implements ClientDevice {

    private Device device;

    public ClientDeviceImpl(Device device) {
        this.device = device;
    }

    @Override
    public String getAudience() {
        if (device.isNormal()) {
            return Audience.WEB.value();
        }
        if (device.isTablet()) {
            return Audience.TABLET.value();
        }
        if (device.isMobile()) {
            return Audience.MOBILE.value();
        }
        return Audience.UNKNOWN.value();
    }

}
