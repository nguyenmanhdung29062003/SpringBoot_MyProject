package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userrepository;
	@Autowired
	private UserMapper usermapper;

	public UserDTO save(UserEntity usera) {
		if (userrepository.existsById(usera.getId())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
//		Mã hóa mât khẩu trước khi thêm vào
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		usera.setPassword(passwordEncoder.encode(usera.getPassword()));
//		Luư xuống DataBase
		userrepository.save(usera);
//		chuyển sang DTO
		return usermapper.toDTO(usera);
	}
	
//	lay tat ca user update 24/9/2024
	public List<UserDTO> getallusers(){
		
		List<UserDTO> listalldto = new ArrayList();
		List<UserEntity> listentity = userrepository.findAll();
		
		for (UserEntity userEntity : listentity) {
			
			listalldto.add(usermapper.toDTO(userEntity));
		}
		
		
		
		
		return listalldto;
		
	}

	public UserDTO find(String id) {
		if (userrepository.existsById(id) == false) {
//			ném ra một ngoại lệ + message tương ứng
			throw new AppException(ErrorCode.USER_NOT_EXIST);
		}
		UserEntity a = userrepository.findOneById(id);
		return usermapper.toDTO(a);
	}

	public UserDTO update(String id, UserDTO dto) {
		if (userrepository.existsById(id) == false) {
//			ném ra một ngoại lệ + message tương ứng
			throw new AppException(ErrorCode.USER_NOT_EXIST);
		}
		dto.setId(id);
		UserEntity oldentity = userrepository.findOneById(id);
//		tiến hành cập nhật data

		usermapper.updateUser(oldentity, dto);
//			Mã hóa mât khẩu trước khi thêm vào
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		oldentity.setPassword(passwordEncoder.encode(oldentity.getPassword()));
//			Luư xuống DataBase
		userrepository.save(oldentity);
//		lấy dữ liệu ra để kiểm tra
		UserEntity newentity = userrepository.findOneById(id);
		return usermapper.toDTO(newentity);

	}
}
