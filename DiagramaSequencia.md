@startuml
actor Medico 
participant Sistema
participant Bancodedados

Medico -> Sistema : login(usuário, senha)
Sistema --> Medico : confirmação de login

Medico -> Sistema : solicitarProntuario
Sistema -> Bancodedados: buscarProntuario(nome)
Bancodedados --> Sistema : listadeProntuarios
Sistema --> Medico : exibirProntuarios

Medico -> Sistema : selecionarProntuario
Sistema -> Bancodedados : buscarProntuario(nome)
Bancodedados --> Sistema : prontuarioDesignado
Sistema --> Medico : exibirProntuario

Medico -> Sistema : editarProntuario(nome)
Sistema -> Bancodedados : alteraçãodeprontuario
Bancodedados --> Sistema : prontuarioEditado
Sistema --> Medico : confirmaçãodeEdição 

@enduml
