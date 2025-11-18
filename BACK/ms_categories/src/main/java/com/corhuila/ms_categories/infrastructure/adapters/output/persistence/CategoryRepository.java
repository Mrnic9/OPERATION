package com.corhuila.ms_categories.infrastructure.adapters.output.persistence;

import com.corhuila.ms_categories.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("SELECT c FROM Category c WHERE c.type = :type AND c.active = true")
    List<Category> findByTypeAndActiveTrue(@Param("type") Category.CategoryType type);

    @Query("SELECT c FROM Category c WHERE c.active = true")
    List<Category> findAllActive();
}
