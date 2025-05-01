package com.example.demo.controller;


import java.util.HashSet;
import java.util.Set;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest //để cấu hình test tức là thay vì call API thì ta test chạy trên đây luôn.
@AutoConfigureMockMvc //để tạo request trong Unit Test

@TestPropertySource("/application-test.properties")
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean //để mock cái bean để không test sang bên Service
	private UserService userService;
	
	//đầu vào
	private UserDTO userDTOin;
	
	//đầu ra
	private UserDTO userDTOout;
	
	//các thành phần khác
	private Set<String> role;
	
	//hàm init các biến để khởi tạo giá trị trước khi test được chạy
	@BeforeEach //chạy trước
	void initData() {
		role = new HashSet<String>();
		role.add("USER");
		userDTOin = new UserDTO().builder().username("Dung ne").password("12345678").roles(role).build();
		

		//mong muốn của ta là đầu vào như thế nao thì đầu ra cũng như thế
		// ta thiết lập đầu ra khởi tạo giống đầu vào nhưng khác mỗi có thêm ID
		
		userDTOout = new UserDTO().builder().id("user02").username("Dung ne").roles(role).build();
		
	}
	
	//bây giờ mới đến lúc test
	@Test //để viết một testcase.
	void createUser() throws Exception {
		//sau khi init dữ liệu xong rồi thì ta tiến hành test cho nó
		//1 Test bao gồm 3 phần : GIVEN, WHEN, THEN
		
		//GIVEN là những dữ liệu đầu vào mà ta đã biết trước, ta dự đoán được là nó sẽ xảy ra.
		//đầu  vào đầu ra là các GIVEN
		initData();
		
		//chuyển đối tượng sang String để truyền, ,như kiểu JSON ấy
		ObjectMapper objectMapper = new ObjectMapper();
		String content = objectMapper.writeValueAsString(userDTOin);
		
		//Mock service laị tức là không để nó gọi sẻvice mà ta sẽ return cụ thể đây luôn
		Mockito.when(userService.save(ArgumentMatchers.any())).thenReturn(userDTOout);
		
		
		//WHEN là khi nào mà chúng ta request
		//để tạo request sd mockMvc
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/user")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(content)) //tức là body khi tạo request
				.andExpect(MockMvcResultMatchers.status().isOk())  //đầu ra ma bạn mong muốn
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(202))
				.andExpect(MockMvcResultMatchers.jsonPath("result.id").value("user02"));
		
			
	}
	
	//test th lỗi vd mật khẩu < 8 ký tự
	@Test
	void  createUser_passwordInvalid_fail() throws Exception{
		//GIVEN
		role = new HashSet<String>();
		role.add("USER");
		userDTOin = new UserDTO().builder().username("Dung ne").password("123456").roles(role).build();
		
		//WHEN
		ObjectMapper objectMapper = new ObjectMapper();
		String content = objectMapper.writeValueAsString(userDTOin);
		//Mock Service lại
		Mockito.when(userService.save(ArgumentMatchers.any())).thenReturn(userDTOout);
		
		//Dùng mockMvc để thực hiện call API
		mockMvc.perform(MockMvcRequestBuilders
				.post("/user")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(content))
		
				//THEN
				.andExpect(MockMvcResultMatchers.jsonPath("message").value("Mật khẩu phải tối thiểu 8 ký tự"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
				
		
		
	}
}
