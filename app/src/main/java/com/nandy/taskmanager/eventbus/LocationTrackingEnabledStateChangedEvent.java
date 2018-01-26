package com.nandy.taskmanager.eventbus;

/**
 * Created by yana on 26.01.18.
 */

public class LocationTrackingEnabledStateChangedEvent {

    private boolean enabled;

    public LocationTrackingEnabledStateChangedEvent(boolean enabled){
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
