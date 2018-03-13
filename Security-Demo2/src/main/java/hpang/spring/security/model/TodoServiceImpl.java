package hpang.spring.security.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/*
 * http://www.baeldung.com/spring-security-method-security
 * http://www.baeldung.com/spring-security-async-principal-propagation
 * 
 * There are two points we’d like to remind regarding method security:
 * By default, Spring AOP proxying is used to apply method security – 
 * if a secured method A is called by another method within the same class, security in A is ignored altogether. 
 * This means method A will execute without any security checking. The same applies to private methods
 * 
 * Spring SecurityContext is thread-bound – by default, the security context isn’t propagated to child-threads. 
 * For more information, we can refer to Spring Security Context Propagation article
 */

@Service
@Transactional
class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    //@PreAuthorize("hasAnyRole('USER', 'ADMIN')") //It works with role
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')") //It work with Authority only
    //@PostFilter("hasAnyAuthority('ADMIN') or filterObject.owner == authentication.name")//It works
    public List<Todo> listTodos() {
        return todoRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    //@PreAuthorize("hasAnyRole('USER')")
    public void save(Todo todo) {
        todoRepository.save(todo);
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    //@PreAuthorize("hasAnyRole('USER')")
    @PostAuthorize("returnObject.getOwner() == authentication.name")//it works for test purpose. But not meaning
    public Todo complete(long id) {
        Todo todo = findById(id);
        todo.setCompleted(true);
        todoRepository.save(todo);
        return todo;
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public void remove(long id) {
        todoRepository.remove(id);
    }

    @Override
    //@PreAuthorize("hasAuthority('USER')") //It is called by complete so the security will be ignored
    public Todo findById(long id) {
    	Todo todo = todoRepository.findOne(id);
    	System.out.println("TODO Owner: " + todo.getOwner());
        return todo;
    }
}

