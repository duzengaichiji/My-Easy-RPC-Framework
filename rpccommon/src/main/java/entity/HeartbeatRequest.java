package entity;

import java.io.Serializable;

public class HeartbeatRequest implements Serializable,Request {
    private String requestId;

    @Override
    public String toString() {
        return "HeartbeatRequest{" +
                "requestId='" + requestId + '\'' +
                '}';
    }

    public HeartbeatRequest(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}