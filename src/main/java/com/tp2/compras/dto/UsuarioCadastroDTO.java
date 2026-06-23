package com.tp2.compras.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/* 
Data Transfer Object (DTO) 
Objeto simples que contém apenas o que o usuário vai enviar no formulário: nome, email e senha. 
*/
public record UsuarioCadastroDTO(

    //Assertiva: Nome não pode ser vazio e dev ter no máximo 100 caracteres.
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    String nome,

    //Assertiva: Email deve ser válido, não vazio e conter no máximo 150 caracteres.
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    @Size(max = 150, message = "O email deve ter no máximo 150 caracteres")
    String email,

    //Assertiva: Senha não pode ser vazia e deve ter um tamanho mínimo de 8 caracteres
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres")
    String senha

){}