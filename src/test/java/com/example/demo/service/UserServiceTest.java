package com.example.demo.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.PermissionEntity;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.PermisionRepository;
import com.example.demo.repository.RolerRepository;
import com.example.demo.repository.UserRepository;
import com.jayway.jsonpath.Option;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private RolerRepository roleRepository;

	@MockBean
	private PermisionRepository permissionRepository;

	private UserDTO userDTOin;

	private UserEntity userEntity;

	// các thành phần khác
	private Set<String> role;
	private RoleEntity roleEntity;
	private PermissionEntity permissionEntity;
	private Set<PermissionEntity> listPermission;

	@BeforeEach
	void initData() {

		role = new HashSet<String>();
		role.add("ADMIN");
		permissionEntity = new PermissionEntity().builder().name("CAN READ").description("READ").build();
		listPermission = new HashSet<>();
		listPermission.add(permissionEntity);

		roleEntity = new RoleEntity().builder().name("ADMIN").description("ADMIN").permissions(listPermission).build();

		userDTOin = new UserDTO().builder().username("Dung ne").password("12345678").roles(role).build();

		userEntity = new UserEntity().builder().id("user01").username("Dung ne").build();

	}

	@Test
	void createUser_validRequest_success() {
		// GIVEN
		initData();

		// Mock repository lại
		Mockito.when(userRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false); // false để nó thực thi
																									// đoạn dưới, do ta
																									// đâng kiểm tra Th
																									// thành công
		// Mock RoleRepository để trả về roleEntity khi tìm theo tên
		Mockito.when(roleRepository.findByName("ADMIN")).thenReturn(roleEntity);

		// Nếu UserMapper tiếp tục truy vấn PermissionRepository, bạn cũng cần mock nó
		Mockito.when(permissionRepository.findByName("CAN READ")).thenReturn(permissionEntity);

		Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(userEntity); // mock lại trả ra userEntity
																							// như ta kỳ vọng

		// WHEN
		var response = userService.save(userDTOin);

		// THEN sd Aseertions.assertThat() để kiểm tra kết quả
		Assertions.assertTrue(response.getUsername().equals("Dung ne"));

	}

	// TEST TH FAIL, USER ĐÃ TỒN TẠI XEM LỖI BẮN RA NTN
	@Test
	void userExist_fail() {
		// GIVEN
		initData();

		// Mocking
		Mockito.when(userRepository.existsById(ArgumentMatchers.any())).thenReturn(true);

		// WHEN
		// do TH này ta bắt lỗi để xem lỗi nó bắn ra đúng như mong đợi không
		// nên sd Assertions.assertThrows() nó nhận 2 parameter
		// 1 là loại Exception Class mà ta expect sẽ nhận được
		// 2 là hàm thực thi gây ra exception đó, thường sd lamda gọi hàm đó cho tiển
		var exception = Assertions.assertThrows(AppException.class, () -> userService.save(userDTOin));

		System.out.println("Actual error code: " + exception.getErrorcode());
		// giờ ta kiểm tra exception
		Assertions.assertTrue(exception.getErrorcode().equals(ErrorCode.USER_EXISTED));

	}

	// Test GetMy Infor không tồn tại
	@Test
	@WithMockUser(authorities="user01", username="Dung ne") //giả lập người đùng
	// đã đăng nhập trong quá trình test các chức năng yêu câu đăng nhập
	void getMyinfor_fail() {
		// GIVEN: giả lập JWT

		// Mock repository lại
		Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(null);

		// WHEN
		var exception = Assertions.assertThrows(AppException.class, () -> {
			userService.getMyInfo2();
		});

		// THEN
		Assertions.assertTrue(exception.getErrorcode().equals(ErrorCode.USER_NOT_EXIST));
	}
}
