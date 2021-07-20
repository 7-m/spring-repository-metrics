package dev.mufaddal.simplerest;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PersonApi {

  PersonRepository repository;
  private final MeterRegistry meterRegistry;

  public PersonApi(PersonRepository repository, MeterRegistry meterRegistry) {
    this.repository = repository;
    this.meterRegistry = meterRegistry;
  }

  @PostMapping("/person")
  Mono<Person> createPerson(@RequestBody Person p) {
    return repository.save(p);
  }

  @GetMapping("/person/{id}")
  Mono<Person> getPerson(@PathVariable("id") String id) {
    return repository.findById(id);
  }

  @GetMapping("/person-name/{name}")
  Mono<Person> getPersonByName(@PathVariable("name") String name) {
    return repository.findByName(name);
  }


  private static final String DB_METRICS = "db.latency";

  @GetMapping("/repo-metrics")
  Mono<String> dbMeausre() {
    return Mono.just(
        meterRegistry.get(DB_METRICS).timers().stream()
            .map(timer ->
                String.join(",", timer.getId().getTags().stream().map(Tag::getValue).collect(Collectors.joining(",")),
                    String.valueOf(timer.count()),
                    String.format("%.2f", timer.mean(TimeUnit.MILLISECONDS))))
            .collect(Collectors.joining("\n")));
  }

}
