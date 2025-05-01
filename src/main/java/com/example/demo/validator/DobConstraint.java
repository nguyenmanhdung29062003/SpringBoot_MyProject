package com.example.demo.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//target : annotation sẽ được apply ở đâu, vs trường là FIELD, phương thức là METHOD,...
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
//retention : annotation sẽ được xử lý lúc nào
@Retention(RetentionPolicy.RUNTIME)

//constraint : class chịu trách nhiệm validate cho annotation này, thực hiện chức năng
@Constraint(validatedBy = { DobValidator.class })
public @interface DobConstraint {
	String message() default "{Invalid date of birth}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	/*
	 trên là 3 property cơ bản
	 bây giờ tạo property thứ 3 để thực hiện việc customize
	 chỉ có chức năng khai báo, nhận data việc thực hiện validate cụ thể sẽ do
	 class trong @Constraint(validatedBy={})
	 
	 */
	int min();
	
	// tức nó nhận giá trị mà ta truyền vào ở cỗ vd $Size(min = 18, message = "k đủ
	// tuổi")

}
