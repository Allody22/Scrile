package ru.scrile.org.model.exception;

import java.util.List;


public record ValidationErrorResponse(List<Violation> violations) {

}
