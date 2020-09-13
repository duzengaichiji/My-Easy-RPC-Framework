package loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import enumeration.LoadBalancerCode;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instanceList) {
        return instanceList.get(new Random().nextInt(instanceList.size()));
    }

    @Override
    public int getCode() {
        return LoadBalancerCode.RANDOMLOADBALANCER.getCode();
    }
}
