package api;

import java.io.Serializable;

//标准数据格式
public class HelloObject implements Serializable {
    private Integer id;
    private String message;

    public HelloObject() {
    }

    public HelloObject(Integer id, String message) {
        this.id = id;
        this.message = message;
    }

    @Override
    public String toString() {
        return "HelloObject{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
