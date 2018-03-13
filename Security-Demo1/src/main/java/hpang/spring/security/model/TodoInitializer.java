package hpang.spring.security.model;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
class TodoInitializer {

    private final TodoService messageBoardService;

    TodoInitializer(TodoService messageBoardService) {
        this.messageBoardService = messageBoardService;
    }

    @PostConstruct
    public void setup() {

        Todo todo = new Todo();
        todo.setOwner("hpang@gmail.com");
        todo.setDescription("Finish Spring Recipes - Security Chapter");

        messageBoardService.save(todo);

        todo = new Todo();
        todo.setOwner("ray@gmail.com");
        todo.setDescription("Get Milk & Eggs");
        todo.setCompleted(true);
        messageBoardService.save(todo);

        todo = new Todo();
        todo.setOwner("david@gmail.com");
        todo.setDescription("Call parents.");

        messageBoardService.save(todo);

    }
}
