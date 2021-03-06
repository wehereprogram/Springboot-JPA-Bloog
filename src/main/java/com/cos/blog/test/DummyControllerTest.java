package com.cos.blog.test;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.*;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

//html 파일이 아니라 data를 리턴해주는 controller = RestController
@RestController
public class DummyControllerTest {

	//실행 시 UserRepository 타입으로 메모리에 띄워줌 
	@Autowired //의존성 주입(DI)
	private UserRepository userRepository;
	
	@GetMapping("/dummy/users")
	public List<User> list(){
		return userRepository.findAll();
	}
	
	// 한 페이당 2건에 데이터를 리턴받아 볼 예정
	@GetMapping("/dummy/user")
	public List<User> pageList(@PageableDefault(size=2, sort="id", direction=Sort.Direction.DESC) Pageable pageable){
		Page<User> pageUser = userRepository.findAll(pageable); 
		
		if(pageUser.isFirst()) {
			
		}
		
		List<User> users = pageUser.getContent(); //getContent()는 List 값으로 반환한다.
		return users;
	}
	
	
	//{id}주소로 파마레터를 전달 받을 수 있음
	//http://localhost:8000/blog/dummy/user/3
	@GetMapping("/dummy/user/{id}")
	public User detail(@PathVariable int id) {
		//user/4을 찾으면 내가 DB에서 못 찾으면 user = null이 됨
		//return할 때 null이 되면 문제가 발생함
		//optional로 너의 User 객체를 가져올테니 null인지 아닌지 판단해서 return해
		//userRepository.findById(id).get() 는 절대 null이 없다는 가정 하에 사용
		//userRepository.findById(id).orElseGet(new Supplier<User>()) 없으면 객체 하나 만들어서 넣어줘
//		User user = userRepository.findById(id).orElseGet(new Supplier<User>() {
//			//Supplier 인터페이스를 이용해 익명의 객체 생성. 인터페이스는 new를 할 수 없기 때문에 익명 Class로 new 해야 함
//			@Override
//			public User get() {
//				// TODO Auto-generated method stub
//				return new User();
//			}
//		});
		
		// 권장되는 방법. 찾는게 없을 경우 예외처리
		User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {
		
			@Override
			public IllegalArgumentException get() {
				// TODO Auto-generated method stub
				return new IllegalArgumentException("해당 유저는 없습니다. id : " + id);
			}
		});
		
		//요청 : 웹 브라우저
		//user 객체 = 자바 오브젝트
		//변환(웹브라우저가 이해할 수 있는 데이터) -> json 
		//스프링부트 = MessageConveter라는 애가 응답시에 자동 작동
		//만약에 자바 오브젝트를 리턴하게 되면 MessageConverter가 Jackson 라이브러리를 호출해서
		//user 오브젝트를 json으로 변환해서 브라우저에게 던져줌
		return user;
	}
	
	//http://localhost:8080/blog/dummy/join(요청)
	//http의 body에 username,password,email 데이터를 가지고 요청
	@PostMapping("/dummy/join")
//	public String join(String username, String password, String email) { // key=value 타입으로 데이터를 전달받음.(약속된 규칙)
	public String join(User user) {	//오브젝트로 데이터를 받을 수 있음
		System.out.println("username : " + user.getUsername());
		System.out.println("password : " + user.getPassword());
		System.out.println("email : " + user.getEmail());
		
		//default 값을 회원가입때 넣을 수 있도록 함
		user.setRole(RoleType.USER);
		
		//DB에 저장
		userRepository.save(user);
		return "회원가입이 완료되었습니다.";
	}
}
