package com.uberclone.user_service.enums;

public enum DocumentType {
    CNH, // Carteira Nacional de Habilitação
    RG,  // Registro Geral
    CPF, // Cadastro de Pessoas Físicas
    CNPJ, // Cadastro Nacional de Pessoas Jurídicas (para empresas)
    VEHICLE_REGISTRATION, // Documento do veículo (CRLV)
    VEHICLE_PHOTO, // Foto do veículo (opcional, mas recomendado)
    PROOF_OF_ADDRESS, // Comprovante de residência (opcional, mas recomendado)
    CRIMINAL_RECORD // Certidão de antecedentes criminais (opcional, mas recomendado)
}
