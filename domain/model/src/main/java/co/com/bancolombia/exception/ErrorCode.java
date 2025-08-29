package co.com.bancolombia.exception;

public enum ErrorCode {

    // Autenticacion
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Credenciales invalidas"),
    INVALID_TOKEN("INVALID_TOKEN", "Token invalido"),

    // Validaciones
    INVALID_EMAIL("VALIDATION_INVALID_EMAIL", "Formato de correo electronico invalido"),
    REQUERID_EMAIL("VALIDATION_REQUERID_EMAIL", "Correo electronico es obligatorio"),
    EXISTS_EMAIL("VALIDATION_EXISTS_EMAIL", "Correo electronico ya esta registrado"),
    REQUERID_NAMES("VALIDATION_REQUERID_NAMES", "Nombres son obligatorios"),
    REQUERID_LASTNAMES("VALIDATION_REQUERID_LASTNAMES", "Apellidos son obligatorios"),
    REQUERID_DOCUMENTNUMBER("VALIDATION_REQUERID_DOCUMENTNUMBER", "Documento de identidad es obligatorio"),
    EXISTS_DOCUMENTNUMBER("VALIDATION_EXISTS_DOCUMENTNUMBER", "Documento de identidad ya esta registrado"),
    REQUERID_APPLICANTID("VALIDATION_REQUERID_APPLICANTID", "Identificador del solicitante es requerido"),
    INVALID_SALARY("VALIDATION_INVALID_SALARY", "Salario base fuera de rango"),
    APPLICANT_NOT_FOUND("APPLICANT_NOT_FOUND", "No se encontro un cliente con el numero de documento proporcionado"),

    // Genericos
    VALIDATION_ERROR("VALIDATION_ERROR", "Error de validacion"),
    NOT_FOUND("NOT_FOUND", "El recurso solicitado no existe"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Error interno inesperado");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}