Feature: Registro exitoso de nuevos usuarios en la plataforma Petstore


  Scenario Outline: Registro  de un usuario con datos validos y verificacion de respuesta exitosa
    Given que el servicio de usuarios de Petstore se encuentra disponible
    And se preparan los datos del nuevo usuario con "<username>", "<firstName>" y "<lastName>"
    When se envia la solicitud de creacion del usuario al sistema
    Then el sistema confirma que el usuario fue registrado exitosamente

    Examples:
      | username     | firstName | lastName  |
      | jperez01     | Juan      | Pérez     |