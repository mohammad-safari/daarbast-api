package ce.web.daarbast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@SpringBootApplication
public class DaarbastApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaarbastApplication.class, args);
	}

	@Bean(name = "taskExecutor")
	public Executor taskExecutor() {
		return Executors.newVirtualThreadPerTaskExecutor();
	}

	@Bean(name = "concurrent")
	public <K, V> Map<K, V> concurrentHashMap() {
		return new ConcurrentHashMap<>();
	}

}
