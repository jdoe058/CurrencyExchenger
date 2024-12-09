package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ResponseDto;

public class ErrorService {
    private static final ErrorService INSTANCE = new ErrorService();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ErrorService() {
    }

    public ResponseDto get(int status, String message) throws JsonProcessingException {
        return new ResponseDto(status, objectMapper.writeValueAsString(new Message(message)));
    }

    public static ErrorService getInstance() {
        return INSTANCE;
    }

    record Message(String message) {
    }
}
