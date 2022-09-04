package pl.gdansk.eventslinker.rest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfiguration {

	@Bean(name = "asyncExecutor")
	public Executor asyncExecutor() {
		return Executors.newCachedThreadPool(r -> {
			final Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		});
	}

}
