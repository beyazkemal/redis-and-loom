package com.kemalbeyaz.manager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kemalbeyaz.shared.JsonHelper;
import com.kemalbeyaz.shared.dto.ServiceType;
import com.kemalbeyaz.shared.dto.TaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Arguments extends TaskData {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(Arguments.class);

    private int serviceCount;
    private String serviceName;

    public Arguments() {
    }

    public Arguments(final String[] args) {
        this.serviceCount = getIntValue(args, 0);
        this.serviceName = getStringValue(args, 1);
        setServiceType(getServiceTypeValue(args));
        setConnectionCount(getIntValue(args, 3));
        setLoopCount(getIntValue(args, 4));
    }

    @JsonIgnore
    public int getServiceCount() {
        return serviceCount;
    }

    @JsonIgnore
    public String getServiceName() {
        return serviceName;
    }

    public String toJSON() {
        return JsonHelper.toJSON(this, Arguments.class);
    }

    public static Arguments fromJSON(final String jsonValue) {
        return JsonHelper.fromJSON(jsonValue, Arguments.class);
    }

    public TaskData toTaskData() {
        return new TaskData(getServiceType(), getConnectionCount(), getLoopCount());
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "serviceCount=" + serviceCount +
                ", serviceName='" + serviceName + '\'' +
                ", serviceType=" + getServiceType() +
                ", connectionCount=" + getConnectionCount() +
                ", loopCount=" + getLoopCount() +
                '}';
    }

    private static String getStringValue(final String[] args, final int index) {
        String arg = "";

        try {
            arg = args[index];
        } catch (IndexOutOfBoundsException e) {
            throwExceptionForUndefinedArg(index);
        }

        if (arg.isEmpty()) {
            throwExceptionForUndefinedArg(index);
        }

        return arg.trim();
    }

    private static int getIntValue(final String[] args, final int index) {
        String stringValue = getStringValue(args, index);

        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException n) {
            throwExceptionForUndefinedArg(index);
            return 0;
        }
    }

    private static ServiceType getServiceTypeValue(final String[] args) {
        String stringValue = getStringValue(args, 2);
        return ServiceType.fromName(stringValue);
    }

    private static void throwExceptionForUndefinedArg(final int index) {
        LOG.warn("{}. argüman verilmediği için işlem yürütülemedi.", index);
        throw new IllegalArgumentException();
    }
}
