package ninja.harmless;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class ReativeMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReativeMongoApplication.class, args);
	}
}

@Configuration
class Config implements WebMvcConfigurer {
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(1000000000000L);
	}
}

@Document(collection = "student")
class Student {


	@Id
	private String id;
	private String name;
	private Integer age;

	public Student(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}

@Repository
interface ReactiveStudentRepository extends ReactiveMongoRepository<Student, UUID> {
	@Tailable
	Flux<Student> findBy();
}

@RestController
@CrossOrigin
class StudentController {
	@Autowired
	private ReactiveStudentRepository repository;


	@GetMapping("/")
	public Flux<ServerSentEvent<Student>> findAllStudents() {
		return repository.findBy().map(student -> ServerSentEvent.builder(student).id(student.getId())
                .data(student).build());
	}

	@GetMapping("/add")
	public void add() {
		repository.save(new Student("I AM NEW!", 19999999)).subscribe();
	}

	@PostConstruct
	private void populate() {
		List<Student> students = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			students.add(new Student("Benjamin", i));
		}
		repository.saveAll(students).subscribe(
				next -> {
					System.out.println("added: " + next.toString());
				},
				error -> {},
				() ->  {
					System.out.println("finished!");
				}
		);
	}
}