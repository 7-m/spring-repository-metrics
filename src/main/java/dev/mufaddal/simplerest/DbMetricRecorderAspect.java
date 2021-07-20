package dev.mufaddal.simplerest;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DbMetricRecorderAspect {

  final MeterRegistry registry;

  @Pointcut(value = "target(org.springframework.data.mongodb.repository.ReactiveMongoRepository)")
  public void anyMongoRepositoryImplementor() {
  }

  @Around(value = "anyMongoRepositoryImplementor()")
  public Object doDbProfiling(ProceedingJoinPoint pjp) throws Throwable {
    Object ret = pjp.proceed(pjp.getArgs());

    String controller = pjp.getTarget().getClass().getInterfaces()[0].getName() + "." + pjp.getSignature().getName();

    final Long startTime = System.nanoTime();
    Consumer<Signal<?>> signalConsumer = new Consumer<>() {
      boolean first = true;

      @Override
      public void accept(Signal<?> signal) {
        // record only on first emitted element
        if (!first) {
          return;
        }
        first = false;
        registry.timer("db.latency", "controller", controller, "completed_with_error",
            String.valueOf(signal.isOnError())).record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
      }

    };

    if (ret instanceof Mono) {
      Mono<?> mono = (Mono<?>) ret;
      return mono.doOnEach(signalConsumer);
    } else if (ret instanceof Flux) {
      Flux<?> flux = (Flux<?>) ret;
      return flux.doOnEach(signalConsumer);
    } else {
      throw new AssertionError("Unexpected return type");
    }

  }

}
