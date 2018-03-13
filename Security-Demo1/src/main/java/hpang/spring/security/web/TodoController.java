package hpang.spring.security.web;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import hpang.spring.security.model.Todo;
import hpang.spring.security.model.TodoService;

@Controller
@RequestMapping("/todos")
public class TodoController {

	@Autowired
	private CacheManager cacheManager;
	
	private static final Logger log = LoggerFactory.getLogger(TodoController.class);
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }
    
    @GetMapping
    public String list(Model model) {
        List<Todo> todos = todoService.listTodos();
        model.addAttribute("todos", todos);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        log.info("Principal:" +  currentPrincipalName);
        
        //UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //log.info("User has authorities: " + userDetails.getAuthorities());

        Cache cache = cacheManager.getCache("userCache");
        log.info("Cache: " + cache);
        model.addAttribute("principal", authentication);

        return "todos";
    }

    //Pricipal is optional param
    @GetMapping("/new")
    public String create(Model model, Principal principal) {
        model.addAttribute("todo", new Todo());
        log.info("Principal: " + principal.getName());
        return "todo-create";
    }

    @PostMapping
    public String newMessage(@ModelAttribute @Valid Todo todo, BindingResult errors) {

        if (errors.hasErrors()) {
            return "todo-create";
        }
        String owner = "hpang@gmail.com";
        todo.setOwner(owner);
        todoService.save(todo);
        return "redirect:/todos";
    }

    @PutMapping("/{todoId}/completed")
    public String complete(@PathVariable("todoId") long todoId) {
        log.info("TodoId: " +todoId);
    	this.todoService.complete(todoId);
        return "redirect:/todos";
    }


    @DeleteMapping("/{todoId}")
    public String delete(@PathVariable("todoId") long todoId) {
        this.todoService.remove(todoId);
        return "redirect:/todos";
    }

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Principal principal) {
    	log.info("Principal: " + principal.getName());
        return principal.getName();
    }

    @RequestMapping(value = "/encode/{password}", method = RequestMethod.GET)
    @ResponseBody
    public String encodePassword(@PathVariable("password") String password) {
    	String encodedPassword = passwordEncoder().encode(password);
    	log.info("Encode " + password + ": " + encodedPassword);
        return encodedPassword;
    }
    
    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("todo", new Todo());
        return "error";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // We don't want to bind the id and owner fields as we control them in this controller and service instead.
        binder.setDisallowedFields("id", "owner");
    }
    
    @Bean
	public BCryptPasswordEncoder passwordEncoder(){
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}

}
