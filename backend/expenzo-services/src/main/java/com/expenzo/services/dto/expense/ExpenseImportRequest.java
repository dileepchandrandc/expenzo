package com.expenzo.services.dto.expense;

import org.springframework.web.multipart.MultipartFile;

import com.expenzo.services.enums.DuplicateMatchingStrategy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseImportRequest {

    @Schema(type = "string", format = "binary")
    private MultipartFile file;

    private boolean removeDuplicates;
    private DuplicateMatchingStrategy duplicateMatchingStrategy;
}
