package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class HelloServerApplication {
	@Autowired
	DiscoveryClient client;

	@RequestMapping("/")
	public String hello() {
        List<String> serviceIds = client.getServices();
		String serviceId = serviceIds.get(0);
		System.out.println(Arrays.toString(serviceIds.toArray()));
		ServiceInstance localInstance = client.getInstances(serviceId).get(0);
		return "Hello World: "+ localInstance.getServiceId()+":"+localInstance.getHost()+":"+localInstance.getPort();
	}

	@RequestMapping("/helloFeign")
	public String helloFeign(){
		return "helloFeign,my port is 7111";
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloServerApplication.class, args);
		}
		}
