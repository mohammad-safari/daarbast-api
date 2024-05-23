package ce.web.daarbast.security;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.path")
public record PathConfiguration(List<String> permitted) {

}
