package com.kemalbeyaz.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kemalbeyaz.shared.JsonHelper;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String startTime;
    private String endTime;
    private long duration;

    public ResultData() {
    }

    public ResultData(final LocalDateTime start, final LocalDateTime end) {
        this.id = UUID.randomUUID().toString();
        this.startTime = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.endTime = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.duration = ChronoUnit.MILLIS.between(start, end);
    }

    public String getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return duration;
    }

    public String toJSON() {
        return JsonHelper.toJSON(this, ResultData.class);
    }

    public static ResultData fromJSON(final String jsonValue) {
        return JsonHelper.fromJSON(jsonValue, ResultData.class);
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration=" + duration + " ms";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultData that = (ResultData) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
