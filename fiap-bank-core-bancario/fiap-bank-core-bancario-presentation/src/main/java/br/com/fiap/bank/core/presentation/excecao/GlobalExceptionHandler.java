package br.com.fiap.bank.core.presentation.excecao;

import br.com.fiap.bank.core.application.excecao.ConflitoDeNegocioException;
import br.com.fiap.bank.core.application.excecao.DadosInvalidosException;
import br.com.fiap.bank.core.application.excecao.RecursoNaoEncontradoException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

// Transforma as excecoes em respostas ProblemDetail com o status HTTP de cada caso
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Campo invalido e a mensagem de validacao dele, usado na lista "erros"
    private record ErroCampo(String campo, String mensagem) {}

    // Cliente ou conta inexistente vira 404
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(RecursoNaoEncontradoException excecao) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, excecao.getMessage());
    }

    // Cadastro duplicado, saldo insuficiente e conta encerrada viram 409
    @ExceptionHandler(ConflitoDeNegocioException.class)
    public ProblemDetail tratarConflito(ConflitoDeNegocioException excecao) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, excecao.getMessage());
    }

    // CPF invalido, numero de conta invalido e regra de dominio quebrada viram 400
    @ExceptionHandler(DadosInvalidosException.class)
    public ProblemDetail tratarRequisicaoInvalida(DadosInvalidosException excecao) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, excecao.getMessage());
    }

    // Qualquer outro erro vai para o log e volta como 500
    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarErroInesperado(Exception excecao) {
        logger.error("Erro inesperado ao processar a requisicao", excecao);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
    }

    // Falha de Bean Validation vira 400 com cada campo invalido em "erros"
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException excecao, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ErroCampo> erros = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ErroCampo(erro.getField(), erro.getDefaultMessage()))
                .toList();
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dados da requisicao invalidos");
        problema.setProperty("erros", erros);
        return handleExceptionInternal(excecao, problema, headers, status, request);
    }

    // JSON mal formado ou com tipo errado vira 400
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException excecao, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Corpo da requisicao invalido ou mal formatado");
        return handleExceptionInternal(excecao, problema, headers, status, request);
    }
}
