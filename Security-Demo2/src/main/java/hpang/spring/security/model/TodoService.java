package hpang.spring.security.model;

import java.util.List;

public interface TodoService {

    public List<Todo> listTodos();
	public void save(Todo todo);
	public Todo complete(long id);
	public void remove(long id);
	public Todo findById(long id);
}
