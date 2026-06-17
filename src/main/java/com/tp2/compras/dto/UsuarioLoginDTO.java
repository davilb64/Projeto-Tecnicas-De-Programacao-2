package com.tp2.compras.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/* 
Data Transfer Object (DTO) 
Objeto simples que contém apenas o que o usuário vai enviar no formulário: email e senha. 
*/
public record UsuarioLoginDTO(

    //Assertiva: Email deve ser válido, não vazio e conter no máximo 150 caracteres.
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email,

    //Assertiva: Senha não pode ser vazia e deve ter um tamanho mínimo de 8 caracteres
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres")
    String senha

) {}