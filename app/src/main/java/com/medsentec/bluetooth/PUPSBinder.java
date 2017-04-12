package com.medsentec.bluetooth;

import android.os.Binder;

/**
 * A class to get the bound service from the binder
 */
public class PUPSBinder extends Binder {

    private final com.medsentec.bluetooth.PUPSGattService PUPSGattService;

    /**
     * Creates a new instance of {@link PUPSBinder}.
     * @param PUPSGattService the {@link com.medsentec.bluetooth.PUPSGattService} to bind to.
     */
    public PUPSBinder(com.medsentec.bluetooth.PUPSGattService PUPSGattService) {
        this.PUPSGattService = PUPSGattService;
    }

    /**
     * Gets the service from the binder
     * @return the bound service
     */
    public com.medsentec.bluetooth.PUPSGattService getService() {
        return this.PUPSGattService;
    }
}
