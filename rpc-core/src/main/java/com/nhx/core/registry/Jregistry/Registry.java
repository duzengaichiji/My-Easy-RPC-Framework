package com.nhx.core.registry.Jregistry;


public interface Registry {
    /**
         * @Author nhx
         * @Description 
         * @Date 19:51 2020/9/22
         * @Param [connectString: a list of servers like [host1:port1,host2:port2...]
         * @return void
         **/
    void connectToRegistryServer(String connectString);
}
