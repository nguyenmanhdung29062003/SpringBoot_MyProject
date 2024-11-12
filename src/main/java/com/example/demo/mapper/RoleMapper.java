package com.example.demo.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.demo.dto.RoleDTO;
import com.example.demo.entity.PermissionEntity;
import com.example.demo.entity.RoleEntity;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.PermisionRepository;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", source = "permissions")
    RoleEntity toEntity(RoleDTO dto, @Context PermisionRepository permissionRepository);

    @Mapping(target = "permissions", source = "permissions")
    RoleDTO toDTO(RoleEntity entity, @Context PermisionRepository permissionRepository);

    @Mapping(target = "permissions", source = "permissions")
    void updateUser(@MappingTarget RoleEntity userEntity, RoleDTO userDTO, @Context PermisionRepository permissionRepository);

    default Set<PermissionEntity> mapPermissions(Set<String> permissionNames, @Context PermisionRepository permissionRepository) {
        Set<PermissionEntity> permissionEntities = new HashSet<>();
        for (String name : permissionNames) {
            PermissionEntity permission = permissionRepository.findByName(name);
            if (permission == null) {
                throw new AppException(ErrorCode.PERMISSION_ERROR);
            }
            permissionEntities.add(permission);
        }
        return permissionEntities;
    }

    default Set<String> mapPermissionsToNames(Set<PermissionEntity> permissionEntities) {
        Set<String> permissionNames = new HashSet<>();
        for (PermissionEntity permission : permissionEntities) {
            String name = permission.getName();
            if (name == null) {
                throw new AppException(ErrorCode.PERMISSION_ERROR);
            }
            permissionNames.add(name);
        }
        return permissionNames;
    }
}
