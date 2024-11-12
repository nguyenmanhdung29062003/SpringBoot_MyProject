package com.example.demo.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//ConstraintValidator; nhận 2 param <annotation mà nó sẽ chịu trách nghiệm cho, kiểu dữ liệu của data mà ta sẽ validated>
//có 2 method mà ta cần @Override là isValid() và initialize() 
public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
	// nhận tham số min trong ANOTATION
	private int min;

	//tiến trình 2, xưa lý kiểm tra dữ liệu
	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		 // sau khi lấy được min = 18 tiến hành code validation
		 //value chính là ngày tháng năm sinh mà ta nhập vào trường
		long years = ChronoUnit.YEARS.between(value, LocalDate.now());
		
		
		return years>=min;
	}
	
	//tiến trình 1 , get thông số mối khi Annotation được khởi tạo, tức nhận bên min = .. mà người ta  nhập vô
	@Override
	public void initialize(DobConstraint constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
		
		min = constraintAnnotation.min();
	}

}
