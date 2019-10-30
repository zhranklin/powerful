package zhranklin.powerful.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name="framew.type", havingValue="grpc")
@ComponentScan(basePackages = {"zhranklin.powerful.grpc"})
public class GrpcConfiguration {

}
