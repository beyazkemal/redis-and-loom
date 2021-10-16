package com.kemalbeyaz.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kemalbeyaz.shared.JsonHelper;

import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ServiceType serviceType;
    private int connectionCount;
    private int loopCount;

    public TaskData() {
    }

    public TaskData(ServiceType serviceType, int connectionCount, int loopCount) {
        this.serviceType = serviceType;
        this.connectionCount = connectionCount;
        this.loopCount = loopCount;
    }

    public String toJSON() {
        return JsonHelper.toJSON(this, TaskData.class);
    }

    public static TaskData fromJSON(final String jsonValue) {
        return JsonHelper.fromJSON(jsonValue, TaskData.class);
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public int getConnectionCount() {
        return connectionCount;
    }

    public void setConnectionCount(int connectionCount) {
        this.connectionCount = connectionCount;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    @Override
    public String toString() {
        return "serviceType=" + serviceType +
                ", connectionCount=" + connectionCount +
                ", loopCount=" + loopCount;
    }
}
