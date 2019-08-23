package demo;

import demo.service.HelloServiceInterface;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * @description: 我的feign客户端
 * @author: guochaoyong
 * @date: 2019-08-21 20:20
 **/
@Component
@Configuration
@Import(FeignClientsConfiguration.class)
public class MyFeignClient {

    private Feign.Builder builder;

    public MyFeignClient() {
    }

    @Autowired
    public MyFeignClient(Decoder decoder,Encoder encoder, Client client, Contract contract) {
        this.builder = Feign.builder()
                .retryer(new Retryer.Default(100,1000,5))
                .options(new Request.Options())
                .client(client)
                .encoder(encoder)
                .decoder(decoder).contract(contract);
    }

    public String helloFeign(){
        HelloServiceInterface helloService = this.builder.target(HelloServiceInterface.class, "http://HelloServer");
        final String s = helloService.helloFeign();
        return s;
    }
}
