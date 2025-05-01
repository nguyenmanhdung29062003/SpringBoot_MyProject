package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RolerRepository;
import com.example.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class UserService {
	@Autowired
	private UserRepository userrepository;
	@Autowired
	private UserMapper usermapper;
	
	@Autowired
	private RolerRepository roleRepository;

	public UserDTO save(UserDTO usera) {
		
		log.info("Service: create User");
		if (userrepository.existsById(usera.getId())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
//		Mã hóa mât khẩu trước khi thêm vào
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		usera.setPassword(passwordEncoder.encode(usera.getPassword()));

//update 26/9 set Role
		//HashSet<String> roles = new HashSet<>();
		//roles.add(Role.USER.name());
		//usera.setRoles(roles);

//		Luư xuống DataBase
		userrepository.save(usermapper.toUser(usera, roleRepository));
//		chuyển sang DTO
		return usermapper.toDTO(usermapper.toUser(usera, roleRepository),roleRepository);
	}

//	lay tat ca user update 24/9/2024
	// kiểm tra người dùng có Role trong TOKEN SCOPE là Admin mới gọi đc hàm này
	//@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserDTO> getallusers() {

		List<UserDTO> listalldto = new ArrayList();
		List<UserEntity> listentity = userrepository.findAll();

		for (UserEntity userEntity : listentity) {

			listalldto.add(usermapper.toDTO(userEntity,roleRepository));
		}

		return listalldto;
	}

	// lay myinfo
	public UserDTO getMyInfo() {
		var authenticated = SecurityContextHolder.getContext().getAuthentication();
		Jwt jwt = (Jwt) authenticated.getPrincipal();
		String id = jwt.getClaimAsString("userId");
		
		UserEntity user = userrepository.findOneById(id);

		return usermapper.toDTO(user,roleRepository);
	}
	
	public UserDTO getMyInfo2() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        UserEntity user = (UserEntity) userrepository.findByUsername(name);
        if(user == null) {
        	throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        return usermapper.toDTO(user, roleRepository);
    }

	public UserDTO find(String id) {
		if (userrepository.existsById(id) == false) {
//			ném ra một ngoại lệ + message tương ứng
			throw new AppException(ErrorCode.USER_NOT_EXIST);
		}
		UserEntity a = userrepository.findOneById(id);
		return usermapper.toDTO(a,roleRepository);
	}
//@PostAuthorize("returnObject.username == authentication.getName()")
	//sau khi update thực thi xong nó sẽ lấy name của DTO để kiểm tra với name trong TOKEN, néue đúng thì đc thưc thi
	public UserDTO update(String id, UserDTO dto) {
		if (userrepository.existsById(id) == false) {
//			ném ra một ngoại lệ + message tương ứng
			throw new AppException(ErrorCode.USER_NOT_EXIST);
		}
		dto.setId(id);
		UserEntity oldentity = userrepository.findOneById(id);
//		tiến hành cập nhật data

		usermapper.updateUser(oldentity, dto,roleRepository);
//			Mã hóa mât khẩu trước khi thêm vào
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		oldentity.setPassword(passwordEncoder.encode(oldentity.getPassword()));
//			Luư xuống DataBase
		userrepository.save(oldentity);
//		lấy dữ liệu ra để kiểm tra
		UserEntity newentity = userrepository.findOneById(id);
		return usermapper.toDTO(newentity,roleRepository);

	}
}
