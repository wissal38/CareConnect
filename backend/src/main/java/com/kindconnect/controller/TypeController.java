package com.kindconnect.controller;

import com.kindconnect.model.Type;
import com.kindconnect.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types")
public class TypeController {

    private final TypeService typeService;

    @Autowired
    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping
    public ResponseEntity<List<Type>> getAllTypes() {
        return ResponseEntity.ok(typeService.getAllTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Type> getTypeById(@PathVariable Long id) {
        return typeService.getTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Type> getTypeByCode(@PathVariable String code) {
        return typeService.getTypeByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Type> createType(@RequestBody Type type) {
        return ResponseEntity.ok(typeService.createType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Type> updateType(
            @PathVariable Long id,
            @RequestBody Type type) {
        if (!typeService.getTypeById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        type.setId(id);
        return ResponseEntity.ok(typeService.updateType(type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        if (!typeService.getTypeById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        typeService.deleteType(id);
        return ResponseEntity.noContent().build();
    }
}
