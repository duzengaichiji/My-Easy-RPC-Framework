package loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public class RoundRobinLoadBalancer implements  LoadBalancer {

    private int index = 0;

    @Override
    public Instance select(List<Instance> instanceList) {
        if(index>=instanceList.size()){
            index%=instanceList.size();
        }
        return instanceList.get(index++);
    }
}
