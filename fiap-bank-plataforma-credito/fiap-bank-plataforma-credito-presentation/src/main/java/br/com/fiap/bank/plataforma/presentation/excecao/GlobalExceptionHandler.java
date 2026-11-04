package br.com.fiap.bank.plataforma.presentation.excecao;

import br.com.fiap.bank.plataforma.application.excecao.ConflitoDeNegocioException;
import br.com.fiap.bank.plataforma.application.excecao.CredenciaisInvalidasException;
import br.com.fiap.bank.plataforma.application.excecao.DadosInvalidosException;
import br.com.fiap.bank.plataforma.application.excecao.RecursoNaoEncontradoException;
import br.com.fiap.bank.plataforma.application.excecao.ServicoIndisponivelException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

// Converte as excecoes da API em ProblemDetail, mantendo os status padrao do Spring (405, 415)
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Bean Validation falhou: 400 com a mensagem de cada campo em campos
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException excecao,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> campos = excecao.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (primeira, segunda) -> primeira + "; " + segunda, TreeMap::new));

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Um ou mais campos estao invalidos");
        problema.setProperty("campos", campos);
        return handleExceptionInternal(excecao, problema, headers, status, request);
    }

    // JSON malformado ou corpo ausente: 400
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException excecao,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Corpo da requisicao invalido ou ausente");
        return handleExceptionInternal(excecao, problema, headers, status, request);
    }

    // Dado recusado pela regra de negocio: 400 com a mensagem da aplicacao
    @ExceptionHandler(DadosInvalidosException.class)
    public ProblemDetail tratarDadosInvalidos(DadosInvalidosException excecao) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, excecao.getMessage());
    }

    // Solicitacao ou conta inexistente: 404
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(RecursoNaoEncontradoException excecao) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, excecao.getMessage());
    }

    // Mudanca de status nao permitida: 409
    @ExceptionHandler(ConflitoDeNegocioException.class)
    public ProblemDetail tratarConflito(ConflitoDeNegocioException excecao) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, excecao.getMessage());
    }

    // Core Bancario fora do ar: 503
    @ExceptionHandler(ServicoIndisponivelException.class)
    public ProblemDetail tratarServicoIndisponivel(ServicoIndisponivelException excecao) {
        log.warn("Falha ao chamar o Core Bancario: {}", excecao.getCause().getClass().getSimpleName());
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, excecao.getMessage());
    }

    // Login ou senha errados: 401
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ProblemDetail tratarCredenciaisInvalidas(CredenciaisInvalidasException excecao) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, excecao.getMessage());
    }

    // Token ausente, invalido ou expirado, repassado pelo TratadorDeErrosDeSeguranca: 401
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail tratarNaoAutenticado(AuthenticationException excecao) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Token de acesso ausente, invalido ou expirado");
    }

    // Token valido sem permissao para o recurso: 403
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail tratarAcessoNegado(AccessDeniedException excecao) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Acesso negado: sem permissao para este recurso");
    }

    // Qualquer outro erro: 500 generico
    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarErroInesperado(Exception excecao) {
        log.error("Erro inesperado ao processar a requisicao", excecao);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar a requisicao");
    }
}
