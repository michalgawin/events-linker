package pl.gdansk.eventslinker.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "eventlinker.security.google")
@Component
public class TokenProperties {

	private String token;

}
