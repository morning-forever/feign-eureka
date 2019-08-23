package demo;

import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableFeignClients
public class HelloClientApplication {

	@Autowired
	MyFeignClient myFeignClient;

	@Autowired
	HelloClient client;

	@Autowired
	private ApplicationContext ctx;

	@RequestMapping("/")
	public String hello() {
        ribbonService(ctx);
		String s = myFeignClient.helloFeign();
		System.out.println("********"+s+"***********");


		return client.hello();
	}

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(HelloClientApplication.class, args);

	}

	@FeignClient("HelloServer")
	interface HelloClient {
		@RequestMapping(value = "/", method = GET)
		String hello();
	}

	private static void ribbonService(ApplicationContext ctx){
        SpringClientFactory springClientFactory = ctx.getBean(SpringClientFactory.class);
        ILoadBalancer loadBalancer = springClientFactory.getLoadBalancer("HelloServer");
        springClientFactory.getLoadBalancerContext("HelloServer").getLoadBalancer().addServers();

        List<Server> servers = loadBalancer.getReachableServers();
        for(Server server:servers){
            //如果服务有设置zone，此处获取的可能并不是所有的实例
            System.out.println("---:"+server.getHostPort());
        }

        DynamicServerListLoadBalancer<DiscoveryEnabledServer> dynamicServerListLoadBalancer = (DynamicServerListLoadBalancer)loadBalancer;

        ServerList<DiscoveryEnabledServer> serverListImpl = dynamicServerListLoadBalancer.getServerListImpl();

        DomainExtractingServerList domainExtractingServerList1 = (DomainExtractingServerList) serverListImpl;

        try {
            Field field = domainExtractingServerList1.getClass().getDeclaredField("list");
            field.setAccessible(true);
            ServerList<DiscoveryEnabledServer> list = (ServerList<DiscoveryEnabledServer>)field.get(domainExtractingServerList1);

            for(DiscoveryEnabledServer server:list.getUpdatedListOfServers()){
                //此处获取的是所有的实例
                System.out.println("%%%:"+server.getHostPort());
            }

            for(DiscoveryEnabledServer server:list.getInitialListOfServers()){
                System.out.println("+++:"+server.getHostPort());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
