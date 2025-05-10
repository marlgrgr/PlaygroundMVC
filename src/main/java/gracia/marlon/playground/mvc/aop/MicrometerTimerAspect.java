package gracia.marlon.playground.mvc.aop;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class MicrometerTimerAspect {

	private final MeterRegistry meterRegistry;

	@Around("execution(* gracia.marlon.playground.mvc.controller..*(..))")
	public Object medirTiempoEjecucion(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getName();


		long startTime = System.nanoTime();

		try {
			return joinPoint.proceed();
		} finally {
			long timeElapsed = System.nanoTime() - startTime;

			Timer.builder("api.execution.time").tag("class", className).tag("method", methodName)
					.description("Time execution by method").publishPercentiles(0.25, 0.5, 0.75, 0.9)
					.register(meterRegistry).record(timeElapsed, TimeUnit.NANOSECONDS);

			meterRegistry.counter("api.methods.count", "class", className, "method", methodName).increment();
		}
	}
}
